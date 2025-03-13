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

import org.zenithblox.*;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;

import java.util.concurrent.CompletableFuture;

/**
 * A {@link Producer} that defers being started, until {@link org.zenithblox.ZwangineContext} has been started, this
 * ensures that the producer is able to adapt to changes that may otherwise occur during starting ZwangineContext. If we do
 * not defer starting the producer it may not adapt to those changes, and send messages to wrong endpoints.
 */
public class DeferProducer extends ServiceSupport implements Producer, AsyncProcessor {

    private Producer delegate;
    private final Endpoint endpoint;

    public DeferProducer(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        if (delegate == null) {
            throw new IllegalStateException("Not started");
        }
        delegate.process(exchange);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (delegate == null) {
            exchange.setException(new IllegalStateException("Not started"));
            callback.done(true);
            return true;
        }

        if (delegate instanceof AsyncProcessor asyncProcessor) {
            return asyncProcessor.process(exchange, callback);
        }

        // fallback to sync mode
        try {
            process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    public CompletableFuture<Exchange> processAsync(Exchange exchange) {
        AsyncCallbackToCompletableFutureAdapter<Exchange> callback = new AsyncCallbackToCompletableFutureAdapter<>(exchange);
        process(exchange, callback);
        return callback.getFuture();
    }

    @Override
    protected void doStart() throws Exception {
        // need to lookup endpoint again as it may be intercepted
        Endpoint lookup = endpoint.getZwangineContext().getEndpoint(endpoint.getEndpointUri());

        delegate = lookup.createProducer();
        ServiceHelper.startService(delegate);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(delegate);
    }

    @Override
    public boolean isSingleton() {
        if (delegate != null) {
            return delegate.isSingleton();
        } else {
            // assume singleton by default
            return true;
        }
    }

    @Override
    public Endpoint getEndpoint() {
        if (delegate != null) {
            return delegate.getEndpoint();
        } else {
            return endpoint;
        }
    }

    @Override
    public String toString() {
        if (delegate != null) {
            return delegate.toString();
        } else {
            return "DelegateProducer[" + endpoint + "]";
        }
    }

}
