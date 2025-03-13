/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.model.cloud;

import org.zenithblox.ZwangineContext;
import org.zenithblox.model.IdentifiedType;
import org.zenithblox.model.PropertyDefinition;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.ExtendedPropertyConfigurerGetter;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;

import java.util.*;

@Configurer
@Deprecated(since = "3.19.0")
public abstract class ServiceCallConfiguration extends IdentifiedType {
    @Metadata(label = "advanced")
    protected List<PropertyDefinition> properties;

    // *************************************************************************
    //
    // *************************************************************************

    public List<PropertyDefinition> getProperties() {
        return properties;
    }

    /**
     * Set client properties to use.
     * <p/>
     * These properties are specific to what service call implementation are in use. For example if using a different
     * one, then the client properties are defined according to the specific service in use.
     */
    public void setProperties(List<PropertyDefinition> properties) {
        this.properties = properties;
    }

    /**
     * Adds a custom property to use.
     * <p/>
     * These properties are specific to what service call implementation are in use. For example if using a different
     * one, then the client properties are defined according to the specific service in use.
     */
    public ServiceCallConfiguration property(String key, String value) {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        PropertyDefinition prop = new PropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);
        properties.add(prop);
        return this;
    }

    protected Map<String, String> getPropertiesAsMap(ZwangineContext zwangineContext) throws Exception {
        Map<String, String> answer;

        if (properties == null || properties.isEmpty()) {
            answer = Collections.emptyMap();
        } else {
            answer = new HashMap<>();
            for (PropertyDefinition prop : properties) {
                // support property placeholders
                String key = ZwangineContextHelper.parseText(zwangineContext, prop.getKey());
                String value = ZwangineContextHelper.parseText(zwangineContext, prop.getValue());
                answer.put(key, value);
            }
        }

        return answer;
    }

    protected Map<String, Object> getConfiguredOptions(ZwangineContext context, Object target) {
        Map<String, Object> answer = new HashMap<>();

        PropertyConfigurer configurer = PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(target.getClass().getName(), context);
        // use reflection free configurer (if possible)
        if (configurer instanceof ExtendedPropertyConfigurerGetter getter) {
            Set<String> all = getter.getAllOptions(target).keySet();
            for (String name : all) {
                Object value = getter.getOptionValue(target, name, true);
                if (value != null) {
                    // lower case the first letter which is what the properties map expects
                    String key = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                    answer.put(key, value);
                }
            }
        } else {
            PluginHelper.getBeanIntrospection(context).getProperties(target, answer,
                    null, false);
        }

        return answer;
    }

    // *************************************************************************
    // Utilities
    // *************************************************************************

    protected void postProcessFactoryParameters(ZwangineContext zwangineContext, Map<String, Object> parameters) throws Exception {
    }
}
