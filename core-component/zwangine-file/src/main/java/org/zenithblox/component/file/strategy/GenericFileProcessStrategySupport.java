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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Exchange;
import org.zenithblox.component.file.*;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Base class for implementations of {@link GenericFileProcessStrategy}.
 */
public abstract class GenericFileProcessStrategySupport<T> extends ServiceSupport
        implements GenericFileProcessStrategy<T>, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(GenericFileProcessStrategySupport.class);

    protected GenericFileExclusiveReadLockStrategy<T> exclusiveReadLockStrategy;
    protected ZwangineContext zwangineContext;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void prepareOnStartup(GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint) throws Exception {
        if (exclusiveReadLockStrategy != null) {
            exclusiveReadLockStrategy.prepareOnStartup(operations, endpoint);
        }
    }

    @Override
    public boolean begin(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        // if we use exclusive read then acquire the exclusive read (waiting
        // until we got it)
        if (exclusiveReadLockStrategy != null) {
            boolean lock = exclusiveReadLockStrategy.acquireExclusiveReadLock(operations, file, exchange);
            if (!lock) {
                // do not begin since we could not get the exclusive read lock
                return false;
            }
        }

        return true;
    }

    @Override
    public void abort(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        deleteLocalWorkFile(exchange);
        operations.releaseRetrievedFileResources(exchange);

        // must release lock last
        if (exclusiveReadLockStrategy != null) {
            exclusiveReadLockStrategy.releaseExclusiveReadLockOnAbort(operations, file, exchange);
        }
    }

    @Override
    public void commit(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        deleteLocalWorkFile(exchange);
        operations.releaseRetrievedFileResources(exchange);

        // must release lock last
        if (exclusiveReadLockStrategy != null) {
            exclusiveReadLockStrategy.releaseExclusiveReadLockOnCommit(operations, file, exchange);
        }
    }

    @Override
    public void rollback(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        deleteLocalWorkFile(exchange);
        operations.releaseRetrievedFileResources(exchange);

        // must release lock last
        if (exclusiveReadLockStrategy != null) {
            exclusiveReadLockStrategy.releaseExclusiveReadLockOnRollback(operations, file, exchange);
        }
    }

    public GenericFileExclusiveReadLockStrategy<T> getExclusiveReadLockStrategy() {
        return exclusiveReadLockStrategy;
    }

    public void setExclusiveReadLockStrategy(GenericFileExclusiveReadLockStrategy<T> exclusiveReadLockStrategy) {
        this.exclusiveReadLockStrategy = exclusiveReadLockStrategy;
    }

    protected GenericFile<T> renameFile(GenericFileOperations<T> operations, GenericFile<T> from, GenericFile<T> to) {
        // deleting any existing files before renaming
        try {
            operations.deleteFile(to.getAbsoluteFilePath());
        } catch (GenericFileOperationFailedException e) {
            // ignore the file does not exist
        }

        // make parent folder if missing
        boolean mkdir = operations.buildDirectory(to.getParent(), to.isAbsolute());

        if (!mkdir) {
            throw new GenericFileOperationFailedException(
                    "Cannot create directory: " + to.getParent() + " (could be because of denied permissions)");
        }

        LOG.debug("Renaming file: {} to: {}", from, to);
        boolean renamed = operations.renameFile(from.getAbsoluteFilePath(), to.getAbsoluteFilePath());
        if (!renamed) {
            throw new GenericFileOperationFailedException("Cannot rename file: " + from + " to: " + to);
        }

        return to;
    }

    protected void deleteLocalWorkFile(Exchange exchange) {
        // delete local work file, if it was used (eg by ftp component)
        String local = exchange.getIn().getHeader(FileConstants.FILE_LOCAL_WORK_PATH, String.class);
        if (local != null) {
            File f = new File(local);
            if (f.exists()) {
                boolean deleted = FileUtil.deleteFile(f);
                LOG.trace("Local work file: {} was deleted: {}", local, deleted);
            }
        }
    }

    @Override
    protected void doStart() throws Exception {
        if (exclusiveReadLockStrategy instanceof ZwangineContextAware zwangineContextAware) {
            zwangineContextAware.setZwangineContext(zwangineContext);
        }
        ServiceHelper.startService(exclusiveReadLockStrategy);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(exclusiveReadLockStrategy);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownService(exclusiveReadLockStrategy);
    }
}
