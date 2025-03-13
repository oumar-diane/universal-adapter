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
import org.zenithblox.spi.VariableRepository;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Workflow {@link VariableRepository} which stores variables in-memory per workflow.
 */
public final class WorkflowVariableRepository extends ServiceSupport implements BrowsableVariableRepository, ZwangineContextAware {

    private final Map<String, Map<String, Object>> workflows = new ConcurrentHashMap<>();
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
        String id = StringHelper.before(name, ":");
        String key = StringHelper.after(name, ":");
        if (id == null || key == null) {
            throw new IllegalArgumentException("Name must be workflowId:name syntax");
        }
        Object answer = null;
        Map<String, Object> variables = workflows.get(id);
        if (variables != null) {
            answer = variables.get(key);
        }
        if (answer instanceof StreamCache sc) {
            // reset so the cache is ready to be used as a variable
            sc.reset();
        }
        return answer;
    }

    @Override
    public void setVariable(String name, Object value) {
        String id = StringHelper.before(name, ":");
        String key = StringHelper.after(name, ":");
        if (id == null || key == null) {
            throw new IllegalArgumentException("Name must be workflowId:name syntax");
        }

        if (value != null && strategy != null) {
            StreamCache sc = convertToStreamCache(value);
            if (sc != null) {
                value = sc;
            }
        }
        if (value != null) {
            Map<String, Object> variables = workflows.computeIfAbsent(id, s -> new ConcurrentHashMap<>(8));
            // avoid the NullPointException
            variables.put(key, value);
        } else {
            // if the value is null, we just remove the key from the map
            Map<String, Object> variables = workflows.get(id);
            if (variables != null) {
                variables.remove(key);
            }
        }
    }

    public boolean hasVariables() {
        for (var vars : workflows.values()) {
            if (!vars.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public int size() {
        int size = 0;
        for (var vars : workflows.values()) {
            size += vars.size();
        }
        return size;
    }

    public Stream<String> names() {
        List<String> answer = new ArrayList<>();
        for (Entry<String, Map<String, Object>> entry : workflows.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> values = entry.getValue();
            for (var e : values.entrySet()) {
                answer.add(id + ":" + e.getKey());
            }
        }
        return answer.stream();
    }

    public Map<String, Object> getVariables() {
        Map<String, Object> answer = new ConcurrentHashMap<>();
        for (Entry<String, Map<String, Object>> entry : workflows.entrySet()) {
            String id = entry.getKey();
            Map<String, Object> values = entry.getValue();
            for (var e : values.entrySet()) {
                answer.put(id + ":" + e.getKey(), e.getValue());
            }
        }
        return answer;
    }

    public void clear() {
        workflows.clear();
    }

    @Override
    public String getId() {
        return "workflow";
    }

    @Override
    public Object removeVariable(String name) {
        String id = StringHelper.before(name, ":");
        String key = StringHelper.after(name, ":");
        if (id == null || key == null) {
            throw new IllegalArgumentException("Name must be workflowId:name syntax");
        }

        Map<String, Object> variables = workflows.get(id);
        if (variables != null) {
            if ("*".equals(key)) {
                variables.clear();
                workflows.remove(id);
                return null;
            } else {
                return variables.remove(key);
            }
        }
        return null;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        if (zwangineContext != null && zwangineContext.isStreamCaching()) {
            strategy = zwangineContext.getStreamCachingStrategy();
        }
    }

    private StreamCache convertToStreamCache(Object body) {
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

    private StreamCache tryStreamCache(Object body) {
        try {
            // cache the body and if we could do that replace it as the new body
            return strategy.cache(body);
        } catch (Exception e) {
            throw new StreamCacheException(body, e);
        }
    }

}
