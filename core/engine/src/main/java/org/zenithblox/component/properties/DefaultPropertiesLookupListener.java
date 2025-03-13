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
package org.zenithblox.component.properties;

import org.zenithblox.PropertiesLookupListener;
import org.zenithblox.spi.PropertiesResolvedValue;
import org.zenithblox.support.LRUCacheFactory;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Map;

/**
 * A {@link PropertiesLookupListener} listener that captures the resolved properties for dev consoles, management and
 * troubleshooting purposes.
 */
public class DefaultPropertiesLookupListener extends ServiceSupport implements PropertiesLookupListener {

    private Map<String, PropertiesResolvedValue> properties;

    @Override
    public void onLookup(String name, String value, String defaultValue, String source) {
        properties.put(name, new PropertiesResolvedValue(name, value, value, defaultValue, source));
    }

    void updateValue(String name, String newValue, String newSource) {
        var p = properties.get(name);
        if (p != null) {
            String source = newSource != null ? newSource : p.source();
            properties.put(name, new PropertiesResolvedValue(p.name(), p.originalValue(), newValue, p.defaultValue(), source));
        }
    }

    public PropertiesResolvedValue getProperty(String key) {
        return properties.get(key);
    }

    @Override
    protected void doBuild() throws Exception {
        // use a cache with max limit to avoid capturing endless property values
        // if there are a lot of dynamic values
        properties = LRUCacheFactory.newLRUCache(1000);
    }

    @Override
    protected void doShutdown() throws Exception {
        properties.clear();
    }
}
