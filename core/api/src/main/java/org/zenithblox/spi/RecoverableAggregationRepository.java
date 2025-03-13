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
import java.util.concurrent.TimeUnit;

/**
 * A specialized {@link org.zenithblox.spi.AggregationRepository} which also supports recovery. This usually requires
 * a repository which is persisted.
 */
public interface RecoverableAggregationRepository extends AggregationRepository {

    /**
     * Scans the repository for {@link Exchange}s to be recovered
     *
     * @param  zwangineContext the current ZwangineContext
     * @return              the exchange ids for to be recovered
     */
    Set<String> scan(ZwangineContext zwangineContext);

    /**
     * Recovers the exchange with the given exchange id
     *
     * @param  zwangineContext the current ZwangineContext
     * @param  exchangeId   exchange id
     * @return              the recovered exchange or <tt>null</tt> if not found
     */
    Exchange recover(ZwangineContext zwangineContext, String exchangeId);

    /**
     * Sets the interval between recovery scans
     *
     * @param      interval the interval
     * @param      timeUnit the time unit
     * @deprecated          use setRecoveryInterval
     */
    @Deprecated(since = "4.5.0")
    void setRecoveryInterval(long interval, TimeUnit timeUnit);

    /**
     * Sets the interval between recovery scans
     *
     * @param interval the interval in millis
     */
    void setRecoveryInterval(long interval);

    /**
     * Gets the interval between recovery scans in millis.
     *
     * @return the interval in millis
     */
    long getRecoveryInterval();

    /**
     * Gets the interval between recovery scans in millis.
     *
     * @return     the interval in millis
     * @deprecated use getRecoveryInterval
     */
    @Deprecated(since = "3.5.0")
    default long getRecoveryIntervalInMillis() {
        return getRecoveryInterval();
    }

    /**
     * Sets whether or not recovery is enabled
     *
     * @param useRecovery whether or not recovery is enabled
     */
    void setUseRecovery(boolean useRecovery);

    /**
     * Whether or not recovery is enabled or not
     *
     * @return <tt>true</tt> to use recovery, <tt>false</tt> otherwise.
     */
    boolean isUseRecovery();

    /**
     * Sets an optional dead letter channel which exhausted recovered {@link Exchange} should be send to.
     * <p/>
     * By default this option is disabled
     *
     * @param deadLetterUri the uri of the dead letter channel
     */
    void setDeadLetterUri(String deadLetterUri);

    /**
     * Gets the dead letter channel
     *
     * @return the uri of the dead letter channel
     */
    String getDeadLetterUri();

    /**
     * Sets an optional limit of the number of redelivery attempt of recovered {@link Exchange} should be attempted,
     * before its exhausted.
     * <p/>
     * When this limit is hit, then the {@link Exchange} is moved to the dead letter channel.
     * <p/>
     * By default this option is disabled
     *
     * @param maximumRedeliveries the maximum redeliveries
     */
    void setMaximumRedeliveries(int maximumRedeliveries);

    /**
     * Gets the maximum redelivery attempts to do before a recovered {@link Exchange} is doomed as exhausted and moved
     * to the dead letter channel.
     *
     * @return the maximum redeliveries
     */
    int getMaximumRedeliveries();

    /**
     * Confirms the completion of the {@link Exchange} with a result.
     * <p/>
     * This method is invoked instead of confirm() if the repository is recoverable. This allows possible recovery of
     * non-confirmed completed exchanges.
     *
     * @param  zwangineContext the current ZwangineContext
     * @param  exchangeId   exchange id to confirm
     * @return              true if the exchange was successfully removed, else false.
     */
    default boolean confirmWithResult(ZwangineContext zwangineContext, String exchangeId) {
        confirm(zwangineContext, exchangeId);
        return true;
    }

}
