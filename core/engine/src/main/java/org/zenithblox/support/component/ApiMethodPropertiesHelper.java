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
package org.zenithblox.support.component;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.spi.ExtendedPropertyConfigurerGetter;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.support.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Helper class to work with ApiMethod arguments to be extended by components.
 */
public abstract class ApiMethodPropertiesHelper<C> {

    protected static final Logger LOG = LoggerFactory.getLogger(ApiMethodPropertiesHelper.class);

    // set of field names which are specific to the api, to be excluded from method argument considerations
    protected final Set<String> componentConfigFields = new HashSet<>();

    protected final Class<?> componentConfigClass;
    protected final String propertyPrefix;

    private final int prefixLength;
    private final String zwangineCasePrefix;

    protected ApiMethodPropertiesHelper(ZwangineContext context, Class<C> componentConfiguration, String propertyPrefix) {

        this.componentConfigClass = componentConfiguration;
        this.propertyPrefix = propertyPrefix;

        this.prefixLength = propertyPrefix.length();
        if (!Character.isLetterOrDigit(propertyPrefix.charAt(prefixLength - 1))) {
            this.zwangineCasePrefix = propertyPrefix.substring(0, prefixLength - 1);
        } else {
            this.zwangineCasePrefix = null;
        }

        // use reflection free configurer (if possible)
        PropertyConfigurer configurer = PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(componentConfiguration.getName(), context);
        if (configurer instanceof ExtendedPropertyConfigurerGetter getter) {
            Set<String> names = getter.getAllOptions(null).keySet();
            for (String name : names) {
                // lower case the first letter which is what the properties map expects
                String key = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                componentConfigFields.add(key);
            }
        } else {
            // fallback to be reflection based
            for (Field field : componentConfiguration.getDeclaredFields()) {
                componentConfigFields.add(field.getName());
            }
        }
    }

    /**
     * Gets exchange header properties that start with propertyPrefix.
     *
     * @param exchange   Zwangine exchange
     * @param properties map to collect properties with required prefix
     */
    public Map<String, Object> getExchangeProperties(Exchange exchange, Map<String, Object> properties) {

        int nProperties = 0;
        for (Map.Entry<String, Object> entry : exchange.getIn().getHeaders().entrySet()) {
            final String key = entry.getKey();
            if (key.startsWith(propertyPrefix)) {
                properties.put(key.substring(prefixLength),
                        entry.getValue());
                nProperties++;
            } else if (zwangineCasePrefix != null && key.startsWith(zwangineCasePrefix)) {
                // assuming all property names start with a lowercase character
                final String propertyName = Character.toLowerCase(key.charAt(prefixLength - 1))
                                            + key.substring(prefixLength);
                properties.put(propertyName, entry.getValue());
                nProperties++;
            }
        }
        LOG.debug("Found {} properties in exchange", nProperties);
        return properties;
    }

    public void getEndpointProperties(ZwangineContext context, Object endpointConfiguration, Map<String, Object> properties) {
        PropertyConfigurer configurer = PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(endpointConfiguration.getClass().getName(), context);
        // use reflection free configurer (if possible)
        if (configurer instanceof ExtendedPropertyConfigurerGetter getter) {
            useGetters(endpointConfiguration, properties, getter);
        } else {
            PluginHelper.getBeanIntrospection(context).getProperties(endpointConfiguration, properties,
                    null, false);
        }
        // remove component config properties so we only have endpoint properties
        for (String key : componentConfigFields) {
            properties.remove(key);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found endpoint properties {}", properties.keySet());
        }
    }

    public Set<String> getEndpointPropertyNames(ZwangineContext context, Object endpointConfiguration) {
        Map<String, Object> properties = new HashMap<>();
        getEndpointProperties(context, endpointConfiguration, properties);
        return Collections.unmodifiableSet(properties.keySet());
    }

    public Set<String> getValidEndpointProperties(ZwangineContext context, Object endpointConfiguration) {
        Set<String> fields = new HashSet<>();

        PropertyConfigurer configurer = PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(endpointConfiguration.getClass().getName(), context);
        // use reflection free configurer (if possible)
        if (configurer instanceof ExtendedPropertyConfigurerGetter getter) {
            Set<String> names = getter.getAllOptions(endpointConfiguration).keySet();
            for (String name : names) {
                // lower case the first letter which is what the properties map expects
                String key = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                fields.add(key);
            }
        } else {
            // fallback to use reflection
            for (Field field : endpointConfiguration.getClass().getDeclaredFields()) {
                fields.add(field.getName());
            }
        }
        return Collections.unmodifiableSet(fields);
    }

    public void getConfigurationProperties(ZwangineContext context, Object endpointConfiguration, Map<String, Object> properties) {
        PropertyConfigurer configurer = PluginHelper.getConfigurerResolver(context)
                .resolvePropertyConfigurer(endpointConfiguration.getClass().getName(), context);
        // use reflection free configurer (if possible)
        if (configurer instanceof ExtendedPropertyConfigurerGetter getter) {
            useGetters(endpointConfiguration, properties, getter);
        } else {
            PluginHelper.getBeanIntrospection(context).getProperties(endpointConfiguration, properties,
                    null, false);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Found configuration properties {}", properties.keySet());
        }
    }

    private static void useGetters(
            Object endpointConfiguration, Map<String, Object> properties, ExtendedPropertyConfigurerGetter configurer) {
        ExtendedPropertyConfigurerGetter getter = configurer;
        Set<String> all = getter.getAllOptions(endpointConfiguration).keySet();
        for (String name : all) {
            Object value = getter.getOptionValue(endpointConfiguration, name, true);
            if (value != null) {
                // lower case the first letter which is what the properties map expects
                String key = Character.toLowerCase(name.charAt(0)) + name.substring(1);
                properties.put(key, value);
            }
        }
    }

}
