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
import org.zenithblox.NoFactoryAvailableException;
import org.zenithblox.cloud.ServiceDiscovery;
import org.zenithblox.cloud.ServiceDiscoveryFactory;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.util.ObjectHelper;

import java.util.Map;
import java.util.Optional;

@Metadata(label = "routing,cloud,service-discovery")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class ServiceCallServiceDiscoveryConfiguration extends ServiceCallConfiguration implements ServiceDiscoveryFactory {
    private final ServiceCallDefinition parent;
    private final String factoryKey;

    public ServiceCallServiceDiscoveryConfiguration() {
        this(null, null);
    }

    public ServiceCallServiceDiscoveryConfiguration(ServiceCallDefinition parent, String factoryKey) {
        this.parent = parent;
        this.factoryKey = factoryKey;
    }

    public ServiceCallDefinition end() {
        return Optional.ofNullable(parent).orElseThrow(() -> new IllegalStateException("Parent definition is not set"));
    }

    public ProcessorDefinition<?> endParent() {
        return Optional.ofNullable(parent).map(ServiceCallDefinition::end)
                .orElseThrow(() -> new IllegalStateException("Parent definition is not set"));
    }

    // *************************************************************************
    //
    // *************************************************************************

    @Override
    public ServiceCallServiceDiscoveryConfiguration property(String key, String value) {
        return (ServiceCallServiceDiscoveryConfiguration) super.property(key, value);
    }

    // *************************************************************************
    // Factory
    // *************************************************************************

    @Override
    public ServiceDiscovery newInstance(ZwangineContext zwangineContext) throws Exception {
        ObjectHelper.notNull(factoryKey, "ServiceDiscovery factoryKey");

        ServiceDiscovery answer;

        // First try to find the factory from the registry.
        ServiceDiscoveryFactory factory = ZwangineContextHelper.lookup(zwangineContext, factoryKey, ServiceDiscoveryFactory.class);
        if (factory != null) {
            // If a factory is found in the registry do not re-configure it as
            // it should be pre-configured.
            answer = factory.newInstance(zwangineContext);
        } else {
            Class<?> type;
            try {
                // Then use Service factory.
                type = zwangineContext.getZwangineContextExtension()
                        .getFactoryFinder(ServiceCallDefinitionConstants.RESOURCE_PATH).findClass(factoryKey).orElse(null);
            } catch (Exception e) {
                throw new NoFactoryAvailableException(ServiceCallDefinitionConstants.RESOURCE_PATH + factoryKey, e);
            }

            if (type != null) {
                if (ServiceDiscoveryFactory.class.isAssignableFrom(type)) {
                    factory = (ServiceDiscoveryFactory) zwangineContext.getInjector().newInstance(type, false);
                } else {
                    throw new IllegalArgumentException(
                            "Resolving ServiceDiscovery: " + factoryKey
                                                       + " detected type conflict: Not a ServiceDiscoveryFactory implementation. Found: "
                                                       + type.getName());
                }
            }

            try {
                Map<String, Object> parameters = getConfiguredOptions(zwangineContext, this);

                parameters.replaceAll((k, v) -> {
                    if (v instanceof String str) {
                        try {
                            v = zwangineContext.resolvePropertyPlaceholders(str);
                        } catch (Exception e) {
                            throw new IllegalArgumentException(
                                    String.format("Exception while resolving %s (%s)", k, v), e);
                        }
                    }

                    return v;
                });

                if (factory != null) {
                    // Convert properties to Map<String, String>
                    Map<String, String> map = getPropertiesAsMap(zwangineContext);
                    if (map != null && !map.isEmpty()) {
                        parameters.put("properties", map);
                    }

                    postProcessFactoryParameters(zwangineContext, parameters);

                    PropertyBindingSupport.build().bind(zwangineContext, factory, parameters);

                    answer = factory.newInstance(zwangineContext);
                } else {
                    throw new IllegalStateException("factory is null");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        return answer;
    }

}
