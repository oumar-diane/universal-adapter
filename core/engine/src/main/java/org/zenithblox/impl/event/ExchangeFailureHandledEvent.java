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
import org.zenithblox.Processor;
import org.zenithblox.spi.ZwangineEvent;
import org.zenithblox.util.URISupport;

import java.io.Serial;

public class ExchangeFailureHandledEvent extends AbstractExchangeEvent implements ZwangineEvent.ExchangeFailureHandledEvent {
    private static final @Serial long serialVersionUID = -7554809462006009548L;

    private final transient Processor failureHandler;
    private final boolean deadLetterChannel;
    private final String deadLetterUri;
    private final boolean handled;

    public ExchangeFailureHandledEvent(Exchange source, Processor failureHandler, boolean deadLetterChannel,
                                       String deadLetterUri) {
        super(source);
        this.failureHandler = failureHandler;
        this.deadLetterChannel = deadLetterChannel;
        this.deadLetterUri = deadLetterUri;
        this.handled = source.getExchangeExtension().isErrorHandlerHandledSet()
                && source.getExchangeExtension().isErrorHandlerHandled();
    }

    @Override
    public Processor getFailureHandler() {
        return failureHandler;
    }

    @Override
    public boolean isDeadLetterChannel() {
        return deadLetterChannel;
    }

    @Override
    public String getDeadLetterUri() {
        return deadLetterUri;
    }

    public boolean isHandled() {
        return handled;
    }

    public boolean isContinued() {
        return !handled;
    }

    @Override
    public final String toString() {
        if (isDeadLetterChannel()) {
            String uri = URISupport.sanitizeUri(deadLetterUri);
            return getExchange().getExchangeId() + " exchange failed"
                   + " and sent to dead letter channel: " + uri;
        } else {
            return getExchange().getExchangeId() + " exchange failed"
                   + " and sent to processor: " + failureHandler;
        }
    }
}
