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

import org.zenithblox.*;
import org.zenithblox.spi.EndpointUtilizationStatistics;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.ProducerCache;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.MessageHelper;
import org.zenithblox.support.ObjectHelper;
import org.zenithblox.support.builder.ExpressionBuilder;
import org.zenithblox.support.cache.DefaultProducerCache;
import org.zenithblox.support.cache.EmptyProducerCache;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

import static org.zenithblox.processor.WorkflowHelper.continueProcessing;
import static org.zenithblox.util.ObjectHelper.notNull;

/**
 * Implements a <a href="http://zwangine.zwangine.org/routing-slip.html">Routing Slip</a> pattern where the list of actual
 * endpoints to send a message exchange to are dependent on the value of a message header.
 * <p/>
 * This implementation mirrors the logic from the {@link Pipeline} in the async variation as
 * the failover load balancer is a specialized workflow. So the trick is to keep doing the same as the workflow to
 * ensure it works the same and the async routing engine is flawless.
 */
public class RoutingSlip extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(RoutingSlip.class);

    protected String id;
    protected String workflowId;
    protected ProducerCache producerCache;
    protected int cacheSize;
    protected boolean ignoreInvalidEndpoints;
    protected String header;
    protected Expression expression;
    protected String uriDelimiter;
    protected final ZwangineContext zwangineContext;
    protected AsyncProcessor errorHandler;

    /**
     * The iterator to be used for retrieving the next routing slip(s) to be used.
     */
    protected interface RoutingSlipIterator {

        /**
         * Are the more routing slip(s)?
         *
         * @param  exchange the current exchange
         * @return          <tt>true</tt> if more slips, <tt>false</tt> otherwise.
         */
        boolean hasNext(Exchange exchange);

        /**
         * Returns the next routing slip(s).
         *
         * @param  exchange the current exchange
         * @return          the slip(s).
         */
        Object next(Exchange exchange);

    }

    public RoutingSlip(ZwangineContext zwangineContext) {
        notNull(zwangineContext, "zwangineContext");
        this.zwangineContext = zwangineContext;
    }

    public RoutingSlip(ZwangineContext zwangineContext, Expression expression, String uriDelimiter) {
        notNull(zwangineContext, "zwangineContext");
        notNull(expression, "expression");

        this.zwangineContext = zwangineContext;
        this.expression = expression;
        this.uriDelimiter = uriDelimiter;
        this.header = null;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public Expression getExpression() {
        return expression;
    }

    public String getUriDelimiter() {
        return uriDelimiter;
    }

    public void setDelimiter(String delimiter) {
        this.uriDelimiter = delimiter;
    }

    public boolean isIgnoreInvalidEndpoints() {
        return ignoreInvalidEndpoints;
    }

    public void setIgnoreInvalidEndpoints(boolean ignoreInvalidEndpoints) {
        this.ignoreInvalidEndpoints = ignoreInvalidEndpoints;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public AsyncProcessor getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(AsyncProcessor errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "routingSlip[" + expression + "]";
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (!isStarted()) {
            exchange.setException(new IllegalStateException("RoutingSlip has not been started: " + this));
            callback.done(true);
            return true;
        }

        Expression exp = expression;
        Object slip = exchange.removeProperty(ExchangePropertyKey.EVALUATE_EXPRESSION_RESULT);
        if (slip != null) {
            if (slip instanceof Expression expression) {
                exp = expression;
            } else {
                exp = ExpressionBuilder.constantExpression(slip);
            }
        }

        return doRoutingSlipWithExpression(exchange, exp, callback);
    }

    /**
     * Creates the workflow slip iterator to be used.
     *
     * @param  exchange   the exchange
     * @param  expression the expression
     * @return            the iterator, should never be <tt>null</tt>
     */
    protected RoutingSlipIterator createRoutingSlipIterator(final Exchange exchange, final Expression expression)
            throws Exception {
        Object slip = expression.evaluate(exchange, Object.class);
        if (exchange.getException() != null) {
            // force any exceptions occurred during evaluation to be thrown
            throw exchange.getException();
        }

        final Iterator<?> delegate = ObjectHelper.createIterator(slip, uriDelimiter);

        return new RoutingSlipIterator() {
            public boolean hasNext(Exchange exchange) {
                return delegate.hasNext();
            }

            public Object next(Exchange exchange) {
                return delegate.next();
            }
        };
    }

    private boolean doRoutingSlipWithExpression(
            final Exchange exchange, final Expression expression, final AsyncCallback originalCallback) {
        Exchange current = exchange;
        RoutingSlipIterator iter;
        try {
            iter = createRoutingSlipIterator(exchange, expression);
        } catch (Exception e) {
            exchange.setException(e);
            originalCallback.done(true);
            return true;
        }

        // ensure the slip is empty when we start
        current.removeProperty(ExchangePropertyKey.SLIP_ENDPOINT);

        while (iter.hasNext(current)) {

            boolean prototype = cacheSize < 0;
            Endpoint endpoint;
            try {
                Object recipient = iter.next(exchange);
                recipient = prepareRecipient(exchange, recipient);
                Endpoint existing = getExistingEndpoint(exchange, recipient);
                if (existing == null) {
                    endpoint = resolveEndpoint(exchange, recipient, prototype);
                } else {
                    endpoint = existing;
                    // we have an existing endpoint then its not a prototype scope
                    prototype = false;
                }
                // if no endpoint was resolved then try the next
                if (endpoint == null) {
                    continue;
                }
            } catch (Exception e) {
                // error resolving endpoint so we should break out
                current.setException(e);
                break;
            }

            //process and prepare the routing slip
            boolean sync = processExchange(endpoint, current, exchange, originalCallback, iter, prototype);
            current = prepareExchangeForRoutingSlip(current, endpoint);

            if (!sync) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Processing exchangeId: {} is continued being processed asynchronously",
                            exchange.getExchangeId());
                }
                // the remainder of the routing slip will be completed async
                // so we break out now, then the callback will be invoked which then continue routing from where we left here
                return false;
            }

            if (LOG.isTraceEnabled()) {
                LOG.trace("Processing exchangeId: {} is continued being processed synchronously", exchange.getExchangeId());
            }

            // we ignore some kind of exceptions and allow us to continue
            if (isIgnoreInvalidEndpoints()) {
                FailedToCreateProducerException e = current.getException(FailedToCreateProducerException.class);
                if (e != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Endpoint uri is invalid: {}. This exception will be ignored.", endpoint, e);
                    }
                    current.setException(null);
                }
            }

            // Decide whether to continue with the recipients or not; similar logic to the Workflow
            // check for error if so we should break out
            if (!continueProcessing(current, "so breaking out of the routing slip", LOG)) {
                break;
            }
        }

        // logging nextExchange as it contains the exchange that might have altered the payload and since
        // we are logging the completion if will be confusing if we log the original instead
        // we could also consider logging the original and the nextExchange then we have *before* and *after* snapshots
        if (LOG.isTraceEnabled()) {
            LOG.trace("Processing complete for exchangeId: {} >>> {}", exchange.getExchangeId(), current);
        }

        // copy results back to the original exchange
        ExchangeHelper.copyResults(exchange, current);

        // okay we are completely done with the routing slip
        // so we need to signal done on the original callback so it can continue
        originalCallback.done(true);
        return true;
    }

    protected static Object prepareRecipient(Exchange exchange, Object recipient) throws NoTypeConversionAvailableException {
        return ProcessorHelper.prepareRecipient(exchange, recipient);
    }

    protected static Endpoint getExistingEndpoint(Exchange exchange, Object recipient) {
        return ProcessorHelper.getExistingEndpoint(exchange, recipient);
    }

    protected Endpoint resolveEndpoint(Exchange exchange, Object recipient, boolean prototype) throws Exception {
        Endpoint endpoint = null;
        try {
            endpoint = prototype
                    ? ExchangeHelper.resolvePrototypeEndpoint(exchange, recipient)
                    : ExchangeHelper.resolveEndpoint(exchange, recipient);
        } catch (Exception e) {
            if (isIgnoreInvalidEndpoints()) {
                LOG.debug("Endpoint uri is invalid: {}. This exception will be ignored.", recipient, e);
            } else {
                throw e;
            }
        }
        return endpoint;
    }

    protected Exchange prepareExchangeForRoutingSlip(Exchange current, Endpoint endpoint) {
        // we must use the same id as this is a snapshot strategy where Zwangine copies a snapshot
        // before processing the next step in the workflow, so we have a snapshot of the exchange
        // just before. This snapshot is used if Zwangine should do redeliveries (re try) using
        // DeadLetterChannel. That is why it's important the id is the same, as it is the *same*
        // exchange being workflowd.
        Exchange copy = ExchangeHelper.createCopy(current, true);

        // prepare for next run
        ExchangeHelper.prepareOutToIn(copy);

        // ensure stream caching is reset
        MessageHelper.resetStreamCache(copy.getIn());

        return copy;
    }

    protected AsyncProcessor createErrorHandler(Workflow workflow, Exchange exchange, AsyncProcessor processor, Endpoint endpoint) {
        AsyncProcessor answer = processor;

        boolean tryBlock = exchange.getProperty(ExchangePropertyKey.TRY_ROUTE_BLOCK, boolean.class);

        // do not wrap in error handler if we are inside a try block
        if (!tryBlock && workflow != null && errorHandler != null) {
            // wrap the producer in error handler so we have fine grained error handling on
            // the output side instead of the input side
            // this is needed to support redelivery on that output alone and not doing redelivery
            // for the entire routingslip/dynamic-workflowr block again which will start from scratch again
            answer = errorHandler;
        }

        return answer;
    }

    protected boolean processExchange(
            final Endpoint endpoint, final Exchange exchange, final Exchange original,
            final AsyncCallback originalCallback, final RoutingSlipIterator iter, final boolean prototype) {

        // this does the actual processing so log at trace level
        if (LOG.isTraceEnabled()) {
            LOG.trace("Processing exchangeId: {} >>> {}", exchange.getExchangeId(), exchange);
        }

        // routing slip callback which are used when
        // - routing slip was workflowd asynchronously
        // - and we are completely done with the routing slip
        // so we need to signal done on the original callback so it can continue
        AsyncCallback callback = doneSync -> {
            if (!doneSync) {
                originalCallback.done(false);
            }
        };
        return producerCache.doInAsyncProducer(endpoint, exchange, callback, (p, ex, cb) -> {

            // rework error handling to support fine grained error handling
            Workflow workflow = ExchangeHelper.getWorkflow(ex);
            AsyncProcessor target = createErrorHandler(workflow, ex, p, endpoint);

            // set property which endpoint we send to and the producer that can do it
            ex.setProperty(ExchangePropertyKey.TO_ENDPOINT, endpoint.getEndpointUri());
            ex.setProperty(ExchangePropertyKey.SLIP_ENDPOINT, endpoint.getEndpointUri());
            // routing slip needs to have access to the producer
            ex.setProperty(ExchangePropertyKey.SLIP_PRODUCER, p);

            return target.process(ex, new AsyncCallback() {
                public void done(boolean doneSync) {
                    // cleanup producer after usage
                    ex.removeProperty(ExchangePropertyKey.SLIP_PRODUCER);

                    // we only have to handle async completion of the routing slip
                    if (doneSync) {
                        // and stop prototype endpoints
                        if (prototype) {
                            ServiceHelper.stopAndShutdownService(endpoint);
                        }
                        cb.done(true);
                        return;
                    }

                    try {
                        // continue processing the routing slip asynchronously
                        Exchange current = prepareExchangeForRoutingSlip(ex, endpoint);

                        while (iter.hasNext(current)) {

                            // we ignore some kind of exceptions and allow us to continue
                            if (isIgnoreInvalidEndpoints()) {
                                FailedToCreateProducerException e = current.getException(FailedToCreateProducerException.class);
                                if (e != null) {
                                    if (LOG.isDebugEnabled()) {
                                        LOG.debug("Endpoint uri is invalid: {}. This exception will be ignored.", endpoint,
                                                e);
                                    }
                                    current.setException(null);
                                }
                            }

                            // Decide whether to continue with the recipients or not; similar logic to the Workflow
                            // check for error if so we should break out
                            if (!continueProcessing(current, "so breaking out of the routing slip", LOG)) {
                                break;
                            }

                            Endpoint nextEndpoint;
                            boolean prototype = cacheSize < 0;
                            try {
                                Object recipient = iter.next(ex);
                                recipient = prepareRecipient(exchange, recipient);
                                Endpoint existing = getExistingEndpoint(exchange, recipient);
                                if (existing == null) {
                                    nextEndpoint = resolveEndpoint(exchange, recipient, prototype);
                                } else {
                                    nextEndpoint = existing;
                                    // we have an existing endpoint then its not a prototype scope
                                    prototype = false;
                                }
                                // if no endpoint was resolved then try the next
                                if (nextEndpoint == null) {
                                    continue;
                                }
                            } catch (Exception e) {
                                // error resolving endpoint so we should break out
                                current.setException(e);
                                break;
                            }

                            // prepare and process the routing slip
                            final AsyncCallback cbNext = getNextCallback(prototype, nextEndpoint, cb);
                            boolean sync = processExchange(nextEndpoint, current, original, cbNext, iter, prototype);
                            current = prepareExchangeForRoutingSlip(current, nextEndpoint);

                            if (!sync) {
                                if (LOG.isTraceEnabled()) {
                                    LOG.trace("Processing exchangeId: {} is continued being processed asynchronously",
                                            original.getExchangeId());
                                }
                                return;
                            }
                        }

                        // logging nextExchange as it contains the exchange that might have altered the payload and since
                        // we are logging the completion if will be confusing if we log the original instead
                        // we could also consider logging the original and the nextExchange then we have *before* and *after* snapshots
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Processing complete for exchangeId: {} >>> {}", original.getExchangeId(), current);
                        }

                        // copy results back to the original exchange
                        ExchangeHelper.copyResults(original, current);
                    } catch (Exception e) {
                        ex.setException(e);
                    }

                    // okay we are completely done with the routing slip
                    // so we need to signal done on the original callback so it can continue
                    cb.done(false);
                }
            });
        });
    }

    private static AsyncCallback getNextCallback(boolean prototype, Endpoint nextEndpoint, AsyncCallback cb) {
        final boolean prototypeEndpoint = prototype;
        AsyncCallback cbNext = doneNext -> {
            // and stop prototype endpoints
            if (prototypeEndpoint) {
                ServiceHelper.stopAndShutdownService(nextEndpoint);
            }
            cb.done(doneNext);
        };
        return cbNext;
    }

    @Override
    protected void doStart() throws Exception {
        if (producerCache == null) {
            if (cacheSize < 0) {
                producerCache = new EmptyProducerCache(this, zwangineContext);
                LOG.debug("RoutingSlip {} is not using ProducerCache", this);
            } else {
                producerCache = new DefaultProducerCache(this, zwangineContext, cacheSize);
                LOG.debug("RoutingSlip {} using ProducerCache with cacheSize={}", this, cacheSize);
            }
        }

        ServiceHelper.startService(producerCache, errorHandler);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(producerCache, errorHandler);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(producerCache, errorHandler);
    }

    public EndpointUtilizationStatistics getEndpointUtilizationStatistics() {
        return producerCache.getEndpointUtilizationStatistics();
    }

    /**
     * Creates the embedded processor to use when wrapping this routing slip in an error handler.
     */
    public AsyncProcessor newRoutingSlipProcessorForErrorHandler() {
        return new RoutingSlipProcessor();
    }

    /**
     * Embedded processor that workflows to the routing slip that has been set via the exchange property
     * {@link Exchange#SLIP_PRODUCER}.
     */
    private static final class RoutingSlipProcessor extends AsyncProcessorSupport {

        @Override
        public boolean process(Exchange exchange, AsyncCallback callback) {
            AsyncProcessor producer = exchange.getProperty(ExchangePropertyKey.SLIP_PRODUCER, AsyncProcessor.class);
            return producer.process(exchange, callback);
        }

        @Override
        public String toString() {
            return "RoutingSlipProcessor";
        }
    }
}
