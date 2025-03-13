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

import org.zenithblox.AsyncProducer;
import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.spi.AsyncProcessorAwaitManager;

import java.util.concurrent.CompletableFuture;

/**
 * A default implementation of {@link org.zenithblox.Producer} for implementation inheritance, which can process
 * {@link Exchange}s asynchronously.
 */
public abstract class DefaultAsyncProducer extends DefaultProducer implements AsyncProducer {

    public DefaultAsyncProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        AsyncProcessorAwaitManager awaitManager
                = PluginHelper.getAsyncProcessorAwaitManager(exchange.getContext());
        awaitManager.process(this, exchange);
    }

    @Override
    public CompletableFuture<Exchange> processAsync(Exchange exchange) {
        AsyncCallbackToCompletableFutureAdapter<Exchange> callback = new AsyncCallbackToCompletableFutureAdapter<>(exchange);
        process(exchange, callback);
        return callback.getFuture();
    }
}
