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
import org.zenithblox.Component;
import org.zenithblox.spi.RestProducerFactory;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Helper for creating configured {@link Component}s used by the {@link RestProducerFactory} contract.
 *
 * When {@link RestProducerFactory} contract is used it could instantiate, start and register the underlying component.
 * During this process we have no way of configuring component properties, most notably the SSL properties.
 */
public final class RestProducerFactoryHelper {

    private static final Logger LOG = LoggerFactory.getLogger(RestProducerFactoryHelper.class);

    private RestProducerFactoryHelper() {
        // helper class
    }

    public static void setupComponentFor(
            final String url, final ZwangineContext zwangineContext,
            final Map<String, Object> componentProperties) {

        final String scheme = StringHelper.before(url, ":");
        setupComponent(scheme, zwangineContext, componentProperties);
    }

    public static Component setupComponent(
            final String componentName, final ZwangineContext zwangineContext,
            final Map<String, Object> componentProperties) {

        if (componentName == null) {
            return null;
        }
        if (componentProperties == null || componentProperties.isEmpty()) {
            return zwangineContext.getComponent(componentName);
        }

        final Component existing = zwangineContext.getComponent(componentName, false, false);
        if (existing != null) {
            if (!componentProperties.isEmpty()) {
                LOG.warn(
                        "Found existing `{}` component already present in the Zwangine context. Not setting component"
                         + " properties on the existing component. You can either prevent the component creation or"
                         + " set the given properties on the component. Component properties given: {}",
                        componentName, componentProperties);
            }

            return existing;
        }

        // component was not added to the context we can configure it
        final Component newlyCreated = zwangineContext.getComponent(componentName, true, false);
        if (newlyCreated == null) {
            throw new IllegalArgumentException(
                    "Cannot find component with name " + componentName
                                               + ". Make sure you have the component on the classpath");
        }
        PropertyBindingSupport.build().withRemoveParameters(false).withIgnoreCase(true)
                .withConfigurer(newlyCreated.getComponentPropertyConfigurer())
                .bind(zwangineContext, newlyCreated, componentProperties);
        ServiceHelper.startService(newlyCreated);

        return newlyCreated;
    }
}
