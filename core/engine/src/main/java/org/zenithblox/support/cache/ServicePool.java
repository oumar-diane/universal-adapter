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

import org.zenithblox.Endpoint;
import org.zenithblox.NonManagedService;
import org.zenithblox.Service;
import org.zenithblox.support.LRUCache;
import org.zenithblox.support.LRUCacheFactory;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.function.ThrowingFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * A base class for a pool for either producers or consumers used by {@link org.zenithblox.spi.ProducerCache} and
 * {@link org.zenithblox.spi.ConsumerCache}.
 */
abstract class ServicePool<S extends Service> extends ServiceSupport implements NonManagedService {

    private static final Logger LOG = LoggerFactory.getLogger(ServicePool.class);

    private final ThrowingFunction<Endpoint, S, Exception> creator;
    private final Function<S, Endpoint> getEndpoint;
    private final ConcurrentMap<Endpoint, Pool<S>> pool = new ConcurrentHashMap<>();
    // keep track of all singleton endpoints with a pooled producer that are evicted
    // for multi pool then they have their own house-keeping for evictions (more complex)
    private final ConcurrentMap<Endpoint, Pool<S>> singlePoolEvicted = new ConcurrentHashMap<>();
    private final int capacity;
    private final Map<S, S> cache;

    private interface Pool<S> {
        S acquire() throws Exception;

        void release(S s);

        int size();

        void stop();

        void evict(S s);

        void cleanUp();
    }

    public ServicePool(ThrowingFunction<Endpoint, S, Exception> creator, Function<S, Endpoint> getEndpoint, int capacity) {
        this.creator = creator;
        this.getEndpoint = getEndpoint;
        this.capacity = capacity;
        this.cache = capacity > 0 ? LRUCacheFactory.newLRUCache(capacity, this::onEvict) : null;
    }

    /**
     * This callback is invoked by LRUCache from a separate background cleanup thread. Therefore, we mark the entries to
     * be evicted from this thread only, and then let SinglePool and MultiPool handle the evictions (stop the
     * producer/consumer safely) when they are acquiring/releases producers/consumers. If we stop the producer/consumer
     * from the LRUCache background thread we can have a race condition with a pooled producer may have been acquired at
     * the same time its being evicted.
     */
    protected void onEvict(S s) {
        Endpoint e = getEndpoint.apply(s);
        Pool<S> p = pool.get(e);
        if (p != null) {
            p.evict(s);
            if (capacity > 0 && pool.size() > capacity) {
                // the pool is growing too large, so we need to stop (stop will remove itself from pool)
                p.stop();
            }
        } else {
            // service no longer in a pool (such as being released twice, or can happen during shutdown of Zwangine etc)
            ServicePool.stop(s);
            try {
                e.getZwangineContext().removeService(s);
            } catch (Exception ex) {
                LOG.debug("Error removing service: {}. This exception is ignored.", s, ex);
            }
        }
    }

    /**
     * Tries to acquire the producer/consumer with the given key
     *
     * @param  endpoint the endpoint
     * @return          the acquired producer/consumer
     */
    public S acquire(Endpoint endpoint) throws Exception {
        if (!isStarted()) {
            return null;
        }
        S s = getOrCreatePool(endpoint).acquire();
        if (s != null && cache != null) {
            cache.putIfAbsent(s, s);
        }
        return s;
    }

    /**
     * Releases the producer/consumer back to the pool
     *
     * @param endpoint the endpoint
     * @param s        the producer/consumer
     */
    public void release(Endpoint endpoint, S s) {
        Pool<S> p = pool.get(endpoint);
        if (p != null) {
            p.release(s);
        }
    }

    private Pool<S> getOrCreatePool(Endpoint endpoint) {
        // it is a pool, so we have a lot more hits, so use regular get, and then fallback to computeIfAbsent
        Pool<S> answer = pool.get(endpoint);
        if (answer == null) {
            boolean singleton = endpoint.isSingletonProducer();
            if (singleton) {
                answer = pool.computeIfAbsent(endpoint, SinglePool::new);
            } else {
                answer = pool.computeIfAbsent(endpoint, MultiplePool::new);
            }
        }
        return answer;
    }

    /**
     * Returns the current size of the pool
     */
    public int size() {
        return pool.values().stream().mapToInt(Pool::size).sum();
    }

    /**
     * Cleanup the pool (removing stale instances that should be evicted)
     */
    @SuppressWarnings("rawtypes")
    public void cleanUp() {
        if (cache instanceof LRUCache lru) {
            lru.cleanUp();
        }
        pool.values().forEach(Pool::cleanUp);
    }

