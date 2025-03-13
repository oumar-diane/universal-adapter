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
package org.zenithblox.spi;

import org.zenithblox.*;

/**
 * Factory to create {@link ZwangineEvent events} that are emitted when such an event occur.
 * <p/>
 * For example when an {@link Exchange} is being created and then later when its done.
 */
public interface EventFactory {

    /**
     * Whether to include timestamp for each event, when the event occurred. This is by default false.
     */
    boolean isTimestampEnabled();

    /**
     * Whether to include timestamp for each event, when the event occurred.
     */
    void setTimestampEnabled(boolean timestampEnabled);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine is initializing.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextInitializingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine has been initialized successfully.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextInitializedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine is starting.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStartingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine has been started successfully.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStartedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine failing to start
     *
     * @param  context zwangine context
     * @param  cause   the cause exception
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStartupFailureEvent(ZwangineContext context, Throwable cause);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine failing to stop cleanly
     *
     * @param  context zwangine context
     * @param  cause   the cause exception
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStopFailureEvent(ZwangineContext context, Throwable cause);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine is stopping.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStoppingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine has been stopped successfully.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextStoppedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine workflows starting.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextWorkflowsStartingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine workflows started.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextWorkflowsStartedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine workflows stopping.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextWorkflowsStoppingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine workflows stopped.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextWorkflowsStoppedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for {@link ZwangineContext} being reloaded.
     *
     * @param  context zwangine context
     * @param  source  the source triggered reload
     * @return         the reloading event
     */
    ZwangineEvent createZwangineContextReloading(ZwangineContext context, Object source);

    /**
     * Creates an {@link ZwangineEvent} for {@link ZwangineContext} has been reloaded successfully.
     *
     * @param  context zwangine context
     * @param  source  the source triggered reload
     * @return         the reloaded event
     */
    ZwangineEvent createZwangineContextReloaded(ZwangineContext context, Object source);

    /**
     * Creates an {@link ZwangineEvent} for {@link ZwangineContext} failed reload.
     *
     * @param  context zwangine context
     * @param  source  the source triggered reload
     * @param  cause   the caused of the failure
     * @return         the reloaded failed event
     */
    ZwangineEvent createZwangineContextReloadFailure(ZwangineContext context, Object source, Throwable cause);

    /**
     * Creates an {@link ZwangineEvent} for a Service failed to start cleanly
     *
     * @param  context zwangine context
     * @param  service the service
     * @param  cause   the cause exception
     * @return         the created event
     */
    ZwangineEvent createServiceStartupFailureEvent(ZwangineContext context, Object service, Throwable cause);

