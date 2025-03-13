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
 * This interface is implemented by all events.
 */
public interface ZwangineEvent {

    enum Type {
        ZwangineContextInitializing,
        ZwangineContextInitialized,
        ZwangineContextResumed,
        ZwangineContextResumeFailure,
        ZwangineContextResuming,
        ZwangineContextStarted,
        ZwangineContextStarting,
        ZwangineContextStartupFailure,
        ZwangineContextStopFailure,
        ZwangineContextStopped,
        ZwangineContextStopping,
        ZwangineContextSuspended,
        ZwangineContextSuspending,
        ZwangineContextReloading,
        ZwangineContextReloaded,
        ZwangineContextReloadFailure,
        ExchangeCompleted,
        ExchangeCreated,
        ExchangeFailed,
        ExchangeFailureHandled,
        ExchangeFailureHandling,
        ExchangeRedelivery,
        ExchangeSending,
        ExchangeSent,
        ExchangeAsyncProcessingStarted,
        WorkflowsStarting,
        WorkflowsStarted,
        WorkflowsStopping,
        WorkflowsStopped,
        WorkflowAdded,
        WorkflowRemoved,
        WorkflowReloaded,
        WorkflowStarting,
        WorkflowStarted,
        WorkflowStopping,
        WorkflowStopped,
        WorkflowRestarting,
        WorkflowRestartingFailure,
        ServiceStartupFailure,
        ServiceStopFailure,
        StepStarted,
        StepCompleted,
        StepFailed,
        Custom
    }

    Type getType();

    Object getSource();

    /**
     * Timestamp for each event, when the event occurred. By default, the timestamp is not included and this method
     * returns 0.
     */
    long getTimestamp();

    void setTimestamp(long timestamp);

    /**
     * This interface is implemented by all events that contain an exception and is used to retrieve the exception in a
     * universal way.
     */
    interface FailureEvent extends ZwangineEvent {

        Throwable getCause();

    }

    interface ZwangineContextEvent extends ZwangineEvent {

        ZwangineContext getContext();

        @Override
        default Object getSource() {
            return getContext();
        }

    }

