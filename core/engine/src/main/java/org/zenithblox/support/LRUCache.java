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

import java.util.Map;

/**
 * A least-recently-used cache.
 */
public interface LRUCache<K, V> extends Map<K, V> {

    /**
     * Clears the cache
     */
    void cleanUp();

    /**
     * Reset usage statistics
     */
    void resetStatistics();

    /**
     * Gets the number of evicted elements
     */
    long getEvicted();

    /**
     * Gets the number of cache misses.
     */
    long getMisses();

    /**
     * Gets the number of cache hits.
     */
    long getHits();

    /**
     * Maximum cache capacity.
     */
    int getMaxCacheSize();

}
