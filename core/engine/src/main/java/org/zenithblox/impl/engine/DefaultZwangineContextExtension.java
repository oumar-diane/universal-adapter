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

import org.zenithblox.*;
import org.zenithblox.spi.*;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.NormalizedUri;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.startup.DefaultStartupStepRecorder;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

class DefaultZwangineContextExtension implements ExtendedZwangineContext {

    private final static String MANAGEMENT_FACTORY_SOURCE = "META-INF/services.org.zenithblox/management/";
    private final AbstractZwangineContext zwangineContext;
    private final ThreadLocal<String> isCreateWorkflow = new ThreadLocal<>();
    private final ThreadLocal<String> isCreateProcessor = new ThreadLocal<>();
    private final ThreadLocal<Boolean> isSetupWorkflows = new ThreadLocal<>();
    private final List<InterceptStrategy> interceptStrategies = new ArrayList<>();
    private final Map<String, FactoryFinder> factories = new ConcurrentHashMap<>();
    private final Map<String, FactoryFinder> bootstrapFactories = new ConcurrentHashMap<>();
    private final Set<LogListener> logListeners = new LinkedHashSet<>();
    private final PluginManager pluginManager = new DefaultContextPluginManager();
    private final WorkflowController internalWorkflowController;

    // start auto assigning workflow ids using numbering 1000 and upwards
    private final List<BootstrapCloseable> bootstraps = new CopyOnWriteArrayList<>();

    private volatile String description;
    private volatile String profile;
    private volatile ExchangeFactory exchangeFactory;
    private volatile ExchangeFactoryManager exchangeFactoryManager;
    private volatile ProcessorExchangeFactory processorExchangeFactory;
    private volatile ReactiveExecutor reactiveExecutor;
    private volatile Registry registry;
    private volatile ManagementStrategy managementStrategy;
    private volatile ManagementMBeanAssembler managementMBeanAssembler;
    private volatile HeadersMapFactory headersMapFactory;
    private volatile boolean eventNotificationApplicable;
    private volatile ZwangineContextNameStrategy nameStrategy;
    private volatile ManagementNameStrategy managementNameStrategy;
    private volatile PropertiesComponent propertiesComponent;
    private volatile RestRegistryFactory restRegistryFactory;
    private volatile RestConfiguration restConfiguration;
    private volatile RestRegistry restRegistry;
    private volatile ClassResolver classResolver;
    private volatile MessageHistoryFactory messageHistoryFactory;
    private volatile StreamCachingStrategy streamCachingStrategy;
    private volatile InflightRepository inflightRepository;
    private volatile UuidGenerator uuidGenerator;
    private volatile Tracer tracer;
    private volatile TransformerRegistry transformerRegistry;
    private volatile ValidatorRegistry validatorRegistry;
    private volatile TypeConverterRegistry typeConverterRegistry;
    private volatile EndpointServiceRegistry endpointServiceRegistry;
    private volatile TypeConverter typeConverter;
    private volatile WorkflowController workflowController;
    private volatile ShutdownStrategy shutdownStrategy;
    private volatile ExecutorServiceManager executorServiceManager;

    private volatile Injector injector;

    private volatile StartupStepRecorder startupStepRecorder = new DefaultStartupStepRecorder();

    @Deprecated(since = "3.17.0")
    private ErrorHandlerFactory errorHandlerFactory;
    private String basePackageScan;

    private final Lock lock = new ReentrantLock();

    private volatile FactoryFinder bootstrapFactoryFinder;

    public DefaultZwangineContextExtension(AbstractZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        this.internalWorkflowController = new InternalWorkflowController(zwangineContext);
    }

    @Override
    public byte getStatusPhase() {
        return zwangineContext.getStatusPhase();
    }

    @Override
    public String getName() {
        return zwangineContext.getNameStrategy().getName();
    }

