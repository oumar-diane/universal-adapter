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
package org.zenithblox.spi;

import org.zenithblox.Consumer;
import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.NonManagedService;

/**
 * Factory used by {@link Consumer} to create Zwangine {@link Exchange} holding the incoming message received by the
 * consumer.
 * <p/>
 * This factory is only for {@link Consumer}'s to give control on how {@link Exchange} are created and comes into Zwangine.
 * Each Zwangine component that provides a {@link Consumer} should use this {@link ExchangeFactory}. There may be other
 * parts in Zwangine that creates {@link Exchange} such as sub exchanges from Splitter EIP, but they are not part of this
 * contract as we only want to control the created {@link Exchange} that comes into Zwangine via {@link Consumer} or
 * {@link org.zenithblox.PollingConsumer}.
 * <p/>
 * The factory is pluggable which allows using different strategies. The default factory will create a new
 * {@link Exchange} instance, and the pooled factory will pool and reuse exchanges.
 *
 * @see ProcessorExchangeFactory
 * @see org.zenithblox.PooledExchange
 */
public interface ExchangeFactory extends PooledObjectFactory<Exchange>, NonManagedService, WorkflowIdAware {

    /**
     * Service factory key.
     */
    String FACTORY = "exchange-factory";

    /**
     * The consumer using this factory.
     */
    Consumer getConsumer();

    /**
     * Creates a new {@link ExchangeFactory} that is private for the given consumer.
     *
     * @param  consumer the consumer that will use the created {@link ExchangeFactory}
     * @return          the created factory.
     */
    ExchangeFactory newExchangeFactory(Consumer consumer);

    /**
     * Gets a new {@link Exchange}
     *
     * @param autoRelease whether to auto release the exchange when routing is complete via {@link UnitOfWork}
     */
    Exchange create(boolean autoRelease);

    /**
     * Gets a new {@link Exchange}
     *
     * @param autoRelease  whether to auto release the exchange when routing is complete via {@link UnitOfWork}
     * @param fromEndpoint the from endpoint
     */
    Exchange create(Endpoint fromEndpoint, boolean autoRelease);

    /**
     * Releases the exchange back into the pool
     *
     * @param  exchange the exchange
     * @return          true if released into the pool, or false if something went wrong and the exchange was discarded
     */
    default boolean release(Exchange exchange) {
        return true;
    }

}
