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
package org.zenithblox.support.cache;

import org.zenithblox.*;
import org.zenithblox.spi.ConsumerCache;
import org.zenithblox.spi.EndpointUtilizationStatistics;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.DefaultEndpointUtilizationStatistics;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.RejectedExecutionException;

/**
 * Cache containing created {@link org.zenithblox.Consumer}.
 */
public class DefaultConsumerCache extends ServiceSupport implements ConsumerCache {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConsumerCache.class);
    public static final String CONTEXT_IS_STOPPED = "ZwangineContext is stopped";

    private final ZwangineContext zwangineContext;
    private final PollingConsumerServicePool consumers;
    private final Object source;

    private EndpointUtilizationStatistics statistics;
    private boolean extendedStatistics;
    private final int maxCacheSize;

    public DefaultConsumerCache(Object source, ZwangineContext zwangineContext, int cacheSize) {
        this.source = source;
        this.zwangineContext = zwangineContext;
        this.maxCacheSize = cacheSize <= 0 ? ZwangineContextHelper.getMaximumCachePoolSize(zwangineContext) : cacheSize;
        this.consumers = createServicePool(zwangineContext, maxCacheSize);
        // only if JMX is enabled
        if (zwangineContext.getManagementStrategy().getManagementAgent() != null) {
            this.extendedStatistics
                    = zwangineContext.getManagementStrategy().getManagementAgent().getStatisticsLevel().isExtended();
        } else {
            this.extendedStatistics = false;
        }
    }

    protected PollingConsumerServicePool createServicePool(ZwangineContext zwangineContext, int cacheSize) {
        return new PollingConsumerServicePool(Endpoint::createPollingConsumer, Consumer::getEndpoint, cacheSize);
    }

    public boolean isExtendedStatistics() {
        return extendedStatistics;
    }

    /**
     * Whether extended JMX statistics is enabled for {@link org.zenithblox.spi.EndpointUtilizationStatistics}
     */
    public void setExtendedStatistics(boolean extendedStatistics) {
        this.extendedStatistics = extendedStatistics;
    }

    /**
     * Releases an acquired producer back after usage.
     *
     * @param endpoint        the endpoint
     * @param pollingConsumer the pollingConsumer to release
     */
    @Override
    public void releasePollingConsumer(Endpoint endpoint, PollingConsumer pollingConsumer) {
        consumers.release(endpoint, pollingConsumer);
    }

    /**
     * Acquires a pooled PollingConsumer which you <b>must</b> release back again after usage using the
     * {@link #releasePollingConsumer(org.zenithblox.Endpoint, org.zenithblox.PollingConsumer)} method.
     *
     * @param  endpoint the endpoint
     * @return          the PollingConsumer
     */
    @Override
    public PollingConsumer acquirePollingConsumer(Endpoint endpoint) {
        try {
            PollingConsumer consumer = consumers.acquire(endpoint);
            if (statistics != null) {
                statistics.onHit(endpoint.getEndpointUri());
            }
            return consumer;
        } catch (Exception e) {
            throw new FailedToCreateConsumerException(endpoint, e);
        }
    }

    @Override
    public Exchange receive(Endpoint endpoint) {
        if (zwangineContext.isStopped()) {
            throw new RejectedExecutionException(CONTEXT_IS_STOPPED);
        }

        LOG.debug("<<<< {}", endpoint);
        PollingConsumer consumer = null;
        try {
            consumer = acquirePollingConsumer(endpoint);
            return consumer.receive();
        } finally {
            if (consumer != null) {
                releasePollingConsumer(endpoint, consumer);
            }
        }
    }

    @Override
    public Exchange receive(Endpoint endpoint, long timeout) {
        if (zwangineContext.isStopped()) {
            throw new RejectedExecutionException(CONTEXT_IS_STOPPED);
        }

        LOG.debug("<<<< {}", endpoint);
        PollingConsumer consumer = null;
        try {
            consumer = acquirePollingConsumer(endpoint);
            return consumer.receive(timeout);
        } finally {
            if (consumer != null) {
                releasePollingConsumer(endpoint, consumer);
            }
        }
    }

    @Override
    public Exchange receiveNoWait(Endpoint endpoint) {
        if (zwangineContext.isStopped()) {
            throw new RejectedExecutionException(CONTEXT_IS_STOPPED);
        }

        LOG.debug("<<<< {}", endpoint);
        PollingConsumer consumer = null;
        try {
            consumer = acquirePollingConsumer(endpoint);
            return consumer.receiveNoWait();
        } finally {
            if (consumer != null) {
                releasePollingConsumer(endpoint, consumer);
            }
        }
    }

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    /**
     * Gets the source which uses this cache
     *
     * @return the source
     */
    @Override
    public Object getSource() {
        return source;
    }

    /**
     * Gets the maximum cache size (capacity).
     *
     * @return the capacity
     */
    @Override
    public int getCapacity() {
        return maxCacheSize;
    }

    /**
     * Returns the current size of the cache
     *
     * @return the current size
     */
    @Override
    public int size() {
        int size = consumers.size();
        LOG.trace("size = {}", size);
        return size;
    }

    /**
     * Purges this cache
     */
    @Override
    public void purge() {
        lock.lock();
        try {
            try {
                consumers.stop();
                consumers.start();
            } catch (Exception e) {
                LOG.debug("Error restarting consumer pool", e);
            }
            if (statistics != null) {
                statistics.clear();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cleanUp() {
        consumers.cleanUp();
    }

    @Override
    public EndpointUtilizationStatistics getEndpointUtilizationStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "ConsumerCache for source: " + source + ", capacity: " + getCapacity();
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(consumers);
    }

    @Override
    protected void doInit() throws Exception {
        if (extendedStatistics) {
            int max = maxCacheSize == 0 ? ZwangineContextHelper.getMaximumCachePoolSize(zwangineContext) : maxCacheSize;
            statistics = new DefaultEndpointUtilizationStatistics(max);
        }
        ServiceHelper.initService(consumers);
    }

    @Override
    protected void doStart() throws Exception {
        if (statistics != null) {
            statistics.clear();
        }
        ServiceHelper.startService(consumers);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(consumers);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(consumers);
    }
}