    ZwangineContextNameStrategy getNameStrategy() {
        if (nameStrategy == null) {
            lock.lock();
            try {
                if (nameStrategy == null) {
                    setNameStrategy(zwangineContext.createZwangineContextNameStrategy());
                }
            } finally {
                lock.unlock();
            }
        }
        return nameStrategy;
    }

    void setNameStrategy(ZwangineContextNameStrategy nameStrategy) {
        this.nameStrategy = zwangineContext.getInternalServiceManager().addService(zwangineContext, nameStrategy);
    }

    ManagementNameStrategy getManagementNameStrategy() {
        if (managementNameStrategy == null) {
            lock.lock();
            try {
                if (managementNameStrategy == null) {
                    setManagementNameStrategy(zwangineContext.createManagementNameStrategy());
                }
            } finally {
                lock.unlock();
            }
        }
        return managementNameStrategy;
    }

    void setManagementNameStrategy(ManagementNameStrategy managementNameStrategy) {
        this.managementNameStrategy = zwangineContext.getInternalServiceManager().addService(zwangineContext, managementNameStrategy);
    }

    PropertiesComponent getPropertiesComponent() {
        if (propertiesComponent == null) {
            lock.lock();
            try {
                if (propertiesComponent == null) {
                    setPropertiesComponent(zwangineContext.createPropertiesComponent());
                }
            } finally {
                lock.unlock();
            }
        }
        return propertiesComponent;
    }

    void setPropertiesComponent(PropertiesComponent propertiesComponent) {
        this.propertiesComponent = zwangineContext.getInternalServiceManager().addService(zwangineContext, propertiesComponent);
    }

