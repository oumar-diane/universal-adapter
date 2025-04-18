/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.file;

import org.zenithblox.Exchange;
import org.zenithblox.Message;
import org.zenithblox.Processor;
import org.zenithblox.component.file.consumer.DirectoryEntriesResumeAdapter;
import org.zenithblox.component.file.consumer.FileOffsetResumeAdapter;
import org.zenithblox.resume.ResumeAdapter;
import org.zenithblox.resume.ResumeAware;
import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.support.resume.Resumables;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.function.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

/**
 * File consumer.
 */
public class FileConsumer extends GenericFileConsumer<File> implements ResumeAware<ResumeStrategy> {

    private static final Logger LOG = LoggerFactory.getLogger(FileConsumer.class);
    private ResumeStrategy resumeStrategy;
    private final String endpointPath;
    private Set<String> extendedAttributes;

    public FileConsumer(FileEndpoint endpoint, Processor processor, GenericFileOperations<File> operations,
                        GenericFileProcessStrategy<File> processStrategy) {
        super(endpoint, processor, operations, processStrategy);
        this.endpointPath = endpoint.getConfiguration().getDirectory();

        if (endpoint.getExtendedAttributes() != null) {
            List<String> attributes = Arrays.asList(endpoint.getExtendedAttributes().split(","));
            this.extendedAttributes = new HashSet<>(attributes);
        }
    }

    @Override
    protected Exchange createExchange(GenericFile<File> file) {
        Exchange exchange = createExchange(true);
        if (file != null) {
            file.bindToExchange(exchange, getEndpoint().isProbeContentType());
        }
        return exchange;
    }

    private boolean pollDirectory(Exchange dynamic, File directory, List<GenericFile<File>> fileList, int depth) {
        depth++;

        if (LOG.isTraceEnabled()) {
            LOG.trace("Polling directory: {}, absolute path: {}", directory.getPath(), directory.getAbsolutePath());
        }
        final File[] files = listFiles(directory);
        if (files == null || files.length == 0) {
            return true;
        }

        if (getEndpoint().isPreSort()) {
            Arrays.sort(files, Comparator.comparing(File::getAbsoluteFile));
        }

        if (processPolledFiles(dynamic, fileList, depth, files)) {
            return false;
        }

        return true;
    }

    private boolean processPolledFiles(Exchange dynamic, List<GenericFile<File>> fileList, int depth, File[] files) {
        for (File file : files) {
            // check if we can continue polling in files
            if (!canPollMoreFiles(fileList)) {
                return true;
            }

            // trace log as Windows/Unix can have different views what the file is
            if (LOG.isTraceEnabled()) {
                LOG.trace("Found file: {} [isAbsolute: {}, isDirectory: {}, isFile: {}, isHidden: {}]", file, file.isAbsolute(),
                        file.isDirectory(), file.isFile(),
                        file.isHidden());
            }

            // creates a generic file
            Supplier<GenericFile<File>> gf = Suppliers.memorize(
                    () -> asGenericFile(endpointPath, file, getEndpoint().getCharset(), getEndpoint().isProbeContentType()));

            if (resumeStrategy != null) {
                final ResumeAdapter adapter = setupResumeStrategy(gf.get());
                if (adapter instanceof DirectoryEntriesResumeAdapter directoryEntriesResumeAdapter) {
                    LOG.trace("Running the resume process for file {}", file);
                    if (directoryEntriesResumeAdapter.resume(file)) {
                        LOG.trace("Skipping file {} because it has been marked previously consumed", file);
                        continue;
                    }
                }
            }

            if (processEntry(dynamic, fileList, depth, file, gf, files)) {
                return true;
            }
        }
        return false;
    }

    private boolean processEntry(
            Exchange dynamic,
            List<GenericFile<File>> fileList, int depth, File file, Supplier<GenericFile<File>> gf, File[] files) {
        if (file.isDirectory()) {
            return processDirectoryEntry(dynamic, fileList, depth, file, gf, files);
        } else {
            processFileEntry(dynamic, fileList, depth, file, gf, files);

        }
        return false;
    }

