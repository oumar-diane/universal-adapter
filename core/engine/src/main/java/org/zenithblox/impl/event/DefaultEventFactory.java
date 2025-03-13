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
package org.zenithblox.impl.event;

import org.zenithblox.*;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.spi.EventFactory;

/**
 * Default implementation of the {@link org.zenithblox.spi.EventFactory}.
 */
public class DefaultEventFactory implements EventFactory {

    private boolean timestampEnabled;

    @Override
    public boolean isTimestampEnabled() {
        return timestampEnabled;
    }

    @Override
    public void setTimestampEnabled(boolean timestampEnabled) {
        this.timestampEnabled = timestampEnabled;
    }

    @Override
    public ZwangineEvent createZwangineContextInitializingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextInitializingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextInitializedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextInitializedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStartingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextStartingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStartedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextStartedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStoppingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextStoppingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStoppedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextStoppedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextWorkflowsStartingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextWorkflowsStartingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextWorkflowsStartedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextWorkflowsStartedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextWorkflowsStoppingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextWorkflowsStoppingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextWorkflowsStoppedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextWorkflowsStoppedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStartupFailureEvent(ZwangineContext context, Throwable cause) {
        ZwangineEvent answer = new ZwangineContextStartupFailureEvent(context, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextStopFailureEvent(ZwangineContext context, Throwable cause) {
        ZwangineEvent answer = new ZwangineContextStopFailureEvent(context, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextReloading(ZwangineContext context, Object source) {
        ZwangineEvent answer = new ZwangineContextReloadingEvent(context, source);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextReloadFailure(ZwangineContext context, Object source, Throwable cause) {
        ZwangineEvent answer = new ZwangineContextReloadFailureEvent(context, source, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextReloaded(ZwangineContext context, Object source) {
        ZwangineEvent answer = new ZwangineContextReloadedEvent(context, source);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createServiceStartupFailureEvent(ZwangineContext context, Object service, Throwable cause) {
        ZwangineEvent answer = new ServiceStartupFailureEvent(context, service, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createServiceStopFailureEvent(ZwangineContext context, Object service, Throwable cause) {
        ZwangineEvent answer = new ServiceStopFailureEvent(context, service, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowStartingEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowStartingEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowStartedEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowStartedEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowRestarting(Workflow workflow, long attempt) {
        ZwangineEvent answer = new WorkflowRestartingEvent(workflow, attempt);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowRestartingFailure(Workflow workflow, long attempt, Throwable cause, boolean exhausted) {
        ZwangineEvent answer = new WorkflowRestartingFailureEvent(workflow, attempt, cause, exhausted);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowStoppingEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowStoppingEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createWorkflowStoppedEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowStoppedEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;

    }

    @Override
    public ZwangineEvent createWorkflowAddedEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowAddedEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;

    }

    @Override
    public ZwangineEvent createWorkflowRemovedEvent(Workflow workflow) {
        ZwangineEvent answer = new WorkflowRemovedEvent(workflow);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;

    }

    @Override
    public ZwangineEvent createWorkflowReloaded(Workflow workflow, int index, int total) {
        ZwangineEvent answer = new WorkflowReloadedEvent(workflow, index, total);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeCreatedEvent(Exchange exchange) {
        ZwangineEvent answer = new ExchangeCreatedEvent(exchange);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeCompletedEvent(Exchange exchange) {
        ZwangineEvent answer = new ExchangeCompletedEvent(exchange);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeFailedEvent(Exchange exchange) {
        ZwangineEvent answer = new ExchangeFailedEvent(exchange);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeFailureHandlingEvent(
            Exchange exchange, Processor failureHandler, boolean deadLetterChannel, String deadLetterUri) {
        // unwrap delegate processor
        Processor handler = failureHandler;
        if (handler instanceof DelegateProcessor delegateProcessor) {
            handler = delegateProcessor.getProcessor();
        }
        ZwangineEvent answer = new ExchangeFailureHandlingEvent(exchange, handler, deadLetterChannel, deadLetterUri);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeFailureHandledEvent(
            Exchange exchange, Processor failureHandler,
            boolean deadLetterChannel, String deadLetterUri) {
        // unwrap delegate processor
        Processor handler = failureHandler;
        if (handler instanceof DelegateProcessor delegateProcessor) {
            handler = delegateProcessor.getProcessor();
        }
        ZwangineEvent answer = new ExchangeFailureHandledEvent(exchange, handler, deadLetterChannel, deadLetterUri);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeRedeliveryEvent(Exchange exchange, int attempt) {
        ZwangineEvent answer = new ExchangeRedeliveryEvent(exchange, attempt);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeSendingEvent(Exchange exchange, Endpoint endpoint) {
        ZwangineEvent answer = new ExchangeSendingEvent(exchange, endpoint);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createExchangeSentEvent(Exchange exchange, Endpoint endpoint, long timeTaken) {
        ZwangineEvent answer = new ExchangeSentEvent(exchange, endpoint, timeTaken);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createStepStartedEvent(Exchange exchange, String stepId) {
        ZwangineEvent answer = new StepStartedEvent(exchange, stepId);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createStepCompletedEvent(Exchange exchange, String stepId) {
        ZwangineEvent answer = new StepCompletedEvent(exchange, stepId);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createStepFailedEvent(Exchange exchange, String stepId) {
        ZwangineEvent answer = new StepFailedEvent(exchange, stepId);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextSuspendingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextSuspendingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextSuspendedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextSuspendedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextResumingEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextResumingEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextResumedEvent(ZwangineContext context) {
        ZwangineEvent answer = new ZwangineContextResumedEvent(context);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineContextResumeFailureEvent(ZwangineContext context, Throwable cause) {
        ZwangineEvent answer = new ZwangineContextResumeFailureEvent(context, cause);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }

    @Override
    public ZwangineEvent createZwangineExchangeAsyncProcessingStartedEvent(Exchange exchange) {
        ZwangineEvent answer = new ExchangeAsyncProcessingStartedEvent(exchange);
        if (timestampEnabled) {
            answer.setTimestamp(System.currentTimeMillis());
        }
        return answer;
    }
}
