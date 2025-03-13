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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;

import java.util.Set;

/**
 * Access to a repository to store aggregated exchanges to support pluggable implementations.
 *
 */
public interface AggregationRepository {

    /**
     * Add the given {@link Exchange} under the correlation key.
     * <p/>
     * Will replace any existing exchange.
     * <p/>
     * <b>Important:</b> This method is <b>not</b> invoked if only one exchange was completed, and therefore the
     * exchange does not need to be added to a repository, as its completed immediately.
     *
     * @param  zwangineContext the current ZwangineContext
     * @param  key          the correlation key
     * @param  exchange     the aggregated exchange
     * @return              the old exchange if any existed
     */
    Exchange add(ZwangineContext zwangineContext, String key, Exchange exchange);

    /**
     * Gets the given exchange with the correlation key
     * <p/>
     * This method is always invoked for any incoming exchange in the aggregator.
     *
     * @param  zwangineContext the current ZwangineContext
     * @param  key          the correlation key
     * @return              the exchange, or <tt>null</tt> if no exchange was previously added
     */
    Exchange get(ZwangineContext zwangineContext, String key);

    /**
     * Removes the exchange with the given correlation key, which should happen when an {@link Exchange} is completed
     * <p/>
     * <b>Important:</b> This method is <b>not</b> invoked if only one exchange was completed, and therefore the
     * exchange does not need to be added to a repository, as its completed immediately.
     *
     * @param zwangineContext the current ZwangineContext
     * @param key          the correlation key
     * @param exchange     the exchange to remove
     */
    void remove(ZwangineContext zwangineContext, String key, Exchange exchange);

    /**
     * Confirms the completion of the {@link Exchange}.
     * <p/>
     * This method is always invoked.
     *
     * @param zwangineContext the current ZwangineContext
     * @param exchangeId   exchange id to confirm
     */
    void confirm(ZwangineContext zwangineContext, String exchangeId);

    /**
     * Gets the keys currently in the repository.
     *
     * @return the keys
     */
    Set<String> getKeys();

}