    private void processFileEntry(
            Exchange dynamic,
            List<GenericFile<File>> fileList, int depth, File file, Supplier<GenericFile<File>> gf, File[] files) {
        // Windows can report false to a file on a share so regard it
        // always as a file (if it is not a directory)
        if (depth >= endpoint.minDepth) {
            boolean valid
                    = isValidFile(dynamic, gf, file.getName(), file.getAbsolutePath(),
                            getRelativeFilePath(endpointPath, null, null, file),
                            false, files);
            if (valid) {
                LOG.trace("Adding valid file: {}", file);
                if (extendedAttributes != null) {
                    Path path = file.toPath();
                    Map<String, Object> allAttributes = new HashMap<>();
                    for (String attribute : extendedAttributes) {
                        readAttributes(file, path, allAttributes, attribute);
                    }
                    gf.get().setExtendedAttributes(allAttributes);
                }
                fileList.add(gf.get());
            }
        }
    }

    private boolean processDirectoryEntry(
            Exchange dynamic,
            List<GenericFile<File>> fileList, int depth, File file, Supplier<GenericFile<File>> gf, File[] files) {
        if (endpoint.isRecursive() && depth < endpoint.getMaxDepth()) {
            boolean valid
                    = isValidFile(dynamic, gf, file.getName(), file.getAbsolutePath(),
                            getRelativeFilePath(endpointPath, null, null, file),
                            true, files);
            if (valid) {
                boolean canPollMore = pollDirectory(dynamic, file, fileList, depth);
                return !canPollMore;
            }
        }
        return false;
    }

    private ResumeAdapter setupResumeStrategy(GenericFile<File> gf) {
        ResumeAdapter adapter = resumeStrategy.getAdapter();
        LOG.trace("Checking the resume adapter: {}", adapter);
        if (adapter instanceof FileOffsetResumeAdapter fileOffsetResumeAdapter) {
            LOG.trace("The resume adapter is for offsets: {}", adapter);
            fileOffsetResumeAdapter.setResumePayload(gf);
            adapter.resume();
        }
        return adapter;
    }

    @Override
    protected boolean pollDirectory(Exchange dynamic, String fileName, List<GenericFile<File>> fileList, int depth) {
        LOG.trace("pollDirectory from fileName: {}", fileName);

        File directory = new File(fileName);
        if (!directory.exists() || !directory.isDirectory()) {
            LOG.debug("Cannot poll as directory does not exist or its not a directory: {}", directory);
            if (getEndpoint().isDirectoryMustExist()) {
                throw new GenericFileOperationFailedException("Directory does not exist: " + directory);
            }
            return true;
        }

        return pollDirectory(dynamic, directory, fileList, depth);
    }

    private File[] listFiles(File directory) {
        if (!getEndpoint().isIncludeHiddenDirs() && directory.isHidden()) {
            return null;
        }
        final File[] dirFiles = directory.listFiles();

        if (dirFiles == null || dirFiles.length == 0) {
            // no files in this directory to poll
            if (LOG.isTraceEnabled()) {
                LOG.trace("No files found in directory: {}", directory.getPath());
            }
            return null;
        } else {
            // we found some files
            if (LOG.isTraceEnabled()) {
                LOG.trace("Found {} in directory: {}", dirFiles.length, directory.getPath());
            }
        }

        return dirFiles;
    }

