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
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Metadata(label = "routing,cloud,service-discovery")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class CachingServiceCallServiceDiscoveryConfiguration extends ServiceCallServiceDiscoveryConfiguration {
    @Metadata(defaultValue = "60", javaType = "java.lang.Integer")
    private String timeout = Integer.toString(60);
    @Metadata(javaType = "java.util.concurrent.TimeUnit", defaultValue = "SECONDS",
              enums = "NANOSECONDS,MICROSECONDS,MILLISECONDS,SECONDS,MINUTES,HOURS,DAYS")
    private String units = TimeUnit.SECONDS.name();

    private ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration;

    public CachingServiceCallServiceDiscoveryConfiguration() {
        this(null);
    }

    public CachingServiceCallServiceDiscoveryConfiguration(ServiceCallDefinition parent) {
        super(parent, "caching-service-discovery");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public String getTimeout() {
        return timeout;
    }

    /**
     * Set the time the services will be retained.
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getUnits() {
        return units;
    }

    /**
     * Set the time unit for the timeout.
     */
    public void setUnits(String units) {
        this.units = units;
    }

    public ServiceCallServiceDiscoveryConfiguration getServiceDiscoveryConfiguration() {
        return serviceDiscoveryConfiguration;
    }

    /**
     * Set the service-call configuration to use
     */
    public void setServiceDiscoveryConfiguration(ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        this.serviceDiscoveryConfiguration = serviceDiscoveryConfiguration;
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * Set the time the services will be retained.
     */
    public CachingServiceCallServiceDiscoveryConfiguration timeout(int timeout) {
        return timeout(Integer.toString(timeout));
    }

    /**
     * Set the time the services will be retained.
     */
    public CachingServiceCallServiceDiscoveryConfiguration timeout(String timeout) {
        setTimeout(timeout);
        return this;
    }

    /**
     * Set the time unit for the timeout.
     */
    public CachingServiceCallServiceDiscoveryConfiguration units(TimeUnit units) {
        return units(units.name());
    }

    /**
     * Set the time unit for the timeout.
     */
    public CachingServiceCallServiceDiscoveryConfiguration units(String units) {
        setUnits(units);
        return this;
    }

    /**
     * Set the service-call configuration to use
     */
    public CachingServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration(
            ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        setServiceDiscoveryConfiguration(serviceDiscoveryConfiguration);
        return this;
    }

    // *****************************
    // Shortcuts - ServiceDiscovery
    // *****************************

    public CachingServiceCallServiceDiscoveryConfiguration cachingServiceDiscovery() {
        CachingServiceCallServiceDiscoveryConfiguration conf = new CachingServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return serviceDiscoveryConfiguration(conf);
    }

    public ConsulServiceCallServiceDiscoveryConfiguration consulServiceDiscovery() {
        ConsulServiceCallServiceDiscoveryConfiguration conf = new ConsulServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public DnsServiceCallServiceDiscoveryConfiguration dnsServiceDiscovery() {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public CombinedServiceCallServiceDiscoveryConfiguration combinedServiceDiscovery() {
        CombinedServiceCallServiceDiscoveryConfiguration conf = new CombinedServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public StaticServiceCallServiceDiscoveryConfiguration staticServiceDiscovery() {
        StaticServiceCallServiceDiscoveryConfiguration conf = new StaticServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    // *************************************************************************
    // Utilities
    // *************************************************************************

    @Override
    protected void postProcessFactoryParameters(ZwangineContext zwangineContext, Map<String, Object> parameters) throws Exception {
        if (serviceDiscoveryConfiguration != null) {
            parameters.put("serviceDiscovery", serviceDiscoveryConfiguration.newInstance(zwangineContext));
        }
    }
}
