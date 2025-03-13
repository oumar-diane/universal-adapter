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

import org.zenithblox.ExchangePattern;
import org.zenithblox.Expression;
import org.zenithblox.builder.ExpressionClause;
import org.zenithblox.cloud.ServiceChooser;
import org.zenithblox.cloud.ServiceDiscovery;
import org.zenithblox.cloud.ServiceFilter;
import org.zenithblox.cloud.ServiceLoadBalancer;
import org.zenithblox.model.NoOutputDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.language.RefExpression;
import org.zenithblox.spi.Metadata;

/**
 * To call remote services
 */
@Metadata(label = "eip,routing")
public class ServiceCallDefinition extends NoOutputDefinition<ServiceCallDefinition> {
    @Metadata(required = true)
    private String name;
    private String uri;
    @Metadata(defaultValue = ServiceCallDefinitionConstants.DEFAULT_COMPONENT)
    private String component;
    @Metadata(javaType = "org.zenithblox.ExchangePattern", enums = "InOnly,InOut")
    private String pattern;
    private String configurationRef;
    private String serviceDiscoveryRef;
    private ServiceDiscovery serviceDiscovery;
    private String serviceFilterRef;
    private ServiceFilter serviceFilter;
    private String serviceChooserRef;
    private ServiceChooser serviceChooser;
    private String loadBalancerRef;
    private ServiceLoadBalancer loadBalancer;
    private String expressionRef;
    private Expression expression;

    private ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration;

    private ServiceCallServiceFilterConfiguration serviceFilterConfiguration;

    private ServiceCallServiceLoadBalancerConfiguration loadBalancerConfiguration;

    private ServiceCallExpressionConfiguration expressionConfiguration;

    public ServiceCallDefinition() {
    }

    public ServiceCallDefinition(String name) {
        this.name = name;
    }

    @Override
    public ProcessorDefinition<?> copyDefinition() {
        // deprecated so we do not implement copy
        return this;
    }

    @Override
    public String toString() {
        return "ServiceCall[" + name + "]";
    }

    @Override
    public String getShortName() {
        return "serviceCall";
    }

    @Override
    public String getLabel() {
        return "serviceCall";
    }

    // *****************************
    // Properties
    // *****************************

    public String getName() {
        return name;
    }

    /**
     * Sets the name of the service to use
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getConfigurationRef() {
        return configurationRef;
    }

    /**
     * Refers to a ServiceCall configuration to use
     */
    public void setConfigurationRef(String configurationRef) {
        this.configurationRef = configurationRef;
    }

    public String getUri() {
        return uri;
    }

    /**
     * The uri of the endpoint to send to. The uri can be dynamic computed using the
     * {@link org.zenithblox.language.simple.SimpleLanguage} expression.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getComponent() {
        return component;
    }

    /**
     * The component to use.
     */
    public void setComponent(String component) {
        this.component = component;
    }

    public String getServiceDiscoveryRef() {
        return serviceDiscoveryRef;
    }

    /**
     * Sets a reference to a custom {@link ServiceDiscovery} to use.
     */
    public void setServiceDiscoveryRef(String serviceDiscoveryRef) {
        this.serviceDiscoveryRef = serviceDiscoveryRef;
    }

    public ServiceDiscovery getServiceDiscovery() {
        return serviceDiscovery;
    }

