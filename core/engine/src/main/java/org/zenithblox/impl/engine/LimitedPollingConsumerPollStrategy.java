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
package org.zenithblox.impl.engine;

import org.zenithblox.Consumer;
import org.zenithblox.Endpoint;
import org.zenithblox.support.DefaultPollingConsumerPollStrategy;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * A {@link org.zenithblox.spi.PollingConsumerPollStrategy} which supports suspending consumers if they failed for X
 * number of times in a row.
 * <p/>
 * If Zwangine cannot successfully consumer from a given consumer, then after X consecutive failed attempts the consumer
 * will be suspended/stopped. This prevents the log to get flooded with failed attempts, for example during nightly
 * runs.
 */
public class LimitedPollingConsumerPollStrategy extends DefaultPollingConsumerPollStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(LimitedPollingConsumerPollStrategy.class);

    private final Map<Consumer, Integer> state = new HashMap<>();
    private int limit = 3;

    public int getLimit() {
        return limit;
    }

    /**
     * Sets the limit for how many straight rollbacks causes this strategy to suspend the fault consumer.
     * <p/>
     * When the consumer has been suspended, it has to be manually resumed/started to be active again. The limit is by
     * default 3.
     *
     * @param limit the limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public void commit(Consumer consumer, Endpoint endpoint, int polledMessages) {
        // we could commit so clear state
        state.remove(consumer);
    }

    @Override
    public boolean rollback(Consumer consumer, Endpoint endpoint, int retryCounter, Exception cause) throws Exception {
        // keep track how many times in a row we have rolled back
        Integer times = state.get(consumer);
        if (times == null) {
            times = 1;
        } else {
            times += 1;
        }
        LOG.debug("Rollback occurred after {} times when consuming {}", times, endpoint);

        boolean retry = false;

        if (times >= limit) {
            // clear state when we suspend so if its restarted manually we start all over again
            state.remove(consumer);
            onSuspend(consumer, endpoint);
        } else {
            // error occurred
            state.put(consumer, times);
            retry = onRollback(consumer, endpoint);
        }

        return retry;
    }

    /**
     * The consumer is to be suspended because it exceeded the limit
     *
     * @param  consumer  the consumer
     * @param  endpoint  the endpoint
     * @throws Exception is thrown if error suspending the consumer
     */
    protected void onSuspend(Consumer consumer, Endpoint endpoint) throws Exception {
        LOG.warn("Suspending consumer {} after {} attempts to consume from {}. You have to manually resume the consumer!",
                consumer, limit, endpoint);
        ServiceHelper.suspendService(consumer);
    }

    /**
     * Rollback occurred.
     *
     * @param  consumer  the consumer
     * @param  endpoint  the endpoint
     * @return           whether to retry immediately, is default <tt>false</tt>
     * @throws Exception can be thrown in case something goes wrong
     */
    protected boolean onRollback(Consumer consumer, Endpoint endpoint) throws Exception {
        // do not retry by default
        return false;
    }

    @Override
    protected void doStop() throws Exception {
        state.clear();
    }

}
