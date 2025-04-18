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
import org.zenithblox.Expression;
import org.zenithblox.LoggingLevel;
import org.zenithblox.component.file.FileConstants;
import org.zenithblox.component.file.GenericFileExclusiveReadLockStrategy;
import org.zenithblox.component.file.GenericFileProcessStrategy;
import org.zenithblox.component.file.GenericFileProcessStrategyFactory;
import org.zenithblox.spi.IdempotentRepository;
import org.zenithblox.spi.Language;
import org.zenithblox.util.ObjectHelper;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public final class FileProcessStrategyFactory implements GenericFileProcessStrategyFactory<File> {

    public GenericFileProcessStrategy<File> createGenericFileProcessStrategy(ZwangineContext context, Map<String, Object> params) {

        // We assume a value is present only if its value not null for String
        // and 'true' for boolean
        Expression moveExpression = (Expression) params.get("move");
        Expression moveFailedExpression = (Expression) params.get("moveFailed");
        Expression preMoveExpression = (Expression) params.get("preMove");
        boolean isNoop = params.get("noop") != null;
        boolean isDelete = params.get("delete") != null;
        boolean isMove = moveExpression != null || preMoveExpression != null || moveFailedExpression != null;

        if (isDelete) {
            return newGenericFileDeleteProcessStrategy(params, preMoveExpression, moveFailedExpression);
        } else if (isMove || isNoop) {
            return newGenericFileRenameProcessStrategy(context, params, isNoop, moveExpression, preMoveExpression,
                    moveFailedExpression);
        } else {
            // default strategy will move files in a .zwangine/ subfolder where the
            // file was consumed
            return newGenericFileRenameProcessStrategy(context, params);
        }
    }

    private static GenericFileRenameProcessStrategy<File> newGenericFileRenameProcessStrategy(
            ZwangineContext context, Map<String, Object> params) {
        GenericFileRenameProcessStrategy<File> strategy = new GenericFileRenameProcessStrategy<>();
        strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
        strategy.setCommitRenamer(getDefaultCommitRenamer(context));
        return strategy;
    }

    private static GenericFileRenameProcessStrategy<File> newGenericFileRenameProcessStrategy(
            ZwangineContext context, Map<String, Object> params, boolean isNoop, Expression moveExpression,
            Expression preMoveExpression, Expression moveFailedExpression) {
        GenericFileRenameProcessStrategy<File> strategy = new GenericFileRenameProcessStrategy<>();
        strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
        if (!isNoop) {
            // move on commit is only possible if not noop
            if (moveExpression != null) {
                GenericFileExpressionRenamer<File> renamer = new GenericFileExpressionRenamer<>();
                renamer.setExpression(moveExpression);
                strategy.setCommitRenamer(renamer);
            } else {
                strategy.setCommitRenamer(getDefaultCommitRenamer(context));
            }
        }
        // both move and noop supports pre move
        if (preMoveExpression != null) {
            GenericFileExpressionRenamer<File> renamer = new GenericFileExpressionRenamer<>();
            renamer.setExpression(preMoveExpression);
            strategy.setBeginRenamer(renamer);
        }
        // both move and noop supports move failed
        if (moveFailedExpression != null) {
            GenericFileExpressionRenamer<File> renamer = new GenericFileExpressionRenamer<>();
            renamer.setExpression(moveFailedExpression);
            strategy.setFailureRenamer(renamer);
        }
        return strategy;
    }

    private static GenericFileDeleteProcessStrategy<File> newGenericFileDeleteProcessStrategy(
            Map<String, Object> params, Expression preMoveExpression, Expression moveFailedExpression) {
        GenericFileDeleteProcessStrategy<File> strategy = new GenericFileDeleteProcessStrategy<>();
        strategy.setExclusiveReadLockStrategy(getExclusiveReadLockStrategy(params));
        if (preMoveExpression != null) {
            GenericFileExpressionRenamer<File> renamer = new GenericFileExpressionRenamer<>();
            renamer.setExpression(preMoveExpression);
            strategy.setBeginRenamer(renamer);
        }
        if (moveFailedExpression != null) {
            GenericFileExpressionRenamer<File> renamer = new GenericFileExpressionRenamer<>();
            renamer.setExpression(moveFailedExpression);
            strategy.setFailureRenamer(renamer);
        }
        return strategy;
    }

    private static GenericFileExpressionRenamer<File> getDefaultCommitRenamer(ZwangineContext context) {
        // use context to lookup language to let it be loose coupled
        Language language = context.resolveLanguage("file");
        Expression expression
                = language.createExpression("${file:parent}/" + FileConstants.DEFAULT_SUB_FOLDER + "/${file:onlyname}");
        return new GenericFileExpressionRenamer<>(expression);
    }

    @SuppressWarnings("unchecked")
    private static GenericFileExclusiveReadLockStrategy<File> getExclusiveReadLockStrategy(Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy
                = (GenericFileExclusiveReadLockStrategy<File>) params.get("exclusiveReadLockStrategy");
        if (strategy != null) {
            return strategy;
        }

        // no explicit strategy set then fallback to readLock option
        return fallbackToReadLock(params);
    }

    private static GenericFileExclusiveReadLockStrategy<File> fallbackToReadLock(
            Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy = null;
        String readLock = (String) params.get("readLock");
        if (ObjectHelper.isNotEmpty(readLock)) {
            if ("none".equals(readLock) || "false".equals(readLock)) {
                return null;
            } else if ("markerFile".equals(readLock)) {
                strategy = new MarkerFileExclusiveReadLockStrategy();
            } else if ("fileLock".equals(readLock)) {
                strategy = new FileLockExclusiveReadLockStrategy();
            } else if ("rename".equals(readLock)) {
                strategy = new FileRenameExclusiveReadLockStrategy();
            } else if ("changed".equals(readLock)) {
                strategy = newStrategyForChanged(params);
            } else if ("idempotent".equals(readLock)) {
                strategy = newStrategyForIdempotent(params);
            } else if ("idempotent-changed".equals(readLock)) {
                strategy = newStrategyForIdempotentChanged(params);
            } else if ("idempotent-rename".equals(readLock)) {
                strategy = newStrategyForIdempotentRename(params);
            }

            if (strategy != null) {
                setupStrategy(params, strategy);
            }
        }

        return strategy;
    }

    private static void setupStrategy(Map<String, Object> params, GenericFileExclusiveReadLockStrategy<File> strategy) {
        Long timeout = (Long) params.get("readLockTimeout");
        if (timeout != null) {
            strategy.setTimeout(timeout);
        }
        Long checkInterval = (Long) params.get("readLockCheckInterval");
        if (checkInterval != null) {
            strategy.setCheckInterval(checkInterval);
        }
        LoggingLevel readLockLoggingLevel = (LoggingLevel) params.get("readLockLoggingLevel");
        if (readLockLoggingLevel != null) {
            strategy.setReadLockLoggingLevel(readLockLoggingLevel);
        }
        Boolean readLockMarkerFile = (Boolean) params.get("readLockMarkerFile");
        if (readLockMarkerFile != null) {
            strategy.setMarkerFiler(readLockMarkerFile);
        }
        Boolean readLockDeleteOrphanLockFiles = (Boolean) params.get("readLockDeleteOrphanLockFiles");
        if (readLockDeleteOrphanLockFiles != null) {
            strategy.setDeleteOrphanLockFiles(readLockDeleteOrphanLockFiles);
        }
    }

    private static GenericFileExclusiveReadLockStrategy<File> newStrategyForChanged(
            Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy;
        FileChangedExclusiveReadLockStrategy readLockStrategy = new FileChangedExclusiveReadLockStrategy();
        Long minLength = (Long) params.get("readLockMinLength");
        if (minLength != null) {
            readLockStrategy.setMinLength(minLength);
        }
        Long minAge = (Long) params.get("readLockMinAge");
        if (null != minAge) {
            readLockStrategy.setMinAge(minAge);
        }
        strategy = readLockStrategy;
        return strategy;
    }

    private static GenericFileExclusiveReadLockStrategy<File> newStrategyForIdempotentRename(
            Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy;
        FileIdempotentRenameRepositoryReadLockStrategy readLockStrategy
                = new FileIdempotentRenameRepositoryReadLockStrategy();
        Boolean readLockRemoveOnRollback = (Boolean) params.get("readLockRemoveOnRollback");
        if (readLockRemoveOnRollback != null) {
            readLockStrategy.setRemoveOnRollback(readLockRemoveOnRollback);
        }
        Boolean readLockRemoveOnCommit = (Boolean) params.get("readLockRemoveOnCommit");
        if (readLockRemoveOnCommit != null) {
            readLockStrategy.setRemoveOnCommit(readLockRemoveOnCommit);
        }
        IdempotentRepository repo = (IdempotentRepository) params.get("readLockIdempotentRepository");
        if (repo != null) {
            readLockStrategy.setIdempotentRepository(repo);
        }
        strategy = readLockStrategy;
        return strategy;
    }

    private static GenericFileExclusiveReadLockStrategy<File> newStrategyForIdempotentChanged(
            Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy;
        FileIdempotentChangedRepositoryReadLockStrategy readLockStrategy
                = new FileIdempotentChangedRepositoryReadLockStrategy();
        Boolean readLockRemoveOnRollback = (Boolean) params.get("readLockRemoveOnRollback");
        if (readLockRemoveOnRollback != null) {
            readLockStrategy.setRemoveOnRollback(readLockRemoveOnRollback);
        }
        Boolean readLockRemoveOnCommit = (Boolean) params.get("readLockRemoveOnCommit");
        if (readLockRemoveOnCommit != null) {
            readLockStrategy.setRemoveOnCommit(readLockRemoveOnCommit);
        }
        IdempotentRepository repo = (IdempotentRepository) params.get("readLockIdempotentRepository");
        if (repo != null) {
            readLockStrategy.setIdempotentRepository(repo);
        }
        Long minLength = (Long) params.get("readLockMinLength");
        if (minLength != null) {
            readLockStrategy.setMinLength(minLength);
        }
        Long minAge = (Long) params.get("readLockMinAge");
        if (null != minAge) {
            readLockStrategy.setMinAge(minAge);
        }
        Integer readLockIdempotentReleaseDelay = (Integer) params.get("readLockIdempotentReleaseDelay");
        if (readLockIdempotentReleaseDelay != null) {
            readLockStrategy.setReadLockIdempotentReleaseDelay(readLockIdempotentReleaseDelay);
        }
        Boolean readLockIdempotentReleaseAsync = (Boolean) params.get("readLockIdempotentReleaseAsync");
        if (readLockIdempotentReleaseAsync != null) {
            readLockStrategy.setReadLockIdempotentReleaseAsync(readLockIdempotentReleaseAsync);
        }
        Integer readLockIdempotentReleaseAsyncPoolSize = (Integer) params.get("readLockIdempotentReleaseAsyncPoolSize");
        if (readLockIdempotentReleaseAsyncPoolSize != null) {
            readLockStrategy.setReadLockIdempotentReleaseAsyncPoolSize(readLockIdempotentReleaseAsyncPoolSize);
        }
        ScheduledExecutorService readLockIdempotentReleaseExecutorService
                = (ScheduledExecutorService) params.get("readLockIdempotentReleaseExecutorService");
        if (readLockIdempotentReleaseExecutorService != null) {
            readLockStrategy.setReadLockIdempotentReleaseExecutorService(readLockIdempotentReleaseExecutorService);
        }
        strategy = readLockStrategy;
        return strategy;
    }

    private static GenericFileExclusiveReadLockStrategy<File> newStrategyForIdempotent(
            Map<String, Object> params) {
        GenericFileExclusiveReadLockStrategy<File> strategy;
        FileIdempotentRepositoryReadLockStrategy readLockStrategy = new FileIdempotentRepositoryReadLockStrategy();
        Boolean readLockRemoveOnRollback = (Boolean) params.get("readLockRemoveOnRollback");
        if (readLockRemoveOnRollback != null) {
            readLockStrategy.setRemoveOnRollback(readLockRemoveOnRollback);
        }
        Boolean readLockRemoveOnCommit = (Boolean) params.get("readLockRemoveOnCommit");
        if (readLockRemoveOnCommit != null) {
            readLockStrategy.setRemoveOnCommit(readLockRemoveOnCommit);
        }
        IdempotentRepository repo = (IdempotentRepository) params.get("readLockIdempotentRepository");
        if (repo != null) {
            readLockStrategy.setIdempotentRepository(repo);
        }
        Integer readLockIdempotentReleaseDelay = (Integer) params.get("readLockIdempotentReleaseDelay");
        if (readLockIdempotentReleaseDelay != null) {
            readLockStrategy.setReadLockIdempotentReleaseDelay(readLockIdempotentReleaseDelay);
        }
        Boolean readLockIdempotentReleaseAsync = (Boolean) params.get("readLockIdempotentReleaseAsync");
        if (readLockIdempotentReleaseAsync != null) {
            readLockStrategy.setReadLockIdempotentReleaseAsync(readLockIdempotentReleaseAsync);
        }
        Integer readLockIdempotentReleaseAsyncPoolSize = (Integer) params.get("readLockIdempotentReleaseAsyncPoolSize");
        if (readLockIdempotentReleaseAsyncPoolSize != null) {
            readLockStrategy.setReadLockIdempotentReleaseAsyncPoolSize(readLockIdempotentReleaseAsyncPoolSize);
        }
        ScheduledExecutorService readLockIdempotentReleaseExecutorService
                = (ScheduledExecutorService) params.get("readLockIdempotentReleaseExecutorService");
        if (readLockIdempotentReleaseExecutorService != null) {
            readLockStrategy.setReadLockIdempotentReleaseExecutorService(readLockIdempotentReleaseExecutorService);
        }
        strategy = readLockStrategy;
        return strategy;
    }
}
