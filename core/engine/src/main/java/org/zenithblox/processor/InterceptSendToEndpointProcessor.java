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
import org.zenithblox.spi.InterceptSendToEndpoint;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.DefaultAsyncProducer;
import org.zenithblox.support.DefaultInterceptSendToEndpoint;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.zenithblox.processor.WorkflowHelper.continueProcessing;

/**
 * {@link org.zenithblox.Processor} used to interceptor and detour the routing when using the
 * {@link DefaultInterceptSendToEndpoint} functionality.
 */
public class InterceptSendToEndpointProcessor extends DefaultAsyncProducer {

    private static final Logger LOG = LoggerFactory.getLogger(InterceptSendToEndpointProcessor.class);

    private final InterceptSendToEndpoint endpoint;
    private final Endpoint delegate;
    private final AsyncProducer producer;
    private final boolean skip;
    private final Predicate onWhen;
    private AsyncProcessor workflow;
    private AsyncProcessor after;

    public InterceptSendToEndpointProcessor(InterceptSendToEndpoint endpoint, Endpoint delegate, AsyncProducer producer,
                                            boolean skip, Predicate onWhen) {
        super(delegate);
        this.endpoint = endpoint;
        this.delegate = delegate;
        this.producer = producer;
        this.skip = skip;
        this.onWhen = onWhen != null ? onWhen : p -> true;
    }

    @Override
    public Endpoint getEndpoint() {
        return producer.getEndpoint();
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        // process the detour so we do the detour routing
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sending to endpoint: {} is intercepted and detoured to: {} for exchange: {}", getEndpoint(),
                    endpoint.getBefore(), exchange);
        }
        exchange.setProperty(ExchangePropertyKey.INTERCEPTED_ENDPOINT, delegate.getEndpointUri());
        return workflow.process(exchange, doneSync -> callback(exchange, callback, doneSync));
    }

    private boolean callback(Exchange exchange, AsyncCallback callback, boolean doneSync) {
        // Decide whether to continue or not; similar logic to the Workflow
        // check for error if so we should break out
        if (!continueProcessing(exchange, "skip sending to original intended destination: " + getEndpoint(), LOG)) {
            callback.done(doneSync);
            return doneSync;
        }

        // determine if we should skip or not
        boolean shouldSkip = skip;

        // if then interceptor has predicate, then we should only skip if matched
        Boolean whenMatches = (Boolean) exchange.getProperty(ExchangePropertyKey.INTERCEPT_SEND_TO_ENDPOINT_WHEN_MATCHED);
        if (whenMatches != null) {
            shouldSkip = skip && whenMatches;
        }

        if (!shouldSkip) {
            ExchangeHelper.prepareOutToIn(exchange);

            AsyncCallback ac1 = doneSync1 -> {
                exchange.removeProperty(ExchangePropertyKey.INTERCEPT_SEND_TO_ENDPOINT_WHEN_MATCHED);
                callback.done(doneSync1);
            };
            AsyncCallback ac2 = null;
            if (after != null && (whenMatches == null || whenMatches)) {
                ac2 = doneSync2 -> after.process(exchange, ac1);
            }

            // workflow to original destination (using producer) and when done, then
            // optional workflow to the after processor
            boolean s = producer.process(exchange, ac2 != null ? ac2 : ac1);
            return doneSync && s;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Skip sending exchange to original intended destination: {} for exchange: {}",
                        getEndpoint(), exchange);
            }
            callback.done(doneSync);
            return doneSync;
        }
    }

    @Override
    public boolean isSingleton() {
        return producer.isSingleton();
    }

    @Override
    protected void doBuild() throws Exception {
        ZwangineContextAware.trySetZwangineContext(producer, endpoint.getZwangineContext());

        workflow = new FilterProcessor(getEndpoint().getZwangineContext(), onWhen, endpoint.getBefore());
        if (endpoint.getAfter() != null) {
            after = AsyncProcessorConverterHelper.convert(endpoint.getAfter());
        }
        ServiceHelper.buildService(producer, workflow, after);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(producer, workflow, after);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(producer, workflow, after);
    }

    @Override
    public void doStop() {
        // do not stop before/after as it should only be stopped when the interceptor stops
        // we should stop the producer here
        ServiceHelper.stopService(producer);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(producer, workflow);
    }
}
