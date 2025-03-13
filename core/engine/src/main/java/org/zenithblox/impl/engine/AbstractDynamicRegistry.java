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

import org.zenithblox.ZwangineContext;
import org.zenithblox.StaticService;
import org.zenithblox.spi.WorkflowController;
import org.zenithblox.support.LRUCache;
import org.zenithblox.support.LRUCacheFactory;
import org.zenithblox.support.service.ServiceHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Base implementation for {@link org.zenithblox.spi.EndpointRegistry},
 * {@link org.zenithblox.spi.TransformerRegistry}, and {@link org.zenithblox.spi.ValidatorRegistry}.
 */
public class AbstractDynamicRegistry<K, V> extends AbstractMap<K, V> implements StaticService {

    protected final ZwangineContext context;
    protected final WorkflowController workflowController;
    protected final int maxCacheSize;
    protected final Map<K, V> dynamicMap;
    protected final Map<K, V> staticMap;

    public AbstractDynamicRegistry(ZwangineContext context, int maxCacheSize) {
        this.context = context;
        this.workflowController = context.getWorkflowController();
        this.maxCacheSize = maxCacheSize;
        // do not stop on eviction, as the endpoint or transformer may still be in use
        this.dynamicMap = LRUCacheFactory.newLRUCache(this.maxCacheSize, this.maxCacheSize, false);
        // static map to hold endpoint or transformer we do not want to be evicted
        this.staticMap = new ConcurrentHashMap<>();
    }

    @Override
    public void start() {
        if (dynamicMap instanceof LRUCache<K, V> lruCache) {
            lruCache.resetStatistics();
        }
    }

    @Override
    public V get(Object o) {
        // keep this get optimized to only lookup
        // try static map first and fallback to dynamic
        V answer = staticMap.get(o);
        if (answer == null) {
            answer = dynamicMap.get(o);
        }
        return answer;
    }

    @Override
    public V put(K key, V obj) {
        // at first we must see if the key already exists and then replace it back, so it stays the same spot
        V answer = staticMap.remove(key);
        if (answer != null) {
            // replace existing
            staticMap.put(key, obj);
            return answer;
        }

        answer = dynamicMap.remove(key);
        if (answer != null) {
            // replace existing
            dynamicMap.put(key, obj);
            return answer;
        }

        // we want endpoint or transformer to be static if they are part of
        // starting up zwangine, or if new workflows are being setup/added or workflows started later
        if (!context.isStarted() || context.getZwangineContextExtension().isSetupWorkflows() || workflowController.isStartingWorkflows()) {
            answer = staticMap.put(key, obj);
        } else {
            answer = dynamicMap.put(key, obj);
        }

        return answer;
    }

    @Override
    public boolean containsKey(Object o) {
        return staticMap.containsKey(o) || dynamicMap.containsKey(o);
    }

    @Override
    public boolean containsValue(Object o) {
        return staticMap.containsValue(o) || dynamicMap.containsValue(o);
    }

    @Override
    public int size() {
        return staticMap.size() + dynamicMap.size();
    }

    public int staticSize() {
        return staticMap.size();
    }

    public int dynamicSize() {
        return dynamicMap.size();
    }

    @Override
    public boolean isEmpty() {
        return staticMap.isEmpty() && dynamicMap.isEmpty();
    }

    @Override
    public V remove(Object o) {
        V answer = staticMap.remove(o);
        if (answer == null) {
            answer = dynamicMap.remove(o);
        }
        return answer;
    }

    @Override
    public void clear() {
        staticMap.clear();
        dynamicMap.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new CompoundIterator<>(
                        Arrays.asList(
                                staticMap.entrySet().iterator(), dynamicMap.entrySet().iterator()));
            }

            @Override
            public int size() {
                return staticMap.size() + dynamicMap.size();
            }
        };
    }

    public int getMaximumCacheSize() {
        return maxCacheSize;
    }

    public void purge() {
        // only purge the dynamic part
        dynamicMap.clear();
    }

    public void cleanUp() {
        if (dynamicMap instanceof LRUCache<K, V> cache) {
            cache.cleanUp();
        }
    }

    public boolean isStatic(K key) {
        return staticMap.containsKey(key);
    }

    public boolean isDynamic(K key) {
        return dynamicMap.containsKey(key);
    }

    public Collection<V> getReadOnlyValues() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        // we want to avoid any kind of locking in get/put methods
        // as getReadOnlyValues is only seldom used, such as when zwangine-mock
        // is asserting endpoints at end of testing
        // so this code will then just retry in case of a concurrency update
        Collection<V> answer = new ArrayList<>();
        boolean done = false;
        while (!done) {
            try {
                answer.addAll(values());
                done = true;
            } catch (ConcurrentModificationException e) {
                answer.clear();
                // try again
            }
        }
        return Collections.unmodifiableCollection(answer);
    }

    public Map<String, V> getReadOnlyMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        // we want to avoid any kind of locking in get/put methods
        // as getReadOnlyValues is only seldom used, such as when zwangine-mock
        // is asserting endpoints at end of testing
        // so this code will then just retry in case of a concurrency update
        Map<String, V> answer = new LinkedHashMap<>();
        boolean done = false;
        while (!done) {
            try {
                for (Entry<K, V> entry : entrySet()) {
                    String k = entry.getKey().toString();
                    answer.put(k, entry.getValue());
                }
                done = true;
            } catch (ConcurrentModificationException e) {
                answer.clear();
                // try again
            }
        }
        return Collections.unmodifiableMap(answer);
    }

    @Override
    public void stop() {
        ServiceHelper.stopService(staticMap.values(), dynamicMap.values());
        purge();
    }

    @Override
    public String toString() {
        return "Registry for " + context.getName() + " [capacity: " + maxCacheSize + "]";
    }

}
