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

import org.zenithblox.ApiEndpoint;
import org.zenithblox.ZwangineContext;
import org.zenithblox.Component;
import org.zenithblox.Consumer;
import org.zenithblox.spi.ExecutorServiceManager;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.spi.ThreadPoolProfile;
import org.zenithblox.spi.UriParam;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.support.PropertyConfigurerHelper;
import org.zenithblox.support.ScheduledPollEndpoint;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Abstract base class for API Component Endpoints.
 */
public abstract class AbstractApiEndpoint<E extends ApiName, T>
        extends ScheduledPollEndpoint
        implements ApiEndpoint, PropertyNamesInterceptor, PropertiesInterceptor {

    // thread pool executor with Endpoint Class name as keys
    private static final Map<String, ExecutorService> EXECUTOR_SERVICE_MAP = new ConcurrentHashMap<>();

    // logger
    protected final Logger log = LoggerFactory.getLogger(getClass());

    // API name
    protected final E apiName;

    // API method name
    protected final String methodName;

    // API method helper
    protected final ApiMethodHelper<? extends ApiMethod> methodHelper;

    // endpoint configuration
    protected final T configuration;

    // property name for Exchange 'In' message body
    @UriParam(description = "Sets the name of a parameter to be passed in the exchange In Body")
    protected String inBody;

    // candidate methods based on method name and endpoint configuration
    private List<ApiMethod> candidates;

    // cached Executor service
    private volatile ExecutorService executorService;

    // cached property names and values
    private Set<String> endpointPropertyNames;
    private Map<String, Object> endpointProperties;
    private Set<String> configurationPropertyNames;
    private Map<String, Object> configurationProperties;

    public AbstractApiEndpoint(String endpointUri, Component component,
                               E apiName, String methodName, ApiMethodHelper<? extends ApiMethod> methodHelper,
                               T endpointConfiguration) {
        super(endpointUri, component);

        this.apiName = apiName;
        this.methodName = methodName;
        this.methodHelper = methodHelper;
        this.configuration = endpointConfiguration;
    }

    /**
     * Returns generated helper that extends {@link ApiMethodPropertiesHelper} to work with API properties.
     *
     * @return properties helper.
     */
    protected abstract ApiMethodPropertiesHelper<T> getPropertiesHelper();

    @Override
    public void configureProperties(Map<String, Object> options) {
        if (options != null && !options.isEmpty()) {
            // configure scheduler first
            configureScheduledPollConsumerProperties(options);

            PropertyConfigurer configurer = getComponent().getEndpointPropertyConfigurer();
            PropertyConfigurer configurer2
                    = PropertyConfigurerHelper.resolvePropertyConfigurer(getZwangineContext(), getConfiguration());

            // we have a mix of options that are general endpoint and then specialized
            // so we need to configure first without reflection
            // use configurer and ignore case as end users may type an option name with mixed case
            PropertyBindingSupport.build().withConfigurer(configurer)
                    .withIgnoreCase(true).withReflection(false)
                    .bind(getZwangineContext(), this, options);
            PropertyBindingSupport.build().withConfigurer(configurer2)
                    .withIgnoreCase(true).withReflection(false)
                    .bind(getZwangineContext(), getConfiguration(), options);

            // after reflection-free then we fallback to allow reflection
            // in case some options are still left
            if (!options.isEmpty()) {
                PropertyBindingSupport.build().withConfigurer(configurer)
                        .withIgnoreCase(true).withReflection(true)
                        .bind(getZwangineContext(), this, options);
                PropertyBindingSupport.build().withConfigurer(configurer2)
                        .withIgnoreCase(true).withReflection(true)
                        .bind(getZwangineContext(), getConfiguration(), options);
            }
        }

        // validate and initialize state
        initState();

        afterConfigureProperties();
    }

    /**
     * Initialize proxies, create server connections, etc. after endpoint properties have been configured.
     */
    protected abstract void afterConfigureProperties();

    /**
     * Initialize endpoint state, including endpoint arguments, find candidate methods, etc.
     */
    private void initState() {

        // compute configuration & endpoint property names and values
        HashMap<String, Object> properties = new HashMap<>();
        getPropertiesHelper().getConfigurationProperties(getZwangineContext(), configuration, properties);
        this.configurationProperties = Collections.unmodifiableMap(properties);
        this.configurationPropertyNames = Collections.unmodifiableSet(properties.keySet());

        properties = new HashMap<>();
        getPropertiesHelper().getEndpointProperties(getZwangineContext(), configuration, properties);
        this.endpointProperties = Collections.unmodifiableMap(properties);
        this.endpointPropertyNames = Collections.unmodifiableSet(properties.keySet());

        // use only endpoint property names when looking for candidate methods
        final Set<String> arguments = new HashSet<>(endpointPropertyNames);
        // add inBody argument for producers
        if (inBody != null) {
            arguments.add(inBody);
        }

        interceptPropertyNames(arguments);

        // create a list of candidate methods
        candidates = new ArrayList<>();
        candidates.addAll(methodHelper.getCandidateMethods(methodName, arguments));
        candidates = Collections.unmodifiableList(candidates);

        // error if there are no candidates
        if (candidates.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("No matching method for %s/%s, with arguments %s",
                            apiName.getName(), methodName, arguments));
        }

        // log missing/extra properties for debugging
        if (log.isDebugEnabled()) {
            final Set<String> missing = methodHelper.getMissingProperties(methodName, arguments);
            if (!missing.isEmpty()) {
                log.debug("Method {} could use one or more properties from {}", methodName, missing);
            }
        }
    }

    @Override
    protected void configureConsumer(Consumer consumer) throws Exception {
        super.configureConsumer(consumer);
        if (getConfiguration() instanceof AbstractApiConfiguration config && consumer instanceof AbstractApiConsumer) {
            ((AbstractApiConsumer<?, ?>) consumer)
                    .setSplitResult(config.isSplitResult());
        }
    }

    @Override
    public void interceptPropertyNames(Set<String> propertyNames) {
        // do nothing by default
    }

    @Override
    public void interceptProperties(Map<String, Object> properties) {
        // do nothing by default
    }

    /**
     * Returns endpoint configuration object. One of the generated EndpointConfiguration classes that extends component
     * configuration class.
     *
     * @return endpoint configuration object
     */
    public final T getConfiguration() {
        return configuration;
    }

    /**
     * Returns API name.
     *
     * @return apiName property.
     */
    public final E getApiName() {
        return apiName;
    }

    /**
     * Returns method name.
     *
     * @return methodName property.
     */
    public final String getMethodName() {
        return methodName;
    }

    /**
     * Returns method helper.
     *
     * @return methodHelper property.
     */
    public final ApiMethodHelper<? extends ApiMethod> getMethodHelper() {
        return methodHelper;
    }

    /**
     * Returns candidate methods for this endpoint.
     *
     * @return list of candidate methods.
     */
    public final List<ApiMethod> getCandidates() {
        return candidates;
    }

    /**
     * Returns name of parameter passed in the exchange In Body.
     *
     * @return inBody property.
     */
    public final String getInBody() {
        return inBody;
    }

    /**
     * Sets the name of a parameter to be passed in the exchange In Body.
     *
     * @param  inBody                   parameter name
     * @throws IllegalArgumentException for invalid parameter name.
     */
    public final void setInBody(String inBody) throws IllegalArgumentException {
        // validate property name
        ObjectHelper.notNull(inBody, "inBody");
        if (!getPropertiesHelper().getValidEndpointProperties(getZwangineContext(), getConfiguration()).contains(inBody)) {
            throw new IllegalArgumentException("Unknown property " + inBody);
        }
        this.inBody = inBody;
    }

    public final Set<String> getEndpointPropertyNames() {
        return endpointPropertyNames;
    }

    public final Map<String, Object> getEndpointProperties() {
        return endpointProperties;
    }

    public Set<String> getConfigurationPropertyNames() {
        return configurationPropertyNames;
    }

    public final Map<String, Object> getConfigurationProperties() {
        return configurationProperties;
    }

    /**
     * Returns an instance of an API Proxy based on apiName, method and args. Called by {@link AbstractApiConsumer} or
     * {@link AbstractApiProducer}.
     *
     * @param  method method about to be invoked
     * @param  args   method arguments
     * @return        a Java object that implements the method to be invoked.
     * @see           AbstractApiProducer
     * @see           AbstractApiConsumer
     */
    public abstract Object getApiProxy(ApiMethod method, Map<String, Object> args);

    private static ExecutorService getExecutorService(
            Class<? extends AbstractApiEndpoint> endpointClass, ZwangineContext context, String threadProfileName) {

        // lookup executorService for extending class name
        final String endpointClassName = endpointClass.getName();
        ExecutorService executorService = EXECUTOR_SERVICE_MAP.get(endpointClassName);

        // ZwangineContext will shutdown thread pool when it shutdown so we can
        // lazy create it on demand
        // but in case of hot-deploy or the likes we need to be able to
        // re-create it (its a shared static instance)
        if (executorService == null || executorService.isTerminated() || executorService.isShutdown()) {
            final ExecutorServiceManager manager = context.getExecutorServiceManager();

            // try to lookup a pool first based on profile
            ThreadPoolProfile poolProfile = manager.getThreadPoolProfile(
                    threadProfileName);
            if (poolProfile == null) {
                poolProfile = manager.getDefaultThreadPoolProfile();
            }

            // create a new pool using the custom or default profile
            executorService = manager.newScheduledThreadPool(endpointClass, threadProfileName, poolProfile);

            EXECUTOR_SERVICE_MAP.put(endpointClassName, executorService);
        }

        return executorService;
    }

    public final ExecutorService getExecutorService() {
        if (executorService == null) {
            lock.lock();
            try {
                if (executorService == null) {
                    executorService = getExecutorService(getClass(), getZwangineContext(), getThreadProfileName());
                }
            } finally {
                lock.unlock();
            }
        }
        return executorService;
    }

    /**
     * Returns Thread profile name. Generated as a constant THREAD_PROFILE_NAME in *Constants.
     *
     * @return thread profile name to use.
     */
    protected abstract String getThreadProfileName();
}
