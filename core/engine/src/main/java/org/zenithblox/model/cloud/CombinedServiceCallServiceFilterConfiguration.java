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
import org.zenithblox.cloud.ServiceFilter;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Metadata(label = "routing,cloud,service-filter")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class CombinedServiceCallServiceFilterConfiguration extends ServiceCallServiceFilterConfiguration {
    private List<ServiceCallServiceFilterConfiguration> serviceFilterConfigurations;

    public CombinedServiceCallServiceFilterConfiguration() {
        this(null);
    }

    public CombinedServiceCallServiceFilterConfiguration(ServiceCallDefinition parent) {
        super(parent, "combined-service-filter");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public List<ServiceCallServiceFilterConfiguration> getServiceFilterConfigurations() {
        return serviceFilterConfigurations;
    }

    /**
     * List of ServiceFilter configuration to use
     *
     * @param serviceFilterConfigurations
     */
    public void setServiceFilterConfigurations(List<ServiceCallServiceFilterConfiguration> serviceFilterConfigurations) {
        this.serviceFilterConfigurations = serviceFilterConfigurations;
    }

    /**
     * Add a ServiceFilter configuration
     */
    public void addServiceFilterConfiguration(ServiceCallServiceFilterConfiguration serviceFilterConfiguration) {
        if (serviceFilterConfigurations == null) {
            serviceFilterConfigurations = new ArrayList<>();
        }

        serviceFilterConfigurations.add(serviceFilterConfiguration);
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * List of ServiceFilter configuration to use
     */
    public CombinedServiceCallServiceFilterConfiguration serviceFilterConfigurations(
            List<ServiceCallServiceFilterConfiguration> serviceFilterConfigurations) {
        setServiceFilterConfigurations(serviceFilterConfigurations);
        return this;
    }

    /**
     * Add a ServiceFilter configuration
     */
    public CombinedServiceCallServiceFilterConfiguration serviceFilterConfiguration(
            ServiceCallServiceFilterConfiguration serviceFilterConfiguration) {
        addServiceFilterConfiguration(serviceFilterConfiguration);
        return this;
    }

    // *****************************
    // Shortcuts - ServiceFilter
    // *****************************

    public CombinedServiceCallServiceFilterConfiguration healthy() {
        addServiceFilterConfiguration(new HealthyServiceCallServiceFilterConfiguration());
        return this;
    }

    public CombinedServiceCallServiceFilterConfiguration passThrough() {
        addServiceFilterConfiguration(new PassThroughServiceCallServiceFilterConfiguration());
        return this;
    }

    public CombinedServiceCallServiceFilterConfiguration custom(String serviceFilter) {
        addServiceFilterConfiguration(new CustomServiceCallServiceFilterConfiguration().serviceFilter(serviceFilter));
        return this;
    }

    public CombinedServiceCallServiceFilterConfiguration custom(ServiceFilter serviceFilter) {
        addServiceFilterConfiguration(new CustomServiceCallServiceFilterConfiguration().serviceFilter(serviceFilter));
        return this;
    }

    // *************************************************************************
    // Utilities
    // *************************************************************************

    @Override
    protected void postProcessFactoryParameters(final ZwangineContext zwangineContext, final Map<String, Object> parameters)
            throws Exception {
        if (serviceFilterConfigurations != null && !serviceFilterConfigurations.isEmpty()) {
            List<ServiceFilter> discoveries = new ArrayList<>(serviceFilterConfigurations.size());
            for (ServiceCallServiceFilterConfiguration conf : serviceFilterConfigurations) {
                discoveries.add(conf.newInstance(zwangineContext));
            }

            parameters.put("serviceFilterList", discoveries);
        }
    }
}
