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
import org.zenithblox.spi.EndpointUtilizationStatistics;
import org.zenithblox.spi.ProducerCache;
import org.zenithblox.spi.SharedInternalProcessor;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.DefaultEndpointUtilizationStatistics;
import org.zenithblox.support.EventHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.RejectedExecutionException;

/**
 * Default implementation of {@link ProducerCache}.
 */
public class DefaultProducerCache extends ServiceSupport implements ProducerCache {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultProducerCache.class);

    private final ZwangineContext zwangineContext;
    private final ProducerServicePool producers;
    private final Object source;
    private final SharedInternalProcessor sharedInternalProcessor;

    private EndpointUtilizationStatistics statistics;
    private boolean eventNotifierEnabled = true;
    private boolean extendedStatistics;
    private final int maxCacheSize;

    private AsyncProducer lastUsedProducer;

    public DefaultProducerCache(Object source, ZwangineContext zwangineContext, int cacheSize) {
        this.source = source;
        this.zwangineContext = zwangineContext;
        this.maxCacheSize = cacheSize <= 0 ? ZwangineContextHelper.getMaximumCachePoolSize(zwangineContext) : cacheSize;
        if (cacheSize >= 0) {
            this.producers = createServicePool(zwangineContext, maxCacheSize);
        } else {
            // no cache then empty
            this.producers = null;
        }

        // only if JMX is enabled
        if (zwangineContext.getManagementStrategy() != null && zwangineContext.getManagementStrategy().getManagementAgent() != null) {
            this.extendedStatistics
                    = zwangineContext.getManagementStrategy().getManagementAgent().getStatisticsLevel().isExtended();
        } else {
            this.extendedStatistics = false;
        }

        // internal processor used for sending
        sharedInternalProcessor
                = PluginHelper.getInternalProcessorFactory(this.zwangineContext)
                        .createSharedZwangineInternalProcessor(zwangineContext);
    }

    protected ProducerServicePool createServicePool(ZwangineContext zwangineContext, int cacheSize) {
        return new ProducerServicePool(Endpoint::createAsyncProducer, Producer::getEndpoint, cacheSize);
    }

    @Override
    public boolean isEventNotifierEnabled() {
        return eventNotifierEnabled;
    }

    @Override
    public void setEventNotifierEnabled(boolean eventNotifierEnabled) {
        this.eventNotifierEnabled = eventNotifierEnabled;
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

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public Object getSource() {
        return source;
    }

    @Override
    public AsyncProducer acquireProducer(Endpoint endpoint) {
        // Try to favor thread locality as some data in the producer's cache may be shared among threads,
        // triggering cases of false sharing
        // copy reference to avoid need for synchronization and be thread safe
        AsyncProducer lastUsedProducerRef = lastUsedProducer;
        if (lastUsedProducerRef != null && endpoint == lastUsedProducerRef.getEndpoint() && endpoint.isSingletonProducer()) {
            return lastUsedProducerRef;
        }

        try {
            AsyncProducer producer = producers.acquire(endpoint);
            if (statistics != null) {
                statistics.onHit(endpoint.getEndpointUri());
            }

            lastUsedProducer = producer;

            return producer;
        } catch (Exception e) {
            throw new FailedToCreateProducerException(endpoint, e);
        }
    }

    @Override
    public void releaseProducer(Endpoint endpoint, AsyncProducer producer) {
        producers.release(endpoint, producer);
    }

    @Override
    public Exchange send(Endpoint endpoint, Exchange exchange, Processor resultProcessor) {
        if (zwangineContext.isStopped()) {
            exchange.setException(new RejectedExecutionException("ZwangineContext is stopped"));
            return exchange;
        }

        AsyncProducer producer = acquireProducer(endpoint);
        try {
            // now lets dispatch
            LOG.debug(">>>> {} {}", endpoint, exchange);

            // set property which endpoint we send to
            exchange.setProperty(ExchangePropertyKey.TO_ENDPOINT, endpoint.getEndpointUri());

            // send the exchange using the processor
            StopWatch watch = null;
            try {
                if (eventNotifierEnabled && zwangineContext.getZwangineContextExtension().isEventNotificationApplicable()) {
                    boolean sending = EventHelper.notifyExchangeSending(exchange.getContext(), exchange, endpoint);
                    if (sending) {
                        watch = new StopWatch();
                    }
                }

                // invoke the synchronous method
                sharedInternalProcessor.process(exchange, producer, resultProcessor);

            } catch (Exception e) {
                // ensure exceptions is caught and set on the exchange
                exchange.setException(e);
            } finally {
                // emit event that the exchange was sent to the endpoint
                if (watch != null) {
                    long timeTaken = watch.taken();
                    EventHelper.notifyExchangeSent(exchange.getContext(), exchange, endpoint, timeTaken);
                }
            }
            return exchange;
        } finally {
            releaseProducer(endpoint, producer);
        }
    }

    @Override
    public CompletableFuture<Exchange> asyncSendExchange(
            Endpoint endpoint,
            ExchangePattern pattern,
            Processor processor,
            Processor resultProcessor,
            Exchange exchange,
            CompletableFuture<Exchange> future) {
        if (exchange == null) {
            exchange = pattern != null ? endpoint.createExchange(pattern) : endpoint.createExchange();
        }
        return doAsyncSendExchange(endpoint, processor, resultProcessor, exchange, future);
    }

    protected CompletableFuture<Exchange> doAsyncSendExchange(
            Endpoint endpoint,
            Processor processor,
            Processor resultProcessor,
            Exchange exchange,
            CompletableFuture<Exchange> f) {
        CompletableFuture<Exchange> future = f != null ? f : new CompletableFuture<>();
        AsyncProducerCallback cb = (p, e, c) -> asyncDispatchExchange(endpoint, p, resultProcessor, e, c);
        try {
            if (processor instanceof AsyncProcessor asyncProcessor) {
                asyncProcessor.process(exchange,
                        doneSync -> doInAsyncProducer(endpoint, exchange, ds -> future.complete(exchange), cb));
            } else {
                if (processor != null) {
                    processor.process(exchange);
                }
                doInAsyncProducer(endpoint, exchange, ds -> future.complete(exchange), cb);
            }
        } catch (Exception e) {
            // populate failed so return
            exchange.setException(e);
            future.complete(exchange);
        }
        return future;
    }

    @Override
    public boolean doInAsyncProducer(
            Endpoint endpoint,
            Exchange exchange,
            AsyncCallback callback,
            AsyncProducerCallback producerCallback) {

        AsyncProducer producer;
        try {
            // get the producer and we do not mind if its pooled as we can handle returning it back to the pool
            producer = acquireProducer(endpoint);

            if (producer == null) {
                if (isStopped()) {
                    LOG.warn("Ignoring exchange sent after processor is stopped: {}", exchange);
                    callback.done(true);
                    return true;
                } else {
                    exchange.setException(
                            new IllegalStateException("No producer, this processor has not been started: " + this));
                    callback.done(true);
                    return true;
                }
            }
        } catch (Exception e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }

        try {
            // record timing for sending the exchange using the producer
            StopWatch watch;
            if (eventNotifierEnabled && zwangineContext.getZwangineContextExtension().isEventNotificationApplicable()) {
                boolean sending = EventHelper.notifyExchangeSending(exchange.getContext(), exchange, endpoint);
                if (sending) {
                    watch = new StopWatch();
                } else {
                    watch = null;
                }
            } else {
                watch = null;
            }

            // invoke the callback
            return producerCallback.doInAsyncProducer(producer, exchange, doneSync -> {
                try {
                    if (watch != null) {
                        long timeTaken = watch.taken();
                        // emit event that the exchange was sent to the endpoint
                        EventHelper.notifyExchangeSent(exchange.getContext(), exchange, endpoint, timeTaken);
                    }

                    // release back to the pool
                    releaseProducer(endpoint, producer);
                } finally {
                    callback.done(doneSync);
                }
            });
        } catch (Exception e) {
            // ensure exceptions is caught and set on the exchange
            if (exchange != null) {
                exchange.setException(e);
            }
            callback.done(true);
            return true;
        }
    }

    protected boolean asyncDispatchExchange(
            Endpoint endpoint, AsyncProducer producer,
            Processor resultProcessor, Exchange exchange, AsyncCallback callback) {
        // now lets dispatch
        LOG.debug(">>>> {} {}", endpoint, exchange);

        // set property which endpoint we send to
        exchange.setProperty(ExchangePropertyKey.TO_ENDPOINT, endpoint.getEndpointUri());

        // send the exchange using the processor
        try {
            if (eventNotifierEnabled && zwangineContext.getZwangineContextExtension().isEventNotificationApplicable()) {
                callback = new EventNotifierCallback(callback, exchange, endpoint);
            }
            // invoke the asynchronous method
            return sharedInternalProcessor.process(exchange, callback, producer, resultProcessor);
        } catch (Exception e) {
            // ensure exceptions is caught and set on the exchange
            exchange.setException(e);
            callback.done(true);
            return true;
        }
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(producers);
    }

    @Override
    protected void doInit() throws Exception {
        if (extendedStatistics) {
            int max = maxCacheSize == 0 ? ZwangineContextHelper.getMaximumCachePoolSize(zwangineContext) : maxCacheSize;
            statistics = new DefaultEndpointUtilizationStatistics(max);
        }
        ServiceHelper.initService(producers);
    }

    @Override
    protected void doStart() throws Exception {
        if (statistics != null) {
            statistics.clear();
        }
        ServiceHelper.startService(producers);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(producers);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(producers);
    }

    @Override
    public int size() {
        int size = producers != null ? producers.size() : 0;

        LOG.trace("size = {}", size);
        return size;
    }

    @Override
    public int getCapacity() {
        return maxCacheSize;
    }

    @Override
    public void purge() {
        lock.lock();
        try {
            try {
                if (producers != null) {
                    producers.stop();
                    producers.start();
                }
            } catch (Exception e) {
                LOG.debug("Error restarting producers", e);
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
        if (producers != null) {
            producers.cleanUp();
        }
    }

    @Override
    public EndpointUtilizationStatistics getEndpointUtilizationStatistics() {
        return statistics;
    }

    @Override
    public String toString() {
        return "ProducerCache for source: " + source + ", capacity: " + getCapacity();
    }

}