    @Override
    public void setName(String name) {
        // use an explicit name strategy since an explicit name was provided to be used
        zwangineContext.setNameStrategy(new ExplicitZwangineContextNameStrategy(name));
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getProfile() {
        return profile;
    }

    @Override
    public void setProfile(String profile) {
        this.profile = profile;
    }

    @Override
    public Endpoint hasEndpoint(NormalizedEndpointUri uri) {
        return zwangineContext.getEndpointRegistry().get(uri);
    }

    @Override
    public NormalizedEndpointUri normalizeUri(String uri) {
        try {
            uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(zwangineContext, uri);
            return NormalizedUri.newNormalizedUri(uri, false);
        } catch (ResolveEndpointFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ResolveEndpointFailedException(uri, e);
        }
    }

    @Override
    public Endpoint getEndpoint(NormalizedEndpointUri uri) {
        return zwangineContext.doGetEndpoint(uri.getUri(), null, true, false);
    }

    @Override
    public Endpoint getPrototypeEndpoint(String uri) {
        return zwangineContext.doGetEndpoint(uri, null, false, true);
    }

    @Override
    public Endpoint getPrototypeEndpoint(NormalizedEndpointUri uri) {
        return zwangineContext.doGetEndpoint(uri.getUri(), null, true, true);
    }

    @Override
    public Endpoint getEndpoint(NormalizedEndpointUri uri, Map<String, Object> parameters) {
        return zwangineContext.doGetEndpoint(uri.getUri(), parameters, true, false);
    }

    @Override
    public void registerEndpointCallback(EndpointStrategy strategy) {
        // let it be invoked for already registered endpoints so it can
        // catch-up.
        if (zwangineContext.getEndpointStrategies().add(strategy)) {
            for (Endpoint endpoint : zwangineContext.getEndpoints()) {
                Endpoint newEndpoint = strategy.registerEndpoint(endpoint.getEndpointUri(),
                        endpoint);
                if (newEndpoint != null) {
                    // put will replace existing endpoint with the new endpoint
                    zwangineContext.getEndpointRegistry()
                            .put(zwangineContext.getEndpointKey(endpoint.getEndpointUri()),
                                    newEndpoint);
                }
            }
        }
    }

    @Override
    public List<WorkflowStartupOrder> getWorkflowStartupOrder() {
        return zwangineContext.getWorkflowStartupOrder();
    }

    @Override
    public boolean isSetupWorkflows() {
        Boolean answer = isSetupWorkflows.get();
        return answer != null && answer;
    }

    @Override
    public String getCreateWorkflow() {
        return isCreateWorkflow.get();
    }

    @Override
    public String getCreateProcessor() {
        return isCreateProcessor.get();
    }

    @Override
    public void addBootstrap(BootstrapCloseable bootstrap) {
        bootstraps.add(bootstrap);
    }

    void closeBootstraps() {
        for (BootstrapCloseable bootstrap : bootstraps) {
            try {
                bootstrap.close();
            } catch (Exception e) {
                logger().warn("Error during closing bootstrap. This exception is ignored.", e);
            }
        }
        bootstraps.clear();
    }

    List<BootstrapCloseable> getBootstraps() {
        return bootstraps;
    }

    @Override
    public List<Service> getServices() {
        return zwangineContext.getInternalServiceManager().getServices();
    }

    @Override
    public String resolvePropertyPlaceholders(String text, boolean keepUnresolvedOptional) {
        if (text != null && text.contains(PropertiesComponent.PREFIX_TOKEN)) {
            // the parser will throw exception if property key was not found
            String answer = zwangineContext.getPropertiesComponent().parseUri(text, keepUnresolvedOptional);
            logger().debug("Resolved text: {} -> {}", text, answer);
            return answer;
        }
        // is the value a known field (currently we only support
        // constants from Exchange.class)
        if (text != null && text.startsWith("Exchange.")) {
            String field = StringHelper.after(text, "Exchange.");
            String constant = ExchangeConstantProvider.lookup(field);
            if (constant != null) {
                logger().debug("Resolved constant: {} -> {}", text, constant);
                return constant;
            } else {
                throw new IllegalArgumentException("Constant field with name: " + field + " not found on Exchange.class");
            }
        }

        // return original text as is
        return text;
    }

    @Override
    public ManagementMBeanAssembler getManagementMBeanAssembler() {
        return managementMBeanAssembler;
    }

    @Override
    public void setManagementMBeanAssembler(ManagementMBeanAssembler managementMBeanAssembler) {
        this.managementMBeanAssembler
                = zwangineContext.getInternalServiceManager().addService(zwangineContext, managementMBeanAssembler, false);
    }

    void stopRegistry() {
        ServiceHelper.stopService(registry);
    }

    @Override
    public Registry getRegistry() {
        if (registry == null) {
            lock.lock();
            try {
                if (registry == null) {
                    setRegistry(zwangineContext.createRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return registry;
    }

    @Override
    public void setRegistry(Registry registry) {
        ZwangineContextAware.trySetZwangineContext(registry, zwangineContext);
        this.registry = registry;
    }

    @Override
    public void createWorkflow(String workflowId) {
        if (workflowId != null) {
            isCreateWorkflow.set(workflowId);
        } else {
            isSetupWorkflows.remove();
        }
    }

    @Override
    public void createProcessor(String processorId) {
        if (processorId != null) {
            isCreateProcessor.set(processorId);
        } else {
            isCreateProcessor.remove();
        }
    }

    @Override
    public void setupWorkflows(boolean done) {
        if (done) {
            isSetupWorkflows.remove();
        } else {
            isSetupWorkflows.set(true);
        }
    }

    @Override
    public List<InterceptStrategy> getInterceptStrategies() {
        return interceptStrategies;
    }

    @Override
    public void addInterceptStrategy(InterceptStrategy interceptStrategy) {
        // avoid adding double which can happen with spring xml on spring boot
        if (!interceptStrategies.contains(interceptStrategy)) {
            interceptStrategies.add(interceptStrategy);
        }
    }

    @Override
    public Set<LogListener> getLogListeners() {
        return logListeners;
    }

    @Override
    public void addLogListener(LogListener listener) {
        // avoid adding double which can happen with spring xml on spring boot
        ZwangineContextAware.trySetZwangineContext(listener, zwangineContext);
        logListeners.add(listener);
    }

    @Override
    public ErrorHandlerFactory getErrorHandlerFactory() {
        return errorHandlerFactory;
    }

    @Override
    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    @Override
    public boolean isEventNotificationApplicable() {
        return eventNotificationApplicable;
    }

    @Override
    public void setEventNotificationApplicable(boolean eventNotificationApplicable) {
        this.eventNotificationApplicable = eventNotificationApplicable;
    }

    @Override
    public FactoryFinder getDefaultFactoryFinder() {
        return getFactoryFinder(FactoryFinder.DEFAULT_PATH);
    }

    @Override
    public void setDefaultFactoryFinder(FactoryFinder factoryFinder) {
        factories.put(FactoryFinder.DEFAULT_PATH, factoryFinder);
    }

    @Override
    public FactoryFinder getBootstrapFactoryFinder() {
        if (bootstrapFactoryFinder == null) {
            lock.lock();
            try {
                if (bootstrapFactoryFinder == null) {
                    bootstrapFactoryFinder
                            = PluginHelper.getFactoryFinderResolver(this)
                                    .resolveBootstrapFactoryFinder(zwangineContext.getClassResolver());
                }
            } finally {
                lock.unlock();
            }
        }
        return bootstrapFactoryFinder;
    }

    @Override
    public void setBootstrapFactoryFinder(FactoryFinder factoryFinder) {
        bootstrapFactoryFinder = factoryFinder;
    }

    @Override
    public FactoryFinder getBootstrapFactoryFinder(String path) {
        return bootstrapFactories.computeIfAbsent(path, zwangineContext::createBootstrapFactoryFinder);
    }

    @Override
    public FactoryFinder getFactoryFinder(String path) {

        return factories.computeIfAbsent(path, zwangineContext::createFactoryFinder);
    }

    @Override
    public void setupManagement(Map<String, Object> options) {
        logger().trace("Setting up management");

        ManagementStrategyFactory factory = null;
        if (!zwangineContext.isJMXDisabled()) {
            try {
                // create a one time factory as we dont need this anymore
                FactoryFinder finder = zwangineContext.createFactoryFinder(MANAGEMENT_FACTORY_SOURCE);
                if (finder != null) {
                    Object object = finder.newInstance("ManagementStrategyFactory").orElse(null);
                    if (object instanceof ManagementStrategyFactory managementStrategyFactory) {
                        factory = managementStrategyFactory;
                    }
                }
            } catch (Exception e) {
                logger().warn("Cannot create JmxManagementStrategyFactory. Will fallback and disable JMX.", e);
            }
        }
        if (factory == null) {
            factory = new DefaultManagementStrategyFactory();
        }
        logger().debug("Setting up management with factory: {}", factory);

        // preserve any existing event notifiers that may have been already added
        List<EventNotifier> notifiers = null;
        if (managementStrategy != null) {
            notifiers = managementStrategy.getEventNotifiers();
        }

        try {
            ManagementStrategy strategy = factory.create(zwangineContext.getZwangineContextReference(), options);
            if (notifiers != null) {
                notifiers.forEach(strategy::addEventNotifier);
            }
            LifecycleStrategy lifecycle = factory.createLifecycle(zwangineContext);
            factory.setupManagement(zwangineContext, strategy, lifecycle);
        } catch (Exception e) {
            logger().warn("Error setting up management due {}", e.getMessage());
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    @Override
    public String getBasePackageScan() {
        return basePackageScan;
    }

    @Override
    public void setBasePackageScan(String basePackageScan) {
        this.basePackageScan = basePackageScan;
    }

    @Override
    public HeadersMapFactory getHeadersMapFactory() {
        return headersMapFactory;
    }

    @Override
    public void setHeadersMapFactory(HeadersMapFactory headersMapFactory) {
        this.headersMapFactory = zwangineContext.getInternalServiceManager().addService(zwangineContext, headersMapFactory);
    }

    void initEagerMandatoryServices(boolean caseInsensitive, Supplier<HeadersMapFactory> headersMapFactorySupplier) {
        if (this.headersMapFactory == null) {
            // we want headers map to be created as then JVM can optimize using it as we use it per exchange/message
            lock.lock();
            try {
                if (this.headersMapFactory == null) {
                    if (caseInsensitive) {
                        // use factory to find the map factory to use
                        setHeadersMapFactory(headersMapFactorySupplier.get());
                    } else {
                        // case sensitive so we can use hash map
                        setHeadersMapFactory(new HashMapHeadersMapFactory());
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public ExchangeFactory getExchangeFactory() {
        if (exchangeFactory == null) {
            lock.lock();
            try {
                if (exchangeFactory == null) {
                    setExchangeFactory(zwangineContext.createExchangeFactory());
                }
            } finally {
                lock.unlock();
            }
        }
        return exchangeFactory;
    }

    @Override
    public void setExchangeFactory(ExchangeFactory exchangeFactory) {
        // automatic inject zwangine context
        exchangeFactory.setZwangineContext(zwangineContext);
        this.exchangeFactory = exchangeFactory;
    }

    @Override
    public ExchangeFactoryManager getExchangeFactoryManager() {
        if (exchangeFactoryManager == null) {
            lock.lock();
            try {
                if (exchangeFactoryManager == null) {
                    setExchangeFactoryManager(zwangineContext.createExchangeFactoryManager());
                }
            } finally {
                lock.unlock();
            }
        }
        return exchangeFactoryManager;
    }

    @Override
    public void setExchangeFactoryManager(ExchangeFactoryManager exchangeFactoryManager) {
        this.exchangeFactoryManager = zwangineContext.getInternalServiceManager().addService(zwangineContext, exchangeFactoryManager);
    }

    @Override
    public ProcessorExchangeFactory getProcessorExchangeFactory() {
        if (processorExchangeFactory == null) {
            lock.lock();
            try {
                if (processorExchangeFactory == null) {
                    setProcessorExchangeFactory(zwangineContext.createProcessorExchangeFactory());
                }
            } finally {
                lock.unlock();
            }
        }
        return processorExchangeFactory;
    }

    @Override
    public void setProcessorExchangeFactory(ProcessorExchangeFactory processorExchangeFactory) {
        // automatic inject zwangine context
        processorExchangeFactory.setZwangineContext(zwangineContext);
        this.processorExchangeFactory = processorExchangeFactory;
    }

    @Override
    public ReactiveExecutor getReactiveExecutor() {
        if (reactiveExecutor == null) {
            lock.lock();
            try {
                if (reactiveExecutor == null) {
                    setReactiveExecutor(zwangineContext.createReactiveExecutor());
                }
            } finally {
                lock.unlock();
            }
        }
        return reactiveExecutor;
    }

    @Override
    public void setReactiveExecutor(ReactiveExecutor reactiveExecutor) {
        // special for executorServiceManager as want to stop it manually so
        // false in stopOnShutdown
        this.reactiveExecutor = zwangineContext.getInternalServiceManager().addService(zwangineContext, reactiveExecutor, false);
    }

    RestRegistryFactory getRestRegistryFactory() {
        if (restRegistryFactory == null) {
            lock.lock();
            try {
                if (restRegistryFactory == null) {
                    setRestRegistryFactory(zwangineContext.createRestRegistryFactory());
                }
            } finally {
                lock.unlock();
            }
        }
        return restRegistryFactory;
    }

    void setRestRegistryFactory(RestRegistryFactory restRegistryFactory) {
        this.restRegistryFactory = zwangineContext.getInternalServiceManager().addService(zwangineContext, restRegistryFactory);
    }

    RestRegistry getRestRegistry() {
        if (restRegistry == null) {
            lock.lock();
            try {
                if (restRegistry == null) {
                    setRestRegistry(zwangineContext.createRestRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return restRegistry;
    }

    void setRestRegistry(RestRegistry restRegistry) {
        this.restRegistry = zwangineContext.getInternalServiceManager().addService(zwangineContext, restRegistry);
    }

    RestConfiguration getRestConfiguration() {
        if (restConfiguration == null) {
            lock.lock();
            try {
                if (restConfiguration == null) {
                    setRestConfiguration(zwangineContext.createRestConfiguration());
                }
            } finally {
                lock.unlock();
            }
        }
        return restConfiguration;
    }

    void setRestConfiguration(RestConfiguration restConfiguration) {
        this.restConfiguration = restConfiguration;
    }

    ClassResolver getClassResolver() {
        if (classResolver == null) {
            lock.lock();
            try {
                if (classResolver == null) {
                    setClassResolver(zwangineContext.createClassResolver());
                }
            } finally {
                lock.unlock();
            }
        }
        return classResolver;
    }

    void setClassResolver(ClassResolver classResolver) {
        this.classResolver = zwangineContext.getInternalServiceManager().addService(zwangineContext, classResolver);
    }

    MessageHistoryFactory getMessageHistoryFactory() {
        if (messageHistoryFactory == null) {
            lock.lock();
            try {
                if (messageHistoryFactory == null) {
                    setMessageHistoryFactory(zwangineContext.createMessageHistoryFactory());
                }
            } finally {
                lock.unlock();
            }
        }
        return messageHistoryFactory;
    }

    void setMessageHistoryFactory(MessageHistoryFactory messageHistoryFactory) {
        this.messageHistoryFactory = zwangineContext.getInternalServiceManager().addService(zwangineContext, messageHistoryFactory);
    }

    StreamCachingStrategy getStreamCachingStrategy() {
        if (streamCachingStrategy == null) {
            lock.lock();
            try {
                if (streamCachingStrategy == null) {
                    setStreamCachingStrategy(zwangineContext.createStreamCachingStrategy());
                }
            } finally {
                lock.unlock();
            }
        }
        return streamCachingStrategy;
    }

    void setStreamCachingStrategy(StreamCachingStrategy streamCachingStrategy) {
        this.streamCachingStrategy
                = zwangineContext.getInternalServiceManager().addService(zwangineContext, streamCachingStrategy, true, false, true);
    }

    InflightRepository getInflightRepository() {
        if (inflightRepository == null) {
            lock.lock();
            try {
                if (inflightRepository == null) {
                    setInflightRepository(zwangineContext.createInflightRepository());
                }
            } finally {
                lock.unlock();
            }
        }
        return inflightRepository;
    }

    void setInflightRepository(InflightRepository repository) {
        this.inflightRepository = zwangineContext.getInternalServiceManager().addService(zwangineContext, repository);
    }

    UuidGenerator getUuidGenerator() {
        if (uuidGenerator == null) {
            lock.lock();
            try {
                if (uuidGenerator == null) {
                    setUuidGenerator(zwangineContext.createUuidGenerator());
                }
            } finally {
                lock.unlock();
            }
        }
        return uuidGenerator;
    }

    void setUuidGenerator(UuidGenerator uuidGenerator) {
        this.uuidGenerator = zwangineContext.getInternalServiceManager().addService(zwangineContext, uuidGenerator);
    }

    Tracer getTracer() {
        if (tracer == null) {
            lock.lock();
            try {
                if (tracer == null) {
                    setTracer(zwangineContext.createTracer());
                }
            } finally {
                lock.unlock();
            }
        }
        return tracer;
    }

    void setTracer(Tracer tracer) {
        this.tracer = zwangineContext.getInternalServiceManager().addService(zwangineContext, tracer, true, false, true);
    }

    TransformerRegistry getTransformerRegistry() {
        if (transformerRegistry == null) {
            lock.lock();
            try {
                if (transformerRegistry == null) {
                    setTransformerRegistry(zwangineContext.createTransformerRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return transformerRegistry;
    }

    void setTransformerRegistry(TransformerRegistry transformerRegistry) {
        this.transformerRegistry = zwangineContext.getInternalServiceManager().addService(zwangineContext, transformerRegistry);
    }

    @Override
    public EndpointServiceRegistry getEndpointServiceRegistry() {
        if (endpointServiceRegistry == null) {
            lock.lock();
            try {
                if (endpointServiceRegistry == null) {
                    setEndpointServiceRegistry(zwangineContext.createEndpointServiceRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return endpointServiceRegistry;
    }

    @Override
    public void setEndpointServiceRegistry(EndpointServiceRegistry endpointServiceRegistry) {
        this.endpointServiceRegistry
                = zwangineContext.getInternalServiceManager().addService(zwangineContext, endpointServiceRegistry);
    }

    ValidatorRegistry getValidatorRegistry() {
        if (validatorRegistry == null) {
            lock.lock();
            try {
                if (validatorRegistry == null) {
                    setValidatorRegistry(zwangineContext.createValidatorRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return validatorRegistry;
    }

    public void setValidatorRegistry(ValidatorRegistry validatorRegistry) {
        this.validatorRegistry = zwangineContext.getInternalServiceManager().addService(zwangineContext, validatorRegistry);
    }

    void stopTypeConverterRegistry() {
        ServiceHelper.stopService(typeConverterRegistry);
    }

    void resetTypeConverterRegistry() {
        typeConverterRegistry = null;
    }

    TypeConverterRegistry getTypeConverterRegistry() {
        if (typeConverterRegistry == null) {
            lock.lock();
            try {
                if (typeConverterRegistry == null) {
                    setTypeConverterRegistry(zwangineContext.createTypeConverterRegistry());
                }
            } finally {
                lock.unlock();
            }
        }
        return typeConverterRegistry;
    }

    void setTypeConverterRegistry(TypeConverterRegistry typeConverterRegistry) {
        this.typeConverterRegistry = zwangineContext.getInternalServiceManager().addService(zwangineContext, typeConverterRegistry);
        // some registries are also a type converter implementation
        if (typeConverterRegistry instanceof TypeConverter newTypeConverter) {
            setTypeConverter(newTypeConverter);
        }
    }

    void stopTypeConverter() {
        ServiceHelper.stopService(typeConverter);
    }

    void resetTypeConverter() {
        typeConverter = null;
    }

    TypeConverter getTypeConverter() {
        return typeConverter;
    }

    void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = zwangineContext.getInternalServiceManager().addService(zwangineContext, typeConverter);
    }

    TypeConverter getOrCreateTypeConverter() {
        if (typeConverter == null) {
            lock.lock();
            try {
                if (typeConverter == null) {
                    setTypeConverter(zwangineContext.createTypeConverter());
                }
            } finally {
                lock.unlock();
            }
        }
        return typeConverter;
    }

    void resetInjector() {
        injector = null;
    }

    Injector getInjector() {
        if (injector == null) {
            lock.lock();
            try {
                if (injector == null) {
                    setInjector(zwangineContext.createInjector());
                }
            } finally {
                lock.unlock();
            }
        }
        return injector;
    }

    void setInjector(Injector injector) {
        this.injector = zwangineContext.getInternalServiceManager().addService(zwangineContext, injector);
    }

    void stopAndShutdownWorkflowController() {
        ServiceHelper.stopAndShutdownService(this.workflowController);
    }

    WorkflowController getWorkflowController() {
        if (workflowController == null) {
            lock.lock();
            try {
                if (workflowController == null) {
                    setWorkflowController(zwangineContext.createWorkflowController());
                }
            } finally {
                lock.unlock();
            }
        }
        return workflowController;
    }

    void setWorkflowController(WorkflowController workflowController) {
        this.workflowController = zwangineContext.getInternalServiceManager().addService(zwangineContext, workflowController);
    }

    ShutdownStrategy getShutdownStrategy() {
        if (shutdownStrategy == null) {
            lock.lock();
            try {
                if (shutdownStrategy == null) {
                    setShutdownStrategy(zwangineContext.createShutdownStrategy());
                }
            } finally {
                lock.unlock();
            }
        }
        return shutdownStrategy;
    }

    void setShutdownStrategy(ShutdownStrategy shutdownStrategy) {
        this.shutdownStrategy = zwangineContext.getInternalServiceManager().addService(zwangineContext, shutdownStrategy);
    }

    ExecutorServiceManager getExecutorServiceManager() {
        if (executorServiceManager == null) {
            lock.lock();
            try {
                if (executorServiceManager == null) {
                    setExecutorServiceManager(zwangineContext.createExecutorServiceManager());
                }
            } finally {
                lock.unlock();
            }
        }
        return this.executorServiceManager;
    }

    void setExecutorServiceManager(ExecutorServiceManager executorServiceManager) {
        // special for executorServiceManager as want to stop it manually so
        // false in stopOnShutdown
        this.executorServiceManager
                = zwangineContext.getInternalServiceManager().addService(zwangineContext, executorServiceManager, false);
    }

    @Override
    public WorkflowController getInternalWorkflowController() {
        return internalWorkflowController;
    }

    @Override
    public EndpointUriFactory getEndpointUriFactory(String scheme) {
        return PluginHelper.getUriFactoryResolver(this).resolveFactory(scheme, zwangineContext);
    }

    @Override
    public StartupStepRecorder getStartupStepRecorder() {
        return startupStepRecorder;
    }

    @Override
    public void setStartupStepRecorder(StartupStepRecorder startupStepRecorder) {
        this.startupStepRecorder = startupStepRecorder;
    }

    @Override
    public void addWorkflow(Workflow workflow) {
        zwangineContext.addWorkflow(workflow);
    }

    @Override
    public void removeWorkflow(Workflow workflow) {
        zwangineContext.removeWorkflow(workflow);
    }

    @Override
    public Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception {
        return zwangineContext.createErrorHandler(workflow, processor);
    }

    @Override
    public void disposeModel() {
    }

    @Override
    public String getTestExcludeWorkflows() {
        return zwangineContext.getTestExcludeWorkflows();
    }

    ManagementStrategy getManagementStrategy() {
        return managementStrategy;
    }

    void setManagementStrategy(ManagementStrategy managementStrategy) {
        this.managementStrategy = managementStrategy;
    }

    @Override
    public <T> T getContextPlugin(Class<T> type) {
        T ret = pluginManager.getContextPlugin(type);

        // Note: this is because of interfaces like Model which are still tightly coupled with the context
        if (ret == null) {
            if (type.isInstance(zwangineContext)) {
                return type.cast(zwangineContext);
            }
        }

        return ret;
    }

    @Override
    public <T> void addContextPlugin(Class<T> type, T module) {
        final T addedModule = zwangineContext.getInternalServiceManager().addService(zwangineContext, module);
        pluginManager.addContextPlugin(type, addedModule);
    }

    @Override
    public <T> void lazyAddContextPlugin(Class<T> type, Supplier<T> module) {
        pluginManager.lazyAddContextPlugin(type, () -> lazyInitAndAdd(module));
    }

    private <T> T lazyInitAndAdd(Supplier<T> supplier) {
        T module = supplier.get();

        return zwangineContext.getInternalServiceManager().addService(zwangineContext, module);
    }

    /*
     * NOTE: see CAMEL-19724. We log like this instead of using a statically declared logger in order to
     * reduce the risk of dropping log messages due to slf4j log substitution behavior during its own
     * initialization.
     */
    private static final class Holder {
        static final Logger LOG = LoggerFactory.getLogger(DefaultZwangineContextExtension.class);
    }

    private static Logger logger() {
        return Holder.LOG;
    }
}
