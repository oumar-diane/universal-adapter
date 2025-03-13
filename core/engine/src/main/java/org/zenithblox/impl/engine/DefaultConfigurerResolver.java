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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.spi.ConfigurerResolver;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default configurer resolver that looks for configurer factories in
 * <b>META-INF/services/org/zentihblox/zwangine/configurer/</b>.
 */
public class DefaultConfigurerResolver implements ConfigurerResolver {
    /**
     * This is a special container for the ZwangineContext because, with Zwangine 4, we split the ZwangineContext and the former
     * ExtendedZwangineContext. This holds them in a single configuration, directing the target appropriately
     */
    public static class ContextConfigurer implements PropertyConfigurer {
        private final PropertyConfigurer contextConfigurer;
        private final PropertyConfigurer extensionConfigurer;

        public ContextConfigurer(PropertyConfigurer contextConfigurer, PropertyConfigurer extensionConfigurer) {
            this.contextConfigurer = contextConfigurer;
            this.extensionConfigurer = extensionConfigurer;
        }

        @Override
        public boolean configure(ZwangineContext zwangineContext, Object target, String name, Object value, boolean ignoreCase) {
            if (target instanceof ZwangineContext contextTarget) {
                if (!contextConfigurer.configure(zwangineContext, contextTarget, name, value, ignoreCase)) {
                    return extensionConfigurer.configure(zwangineContext, contextTarget.getZwangineContextExtension(), name, value,
                            ignoreCase);
                }
            }

            return false;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(DefaultConfigurerResolver.class);

    protected FactoryFinder factoryFinder;

    public DefaultConfigurerResolver() {
    }

    public DefaultConfigurerResolver(FactoryFinder factoryFinder) {
        this.factoryFinder = factoryFinder;
    }

    @Override
    public PropertyConfigurer resolvePropertyConfigurer(String name, ZwangineContext context) {
        if (ObjectHelper.isEmpty(name)) {
            return null;
        }

        // lookup in registry first
        PropertyConfigurer configurer = context.getRegistry().lookupByNameAndType(name, PropertyConfigurer.class);
        if (configurer != null) {
            return configurer;
        }

        // clip -configurer from the name as that is not the name in META-INF
        if (name.endsWith("-configurer")) {
            name = name.substring(0, name.length() - 11);
        }

        // not in registry then use configurer factory
        Class<?> type;
        try {
            // fallback special for zwangine context itself as we have an extended configurer
            if (name.startsWith("org.zenithblox.") && name.contains("ZwangineContext") && !name.contains("Extension")) {
                type = findConfigurer(ZwangineContext.class.getName(), context);

                if (type != null) {
                    var extensionType = findConfigurer(ExtendedZwangineContext.class.getName(), context);

                    if (extensionType != null) {
                        return createPropertyConfigurerForContext(name, context, type, extensionType);
                    }
                }
                //
            } else {
                type = findConfigurer(name, context);
                if (type == null) {
                    return null;
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no Configurer registered for scheme: " + name, e);
        }

        return createPropertyConfigurer(name, context, type);
    }

    private PropertyConfigurer createPropertyConfigurer(String name, ZwangineContext context, Class<?> type) {
        if (getLog().isDebugEnabled()) {
            getLog().debug("Found configurer: {} via type: {} via: {}{}", name, type.getName(), factoryFinder.getResourcePath(),
                    name);
        }

        // create the component
        if (PropertyConfigurer.class.isAssignableFrom(type)) {
            return (PropertyConfigurer) context.getInjector().newInstance(type, false);
        } else {
            throw new IllegalArgumentException(
                    "Type is not a PropertyConfigurer implementation. Found: " + type.getName());
        }
    }

    private PropertyConfigurer createPropertyConfigurerForContext(
            String name, ZwangineContext context, Class<?> type, Class<?> extensionType) {
        if (getLog().isDebugEnabled()) {
            getLog().debug("Found configurer: {} via type: {} via: {}{}", name, type.getName(), factoryFinder.getResourcePath(),
                    name);
        }

        var contextConfigurer = (PropertyConfigurer) context.getInjector().newInstance(type, false);
        var extensionConfigurer = (PropertyConfigurer) context.getInjector().newInstance(extensionType, false);

        // create the component
        if (PropertyConfigurer.class.isAssignableFrom(type)) {
            return new ContextConfigurer(contextConfigurer, extensionConfigurer);
        } else {
            throw new IllegalArgumentException(
                    "Type is not a PropertyConfigurer implementation. Found: " + type.getName());
        }
    }

    private Class<?> findConfigurer(String name, ZwangineContext context) {
        if (factoryFinder == null) {
            factoryFinder = context.getZwangineContextExtension().getFactoryFinder(ConfigurerResolver.RESOURCE_PATH);
        }
        return factoryFinder.findClass(name).orElse(null);
    }

    protected Logger getLog() {
        return LOG;
    }
}
