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
package org.zenithblox.support.processor.idempotent;

import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.IdempotentRepository;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.LRUCache;
import org.zenithblox.support.LRUCacheFactory;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A memory based implementation of {@link org.zenithblox.spi.IdempotentRepository}.
 * <p/>
 * Care should be taken to use a suitable underlying {@link Map} to avoid this class being a memory leak.
 */
@Metadata(label = "bean",
          description = "A memory based IdempotentRepository.",
          annotations = { "interfaceName=org.zenithblox.spi.IdempotentRepository" })
@Configurer(metadataOnly = true)
public class MemoryIdempotentRepository extends ServiceSupport implements IdempotentRepository {

    private static final int MAX_CACHE_SIZE = 1000;

    private Map<String, Object> cache;
    private final Lock cacheAndStoreLock = new ReentrantLock();

    @Metadata(description = "Maximum elements that can be stored in-memory", defaultValue = "" + MAX_CACHE_SIZE)
    private int cacheSize;

    public MemoryIdempotentRepository() {
    }

    public MemoryIdempotentRepository(Map<String, Object> set) {
        this.cache = set;
    }

    /**
     * Creates a new memory based repository using a {@link LRUCache} with a default of 1000 entries in the cache.
     */
    public static IdempotentRepository memoryIdempotentRepository() {
        return memoryIdempotentRepository(MAX_CACHE_SIZE);
    }

    /**
     * Creates a new memory based repository using a {@link LRUCache}.
     *
     * @param cacheSize the cache size
     */
    public static IdempotentRepository memoryIdempotentRepository(int cacheSize) {
        MemoryIdempotentRepository answer = new MemoryIdempotentRepository();
        answer.setCacheSize(cacheSize);
        ServiceHelper.startService(answer);
        return answer;
    }

    /**
     * Creates a new memory based repository using the given {@link Map} to use to store the processed message ids.
     * <p/>
     * Care should be taken to use a suitable underlying {@link Map} to avoid this class being a memory leak.
     *
     * @param cache the cache
     */
    public static IdempotentRepository memoryIdempotentRepository(Map<String, Object> cache) {
        return new MemoryIdempotentRepository(cache);
    }

    @Override
    public boolean add(String key) {
        cacheAndStoreLock.lock();
        try {
            if (cache.containsKey(key)) {
                return false;
            } else {
                cache.put(key, key);
                return true;
            }
        } finally {
            cacheAndStoreLock.unlock();
        }
    }

    @Override
    public boolean contains(String key) {
        cacheAndStoreLock.lock();
        try {
            return cache.containsKey(key);
        } finally {
            cacheAndStoreLock.unlock();
        }
    }

    @Override
    public boolean remove(String key) {
        cacheAndStoreLock.lock();
        try {
            return cache.remove(key) != null;
        } finally {
            cacheAndStoreLock.unlock();
        }
    }

    @Override
    public boolean confirm(String key) {
        // noop
        return true;
    }

    @Override
    public void clear() {
        cacheAndStoreLock.lock();
        try {
            cache.clear();
        } finally {
            cacheAndStoreLock.unlock();
        }
    }

    public Map<String, Object> getCache() {
        return cache;
    }

    public int getCacheSize() {
        return cache.size();
    }

    public int getMaxCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    protected void doStart() throws Exception {
        if (cache == null) {
            cache = LRUCacheFactory.newLRUCache(cacheSize <= 0 ? MAX_CACHE_SIZE : cacheSize);
        }
    }

    @Override
    protected void doStop() throws Exception {
        cache.clear();
    }
}