    @Override
    protected void doStop() throws Exception {
        cleanUp();

        pool.values().forEach(Pool::stop);
        pool.clear();
        if (cache != null) {
            cache.values().forEach(ServicePool::stop);
            cache.clear();
        }
        singlePoolEvicted.values().forEach(Pool::stop);
        singlePoolEvicted.clear();
    }

    /**
     * Stops the service safely
     */
    private static <S extends Service> void stop(S s) {
        try {
            s.stop();
        } catch (Exception e) {
            LOG.debug("Error stopping service: {}. This exception is ignored.", s, e);
        }
    }

    /**
     * Pool used for singleton producers or consumers which are thread-safe and can be shared by multiple worker threads
     * at any given time.
     */
    private class SinglePool implements Pool<S> {
        private final Endpoint endpoint;
        private volatile S s;

        SinglePool(Endpoint endpoint) {
            this.endpoint = requireNonNull(endpoint);
        }

        @Override
        public S acquire() throws Exception {
            cleanupEvicts();

            if (s == null) {
                lock.lock();
                try {
                    if (s == null) {
                        LOG.trace("Creating service from endpoint: {}", endpoint);
                        S tempS = creator.apply(endpoint);
                        endpoint.getZwangineContext().addService(tempS, true, true);
                        s = tempS;
                    }
                } finally {
                    lock.unlock();
                }
            }
            LOG.trace("Acquired service: {}", s);
            return s;
        }

        @Override
        public void release(S s) {
            cleanupEvicts();

            // noop
            LOG.trace("Released service: {}", s);
        }

        @Override
        public int size() {
            return s != null ? 1 : 0;
        }

        @Override
        public void stop() {
            S toStop;
            lock.lock();
            try {
                toStop = s;
                s = null;
            } finally {
                lock.unlock();
            }
            doStop(toStop);
            pool.remove(endpoint);
        }

        @Override
        public void evict(S s) {
            singlePoolEvicted.putIfAbsent(endpoint, this);
        }

        @Override
        public void cleanUp() {
            cleanupEvicts();
        }

        private void cleanupEvicts() {
            if (!singlePoolEvicted.isEmpty()) {
                for (Map.Entry<Endpoint, Pool<S>> entry : singlePoolEvicted.entrySet()) {
                    Endpoint e = entry.getKey();
                    Pool<S> p = entry.getValue();
                    doStop(e);
                    p.stop();
                    singlePoolEvicted.remove(e);
                }
            }
        }

        void doStop(Service s) {
            if (s != null) {
                ServicePool.stop(s);
                try {
                    endpoint.getZwangineContext().removeService(s);
                } catch (Exception e) {
                    LOG.debug("Error removing service: {}. This exception is ignored.", s, e);
                }
            }
        }
    }

    /**
     * Pool used for non-singleton producers or consumers which are not thread-safe and can only be used by one worker
     * thread at any given time.
     */
    private class MultiplePool implements Pool<S> {
        private final Endpoint endpoint;
        private final BlockingQueue<S> queue;
        private final Deque<S> evicts;

        MultiplePool(Endpoint endpoint) {
            this.endpoint = endpoint;
            this.queue = new ArrayBlockingQueue<>(capacity);
            this.evicts = new ConcurrentLinkedDeque<>();
        }

        private void cleanupEvicts() {
            for (S evict = evicts.pollFirst(); evict != null; evict = evicts.pollFirst()) {
                doStop(evict);
            }
        }

        @Override
        public S acquire() throws Exception {
            cleanupEvicts();

            S s = queue.poll();
            if (s == null) {
                s = creator.apply(endpoint);
                s.start();
            }
            return s;
        }

        @Override
        public void release(S s) {
            cleanupEvicts();

            if (!queue.offer(s)) {
                // there is no room so let's just stop and discard this
                doStop(s);
            }
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public void stop() {
            ArrayList<S> list = new ArrayList<>();
            queue.drainTo(list);
            pool.remove(endpoint);
            list.forEach(this::doStop);
        }

        @Override
        public void evict(S s) {
            // to be evicted
            evicts.add(s);
        }

        @Override
        public void cleanUp() {
            cleanupEvicts();
        }

        void doStop(Service s) {
            if (s != null) {
                ServicePool.stop(s);
                try {
                    if (endpoint != null) {
                        endpoint.getZwangineContext().removeService(s);
                    }
                } catch (Exception e) {
                    LOG.debug("Error removing service: {}. This exception is ignored.", s, e);
                }
            }
        }
    }

}
