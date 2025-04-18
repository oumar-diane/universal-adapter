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

import org.zenithblox.Ordered;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.util.OrderedProperties;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Properties;

public class RefPropertiesSource implements LocationPropertiesSource, Ordered {

    private final int order;
    private final PropertiesComponent propertiesComponent;
    private final PropertiesLocation location;

    public RefPropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        this(propertiesComponent, location, 200);
    }

    public RefPropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location, int order) {
        this.propertiesComponent = propertiesComponent;
        this.location = location;
        this.order = order;
    }

    @Override
    public String getName() {
        return "RefPropertiesSource[" + getLocation().getPath() + "]";
    }

    @Override
    public PropertiesLocation getLocation() {
        return location;
    }

    @Override
    public String getProperty(String name) {
        // this will lookup the property on-demand
        Properties properties = lookupPropertiesInRegistry(propertiesComponent, location);
        if (properties != null) {
            return properties.getProperty(name);
        } else {
            return null;
        }
    }

    protected Properties lookupPropertiesInRegistry(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        String path = location.getPath();
        Properties answer = null;

        Object obj = propertiesComponent.getZwangineContext().getRegistry().lookupByName(path);
        if (obj instanceof Properties) {
            answer = (Properties) obj;
        } else if (obj instanceof Map) {
            answer = new OrderedProperties();
            answer.putAll((Map<?, ?>) obj);
        } else if (!propertiesComponent.isIgnoreMissingLocation() && !location.isOptional()) {
            throw RuntimeZwangineException
                    .wrapRuntimeZwangineException(new FileNotFoundException("Properties " + path + " not found in registry"));
        }

        return answer;
    }

    @Override
    public int getOrder() {
        return order;
    }

}