    /**
     * Creates an {@link ZwangineEvent} for a Service failed to stop cleanly
     *
     * @param  context zwangine context
     * @param  service the service
     * @param  cause   the cause exception
     * @return         the created event
     */
    ZwangineEvent createServiceStopFailureEvent(ZwangineContext context, Object service, Throwable cause);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} is starting.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowStartingEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} has been started successfully.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowStartedEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} is stopping.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowStoppingEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} has been stopped successfully.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowStoppedEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} has been added successfully.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowAddedEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} has been removed successfully.
     *
     * @param  workflow the workflow
     * @return       the created event
     */
    ZwangineEvent createWorkflowRemovedEvent(Workflow workflow);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} has been reloaded successfully.
     *
     * @param  workflow the workflow
     * @param  index the workflow index in this batch
     * @param  total total number of workflows being reloaded in this batch
     * @return       the reloaded event
     */
    ZwangineEvent createWorkflowReloaded(Workflow workflow, int index, int total);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} being restarted by {@link SupervisingWorkflowController}.
     *
     * @param  workflow   the workflow
     * @param  attempt the attempt number for restarting the workflow
     * @return         the restarting event
     */
    ZwangineEvent createWorkflowRestarting(Workflow workflow, long attempt);

    /**
     * Creates an {@link ZwangineEvent} for {@link Workflow} being restarted and failed by {@link SupervisingWorkflowController}.
     *
     * @param  workflow     the workflow
     * @param  attempt   the attempt number for restarting the workflow
     * @param  cause     the exception causing the failure
     * @param  exhausted whether the supervising controller is exhausted and will not attempt to restart this workflow
     *                   anymore
     * @return           the restarting failure event
     */
    ZwangineEvent createWorkflowRestartingFailure(Workflow workflow, long attempt, Throwable cause, boolean exhausted);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has been created
     *
     * @param  exchange the exchange
     * @return          the created event
     */
    ZwangineEvent createExchangeCreatedEvent(Exchange exchange);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has been completed successfully
     *
     * @param  exchange the exchange
     * @return          the created event
     */
    ZwangineEvent createExchangeCompletedEvent(Exchange exchange);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has failed
     *
     * @param  exchange the exchange
     * @return          the created event
     */
    ZwangineEvent createExchangeFailedEvent(Exchange exchange);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has failed but is being handled by the
     * Zwangine error handlers such as an dead letter channel, or a doTry .. doCatch block.
     * <p/>
     * This event is triggered <b>before</b> sending the failure handler, where as
     * <tt>createExchangeFailureHandledEvent</tt> if the event <b>after</b>.
     *
     * @param  exchange          the exchange
     * @param  failureHandler    the failure handler such as moving the message to a dead letter queue
     * @param  deadLetterChannel whether it was a dead letter channel or not handling the failure
     * @param  deadLetterUri     the dead letter uri, if its a dead letter channel
     * @return                   the created event
     */
    ZwangineEvent createExchangeFailureHandlingEvent(
            Exchange exchange, Processor failureHandler,
            boolean deadLetterChannel, String deadLetterUri);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has failed but was handled by the Zwangine
     * error handlers such as an dead letter channel, or a doTry .. doCatch block.
     * <p/>
     * This event is triggered <b>after</b> the exchange was sent to failure handler, where as
     * <tt>createExchangeFailureHandlingEvent</tt> if the event <b>before</b>.
     *
     * @param  exchange          the exchange
     * @param  failureHandler    the failure handler such as moving the message to a dead letter queue
     * @param  deadLetterChannel whether it was a dead letter channel or not handling the failure
     * @param  deadLetterUri     the dead letter uri, if its a dead letter channel
     * @return                   the created event
     */
    ZwangineEvent createExchangeFailureHandledEvent(
            Exchange exchange, Processor failureHandler,
            boolean deadLetterChannel, String deadLetterUri);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} is about to be redelivered
     *
     * @param  exchange the exchange
     * @param  attempt  the current redelivery attempt (starts from 1)
     * @return          the created event
     */
    ZwangineEvent createExchangeRedeliveryEvent(Exchange exchange, int attempt);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} is about to be sent to the endpoint (eg
     * before).
     *
     * @param  exchange the exchange
     * @param  endpoint the destination
     * @return          the created event
     */
    ZwangineEvent createExchangeSendingEvent(Exchange exchange, Endpoint endpoint);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} asynchronous processing has been started.
     * This is guaranteed to run on the same thread on which {@code WorkflowPolicySupport.onExchangeBegin} was called
     * and/or {@code ExchangeSendingEvent} was fired.
     *
     * Special event only in use for zwangine-tracing / zwangine-opentelemetry. This event is NOT (by default) in use.
     *
     * @param  exchange the exchange
     * @return          the created event
     */
    ZwangineEvent createZwangineExchangeAsyncProcessingStartedEvent(Exchange exchange);

    /**
     * Creates an {@link ZwangineEvent} when an {@link Exchange} has completely been sent to the endpoint
     * (eg after).
     *
     * @param  exchange  the exchange
     * @param  endpoint  the destination
     * @param  timeTaken time in millis taken
     * @return           the created event
     */
    ZwangineEvent createExchangeSentEvent(Exchange exchange, Endpoint endpoint, long timeTaken);

    /**
     * Creates an {@link ZwangineEvent} when a step has been started
     *
     * @param  exchange the exchange
     * @param  stepId   the step id
     * @return          the created event
     */
    ZwangineEvent createStepStartedEvent(Exchange exchange, String stepId);

    /**
     * Creates an {@link ZwangineEvent} when a step has been completed successfully
     *
     * @param  exchange the exchange
     * @param  stepId   the step id
     * @return          the created event
     */
    ZwangineEvent createStepCompletedEvent(Exchange exchange, String stepId);

    /**
     * Creates an {@link ZwangineEvent} when a step has failed
     *
     * @param  exchange the exchange
     * @param  stepId   the step id
     * @return          the created event
     */
    ZwangineEvent createStepFailedEvent(Exchange exchange, String stepId);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine is suspending.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextSuspendingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine has been suspended successfully.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextSuspendedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine is resuming.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextResumingEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine has been resumed successfully.
     *
     * @param  context zwangine context
     * @return         the created event
     */
    ZwangineEvent createZwangineContextResumedEvent(ZwangineContext context);

    /**
     * Creates an {@link ZwangineEvent} for Zwangine failing to resume
     *
     * @param  context zwangine context
     * @param  cause   the cause exception
     * @return         the created event
     */
    ZwangineEvent createZwangineContextResumeFailureEvent(ZwangineContext context, Throwable cause);
}
