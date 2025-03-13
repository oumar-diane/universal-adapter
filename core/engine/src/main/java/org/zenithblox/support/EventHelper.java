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
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.spi.EventFactory;
import org.zenithblox.spi.EventNotifier;
import org.zenithblox.spi.ManagementStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Helper for easily sending event notifications in a single line of code
 */
@SuppressWarnings("UnusedReturnValue")
public final class EventHelper {

    // This implementation has been optimized to be as fast and not create unnecessary objects or lambdas.
    // Therefore there is some code that seems duplicated. But this code is used frequently during routing and should
    // be left as-is.

    private static final Logger LOG = LoggerFactory.getLogger(EventHelper.class);

    private EventHelper() {
    }

    /**
     * Checks whether event notifications is applicable or not
     */
    public static boolean eventsApplicable(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }
        // is there any notifiers that would receive exchange events
        boolean exchange = false;
        for (EventNotifier en : notifiers) {
            exchange |= !en.isIgnoreExchangeEvents();
        }
        return exchange;
    }

    public static boolean notifyZwangineContextInitializing(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextInitializingEvent, true);
    }

    public static boolean notifyZwangineContextInitialized(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextInitializedEvent, true);
    }

    public static boolean notifyZwangineContextStarting(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextStartingEvent, false);
    }

    public static boolean notifyZwangineContextStarted(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextStartedEvent, false);
    }

    public static boolean notifyZwangineContextStartupFailed(ZwangineContext context, Throwable cause) {
        return notifyZwangineContext(context, (ef, ctx) -> ef.createZwangineContextStartupFailureEvent(ctx, cause), false);
    }

    public static boolean notifyZwangineContextStopping(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextStoppingEvent, false);
    }

    public static boolean notifyZwangineContextStopped(ZwangineContext context) {
        return notifyZwangineContext(context, EventFactory::createZwangineContextStoppedEvent, false);
    }

    public static boolean notifyZwangineContextStopFailed(ZwangineContext context, Throwable cause) {
        return notifyZwangineContext(context, (ef, ctx) -> ef.createZwangineContextStopFailureEvent(ctx, cause), false);
    }

    private static boolean notifyZwangineContext(
            ZwangineContext context, BiFunction<EventFactory, ZwangineContext, ZwangineEvent> eventSupplier, boolean init) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        // init zwangine context events are triggered before event notifiers is started so get those pre-started notifiers
        // so we can emit those special init events
        List<EventNotifier> notifiers = init ? management.getEventNotifiers() : management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }
            if (init && notifier.isIgnoreZwangineContextInitEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = eventSupplier.apply(factory, context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyServiceStopFailure(ZwangineContext context, Object service, Throwable cause) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreServiceEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createServiceStopFailureEvent(context, service, cause);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyServiceStartupFailure(ZwangineContext context, Object service, Throwable cause) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreServiceEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createServiceStartupFailureEvent(context, service, cause);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowStarting(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowStartingEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowRestarting(ZwangineContext context, Workflow workflow, long attempt) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowRestarting(workflow, attempt);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowRestartingFailure(
            ZwangineContext context, Workflow workflow, long attempt, Throwable cause, boolean exhausted) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowRestartingFailure(workflow, attempt, cause, exhausted);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowStarted(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowStartedEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowStopping(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowStoppingEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowStopped(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowStoppedEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowAdded(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowAddedEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowRemoved(ZwangineContext context, Workflow workflow) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowRemovedEvent(workflow);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyWorkflowReloaded(ZwangineContext context, Workflow workflow, int index, int total) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createWorkflowReloaded(workflow, index, total);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyContextReloading(ZwangineContext context, Object source) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextReloading(context, source);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyContextReloaded(ZwangineContext context, Object source) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextReloaded(context, source);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyContextReloadFailure(ZwangineContext context, Object source, Throwable cause) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreWorkflowEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextReloadFailure(context, source, cause);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeCreated(ZwangineContext context, Exchange exchange) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreExchangeEvents() || notifier.isIgnoreExchangeCreatedEvent()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeCreatedEvent(exchange);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeDone(ZwangineContext context, Exchange exchange) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeCompletedEvent()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeCompletedEvent(exchange);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeFailed(ZwangineContext context, Exchange exchange) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);

            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeFailedEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeFailedEvent(exchange);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeFailureHandling(
            ZwangineContext context, Exchange exchange, Processor failureHandler,
            boolean deadLetterChannel, String deadLetterUri) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);

            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeFailedEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeFailureHandlingEvent(exchange, failureHandler, deadLetterChannel, deadLetterUri);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeFailureHandled(
            ZwangineContext context, Exchange exchange, Processor failureHandler,
            boolean deadLetterChannel, String deadLetterUri) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeFailedEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeFailureHandledEvent(exchange, failureHandler, deadLetterChannel, deadLetterUri);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeRedelivery(ZwangineContext context, Exchange exchange, int attempt) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);

            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeFailedEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeRedeliveryEvent(exchange, attempt);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeSending(ZwangineContext context, Exchange exchange, Endpoint endpoint) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeSendingEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeSendingEvent(exchange, endpoint);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeSent(ZwangineContext context, Exchange exchange, Endpoint endpoint, long timeTaken) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeSentEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createExchangeSentEvent(exchange, endpoint, timeTaken);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextSuspending(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextSuspendingEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextSuspended(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextSuspendedEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextResuming(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextResumingEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextResumed(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextResumedEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextResumeFailed(ZwangineContext context, Throwable cause) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextResumeFailureEvent(context, cause);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextWorkflowsStarting(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextWorkflowsStartingEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextWorkflowsStarted(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextWorkflowsStartedEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextWorkflowsStopping(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextWorkflowsStoppingEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyZwangineContextWorkflowsStopped(ZwangineContext context) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreZwangineContextEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineContextWorkflowsStoppedEvent(context);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyStepStarted(ZwangineContext context, Exchange exchange, String stepId) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreStepEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createStepStartedEvent(exchange, stepId);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyStepDone(ZwangineContext context, Exchange exchange, String stepId) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreStepEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createStepCompletedEvent(exchange, stepId);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyStepFailed(ZwangineContext context, Exchange exchange, String stepId) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        for (EventNotifier notifier : notifiers) {
            if (notifier.isDisabled()) {
                continue;
            }
            if (notifier.isIgnoreStepEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createStepFailedEvent(exchange, stepId);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    public static boolean notifyExchangeAsyncProcessingStartedEvent(ZwangineContext context, Exchange exchange) {
        ManagementStrategy management = context.getManagementStrategy();
        if (management == null) {
            return false;
        }

        EventFactory factory = management.getEventFactory();
        if (factory == null) {
            return false;
        }

        List<EventNotifier> notifiers = management.getStartedEventNotifiers();
        if (notifiers == null || notifiers.isEmpty()) {
            return false;
        }

        if (exchange.getExchangeExtension().isNotifyEvent()) {
            // do not generate events for an notify event
            return false;
        }

        boolean answer = false;
        ZwangineEvent event = null;
        // optimise for loop using index access to avoid creating iterator object
        for (int i = 0; i < notifiers.size(); i++) {
            EventNotifier notifier = notifiers.get(i);
            if (isDisabledOrIgnored(notifier) || notifier.isIgnoreExchangeAsyncProcessingStartedEvents()) {
                continue;
            }

            if (event == null) {
                // only create event once
                event = factory.createZwangineExchangeAsyncProcessingStartedEvent(exchange);
                if (event == null) {
                    // factory could not create event so exit
                    return false;
                }
            }
            answer |= doNotifyEvent(notifier, event);
        }
        return answer;
    }

    private static boolean isDisabledOrIgnored(EventNotifier notifier) {
        return notifier.isDisabled() || notifier.isIgnoreExchangeEvents();
    }

    private static boolean doNotifyEvent(EventNotifier notifier, ZwangineEvent event) {
        if (!notifier.isEnabled(event)) {
            return false;
        }

        try {
            notifier.notify(event);
        } catch (Throwable e) {
            LOG.warn("Error notifying event {}. This exception will be ignored.", event, e);
        }

        return true;
    }
}
