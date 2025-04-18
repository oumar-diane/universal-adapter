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
import org.zenithblox.component.file.FileEndpoint;
import org.zenithblox.component.file.GenericFile;
import org.zenithblox.component.file.GenericFileEndpoint;
import org.zenithblox.component.file.GenericFileOperations;
import org.zenithblox.support.ExchangeHelper;

public class GenericFileRenameProcessStrategy<T> extends GenericFileProcessStrategySupport<T> {
    private GenericFileRenamer<T> beginRenamer;
    private GenericFileRenamer<T> failureRenamer;
    private GenericFileRenamer<T> commitRenamer;

    public GenericFileRenameProcessStrategy() {
    }

    @Override
    public boolean begin(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        // must invoke super
        boolean result = super.begin(operations, endpoint, exchange, file);
        if (!result) {
            return false;
        }

        // okay we got the file then execute the begin renamer
        if (beginRenamer != null) {
            GenericFile<T> newName = beginRenamer.renameFile(operations, exchange, file);
            GenericFile<T> to = renameFile(operations, file, newName);

            if (endpoint instanceof FileEndpoint fe) {
                if (to != null) {
                    to.bindToExchange(exchange, fe.isProbeContentType());
                }
            } else {
                if (to != null) {
                    to.bindToExchange(exchange);
                }
            }

        }

        return true;
    }

    @Override
    public void rollback(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        try {
            operations.releaseRetrievedFileResources(exchange);

            if (failureRenamer != null) {
                // create a copy and bind the file to the exchange to be used by
                // the renamer to evaluate the file name
                Exchange copy = ExchangeHelper.createCopy(exchange, true);
                if (endpoint instanceof FileEndpoint fe) {
                    file.bindToExchange(copy, fe.isProbeContentType());
                } else {
                    file.bindToExchange(copy);
                }
                // must preserve message id
                copy.getMessage().setMessageId(exchange.getMessage().getMessageId());
                copy.setExchangeId(exchange.getExchangeId());

                GenericFile<T> newName = failureRenamer.renameFile(operations, copy, file);
                renameFile(operations, file, newName);
            }
        } finally {
            if (exclusiveReadLockStrategy != null) {
                exclusiveReadLockStrategy.releaseExclusiveReadLockOnRollback(operations, file, exchange);
            }
            deleteLocalWorkFile(exchange);
        }
    }

    @Override
    public void commit(
            GenericFileOperations<T> operations, GenericFileEndpoint<T> endpoint, Exchange exchange, GenericFile<T> file)
            throws Exception {
        try {
            operations.releaseRetrievedFileResources(exchange);

            if (commitRenamer != null) {
                // create a copy and bind the file to the exchange to be used by
                // the renamer to evaluate the file name
                Exchange copy = ExchangeHelper.createCopy(exchange, true);
                if (endpoint instanceof FileEndpoint fe) {
                    file.bindToExchange(copy, fe.isProbeContentType());
                } else {
                    file.bindToExchange(copy);
                }
                // must preserve message id
                copy.getMessage().setMessageId(exchange.getMessage().getMessageId());
                copy.setExchangeId(exchange.getExchangeId());

                GenericFile<T> newName = commitRenamer.renameFile(operations, copy, file);
                renameFile(operations, file, newName);
            }
        } finally {
            deleteLocalWorkFile(exchange);
            // must release lock last
            if (exclusiveReadLockStrategy != null) {
                exclusiveReadLockStrategy.releaseExclusiveReadLockOnCommit(operations, file, exchange);
            }
        }
    }

    public GenericFileRenamer<T> getBeginRenamer() {
        return beginRenamer;
    }

    public void setBeginRenamer(GenericFileRenamer<T> beginRenamer) {
        this.beginRenamer = beginRenamer;
    }

    public GenericFileRenamer<T> getCommitRenamer() {
        return commitRenamer;
    }

    public void setCommitRenamer(GenericFileRenamer<T> commitRenamer) {
        this.commitRenamer = commitRenamer;
    }

    public GenericFileRenamer<T> getFailureRenamer() {
        return failureRenamer;
    }

    public void setFailureRenamer(GenericFileRenamer<T> failureRenamer) {
        this.failureRenamer = failureRenamer;
    }
}
