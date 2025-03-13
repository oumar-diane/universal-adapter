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
import org.zenithblox.catalog.RuntimeZwangineCatalog;
import org.zenithblox.clock.Clock;
import org.zenithblox.clock.ContextClock;
import org.zenithblox.clock.EventClock;
import org.zenithblox.health.HealthCheckRegistry;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.spi.*;
import org.zenithblox.spi.WorkflowError.Phase;
import org.zenithblox.support.*;
import org.zenithblox.support.jsse.SSLContextParameters;
import org.zenithblox.support.service.BaseService;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.*;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.vault.VaultConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

import static org.zenithblox.spi.UnitOfWork.MDC_ZWANGINE_CONTEXT_ID;

/**
 * Represents the context used to configure workflows and the policies to use.
 */
public abstract class AbstractZwangineContext extends BaseService
        implements ZwangineContext, CatalogZwangineContext, Suspendable {

    private static final String LANGUAGE_RESOURCE = "META-INF/org.zenithblox.language/";

    private static final Logger LOG = LoggerFactory.getLogger(AbstractZwangineContext.class);

    private final InternalServiceManager internalServiceManager;

    private final DefaultZwangineContextExtension zwangineContextExtension = new DefaultZwangineContextExtension(this);
    private final AtomicInteger endpointKeyCounter = new AtomicInteger();
    private final Set<EndpointStrategy> endpointStrategies = ConcurrentHashMap.newKeySet();
    private final GlobalEndpointConfiguration globalEndpointConfiguration = new DefaultGlobalEndpointConfiguration();
    private final Map<String, Component> components = new ConcurrentHashMap<>();
    private final Set<Workflow> workflows = new LinkedHashSet<>();
    private final List<StartupListener> startupListeners = new CopyOnWriteArrayList<>();
    private final Map<String, Language> languages = new ConcurrentHashMap<>();
    private final Map<String, DataFormat> dataformats = new ConcurrentHashMap<>();
    private final List<LifecycleStrategy> lifecycleStrategies = new CopyOnWriteArrayList<>();
    private final ThreadLocal<Boolean> isStartingWorkflows = new ThreadLocal<>();
    private final ThreadLocal<Boolean> isLockModel = new ThreadLocal<>();
    private final Map<String, WorkflowService> workflowServices = new LinkedHashMap<>();
    private final Map<String, WorkflowService> suspendedWorkflowServices = new LinkedHashMap<>();
    private final InternalWorkflowStartupManager internalWorkflowStartupManager = new InternalWorkflowStartupManager();
    private final List<WorkflowStartupOrder> workflowStartupOrder = new ArrayList<>();
    private final StopWatch stopWatch = new StopWatch(false);
    private final ThreadLocal<Set<String>> componentsInCreation = ThreadLocal.withInitial(HashSet::new);
    private final Lock workflowsLock = new ReentrantLock();
    private final Lock lock = new ReentrantLock();
    private VetoZwangineContextStartException vetoed;
    private String managementName;
    private ClassLoader applicationContextClassLoader;
    private boolean autoCreateComponents = true;
    private VaultConfiguration vaultConfiguration = new VaultConfiguration();

    private final List<WorkflowPolicyFactory> workflowPolicyFactories = new ArrayList<>();
    // special flags to control the first startup which can are special
    private volatile boolean firstStartDone;
    private volatile boolean doNotStartWorkflowsOnFirstStart;
    private Boolean autoStartup = Boolean.TRUE;
    private Boolean backlogTrace = Boolean.FALSE;
    private Boolean backlogTraceStandby = Boolean.FALSE;
    private Boolean backlogTraceTemplates = Boolean.FALSE;
    private Boolean trace = Boolean.FALSE;
    private Boolean traceStandby = Boolean.FALSE;
    private Boolean traceTemplates = Boolean.FALSE;
    private String tracePattern;
    private String tracingLoggingFormat;
    private Boolean modeline = Boolean.FALSE;
    private Boolean debugStandby = Boolean.FALSE;
    private String debugBreakpoints;
    private Boolean messageHistory = Boolean.FALSE;
    private Boolean logMask = Boolean.FALSE;
    private Boolean logExhaustedMessageBody = Boolean.FALSE;
    private Boolean streamCache = Boolean.TRUE;
    private Boolean disableJMX = Boolean.FALSE;
    private Boolean loadTypeConverters = Boolean.FALSE;
    private Boolean loadHealthChecks = Boolean.FALSE;
    private Boolean sourceLocationEnabled = Boolean.FALSE;
    private Boolean typeConverterStatisticsEnabled = Boolean.FALSE;
    private String dumpWorkflows;
    private Boolean useMDCLogging = Boolean.FALSE;
    private String mdcLoggingKeysPattern;
    private Boolean useDataType = Boolean.FALSE;
    private Boolean useBreadcrumb = Boolean.FALSE;
    private Boolean allowUseOriginalMessage = Boolean.FALSE;
    private Boolean caseInsensitiveHeaders = Boolean.TRUE;
    private Boolean autowiredEnabled = Boolean.TRUE;
    private Long delay;
    private Map<String, String> globalOptions = new HashMap<>();
    private EndpointRegistry endpoints;
    private RuntimeEndpointRegistry runtimeEndpointRegistry;
    private ShutdownWorkflow shutdownWorkflow = ShutdownWorkflow.Default;
    private ShutdownRunningTask shutdownRunningTask = ShutdownRunningTask.CompleteCurrentTaskOnly;
    private long buildTaken;
    private long initTaken;
    private final ContextClock clock = new ContextClock();
    private SSLContextParameters sslContextParameters;
    private StartupSummaryLevel startupSummaryLevel = StartupSummaryLevel.Default;

    /**
     * Creates the {@link ZwangineContext} using {@link org.zenithblox.support.DefaultRegistry} as registry.
     * <p/>
     * Use one of the other constructors to force use an explicit registry.
     */
    protected AbstractZwangineContext() {
        this(true);
    }

    /**
     * Creates the {@link ZwangineContext} using the given registry
     *
     * @param registry the registry
     */
    protected AbstractZwangineContext(Registry registry) {
        this();
        zwangineContextExtension.setRegistry(registry);
    }

    protected AbstractZwangineContext(boolean build) {
        // create a provisional (temporary) endpoint registry at first since end
        // users may access endpoints before ZwangineContext is started
        // we will later transfer the endpoints to the actual
        // DefaultEndpointRegistry later, but we do this to startup Zwangine faster.
        this.endpoints = new ProvisionalEndpointRegistry();

        // add a default LifecycleStrategy that discover strategies on the registry and invoke them
        this.lifecycleStrategies.add(new OnZwangineContextLifecycleStrategy());

        // add a default LifecycleStrategy to customize services using customizers from registry
        this.lifecycleStrategies.add(new CustomizersLifecycleStrategy(this));

        // add a default autowired strategy
        this.lifecycleStrategies.add(new DefaultAutowiredLifecycleStrategy(this));

        // add the default bootstrap closer
        zwangineContextExtension.addBootstrap(new DefaultServiceBootstrapCloseable(this));

        this.internalServiceManager = new InternalServiceManager(internalWorkflowStartupManager, startupListeners);

        initPlugins();

        if (build) {
            try {
                build();
            } catch (Exception e) {
                throw new RuntimeException("Error initializing ZwangineContext", e);
            }
        }
    }

    /**
     * Called during object construction to initialize context plugins
     */
    protected void initPlugins() {
        zwangineContextExtension.addContextPlugin(StartupConditionStrategy.class, createStartupConditionStrategy());
        zwangineContextExtension.addContextPlugin(ZwangineBeanPostProcessor.class, createBeanPostProcessor());
        zwangineContextExtension.addContextPlugin(ZwangineDependencyInjectionAnnotationFactory.class,
                createDependencyInjectionAnnotationFactory());
        zwangineContextExtension.addContextPlugin(ComponentResolver.class, createComponentResolver());
        zwangineContextExtension.addContextPlugin(ComponentNameResolver.class, createComponentNameResolver());
        zwangineContextExtension.addContextPlugin(LanguageResolver.class, createLanguageResolver());
        zwangineContextExtension.addContextPlugin(ConfigurerResolver.class, createConfigurerResolver());
        zwangineContextExtension.addContextPlugin(UriFactoryResolver.class, createUriFactoryResolver());
        zwangineContextExtension.addContextPlugin(FactoryFinderResolver.class, createFactoryFinderResolver());
        zwangineContextExtension.addContextPlugin(PackageScanClassResolver.class, createPackageScanClassResolver());
        zwangineContextExtension.addContextPlugin(PackageScanResourceResolver.class, createPackageScanResourceResolver());
        zwangineContextExtension.addContextPlugin(VariableRepositoryFactory.class, createVariableRepositoryFactory());
        zwangineContextExtension.lazyAddContextPlugin(ModelineFactory.class, this::createModelineFactory);
        zwangineContextExtension.lazyAddContextPlugin(ModelJAXBContextFactory.class, this::createModelJAXBContextFactory);
        zwangineContextExtension.addContextPlugin(DataFormatResolver.class, createDataFormatResolver());
        zwangineContextExtension.lazyAddContextPlugin(PeriodTaskResolver.class, this::createPeriodTaskResolver);
        zwangineContextExtension.lazyAddContextPlugin(PeriodTaskScheduler.class, this::createPeriodTaskScheduler);
        zwangineContextExtension.lazyAddContextPlugin(HealthCheckResolver.class, this::createHealthCheckResolver);
        zwangineContextExtension.lazyAddContextPlugin(ProcessorFactory.class, this::createProcessorFactory);
        zwangineContextExtension.lazyAddContextPlugin(InternalProcessorFactory.class, this::createInternalProcessorFactory);
        zwangineContextExtension.lazyAddContextPlugin(InterceptEndpointFactory.class, this::createInterceptEndpointFactory);
        zwangineContextExtension.lazyAddContextPlugin(WorkflowFactory.class, this::createWorkflowFactory);
        zwangineContextExtension.lazyAddContextPlugin(WorkflowsLoader.class, this::createWorkflowsLoader);
        zwangineContextExtension.lazyAddContextPlugin(AsyncProcessorAwaitManager.class, this::createAsyncProcessorAwaitManager);
        zwangineContextExtension.lazyAddContextPlugin(RuntimeZwangineCatalog.class, this::createRuntimeZwangineCatalog);
        zwangineContextExtension.lazyAddContextPlugin(RestBindingJaxbDataFormatFactory.class,
                this::createRestBindingJaxbDataFormatFactory);
        zwangineContextExtension.lazyAddContextPlugin(BeanProxyFactory.class, this::createBeanProxyFactory);
        zwangineContextExtension.lazyAddContextPlugin(UnitOfWorkFactory.class, this::createUnitOfWorkFactory);
        zwangineContextExtension.lazyAddContextPlugin(BeanIntrospection.class, this::createBeanIntrospection);
        zwangineContextExtension.lazyAddContextPlugin(ResourceLoader.class, this::createResourceLoader);
        zwangineContextExtension.lazyAddContextPlugin(BeanProcessorFactory.class, this::createBeanProcessorFactory);
        zwangineContextExtension.lazyAddContextPlugin(ModelToXMLDumper.class, this::createModelToXMLDumper);
        zwangineContextExtension.lazyAddContextPlugin(ModelToYAMLDumper.class, this::createModelToYAMLDumper);
        zwangineContextExtension.lazyAddContextPlugin(DeferServiceFactory.class, this::createDeferServiceFactory);
        zwangineContextExtension.lazyAddContextPlugin(AnnotationBasedProcessorFactory.class,
                this::createAnnotationBasedProcessorFactory);
        zwangineContextExtension.lazyAddContextPlugin(DumpWorkflowsStrategy.class, this::createDumpWorkflowsStrategy);
    }

    protected static <T> T lookup(ZwangineContext context, String ref, Class<T> type) {
        try {
            return context.getRegistry().lookupByNameAndType(ref, type);
        } catch (Exception e) {
            // need to ignore not same type and return it as null
            return null;
        }
    }

    public void close() throws IOException {
        try {
            stop();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public ZwangineContext getZwangineContextReference() {
        return this;
    }

    /**
     * Whether to eager create {@link TypeConverter} during initialization of ZwangineContext. This is enabled by default
     * to optimize zwangine-core.
     */
    protected boolean eagerCreateTypeConverter() {
        return true;
    }

    @Override
    public boolean isVetoStarted() {
        return vetoed != null;
    }

    @Override
    public ZwangineContextNameStrategy getNameStrategy() {
        return zwangineContextExtension.getNameStrategy();
    }

    @Override
    public void setNameStrategy(ZwangineContextNameStrategy nameStrategy) {
        zwangineContextExtension.setNameStrategy(nameStrategy);
    }

    @Override
    public ManagementNameStrategy getManagementNameStrategy() {
        return zwangineContextExtension.getManagementNameStrategy();
    }

    @Override
    public void setManagementNameStrategy(ManagementNameStrategy managementNameStrategy) {
        zwangineContextExtension.setManagementNameStrategy(managementNameStrategy);
    }

    @Override
    public String getManagementName() {
        return managementName;
    }

    @Override
    public void setManagementName(String managementName) {
        this.managementName = managementName;
    }

    @Override
    public Component hasComponent(String componentName) {
        if (components.isEmpty()) {
            return null;
        }
        return components.get(componentName);
    }

    @Override
    public void addComponent(String componentName, final Component component) {
        ObjectHelper.notNull(component, "component");
        component.setZwangineContext(getZwangineContextReference());
        if (isStarted()) {
            // start component if context is already started (zwangine will start components when it starts)
            ServiceHelper.startService(component);
        } else {
            // otherwise init the component
            ServiceHelper.initService(component);
        }
        Component oldValue = components.putIfAbsent(componentName, component);
        if (oldValue != null) {
            throw new IllegalArgumentException("Cannot add component as its already previously added: " + componentName);
        }
        postInitComponent(componentName, component);
    }

    private void postInitComponent(String componentName, final Component component) {
        for (LifecycleStrategy strategy : lifecycleStrategies) {
            strategy.onComponentAdd(componentName, component);
        }
    }

    @Override
    public Component getComponent(String name) {
        return getComponent(name, autoCreateComponents, true);
    }

    @Override
    public Component getComponent(String name, boolean autoCreateComponents) {
        return getComponent(name, autoCreateComponents, true);
    }

    @Override
    public Component getComponent(String name, boolean autoCreateComponents, boolean autoStart) {
        // ensure ZwangineContext are initialized before we can get a component
        build();

        // Check if the named component is already being created, that would mean
        // that the initComponent has triggered a new getComponent
        if (componentsInCreation.get().contains(name)) {
            throw new IllegalStateException(
                    "Circular dependency detected, the component " + name + " is already being created");
        }

        try {
            // Flag used to mark a component of being created.
            final AtomicBoolean created = new AtomicBoolean();

            // atomic operation to get/create a component. Avoid global locks.
            final Component component = components.computeIfAbsent(name, comp -> {
                created.set(true);
                return initComponent(name, autoCreateComponents);
            });

            // Start the component after its creation as if it is a component proxy
            // that creates/start a delegated component, we may end up in a deadlock
            if (component != null && created.get() && autoStart && (isStarted() || isStarting())) {
                // If the component is looked up after the context is started,
                // lets start it up.
                final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
                StartupStep step = startupStepRecorder.beginStep(Component.class, name, "Start Component");
                startService(component);
                startupStepRecorder.endStep(step);
            }

            return component;
        } catch (Exception e) {
            throw new RuntimeZwangineException("Cannot auto create component: " + name, e);
        } finally {
            // remove the reference to the component being created
            componentsInCreation.get().remove(name);
        }
    }

    /**
     * Function to initialize a component and auto start. Returns null if the autoCreateComponents is disabled
     */
    private Component initComponent(String name, boolean autoCreateComponents) {
        Component component = null;
        if (autoCreateComponents) {
            final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();

            StartupStep step = startupStepRecorder.beginStep(Component.class, name, "Resolve Component");
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Using ComponentResolver: {} to resolve component with name: {}",
                            PluginHelper.getComponentResolver(zwangineContextExtension), name);
                }

                // Mark the component as being created so we can detect circular
                // requests.
                //
                // In spring apps, the component resolver may trigger a new
                // getComponent because of the underlying bean factory and as
                // the endpoints are registered as singleton, the spring factory
                // creates the bean and then check the type so the getComponent
                // is always triggered.
                //
                // This would freeze the app (lock or infinite loop).
                //
                // See https://issues.zwangine.org/jira/browse/CAMEL-11225
                componentsInCreation.get().add(name);

                component = ResolverHelper.lookupComponentInRegistryWithFallback(getZwangineContextReference(), name);
                if (component == null) {
                    component = PluginHelper.getComponentResolver(zwangineContextExtension).resolveComponent(name,
                            getZwangineContextReference());
                }

                if (component != null) {
                    component.setZwangineContext(getZwangineContextReference());
                    ServiceHelper.buildService(component);
                    postInitComponent(name, component);
                }
            } catch (Exception e) {
                throw new RuntimeZwangineException("Cannot auto create component: " + name, e);
            }
            startupStepRecorder.endStep(step);
        }
        return component;
    }

    @Override
    public <T extends Component> T getComponent(String name, Class<T> componentType) {
        Component component = getComponent(name);
        if (componentType.isInstance(component)) {
            return componentType.cast(component);
        }

        final String message = invalidComponentMessage(name, componentType, component);
        throw new IllegalArgumentException(message);
    }

    private static <
            T extends Component> String invalidComponentMessage(String name, Class<T> componentType, Component component) {
        if (component == null) {
            return "Did not find component given by the name: " + name;
        } else {
            return "Found component of type: " + component.getClass() + " instead of expected: " + componentType;
        }
    }

    // Endpoint Management Methods
    // -----------------------------------------------------------------------

    @Override
    public Component removeComponent(String componentName) {
        Component oldComponent = components.remove(componentName);
        if (oldComponent != null) {
            try {
                stopServices(oldComponent);
            } catch (Exception e) {
                LOG.warn("Error stopping component {}. This exception will be ignored.", oldComponent, e);
            }
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onComponentRemove(componentName, oldComponent);
            }
        }
        return oldComponent;
    }

    @Override
    public EndpointRegistry getEndpointRegistry() {
        return endpoints;
    }

    @Override
    public Collection<Endpoint> getEndpoints() {
        return endpoints.getReadOnlyValues();
    }

    @Override
    public Endpoint hasEndpoint(String uri) {
        if (endpoints.isEmpty()) {
            return null;
        }
        return endpoints.get(getEndpointKey(uri));
    }

    @Override
    public Endpoint addEndpoint(String uri, Endpoint endpoint) throws Exception {
        Endpoint oldEndpoint;

        startService(endpoint);
        oldEndpoint = endpoints.remove(getEndpointKey(uri));
        for (LifecycleStrategy strategy : lifecycleStrategies) {
            strategy.onEndpointAdd(endpoint);
        }
        addEndpointToRegistry(uri, endpoint);
        if (oldEndpoint != null && oldEndpoint != endpoint) {
            stopServices(oldEndpoint);
        }

        return oldEndpoint;
    }

    @Override
    public void removeEndpoint(Endpoint endpoint) {
        Endpoint oldEndpoint = null;
        NormalizedEndpointUri oldKey = null;
        for (Map.Entry<NormalizedEndpointUri, Endpoint> entry : endpoints.entrySet()) {
            if (endpoint == entry.getValue()) {
                oldKey = entry.getKey();
                oldEndpoint = endpoint;
                break;
            }
        }
        if (oldEndpoint != null) {
            endpoints.remove(oldKey);
            try {
                stopServices(oldEndpoint);
            } catch (Exception e) {
                LOG.warn("Error stopping endpoint {}. This exception will be ignored.", oldEndpoint, e);
            }
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onEndpointRemove(oldEndpoint);
            }
        }
    }

    @Override
    public Collection<Endpoint> removeEndpoints(String uri) {
        Collection<Endpoint> answer = new ArrayList<>();
        Endpoint oldEndpoint = endpoints.remove(getEndpointKey(uri));
        if (oldEndpoint != null) {
            answer.add(oldEndpoint);
            stopServices(oldEndpoint);
        } else {
            final String decodeUri = URISupport.getDecodeQuery(uri);
            if (decodeUri != null) {
                oldEndpoint = endpoints.remove(getEndpointKey(decodeUri));
            }
            if (oldEndpoint != null) {
                answer.add(oldEndpoint);
                stopServices(oldEndpoint);
            } else {
                tryMatchingEndpoints(uri, answer);
            }
        }

        // notify lifecycle its being removed
        for (Endpoint endpoint : answer) {
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onEndpointRemove(endpoint);
            }
        }

        return answer;
    }

    private void tryMatchingEndpoints(String uri, Collection<Endpoint> answer) {
        Endpoint oldEndpoint;
        List<NormalizedEndpointUri> toRemove = new ArrayList<>();
        for (Map.Entry<NormalizedEndpointUri, Endpoint> entry : endpoints.entrySet()) {
            oldEndpoint = entry.getValue();
            if (EndpointHelper.matchEndpoint(this, oldEndpoint.getEndpointUri(), uri)) {
                try {
                    stopServices(oldEndpoint);
                } catch (Exception e) {
                    LOG.warn("Error stopping endpoint {}. This exception will be ignored.", oldEndpoint, e);
                }
                answer.add(oldEndpoint);
                toRemove.add(entry.getKey());
            }
        }
        for (NormalizedEndpointUri key : toRemove) {
            endpoints.remove(key);
        }
    }

    @Override
    public Endpoint getEndpoint(String uri) {
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        StartupStep step = null;
        // only record startup step during startup (not started)
        if (!isStarted() && startupStepRecorder.isEnabled()) {
            String u = URISupport.sanitizeUri(uri);
            step = startupStepRecorder.beginStep(Endpoint.class, u, "Get Endpoint");
        }
        Endpoint answer = doGetEndpoint(uri, null, false, false);
        if (step != null) {
            startupStepRecorder.endStep(step);
        }
        return answer;
    }

    @Override
    public Endpoint getEndpoint(String uri, Map<String, Object> parameters) {
        return doGetEndpoint(uri, parameters, false, false);
    }

    protected Endpoint doGetEndpoint(String uri, Map<String, Object> parameters, boolean normalized, boolean prototype) {
        // ensure ZwangineContext are initialized before we can get an endpoint
        build();

        StringHelper.notEmpty(uri, "uri");

        LOG.trace("Getting endpoint with uri: {} and parameters: {}", uri, parameters);

        if (!normalized) {
            // java 17 text blocks to single line uri
            uri = URISupport.textBlockToSingleLine(uri);
            // in case path has property placeholders then try to let property component resolve those
            uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(this, uri);
        }

        final String rawUri = uri;

        // normalize uri so we can do endpoint hits with minor mistakes and
        // parameters is not in the same order
        if (!normalized) {
            uri = EndpointHelper.normalizeEndpointUri(uri);
        }

        LOG.trace("Getting endpoint with raw uri: {}, normalized uri: {}", rawUri, uri);

        String scheme;
        Endpoint answer = null;
        if (!prototype) {
            // use optimized method to get the endpoint uri
            NormalizedUri key = NormalizedUri.newNormalizedUri(uri, true);
            // only lookup and reuse existing endpoints if not prototype scoped
            answer = endpoints.get(key);
        }
        if (answer == null) {
            try {
                scheme = StringHelper.before(uri, ":");
                if (scheme == null) {
                    // it may refer to a logical endpoint
                    answer = zwangineContextExtension.getRegistry().lookupByNameAndType(uri, Endpoint.class);
                    if (answer != null) {
                        return answer;
                    } else {
                        throw new NoSuchEndpointException(uri);
                    }
                }
                LOG.trace("Endpoint uri: {} is from component with name: {}", uri, scheme);
                Component component = getComponent(scheme);
                ServiceHelper.initService(component);

                // Ask the component to resolve the endpoint.
                if (component != null) {
                    LOG.trace("Creating endpoint from uri: {} using component: {}", uri, component);

                    // Have the component create the endpoint if it can.
                    answer = component.createEndpoint(
                            component.useRawUri() ? rawUri : uri,
                            parameters);

                    if (answer != null && LOG.isDebugEnabled()) {
                        LOG.debug("{} converted to endpoint: {} by component: {}", URISupport.sanitizeUri(uri), answer,
                                component);
                    }
                }

                if (answer == null) {
                    // no component then try in registry and elsewhere
                    answer = createEndpoint(uri);
                    LOG.trace("No component to create endpoint from uri: {} fallback lookup in registry -> {}", uri, answer);
                }

                if (answer != null) {
                    if (!prototype) {
                        addService(answer);
                        // register in registry
                        answer = addEndpointToRegistry(uri, answer);
                    } else {
                        addPrototypeService(answer);
                        // if there is endpoint strategies, then use the endpoints they return
                        // as this allows to intercept endpoints etc.
                        for (EndpointStrategy strategy : endpointStrategies) {
                            answer = strategy.registerEndpoint(uri, answer);
                        }
                    }
                }
            } catch (NoSuchEndpointException e) {
                // throw as-is
                throw e;
            } catch (Exception e) {
                throw new ResolveEndpointFailedException(uri, e);
            }
        }

        // unknown scheme
        if (answer == null) {
            throw new NoSuchEndpointException(uri);
        }

        return answer;
    }

    @Override
    public <T extends Endpoint> T getEndpoint(String name, Class<T> endpointType) {
        Endpoint endpoint = getEndpoint(name);
        if (endpoint == null) {
            throw new NoSuchEndpointException(name);
        }
        if (endpoint instanceof InterceptSendToEndpoint interceptSendToEndpoint) {
            endpoint = interceptSendToEndpoint.getOriginalEndpoint();
        }
        if (endpointType.isInstance(endpoint)) {
            return endpointType.cast(endpoint);
        } else {
            throw new IllegalArgumentException(
                    "The endpoint is not of type: " + endpointType + " but is: " + endpoint.getClass().getCanonicalName());
        }
    }

    /**
     * Strategy to add the given endpoint to the internal endpoint registry
     *
     * @param  uri      uri of the endpoint
     * @param  endpoint the endpoint to add
     * @return          the added endpoint
     */
    protected Endpoint addEndpointToRegistry(String uri, Endpoint endpoint) {
        StringHelper.notEmpty(uri, "uri");
        ObjectHelper.notNull(endpoint, "endpoint");

        // if there is endpoint strategies, then use the endpoints they return
        // as this allows to intercept endpoints etc.
        for (EndpointStrategy strategy : endpointStrategies) {
            endpoint = strategy.registerEndpoint(uri, endpoint);
        }
        endpoints.put(getEndpointKey(uri, endpoint), endpoint);
        return endpoint;
    }

    /**
     * Gets the endpoint key to use for lookup or whe adding endpoints to the {@link DefaultEndpointRegistry}
     *
     * @param  uri the endpoint uri
     * @return     the key
     */
    protected NormalizedUri getEndpointKey(String uri) {
        return NormalizedUri.newNormalizedUri(uri, false);
    }

    /**
     * Gets the endpoint key to use for lookup or whe adding endpoints to the {@link DefaultEndpointRegistry}
     *
     * @param  uri      the endpoint uri
     * @param  endpoint the endpoint
     * @return          the key
     */
    protected NormalizedUri getEndpointKey(String uri, Endpoint endpoint) {
        if (endpoint != null && !endpoint.isSingleton()) {
            int counter = endpointKeyCounter.incrementAndGet();
            return NormalizedUri.newNormalizedUri(uri + ":" + counter, false);
        } else {
            return NormalizedUri.newNormalizedUri(uri, false);
        }
    }

    // Workflow Management Methods
    // -----------------------------------------------------------------------

    @Override
    public GlobalEndpointConfiguration getGlobalEndpointConfiguration() {
        return globalEndpointConfiguration;
    }

    @Override
    public WorkflowController getWorkflowController() {
        return zwangineContextExtension.getWorkflowController();
    }

    @Override
    public void setWorkflowController(WorkflowController workflowController) {
        zwangineContextExtension.setWorkflowController(workflowController);
    }

    @Override
    public List<Workflow> getWorkflows() {
        // let's return a copy of the collection as objects are removed later
        // when services are stopped
        if (workflows.isEmpty()) {
            return Collections.emptyList();
        } else {
            workflowsLock.lock();
            try {
                return new ArrayList<>(workflows);
            } finally {
                workflowsLock.unlock();
            }
        }
    }

    @Override
    public int getWorkflowsSize() {
        return workflows.size();
    }

    @Override
    public Workflow getWorkflow(String id) {
        if (id != null) {
            for (Workflow workflow : getWorkflows()) {
                if (workflow.getId().equals(id)) {
                    return workflow;
                }
            }
        }
        return null;
    }

    @Override
    public Processor getProcessor(String id) {
        for (Workflow workflow : getWorkflows()) {
            List<Processor> list = workflow.filter(id);
            if (list.size() == 1) {
                return list.get(0);
            }
        }
        return null;
    }

    @Override
    public <T extends Processor> T getProcessor(String id, Class<T> type) {
        Processor answer = getProcessor(id);
        if (answer != null) {
            return type.cast(answer);
        }
        return null;
    }

    @Override
    public void addWorkflows(WorkflowsBuilder builder) throws Exception {
        // in case the builder is also a workflow configuration builder
        // then we need to add the configuration first
        if (builder instanceof WorkflowConfigurationsBuilder rcBuilder) {
            addWorkflowsConfigurations(rcBuilder);
        }
        try (LifecycleHelper helper = new LifecycleHelper()) {
            build();
            LOG.debug("Adding workflows from builder: {}", builder);
            builder.addWorkflowsToZwangineContext(this);
        }
    }

    @Override
    public void addTemplatedWorkflows(WorkflowsBuilder builder) throws Exception {
        try (LifecycleHelper helper = new LifecycleHelper()) {
            build();
            LOG.debug("Adding templated workflows from builder: {}", builder);
            builder.addTemplatedWorkflowsToZwangineContext(this);
        }
    }

    @Override
    public void addWorkflowsConfigurations(WorkflowConfigurationsBuilder builder) throws Exception {
        try (LifecycleHelper helper = new LifecycleHelper()) {
            build();
            LOG.debug("Adding workflow configurations from builder: {}", builder);
            builder.addWorkflowConfigurationsToZwangineContext(this);
        }
    }

    public ServiceStatus getWorkflowStatus(String key) {
        WorkflowService workflowService = workflowServices.get(key);
        if (workflowService != null) {
            return workflowService.getStatus();
        }
        return null;
    }

    public boolean isStartingWorkflows() {
        Boolean answer = isStartingWorkflows.get();
        return answer != null && answer;
    }

    public void setStartingWorkflows(boolean starting) {
        if (starting) {
            isStartingWorkflows.set(true);
        } else {
            isStartingWorkflows.remove();
        }
    }

    public boolean isLockModel() {
        Boolean answer = isLockModel.get();
        return answer != null && answer;
    }

    public void setLockModel(boolean lockModel) {
        if (lockModel) {
            isLockModel.set(true);
        } else {
            isLockModel.remove();
        }
    }

    public void startAllWorkflows() throws Exception {
        internalWorkflowStartupManager.doStartOrResumeWorkflows(this, workflowServices, true, true, false, false);

        if (startupSummaryLevel != StartupSummaryLevel.Oneline
                && startupSummaryLevel != StartupSummaryLevel.Off) {
            logWorkflowStartSummary(LoggingLevel.INFO);
        }
    }

    private void doStopWorkflows(WorkflowController controller, Comparator<WorkflowStartupOrder> comparator) throws Exception {
        List<WorkflowStartupOrder> workflowsOrdered = new ArrayList<>(zwangineContextExtension.getWorkflowStartupOrder());
        workflowsOrdered.sort(comparator);
        for (WorkflowStartupOrder order : workflowsOrdered) {
            Workflow workflow = order.getWorkflow();
            var status = controller.getWorkflowStatus(workflow.getWorkflowId());
            boolean stopped = status == null || status.isStopped();
            if (!stopped) {
                stopWorkflow(workflow.getWorkflowId(), LoggingLevel.DEBUG);
            }
        }
        // stop any remainder workflows
        for (Workflow workflow : getWorkflows()) {
            var status = controller.getWorkflowStatus(workflow.getWorkflowId());
            boolean stopped = status == null || status.isStopped();
            if (!stopped) {
                stopWorkflow(workflow.getWorkflowId(), LoggingLevel.DEBUG);
            }
        }
    }

    public void stopAllWorkflows() throws Exception {
        WorkflowController controller = getWorkflowController();
        if (controller == null) {
            // in case we are called during shutdown and controller is null
            return;
        }

        // stop all workflows in reverse order that they were started
        Comparator<WorkflowStartupOrder> comparator = Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder);

        final ShutdownStrategy shutdownStrategy = zwangineContextExtension.getShutdownStrategy();
        if (shutdownStrategy == null || shutdownStrategy.isShutdownWorkflowsInReverseOrder()) {
            comparator = comparator.reversed();
        }
        doStopWorkflows(controller, comparator);

        if (startupSummaryLevel != StartupSummaryLevel.Oneline
                && startupSummaryLevel != StartupSummaryLevel.Off) {
            logWorkflowStopSummary(LoggingLevel.INFO);
        }
    }

    public void removeAllWorkflows() throws Exception {
        // stop all workflows in reverse order that they were started
        Comparator<WorkflowStartupOrder> comparator = Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder);
        final ShutdownStrategy shutdownStrategy = getShutdownStrategy();
        if (shutdownStrategy == null || shutdownStrategy.isShutdownWorkflowsInReverseOrder()) {
            comparator = comparator.reversed();
        }
        doStopWorkflows(getWorkflowController(), comparator);

        // do not be noisy when removing workflows
        // as this is used by workflow-reload functionality, so lets be brief
        logWorkflowStopSummary(LoggingLevel.DEBUG);

        // remove all workflows
        for (Workflow workflow : getWorkflows()) {
            removeWorkflow(workflow.getWorkflowId(), LoggingLevel.DEBUG);
        }
    }

    public void startWorkflow(String workflowId) throws Exception {
        startWorkflow(workflowId, LoggingLevel.INFO);
    }

    public void startWorkflow(String workflowId, LoggingLevel loggingLevel) throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            WorkflowService workflowService = workflowServices.get(workflowId);
            if (workflowService != null) {
                try {
                    startWorkflowService(workflowService, false);
                    logWorkflowState(workflowService.getWorkflow(), "Started", loggingLevel);
                } catch (Exception e) {
                    DefaultWorkflowError.set(this, workflowId, Phase.START, e);
                    throw e;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    public void resumeWorkflow(String workflowId) throws Exception {
        resumeWorkflow(workflowId, LoggingLevel.INFO);
    }

    public void resumeWorkflow(String workflowId, LoggingLevel loggingLevel) throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            try {
                if (!workflowSupportsSuspension(workflowId)) {
                    // start workflow if suspension is not supported
                    startWorkflow(workflowId, loggingLevel);
                    return;
                }

                WorkflowService workflowService = workflowServices.get(workflowId);
                if (workflowService != null) {
                    resumeWorkflowService(workflowService);
                    // must resume the workflow as well
                    Workflow workflow = getWorkflow(workflowId);
                    ServiceHelper.resumeService(workflow);
                    logWorkflowState(workflowService.getWorkflow(), "Resumed", loggingLevel);
                }
            } catch (Exception e) {
                DefaultWorkflowError.set(this, workflowId, Phase.RESUME, e);
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }

    public boolean stopWorkflow(
            String workflowId, long timeout, TimeUnit timeUnit, boolean abortAfterTimeout, LoggingLevel loggingLevel)
            throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            WorkflowService workflowService = workflowServices.get(workflowId);
            if (workflowService != null) {
                try {
                    WorkflowStartupOrder workflow = new DefaultWorkflowStartupOrder(1, workflowService.getWorkflow(), workflowService);

                    boolean completed = zwangineContextExtension.getShutdownStrategy()
                            .shutdown(this, workflow, timeout, timeUnit, abortAfterTimeout);
                    if (completed) {
                        // must stop workflow service as well
                        stopWorkflowService(workflowService, false, loggingLevel);
                    } else {
                        // shutdown was aborted, make sure workflow is re-started properly
                        startWorkflowService(workflowService, false);
                    }
                    return completed;
                } catch (Exception e) {
                    DefaultWorkflowError.set(this, workflowId, Phase.STOP, e);
                    throw e;
                }
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    public void stopWorkflow(String workflowId) throws Exception {
        stopWorkflow(workflowId, LoggingLevel.INFO);
    }

    public void stopWorkflow(String workflowId, LoggingLevel loggingLevel) throws Exception {
        doShutdownWorkflow(workflowId, getShutdownStrategy().getTimeout(), getShutdownStrategy().getTimeUnit(), false, loggingLevel);
    }

    public void stopWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        doShutdownWorkflow(workflowId, timeout, timeUnit, false, LoggingLevel.INFO);
    }

    protected void doShutdownWorkflow(
            String workflowId, long timeout, TimeUnit timeUnit, boolean removingWorkflows, LoggingLevel loggingLevel)
            throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            WorkflowService workflowService = workflowServices.get(workflowId);
            if (workflowService != null) {
                try {
                    List<WorkflowStartupOrder> workflowList = new ArrayList<>(1);
                    WorkflowStartupOrder order = new DefaultWorkflowStartupOrder(1, workflowService.getWorkflow(), workflowService);
                    workflowList.add(order);

                    getShutdownStrategy().shutdown(this, workflowList, timeout, timeUnit);
                    // must stop workflow service as well (and remove the workflows from
                    // management)
                    stopWorkflowService(workflowService, removingWorkflows, loggingLevel);
                } catch (Exception e) {
                    DefaultWorkflowError.set(this, workflowId, removingWorkflows ? Phase.SHUTDOWN : Phase.STOP, e);
                    throw e;
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeWorkflow(String workflowId) throws Exception {
        lock.lock();
        try {
            return removeWorkflow(workflowId, LoggingLevel.INFO);
        } finally {
            lock.unlock();
        }
    }

    protected boolean removeWorkflow(String workflowId, LoggingLevel loggingLevel) throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            // gather a map of all the endpoints in use by the workflows, so we can
            // known if a given endpoints is in use
            // by one or more workflows, when we remove the workflow
            Map<String, Set<Endpoint>> endpointsInUse = new HashMap<>();
            for (Map.Entry<String, WorkflowService> entry : workflowServices.entrySet()) {
                endpointsInUse.put(entry.getKey(), entry.getValue().gatherEndpoints());
            }

            WorkflowService workflowService = workflowServices.get(workflowId);
            if (workflowService != null) {
                if (getWorkflowStatus(workflowId).isStopped()) {
                    try {
                        doRemove(workflowId, loggingLevel, workflowService, endpointsInUse);
                    } catch (Exception e) {
                        DefaultWorkflowError.set(this, workflowId, Phase.REMOVE, e);
                        throw e;
                    }

                    return true;
                } else {
                    return false;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void doRemove(
            String workflowId, LoggingLevel loggingLevel, WorkflowService workflowService, Map<String, Set<Endpoint>> endpointsInUse) {
        workflowService.setRemovingWorkflows(true);
        shutdownWorkflowService(workflowService, loggingLevel);
        workflowServices.remove(workflowId);
        // remove workflow from startup order as well, as it was
        // removed
        workflowStartupOrder.removeIf(order -> order.getWorkflow().getId().equals(workflowId));

        // from the workflow which we have removed, then remove all its
        // private endpoints
        // (eg the endpoints which are not in use by other workflows)
        Set<Endpoint> toRemove = new LinkedHashSet<>();
        for (Endpoint endpoint : endpointsInUse.get(workflowId)) {
            // how many times is the endpoint in use
            int count = 0;
            for (Set<Endpoint> endpointSet : endpointsInUse.values()) {
                if (endpointSet.contains(endpoint)) {
                    count++;
                }
            }
            // notice we will count ourselves so if there is only 1
            // then its safe to remove
            if (count <= 1) {
                toRemove.add(endpoint);
            }
        }
        for (Endpoint endpoint : toRemove) {
            LOG.debug("Removing: {} which was only in use by workflow: {}", endpoint, workflowId);
            removeEndpoint(endpoint);
        }
    }

    public void suspendWorkflow(String workflowId) throws Exception {
        suspendWorkflow(workflowId, getShutdownStrategy().getTimeout(), getShutdownStrategy().getTimeUnit());
    }

    public void suspendWorkflow(String workflowId, long timeout, TimeUnit timeUnit) throws Exception {
        lock.lock();
        try {
            DefaultWorkflowError.reset(this, workflowId);

            try {
                if (!workflowSupportsSuspension(workflowId)) {
                    stopWorkflow(workflowId, timeout, timeUnit);
                    return;
                }

                WorkflowService workflowService = workflowServices.get(workflowId);
                if (workflowService != null) {
                    List<WorkflowStartupOrder> workflowList = new ArrayList<>(1);
                    Workflow workflow = workflowService.getWorkflow();
                    WorkflowStartupOrder order = new DefaultWorkflowStartupOrder(1, workflow, workflowService);
                    workflowList.add(order);

                    getShutdownStrategy().suspend(this, workflowList, timeout, timeUnit);
                    // must suspend workflow service as well
                    suspendWorkflowService(workflowService);
                    // must suspend the workflow as well
                    if (workflow instanceof SuspendableService suspendableService) {
                        suspendableService.suspend();
                    }
                }
            } catch (Exception e) {
                DefaultWorkflowError.set(this, workflowId, Phase.SUSPEND, e);
                throw e;
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void addService(Object object) throws Exception {
        addService(object, true);
    }

    @Override
    public void addService(Object object, boolean stopOnShutdown) throws Exception {
        addService(object, stopOnShutdown, false);
    }

    @Override
    public void addService(Object object, boolean stopOnShutdown, boolean forceStart) throws Exception {
        internalServiceManager.doAddService(this, object, stopOnShutdown, forceStart, true);
    }

    @Override
    public void addPrototypeService(Object object) {
        internalServiceManager.addService(this, object, false, true, false);
    }

    @Override
    public boolean removeService(Object object) throws Exception {
        if (object instanceof Endpoint endpoint) {
            removeEndpoint(endpoint);
            return true;
        }
        if (object instanceof Service service) {
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onServiceRemove(this, service, null);
            }

            return internalServiceManager.removeService(service);
        }
        return false;
    }

    @Override
    public boolean hasService(Object object) {
        return internalServiceManager.hasService(object);
    }

    @Override
    public <T> T hasService(Class<T> type) {
        return internalServiceManager.hasService(type);
    }

    @Override
    public <T> Set<T> hasServices(Class<T> type) {
        return internalServiceManager.hasServices(type);
    }

    @Override
    public Service hasService(Predicate<Service> filter) {
        return internalServiceManager.getServices().stream().filter(filter).findFirst().orElse(null);
    }

    @Override
    public void deferStartService(Object object, boolean stopOnShutdown) {
        internalServiceManager.deferStartService(this, object, stopOnShutdown, false);
    }

    protected List<StartupListener> getStartupListeners() {
        return startupListeners;
    }

    @Override
    public void addStartupListener(StartupListener listener) throws Exception {
        // either add to listener so we can invoke then later when ZwangineContext
        // has been started
        // or invoke the callback right now
        if (isStarted()) {
            listener.onZwangineContextStarted(this, true);
        } else {
            startupListeners.add(listener);
        }
    }

    private static String toResourcePath(Package clazz, String languageName) {
        return LANGUAGE_RESOURCE + languageName + ".json";
    }

    private String doLoadResource(String resourceName, String path, String resourceType) throws IOException {
        final ClassResolver resolver = getClassResolver();
        try (InputStream inputStream = resolver.loadResourceAsStream(path)) {
            LOG.debug("Loading {} JSON Schema for: {} using class resolver: {} -> {}", resourceType, resourceName, resolver,
                    inputStream);
            if (inputStream != null) {
                return IOHelper.loadText(inputStream);
            }
        }
        return null;
    }

    @Override
    public String getComponentParameterJsonSchema(String componentName) throws IOException {
        // use the component factory finder to find the package name of the
        // component class, which is the location
        // where the documentation exists as well
        FactoryFinder finder = zwangineContextExtension.getFactoryFinder(DefaultComponentResolver.RESOURCE_PATH);
        Class<?> clazz = finder.findClass(componentName).orElse(null);
        if (clazz == null) {
            // fallback and find existing component
            Component existing = hasComponent(componentName);
            if (existing != null) {
                clazz = existing.getClass();
            } else {
                return null;
            }
        }

        String path = toResourcePath(clazz.getPackage(), componentName);

        String inputStream = doLoadResource(componentName, path, "component");
        if (inputStream != null) {
            return inputStream;
        }

        return null;
    }

    @Override
    public String getDataFormatParameterJsonSchema(String dataFormatName) throws IOException {
        // use the dataformat factory finder to find the package name of the
        // dataformat class, which is the location
        // where the documentation exists as well
        FactoryFinder finder = zwangineContextExtension.getFactoryFinder(DefaultDataFormatResolver.DATAFORMAT_RESOURCE_PATH);
        Class<?> clazz = finder.findClass(dataFormatName).orElse(null);
        if (clazz == null) {
            return null;
        }

        String path = toResourcePath(clazz.getPackage(), dataFormatName);

        String inputStream = doLoadResource(dataFormatName, path, "dataformat");
        if (inputStream != null) {
            return inputStream;
        }
        return null;
    }

    @Override
    public String getLanguageParameterJsonSchema(String languageName) throws IOException {
        // use the language factory finder to find the package name of the
        // language class, which is the location
        // where the documentation exists as well
        FactoryFinder finder = zwangineContextExtension.getFactoryFinder(DefaultLanguageResolver.LANGUAGE_RESOURCE_PATH);
        Class<?> clazz = finder.findClass(languageName).orElse(null);
        if (clazz == null) {
            return null;
        }

        String path = toResourcePath(clazz.getPackage(), languageName);

        String inputStream = doLoadResource(languageName, path, "language");
        if (inputStream != null) {
            return inputStream;
        }
        return null;
    }

    @Override
    public String getTransformerParameterJsonSchema(String transformerName) throws IOException {
        String name = sanitizeFileName(transformerName) + ".json";
        String path = DefaultTransformerResolver.DATA_TYPE_TRANSFORMER_RESOURCE_PATH + name;
        String inputStream = doLoadResource(transformerName, path, "transformer");
        if (inputStream != null) {
            return inputStream;
        }
        return null;
    }

    @Override
    public String getPojoBeanParameterJsonSchema(String beanName) throws IOException {
        String name = sanitizeFileName(beanName) + ".json";
        String path = "META-INF/services.org.zenithblox/bean/" + name;
        return doLoadResource(beanName, path, "bean");
    }

    // Helper methods
    // -----------------------------------------------------------------------

    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^A-Za-z0-9-]", "-");
    }

    @Override
    public String getEipParameterJsonSchema(String eipName) throws IOException {
        // the eip json schema may be in some of the sub-packages so look until
        // we find it
        String[] subPackages = new String[] {
                "", "cloud/", "config/", "dataformat/", "errorhandler/", "language/", "loadbalancer/", "rest/", "transformer/",
                "validator/" };
        for (String sub : subPackages) {
            String path = ZwangineContextHelper.MODEL_DOCUMENTATION_PREFIX + sub + eipName + ".json";
            String inputStream = doLoadResource(eipName, path, "eip");
            if (inputStream != null) {
                return inputStream;
            }
        }
        return null;
    }

    @Override
    public Language resolveLanguage(String name) {
        LOG.debug("Resolving language: {}", name);

        return languages.computeIfAbsent(name, s -> doResolveLanguage(name));
    }

    private Language doResolveLanguage(String name) {
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        StartupStep step = null;
        // only record startup step during startup (not started)
        if (!isStarted() && startupStepRecorder.isEnabled()) {
            step = startupStepRecorder.beginStep(Language.class, name, "Resolve Language");
        }

        final ZwangineContext zwangineContext = getZwangineContextReference();

        // as first iteration, check if there is a language instance for the given name
        // bound to the registry
        Language language = ResolverHelper.lookupLanguageInRegistryWithFallback(zwangineContext, name);

        if (language == null) {
            // language not known, then use resolver
            language = PluginHelper.getLanguageResolver(zwangineContextExtension).resolveLanguage(name, zwangineContext);
        }

        if (language != null) {
            if (language instanceof Service service) {
                try {
                    // init service first
                    ZwangineContextAware.trySetZwangineContext(service, zwangineContext);
                    ServiceHelper.initService(service);
                    startService(service);
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                }
            }

            // inject ZwangineContext if aware
            ZwangineContextAware.trySetZwangineContext(language, zwangineContext);

            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onLanguageCreated(name, language);
            }
        }

        if (step != null) {
            startupStepRecorder.endStep(step);
        }
        return language;
    }

    // Properties
    // -----------------------------------------------------------------------

    @Override
    public String resolvePropertyPlaceholders(String text) {
        return zwangineContextExtension.resolvePropertyPlaceholders(text, false);
    }

    @Override
    public Object getVariable(String name) {
        String id = StringHelper.before(name, ":", "global");
        name = StringHelper.after(name, ":", name);
        VariableRepository repo
                = zwangineContextExtension.getContextPlugin(VariableRepositoryFactory.class).getVariableRepository(id);
        if (repo != null) {
            return repo.getVariable(name);
        }
        return null;
    }

    @Override
    public <T> T getVariable(String name, Class<T> type) {
        Object value = getVariable(name);
        if (value != null) {
            return getTypeConverter().convertTo(type, value);
        }
        return null;
    }

    @Override
    public void setVariable(String name, Object value) {
        String id = StringHelper.before(name, ":", "global");
        name = StringHelper.after(name, ":", name);
        VariableRepository repo
                = zwangineContextExtension.getContextPlugin(VariableRepositoryFactory.class).getVariableRepository(id);
        if (repo != null) {
            repo.setVariable(name, value);
        }
    }

    @Override
    public TypeConverter getTypeConverter() {
        return zwangineContextExtension.getTypeConverter();
    }

    @Override
    public TypeConverterRegistry getTypeConverterRegistry() {
        return zwangineContextExtension.getTypeConverterRegistry();
    }

    @Override
    public void setTypeConverterRegistry(TypeConverterRegistry typeConverterRegistry) {
        zwangineContextExtension.setTypeConverterRegistry(typeConverterRegistry);
    }

    @Override
    public Injector getInjector() {
        return zwangineContextExtension.getInjector();
    }

    @Override
    public void setInjector(Injector injector) {
        zwangineContextExtension.setInjector(injector);
    }

    @Override
    public PropertiesComponent getPropertiesComponent() {
        return zwangineContextExtension.getPropertiesComponent();
    }

    @Override
    public void setPropertiesComponent(PropertiesComponent propertiesComponent) {
        zwangineContextExtension.setPropertiesComponent(propertiesComponent);
    }

    public void setAutoCreateComponents(boolean autoCreateComponents) {
        this.autoCreateComponents = autoCreateComponents;
    }

    @Override
    public <T> T getRegistry(Class<T> type) {
        Registry reg = zwangineContextExtension.getRegistry();

        if (type.isAssignableFrom(reg.getClass())) {
            return type.cast(reg);
        }
        return null;
    }

    @Override
    public List<LifecycleStrategy> getLifecycleStrategies() {
        return lifecycleStrategies;
    }

    @Override
    public void addLifecycleStrategy(LifecycleStrategy lifecycleStrategy) {
        // ensure zwangine context is injected in factory
        ZwangineContextAware.trySetZwangineContext(lifecycleStrategy, this);
        // avoid adding double which can happen with spring xml on spring boot
        if (!getLifecycleStrategies().contains(lifecycleStrategy)) {
            getLifecycleStrategies().add(lifecycleStrategy);
        }
    }

    @Override
    public RestConfiguration getRestConfiguration() {
        return zwangineContextExtension.getRestConfiguration();
    }

    @Override
    public void setRestConfiguration(RestConfiguration restConfiguration) {
        zwangineContextExtension.setRestConfiguration(restConfiguration);
    }

    @Override
    public VaultConfiguration getVaultConfiguration() {
        return vaultConfiguration;
    }

    @Override
    public void setVaultConfiguration(VaultConfiguration vaultConfiguration) {
        this.vaultConfiguration = vaultConfiguration;
    }

    @Override
    public List<WorkflowPolicyFactory> getWorkflowPolicyFactories() {
        return workflowPolicyFactories;
    }

    @Override
    public void addWorkflowPolicyFactory(WorkflowPolicyFactory workflowPolicyFactory) {
        // ensure zwangine context is injected in factory
        ZwangineContextAware.trySetZwangineContext(workflowPolicyFactory, this);
        // avoid adding double which can happen with spring xml on spring boot
        if (!getWorkflowPolicyFactories().contains(workflowPolicyFactory)) {
            getWorkflowPolicyFactories().add(workflowPolicyFactory);
        }
    }

    @Override
    public void setStreamCaching(Boolean cache) {
        this.streamCache = cache;
    }

    @Override
    public Boolean isStreamCaching() {
        return streamCache;
    }

    @Override
    public void setTracing(Boolean tracing) {
        this.trace = tracing;
    }

    @Override
    public Boolean isTracing() {
        return trace;
    }

    @Override
    public String getTracingPattern() {
        return tracePattern;
    }

    @Override
    public void setTracingPattern(String tracePattern) {
        this.tracePattern = tracePattern;
    }

    @Override
    public String getTracingLoggingFormat() {
        return tracingLoggingFormat;
    }

    @Override
    public void setTracingLoggingFormat(String format) {
        this.tracingLoggingFormat = format;
    }

    @Override
    public Boolean isBacklogTracing() {
        return backlogTrace;
    }

    @Override
    public void setBacklogTracing(Boolean backlogTrace) {
        this.backlogTrace = backlogTrace;
    }

    @Override
    public void setDebugStandby(boolean debugStandby) {
        this.debugStandby = debugStandby;
    }

    @Override
    public boolean isDebugStandby() {
        return debugStandby != null && debugStandby;
    }

    public void setDebuggingBreakpoints(String debugBreakpoints) {
        this.debugBreakpoints = debugBreakpoints;
    }

    public String getDebuggingBreakpoints() {
        return debugBreakpoints;
    }

    @Override
    public void setMessageHistory(Boolean messageHistory) {
        this.messageHistory = messageHistory;
    }

    @Override
    public Boolean isMessageHistory() {
        return messageHistory;
    }

    @Override
    public void setLogMask(Boolean logMask) {
        this.logMask = logMask;
    }

    @Override
    public Boolean isLogMask() {
        return logMask != null && logMask;
    }

    @Override
    public Boolean isLogExhaustedMessageBody() {
        return logExhaustedMessageBody;
    }

    @Override
    public void setLogExhaustedMessageBody(Boolean logExhaustedMessageBody) {
        this.logExhaustedMessageBody = logExhaustedMessageBody;
    }

    @Override
    public Long getDelayer() {
        return delay;
    }

    @Override
    public void setDelayer(Long delay) {
        this.delay = delay;
    }

    @Override
    public ProducerTemplate createProducerTemplate() {
        return createProducerTemplate(0);
    }

    @Override
    public ProducerTemplate createProducerTemplate(int maximumCacheSize) {
        DefaultProducerTemplate answer = new DefaultProducerTemplate(getZwangineContextReference());
        answer.setMaximumCacheSize(maximumCacheSize);
        // start it so its ready to use
        try {
            startService(answer);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    @Override
    public FluentProducerTemplate createFluentProducerTemplate() {
        return createFluentProducerTemplate(0);
    }

    @Override
    public FluentProducerTemplate createFluentProducerTemplate(int maximumCacheSize) {
        DefaultFluentProducerTemplate answer = new DefaultFluentProducerTemplate(getZwangineContextReference());
        answer.setMaximumCacheSize(maximumCacheSize);
        // start it so its ready to use
        try {
            startService(answer);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    @Override
    public ConsumerTemplate createConsumerTemplate() {
        return createConsumerTemplate(0);
    }

    @Override
    public ConsumerTemplate createConsumerTemplate(int maximumCacheSize) {
        DefaultConsumerTemplate answer = new DefaultConsumerTemplate(getZwangineContextReference());
        answer.setMaximumCacheSize(maximumCacheSize);
        // start it so its ready to use
        try {
            startService(answer);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    protected ScheduledExecutorService createErrorHandlerExecutorService() {
        return getExecutorServiceManager().newDefaultScheduledThreadPool("ErrorHandlerRedeliveryThreadPool",
                "ErrorHandlerRedeliveryTask");
    }

    @Override
    public RuntimeEndpointRegistry getRuntimeEndpointRegistry() {
        return runtimeEndpointRegistry;
    }

    @Override
    public void setRuntimeEndpointRegistry(RuntimeEndpointRegistry runtimeEndpointRegistry) {
        this.runtimeEndpointRegistry = internalServiceManager.addService(this, runtimeEndpointRegistry);
    }

    @Override
    public Duration getUptime() {
        EventClock<ContextEvents> contextClock = getClock();

        final Clock startClock = contextClock.get(ContextEvents.START);
        if (startClock == null) {
            return Duration.ZERO;
        }

        return startClock.asDuration();
    }

    @Override
    public String getVersion() {
        return VersionHolder.VERSION;
    }

    @Override
    public EventClock<ContextEvents> getClock() {
        return clock;
    }

    @Override
    protected void doSuspend() throws Exception {
        EventHelper.notifyZwangineContextSuspending(this);

        LOG.info(" Zwangine {} ({}) is suspending", getVersion(), zwangineContextExtension.getName());
        StopWatch watch = new StopWatch();

        // update list of started workflows to be suspended
        // because we only want to suspend started workflows
        // (so when we resume we only resume the workflows which actually was
        // suspended)
        for (Map.Entry<String, WorkflowService> entry : getWorkflowServices().entrySet()) {
            if (entry.getValue().getStatus().isStarted()) {
                suspendedWorkflowServices.put(entry.getKey(), entry.getValue());
            }
        }

        // assemble list of startup ordering so workflows can be shutdown
        // accordingly
        List<WorkflowStartupOrder> orders = new ArrayList<>();
        for (Map.Entry<String, WorkflowService> entry : suspendedWorkflowServices.entrySet()) {
            Workflow workflow = entry.getValue().getWorkflow();
            Integer order = workflow.getStartupOrder();
            if (order == null) {
                order = internalWorkflowStartupManager.incrementWorkflowStartupOrder();
            }
            orders.add(new DefaultWorkflowStartupOrder(order, workflow, entry.getValue()));
        }

        // suspend workflows using the shutdown strategy so it can shutdown in
        // correct order
        // workflows which doesn't support suspension will be stopped instead
        getShutdownStrategy().suspend(this, orders);

        // mark the workflow services as suspended or stopped
        for (WorkflowService service : suspendedWorkflowServices.values()) {
            if (workflowSupportsSuspension(service.getId())) {
                service.suspend();
            } else {
                service.stop();
            }
        }

        watch.taken();
        if (LOG.isInfoEnabled()) {
            LOG.info(" Zwangine {} ({}) is suspended in {}", getVersion(), zwangineContextExtension.getName(),
                    TimeUtils.printDuration(watch.taken(), true));
        }

        EventHelper.notifyZwangineContextSuspended(this);
    }

    // Implementation methods
    // -----------------------------------------------------------------------

    @Override
    protected void doResume() throws Exception {
        try {
            EventHelper.notifyZwangineContextResuming(this);

            LOG.info(" Zwangine {} ({}) is resuming", getVersion(), zwangineContextExtension.getName());
            StopWatch watch = new StopWatch();

            // start the suspended workflows (do not check for workflow clashes, and
            // indicate)
            internalWorkflowStartupManager.doStartOrResumeWorkflows(this, suspendedWorkflowServices, false, true, true, false);

            // mark the workflow services as resumed (will be marked as started) as
            // well
            for (WorkflowService service : suspendedWorkflowServices.values()) {
                if (workflowSupportsSuspension(service.getId())) {
                    service.resume();
                } else {
                    service.start();
                }
            }

            if (LOG.isInfoEnabled()) {
                LOG.info("Resumed {} workflows", suspendedWorkflowServices.size());
                LOG.info(" Zwangine {} ({}) resumed in {}", getVersion(), zwangineContextExtension.getName(),
                        TimeUtils.printDuration(watch.taken(), true));
            }

            // and clear the list as they have been resumed
            suspendedWorkflowServices.clear();

            EventHelper.notifyZwangineContextResumed(this);
        } catch (Exception e) {
            EventHelper.notifyZwangineContextResumeFailed(this, e);
            throw e;
        }
    }

    @Override
    protected AutoCloseable doLifecycleChange() {
        return new LifecycleHelper();
    }

    @Override
    public void init() {
        try {
            super.init();
        } catch (RuntimeZwangineException e) {
            if (e.getCause() instanceof VetoZwangineContextStartException veto) {
                vetoed = veto;
            } else {
                throw e;
            }
        }

        // was the initialization vetoed?
        if (vetoed != null) {
            LOG.warn("ZwangineContext ({}) vetoed to not initialize due to: {}", zwangineContextExtension.getName(),
                    vetoed.getMessage());
            failOnStartup(vetoed);
        }
    }

    @Override
    public void start() {
        super.start();

        //
        // We need to perform the following actions after the {@link #start()} method
        // is called, so that the state of the {@link ZwangineContext} is <code>Started<code>.
        //

        // did the start veto?
        if (vetoed != null) {
            LOG.warn("ZwangineContext ({}) vetoed to not start due to: {}", zwangineContextExtension.getName(), vetoed.getMessage());
            failOnStartup(vetoed);
            stop();
            return;
        }

        for (LifecycleStrategy strategy : lifecycleStrategies) {
            try {
                strategy.onContextStarted(this);
            } catch (Exception e) {
                LOG.warn("Lifecycle strategy {} failed on ZwangineContext ({}) due to: {}. This exception will be ignored",
                        strategy,
                        zwangineContextExtension.getName(),
                        e.getMessage());
            }
        }

        // okay the workflows has been started so emit event that ZwangineContext
        // has started (here at the end)
        EventHelper.notifyZwangineContextStarted(this);

        // now call the startup listeners where the workflows has been started
        for (StartupListener startup : startupListeners) {
            try {
                startup.onZwangineContextFullyStarted(this, isStarted());
            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeException(e);
            }
        }
    }

    @Override
    public void stop() {
        for (LifecycleStrategy strategy : lifecycleStrategies) {
            try {
                strategy.onContextStopping(this);
            } catch (Exception e) {
                LOG.warn("Lifecycle strategy {} failed on ZwangineContext ({}) due to: {}. This exception will be ignored",
                        strategy,
                        zwangineContextExtension.getName(),
                        e.getMessage());
            }
        }

        super.stop();
    }

    @Override
    public void doBuild() throws Exception {
        final StopWatch watch = new StopWatch();

        getZwangineContextExtension().addContextPlugin(NodeIdFactory.class, createNodeIdFactory());

        // auto-detect step recorder from classpath if none has been explicit configured
        StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        if (startupStepRecorder.getClass().getSimpleName().equals("DefaultStartupStepRecorder")) {
            StartupStepRecorder fr = zwangineContextExtension.getBootstrapFactoryFinder()
                    .newInstance(StartupStepRecorder.FACTORY, StartupStepRecorder.class).orElse(null);
            if (fr != null) {
                LOG.debug("Discovered startup recorder: {} from classpath", fr);
                zwangineContextExtension.setStartupStepRecorder(fr);
                startupStepRecorder = fr;
            }
        }

        startupStepRecorder.start();
        StartupStep step = startupStepRecorder.beginStep(ZwangineContext.class, null, "Build ZwangineContext");

        // Initialize LRUCacheFactory as eager as possible,
        // to let it warm up concurrently while Zwangine is startup up
        StartupStep subStep = startupStepRecorder.beginStep(ZwangineContext.class, null, "Setup LRUCacheFactory");
        LRUCacheFactory.init();
        startupStepRecorder.endStep(subStep);

        // Setup management first since end users may use it to add event
        // notifiers using the management strategy before the ZwangineContext has been started
        StartupStep step3 = startupStepRecorder.beginStep(ZwangineContext.class, null, "Setup Management");
        zwangineContextExtension.setupManagement(null);
        startupStepRecorder.endStep(step3);

        // setup health-check registry as its needed this early phase for 3rd party to register custom repositories
        HealthCheckRegistry hcr = getZwangineContextExtension().getContextPlugin(HealthCheckRegistry.class);
        if (hcr == null) {
            StartupStep step4 = startupStepRecorder.beginStep(ZwangineContext.class, null, "Setup HealthCheckRegistry");
            hcr = createHealthCheckRegistry();
            if (hcr != null) {
                // install health-check registry if it was discovered from classpath (zwangine-health)
                hcr.setZwangineContext(this);
                getZwangineContextExtension().addContextPlugin(HealthCheckRegistry.class, hcr);
            }
            startupStepRecorder.endStep(step4);
        }

        // Call all registered trackers with this context
        // Note, this may use a partially constructed object
        ZwangineContextTracker.notifyContextCreated(this);

        // Setup type converter eager as its highly in use and should not be lazy initialized
        if (eagerCreateTypeConverter()) {
            StartupStep step5 = startupStepRecorder.beginStep(ZwangineContext.class, null, "Setting up TypeConverter");
            zwangineContextExtension.getOrCreateTypeConverter();
            startupStepRecorder.endStep(step5);
        }

        startupStepRecorder.endStep(step);

        if (LOG.isDebugEnabled()) {
            buildTaken = watch.taken();
            LOG.debug(" Zwangine {} ({}) built in {}", getVersion(), zwangineContextExtension.getName(),
                    TimeUtils.printDuration(buildTaken, true));
        }
    }

    /**
     * Internal API to reset build time. Used by quarkus.
     */
    @SuppressWarnings("unused")
    protected void resetBuildTime() {
        // needed by zwangine-quarkus
        buildTaken = 0;
    }

    @Override
    public void doInit() throws Exception {
        final StopWatch watch = new StopWatch();

        vetoed = null;

        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        StartupStep step = startupStepRecorder.beginStep(ZwangineContext.class, null, "Init ZwangineContext");

        // init the workflow controller
        final WorkflowController workflowController = getWorkflowController();
        if (startupSummaryLevel == StartupSummaryLevel.Verbose) {
            // verbose startup should let workflow controller do the workflow startup logging
            if (workflowController.getLoggingLevel().ordinal() < LoggingLevel.INFO.ordinal()) {
                workflowController.setLoggingLevel(LoggingLevel.INFO);
            }
        }

        // init the shutdown strategy
        final ShutdownStrategy shutdownStrategy = getShutdownStrategy();
        if (startupSummaryLevel == StartupSummaryLevel.Verbose) {
            // verbose startup should let workflow controller do the workflow shutdown logging
            if (shutdownStrategy != null && shutdownStrategy.getLoggingLevel().ordinal() < LoggingLevel.INFO.ordinal()) {
                shutdownStrategy.setLoggingLevel(LoggingLevel.INFO);
            }
        }

        // optimize - before starting workflows lets check if event notifications are possible
        zwangineContextExtension.setEventNotificationApplicable(EventHelper.eventsApplicable(this));

        // ensure additional type converters is loaded (either if enabled or we should use package scanning from the base)
        boolean load = loadTypeConverters || zwangineContextExtension.getBasePackageScan() != null;
        final TypeConverter typeConverter = zwangineContextExtension.getTypeConverter();
        if (load && typeConverter instanceof AnnotationScanTypeConverters annotationScanTypeConverters) {
            StartupStep step2 = startupStepRecorder.beginStep(ZwangineContext.class, null, "Scan TypeConverters");
            annotationScanTypeConverters.scanTypeConverters();
            startupStepRecorder.endStep(step2);
        }

        // ensure additional health checks is loaded
        if (loadHealthChecks) {
            StartupStep step3 = startupStepRecorder.beginStep(ZwangineContext.class, null, "Scan HealthChecks");
            HealthCheckRegistry hcr = getZwangineContextExtension().getContextPlugin(HealthCheckRegistry.class);
            if (hcr != null) {
                hcr.loadHealthChecks();
            }
            startupStepRecorder.endStep(step3);
        }

        // custom properties may use property placeholders so resolve those
        // early on
        if (globalOptions != null && !globalOptions.isEmpty()) {
            for (Map.Entry<String, String> entry : globalOptions.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value != null) {
                    String replaced = resolvePropertyPlaceholders(value);
                    if (!value.equals(replaced)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Zwangine property with key {} replaced value from {} -> {}", key, value, replaced);
                        }
                        entry.setValue(replaced);
                    }
                }
            }
        }

        forceLazyInitialization();

        // setup cli-connector if not already done
        if (hasService(CliConnector.class) == null) {
            CliConnectorFactory ccf = getZwangineContextExtension().getContextPlugin(CliConnectorFactory.class);
            if (ccf != null && ccf.isEnabled()) {
                CliConnector connector = ccf.createConnector();
                addService(connector, true);
                // force start cli connector early as otherwise it will be deferred until context is started
                // but, we want status available during startup phase
                ServiceHelper.startService(connector);
            }
        }
        addService(getManagementStrategy(), false);

        // check startup conditions before we can continue
        StartupConditionStrategy scs = getZwangineContextExtension().getContextPlugin(StartupConditionStrategy.class);
        scs.checkStartupConditions();

        lifecycleStrategies.sort(OrderedComparator.get());
        ServiceHelper.initService(lifecycleStrategies);
        for (LifecycleStrategy strategy : lifecycleStrategies) {
            try {
                strategy.onContextInitializing(this);
            } catch (VetoZwangineContextStartException e) {
                // okay we should not start Zwangine since it was vetoed
                LOG.warn("Lifecycle strategy {} vetoed initializing ZwangineContext ({}) due to: {}", strategy,
                        zwangineContextExtension.getName(),
                        e.getMessage());
                throw e;
            } catch (Exception e) {
                LOG.warn("Lifecycle strategy {} failed initializing ZwangineContext ({}) due to: {}", strategy,
                        zwangineContextExtension.getName(),
                        e.getMessage());
                throw e;
            }
        }

        // optimize - before starting workflows lets check if event notifications are possible
        zwangineContextExtension.setEventNotificationApplicable(EventHelper.eventsApplicable(this));

        // start notifiers as services
        for (EventNotifier notifier : getManagementStrategy().getEventNotifiers()) {
            if (notifier instanceof Service service) {
                for (LifecycleStrategy strategy : lifecycleStrategies) {
                    strategy.onServiceAdd(getZwangineContextReference(), service, null);
                }
            }
            ServiceHelper.initService(notifier);
        }

        // the event notifiers must be initialized before we can emit this event
        EventHelper.notifyZwangineContextInitializing(this);

        // re-create endpoint registry as the cache size limit may be set after the constructor of this instance was called.
        // and we needed to create endpoints up-front as it may be accessed before this context is started
        endpoints = internalServiceManager.addService(this, createEndpointRegistry(endpoints));

        // optimised to not include runtimeEndpointRegistry unless startServices
        // is enabled or JMX statistics is in extended mode
        if (runtimeEndpointRegistry == null && getManagementStrategy() != null
                && getManagementStrategy().getManagementAgent() != null) {
            Boolean isEnabled = getManagementStrategy().getManagementAgent().getEndpointRuntimeStatisticsEnabled();
            boolean isExtended = getManagementStrategy().getManagementAgent().getStatisticsLevel().isExtended();
            // extended mode is either if we use Extended statistics level or
            // the option is explicit enabled
            boolean extended = isExtended || isEnabled != null && isEnabled;
            if (extended) {
                runtimeEndpointRegistry = new DefaultRuntimeEndpointRegistry();
            }
        }
        if (runtimeEndpointRegistry != null) {
            if (runtimeEndpointRegistry instanceof EventNotifier && getManagementStrategy() != null) {
                getManagementStrategy().addEventNotifier((EventNotifier) runtimeEndpointRegistry);
            }
            addService(runtimeEndpointRegistry, true, true);
        }

        bindDataFormats();

        // init components
        ServiceHelper.initService(components.values());

        // create workflow definitions from workflow templates if we have any sources
        for (WorkflowTemplateParameterSource source : zwangineContextExtension.getRegistry()
                .findByType(WorkflowTemplateParameterSource.class)) {
            for (String workflowId : source.workflowIds()) {
                // do a defensive copy of the parameters
                Map<String, Object> map = new HashMap<>(source.parameters(workflowId));
                Object templateId = map.remove(WorkflowTemplateParameterSource.TEMPLATE_ID);
                if (templateId == null) {
                    // use alternative style as well
                    templateId = map.remove("template-id");
                }
                final String id = templateId != null ? templateId.toString() : null;
                if (id == null) {
                    throw new IllegalArgumentException(
                            "WorkflowTemplateParameterSource with workflowId: " + workflowId + " has no templateId defined");
                }
                addWorkflowFromTemplate(workflowId, id, map);
            }
        }

        // init the workflow definitions before the workflows is started
        StartupStep subStep = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(), "Init Workflows");
        // the method is called start but at this point it will only initialize (as context is starting up)
        startWorkflowDefinitions();
        // this will init workflow definitions and populate as workflow services which we can then initialize now
        internalWorkflowStartupManager.doInitWorkflows(this, workflowServices);
        startupStepRecorder.endStep(subStep);

        if (!lifecycleStrategies.isEmpty()) {
            subStep = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(),
                    "LifecycleStrategy onContextInitialized");
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                try {
                    strategy.onContextInitialized(this);
                } catch (VetoZwangineContextStartException e) {
                    // okay we should not start Zwangine since it was vetoed
                    LOG.warn("Lifecycle strategy {} vetoed initializing ZwangineContext ({}) due to: {}", strategy,
                            zwangineContextExtension.getName(),
                            e.getMessage());
                    throw e;
                } catch (Exception e) {
                    LOG.warn("Lifecycle strategy {} failed initializing ZwangineContext ({}) due to: {}", strategy,
                            zwangineContextExtension.getName(),
                            e.getMessage());
                    throw e;
                }
            }
            startupStepRecorder.endStep(subStep);
        }

        EventHelper.notifyZwangineContextInitialized(this);

        startupStepRecorder.endStep(step);

        if (LOG.isDebugEnabled()) {
            initTaken = watch.taken();
            LOG.debug(" Zwangine {} ({}) initialized in {}", getVersion(), zwangineContextExtension.getName(),
                    TimeUtils.printDuration(initTaken, true));
        }
    }

    @Override
    protected void doStart() throws Exception {
        if (firstStartDone) {
            // its not good practice resetting a zwangine context
            LOG.warn("Starting ZwangineContext: {} after the context has been stopped is not recommended",
                    zwangineContextExtension.getName());
        }
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        StartupStep step
                = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(), "Start ZwangineContext");

        try {
            doStartContext();
        } catch (Exception e) {
            // fire event that we failed to start
            EventHelper.notifyZwangineContextStartupFailed(this, e);
            // rethrow cause
            throw e;
        }

        startupStepRecorder.endStep(step);

        // if we should only record the startup process then stop it right after started
        if (startupStepRecorder.getStartupRecorderDuration() < 0) {
            startupStepRecorder.stop();
        }
    }

    protected void doStartContext() throws Exception {
        LOG.info(" Zwangine {} ({}) is starting", getVersion(), zwangineContextExtension.getName());

        vetoed = null;
        clock.add(ContextEvents.START, new ResetableClock());
        stopWatch.restart();

        // Start the workflow controller
        startService(zwangineContextExtension.getWorkflowController());

        doNotStartWorkflowsOnFirstStart = !firstStartDone && !isAutoStartup();

        // if the context was configured with auto startup = false, and we
        // are already started,
        // then we may need to start the workflows on the 2nd start call
        if (firstStartDone && !isAutoStartup() && isStarted()) {
            // invoke this logic to warm up the workflows and if possible also
            // start the workflows
            try {
                internalWorkflowStartupManager.doStartOrResumeWorkflows(this, workflowServices, true, true, false, true);
            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeException(e);
            }
        }

        // super will invoke doStart which will prepare internal services
        // and start workflows etc.
        try {
            firstStartDone = true;
            doStartZwangine();
        } catch (Exception e) {
            VetoZwangineContextStartException veto = ObjectHelper.getException(VetoZwangineContextStartException.class, e);
            if (veto != null) {
                // mark we veto against starting Zwangine
                vetoed = veto;
                return;
            } else {
                LOG.error("Error starting ZwangineContext ({}) due to exception thrown: {}", zwangineContextExtension.getName(),
                        e.getMessage(), e);
                throw RuntimeZwangineException.wrapRuntimeException(e);
            }
        }

        // duplicate components in use?
        logDuplicateComponents();

        // log startup summary
        logStartSummary();

        // now Zwangine has been started/bootstrap is complete, then run cleanup to help free up memory etc
        zwangineContextExtension.closeBootstraps();

        if (zwangineContextExtension.getExchangeFactory().isPooled()) {
            LOG.info(
                    "Pooled mode enabled. Zwangine pools and reuses objects to reduce JVM object allocations. The pool capacity is: {} elements.",
                    zwangineContextExtension.getExchangeFactory().getCapacity());
        }
    }

    protected void logDuplicateComponents() {
        // output how many instances of the same component class are in use, as multiple instances is potential a mistake
        if (LOG.isInfoEnabled()) {
            Map<Class<?>, Set<String>> counters = new LinkedHashMap<>();
            // use TreeSet to sort the names
            Set<String> cnames = new TreeSet<>(getComponentNames());
            for (String sourceName : cnames) {
                Class<?> source = getComponent(sourceName).getClass();
                if (!counters.containsKey(source)) {
                    for (String targetName : cnames) {
                        Class<?> target = getComponent(targetName).getClass();
                        if (source == target) {
                            Set<String> names = counters.computeIfAbsent(source, k -> new TreeSet<>());
                            names.add(targetName);
                        }
                    }
                }
            }
            for (Map.Entry<Class<?>, Set<String>> entry : counters.entrySet()) {
                int count = entry.getValue().size();
                if (count > 1) {
                    String fqn = entry.getKey().getName();
                    String names = String.join(", ", entry.getValue());
                    LOG.info("Using {} instances of same component class: {} with names: {}", count,
                            fqn, names);
                }
            }
        }

    }

    protected void logStartSummary() {
        // supervising workflow controller should do their own startup log summary
        boolean supervised = getWorkflowController().isSupervising();
        if (!supervised && startupSummaryLevel != StartupSummaryLevel.Oneline && startupSummaryLevel != StartupSummaryLevel.Off
                && LOG.isInfoEnabled()) {
            int started = 0;
            int total = 0;
            int kamelets = 0;
            int templates = 0;
            int rests = 0;
            int disabled = 0;
            boolean registerKamelets = false;
            boolean registerTemplates = true;
            ManagementStrategy ms = getManagementStrategy();
            if (ms != null && ms.getManagementAgent() != null) {
                registerKamelets = ms.getManagementAgent().getRegisterWorkflowsCreateByKamelet();
                registerTemplates = ms.getManagementAgent().getRegisterWorkflowsCreateByTemplate();
            }
            List<String> lines = new ArrayList<>();
            List<String> configs = new ArrayList<>();
            workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder));
            for (WorkflowStartupOrder order : workflowStartupOrder) {
                total++;
                String id = order.getWorkflow().getWorkflowId();
                String status = getWorkflowStatus(id).name();
                if (order.getWorkflow().isCreatedByWorkflowTemplate()) {
                    templates++;
                } else if (order.getWorkflow().isCreatedByRestDsl()) {
                    rests++;
                }
                boolean skip = (!registerTemplates && order.getWorkflow().isCreatedByWorkflowTemplate());
                if (!skip && ServiceStatus.Started.name().equals(status)) {
                    started++;
                }

                // use basic endpoint uri to not log verbose details or potential sensitive data
                String uri = order.getWorkflow().getEndpoint().getEndpointBaseUri();
                uri = URISupport.sanitizeUri(uri);
                String loc = order.getWorkflow().getSourceLocationShort();
                if (startupSummaryLevel == StartupSummaryLevel.Verbose && loc != null) {
                    lines.add(String.format("    %s %s (%s) (source: %s)", status, id, uri, loc));
                } else {
                    if (!skip) {
                        lines.add(String.format("    %s %s (%s)", status, id, uri));
                    }
                }
                String cid = order.getWorkflow().getConfigurationId();
                if (cid != null) {
                    configs.add(String.format("    %s (%s)", id, cid));
                }
            }
            for (Workflow workflow : workflows) {
                if (!workflow.isAutoStartup()) {
                    total++;
                    disabled++;
                    String id = workflow.getWorkflowId();
                    String status = getWorkflowStatus(id).name();
                    if (ServiceStatus.Stopped.name().equals(status)) {
                        status = "Disabled";
                    }
                    else if (workflow.isCreatedByWorkflowTemplate()) {
                        templates++;
                    } else if (workflow.isCreatedByRestDsl()) {
                        rests++;
                    }
                    boolean skip = (!registerTemplates && workflow.isCreatedByWorkflowTemplate());
                    // use basic endpoint uri to not log verbose details or potential sensitive data
                    String uri = workflow.getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    String loc = workflow.getSourceLocationShort();
                    if (startupSummaryLevel == StartupSummaryLevel.Verbose && loc != null) {
                        lines.add(String.format("    %s %s (%s) (source: %s)", status, id, uri, loc));
                    } else {
                        if (!skip) {
                            lines.add(String.format("    %s %s (%s)", status, id, uri));
                        }
                    }

                    String cid = workflow.getConfigurationId();
                    if (cid != null) {
                        configs.add(String.format("    %s (%s)", id, cid));
                    }
                }
            }
            int newTotal = total;
            if (!registerKamelets) {
                newTotal -= kamelets;
            }
            if (!registerTemplates) {
                newTotal -= templates;
            }
            StringJoiner sj = new StringJoiner(" ");
            sj.add("total:" + newTotal);
            if (total != started) {
                sj.add("started:" + started);
            }
            if (kamelets > 0) {
                sj.add("kamelets:" + kamelets);
            }
            if (templates > 0) {
                sj.add("templates:" + templates);
            }
            if (rests > 0) {
                sj.add("rest-dsl:" + rests);
            }
            if (disabled > 0) {
                sj.add("disabled:" + disabled);
            }
            LOG.info("Workflows startup ({})", sj);
            // if we are default/verbose then log each workflow line
            if (startupSummaryLevel == StartupSummaryLevel.Default || startupSummaryLevel == StartupSummaryLevel.Verbose) {
                for (String line : lines) {
                    LOG.info(line);
                }
                if (startupSummaryLevel == StartupSummaryLevel.Verbose && !configs.isEmpty()) {
                    LOG.info("Workflows configuration:");
                    for (String line : configs) {
                        LOG.info(line);
                    }
                }
            }
        }

        if (startupSummaryLevel != StartupSummaryLevel.Off && LOG.isInfoEnabled()) {
            long taken = stopWatch.taken();
            long max = buildTaken + initTaken + taken;
            String total = TimeUtils.printDuration(max, true);
            String start = TimeUtils.printDuration(taken, true);
            String init = TimeUtils.printDuration(initTaken, true);
            String built = TimeUtils.printDuration(buildTaken, true);
            String boot = null;
            Clock bc = getClock().get(ContextEvents.BOOT);
            if (bc != null) {
                // calculate boot time as time before zwangine is starting
                long delta = bc.elapsed() - max;
                if (delta > 0) {
                    boot = TimeUtils.printDuration(delta, true);
                }
            }
            String msg = String.format(" Zwangine %s (%s) started in %s (build:%s init:%s start:%s", getVersion(),
                    zwangineContextExtension.getName(), total, built, init, start);
            if (boot != null) {
                msg += " boot:" + boot;
            }
            msg += ")";
            LOG.info(msg);
        }
    }

    protected void doStartZwangine() throws Exception {
        if (!zwangineContextExtension.getContextPlugin(ZwangineBeanPostProcessor.class).isEnabled()) {
            LOG.info("BeanPostProcessor is disabled. Dependency injection of Zwangine annotations in beans is not supported.");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(
                    "Using ClassResolver={}, PackageScanClassResolver={}, ApplicationContextClassLoader={}, WorkflowController={}",
                    getClassResolver(),
                    PluginHelper.getPackageScanClassResolver(zwangineContextExtension), getApplicationContextClassLoader(),
                    getWorkflowController());
        }
        if (isStreamCaching()) {
            // stream caching is default enabled so lets report if it has been disabled
            LOG.debug("StreamCaching is disabled on ZwangineContext: {}", zwangineContextExtension.getName());
        }
        if (isBacklogTracing()) {
            // tracing is added in the DefaultChannel so we can enable it on the fly
            LOG.debug("Backlog Tracing is enabled on ZwangineContext: {}", zwangineContextExtension.getName());
        }
        if (isTracing()) {
            // tracing is added in the DefaultChannel so we can enable it on the fly
            LOG.info("Tracing is enabled on ZwangineContext: {}", zwangineContextExtension.getName());
        }
        if (isUseMDCLogging()) {
            // log if MDC has been enabled
            String pattern = getMDCLoggingKeysPattern();
            if (pattern != null) {
                LOG.info("MDC logging (keys-pattern: {}) is enabled on ZwangineContext: {}", pattern,
                        zwangineContextExtension.getName());
            } else {
                LOG.info("MDC logging is enabled on ZwangineContext: {}", zwangineContextExtension.getName());
            }
        }
        if (getDelayer() != null && getDelayer() > 0) {
            LOG.info("Delayer is enabled with: {} ms. on ZwangineContext: {}", getDelayer(), zwangineContextExtension.getName());
        }

        // start management strategy before lifecycles are started
        startService(getManagementStrategy());

        // start lifecycle strategies
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        if (!lifecycleStrategies.isEmpty()) {
            StartupStep subStep
                    = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(),
                            "LifecycleStrategy onContextStarting");
            startServices(lifecycleStrategies);
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                try {
                    strategy.onContextStarting(this);
                } catch (VetoZwangineContextStartException e) {
                    // okay we should not start Zwangine since it was vetoed
                    LOG.warn("Lifecycle strategy {} vetoed starting ZwangineContext ({}) due to: {}", strategy,
                            zwangineContextExtension.getName(),
                            e.getMessage());
                    throw e;
                } catch (Exception e) {
                    LOG.warn("Lifecycle strategy {} failed starting ZwangineContext ({}) due to: {}", strategy,
                            zwangineContextExtension.getName(),
                            e.getMessage());
                    throw e;
                }
            }
            startupStepRecorder.endStep(subStep);
        }

        // start log listeners
        ServiceHelper.startService(getZwangineContextExtension().getLogListeners());

        // ensure components are started
        for (Map.Entry<String, Component> entry : components.entrySet()) {
            StartupStep step = startupStepRecorder.beginStep(Component.class, entry.getKey(), "Start Component");
            try {
                startService(entry.getValue());
            } catch (Exception e) {
                throw new FailedToStartComponentException(entry.getKey(), e.getMessage(), e);
            } finally {
                startupStepRecorder.endStep(step);
            }
        }

        if (!startupListeners.isEmpty()) {
            StartupStep subStep
                    = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(),
                            "StartupListener onZwangineContextStarting");
            // sort the startup listeners so they are started in the right order
            startupListeners.sort(OrderedComparator.get());
            // now call the startup listeners where the workflows has been warmed up
            // (only the actual workflow consumer has not yet been started)
            for (StartupListener startup : startupListeners) {
                startup.onZwangineContextStarting(getZwangineContextReference(), isStarted());
            }
            startupStepRecorder.endStep(subStep);
        }

        // start notifiers as services
        for (EventNotifier notifier : getManagementStrategy().getEventNotifiers()) {
            if (notifier instanceof Service service) {
                startService(service);
            }
        }

        // must let some bootstrap service be started before we can notify the starting event
        EventHelper.notifyZwangineContextStarting(this);

        if (isUseDataType()) {
            // log if DataType has been enabled
            LOG.debug("Message DataType is enabled on ZwangineContext: {}", zwangineContextExtension.getName());
        }

        // is there any stream caching enabled then log an info about this and
        // its limit of spooling to disk, so people is aware of this
        if (isStreamCachingInUse()) {
            // stream caching is in use so enable the strategy
            getStreamCachingStrategy().setEnabled(true);
        } else {
            // log if stream caching is not in use as this can help people to
            // enable it if they use streams
            LOG.debug("StreamCaching is not in use. If using streams then it's recommended to enable stream caching."
                      + " See more details at https://zwangine.zwangine.org/stream-caching.html");
        }

        if (isAllowUseOriginalMessage()) {
            LOG.debug("AllowUseOriginalMessage enabled because UseOriginalMessage is in use");
        }

        LOG.debug("Using HeadersMapFactory: {}", zwangineContextExtension.getHeadersMapFactory());
        if (isCaseInsensitiveHeaders() && !zwangineContextExtension.getHeadersMapFactory().isCaseInsensitive()) {
            LOG.info(
                    "HeadersMapFactory: {} is case-sensitive which can cause problems for protocols such as HTTP based, which rely on case-insensitive headers.",
                    zwangineContextExtension.getHeadersMapFactory());
        } else if (!isCaseInsensitiveHeaders()) {
            // notify user that the headers are sensitive which can be a problem
            LOG.info(
                    "Case-insensitive headers is not in use. This can cause problems for protocols such as HTTP based, which rely on case-insensitive headers.");
        }

        // lets log at INFO level if we are not using the default reactive executor
        final ReactiveExecutor reactiveExecutor = zwangineContextExtension.getReactiveExecutor();
        if (!reactiveExecutor.getClass().getSimpleName().equals("DefaultReactiveExecutor")) {
            LOG.info("Using ReactiveExecutor: {}", reactiveExecutor);
        } else {
            LOG.debug("Using ReactiveExecutor: {}", reactiveExecutor);
        }

        // lets log at INFO level if we are not using the default thread pool factory
        if (!getExecutorServiceManager().getThreadPoolFactory().getClass().getSimpleName().equals("DefaultThreadPoolFactory")) {
            LOG.info("Using ThreadPoolFactory: {}", getExecutorServiceManager().getThreadPoolFactory());
        } else {
            LOG.debug("Using ThreadPoolFactory: {}", getExecutorServiceManager().getThreadPoolFactory());
        }

        HealthCheckRegistry hcr = getZwangineContextExtension().getContextPlugin(HealthCheckRegistry.class);
        if (hcr != null && hcr.isEnabled()) {
            LOG.debug("Using HealthCheck: {}", hcr.getId());
        }

        // start workflows
        if (doNotStartWorkflowsOnFirstStart) {
            LOG.debug("Skip starting workflows as ZwangineContext has been configured with autoStartup=false");
        }

        if (getDumpWorkflows() != null && !"false".equals(getDumpWorkflows())) {
            doDumpWorkflows();
        }

        if (!getWorkflowController().isSupervising()) {
            // invoke this logic to warmup the workflows and if possible also start the workflows (using default workflow controller)
            StartupStep subStep
                    = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(), "Start Workflows");
            EventHelper.notifyZwangineContextWorkflowsStarting(this);
            internalWorkflowStartupManager.doStartOrResumeWorkflows(this, workflowServices, true, !doNotStartWorkflowsOnFirstStart, false,
                    true);
            EventHelper.notifyZwangineContextWorkflowsStarted(this);
            startupStepRecorder.endStep(subStep);
        }


        final BeanIntrospection beanIntrospection = PluginHelper.getBeanIntrospection(this);
        long cacheCounter = beanIntrospection != null ? beanIntrospection.getCachedClassesCounter() : 0;
        if (cacheCounter > 0) {
            LOG.debug("Clearing BeanIntrospection cache with {} objects using during starting Zwangine", cacheCounter);
            beanIntrospection.clearCache();
        }
        long invokedCounter = beanIntrospection != null ? beanIntrospection.getInvokedCounter() : 0;
        if (invokedCounter > 0) {
            LOG.debug("BeanIntrospection invoked {} times during starting Zwangine", invokedCounter);
        }
        // starting will continue in the start method
    }

    @Override
    protected void doStop() throws Exception {
        stopWatch.restart();
        final ShutdownStrategy shutdownStrategy = getShutdownStrategy();

        if (startupSummaryLevel != StartupSummaryLevel.Oneline && startupSummaryLevel != StartupSummaryLevel.Off) {
            if (shutdownStrategy != null && shutdownStrategy.getTimeUnit() != null) {
                long timeout = shutdownStrategy.getTimeUnit().toMillis(shutdownStrategy.getTimeout());
                // only use precise print duration if timeout is shorter than 10 seconds
                String to = TimeUtils.printDuration(timeout, timeout < 10000);
                LOG.info(" Zwangine {} ({}) is shutting down (timeout:{})", getVersion(), zwangineContextExtension.getName(),
                        to);
            } else {
                LOG.info(" Zwangine {} ({}) is shutting down", getVersion(), zwangineContextExtension.getName());
            }
        }

        EventHelper.notifyZwangineContextStopping(this);
        EventHelper.notifyZwangineContextWorkflowsStopping(this);

        // Stop the workflow controller
        zwangineContextExtension.stopAndShutdownWorkflowController();

        // stop workflow inputs in the same order as they were started, so we stop
        // the very first inputs at first
        try {
            // force shutting down workflows as they may otherwise cause shutdown to hang
            if (shutdownStrategy != null) {
                shutdownStrategy.shutdownForced(this, zwangineContextExtension.getWorkflowStartupOrder());
            }
        } catch (Exception e) {
            LOG.warn("Error occurred while shutting down workflows. This exception will be ignored.", e);
        }

        // shutdown await manager to trigger interrupt of blocked threads to
        // attempt to free these threads graceful
        final AsyncProcessorAwaitManager asyncProcessorAwaitManager = PluginHelper.getAsyncProcessorAwaitManager(this);
        InternalServiceManager.shutdownServices(this, asyncProcessorAwaitManager);

        // we need also to include workflows which failed to start to ensure all resources get stopped when stopping Zwangine
        for (WorkflowService workflowService : workflowServices.values()) {
            boolean found = workflowStartupOrder.stream().anyMatch(o -> o.getWorkflow().getId().equals(workflowService.getId()));
            if (!found) {
                LOG.debug("Workflow: {} which failed to startup will be stopped", workflowService.getId());
                workflowStartupOrder.add(internalWorkflowStartupManager.doPrepareWorkflowToBeStarted(this, workflowService));
            }
        }

        workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder).reversed());
        List<WorkflowService> list = new ArrayList<>();
        for (WorkflowStartupOrder startupOrder : workflowStartupOrder) {
            DefaultWorkflowStartupOrder order = (DefaultWorkflowStartupOrder) startupOrder;
            WorkflowService workflowService = order.getWorkflowService();
            list.add(workflowService);
        }
        InternalServiceManager.shutdownServices(this, list, false);

        if (startupSummaryLevel != StartupSummaryLevel.Oneline
                && startupSummaryLevel != StartupSummaryLevel.Off) {
            logWorkflowStopSummary(LoggingLevel.INFO);
        }

        // do not clear workflow services or startup listeners as we can start
        // Zwangine again and get the workflow back as before
        workflowStartupOrder.clear();

        EventHelper.notifyZwangineContextWorkflowsStopped(this);

        // but clear any suspend workflows
        suspendedWorkflowServices.clear();

        // stop consumers from the services to close first, such as POJO
        // consumer (eg @Consumer)
        // which we need to stop after the workflows, as a POJO consumer is
        // essentially a workflow also
        internalServiceManager.stopConsumers(this);

        // the stop order is important

        // shutdown default error handler thread pool
        final ScheduledExecutorService errorHandlerExecutorService = PluginHelper.getErrorHandlerExecutorService(this);
        if (errorHandlerExecutorService != null) {
            // force shutting down the thread pool
            getExecutorServiceManager().shutdownNow(errorHandlerExecutorService);
        }

        InternalServiceManager.shutdownServices(this, endpoints.values());
        endpoints.clear();

        InternalServiceManager.shutdownServices(this, components.values());
        components.clear();

        InternalServiceManager.shutdownServices(this, languages.values());
        languages.clear();

        // shutdown services as late as possible (except type converters as they may be needed during the remainder of the stopping)
        internalServiceManager.shutdownServices(this);

        // shutdown log listeners
        ServiceHelper.stopAndShutdownServices(getZwangineContextExtension().getLogListeners());

        try {
            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onContextStopped(this);
            }
        } catch (Exception e) {
            LOG.warn("Error occurred while stopping lifecycle strategies. This exception will be ignored.", e);
        }

        // must notify that we are stopped before stopping the management strategy
        EventHelper.notifyZwangineContextStopped(this);

        // stop the notifier service
        if (getManagementStrategy() != null) {
            for (EventNotifier notifier : getManagementStrategy().getEventNotifiers()) {
                InternalServiceManager.shutdownServices(this, notifier);
            }
        }

        // shutdown management and lifecycle after all other services
        InternalServiceManager.shutdownServices(this, zwangineContextExtension.getManagementStrategy());
        InternalServiceManager.shutdownServices(this, zwangineContextExtension.getManagementMBeanAssembler());
        InternalServiceManager.shutdownServices(this, lifecycleStrategies);
        // do not clear lifecycleStrategies as we can start Zwangine again and get
        // the workflow back as before

        // shutdown executor service, reactive executor last
        InternalServiceManager.shutdownServices(this, zwangineContextExtension.getExecutorServiceManager());
        InternalServiceManager.shutdownServices(this, zwangineContextExtension.getReactiveExecutor());

        // shutdown type converter and registry as late as possible
        zwangineContextExtension.stopTypeConverter();
        zwangineContextExtension.stopTypeConverterRegistry();
        zwangineContextExtension.stopRegistry();

        // stop the lazy created so they can be re-created on restart
        forceStopLazyInitialization();

        if (startupSummaryLevel != StartupSummaryLevel.Off) {
            if (LOG.isInfoEnabled()) {
                String taken = TimeUtils.printDuration(stopWatch.taken(), true);
                LOG.info(" Zwangine {} ({}) shutdown in {} (uptime:{})", getVersion(), zwangineContextExtension.getName(),
                        taken, ZwangineContextHelper.getUptime(this));
            }
        }

        // ensure any recorder is stopped in case it was kept running
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        startupStepRecorder.stop();

        // and clear start date
        clock.add(ContextEvents.START, null);

        // Call all registered trackers with this context
        // Note, this may use a partially constructed object
        ZwangineContextTracker.notifyContextDestroyed(this);

        firstStartDone = true;
    }

    @Override
    protected void doFail(Exception e) {
        super.doFail(e);
        // reset flag in case of startup fail as we want to be able to allow to start again
        firstStartDone = false;
    }

    protected void doDumpWorkflows() {
        // noop
    }

    protected void logWorkflowStopSummary(LoggingLevel loggingLevel) {
        ZwangineLogger logger = new ZwangineLogger(LOG, loggingLevel);
        if (logger.shouldLog()) {
            int total = 0;
            int stopped = 0;
            int forced = 0;
            int kamelets = 0;
            int templates = 0;
            int rests = 0;
            boolean registerKamelets = false;
            boolean registerTemplates = true;
            ManagementStrategy ms = getManagementStrategy();
            if (ms != null && ms.getManagementAgent() != null) {
                registerKamelets = ms.getManagementAgent().getRegisterWorkflowsCreateByKamelet();
                registerTemplates = ms.getManagementAgent().getRegisterWorkflowsCreateByTemplate();
            }
            List<String> lines = new ArrayList<>();

            final ShutdownStrategy shutdownStrategy = zwangineContextExtension.getShutdownStrategy();
            if (shutdownStrategy != null && shutdownStrategy.isShutdownWorkflowsInReverseOrder()) {
                workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder).reversed());
            } else {
                workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder));
            }
            for (WorkflowStartupOrder order : workflowStartupOrder) {
                total++;
                String id = order.getWorkflow().getWorkflowId();
                String status = getWorkflowStatus(id).name();
                if (order.getWorkflow().isCreatedByWorkflowTemplate()) {
                    templates++;
                } else if (order.getWorkflow().isCreatedByRestDsl()) {
                    rests++;
                }
                boolean skip = (!registerTemplates && order.getWorkflow().isCreatedByWorkflowTemplate());
                if (!skip && ServiceStatus.Stopped.name().equals(status)) {
                    stopped++;
                }
                if (order.getWorkflow().getProperties().containsKey("forcedShutdown")) {
                    forced++;
                    status = "Forced stopped";
                }
                // use basic endpoint uri to not log verbose details or potential sensitive data
                String uri = order.getWorkflow().getEndpoint().getEndpointBaseUri();
                uri = URISupport.sanitizeUri(uri);
                if (startupSummaryLevel == StartupSummaryLevel.Verbose || !skip) {
                    lines.add(String.format("    %s %s (%s)", status, id, uri));
                }
            }
            int newTotal = total;
            if (!registerKamelets) {
                newTotal -= kamelets;
            }
            if (!registerTemplates) {
                newTotal -= templates;
            }
            StringJoiner sj = new StringJoiner(" ");
            sj.add("total:" + newTotal);
            if (total != stopped) {
                sj.add("stopped:" + stopped);
            }
            if (kamelets > 0) {
                sj.add("kamelets:" + kamelets);
            }
            if (templates > 0) {
                sj.add("templates:" + templates);
            }
            if (rests > 0) {
                sj.add("rest-dsl:" + rests);
            }
            if (forced > 0) {
                sj.add("forced:" + forced);
            }
            logger.log(String.format("Workflows stopped (%s)", sj));
            // if we are default/verbose then log each workflow line
            if (startupSummaryLevel == StartupSummaryLevel.Default || startupSummaryLevel == StartupSummaryLevel.Verbose) {
                for (String line : lines) {
                    logger.log(line);
                }
            }
        }
    }

    protected void logWorkflowStartSummary(LoggingLevel loggingLevel) {
        ZwangineLogger logger = new ZwangineLogger(LOG, loggingLevel);
        if (logger.shouldLog()) {
            int total = 0;
            int started = 0;
            int kamelets = 0;
            int templates = 0;
            int rests = 0;
            boolean registerKamelets = false;
            boolean registerTemplates = true;
            ManagementStrategy ms = getManagementStrategy();
            if (ms != null && ms.getManagementAgent() != null) {
                registerKamelets = ms.getManagementAgent().getRegisterWorkflowsCreateByKamelet();
                registerTemplates = ms.getManagementAgent().getRegisterWorkflowsCreateByTemplate();
            }
            List<String> lines = new ArrayList<>();

            final ShutdownStrategy shutdownStrategy = zwangineContextExtension.getShutdownStrategy();
            if (shutdownStrategy != null && shutdownStrategy.isShutdownWorkflowsInReverseOrder()) {
                workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder).reversed());
            } else {
                workflowStartupOrder.sort(Comparator.comparingInt(WorkflowStartupOrder::getStartupOrder));
            }
            for (WorkflowStartupOrder order : workflowStartupOrder) {
                total++;
                String id = order.getWorkflow().getWorkflowId();
                String status = getWorkflowStatus(id).name();
                if (order.getWorkflow().isCreatedByWorkflowTemplate()) {
                    templates++;
                } else if (order.getWorkflow().isCreatedByRestDsl()) {
                    rests++;
                }
                boolean skip = (!registerTemplates && order.getWorkflow().isCreatedByWorkflowTemplate());
                if (!skip && ServiceStatus.Started.name().equals(status)) {
                    started++;
                }
                // use basic endpoint uri to not log verbose details or potential sensitive data
                String uri = order.getWorkflow().getEndpoint().getEndpointBaseUri();
                uri = URISupport.sanitizeUri(uri);
                if (startupSummaryLevel == StartupSummaryLevel.Verbose || !skip) {
                    lines.add(String.format("    %s %s (%s)", status, id, uri));
                }
            }
            int newTotal = total;
            if (!registerKamelets) {
                newTotal -= kamelets;
            }
            if (!registerTemplates) {
                newTotal -= templates;
            }
            StringJoiner sj = new StringJoiner(" ");
            sj.add("total:" + newTotal);
            if (total != started) {
                sj.add("started:" + started);
            }
            if (kamelets > 0) {
                sj.add("kamelets:" + kamelets);
            }
            if (templates > 0) {
                sj.add("templates:" + templates);
            }
            if (rests > 0) {
                sj.add("rest-dsl:" + rests);
            }
            logger.log(String.format("Workflows started (%s)", sj));
            // if we are default/verbose then log each workflow line
            if (startupSummaryLevel == StartupSummaryLevel.Default || startupSummaryLevel == StartupSummaryLevel.Verbose) {
                for (String line : lines) {
                    logger.log(line);
                }
            }
        }
    }

    public void startWorkflowDefinitions() throws Exception {
    }

    public void removeWorkflowDefinitionsFromTemplate() throws Exception {
    }

    protected boolean isStreamCachingInUse() throws Exception {
        return isStreamCaching();
    }

    protected void bindDataFormats() throws Exception {
    }

    protected boolean workflowSupportsSuspension(String workflowId) {
        WorkflowService workflowService = workflowServices.get(workflowId);
        if (workflowService != null) {
            return workflowService.getWorkflow().supportsSuspension();
        }
        return false;
    }

    void startService(Service service) throws Exception {
        // and register startup aware so they can be notified when
        // zwangine context has been started
        if (service instanceof StartupListener listener) {
            addStartupListener(listener);
        }

        ZwangineContextAware.trySetZwangineContext(service, getZwangineContextReference());
        ServiceHelper.startService(service);
    }

    private void startServices(Collection<?> services) throws Exception {
        for (Object element : services) {
            if (element instanceof Service service) {
                startService(service);
            }
        }
    }

    private void stopServices(Object service) {
        // allow us to do custom work before delegating to service helper
        try {
            ServiceHelper.stopService(service);
        } catch (Exception e) {
            // fire event
            EventHelper.notifyServiceStopFailure(getZwangineContextReference(), service, e);
            // rethrow to signal error with stopping
            throw e;
        }
    }

    /**
     * Starts the given workflow service
     */
    public void startWorkflowService(WorkflowService workflowService, boolean addingWorkflows) throws Exception {
        lock.lock();
        try {
            // we may already be starting workflows so remember this, so we can unset
            // accordingly in finally block
            boolean alreadyStartingWorkflows = isStartingWorkflows();
            if (!alreadyStartingWorkflows) {
                setStartingWorkflows(true);
            }

            try {
                // the workflow service could have been suspended, and if so then
                // resume it instead
                if (workflowService.getStatus().isSuspended()) {
                    resumeWorkflowService(workflowService);
                } else {
                    // start the workflow service
                    workflowServices.put(workflowService.getId(), workflowService);
                    if (shouldStartWorkflows()) {
                        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
                        StartupStep step
                                = startupStepRecorder.beginStep(Workflow.class, workflowService.getId(), "Start Workflow Services");
                        // this method will log the workflows being started
                        internalWorkflowStartupManager.safelyStartWorkflowServices(this, true, true, true, false, addingWorkflows,
                                workflowService);
                        // start workflow services if it was configured to auto startup
                        // and we are not adding workflows
                        boolean isAutoStartup = workflowService.isAutoStartup();
                        if (!addingWorkflows || isAutoStartup) {
                            // start the workflow since auto start is enabled or we are
                            // starting a workflow (not adding new workflows)
                            workflowService.start();
                        }
                        startupStepRecorder.endStep(step);
                    }
                }
            } finally {
                if (!alreadyStartingWorkflows) {
                    setStartingWorkflows(false);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Resumes the given workflow service
     */
    protected void resumeWorkflowService(WorkflowService workflowService) throws Exception {
        lock.lock();
        try {
            // the workflow service could have been stopped, and if so then start it
            // instead
            if (!workflowService.getStatus().isSuspended()) {
                startWorkflowService(workflowService, false);
            } else {
                // resume the workflow service
                if (shouldStartWorkflows()) {
                    // this method will log the workflows being started
                    internalWorkflowStartupManager.safelyStartWorkflowServices(this, true, false, true, true, false, workflowService);
                    // must resume workflow service as well
                    workflowService.resume();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    protected void stopWorkflowService(WorkflowService workflowService, boolean removingWorkflows, LoggingLevel loggingLevel)
            throws Exception {
        lock.lock();
        try {
            workflowService.setRemovingWorkflows(removingWorkflows);
            stopWorkflowService(workflowService, loggingLevel);
        } finally {
            lock.unlock();
        }
    }

    protected void logWorkflowState(Workflow workflow, String state, LoggingLevel loggingLevel) {
        lock.lock();
        try {
            ZwangineLogger logger = new ZwangineLogger(LOG, loggingLevel);
            if (logger.shouldLog()) {
                if (workflow.getConsumer() != null) {
                    String id = workflow.getId();
                    String uri = workflow.getEndpoint().getEndpointBaseUri();
                    uri = URISupport.sanitizeUri(uri);
                    String line = String.format("%s %s (%s)", state, id, uri);
                    logger.log(line);
                } else {
                    String id = workflow.getId();
                    String line = String.format("%s %s", state, id);
                    logger.log(line);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    protected void stopWorkflowService(WorkflowService workflowService, LoggingLevel loggingLevel) {
        lock.lock();
        try {
            workflowService.stop();
            logWorkflowState(workflowService.getWorkflow(), "Stopped", loggingLevel);
        } finally {
            lock.unlock();
        }
    }

    protected void shutdownWorkflowService(WorkflowService workflowService) throws Exception {
        lock.lock();
        try {
            shutdownWorkflowService(workflowService, LoggingLevel.INFO);
        } finally {
            lock.unlock();
        }
    }

    protected void shutdownWorkflowService(WorkflowService workflowService, LoggingLevel loggingLevel) {
        lock.lock();
        try {
            workflowService.shutdown();
            logWorkflowState(workflowService.getWorkflow(), "Shutdown", loggingLevel);
        } finally {
            lock.unlock();
        }
    }

    protected void suspendWorkflowService(WorkflowService workflowService) {
        lock.lock();
        try {
            workflowService.setRemovingWorkflows(false);
            workflowService.suspend();
            logWorkflowState(workflowService.getWorkflow(), "Suspended", LoggingLevel.INFO);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Force some lazy initialization to occur upfront before we start any components and create workflows
     */
    protected void forceLazyInitialization() {
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        StartupStep step = startupStepRecorder.beginStep(ZwangineContext.class, zwangineContextExtension.getName(),
                "Start Mandatory Services");
        initEagerMandatoryServices();
        startupStepRecorder.endStep(step);
        step = startupStepRecorder.beginStep(ZwangineContext.class, getName(), "Start Standard Services");
        doStartStandardServices();
        startupStepRecorder.endStep(step);
    }

    /**
     * Initializes eager some mandatory services which needs to warmup and be ready as this helps optimize Zwangine at
     * runtime.
     */
    protected void initEagerMandatoryServices() {
        zwangineContextExtension.initEagerMandatoryServices(isCaseInsensitiveHeaders(), this::createHeadersMapFactory);
    }

    protected void doStartStandardServices() {
        getVersion();
        getClassResolver();
        zwangineContextExtension.getRegistry();
        zwangineContextExtension.getBootstrapFactoryFinder();
        getTypeConverterRegistry();
        getInjector();
        zwangineContextExtension.getDefaultFactoryFinder();
        getPropertiesComponent();

        getExecutorServiceManager();
        zwangineContextExtension.getExchangeFactoryManager();
        zwangineContextExtension.getExchangeFactory();
        getShutdownStrategy();
        getUuidGenerator();

        // resolve simple language to initialize it
        resolveLanguage("simple");
    }

    /**
     * Force clear lazy initialization so they can be re-created on restart
     */
    protected void forceStopLazyInitialization() {
        zwangineContextExtension.resetInjector();
        zwangineContextExtension.resetTypeConverterRegistry();
        zwangineContextExtension.resetTypeConverter();
    }

    /**
     * A pluggable strategy to allow an endpoint to be created without requiring a component to be its factory, such as
     * for looking up the URI inside some {@link Registry}
     *
     * @param  uri the uri for the endpoint to be created
     * @return     the newly created endpoint or null if it could not be resolved
     */
    protected Endpoint createEndpoint(String uri) {
        Object value = zwangineContextExtension.getRegistry().lookupByName(uri);
        if (value instanceof Endpoint endpoint) {
            return endpoint;
        } else if (value instanceof Processor processor) {
            return new ProcessorEndpoint(uri, getZwangineContextReference(), processor);
        } else if (value != null) {
            return convertBeanToEndpoint(uri, value);
        }
        return null;
    }

    /**
     * Strategy method for attempting to convert the bean from a {@link Registry} to an endpoint using some kind of
     * transformation or wrapper
     *
     * @param  uri  the uri for the endpoint (and name in the registry)
     * @param  bean the bean to be converted to an endpoint, which will be not null
     * @return      a new endpoint
     */
    protected Endpoint convertBeanToEndpoint(String uri, Object bean) {
        throw new IllegalArgumentException("uri: " + uri + " bean: " + bean + " could not be converted to an Endpoint");
    }

    /**
     * Should we start newly added workflows?
     */
    protected boolean shouldStartWorkflows() {
        return isStarted() && !isStarting();
    }

    @Override
    public Map<String, String> getGlobalOptions() {
        return globalOptions;
    }

    @Override
    public void setGlobalOptions(Map<String, String> globalOptions) {
        this.globalOptions = globalOptions;
    }

    protected FactoryFinder createBootstrapFactoryFinder(String path) {
        return PluginHelper.getFactoryFinderResolver(zwangineContextExtension).resolveBootstrapFactoryFinder(getClassResolver(),
                path);
    }

    protected FactoryFinder createFactoryFinder(String path) {
        return PluginHelper.getFactoryFinderResolver(zwangineContextExtension).resolveFactoryFinder(getClassResolver(), path);
    }

    @Override
    public ClassResolver getClassResolver() {
        return zwangineContextExtension.getClassResolver();
    }

    @Override
    public void setClassResolver(ClassResolver classResolver) {
        zwangineContextExtension.setClassResolver(classResolver);
    }

    @Override
    public Set<String> getComponentNames() {
        return Collections.unmodifiableSet(components.keySet());
    }

    @Override
    public Set<String> getLanguageNames() {
        return Collections.unmodifiableSet(languages.keySet());
    }

    @Override
    public ManagementStrategy getManagementStrategy() {
        return zwangineContextExtension.getManagementStrategy();
    }

    @Override
    public void setManagementStrategy(ManagementStrategy managementStrategy) {
        zwangineContextExtension.setManagementStrategy(managementStrategy);
    }

    @Override
    public void disableJMX() {
        if (isNew()) {
            disableJMX = true;
        } else if (isInit() || isBuild()) {
            disableJMX = true;
            // we are still in initializing mode, so we can disable JMX, by
            // setting up management again
            zwangineContextExtension.setupManagement(null);
        } else {
            throw new IllegalStateException("Disabling JMX can only be done when ZwangineContext has not been started");
        }
    }

    public boolean isJMXDisabled() {
        String override = System.getProperty("jmxdisabled");
        if (override != null) {
            return Boolean.parseBoolean(override);
        }

        return disableJMX;
    }


    @Override
    public InflightRepository getInflightRepository() {
        return zwangineContextExtension.getInflightRepository();
    }

    @Override
    public void setInflightRepository(InflightRepository repository) {
        zwangineContextExtension.setInflightRepository(repository);
    }

    @Override
    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public Boolean isAutoStartup() {
        return autoStartup != null && autoStartup;
    }

    @Override
    public Boolean isLoadTypeConverters() {
        return loadTypeConverters != null && loadTypeConverters;
    }

    @Override
    public void setLoadTypeConverters(Boolean loadTypeConverters) {
        this.loadTypeConverters = loadTypeConverters;
    }

    @Override
    public Boolean isLoadHealthChecks() {
        return loadHealthChecks != null && loadHealthChecks;
    }

    @Override
    public void setLoadHealthChecks(Boolean loadHealthChecks) {
        this.loadHealthChecks = loadHealthChecks;
    }

    @Override
    public Boolean isModeline() {
        return modeline != null && modeline;
    }

    @Override
    public void setModeline(Boolean modeline) {
        this.modeline = modeline;
    }

    @Override
    public Boolean isTypeConverterStatisticsEnabled() {
        return typeConverterStatisticsEnabled != null && typeConverterStatisticsEnabled;
    }

    @Override
    public Boolean isSourceLocationEnabled() {
        return sourceLocationEnabled;
    }

    @Override
    public void setSourceLocationEnabled(Boolean sourceLocationEnabled) {
        this.sourceLocationEnabled = sourceLocationEnabled;
    }

    @Override
    public void setTypeConverterStatisticsEnabled(Boolean typeConverterStatisticsEnabled) {
        this.typeConverterStatisticsEnabled = typeConverterStatisticsEnabled;
    }

    @Override
    public String getDumpWorkflows() {
        return dumpWorkflows;
    }

    @Override
    public void setDumpWorkflows(String dumpWorkflows) {
        this.dumpWorkflows = dumpWorkflows;
    }

    @Override
    public Boolean isUseMDCLogging() {
        return useMDCLogging != null && useMDCLogging;
    }

    @Override
    public void setUseMDCLogging(Boolean useMDCLogging) {
        this.useMDCLogging = useMDCLogging;
    }

    @Override
    public String getMDCLoggingKeysPattern() {
        return mdcLoggingKeysPattern;
    }

    @Override
    public void setMDCLoggingKeysPattern(String pattern) {
        this.mdcLoggingKeysPattern = pattern;
    }

    @Override
    public Boolean isUseDataType() {
        return useDataType;
    }

    @Override
    public void setUseDataType(Boolean useDataType) {
        this.useDataType = useDataType;
    }

    @Override
    public Boolean isUseBreadcrumb() {
        return useBreadcrumb != null && useBreadcrumb;
    }

    @Override
    public void setUseBreadcrumb(Boolean useBreadcrumb) {
        this.useBreadcrumb = useBreadcrumb;
    }

    @Override
    public ClassLoader getApplicationContextClassLoader() {
        return applicationContextClassLoader;
    }

    @Override
    public void setApplicationContextClassLoader(ClassLoader classLoader) {
        applicationContextClassLoader = classLoader;
    }

    private DataFormat doResolveDataFormat(String name) {
        StartupStep step = null;
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();

        // only record startup step during startup (not started)
        if (!isStarted() && startupStepRecorder.isEnabled()) {
            step = startupStepRecorder.beginStep(DataFormat.class, name, "Resolve DataFormat");
        }

        final DataFormat df = Optional
                .ofNullable(ResolverHelper.lookupDataFormatInRegistryWithFallback(getZwangineContextReference(), name))
                .orElseGet(() -> PluginHelper.getDataFormatResolver(zwangineContextExtension).createDataFormat(name,
                        getZwangineContextReference()));

        if (df != null) {
            // inject ZwangineContext if aware
            ZwangineContextAware.trySetZwangineContext(df, getZwangineContextReference());

            for (LifecycleStrategy strategy : lifecycleStrategies) {
                strategy.onDataFormatCreated(name, df);
            }
        }

        if (step != null) {
            startupStepRecorder.endStep(step);
        }

        return df;
    }

    @Override
    public DataFormat resolveDataFormat(String name) {
        return dataformats.computeIfAbsent(name, s -> doResolveDataFormat(name));
    }

    @Override
    public DataFormat createDataFormat(String name) {
        StartupStep step = null;
        // only record startup step during startup (not started)
        final StartupStepRecorder startupStepRecorder = zwangineContextExtension.getStartupStepRecorder();
        if (!isStarted() && startupStepRecorder.isEnabled()) {
            step = startupStepRecorder.beginStep(DataFormat.class, name, "Create DataFormat");
        }

        DataFormat answer
                = PluginHelper.getDataFormatResolver(zwangineContextExtension).createDataFormat(name, getZwangineContextReference());

        // inject ZwangineContext if aware
        ZwangineContextAware.trySetZwangineContext(answer, getZwangineContextReference());

        for (LifecycleStrategy strategy : lifecycleStrategies) {
            strategy.onDataFormatCreated(name, answer);
        }

        if (step != null) {
            startupStepRecorder.endStep(step);
        }
        return answer;
    }

    @Override
    public Set<String> getDataFormatNames() {
        return Collections.unmodifiableSet(dataformats.keySet());
    }

    @Override
    public ShutdownStrategy getShutdownStrategy() {
        return zwangineContextExtension.getShutdownStrategy();
    }

    @Override
    public void setShutdownStrategy(ShutdownStrategy shutdownStrategy) {
        zwangineContextExtension.setShutdownStrategy(shutdownStrategy);
    }

    @Override
    public ShutdownWorkflow getShutdownWorkflow() {
        return shutdownWorkflow;
    }

    @Override
    public void setShutdownWorkflow(ShutdownWorkflow shutdownWorkflow) {
        this.shutdownWorkflow = shutdownWorkflow;
    }

    @Override
    public ShutdownRunningTask getShutdownRunningTask() {
        return shutdownRunningTask;
    }

    @Override
    public void setShutdownRunningTask(ShutdownRunningTask shutdownRunningTask) {
        this.shutdownRunningTask = shutdownRunningTask;
    }

    @Override
    public void setAllowUseOriginalMessage(Boolean allowUseOriginalMessage) {
        this.allowUseOriginalMessage = allowUseOriginalMessage;
    }

    @Override
    public Boolean isAllowUseOriginalMessage() {
        return allowUseOriginalMessage != null && allowUseOriginalMessage;
    }

    @Override
    public Boolean isCaseInsensitiveHeaders() {
        return caseInsensitiveHeaders != null && caseInsensitiveHeaders;
    }

    @Override
    public void setCaseInsensitiveHeaders(Boolean caseInsensitiveHeaders) {
        this.caseInsensitiveHeaders = caseInsensitiveHeaders;
    }

    @Override
    public Boolean isAutowiredEnabled() {
        return autowiredEnabled != null && autowiredEnabled;
    }

    @Override
    public void setAutowiredEnabled(Boolean autowiredEnabled) {
        this.autowiredEnabled = autowiredEnabled;
    }

    @Override
    public ExecutorServiceManager getExecutorServiceManager() {
        return zwangineContextExtension.getExecutorServiceManager();
    }

    @Override
    public void setExecutorServiceManager(ExecutorServiceManager executorServiceManager) {
        zwangineContextExtension.setExecutorServiceManager(executorServiceManager);
    }

    @Override
    public MessageHistoryFactory getMessageHistoryFactory() {
        return zwangineContextExtension.getMessageHistoryFactory();
    }

    @Override
    public void setMessageHistoryFactory(MessageHistoryFactory messageHistoryFactory) {
        zwangineContextExtension.setMessageHistoryFactory(messageHistoryFactory);

        // enable message history if we set a custom factory
        setMessageHistory(true);
    }

    @Override
    public Tracer getTracer() {
        return zwangineContextExtension.getTracer();
    }

    @Override
    public void setTracer(Tracer tracer) {
        // if tracing is in standby mode, then we can use it after zwangine is started
        if (!isTracingStandby() && isStartingOrStarted()) {
            throw new IllegalStateException("Cannot set tracer on a started ZwangineContext");
        }

        zwangineContextExtension.setTracer(tracer);
    }

    @Override
    public void setTracingStandby(boolean tracingStandby) {
        this.traceStandby = tracingStandby;
    }

    @Override
    public boolean isTracingStandby() {
        return traceStandby != null && traceStandby;
    }

    @Override
    public void setTracingTemplates(boolean tracingTemplates) {
        this.traceTemplates = tracingTemplates;
    }

    @Override
    public boolean isTracingTemplates() {
        return traceTemplates != null && traceTemplates;
    }

    @Override
    public void setBacklogTracingTemplates(boolean backlogTracingTemplates) {
        this.backlogTraceTemplates = backlogTracingTemplates;
    }

    @Override
    public boolean isBacklogTracingTemplates() {
        return backlogTraceTemplates != null && backlogTraceTemplates;
    }

    @Override
    public void setBacklogTracingStandby(boolean backlogTracingStandby) {
        this.backlogTraceStandby = backlogTracingStandby;
    }

    @Override
    public boolean isBacklogTracingStandby() {
        return backlogTraceStandby != null && backlogTraceStandby;
    }

    @Override
    public UuidGenerator getUuidGenerator() {
        return zwangineContextExtension.getUuidGenerator();
    }

    @Override
    public void setUuidGenerator(UuidGenerator uuidGenerator) {
        zwangineContextExtension.setUuidGenerator(uuidGenerator);
    }

    @Override
    public StreamCachingStrategy getStreamCachingStrategy() {
        return zwangineContextExtension.getStreamCachingStrategy();
    }

    @Override
    public void setStreamCachingStrategy(StreamCachingStrategy streamCachingStrategy) {
        zwangineContextExtension.setStreamCachingStrategy(streamCachingStrategy);
    }

    @Override
    public RestRegistry getRestRegistry() {
        return zwangineContextExtension.getRestRegistry();
    }

    @Override
    public void setRestRegistry(RestRegistry restRegistry) {
        zwangineContextExtension.setRestRegistry(restRegistry);
    }

    protected RestRegistry createRestRegistry() {
        RestRegistryFactory factory = zwangineContextExtension.getRestRegistryFactory();
        return factory.createRegistry();
    }

    @Override
    public String getGlobalOption(String key) {
        String value = getGlobalOptions().get(key);
        if (ObjectHelper.isNotEmpty(value)) {
            try {
                value = resolvePropertyPlaceholders(value);
            } catch (Exception e) {
                throw new RuntimeZwangineException("Error getting global option: " + key, e);
            }
        }
        return value;
    }

    @Override
    public Transformer resolveTransformer(String name) {
        return getTransformerRegistry().resolveTransformer(new TransformerKey(name));
    }

    @Override
    public Transformer resolveTransformer(DataType from, DataType to) {
        return getTransformerRegistry().resolveTransformer(new TransformerKey(from, to));
    }

    @Override
    public TransformerRegistry getTransformerRegistry() {
        return zwangineContextExtension.getTransformerRegistry();
    }

    @Override
    public Validator resolveValidator(DataType type) {
        return getValidatorRegistry().resolveValidator(new ValidatorKey(type));
    }

    @Override
    public ValidatorRegistry getValidatorRegistry() {
        return zwangineContextExtension.getValidatorRegistry();
    }

    @Override
    public SSLContextParameters getSSLContextParameters() {
        return this.sslContextParameters;
    }

    @Override
    public void setSSLContextParameters(SSLContextParameters sslContextParameters) {
        this.sslContextParameters = sslContextParameters;
    }

    @Override
    public StartupSummaryLevel getStartupSummaryLevel() {
        return startupSummaryLevel;
    }

    @Override
    public void setStartupSummaryLevel(StartupSummaryLevel startupSummaryLevel) {
        this.startupSummaryLevel = startupSummaryLevel;
    }

    protected Map<String, WorkflowService> getWorkflowServices() {
        return workflowServices;
    }

    @Override
    public String toString() {
        return "ZwangineContext(" + zwangineContextExtension.getName() + ")";
    }

    protected void failOnStartup(Exception e) {
        if (e instanceof VetoZwangineContextStartException vetoException) {
            if (vetoException.isRethrowException()) {
                fail(e);
            } else {
                // swallow exception and change state of this zwangine context to stopped
                status = FAILED;
            }
        } else {
            fail(e);
        }
    }

    protected abstract ExchangeFactory createExchangeFactory();

    protected abstract ExchangeFactoryManager createExchangeFactoryManager();

    protected abstract ProcessorExchangeFactory createProcessorExchangeFactory();

    protected abstract HealthCheckRegistry createHealthCheckRegistry();

    protected abstract ReactiveExecutor createReactiveExecutor();

    protected abstract StreamCachingStrategy createStreamCachingStrategy();

    protected abstract TypeConverter createTypeConverter();

    protected abstract TypeConverterRegistry createTypeConverterRegistry();

    protected abstract Injector createInjector();

    protected abstract PropertiesComponent createPropertiesComponent();

    protected abstract ZwangineBeanPostProcessor createBeanPostProcessor();

    protected abstract ZwangineDependencyInjectionAnnotationFactory createDependencyInjectionAnnotationFactory();

    protected abstract ComponentResolver createComponentResolver();

    protected abstract ComponentNameResolver createComponentNameResolver();

    protected abstract Registry createRegistry();

    protected abstract UuidGenerator createUuidGenerator();

    protected abstract ModelJAXBContextFactory createModelJAXBContextFactory();

    protected abstract NodeIdFactory createNodeIdFactory();

    protected abstract ModelineFactory createModelineFactory();

    protected abstract PeriodTaskResolver createPeriodTaskResolver();

    protected abstract PeriodTaskScheduler createPeriodTaskScheduler();

    protected abstract FactoryFinderResolver createFactoryFinderResolver();

    protected abstract ClassResolver createClassResolver();

    protected abstract ProcessorFactory createProcessorFactory();

    protected abstract InternalProcessorFactory createInternalProcessorFactory();

    protected abstract InterceptEndpointFactory createInterceptEndpointFactory();

    protected abstract WorkflowFactory createWorkflowFactory();

    protected abstract DataFormatResolver createDataFormatResolver();

    protected abstract HealthCheckResolver createHealthCheckResolver();


    protected abstract MessageHistoryFactory createMessageHistoryFactory();

    protected abstract InflightRepository createInflightRepository();

    protected abstract AsyncProcessorAwaitManager createAsyncProcessorAwaitManager();

    protected abstract WorkflowController createWorkflowController();

    protected abstract ShutdownStrategy createShutdownStrategy();

    protected abstract PackageScanClassResolver createPackageScanClassResolver();

    protected abstract PackageScanResourceResolver createPackageScanResourceResolver();

    protected abstract ExecutorServiceManager createExecutorServiceManager();

    protected abstract UnitOfWorkFactory createUnitOfWorkFactory();

    protected abstract ZwangineContextNameStrategy createZwangineContextNameStrategy();

    protected abstract ManagementNameStrategy createManagementNameStrategy();

    protected abstract HeadersMapFactory createHeadersMapFactory();

    protected abstract BeanProxyFactory createBeanProxyFactory();

    protected abstract AnnotationBasedProcessorFactory createAnnotationBasedProcessorFactory();

    protected abstract DeferServiceFactory createDeferServiceFactory();

    protected abstract BeanProcessorFactory createBeanProcessorFactory();

    protected abstract BeanIntrospection createBeanIntrospection();

    protected abstract WorkflowsLoader createWorkflowsLoader();

    protected abstract ResourceLoader createResourceLoader();

    protected abstract ModelToXMLDumper createModelToXMLDumper();

    protected abstract ModelToYAMLDumper createModelToYAMLDumper();

    protected abstract RestBindingJaxbDataFormatFactory createRestBindingJaxbDataFormatFactory();

    protected abstract RuntimeZwangineCatalog createRuntimeZwangineCatalog();

    protected abstract DumpWorkflowsStrategy createDumpWorkflowsStrategy();

    protected abstract Tracer createTracer();

    protected abstract LanguageResolver createLanguageResolver();

    protected abstract ConfigurerResolver createConfigurerResolver();

    protected abstract UriFactoryResolver createUriFactoryResolver();

    protected abstract RestRegistryFactory createRestRegistryFactory();

    protected abstract EndpointRegistry createEndpointRegistry(
            Map<NormalizedEndpointUri, Endpoint> endpoints);

    protected abstract TransformerRegistry createTransformerRegistry();

    protected abstract ValidatorRegistry createValidatorRegistry();

    protected abstract VariableRepositoryFactory createVariableRepositoryFactory();

    protected abstract EndpointServiceRegistry createEndpointServiceRegistry();

    protected abstract StartupConditionStrategy createStartupConditionStrategy();

    protected RestConfiguration createRestConfiguration() {
        // lookup a global which may have been on a container such spring-boot / CDI / etc.
        RestConfiguration conf
                = ZwangineContextHelper.lookup(this, RestConfiguration.DEFAULT_REST_CONFIGURATION_ID, RestConfiguration.class);
        if (conf == null) {
            conf = ZwangineContextHelper.findSingleByType(this, RestConfiguration.class);
        }
        if (conf == null) {
            conf = new RestConfiguration();
        }

        return conf;
    }

    public abstract Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception;

    @Deprecated
    public abstract void disposeModel();

    public abstract String getTestExcludeWorkflows();

    @Override
    public ExtendedZwangineContext getZwangineContextExtension() {
        return zwangineContextExtension;
    }

    @Override
    public String getName() {
        return zwangineContextExtension.getName();
    }

    @Override
    public String getDescription() {
        return zwangineContextExtension.getDescription();
    }

    public void addWorkflow(Workflow workflow) {
        workflowsLock.lock();
        try {
            workflows.add(workflow);
        } finally {
            workflowsLock.unlock();
        }
    }

    public void removeWorkflow(Workflow workflow) {
        workflowsLock.lock();
        try {
            workflows.remove(workflow);
        } finally {
            workflowsLock.unlock();
        }
    }

    protected Lock getLock() {
        return lock;
    }

    byte getStatusPhase() {
        return status;
    }

    class LifecycleHelper implements AutoCloseable {
        final Map<String, String> originalContextMap;
        final ClassLoader tccl;

        LifecycleHelper() {
            // Using the ApplicationClassLoader as the default for TCCL
            tccl = Thread.currentThread().getContextClassLoader();
            if (applicationContextClassLoader != null) {
                Thread.currentThread().setContextClassLoader(applicationContextClassLoader);
            }
            if (isUseMDCLogging()) {
                originalContextMap = MDC.getCopyOfContextMap();
                MDC.put(MDC_ZWANGINE_CONTEXT_ID, zwangineContextExtension.getName());
            } else {
                originalContextMap = null;
            }
        }

        @Override
        public void close() {
            if (isUseMDCLogging()) {
                if (originalContextMap != null) {
                    MDC.setContextMap(originalContextMap);
                } else {
                    MDC.clear();
                }
            }
            Thread.currentThread().setContextClassLoader(tccl);
        }
    }

    @Override
    public Registry getRegistry() {
        return zwangineContextExtension.getRegistry();
    }

    Set<EndpointStrategy> getEndpointStrategies() {
        return endpointStrategies;
    }

    List<WorkflowStartupOrder> getWorkflowStartupOrder() {
        return workflowStartupOrder;
    }

    InternalServiceManager getInternalServiceManager() {
        return internalServiceManager;
    }

    /*
     * This method exists for testing purposes only: we need to make sure we don't leak bootstraps.
     * This allows us to check for leaks without compromising the visibility/access on the DefaultZwangineContextExtension.
     * Check the test AddWorkflowsAtRuntimeTest for details.
     */
    @SuppressWarnings("unused")
    private List<BootstrapCloseable> getBootstraps() {
        return zwangineContextExtension.getBootstraps();
    }
}