    /**
     * Sets a custom {@link ServiceDiscovery} to use.
     */
    public void setServiceDiscovery(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public String getServiceFilterRef() {
        return serviceFilterRef;
    }

    /**
     * Sets a reference to a custom {@link ServiceFilter} to use.
     */
    public void setServiceFilterRef(String serviceFilterRef) {
        this.serviceFilterRef = serviceFilterRef;
    }

    public ServiceFilter getServiceFilter() {
        return serviceFilter;
    }

    /**
     * Sets a custom {@link ServiceFilter} to use.
     */
    public void setServiceFilter(ServiceFilter serviceFilter) {
        this.serviceFilter = serviceFilter;
    }

    public String getServiceChooserRef() {
        return serviceChooserRef;
    }

    /**
     * Sets a reference to a custom {@link ServiceChooser} to use.
     */
    public void setServiceChooserRef(String serviceChooserRef) {
        this.serviceChooserRef = serviceChooserRef;
    }

    public ServiceChooser getServiceChooser() {
        return serviceChooser;
    }

    /**
     * Sets a custom {@link ServiceChooser} to use.
     */
    public void setServiceChooser(ServiceChooser serviceChooser) {
        this.serviceChooser = serviceChooser;
    }

    public String getLoadBalancerRef() {
        return loadBalancerRef;
    }

    /**
     * Sets a reference to a custom {@link ServiceLoadBalancer} to use.
     */
    public void setLoadBalancerRef(String loadBalancerRef) {
        this.loadBalancerRef = loadBalancerRef;
    }

    public ServiceLoadBalancer getLoadBalancer() {
        return loadBalancer;
    }

    /**
     * Sets a custom {@link ServiceLoadBalancer} to use.
     */
    public void setLoadBalancer(ServiceLoadBalancer loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public String getExpressionRef() {
        return expressionRef;
    }

    /**
     * Set a reference to a custom {@link Expression} to use.
     */
    public void setExpressionRef(String expressionRef) {
        if (this.expressionConfiguration == null) {
            this.expressionConfiguration = new ServiceCallExpressionConfiguration();
        }

        this.expressionConfiguration.expressionType(new RefExpression(expressionRef));
    }

    public Expression getExpression() {
        return expression;
    }

    /**
     * Set a custom {@link Expression} to use.
     */
    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public ServiceCallServiceDiscoveryConfiguration getServiceDiscoveryConfiguration() {
        return serviceDiscoveryConfiguration;
    }

    /**
     * Configures the ServiceDiscovery using the given configuration.
     */
    public void setServiceDiscoveryConfiguration(ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        this.serviceDiscoveryConfiguration = serviceDiscoveryConfiguration;
    }

    public ServiceCallServiceFilterConfiguration getServiceFilterConfiguration() {
        return serviceFilterConfiguration;
    }

    /**
     * Configures the ServiceFilter using the given configuration.
     */
    public void setServiceFilterConfiguration(ServiceCallServiceFilterConfiguration serviceFilterConfiguration) {
        this.serviceFilterConfiguration = serviceFilterConfiguration;
    }

    public ServiceCallServiceLoadBalancerConfiguration getLoadBalancerConfiguration() {
        return loadBalancerConfiguration;
    }

    /**
     * Configures the LoadBalancer using the given configuration.
     */
    public void setLoadBalancerConfiguration(ServiceCallServiceLoadBalancerConfiguration loadBalancerConfiguration) {
        this.loadBalancerConfiguration = loadBalancerConfiguration;
    }

    public ServiceCallExpressionConfiguration getExpressionConfiguration() {
        return expressionConfiguration;
    }

    /**
     * Configures the Expression using the given configuration.
     */
    public void setExpressionConfiguration(ServiceCallExpressionConfiguration expressionConfiguration) {
        this.expressionConfiguration = expressionConfiguration;
    }

    // *****************************
    // Fluent API
    // *****************************

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ServiceCallDefinition pattern(ExchangePattern pattern) {
        return pattern(pattern.name());
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ServiceCallDefinition pattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    /**
     * Sets the name of the service to use
     */
    public ServiceCallDefinition name(String name) {
        setName(name);
        return this;
    }

    /**
     * Sets the uri of the service to use
     */
    public ServiceCallDefinition uri(String uri) {
        setUri(uri);
        return this;
    }

    /**
     * Sets the component to use
     */
    public ServiceCallDefinition component(String component) {
        setComponent(component);
        return this;
    }

    /**
     * Refers to a ServiceCall configuration to use
     */
    public ServiceCallDefinition serviceCallConfiguration(String ref) {
        configurationRef = ref;
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceDiscovery} to use.
     */
    public ServiceCallDefinition serviceDiscovery(String serviceDiscoveryRef) {
        setServiceDiscoveryRef(serviceDiscoveryRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceDiscovery} to use.
     */
    public ServiceCallDefinition serviceDiscovery(ServiceDiscovery serviceDiscovery) {
        setServiceDiscovery(serviceDiscovery);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceFilter} to use.
     */
    public ServiceCallDefinition serviceFilter(String serviceFilterRef) {
        setServiceDiscoveryRef(serviceDiscoveryRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceFilter} to use.
     */
    public ServiceCallDefinition serviceFilter(ServiceFilter serviceFilter) {
        setServiceFilter(serviceFilter);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceChooser} to use.
     */
    public ServiceCallDefinition serviceChooser(String serviceChooserRef) {
        setServiceChooserRef(serviceChooserRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceChooser} to use.
     */
    public ServiceCallDefinition serviceChooser(ServiceChooser serviceChooser) {
        setServiceChooser(serviceChooser);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceLoadBalancer} to use.
     */
    public ServiceCallDefinition loadBalancer(String loadBalancerRef) {
        setLoadBalancerRef(loadBalancerRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceLoadBalancer} to use.
     */
    public ServiceCallDefinition loadBalancer(ServiceLoadBalancer loadBalancer) {
        setLoadBalancer(loadBalancer);
        return this;
    }

    /**
     * Sets a reference to a custom {@link Expression} to use.
     */
    public ServiceCallDefinition expression(String expressionRef) {
        setExpressionRef(expressionRef);
        return this;
    }

    /**
     * Sets a custom {@link Expression} to use.
     */
    public ServiceCallDefinition expression(Expression expression) {
        setExpression(expression);
        return this;
    }

    /**
     * Sets a custom {@link Expression} to use through an expression builder clause.
     *
     * @return a expression builder clause to set the body
     */
    public ExpressionClause<ServiceCallDefinition> expression() {
        ExpressionClause<ServiceCallDefinition> clause = new ExpressionClause<>(this);
        setExpression(clause);

        return clause;
    }

    /**
     * Configures the ServiceDiscovery using the given configuration.
     */
    public ServiceCallDefinition serviceDiscoveryConfiguration(
            ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        setServiceDiscoveryConfiguration(serviceDiscoveryConfiguration);
        return this;
    }

    /**
     * Configures the ServiceFilter using the given configuration.
     */
    public ServiceCallDefinition serviceFilterConfiguration(ServiceCallServiceFilterConfiguration serviceFilterConfiguration) {
        setServiceFilterConfiguration(serviceFilterConfiguration);
        return this;
    }

    /**
     * Configures the LoadBalancer using the given configuration.
     */
    public ServiceCallDefinition loadBalancerConfiguration(
            ServiceCallServiceLoadBalancerConfiguration loadBalancerConfiguration) {
        setLoadBalancerConfiguration(loadBalancerConfiguration);
        return this;
    }

    /**
     * Configures the Expression using the given configuration.
     */
    public ServiceCallDefinition expressionConfiguration(ServiceCallExpressionConfiguration expressionConfiguration) {
        setExpressionConfiguration(expressionConfiguration);
        return this;
    }

    // *****************************
    // Shortcuts - ServiceDiscovery
    // *****************************

    public CachingServiceCallServiceDiscoveryConfiguration cachingServiceDiscovery() {
        CachingServiceCallServiceDiscoveryConfiguration conf = new CachingServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ConsulServiceCallServiceDiscoveryConfiguration consulServiceDiscovery() {
        ConsulServiceCallServiceDiscoveryConfiguration conf = new ConsulServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallDefinition consulServiceDiscovery(String url) {
        ConsulServiceCallServiceDiscoveryConfiguration conf = new ConsulServiceCallServiceDiscoveryConfiguration(this);
        conf.setUrl(url);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public DnsServiceCallServiceDiscoveryConfiguration dnsServiceDiscovery() {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallDefinition dnsServiceDiscovery(String domain) {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration(this);
        conf.setDomain(domain);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public ServiceCallDefinition dnsServiceDiscovery(String domain, String protocol) {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration(this);
        conf.setDomain(domain);
        conf.setProto(protocol);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesClientServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration(this);
        conf.setLookup("client");

        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallDefinition kubernetesEnvServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration(this);
        conf.setLookup("environment");

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public ServiceCallDefinition kubernetesDnsServiceDiscovery(String namespace, String domain) {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration(this);
        conf.setLookup("dns");
        conf.setNamespace(namespace);
        conf.setDnsDomain(domain);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public CombinedServiceCallServiceDiscoveryConfiguration combinedServiceDiscovery() {
        CombinedServiceCallServiceDiscoveryConfiguration conf = new CombinedServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public StaticServiceCallServiceDiscoveryConfiguration staticServiceDiscovery() {
        StaticServiceCallServiceDiscoveryConfiguration conf = new StaticServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration zookeeperServiceDiscovery() {
        ZooKeeperServiceCallServiceDiscoveryConfiguration conf = new ZooKeeperServiceCallServiceDiscoveryConfiguration(this);
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallDefinition zookeeperServiceDiscovery(String nodes, String basePath) {
        ZooKeeperServiceCallServiceDiscoveryConfiguration conf = new ZooKeeperServiceCallServiceDiscoveryConfiguration(this);
        conf.setNodes(nodes);
        conf.setBasePath(basePath);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    // *****************************
    // Shortcuts - ServiceFilter
    // *****************************

    public ServiceCallDefinition healthyFilter() {
        HealthyServiceCallServiceFilterConfiguration conf = new HealthyServiceCallServiceFilterConfiguration(this);
        setServiceFilterConfiguration(conf);

        return this;
    }

    public ServiceCallDefinition passThroughFilter() {
        PassThroughServiceCallServiceFilterConfiguration conf = new PassThroughServiceCallServiceFilterConfiguration(this);
        setServiceFilterConfiguration(conf);

        return this;
    }

    public CombinedServiceCallServiceFilterConfiguration combinedFilter() {
        CombinedServiceCallServiceFilterConfiguration conf = new CombinedServiceCallServiceFilterConfiguration(this);
        setServiceFilterConfiguration(conf);

        return conf;
    }

    public BlacklistServiceCallServiceFilterConfiguration blacklistFilter() {
        BlacklistServiceCallServiceFilterConfiguration conf = new BlacklistServiceCallServiceFilterConfiguration();
        setServiceFilterConfiguration(conf);

        return conf;
    }

    public ServiceCallDefinition customFilter(String serviceFilter) {
        CustomServiceCallServiceFilterConfiguration conf = new CustomServiceCallServiceFilterConfiguration();
        conf.setServiceFilterRef(serviceFilter);

        setServiceFilterConfiguration(conf);

        return this;
    }

    public ServiceCallDefinition customFilter(ServiceFilter serviceFilter) {
        CustomServiceCallServiceFilterConfiguration conf = new CustomServiceCallServiceFilterConfiguration();
        conf.setServiceFilter(serviceFilter);

        setServiceFilterConfiguration(conf);

        return this;
    }

    // *****************************
    // Shortcuts - LoadBalancer
    // *****************************

    public ServiceCallDefinition defaultLoadBalancer() {
        DefaultServiceCallServiceLoadBalancerConfiguration conf = new DefaultServiceCallServiceLoadBalancerConfiguration();
        setLoadBalancerConfiguration(conf);

        return this;
    }
}
