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
import org.zenithblox.model.IdentifiedType;
import org.zenithblox.spi.Metadata;

/**
 * Remote service call configuration
 */
@Metadata(label = "routing,cloud")
@Deprecated(since = "3.19.0")
public class ServiceCallConfigurationDefinition extends IdentifiedType {
    private String uri;
    @Metadata(defaultValue = ServiceCallDefinitionConstants.DEFAULT_COMPONENT)
    private String component;
    @Metadata(javaType = "org.zenithblox.ExchangePattern", enums = "InOnly,InOut")
    private String pattern;
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

    public ServiceCallConfigurationDefinition() {
    }

    // *****************************
    // Properties
    // *****************************

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getUri() {
        return uri;
    }

    /**
     * The uri of the endpoint to send to. The uri can be dynamic computed using the simple language expression.
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
        this.expressionRef = expressionRef;
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
     * Configures theL oadBalancer using the given configuration.
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
     * Sets the default Zwangine component to use for calling the remote service.
     * <p/>
     * By default the http component is used. You can configure this to use <tt>netty-http</tt>, <tt>jetty</tt>,
     * <tt>undertow</tt> or some other components of choice. If the service is not HTTP protocol you can use other
     * components such as <tt>mqtt</tt>, <tt>jms</tt>, <tt>amqp</tt> etc.
     * <p/>
     * If the service call has been configured using an uri, then the component from the uri is used instead of this
     * default component.
     */
    public ServiceCallConfigurationDefinition component(String component) {
        setComponent(component);
        return this;
    }

