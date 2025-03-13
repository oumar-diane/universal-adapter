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

import org.zenithblox.*;
import org.zenithblox.model.RedeliveryPolicyDefinition;
import org.zenithblox.model.errorhandler.DeadLetterChannelDefinition;
import org.zenithblox.processor.FatalFallbackErrorHandler;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.processor.errorhandler.DeadLetterChannel;
import org.zenithblox.processor.errorhandler.RedeliveryPolicy;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.ExecutorServiceManager;
import org.zenithblox.spi.Language;
import org.zenithblox.spi.ThreadPoolProfile;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

public class DeadLetterChannelReifier extends ErrorHandlerReifier<DeadLetterChannelDefinition> {

    public DeadLetterChannelReifier(Workflow workflow, DeadLetterChannelDefinition definition) {
        super(workflow, definition);
    }

    @Override
    public Processor createErrorHandler(Processor processor) throws Exception {
        String uri = parseString(definition.getDeadLetterUri());
        ObjectHelper.notNull(uri, "deadLetterUri", this);

        // optimize to use shared default instance if using out of the box settings
        RedeliveryPolicy redeliveryPolicy = resolveRedeliveryPolicy(definition, zwangineContext);
        ZwangineLogger logger = resolveLogger(definition);

        Processor deadLetterProcessor = createDeadLetterChannelProcessor(uri);

        DeadLetterChannel answer = new DeadLetterChannel(
                zwangineContext, processor, logger,
                getProcessor(definition.getOnRedeliveryProcessor(), definition.getOnRedeliveryRef()),
                redeliveryPolicy, deadLetterProcessor, uri,
                parseBoolean(definition.getDeadLetterHandleNewException(), true),
                parseBoolean(definition.getUseOriginalMessage(), false),
                parseBoolean(definition.getUseOriginalBody(), false),
                resolveRetryWhilePolicy(definition, zwangineContext),
                getExecutorService(definition.getExecutorServiceBean(), definition.getExecutorServiceRef()),
                getProcessor(definition.getOnPrepareFailureProcessor(), definition.getOnPrepareFailureRef()),
                getProcessor(definition.getOnExceptionOccurredProcessor(), definition.getOnExceptionOccurredRef()));
        // configure error handler before we can use it
        configure(answer);
        return answer;
    }

    private Predicate resolveRetryWhilePolicy(DeadLetterChannelDefinition definition, ZwangineContext zwangineContext) {
        Predicate answer = definition.getRetryWhilePredicate();

        if (answer == null && definition.getRetryWhileRef() != null) {
            // it is a bean expression
            Language bean = zwangineContext.resolveLanguage("bean");
            answer = bean.createPredicate(definition.getRetryWhileRef());
            answer.initPredicate(zwangineContext);
        }

        return answer;
    }

    private ZwangineLogger resolveLogger(DeadLetterChannelDefinition definition) {
        ZwangineLogger answer = definition.getLoggerBean();
        if (answer == null && definition.getLoggerRef() != null) {
            answer = mandatoryLookup(definition.getLoggerRef(), ZwangineLogger.class);
        }
        if (answer == null) {
            answer = new ZwangineLogger(LoggerFactory.getLogger(DeadLetterChannel.class), LoggingLevel.ERROR);
        }
        if (definition.getLevel() != null) {
            answer.setLevel(parse(LoggingLevel.class, definition.getLevel()));
        }
        return answer;
    }

    private Processor createDeadLetterChannelProcessor(String uri) {
        // wrap in our special safe fallback error handler if sending to
        // dead letter channel fails
        Processor child = new SendProcessor(zwangineContext.getEndpoint(uri), ExchangePattern.InOnly);
        // force MEP to be InOnly so when sending to DLQ we would not expect
        // a reply if the MEP was InOut
        return new FatalFallbackErrorHandler(child, true);
    }

    private RedeliveryPolicy resolveRedeliveryPolicy(DeadLetterChannelDefinition definition, ZwangineContext zwangineContext) {
        if (definition.hasRedeliveryPolicy() && definition.getRedeliveryPolicyRef() != null) {
            throw new IllegalArgumentException(
                    "Cannot have both redeliveryPolicy and redeliveryPolicyRef set at the same time.");
        }

        RedeliveryPolicy answer = null;
        RedeliveryPolicyDefinition def = definition.hasRedeliveryPolicy() ? definition.getRedeliveryPolicy() : null;
        if (def == null && definition.getRedeliveryPolicyRef() != null) {
            // ref may point to a definition
            def = lookupByNameAndType(definition.getRedeliveryPolicyRef(), RedeliveryPolicyDefinition.class);
        }
        if (def != null) {
            answer = ErrorHandlerReifier.createRedeliveryPolicy(def, zwangineContext, null);
        }
        if (def == null && definition.getRedeliveryPolicyRef() != null) {
            answer = mandatoryLookup(definition.getRedeliveryPolicyRef(), RedeliveryPolicy.class);
        }
        if (answer == null) {
            answer = RedeliveryPolicy.DEFAULT_POLICY;
        }
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
                    // error handler to decide if it need a default thread pool from
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
