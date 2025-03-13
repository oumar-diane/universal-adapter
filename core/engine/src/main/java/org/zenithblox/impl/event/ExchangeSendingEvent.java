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

import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.spi.ZwangineEvent;

import java.io.Serial;

/**
 * Event for <b>before</b> sending an {@link Exchange} to an {@link Endpoint}.
 * <p/>
 * This event is emitted before attempting to send the {@link Exchange} to an {@link Endpoint}. There is still some
 * internal processing done before the actual sending takes places, and therefore it is not 100% guaranteed that the
 * sending actually happens, as an internal error may occur.
 * <p/>
 * The {@link ExchangeSentEvent} is an event which is emitted <b>after</b> the sending is done.
 * <p/>
 * These two events (sending and sent) come in a pair, and therefore you need to make sure to return <tt>true</tt> for
 * both events in the {@link org.zenithblox.spi.EventNotifier#isEnabled(ZwangineEvent)} method to receive events for
 * either of them.
 *
 * @see ExchangeSentEvent
 */
public class ExchangeSendingEvent extends AbstractExchangeEvent implements ZwangineEvent.ExchangeSendingEvent {
    private static final @Serial long serialVersionUID = -19248832613958122L;

    private final Endpoint endpoint;

    public ExchangeSendingEvent(Exchange source, Endpoint endpoint) {
        super(source);
        this.endpoint = endpoint;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public final String toString() {
        return getExchange().getExchangeId() + " exchange sending to: " + endpoint;
    }

}
