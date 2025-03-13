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
import org.zenithblox.Exchange;
import org.zenithblox.spi.VariableRepository;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.CaseInsensitiveMap;
import org.zenithblox.util.StringHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link VariableRepository} which is local per {@link Exchange} to hold request-scoped variables.
 */
final class ExchangeVariableRepository extends AbstractVariableRepository {

    private final Map<String, Object> headers = new ConcurrentHashMap<>(8);

    public ExchangeVariableRepository(ZwangineContext zwangineContext) {
        setZwangineContext(zwangineContext);
        // ensure its started
        ServiceHelper.startService(this);
    }

    void copyFrom(ExchangeVariableRepository source) {
        setVariables(source.getVariables());
        this.headers.putAll(source.headers);
    }

    @Override
    public Object getVariable(String name) {
        String id = StringHelper.before(name, ":");
        if ("header".equals(id)) {
            String prefix = StringHelper.after(name, ":");
            if (prefix == null || prefix.isBlank()) {
                throw new IllegalArgumentException("Variable " + name + " must have header key");
            }
            if (!prefix.contains(".")) {
                prefix = prefix + ".";
                // we want all headers for a given variable
                Map<String, Object> map = new CaseInsensitiveMap();
                for (Map.Entry<String, Object> entry : headers.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith(prefix)) {
                        key = StringHelper.after(key, prefix);
                        map.put(key, entry.getValue());
                    }
                }
                return map;
            } else {
                return headers.get(prefix);
            }
        }
        return super.getVariable(name);
    }

    @Override
    public void setVariable(String name, Object value) {
        String id = StringHelper.before(name, ":");
        if ("header".equals(id)) {
            name = StringHelper.after(name, ":");
            if (value != null) {
                // avoid the NullPointException
                headers.put(name, value);
            } else {
                // if the value is null, we just remove the key from the map
                headers.remove(name);
            }
        } else {
            super.setVariable(name, value);
        }
    }

    @Override
    public Object removeVariable(String name) {
        String id = StringHelper.before(name, ":");
        if ("header".equals(id)) {
            name = StringHelper.after(name, ":");
            return headers.remove(name);
        }
        return super.removeVariable(name);
    }

    @Override
    public void clear() {
        super.clear();
        headers.clear();
    }

    @Override
    public String getId() {
        return "exchange";
    }

}
