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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.StreamCache;
import org.zenithblox.StreamCacheException;
import org.zenithblox.spi.BrowsableVariableRepository;
import org.zenithblox.spi.StreamCachingStrategy;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Base class for {@link org.zenithblox.spi.VariableRepository} implementations that store variables in memory.
 */
public abstract class AbstractVariableRepository extends ServiceSupport
        implements BrowsableVariableRepository, ZwangineContextAware {

    private final Map<String, Object> variables = new ConcurrentHashMap<>(8);
    private ZwangineContext zwangineContext;
    private StreamCachingStrategy strategy;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public Object getVariable(String name) {
        Object answer = variables.get(name);
        if (answer instanceof StreamCache sc) {
            // reset so the cache is ready to be used as a variable
            sc.reset();
        }
        return answer;
    }

    @Override
    public void setVariable(String name, Object value) {
        if (value != null && strategy != null) {
            StreamCache sc = convertToStreamCache(value);
            if (sc != null) {
                value = sc;
            }
        }
        if (value != null) {
            // avoid the NullPointException
            variables.put(name, value);
        } else {
            // if the value is null, we just remove the key from the map
            variables.remove(name);
        }
    }

    public boolean hasVariables() {
        return !variables.isEmpty();
    }

    public int size() {
        return variables.size();
    }

    public Stream<String> names() {
        return variables.keySet().stream();
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> map) {
        variables.putAll(map);
    }

    public void clear() {
        variables.clear();
    }

    @Override
    public Object removeVariable(String name) {
        if (!hasVariables()) {
            return null;
        }
        return variables.remove(name);
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        if (zwangineContext != null && zwangineContext.isStreamCaching()) {
            strategy = zwangineContext.getStreamCachingStrategy();
        }
    }

    protected StreamCache convertToStreamCache(Object body) {
        // check if body is already cached
        if (body == null) {
            return null;
        } else if (body instanceof StreamCache sc) {
            // reset so the cache is ready to be used before processing
            sc.reset();
            return sc;
        }
        return tryStreamCache(body);
    }

    protected StreamCache tryStreamCache(Object body) {
        try {
            // cache the body and if we could do that replace it as the new body
            return strategy.cache(body);
        } catch (Exception e) {
            throw new StreamCacheException(body, e);
        }
    }

}
