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

/**
 * Notifier to send {@link java.util.EventObject events}.
 *
 * @see org.zenithblox.spi.EventFactory
 */
public interface EventNotifier {

    /**
     * Notifies the given event
     *
     * @param  event     the event
     * @throws Exception can be thrown if notification failed
     */
    void notify(ZwangineEvent event) throws Exception;

    /**
     * Checks whether notification for the given event is enabled.
     * <p/>
     * If disabled the event will not be sent and silently ignored instead.
     *
     * @param  event the event
     * @return       <tt>true</tt> if the event should be sent, <tt>false</tt> to silently ignore it
     */
    boolean isEnabled(ZwangineEvent event);

    /**
     * Checks whether notification is disabled for all events
     *
     * @return <tt>true</tt> if disabled and no events is being notified.
     */
    boolean isDisabled();

    boolean isIgnoreZwangineContextInitEvents();

    void setIgnoreZwangineContextInitEvents(boolean ignoreZwangineContextInitEvents);

    boolean isIgnoreZwangineContextEvents();

    void setIgnoreZwangineContextEvents(boolean ignoreZwangineContextEvents);

    boolean isIgnoreWorkflowEvents();

    void setIgnoreWorkflowEvents(boolean ignoreWorkflowEvents);

    boolean isIgnoreServiceEvents();

    void setIgnoreServiceEvents(boolean ignoreServiceEvents);

    boolean isIgnoreExchangeEvents();

    void setIgnoreExchangeEvents(boolean ignoreExchangeEvents);

    boolean isIgnoreExchangeCreatedEvent();

    void setIgnoreExchangeCreatedEvent(boolean ignoreExchangeCreatedEvent);

    boolean isIgnoreExchangeCompletedEvent();

    void setIgnoreExchangeCompletedEvent(boolean ignoreExchangeCompletedEvent);

    boolean isIgnoreExchangeFailedEvents();

    void setIgnoreExchangeFailedEvents(boolean ignoreExchangeFailureEvents);

    boolean isIgnoreExchangeRedeliveryEvents();

    void setIgnoreExchangeRedeliveryEvents(boolean ignoreExchangeRedeliveryEvents);

    boolean isIgnoreExchangeSentEvents();

    void setIgnoreExchangeSentEvents(boolean ignoreExchangeSentEvents);

    boolean isIgnoreExchangeSendingEvents();

    void setIgnoreExchangeSendingEvents(boolean ignoreExchangeSendingEvents);

    boolean isIgnoreStepEvents();

    void setIgnoreStepEvents(boolean ignoreStepEvents);

    void setIgnoreExchangeAsyncProcessingStartedEvents(boolean ignoreExchangeAsyncProcessingStartedEvents);

    boolean isIgnoreExchangeAsyncProcessingStartedEvents();
}
