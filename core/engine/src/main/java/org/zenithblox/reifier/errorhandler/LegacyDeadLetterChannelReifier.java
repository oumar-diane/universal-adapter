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
import org.zenithblox.ExchangePattern;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.errorhandler.DeadLetterChannelProperties;
import org.zenithblox.processor.FatalFallbackErrorHandler;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.processor.errorhandler.DeadLetterChannel;
import org.zenithblox.processor.errorhandler.RedeliveryPolicy;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.util.ObjectHelper;

/**
 * Legacy error handler for XML DSL in zwangine-spring-xml
 */
public class LegacyDeadLetterChannelReifier extends LegacyDefaultErrorHandlerReifier<DeadLetterChannelProperties> {

    public LegacyDeadLetterChannelReifier(Workflow workflow, ErrorHandlerFactory definition) {
        super(workflow, definition);
    }

    @Override
    public Processor createErrorHandler(Processor processor) throws Exception {
        ObjectHelper.notNull(definition.getDeadLetterUri(), "deadLetterUri", this);

        // optimize to use shared default instance if using out of the box settings
        RedeliveryPolicy redeliveryPolicy
                = definition.hasRedeliveryPolicy() ? definition.getRedeliveryPolicy() : definition.getDefaultRedeliveryPolicy();
        ZwangineLogger logger = definition.hasLogger() ? definition.getLogger() : null;

        Processor deadLetterProcessor = createDeadLetterChannelProcessor(definition.getDeadLetterUri());

        DeadLetterChannel answer = new DeadLetterChannel(
                zwangineContext, processor, logger,
                getProcessor(definition.getOnRedelivery(), definition.getOnRedeliveryRef()),
                redeliveryPolicy, deadLetterProcessor,
                definition.getDeadLetterUri(), definition.isDeadLetterHandleNewException(), definition.isUseOriginalMessage(),
                definition.isUseOriginalBody(),
                definition.getRetryWhilePolicy(zwangineContext),
                getExecutorService(definition.getExecutorService(), definition.getExecutorServiceRef()),
                getProcessor(definition.getOnPrepareFailure(), definition.getOnPrepareFailureRef()),
                getProcessor(definition.getOnExceptionOccurred(), definition.getOnExceptionOccurredRef()));
        // configure error handler before we can use it
        configure(answer);
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

}
