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
package org.zenithblox.component.file.strategy;

import org.zenithblox.Exchange;
import org.zenithblox.LoggingLevel;
import org.zenithblox.component.file.*;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.regex.Pattern;

/**
 * Acquires read lock to the given file using a marker file so other Zwangine consumers wont acquire the same file. This is
 * the default behavior in Zwangine 1.x.
 */
public class MarkerFileExclusiveReadLockStrategy implements GenericFileExclusiveReadLockStrategy<File> {
    private static final Logger LOG = LoggerFactory.getLogger(MarkerFileExclusiveReadLockStrategy.class);

    private boolean markerFile = true;
    private boolean deleteOrphanLockFiles = true;

    @Override
    public void prepareOnStartup(GenericFileOperations<File> operations, GenericFileEndpoint<File> endpoint) {
        if (deleteOrphanLockFiles) {

            String dir = endpoint.getConfiguration().getDirectory();
            File file = new File(dir);

            LOG.debug("Prepare on startup by deleting orphaned lock files from: {}", dir);

            Pattern excludePattern = endpoint.getExcludePattern();
            Pattern includePattern = endpoint.getIncludePattern();
            String endpointPath = endpoint.getConfiguration().getDirectory();

            StopWatch watch = new StopWatch();
            deleteLockFiles(file, endpoint.isRecursive(), endpoint.getMinDepth(), endpoint.getMaxDepth(), 1,
                    endpoint.isHiddenFilesEnabled(), endpointPath, endpoint.getFilter(),
                    endpoint.getAntFilter(),
                    excludePattern, includePattern);

            // log anything that takes more than a second
            if (watch.taken() > 1000) {
                LOG.info("Prepared on startup by deleting orphaned lock files from: {} took {} millis to complete.", dir,
                        watch.taken());
            }
        }
    }

    @Override
    public boolean acquireExclusiveReadLock(GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange)
            throws Exception {

        if (!markerFile) {
            // if not using marker file then we assume acquired
            return true;
        }

        String lockFileName = getLockFileName(file);
        LOG.trace("Locking the file: {} using the lock file name: {}", file, lockFileName);

        // create a plain file as marker filer for locking (do not use FileLock)
        boolean acquired = FileUtil.createNewFile(new File(lockFileName));

        // store read-lock state
        exchange.setProperty(asReadLockKey(file, Exchange.FILE_LOCK_FILE_ACQUIRED), acquired);
        exchange.setProperty(asReadLockKey(file, Exchange.FILE_LOCK_FILE_NAME), lockFileName);

        return acquired;
    }

    @Override
    public void releaseExclusiveReadLockOnAbort(
            GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange)
            throws Exception {
        doReleaseExclusiveReadLock(file, exchange);
    }

    @Override
    public void releaseExclusiveReadLockOnRollback(
            GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange)
            throws Exception {
        doReleaseExclusiveReadLock(file, exchange);
    }

    @Override
    public void releaseExclusiveReadLockOnCommit(
            GenericFileOperations<File> operations, GenericFile<File> file, Exchange exchange)
            throws Exception {
        doReleaseExclusiveReadLock(file, exchange);
    }

    /**
     * Perform the releases of the exclusive read lock granted by the <tt>acquireExclusiveReadLock</tt> method due a
     * commit operation.
     *
     * @param  file      the file
     * @param  exchange  the exchange
     * @throws Exception can be thrown in case of errors
     * @see              #releaseExclusiveReadLockOnCommit(GenericFileOperations, GenericFile, Exchange)
     * @see              #releaseExclusiveReadLockOnRollback(GenericFileOperations, GenericFile, Exchange)
     * @see              #releaseExclusiveReadLockOnAbort(GenericFileOperations, GenericFile, Exchange)
     */
    protected void doReleaseExclusiveReadLock(GenericFile<File> file, Exchange exchange) throws Exception {
        if (!markerFile) {
            // if not using marker file then nothing to release
            return;
        }

        boolean acquired = exchange.getProperty(asReadLockKey(file, Exchange.FILE_LOCK_FILE_ACQUIRED), false, Boolean.class);

        // only release the file if zwangine get the lock before
        if (acquired) {
            String lockFileName = exchange.getProperty(asReadLockKey(file, Exchange.FILE_LOCK_FILE_NAME), String.class);
            File lock = new File(lockFileName);

            if (lock.exists()) {
                LOG.trace("Unlocking file: {}", lockFileName);
                boolean deleted = FileUtil.deleteFile(lock);
                LOG.trace("Lock file: {} was deleted: {}", lockFileName, deleted);
            }
        }
    }

    @Override
    public void setTimeout(long timeout) {
        // noop
    }

    @Override
    public void setCheckInterval(long checkInterval) {
        // noop
    }

    @Override
    public void setReadLockLoggingLevel(LoggingLevel readLockLoggingLevel) {
        // noop
    }

    @Override
    public void setMarkerFiler(boolean markerFile) {
        this.markerFile = markerFile;
    }

    @Override
    public void setDeleteOrphanLockFiles(boolean deleteOrphanLockFiles) {
        this.deleteOrphanLockFiles = deleteOrphanLockFiles;
    }

