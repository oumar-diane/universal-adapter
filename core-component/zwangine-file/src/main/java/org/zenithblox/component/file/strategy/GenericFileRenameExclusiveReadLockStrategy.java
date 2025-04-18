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
import org.zenithblox.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Acquires exclusive read lock to the given file. Will wait until the lock is granted. After granting the read lock it
 * is released, we just want to make sure that when we start consuming the file its not currently in progress of being
 * written by third party.
 */
public class GenericFileRenameExclusiveReadLockStrategy<T> implements GenericFileExclusiveReadLockStrategy<T> {
    private static final Logger LOG = LoggerFactory.getLogger(GenericFileRenameExclusiveReadLockStrategy.class);
    private long timeout;
    private long checkInterval;
    private LoggingLevel readLockLoggingLevel = LoggingLevel.DEBUG;

    @Override
    public void prepareOnStartup(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint) throws Exception {
        // noop
    }

    @Override
    public boolean acquireExclusiveReadLock(GenericFileOperations<T> operations, GenericFile<T> file, Exchange exchange)
            throws Exception {
        LOG.trace("Waiting for exclusive read lock to file: {}", file);

        // the trick is to try to rename the file, if we can rename then we have
        // exclusive read
        // since its a Generic file we cannot use java.nio to get a RW lock
        String newName = file.getFileName() + ".zwangineExclusiveReadLock";

        // make a copy as result and change its file name
        GenericFile<T> newFile = operations.newGenericFile();
        file.copyFrom(file, newFile);
        newFile.changeFileName(newName);
        StopWatch watch = new StopWatch();

        boolean exclusive = false;
        while (!exclusive) {
            // timeout check
            if (timeout > 0) {
                long delta = watch.taken();
                if (delta > timeout) {
                    ZwangineLogger.log(LOG, readLockLoggingLevel,
                            "Cannot acquire read lock within " + timeout + " millis. Will skip the file: " + file);
                    // we could not get the lock within the timeout period, so
                    // return false
                    return false;
                }
            }

            try {
                exclusive = operations.renameFile(file.getAbsoluteFilePath(), newFile.getAbsoluteFilePath());
            } catch (GenericFileOperationFailedException ex) {
                if (ex.getCause() instanceof IOException) {
                    exclusive = false;
                } else {
                    throw ex;
                }
            }
            if (exclusive) {
                LOG.trace("Acquired exclusive read lock to file: {}", file);
                // rename it back so we can read it
                operations.renameFile(newFile.getAbsoluteFilePath(), file.getAbsoluteFilePath());
            } else {
                boolean interrupted = sleep();
                if (interrupted) {
                    // we were interrupted while sleeping, we are likely being
                    // shutdown so return false
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void releaseExclusiveReadLockOnAbort(GenericFileOperations<T> operations, GenericFile<T> file, Exchange exchange)
            throws Exception {
        // noop
    }

    @Override
    public void releaseExclusiveReadLockOnRollback(GenericFileOperations<T> operations, GenericFile<T> file, Exchange exchange)
            throws Exception {
        // noop
    }

    @Override
    public void releaseExclusiveReadLockOnCommit(GenericFileOperations<T> operations, GenericFile<T> file, Exchange exchange)
            throws Exception {
        // noop
    }

    private boolean sleep() {
        LOG.trace("Exclusive read lock not granted. Sleeping for {} millis.", checkInterval);
        try {
            Thread.sleep(checkInterval);
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.debug("Sleep interrupted while waiting for exclusive read lock, so breaking out");
            return true;
        }
    }

    public long getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void setCheckInterval(long checkInterval) {
        this.checkInterval = checkInterval;
    }

    @Override
    public void setReadLockLoggingLevel(LoggingLevel readLockLoggingLevel) {
        this.readLockLoggingLevel = readLockLoggingLevel;
    }

    @Override
    public void setMarkerFiler(boolean markerFile) {
        // noop - we do not use marker file with the rename strategy
    }

    @Override
    public void setDeleteOrphanLockFiles(boolean deleteOrphanLockFiles) {
        // noop - we do not use marker file with the rename strategy
    }
}
