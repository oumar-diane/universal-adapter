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

import org.zenithblox.Exchange;
import org.zenithblox.Workflow;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.support.WorkflowPolicySupport;

import java.io.Serial;

/**
 * Notifies that async processing has started. It's guaranteed to run on the same thread on which
 * {@link WorkflowPolicySupport#onExchangeBegin(Workflow, Exchange)} was called and/or {@link ExchangeSendingEvent} was fired.
 *
 * Special event only in use for zwangine-tracing / zwangine-opentelemetry. This event is NOT (by default) in use.
 *
 * @see ExchangeAsyncProcessingStartedEvent
 */
public class ExchangeAsyncProcessingStartedEvent extends AbstractExchangeEvent
        implements ZwangineEvent.ExchangeAsyncProcessingStartedEvent {
    private static final @Serial long serialVersionUID = -19248832613958122L;

    public ExchangeAsyncProcessingStartedEvent(Exchange source) {
        super(source);
    }

    @Override
    public final String toString() {
        return getExchange().getExchangeId();
    }
}
