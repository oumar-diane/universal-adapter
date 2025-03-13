/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.ExceptionHandler;
import org.slf4j.LoggerFactory;

/**
 * A default implementation of {@link ExceptionHandler} which uses a {@link ZwangineLogger} to log the exception.
 * <p/>
 * This implementation will by default log the exception with stack trace at WARN level.
 * <p/>
 * This implementation honors the {@link org.zenithblox.spi.ShutdownStrategy#isSuppressLoggingOnTimeout()} option to
 * avoid logging if the logging should be suppressed.
 */
public class LoggingExceptionHandler implements ExceptionHandler {
    private final ZwangineLogger logger;
    private final ZwangineContext zwangineContext;

    public LoggingExceptionHandler(ZwangineContext zwangineContext, Class<?> ownerType) {
        this(zwangineContext, new ZwangineLogger(LoggerFactory.getLogger(ownerType), LoggingLevel.WARN));
    }

    public LoggingExceptionHandler(ZwangineContext zwangineContext, Class<?> ownerType, LoggingLevel level) {
        this(zwangineContext, new ZwangineLogger(LoggerFactory.getLogger(ownerType), level));
    }

    public LoggingExceptionHandler(ZwangineContext zwangineContext, ZwangineLogger logger) {
        this.zwangineContext = zwangineContext;
        this.logger = logger;
    }

    @Override
    public void handleException(Throwable exception) {
        handleException(null, null, exception);
    }

    @Override
    public void handleException(String message, Throwable exception) {
        handleException(message, null, exception);
    }

    @Override
    public void handleException(String message, Exchange exchange, Throwable exception) {
        try {
            if (!isSuppressLogging()) {
                String msg = ZwangineExchangeException.createExceptionMessage(message, exchange, exception);
                if (isCausedByRollbackExchangeException(exception)) {
                    // do not log stack trace for intended rollbacks
                    logger.log(msg);
                } else {
                    if (exception != null) {
                        logger.log(msg, exception);
                    } else {
                        logger.log(msg);
                    }
                }
            }
        } catch (Exception e) {
            // the logging exception handler must not cause new exceptions to occur
        }
    }

    protected boolean isCausedByRollbackExchangeException(Throwable exception) {
        if (exception == null) {
            return false;
        }
        if (exception instanceof RollbackExchangeException) {
            return true;
        } else if (exception.getCause() != null) {
            // recursive children
            return isCausedByRollbackExchangeException(exception.getCause());
        }

        return false;
    }

    protected boolean isSuppressLogging() {
        if (zwangineContext != null) {
            return (zwangineContext.getStatus().isStopping() || zwangineContext.getStatus().isStopped())
                    && zwangineContext.getShutdownStrategy().isTimeoutOccurred()
                    && zwangineContext.getShutdownStrategy().isSuppressLoggingOnTimeout();
        } else {
            return false;
        }
    }
}
