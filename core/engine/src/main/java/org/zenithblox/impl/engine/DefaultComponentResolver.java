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
import org.zenithblox.Component;
import org.zenithblox.spi.ComponentResolver;
import org.zenithblox.spi.FactoryFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default implementation of {@link ComponentResolver} which tries to find components by using the URI scheme prefix
 * and searching for a file of the URI scheme name in the <b>META-INF/services/org/zentihblox/zwangine/component/</b> directory
 * on the classpath.
 */
public class DefaultComponentResolver implements ComponentResolver {

    public static final String RESOURCE_PATH = "META-INF/services.org.zenithblox/component/";

    private static final Logger LOG = LoggerFactory.getLogger(DefaultComponentResolver.class);

    private FactoryFinder factoryFinder;

    @Override
    public Component resolveComponent(String name, ZwangineContext context) {
        // not in registry then use component factory
        Class<?> type;
        try {
            type = findComponent(name, context);
            if (type == null) {
                // not found
                return null;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no Component registered for scheme: " + name, e);
        }

        if (getLog().isDebugEnabled()) {
            getLog().debug("Found component: {} via type: {} via: {}{}", name, type.getName(), factoryFinder.getResourcePath(),
                    name);
        }

        // create the component
        if (Component.class.isAssignableFrom(type)) {
            return (Component) context.getInjector().newInstance(type, false);
        } else {
            throw new IllegalArgumentException("Type is not a Component implementation. Found: " + type.getName());
        }
    }

    private Class<?> findComponent(String name, ZwangineContext context) {
        if (factoryFinder == null) {
            factoryFinder = context.getZwangineContextExtension().getFactoryFinder(RESOURCE_PATH);
        }
        return factoryFinder.findClass(name).orElse(null);
    }

    protected Logger getLog() {
        return LOG;
    }

}
