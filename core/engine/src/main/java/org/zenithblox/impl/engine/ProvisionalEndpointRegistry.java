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

import org.zenithblox.Endpoint;
import org.zenithblox.spi.EndpointRegistry;
import org.zenithblox.spi.NormalizedEndpointUri;
import org.zenithblox.support.LRUCacheFactory;

import java.util.*;

/**
 * A provisional (temporary) {@link EndpointRegistry} that is only used during startup of  Zwangine to make starting
 * Zwangine faster while {@link LRUCacheFactory} is warming up etc.
 */
class ProvisionalEndpointRegistry extends HashMap<NormalizedEndpointUri, Endpoint>
        implements EndpointRegistry {

    @Override
    public void start() {
        // noop
    }

    @Override
    public void stop() {
        // noop
    }

    @Override
    public int staticSize() {
        return 0;
    }

    @Override
    public int dynamicSize() {
        return 0;
    }

    @Override
    public int getMaximumCacheSize() {
        return 0;
    }

    @Override
    public void purge() {
        // noop
    }

    @Override
    public boolean isStatic(String key) {
        return false;
    }

    @Override
    public boolean isDynamic(String key) {
        return false;
    }

    @Override
    public void cleanUp() {
        // noop
    }

    @Override
    public Collection<Endpoint> getReadOnlyValues() {
        if (isEmpty()) {
            return Collections.emptyList();
        }

        // we want to avoid any kind of locking in get/put methods
        // as getReadOnlyValues is only seldom used, such as when zwangine-mock
        // is asserting endpoints at end of testing
        // so this code will then just retry in case of a concurrency update
        Collection<Endpoint> answer = new ArrayList<>();
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

    @Override
    public Map<String, Endpoint> getReadOnlyMap() {
        if (isEmpty()) {
            return Collections.emptyMap();
        }

        // we want to avoid any kind of locking in get/put methods
        // as getReadOnlyValues is only seldom used, such as when zwangine-mock
        // is asserting endpoints at end of testing
        // so this code will then just retry in case of a concurrency update
        Map<String, Endpoint> answer = new LinkedHashMap<>();
        boolean done = false;
        while (!done) {
            try {
                for (Entry<NormalizedEndpointUri, Endpoint> entry : entrySet()) {
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
}
