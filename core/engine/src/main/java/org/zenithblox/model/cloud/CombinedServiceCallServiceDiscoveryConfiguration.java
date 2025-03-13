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
import org.zenithblox.cloud.ServiceDiscovery;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Metadata(label = "routing,cloud,service-discovery")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class CombinedServiceCallServiceDiscoveryConfiguration extends ServiceCallServiceDiscoveryConfiguration {
    private List<ServiceCallServiceDiscoveryConfiguration> serviceDiscoveryConfigurations;

    public CombinedServiceCallServiceDiscoveryConfiguration() {
        this(null);
    }

    public CombinedServiceCallServiceDiscoveryConfiguration(ServiceCallDefinition parent) {
        super(parent, "combined-service-discovery");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public List<ServiceCallServiceDiscoveryConfiguration> getServiceDiscoveryConfigurations() {
        return serviceDiscoveryConfigurations;
    }

    /**
     * List of ServiceDiscovery configuration to use
     *
     * @param serviceDiscoveryConfigurations
     */
    public void setServiceDiscoveryConfigurations(
            List<ServiceCallServiceDiscoveryConfiguration> serviceDiscoveryConfigurations) {
        this.serviceDiscoveryConfigurations = serviceDiscoveryConfigurations;
    }

    /**
     * Add a ServiceDiscovery configuration
     */
    public void addServiceDiscoveryConfigurations(ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        if (serviceDiscoveryConfigurations == null) {
            serviceDiscoveryConfigurations = new ArrayList<>();
        }

        serviceDiscoveryConfigurations.add(serviceDiscoveryConfiguration);
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * List of ServiceDiscovery configuration to use
     */
    public CombinedServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfigurations(
            List<ServiceCallServiceDiscoveryConfiguration> serviceDiscoveryConfigurations) {
        setServiceDiscoveryConfigurations(serviceDiscoveryConfigurations);
        return this;
    }

    /**
     * Add a ServiceDiscovery configuration
     */
    public CombinedServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration(
            ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        addServiceDiscoveryConfigurations(serviceDiscoveryConfiguration);
        return this;
    }

    // *****************************
    // Shortcuts - ServiceDiscovery
    // *****************************

    public CachingServiceCallServiceDiscoveryConfiguration cachingServiceDiscovery() {
        CachingServiceCallServiceDiscoveryConfiguration conf = new CachingServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    public ConsulServiceCallServiceDiscoveryConfiguration consulServiceDiscovery() {
        ConsulServiceCallServiceDiscoveryConfiguration conf = new ConsulServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    public DnsServiceCallServiceDiscoveryConfiguration dnsServiceDiscovery() {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    public CombinedServiceCallServiceDiscoveryConfiguration combinedServiceDiscovery() {
        CombinedServiceCallServiceDiscoveryConfiguration conf = new CombinedServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    public StaticServiceCallServiceDiscoveryConfiguration staticServiceDiscovery() {
        StaticServiceCallServiceDiscoveryConfiguration conf = new StaticServiceCallServiceDiscoveryConfiguration();
        addServiceDiscoveryConfigurations(conf);

        return conf;
    }

    // *************************************************************************
    // Utilities
    // *************************************************************************

    @Override
    protected void postProcessFactoryParameters(final ZwangineContext zwangineContext, final Map<String, Object> parameters)
            throws Exception {
        if (serviceDiscoveryConfigurations != null && !serviceDiscoveryConfigurations.isEmpty()) {
            List<ServiceDiscovery> discoveries = new ArrayList<>(serviceDiscoveryConfigurations.size());
            for (ServiceCallServiceDiscoveryConfiguration conf : serviceDiscoveryConfigurations) {
                discoveries.add(conf.newInstance(zwangineContext));
            }

            parameters.put("serviceDiscoveryList", discoveries);
        }
    }
}