    /**
     * Sets the uri of the service to use
     */
    public ServiceCallConfigurationDefinition uri(String uri) {
        setUri(uri);
        return this;
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ServiceCallConfigurationDefinition pattern(ExchangePattern pattern) {
        return pattern(pattern.name());
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ServiceCallConfigurationDefinition pattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceDiscovery} to use.
     */
    public ServiceCallConfigurationDefinition serviceDiscovery(String serviceDiscoveryRef) {
        setServiceDiscoveryRef(serviceDiscoveryRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceDiscovery} to use.
     */
    public ServiceCallConfigurationDefinition serviceDiscovery(ServiceDiscovery serviceDiscovery) {
        setServiceDiscovery(serviceDiscovery);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceFilter} to use.
     */
    public ServiceCallConfigurationDefinition serviceFilter(String serviceFilterRef) {
        setServiceDiscoveryRef(serviceDiscoveryRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceFilter} to use.
     */
    public ServiceCallConfigurationDefinition serviceFilter(ServiceFilter serviceFilter) {
        setServiceFilter(serviceFilter);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceChooser} to use.
     */
    public ServiceCallConfigurationDefinition serviceChooser(String serviceChooserRef) {
        setServiceChooserRef(serviceChooserRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceChooser} to use.
     */
    public ServiceCallConfigurationDefinition serviceChooser(ServiceChooser serviceChooser) {
        setServiceChooser(serviceChooser);
        return this;
    }

    /**
     * Sets a reference to a custom {@link ServiceLoadBalancer} to use.
     */
    public ServiceCallConfigurationDefinition loadBalancer(String loadBalancerRef) {
        setLoadBalancerRef(loadBalancerRef);
        return this;
    }

    /**
     * Sets a custom {@link ServiceLoadBalancer} to use.
     */
    public ServiceCallConfigurationDefinition loadBalancer(ServiceLoadBalancer loadBalancer) {
        setLoadBalancer(loadBalancer);
        return this;
    }

    /**
     * Sets a reference to a custom {@link Expression} to use.
     */
    public ServiceCallConfigurationDefinition expression(String expressionRef) {
        setExpressionRef(loadBalancerRef);
        return this;
    }

    /**
     * Sets a custom {@link Expression} to use.
     */
    public ServiceCallConfigurationDefinition expression(Expression expression) {
        setExpression(expression);
        return this;
    }

    /**
     * Sets a custom {@link Expression} to use through an expression builder clause.
     *
     * @return a expression builder clause to set the body
     */
    public ExpressionClause<ServiceCallConfigurationDefinition> expression() {
        ExpressionClause<ServiceCallConfigurationDefinition> clause = new ExpressionClause<>(this);
        setExpression(clause);

        return clause;
    }

    /**
     * Configures the ServiceDiscovery using the given configuration.
     */
    public ServiceCallConfigurationDefinition serviceDiscoveryConfiguration(
            ServiceCallServiceDiscoveryConfiguration serviceDiscoveryConfiguration) {
        setServiceDiscoveryConfiguration(serviceDiscoveryConfiguration);
        return this;
    }

    /**
     * Configures the ServiceFilter using the given configuration.
     */
    public ServiceCallConfigurationDefinition serviceFilterConfiguration(
            ServiceCallServiceFilterConfiguration serviceFilterConfiguration) {
        setServiceFilterConfiguration(serviceFilterConfiguration);
        return this;
    }

    /**
     * Configures the LoadBalancer using the given configuration.
     */
    public ServiceCallConfigurationDefinition loadBalancerConfiguration(
            ServiceCallServiceLoadBalancerConfiguration loadBalancerConfiguration) {
        setLoadBalancerConfiguration(loadBalancerConfiguration);
        return this;
    }

    /**
     * Configures the Expression using the given configuration.
     */
    public ServiceCallConfigurationDefinition expressionConfiguration(
            ServiceCallExpressionConfiguration expressionConfiguration) {
        setExpressionConfiguration(expressionConfiguration);
        return this;
    }

    // *****************************
    // Shortcuts - ServiceDiscovery
    // *****************************

    public CachingServiceCallServiceDiscoveryConfiguration cachingServiceDiscovery() {
        CachingServiceCallServiceDiscoveryConfiguration conf = new CachingServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
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

    public ServiceCallConfigurationDefinition dnsServiceDiscovery(String domain) {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration();
        conf.setDomain(domain);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public ServiceCallConfigurationDefinition dnsServiceDiscovery(String domain, String protocol) {
        DnsServiceCallServiceDiscoveryConfiguration conf = new DnsServiceCallServiceDiscoveryConfiguration();
        conf.setDomain(domain);
        conf.setProto(protocol);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public KubernetesServiceCallServiceDiscoveryConfiguration kubernetesClientServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();
        conf.setLookup("client");

        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallConfigurationDefinition kubernetesEnvServiceDiscovery() {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();
        conf.setLookup("environment");

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public ServiceCallConfigurationDefinition kubernetesDnsServiceDiscovery(String namespace, String domain) {
        KubernetesServiceCallServiceDiscoveryConfiguration conf = new KubernetesServiceCallServiceDiscoveryConfiguration();

        conf.setNamespace(namespace);
        conf.setDnsDomain(domain);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public CombinedServiceCallServiceDiscoveryConfiguration combinedServiceDiscovery() {
        CombinedServiceCallServiceDiscoveryConfiguration conf = new CombinedServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallConfigurationDefinition combinedServiceDiscovery(CombinedServiceCallServiceDiscoveryConfiguration conf) {
        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public StaticServiceCallServiceDiscoveryConfiguration staticServiceDiscovery() {
        StaticServiceCallServiceDiscoveryConfiguration conf = new StaticServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallConfigurationDefinition staticServiceDiscovery(StaticServiceCallServiceDiscoveryConfiguration conf) {
        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration zookeeperServiceDiscovery() {
        ZooKeeperServiceCallServiceDiscoveryConfiguration conf = new ZooKeeperServiceCallServiceDiscoveryConfiguration();
        setServiceDiscoveryConfiguration(conf);

        return conf;
    }

    public ServiceCallConfigurationDefinition zookeeperServiceDiscovery(String nodes, String basePath) {
        ZooKeeperServiceCallServiceDiscoveryConfiguration conf = new ZooKeeperServiceCallServiceDiscoveryConfiguration();
        conf.setNodes(nodes);
        conf.setBasePath(basePath);

        setServiceDiscoveryConfiguration(conf);

        return this;
    }

    // *****************************
    // Shortcuts - ServiceFilter
    // *****************************

    public ServiceCallConfigurationDefinition healthyFilter() {
        HealthyServiceCallServiceFilterConfiguration conf = new HealthyServiceCallServiceFilterConfiguration();
        setServiceFilterConfiguration(conf);

        return this;
    }

    public ServiceCallConfigurationDefinition passThroughFilter() {
        PassThroughServiceCallServiceFilterConfiguration conf = new PassThroughServiceCallServiceFilterConfiguration();
        setServiceFilterConfiguration(conf);

        return this;
    }

    public CombinedServiceCallServiceFilterConfiguration combinedFilter() {
        CombinedServiceCallServiceFilterConfiguration conf = new CombinedServiceCallServiceFilterConfiguration();
        setServiceFilterConfiguration(conf);

        return conf;
    }

    public BlacklistServiceCallServiceFilterConfiguration blacklistFilter() {
        BlacklistServiceCallServiceFilterConfiguration conf = new BlacklistServiceCallServiceFilterConfiguration();
        setServiceFilterConfiguration(conf);

        return conf;
    }

    public ServiceCallConfigurationDefinition customFilter(String serviceFilter) {
        CustomServiceCallServiceFilterConfiguration conf = new CustomServiceCallServiceFilterConfiguration();
        conf.setServiceFilterRef(serviceFilter);

        setServiceFilterConfiguration(conf);

        return this;
    }

    public ServiceCallConfigurationDefinition customFilter(ServiceFilter serviceFilter) {
        CustomServiceCallServiceFilterConfiguration conf = new CustomServiceCallServiceFilterConfiguration();
        conf.setServiceFilter(serviceFilter);

        setServiceFilterConfiguration(conf);

        return this;
    }

    // *****************************
    // Shortcuts - LoadBalancer
    // *****************************

    public ServiceCallConfigurationDefinition defaultLoadBalancer() {
        DefaultServiceCallServiceLoadBalancerConfiguration conf = new DefaultServiceCallServiceLoadBalancerConfiguration();
        setLoadBalancerConfiguration(conf);

        return this;
    }
}
