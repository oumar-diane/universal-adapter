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
import org.zenithblox.InvalidPayloadException;
import org.zenithblox.WrappedFile;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.zenithblox.component.file.GenericFileHelper.asExclusiveReadLockKey;

/**
 * File operations for {@link File}.
 */
public class FileOperations implements GenericFileOperations<File> {
    private static final Logger LOG = LoggerFactory.getLogger(FileOperations.class);
    private final Lock lock = new ReentrantLock();
    private FileEndpoint endpoint;

    public FileOperations() {
    }

    public FileOperations(FileEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public GenericFile<File> newGenericFile() {
        return new GenericFile<>();
    }

    @Override
    public void setEndpoint(GenericFileEndpoint<File> endpoint) {
        this.endpoint = (FileEndpoint) endpoint;
    }

    @Override
    public boolean deleteFile(String name) throws GenericFileOperationFailedException {
        File file = new File(name);
        return FileUtil.deleteFile(file);
    }

    @Override
    public boolean renameFile(String from, String to) throws GenericFileOperationFailedException {
        boolean renamed;
        File file = new File(from);
        File target = new File(to);
        try {
            if (endpoint.isRenameUsingCopy()) {
                renamed = FileUtil.renameFileUsingCopy(file, target);
            } else {
                renamed = FileUtil.renameFile(file, target, endpoint.isCopyAndDeleteOnRenameFail());
            }
        } catch (IOException e) {
            throw new GenericFileOperationFailedException("Error renaming file from " + from + " to " + to, e);
        }

        return renamed;
    }

    @Override
    public boolean existsFile(String name) throws GenericFileOperationFailedException {
        File file = new File(name);
        return file.exists();
    }

    protected boolean buildDirectory(File dir, Set<PosixFilePermission> permissions, boolean absolute, boolean stepwise) {
        if (dir.exists()) {
            return true;
        }

        boolean hasPermissions = permissions != null && !permissions.isEmpty();

        if (!stepwise && !hasPermissions) {
            return dir.mkdirs();
        }

        // create directory one part of a time and set permissions
        try {
            String[] parts = dir.getPath().split("\\" + File.separatorChar);

            File base;
            // reusing absolute flag to handle relative and absolute paths
            if (absolute) {
                base = new File("");
            } else {
                base = new File(".");
            }

            for (String part : parts) {
                File subDir = new File(base, part);
                if (!subDir.exists()) {
                    if (subDir.mkdir()) {
                        if (hasPermissions) {
                            if (LOG.isTraceEnabled()) {
                                LOG.trace("Setting chmod: {} on directory: {}", PosixFilePermissions.toString(permissions),
                                        subDir);
                            }
                            Files.setPosixFilePermissions(subDir.toPath(), permissions);
                        }
                    } else {
                        return false;
                    }
                }
                base = new File(base, subDir.getName());
            }
        } catch (IOException e) {
            throw new GenericFileOperationFailedException("Error setting chmod on directory: " + dir, e);
        }

        return true;
    }

    @Override
    public boolean buildDirectory(String directory, boolean absolute) throws GenericFileOperationFailedException {
        ObjectHelper.notNull(endpoint, "endpoint");

        // always create endpoint defined directory
        if (endpoint.isAutoCreate() && !endpoint.getFile().exists()) {
            LOG.trace("Building starting directory: {}", endpoint.getFile());
            buildDirectory(endpoint.getFile(), endpoint.getDirectoryPermissions(), absolute, endpoint.isAutoCreateStepwise());
        }

        if (ObjectHelper.isEmpty(directory)) {
            // no directory to build so return true to indicate ok
            return true;
        }

        File endpointPath = endpoint.getFile();
        File target = new File(directory);

        // check if directory is a path
        boolean isPath = directory.contains("/") || directory.contains("\\");

        File path;
        if (absolute) {
            // absolute path
            path = target;
        } else if (endpointPath.equals(target)) {
            // its just the root of the endpoint path
            path = endpointPath;
        } else if (isPath) {
            // relative after the endpoint path
            String afterRoot = StringHelper.after(directory, endpointPath.getPath() + File.separator);
            if (ObjectHelper.isNotEmpty(afterRoot)) {
                // dir is under the root path
                path = new File(endpoint.getFile(), afterRoot);
            } else {
                // dir path is relative to the root path
                path = new File(directory);
            }
        } else {
            // dir is a child of the root path
            path = new File(endpoint.getFile(), directory);
        }

        // We need to make sure that this is thread-safe and only one thread
        // tries to create the path directory at the same time.
        lock.lock();
        try {
            if (path.isDirectory() && path.exists()) {
                // the directory already exists
                return true;
            } else {
                LOG.trace("Building directory: {}", path);
                return buildDirectory(path, endpoint.getDirectoryPermissions(), absolute, endpoint.isAutoCreateStepwise());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public File[] listFiles() throws GenericFileOperationFailedException {
        // noop
        return null;
    }

    @Override
    public File[] listFiles(String path) throws GenericFileOperationFailedException {
        // noop
        return null;
    }

    @Override
    public void changeCurrentDirectory(String path) throws GenericFileOperationFailedException {
        // noop
    }

    @Override
    public void changeToParentDirectory() throws GenericFileOperationFailedException {
        // noop
    }

    @Override
    public String getCurrentDirectory() throws GenericFileOperationFailedException {
        // noop
        return null;
    }

    @Override
    public boolean retrieveFile(String name, Exchange exchange, long size) throws GenericFileOperationFailedException {
        // noop as we use type converters to read the body content for
        // java.io.File
        return true;
    }

    @Override
    public void releaseRetrievedFileResources(Exchange exchange) throws GenericFileOperationFailedException {
        // noop as we used type converters to read the body content for
        // java.io.File
    }

    @Override
    public boolean storeFile(String fileName, Exchange exchange, long size) throws GenericFileOperationFailedException {
        ObjectHelper.notNull(endpoint, "endpoint");

        File file = new File(fileName);

        // if an existing file already exists what should we do?
        if (file.exists()) {
            if (applyExistingFilePolicy(fileName, file)) {
                return true;
            }
        }

        // Do an explicit test for a null body and decide what to do
        if (exchange.getIn().getBody() == null) {
            return handleNullBody(file);
        }

        // we can write the file by 3 different techniques
        // 1. write file to file
        // 2. rename a file from a local work path
        // 3. write stream to file
        try {

            // is there an explicit charset configured we must write the file as
            String charset = endpoint.getCharset();

            // we can optimize and use file based if no charset must be used,
            // and the input body is a file
            // however optimization cannot be applied when content should be
            // appended to target file
            File source = null;
            boolean fileBased = false;
            if (charset == null && endpoint.getFileExist() != GenericFileExist.Append) {
                // if no charset and not in appending mode, then we can try
                // using file directly (optimized)
                final Object body = extractBodyFromExchange(exchange);
                if (body instanceof File fileBody) {
                    source = fileBody;
                    fileBased = true;
                }
            }

            if (fileBased) {
                // okay we know the body is a file based

                // so try to see if we can optimize by renaming the local work
                // path file instead of doing
                // a full file to file copy, as the local work copy is to be
                // deleted afterwards anyway
                // local work path
                if (handleFileAsFileSource(exchange, file, source)) {
                    return true;
                }
            }

            if (charset != null) {
                // charset configured so we must use a reader so we can write
                // with encoding
                handleReaderAsFileSource(exchange, file, charset);
            } else if (exchange.getIn().getBody() instanceof String) {
                // If the body is a string, write it directly
                handleStringAsFileSource(exchange, file);
            } else {
                // fallback and use stream based
                handleStreamAsFileSource(exchange, file);
            }

            // try to keep last modified timestamp if configured to do so
            keepLastModified(exchange, file);
            // set permissions if the chmod option was set
            setPermissions(file);

            return true;
        } catch (InvalidPayloadException | IOException e) {
            throw new GenericFileOperationFailedException("Cannot store file: " + file, e);
        }
    }

    private void handleStreamAsFileSource(Exchange exchange, File file) throws InvalidPayloadException, IOException {
        InputStream in = exchange.getIn().getMandatoryBody(InputStream.class);
        writeFileByStream(in, file);
    }

    private void handleStringAsFileSource(Exchange exchange, File file) throws IOException {
        String stringBody = (String) exchange.getIn().getBody();
        writeFileByString(stringBody, file);
    }

    private void handleReaderAsFileSource(Exchange exchange, File file, String charset)
            throws InvalidPayloadException, IOException {
        Reader in = exchange.getContext().getTypeConverter().tryConvertTo(Reader.class, exchange,
                exchange.getIn().getBody());
        if (in == null) {
            // okay no direct reader conversion, so use an input stream
            // (which a lot can be converted as)
            InputStream is = exchange.getIn().getMandatoryBody(InputStream.class);
            in = new InputStreamReader(is);
        }
        // buffer the reader
        in = IOHelper.buffered(in);
        writeFileByReaderWithCharset(in, file, charset);
    }

    private boolean handleFileAsFileSource(Exchange exchange, File file, File source) throws IOException {
        String local = exchange.getIn().getHeader(FileConstants.FILE_LOCAL_WORK_PATH, String.class);
        if (local != null) {
            File f = new File(local);
            if (f.exists()) {
                boolean renamed = writeFileByLocalWorkPath(f, file);
                if (renamed) {
                    // try to keep last modified timestamp if configured to
                    // do so
                    keepLastModified(exchange, file);
                    // set permissions if the chmod option was set
                    setPermissions(file);
                    // clear header as we have renamed the file
                    exchange.getIn().setHeader(FileConstants.FILE_LOCAL_WORK_PATH, null);
                    // return as the operation is complete, we just renamed
                    // the local work file
                    // to the target.
                    return true;
                }
            }
        } else if (source != null && source.exists()) {
            // no there is no local work file so use file to file copy
            // if the source exists
            writeFileByFile(source, file, exchange);
            // try to keep last modified timestamp if configured to do
            // so
            keepLastModified(exchange, file);
            // set permissions if the chmod option was set
            setPermissions(file);
            return true;
        }
        return false;
    }

    private static Object extractBodyFromExchange(Exchange exchange) {
        Object body = exchange.getIn().getBody();
        if (body instanceof WrappedFile<?> wrapped) {
            body = wrapped.getFile();
            if (!(body instanceof File)) {
                // the wrapped file may be from remote (FTP) which then can store
                // a local java.io.File handle if storing to local work-dir so check for that
                Object maybeFile = wrapped.getBody();
                if (maybeFile instanceof File) {
                    body = maybeFile;
                }
            }
        }
        return body;
    }

    private boolean handleNullBody(File file) {
        if (endpoint.isAllowNullBody()) {
            LOG.trace("Writing empty file.");
            try {
                writeFileEmptyBody(file);
                return true;
            } catch (IOException e) {
                throw new GenericFileOperationFailedException("Cannot store file: " + file, e);
            }
        } else {
            throw new GenericFileOperationFailedException("Cannot write null body to file: " + file);
        }
    }

    private boolean applyExistingFilePolicy(String fileName, File file) {
        if (endpoint.getFileExist() == GenericFileExist.Ignore) {
            // ignore but indicate that the file was written
            LOG.trace("An existing file already exists: {}. Ignore and do not override it.", file);
            return true;
        } else if (endpoint.getFileExist() == GenericFileExist.Fail) {
            throw new GenericFileOperationFailedException("File already exist: " + file + ". Cannot write new file.");
        } else if (endpoint.getFileExist() == GenericFileExist.Move) {
            // move any existing file first
            this.endpoint.getMoveExistingFileStrategy().moveExistingFile(endpoint, this, fileName);
        }
        return false;
    }

    private void setPermissions(File file) throws IOException {
        if (ObjectHelper.isNotEmpty(endpoint.getChmod())) {
            Set<PosixFilePermission> permissions = endpoint.getPermissions();
            if (!permissions.isEmpty()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Setting chmod: {} on file: {}", PosixFilePermissions.toString(permissions),
                            file);
                }
                Files.setPosixFilePermissions(file.toPath(), permissions);
            }
        }
    }

    private void keepLastModified(Exchange exchange, File file) {
        if (endpoint.isKeepLastModified()) {
            Long last;
            Date date = exchange.getIn().getHeader(FileConstants.FILE_LAST_MODIFIED, Date.class);
            if (date != null) {
                last = date.getTime();
            } else {
                // fallback and try a long
                last = exchange.getIn().getHeader(FileConstants.FILE_LAST_MODIFIED, Long.class);
            }
            if (last != null) {
                boolean result = file.setLastModified(last);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Keeping last modified timestamp: {} on file: {} with result: {}", last, file, result);
                }
            }
        }
    }

    private boolean writeFileByLocalWorkPath(File source, File file) throws IOException {
        LOG.trace("writeFileByFile using local work file being renamed from: {} to: {}", source, file);
        return FileUtil.renameFile(source, file, endpoint.isCopyAndDeleteOnRenameFail());
    }

    private void writeFileByFile(File source, File target, Exchange exchange) throws IOException {
        // in case we are using file locks as read-locks then we need to use
        // file channels for copying to support this
        String path = source.getAbsolutePath();
        FileChannel channel
                = exchange.getProperty(asExclusiveReadLockKey(path, Exchange.FILE_LOCK_CHANNEL_FILE), FileChannel.class);
        if (channel != null) {
            try (FileChannel out = new FileOutputStream(target).getChannel()) {
                LOG.trace("writeFileByFile using FileChannel: {} -> {}", source, target);
                channel.transferTo(0, channel.size(), out);
            }
        } else {
            // use regular file copy
            LOG.trace("writeFileByFile using Files.copy: {} -> {}", source, target);
            Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void writeFileByStream(InputStream in, File target) throws IOException {
        try (SeekableByteChannel out = prepareOutputFileChannel(target)) {
            LOG.debug("Using InputStream to write file: {}", target);
            int size = endpoint.getBufferSize();
            byte[] buffer = new byte[size];
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                if (bytesRead < size) {
                    ((Buffer) byteBuffer).limit(bytesRead);
                }
                out.write(byteBuffer);
                ((Buffer) byteBuffer).clear();
            }

            boolean append = endpoint.getFileExist() == GenericFileExist.Append;
            if (append && endpoint.getAppendChars() != null) {
                byteBuffer = ByteBuffer.wrap(endpoint.getAppendChars().getBytes());
                out.write(byteBuffer);
                Buffer buf = byteBuffer;
                buf.clear();
            }
        } finally {
            IOHelper.close(in, target.getName(), LOG);
        }
    }

    private void writeFileByReaderWithCharset(Reader in, File target, String charset) throws IOException {
        boolean append = endpoint.getFileExist() == GenericFileExist.Append;
        try (Writer out = Files.newBufferedWriter(target.toPath(), Charset.forName(charset), StandardOpenOption.WRITE,
                append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)) {
            LOG.debug("Using Reader to write file: {} with charset: {}", target, charset);
            int size = endpoint.getBufferSize();
            IOHelper.copy(in, out, size);

            if (append && endpoint.getAppendChars() != null) {
                out.write(endpoint.getAppendChars());
            }
        } finally {
            IOHelper.close(in, target.getName(), LOG);
        }
    }

    private void writeFileByString(String body, File target) throws IOException {
        boolean append = endpoint.getFileExist() == GenericFileExist.Append;
        Files.writeString(target.toPath(), body, StandardOpenOption.WRITE,
                append ? StandardOpenOption.APPEND : StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
        if (append && endpoint.getAppendChars() != null) {
            Files.writeString(target.toPath(), endpoint.getAppendChars(), StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND);
        }
    }

    /**
     * Creates a new file if the file doesn't exist. If the endpoint's existing file logic is set to 'Override' then the
     * target file will be truncated
     */
    private void writeFileEmptyBody(File target) throws IOException {
        if (!target.exists()) {
            LOG.debug("Creating new empty file: {}", target);
            FileUtil.createNewFile(target);
        } else if (endpoint.getFileExist() == GenericFileExist.Override) {
            LOG.debug("Truncating existing file: {}", target);
            Files.write(target.toPath(), new byte[0], StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        }
    }

    /**
     * Creates and prepares the output file channel. Will position itself in correct position if the file is writable
     * eg. it should append or override any existing content.
     */
    private SeekableByteChannel prepareOutputFileChannel(File target) throws IOException {
        if (endpoint.getFileExist() == GenericFileExist.Append) {
            SeekableByteChannel out
                    = Files.newByteChannel(target.toPath(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            return out.position(out.size());
        }
        return Files.newByteChannel(target.toPath(), StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
    }
}
