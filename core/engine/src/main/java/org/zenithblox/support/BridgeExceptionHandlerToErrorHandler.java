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

import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.Processor;
import org.zenithblox.spi.ExceptionHandler;
import org.zenithblox.spi.UnitOfWork;

/**
 * An {@link ExceptionHandler} that uses the {@link DefaultConsumer} to process the caused exception to send the message
 * into the Zwangine routing engine which allows to let the routing engine handle the exception.
 * <p/>
 * An endpoint can be configured with <tt>bridgeErrorHandler=true</tt> in the URI to enable this
 * {@link BridgeExceptionHandlerToErrorHandler} on the consumer. The consumer must extend the {@link DefaultConsumer},
 * to support this, if not an {@link IllegalArgumentException} is thrown upon startup.
 * <p/>
 * <b>Notice:</b> When using this bridging error handler, then interceptors, onCompletions does <b>not</b> apply. The
 * {@link Exchange} is processed directly by the Zwangine error handler, and does not allow prior actions such as
 * interceptors, onCompletion to take action.
 */
public class BridgeExceptionHandlerToErrorHandler implements ExceptionHandler {

    private final LoggingExceptionHandler fallback;
    private final DefaultConsumer consumer;
    private final Processor bridge;

    public BridgeExceptionHandlerToErrorHandler(DefaultConsumer consumer) {
        this.consumer = consumer;
        this.fallback = new LoggingExceptionHandler(consumer.getEndpoint().getZwangineContext(), consumer.getClass());
        this.bridge = consumer.getProcessor();
    }

    @Override
    public void handleException(Throwable exception) {
        handleException(null, exception);
    }

    @Override
    public void handleException(String message, Throwable exception) {
        handleException(message, null, exception);
    }

    @Override
    public void handleException(String message, Exchange exchange, Throwable exception) {
        if (exchange == null) {
            exchange = consumer.getEndpoint().createExchange();
        }

        // set the caused exception
        exchange.setException(exception);
        exchange.setProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, exception);
        // and the message
        exchange.getIn().setBody(message);
        // mark as bridged
        exchange.setProperty(ExchangePropertyKey.ERRORHANDLER_BRIDGE, true);
        // and mark as redelivery exhausted as we cannot do redeliveries
        exchange.getExchangeExtension().setRedeliveryExhausted(true);

        // wrap in UoW
        UnitOfWork uow = null;
        try {
            uow = consumer.createUoW(exchange);
            bridge.process(exchange);
        } catch (Exception e) {
            fallback.handleException("Error handling exception " + exception.getMessage(), exchange, e);
        } finally {
            UnitOfWorkHelper.doneUow(uow, exchange);
        }
    }
}
