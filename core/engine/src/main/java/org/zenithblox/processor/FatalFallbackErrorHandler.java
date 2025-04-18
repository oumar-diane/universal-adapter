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
package org.zenithblox.processor;

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.Processor;
import org.zenithblox.spi.ErrorHandler;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.zenithblox.support.builder.ExpressionBuilder.workflowIdExpression;

/**
 * An {@link ErrorHandler} used as a safe fallback when processing by other error handlers such as the
 * {@link org.zenithblox.model.OnExceptionDefinition}.
 * <p/>
 * This error handler is used as a fail-safe to ensure that error handling does not run in endless recursive looping
 * which potentially can happen if a new exception is thrown while error handling a previous exception which then cause
 * new error handling to process and this then keep on failing with new exceptions in an endless loop.
 */
public class FatalFallbackErrorHandler extends DelegateAsyncProcessor implements ErrorHandler {

    private static final Logger LOG = LoggerFactory.getLogger(FatalFallbackErrorHandler.class);

    private final boolean deadLetterChannel;

    public FatalFallbackErrorHandler(Processor processor) {
        this(processor, false);
    }

    public FatalFallbackErrorHandler(Processor processor, boolean isDeadLetterChannel) {
        super(processor);
        this.deadLetterChannel = isDeadLetterChannel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        // get the current workflow id we use
        final String id = workflowIdExpression().evaluate(exchange, String.class);

        // prevent endless looping if we end up coming back to ourself
        Deque<String> fatals = exchange.getProperty(ExchangePropertyKey.FATAL_FALLBACK_ERROR_HANDLER, Deque.class);
        if (fatals == null) {
            fatals = new ArrayDeque<>();
            exchange.setProperty(ExchangePropertyKey.FATAL_FALLBACK_ERROR_HANDLER, fatals);
        }
        if (fatals.contains(id)) {
            LOG.warn("Circular error-handler detected at workflow: {} - breaking out processing Exchange: {}", id, exchange);
            // mark this exchange as already been error handler handled (just by having this property)
            // the false value mean the caught exception will be kept on the exchange, causing the
            // exception to be propagated back to the caller, and to break out routing
            exchange.getExchangeExtension().setErrorHandlerHandled(false);
            exchange.setProperty(ExchangePropertyKey.ERRORHANDLER_CIRCUIT_DETECTED, true);
            callback.done(true);
            return true;
        }

        // okay we run under this fatal error handler now
        fatals.push(id);

        // support the asynchronous routing engine
        boolean sync = processor.process(exchange, new AsyncCallback() {
            public void done(boolean doneSync) {
                try {
                    if (exchange.getException() != null) {
                        // an exception occurred during processing onException

                        // log detailed error message with as much detail as possible
                        Throwable previous = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Throwable.class);

                        // check if previous and this exception are set as the same exception
                        // which happens when using global scoped onException and you call a direct workflow that causes the 2nd exception
                        // then we need to find the original previous exception as the suppressed exception
                        if (previous != null && previous == exchange.getException()) {
                            previous = null;
                            // maybe previous was suppressed?
                            if (exchange.getException().getSuppressed().length > 0) {
                                previous = exchange.getException().getSuppressed()[0];
                            }
                        }

                        String msg = "Exception occurred while trying to handle previously thrown exception on exchangeId: "
                                     + exchange.getExchangeId() + " using: [" + processor + "].";
                        if (previous != null) {
                            msg += " The previous and the new exception will be logged in the following.";
                            log(msg);
                            log("\\--> Previous exception on exchangeId: " + exchange.getExchangeId(), previous);
                            log("\\--> New exception on exchangeId: " + exchange.getExchangeId(), exchange.getException());
                        } else {
                            log(msg);
                            log("\\--> New exception on exchangeId: " + exchange.getExchangeId(), exchange.getException());
                        }

                        // add previous as suppressed to exception if not already there
                        if (previous != null) {
                            Throwable[] suppressed = exchange.getException().getSuppressed();
                            boolean found = false;
                            for (Throwable t : suppressed) {
                                if (t == previous) {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found) {
                                exchange.getException().addSuppressed(previous);
                            }
                        }

                        // we can propagated that exception to the caught property on the exchange
                        // which will shadow any previously caught exception and cause this new exception
                        // to be visible in the error handler
                        exchange.setProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, exchange.getException());

                        if (deadLetterChannel) {
                            // special for dead letter channel as we want to let it determine what to do, depending how
                            // it has been configured
                            exchange.getExchangeExtension().setErrorHandlerHandled(null);
                        } else {
                            // mark this exchange as already been error handler handled (just by having this property)
                            // the false value mean the caught exception will be kept on the exchange, causing the
                            // exception to be propagated back to the caller, and to break out routing
                            exchange.getExchangeExtension().setErrorHandlerHandled(false);
                        }
                    }
                } finally {
                    // no longer running under this fatal fallback error handler
                    Deque<String> fatals = exchange.getProperty(ExchangePropertyKey.FATAL_FALLBACK_ERROR_HANDLER, Deque.class);
                    if (fatals != null) {
                        fatals.removeLastOccurrence(id);
                    }
                    callback.done(doneSync);
                }
            }
        });

        return sync;
    }

    private void log(String message) {
        log(message, null);
    }

    private void log(String message, Throwable t) {
        // when using dead letter channel we only want to log at WARN level
        if (deadLetterChannel) {
            if (t != null) {
                LOG.warn(message, t);
            } else {
                LOG.warn(message);
            }
        } else {
            if (t != null) {
                LOG.error(message, t);
            } else {
                LOG.error(message);
            }
        }
    }

    @Override
    public String toString() {
        return "FatalFallbackErrorHandler[" + processor + "]";
    }
}
