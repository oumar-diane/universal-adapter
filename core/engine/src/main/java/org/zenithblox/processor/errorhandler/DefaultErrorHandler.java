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
package org.zenithblox.processor.errorhandler;

import org.zenithblox.ZwangineContext;
import org.zenithblox.LoggingLevel;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.ErrorHandler;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Default error handler
 */
public class DefaultErrorHandler extends RedeliveryErrorHandler {

    private static final ZwangineLogger DEFAULT_LOGGER
            = new ZwangineLogger(LoggerFactory.getLogger(DefaultErrorHandler.class), LoggingLevel.ERROR);

    /**
     * Creates the default error handler.
     *
     * @param zwangineContext                 the zwangine context
     * @param output                       outer processor that should use this default error handler
     * @param logger                       logger to use for logging failures and redelivery attempts
     * @param redeliveryProcessor          an optional processor to run before redelivery attempt
     * @param redeliveryPolicy             policy for redelivery
     * @param retryWhile                   retry while
     * @param executorService              the {@link ScheduledExecutorService} to be used for
     *                                     redelivery thread pool. Can be <tt>null</tt>.
     * @param onPrepareProcessor           a custom {@link org.zenithblox.Processor} to prepare the
     *                                     {@link org.zenithblox.Exchange} before handled by the failure processor /
     *                                     dead letter channel.
     * @param onExceptionOccurredProcessor a custom {@link org.zenithblox.Processor} to process the
     *                                     {@link org.zenithblox.Exchange} just after an exception was thrown.
     */
    public DefaultErrorHandler(ZwangineContext zwangineContext, Processor output, ZwangineLogger logger, Processor redeliveryProcessor,
                               RedeliveryPolicy redeliveryPolicy, Predicate retryWhile,
                               ScheduledExecutorService executorService, Processor onPrepareProcessor,
                               Processor onExceptionOccurredProcessor) {

        super(zwangineContext, output, logger != null ? logger : DEFAULT_LOGGER, redeliveryProcessor, redeliveryPolicy, null, null,
              true, false, false, retryWhile,
              executorService, onPrepareProcessor, onExceptionOccurredProcessor);
    }

    @Override
    public ErrorHandler clone(Processor output) {
        DefaultErrorHandler answer = new DefaultErrorHandler(
                zwangineContext, output, logger, redeliveryProcessor, redeliveryPolicy, retryWhilePolicy, executorService,
                onPrepareProcessor, onExceptionProcessor);
        // shallow clone is okay as we do not mutate these
        if (exceptionPolicies != null) {
            answer.exceptionPolicies = exceptionPolicies;
        }
        return answer;
    }

    @Override
    public String toString() {
        if (output == null) {
            // if no output then dont do any description
            return "";
        }
        return "DefaultErrorHandler[" + output + "]";
    }

}