    private static <T> void deleteLockFiles(
            File dir, boolean recursive, int minDepth, int maxDepth, int depth, boolean hiddenFilesEnabled, String endpointPath,
            GenericFileFilter<T> filter,
            AntFilter antFilter,
            Pattern excludePattern,
            Pattern includePattern) {

        if (recursive) {
            LOG.trace("checking: depth {}, minDepth {}, maxDepth {}, directory: {}", depth, minDepth, maxDepth, dir);
        }

        File[] files = dir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {

            if (!hiddenFilesEnabled && file.getName().startsWith(".")) {
                // files starting with dot should be skipped
                continue;
            }

            // filter unwanted files and directories to avoid traveling
            // everything
            if (filter != null || antFilter != null || excludePattern != null || includePattern != null) {

                File targetFile = file;

                // if its a lock file then check if we accept its target file to
                // know if we should delete the orphan lock file
                if (file.getName().endsWith(FileComponent.DEFAULT_LOCK_FILE_POSTFIX)) {
                    String target = file.getName().substring(0,
                            file.getName().length() - FileComponent.DEFAULT_LOCK_FILE_POSTFIX.length());
                    if (file.getParent() != null) {
                        targetFile = new File(file.getParent(), target);
                    } else {
                        targetFile = new File(target);
                    }
                }

                boolean accept = acceptFile(targetFile, endpointPath, filter, antFilter, excludePattern, includePattern);
                if (!accept) {
                    continue;
                }
            }

            if (file.getName().endsWith(FileComponent.DEFAULT_LOCK_FILE_POSTFIX) && (depth >= minDepth)) {
                LOG.warn("Deleting orphaned lock file: {}", file);
                FileUtil.deleteFile(file);
            } else if (recursive && file.isDirectory() && (depth < maxDepth)) {
                deleteLockFiles(file, true, minDepth, maxDepth, depth + 1, hiddenFilesEnabled, endpointPath, filter,
                        antFilter,
                        excludePattern,
                        includePattern);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> boolean acceptFile(
            File file, String endpointPath, GenericFileFilter<T> filter, AntFilter antFilter, Pattern excludePattern,
            Pattern includePattern) {
        GenericFile gf = new GenericFile<>();
        gf.setEndpointPath(endpointPath);
        gf.setFile(file);
        gf.setFileNameOnly(file.getName());
        gf.setFileLength(file.length());
        gf.setDirectory(file.isDirectory());
        // must use FileUtil.isAbsolute to have consistent check for whether the
        // file is
        // absolute or not. As windows do not consider \ paths as absolute where
        // as all
        // other OS platforms will consider \ as absolute. The logic in Zwangine
        // mandates
        // that we align this for all OS. That is why we must use
        // FileUtil.isAbsolute
        // to return a consistent answer for all OS platforms.
        gf.setAbsolute(FileUtil.isAbsolute(file));
        gf.setAbsoluteFilePath(file.getAbsolutePath());
        gf.setLastModified(file.lastModified());

        // compute the file path as relative to the starting directory
        File path;
        String endpointNormalized = FileUtil.normalizePath(endpointPath);
        if (file.getPath().startsWith(endpointNormalized + File.separator)) {
            // skip duplicate endpoint path
            path = new File(StringHelper.after(file.getPath(), endpointNormalized + File.separator));
        } else {
            path = new File(file.getPath());
        }

        if (path.getParent() != null) {
            gf.setRelativeFilePath(path.getParent() + File.separator + file.getName());
        } else {
            gf.setRelativeFilePath(path.getName());
        }

        // the file name should be the relative path
        gf.setFileName(gf.getRelativeFilePath());

        if (filter != null) {
            // a custom filter can also filter directories
            if (!filter.accept(gf)) {
                return false;
            }
        }

        // the following filters only works on files so allow any directory from
        // this point
        if (file.isDirectory()) {
            return true;
        }

        if (antFilter != null) {
            if (!antFilter.accept(gf.isDirectory(), gf.getRelativeFilePath())) {
                return false;
            }
        }

        // exclude take precedence over include
        if (excludePattern != null) {
            if (excludePattern.matcher(file.getName()).matches()) {
                return false;
            }
        }
        if (includePattern != null) {
            if (!includePattern.matcher(file.getName()).matches()) {
                return false;
            }
        }

        return true;
    }

    private static String getLockFileName(GenericFile<File> file) {
        return file.getAbsoluteFilePath() + FileComponent.DEFAULT_LOCK_FILE_POSTFIX;
    }

    private static String asReadLockKey(GenericFile file, String key) {
        // use the copy from absolute path as that was the original path of the
        // file when the lock was acquired
        // for example if the file consumer uses preMove then the file is moved
        // and therefore has another name
        // that would no longer match
        String path
                = file.getCopyFromAbsoluteFilePath() != null ? file.getCopyFromAbsoluteFilePath() : file.getAbsoluteFilePath();
        return path + "-" + key;
    }

    protected static boolean isTimedOut(StopWatch watch, File target, long timeout, LoggingLevel readLockLoggingLevel) {
        long delta = watch.taken();
        if (delta > timeout) {
            ZwangineLogger.log(LOG, readLockLoggingLevel,
                    "Cannot acquire read lock within " + timeout + " millis. Will skip the file: " + target);
            // we could not get the lock within the timeout period,
            // so return false
            return true;
        }
        return false;
    }

}
