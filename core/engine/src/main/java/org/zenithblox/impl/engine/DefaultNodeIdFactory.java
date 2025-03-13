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

import org.zenithblox.NamedNode;
import org.zenithblox.spi.NodeIdFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Default id factory.
 */
public class DefaultNodeIdFactory implements NodeIdFactory {

    protected static final Map<String, AtomicInteger> NODE_COUNTERS = new ConcurrentHashMap<>();

    @Override
    public String createId(NamedNode definition) {
        String key = definition.getShortName();
        return key + getNodeCounter(key).incrementAndGet();
    }

    /**
     * Returns the counter for the given node key, lazily creating one if necessary
     */
    protected static AtomicInteger getNodeCounter(String key) {
        return NODE_COUNTERS.computeIfAbsent(key, k -> new AtomicInteger());
    }

    /**
     * Helper method for test purposes that allows tests to start clean (made protected to ensure that it is not called
     * accidentally)
     */
    protected static void resetAllCounters() {
        for (AtomicInteger counter : NODE_COUNTERS.values()) {
            counter.set(0);
        }
    }
}