    private void readAttributes(File file, Path path, Map<String, Object> allAttributes, String attribute) {
        try {
            String prefix = null;
            if (attribute.endsWith(":*")) {
                prefix = attribute.substring(0, attribute.length() - 1);
            } else if (attribute.equals("*")) {
                prefix = "basic:";
            }

            if (ObjectHelper.isNotEmpty(prefix)) {
                Map<String, Object> attributes = Files.readAttributes(path, attribute);
                if (attributes != null) {
                    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
                        allAttributes.put(prefix + entry.getKey(), entry.getValue());
                    }
                }
            } else if (!attribute.contains(":")) {
                allAttributes.put("basic:" + attribute, Files.getAttribute(path, attribute));
            } else {
                allAttributes.put(attribute, Files.getAttribute(path, attribute));
            }
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Unable to read attribute {} on file {}", attribute, file, e);
            }
        }
    }

    @Override
    protected boolean isMatched(Supplier<GenericFile<File>> file, String doneFileName, File[] files) {
        String onlyName = FileUtil.stripPath(doneFileName);
        // the done file name must be among the files
        for (File f : files) {
            if (f.getName().equals(onlyName)) {
                return true;
            }
        }
        LOG.trace("Done file: {} does not exist", doneFileName);
        return false;
    }

    /**
     * Creates a new GenericFile<File> based on the given file.
     *
     * @param  endpointPath     the starting directory the endpoint was configured with
     * @param  file             the source file
     * @param  probeContentType whether to probe the content type of the file or not
     * @return                  wrapped as a GenericFile
     */
    public static GenericFile<File> asGenericFile(String endpointPath, File file, String charset, boolean probeContentType) {
        GenericFile<File> answer = new GenericFile<>(probeContentType);
        // use file specific binding
        answer.setBinding(new FileBinding());

        answer.setCharset(charset);
        answer.setEndpointPath(endpointPath);
        answer.setFile(file);
        answer.setFileNameOnly(file.getName());
        answer.setDirectory(file.isDirectory());
        // must use FileUtil.isAbsolute to have consistent check for whether the
        // file is
        // absolute or not. As windows do not consider \ paths as absolute where
        // as all
        // other OS platforms will consider \ as absolute. The logic in Zwangine
        // mandates
        // that we align this for all OS. That is why we must use
        // FileUtil.isAbsolute
        // to return a consistent answer for all OS platforms.
        answer.setAbsolute(FileUtil.isAbsolute(file));
        answer.setAbsoluteFilePath(file.getAbsolutePath());

        // file length and last modified are loaded lazily
        answer.setFileLengthSupplier(file::length);
        answer.setLastModifiedSupplier(file::lastModified);

        // compute the file path as relative to the starting directory
        File path;
        String endpointNormalizedSep = FileUtil.normalizePath(endpointPath) + File.separator;
        String p = file.getPath();
        if (p.startsWith(endpointNormalizedSep)) {
            p = p.substring(endpointNormalizedSep.length());
        }
        path = new File(p);

        if (path.getParent() != null) {
            answer.setRelativeFilePath(path.getParent() + File.separator + file.getName());
        } else {
            answer.setRelativeFilePath(path.getName());
        }

        // the file name should be the relative path
        answer.setFileName(answer.getRelativeFilePath());

        // use file as body as we have converters if needed as stream
        answer.setBody(file);
        return answer;
    }

    @Override
    protected Supplier<String> getRelativeFilePath(String endpointPath, String path, String absolutePath, File file) {
        return () -> {
            File f;
            String endpointNormalizedSep = FileUtil.normalizePath(endpointPath) + File.separator;
            String p = file.getPath();
            if (p.startsWith(endpointNormalizedSep)) {
                p = p.substring(endpointNormalizedSep.length());
            }
            f = new File(p);

            String answer;
            if (f.getParent() != null) {
                answer = f.getParent() + File.separator + file.getName();
            } else {
                answer = f.getName();
            }
            return answer;
        };
    }

    @Override
    protected void updateFileHeaders(GenericFile<File> file, Message message) {
        File upToDateFile = file.getFile();
        if (fileHasMoved(file)) {
            upToDateFile = new File(file.getAbsoluteFilePath());
        }
        long length = upToDateFile.length();
        long modified = upToDateFile.lastModified();
        file.setFileLength(length);
        file.setLastModified(modified);
        if (length >= 0) {
            message.setHeader(FileConstants.FILE_LENGTH, length);
        }
        if (modified >= 0) {
            message.setHeader(FileConstants.FILE_LAST_MODIFIED, modified);
        }

        message.setHeader(FileConstants.INITIAL_OFFSET, Resumables.of(upToDateFile, file.getLastOffsetValue()));
    }

    @Override
    public FileEndpoint getEndpoint() {
        return (FileEndpoint) super.getEndpoint();
    }

    @Override
    protected boolean isMatchedHiddenFile(Supplier<GenericFile<File>> file, String name, boolean isDirectory) {
        if (isDirectory) {
            if (!name.startsWith(".")) {
                return true;
            }
            return getEndpoint().isIncludeHiddenDirs() && !FileConstants.DEFAULT_SUB_FOLDER.equals(name);
        }

        if (getEndpoint().isIncludeHiddenFiles()) {
            return true;
        } else {
            return super.isMatchedHiddenFile(file, name, isDirectory);
        }
    }

    private boolean fileHasMoved(GenericFile<File> file) {
        // GenericFile's absolute path is always up to date whereas the
        // underlying file is not
        return !file.getFile().getAbsolutePath().equals(file.getAbsoluteFilePath());
    }

    @Override
    protected void doStart() throws Exception {
        if (resumeStrategy != null) {
            resumeStrategy.loadCache();
        }

        super.doStart();
    }

    @Override
    public ResumeStrategy getResumeStrategy() {
        return resumeStrategy;
    }

    @Override
    public void setResumeStrategy(ResumeStrategy resumeStrategy) {
        this.resumeStrategy = resumeStrategy;
    }

    @Override
    public String adapterFactoryService() {
        return "file-adapter-factory";
    }
}