    interface ZwangineContextInitializingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextInitializing;
        }
    }

    interface ZwangineContextInitializedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextInitialized;
        }
    }

    interface ZwangineContextResumedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextResumed;
        }
    }

    interface ZwangineContextResumeFailureEvent extends ZwangineContextEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextResumeFailure;
        }
    }

    interface ZwangineContextResumingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextResuming;
        }
    }

    interface ZwangineContextStartedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStarted;
        }
    }

    interface ZwangineContextStartingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStarting;
        }
    }

    interface ZwangineContextStartupFailureEvent extends ZwangineContextEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStartupFailure;
        }
    }

    interface ZwangineContextStopFailureEvent extends ZwangineContextEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStopFailure;
        }
    }

    interface ZwangineContextStoppedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStopped;
        }
    }

    interface ZwangineContextStoppingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextStopping;
        }
    }

    interface ZwangineContextSuspendedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextSuspended;
        }
    }

    interface ZwangineContextSuspendingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextSuspending;
        }
    }

    interface ZwangineContextWorkflowsStartingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.WorkflowsStarting;
        }
    }

    interface ZwangineContextWorkflowsStartedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.WorkflowsStarted;
        }
    }

    interface ZwangineContextWorkflowsStoppingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.WorkflowsStopping;
        }
    }

    interface ZwangineContextWorkflowsStoppedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.WorkflowsStopped;
        }
    }

    interface ZwangineContextReloadingEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextReloading;
        }
    }

    interface ZwangineContextReloadedEvent extends ZwangineContextEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextReloaded;
        }
    }

    interface ZwangineContextReloadFailureEvent extends ZwangineContextEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ZwangineContextReloadFailure;
        }
    }

    interface ExchangeEvent extends ZwangineEvent {

        Exchange getExchange();

        @Override
        default Object getSource() {
            return getExchange();
        }
    }

    interface ExchangeCompletedEvent extends ExchangeEvent {
        @Override
        default Type getType() {
            return Type.ExchangeCompleted;
        }
    }

    interface ExchangeCreatedEvent extends ExchangeEvent {
        @Override
        default Type getType() {
            return Type.ExchangeCreated;
        }
    }

    interface ExchangeFailedEvent extends ExchangeEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ExchangeFailed;
        }
    }

    interface ExchangeFailureEvent extends ExchangeEvent {

        Processor getFailureHandler();

        boolean isDeadLetterChannel();

        String getDeadLetterUri();

    }

    interface ExchangeFailureHandledEvent extends ExchangeFailureEvent {
        @Override
        default Type getType() {
            return Type.ExchangeFailureHandled;
        }
    }

    interface ExchangeFailureHandlingEvent extends ExchangeFailureEvent {
        @Override
        default Type getType() {
            return Type.ExchangeFailureHandling;
        }
    }

    interface ExchangeRedeliveryEvent extends ExchangeEvent {

        int getAttempt();

        @Override
        default Type getType() {
            return Type.ExchangeRedelivery;
        }
    }

    interface ExchangeSendingEvent extends ExchangeEvent {

        Endpoint getEndpoint();

        @Override
        default Type getType() {
            return Type.ExchangeSending;
        }
    }

    interface ExchangeSentEvent extends ExchangeEvent {

        Endpoint getEndpoint();

        long getTimeTaken();

        @Override
        default Type getType() {
            return Type.ExchangeSent;
        }
    }

    interface StepEvent extends ExchangeEvent {
        String getStepId();
    }

    interface StepStartedEvent extends StepEvent {
        @Override
        default Type getType() {
            return Type.StepStarted;
        }
    }

    interface StepCompletedEvent extends StepEvent {
        @Override
        default Type getType() {
            return Type.StepCompleted;
        }
    }

    interface StepFailedEvent extends StepEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.StepFailed;
        }
    }

    interface WorkflowEvent extends ZwangineEvent {

        Workflow getWorkflow();

        @Override
        default Object getSource() {
            return getWorkflow();
        }
    }

    interface WorkflowAddedEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowAdded;
        }
    }

    interface WorkflowRemovedEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowRemoved;
        }
    }

    interface WorkflowReloadedEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowReloaded;
        }

        /**
         * The workflow index in this batch (starts from 1)
         */
        int getIndex();

        /**
         * Total number of workflows being reloaded in this batch
         */
        int getTotal();
    }

    interface WorkflowStartingEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowStarting;
        }
    }

    interface WorkflowStartedEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowStarted;
        }
    }

    interface WorkflowStoppingEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowStopping;
        }
    }

    interface WorkflowStoppedEvent extends WorkflowEvent {
        @Override
        default Type getType() {
            return Type.WorkflowStopped;
        }
    }

    interface WorkflowRestartingEvent extends WorkflowEvent {

        /**
         * Restart attempt (0 = initial start, 1 = first restart attempt)
         */
        long getAttempt();

        @Override
        default Type getType() {
            return Type.WorkflowRestarting;
        }
    }

    interface WorkflowRestartingFailureEvent extends WorkflowEvent, FailureEvent {

        /**
         * Failure attempt (0 = initial start, 1 = first restart attempt)
         */
        long getAttempt();

        /**
         * Whether all restarts have failed and the workflow controller will not attempt to restart the workflow anymore due
         * to maximum attempts reached and being exhausted.
         */
        boolean isExhausted();

        @Override
        default Type getType() {
            return Type.WorkflowRestartingFailure;
        }
    }

    interface ServiceEvent extends ZwangineEvent {

        Object getService();

        @Override
        default Object getSource() {
            return getService();
        }
    }

    interface ServiceStartupFailureEvent extends ServiceEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ServiceStartupFailure;
        }
    }

    interface ServiceStopFailureEvent extends ServiceEvent, FailureEvent {
        @Override
        default Type getType() {
            return Type.ServiceStopFailure;
        }
    }

    /**
     * Special event only in use for zwangine-tracing / zwangine-opentelemetry. This event is NOT (by default) in use.
     */
    interface ExchangeAsyncProcessingStartedEvent extends ExchangeEvent {
        @Override
        default Type getType() {
            return Type.ExchangeAsyncProcessingStarted;
        }
    }
}
