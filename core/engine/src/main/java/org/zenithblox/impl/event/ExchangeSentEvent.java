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
import org.zenithblox.util.TimeUtils;

import java.io.Serial;

/**
 * Event for <b>after</b> an {@link Exchange} has been sent to an {@link Endpoint}. The {@link ExchangeSentEvent} is an
 * event which is emitted <b>after</b> the sending is done.
 * <p/>
 * These two events (sending and sent) come in a pair, and therefore you need to make sure to return <tt>true</tt> for
 * both events in the {@link org.zenithblox.spi.EventNotifier#isEnabled(ZwangineEvent)} method to receive events for
 * either of them.
 *
 * @see ExchangeSendingEvent
 */
public class ExchangeSentEvent extends AbstractExchangeEvent implements ZwangineEvent.ExchangeSentEvent {
    private static final @Serial long serialVersionUID = -19248832613958123L;

    private final Endpoint endpoint;
    private final long timeTaken;

    public ExchangeSentEvent(Exchange source, Endpoint endpoint, long timeTaken) {
        super(source);
        this.endpoint = endpoint;
        this.timeTaken = timeTaken;
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public long getTimeTaken() {
        return timeTaken;
    }

    @Override
    public final String toString() {
        return getExchange().getExchangeId() + " exchange sent to: " + endpoint
               + " took: " + TimeUtils.printDuration(timeTaken, true);
    }

}
