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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Exchange;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.spi.EventNotifier;

/**
 * Base class to extend for custom {@link EventNotifier} implementations.
 *
 * This event notifier will also receive notifications about {@link Exchange} events. If there is <b>no</b> need to
 * receive such events then extend {@link SimpleEventNotifierSupport} instead.
 *
 * @see SimpleEventNotifierSupport
 */
public abstract class EventNotifierSupport extends SimpleEventNotifierSupport implements ZwangineContextAware {

    private ZwangineContext zwangineContext;

    public EventNotifierSupport() {
        setupIgnore(false);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public boolean isIgnoreZwangineContextEvents() {
        return ignoreZwangineContextEvents;
    }

    @Override
    public boolean isEnabled(ZwangineEvent event) {
        return true;
    }

    @Override
    public boolean isDisabled() {
        return false;
    }

    @Override
    public void setIgnoreZwangineContextEvents(boolean ignoreZwangineContextEvents) {
        this.ignoreZwangineContextEvents = ignoreZwangineContextEvents;
    }

    @Override
    public boolean isIgnoreWorkflowEvents() {
        return ignoreWorkflowEvents;
    }

    @Override
    public void setIgnoreWorkflowEvents(boolean ignoreWorkflowEvents) {
        this.ignoreWorkflowEvents = ignoreWorkflowEvents;
    }

    @Override
    public boolean isIgnoreServiceEvents() {
        return ignoreServiceEvents;
    }

    @Override
    public void setIgnoreServiceEvents(boolean ignoreServiceEvents) {
        this.ignoreServiceEvents = ignoreServiceEvents;
    }

    @Override
    public boolean isIgnoreExchangeEvents() {
        return ignoreExchangeEvents;
    }

    @Override
    public void setIgnoreExchangeEvents(boolean ignoreExchangeEvents) {
        this.ignoreExchangeEvents = ignoreExchangeEvents;
    }

    @Override
    public boolean isIgnoreExchangeCreatedEvent() {
        return ignoreExchangeCreatedEvent;
    }

    @Override
    public void setIgnoreExchangeCreatedEvent(boolean ignoreExchangeCreatedEvent) {
        this.ignoreExchangeCreatedEvent = ignoreExchangeCreatedEvent;
    }

    @Override
    public boolean isIgnoreExchangeCompletedEvent() {
        return ignoreExchangeCompletedEvent;
    }

    @Override
    public void setIgnoreExchangeCompletedEvent(boolean ignoreExchangeCompletedEvent) {
        this.ignoreExchangeCompletedEvent = ignoreExchangeCompletedEvent;
    }

    @Override
    public boolean isIgnoreExchangeFailedEvents() {
        return ignoreExchangeFailedEvents;
    }

    @Override
    public void setIgnoreExchangeFailedEvents(boolean ignoreExchangeFailedEvents) {
        this.ignoreExchangeFailedEvents = ignoreExchangeFailedEvents;
    }

    @Override
    public boolean isIgnoreExchangeRedeliveryEvents() {
        return ignoreExchangeRedeliveryEvents;
    }

    @Override
    public void setIgnoreExchangeRedeliveryEvents(boolean ignoreExchangeRedeliveryEvents) {
        this.ignoreExchangeRedeliveryEvents = ignoreExchangeRedeliveryEvents;
    }

    @Override
    public boolean isIgnoreExchangeSentEvents() {
        return ignoreExchangeSentEvents;
    }

    @Override
    public void setIgnoreExchangeSentEvents(boolean ignoreExchangeSentEvents) {
        this.ignoreExchangeSentEvents = ignoreExchangeSentEvents;
    }

    @Override
    public boolean isIgnoreExchangeSendingEvents() {
        return ignoreExchangeSendingEvents;
    }

    @Override
    public void setIgnoreExchangeSendingEvents(boolean ignoreExchangeSendingEvents) {
        this.ignoreExchangeSendingEvents = ignoreExchangeSendingEvents;
    }

    @Override
    public boolean isIgnoreStepEvents() {
        return ignoreStepEvents;
    }

    @Override
    public void setIgnoreStepEvents(boolean ignoreStepEvents) {
        this.ignoreStepEvents = ignoreStepEvents;
    }
}
