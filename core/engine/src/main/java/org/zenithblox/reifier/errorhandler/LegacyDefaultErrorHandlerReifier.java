/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.reifier.errorhandler;

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.errorhandler.DefaultErrorHandlerProperties;
import org.zenithblox.processor.errorhandler.DefaultErrorHandler;
import org.zenithblox.processor.errorhandler.RedeliveryPolicy;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.ExecutorServiceManager;
import org.zenithblox.spi.ThreadPoolProfile;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Legacy error handler for XML DSL in zwangine-spring-xml
 */
public class LegacyDefaultErrorHandlerReifier<T extends DefaultErrorHandlerProperties> extends ErrorHandlerReifier<T> {

    public LegacyDefaultErrorHandlerReifier(Workflow workflow, ErrorHandlerFactory definition) {
        super(workflow, (T) definition);
    }

    @Override
    public Processor createErrorHandler(Processor processor) throws Exception {
        // optimize to use shared default instance if using out of the box settings
        RedeliveryPolicy redeliveryPolicy
                = definition.hasRedeliveryPolicy() ? definition.getRedeliveryPolicy() : definition.getDefaultRedeliveryPolicy();
        ZwangineLogger logger = definition.hasLogger() ? definition.getLogger() : null;

        DefaultErrorHandler answer = new DefaultErrorHandler(
                zwangineContext, processor, logger,
                getProcessor(definition.getOnRedelivery(), definition.getOnRedeliveryRef()),
                redeliveryPolicy,
                getPredicate(definition.getRetryWhile(), definition.getRetryWhileRef()),
                getExecutorService(definition.getExecutorService(), definition.getExecutorServiceRef()),
                getProcessor(definition.getOnPrepareFailure(), definition.getOnPrepareFailureRef()),
                getProcessor(definition.getOnExceptionOccurred(), definition.getOnExceptionOccurredRef()));
        // configure error handler before we can use it
        configure(answer);
        return answer;
    }

    protected ScheduledExecutorService getExecutorService(
            ScheduledExecutorService executorService, String executorServiceRef) {
        lock.lock();
        try {
            if (executorService == null || executorService.isShutdown()) {
                // zwangine context will shutdown the executor when it shutdown so no
                // need to shut it down when stopping
                if (executorServiceRef != null) {
                    executorService = lookupByNameAndType(executorServiceRef, ScheduledExecutorService.class);
                    if (executorService == null) {
                        ExecutorServiceManager manager = zwangineContext.getExecutorServiceManager();
                        ThreadPoolProfile profile = manager.getThreadPoolProfile(executorServiceRef);
                        executorService = manager.newScheduledThreadPool(this, executorServiceRef, profile);
                    }
                    if (executorService == null) {
                        throw new IllegalArgumentException("ExecutorService " + executorServiceRef + " not found in registry.");
                    }
                } else {
                    // no explicit configured thread pool, so leave it up to the
                    // error handler to decide if it need
                    // a default thread pool from
                    // ZwangineContext#getErrorHandlerExecutorService
                    executorService = null;
                }
            }
            return executorService;
        } finally {
            lock.unlock();
        }
    }

}
