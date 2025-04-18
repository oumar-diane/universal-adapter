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

import org.zenithblox.spi.EndpointUtilizationStatistics;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DefaultEndpointUtilizationStatistics implements EndpointUtilizationStatistics {

    private final int maxCapacity;
    private final Map<String, Long> map;
    private final Lock lock = new ReentrantLock();

    public DefaultEndpointUtilizationStatistics(int maxCapacity) {
        this.map = LRUCacheFactory.newLRUCache(16, maxCapacity, false);
        this.maxCapacity = maxCapacity;
    }

    @Override
    public int maxCapacity() {
        return maxCapacity;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void onHit(String uri) {
        lock.lock();
        try {
            map.compute(uri, (key, current) -> {
                if (current == null) {
                    return 1L;
                } else {
                    return ++current;
                }
            });
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void remove(String uri) {
        map.remove(uri);
    }

    @Override
    public Map<String, Long> getStatistics() {
        return Collections.unmodifiableMap(map);
    }

    @Override
    public void clear() {
        map.clear();
    }
}
