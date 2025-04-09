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
import org.zenithblox.health.HealthCheckRegistry;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.impl.converter.DefaultTypeConverter;
import org.zenithblox.spi.*;
import org.zenithblox.support.DefaultRegistry;
import org.zenithblox.support.DefaultUuidGenerator;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResolverHelper;
import org.zenithblox.support.startup.DefaultStartupConditionStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zenithblox.util.KeyValuePair;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents the context used to configure workflows and the policies to use.
 */
public class SimpleZwangineContext extends AbstractZwangineContext {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleZwangineContext.class);

    /**
     * Creates the {@link ZwangineContext} using {@link DefaultRegistry} as registry.
     * <p/>
     * Use one of the other constructors to force use an explicit registry.
     */
    public SimpleZwangineContext() {
        this(true);
    }

    /**
     * Creates the {@link ZwangineContext} and allows to control whether the context should automatic initialize or not.
     * <p/>
     * Note: Not for end users - this method is used internally by zwangine-blueprint/zwangine-cdi
     *
     * @param init whether to automatic initialize.
     */
    public SimpleZwangineContext(boolean init) {
        super(init);
    }

    @Override
    public void disposeModel() {
        // noop
    }

    @Override
    public void doBuild() throws Exception {
        super.doBuild();

        getZwangineContextExtension().addContextPlugin(CliConnectorFactory.class, createCliConnectorFactory());
        getZwangineContextExtension().addContextPlugin(ScheduledExecutorService.class, createErrorHandlerExecutorService());
    }

    @Override
    protected HealthCheckRegistry createHealthCheckRegistry() {
        Optional<HealthCheckRegistry> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                HealthCheckRegistry.FACTORY,
                HealthCheckRegistry.class);

        return result.orElse(null);
    }

    @Override
    protected TypeConverter createTypeConverter() {
        return new DefaultTypeConverter(
                getZwangineContextReference(), PluginHelper.getPackageScanClassResolver(this), getInjector(),
                isLoadTypeConverters(), isTypeConverterStatisticsEnabled());
    }

    @Override
    protected TypeConverterRegistry createTypeConverterRegistry() {
        TypeConverter typeConverter = getTypeConverter();
        // type converter is also registry so create type converter
        if (typeConverter == null) {
            typeConverter = createTypeConverter();
        }
        if (typeConverter instanceof TypeConverterRegistry typeConverterRegistry) {
            return typeConverterRegistry;
        }
        return null;
    }

    @Override
    protected Injector createInjector() {
        FactoryFinder finder = getZwangineContextExtension().getBootstrapFactoryFinder();
        Optional<Injector> result = finder.newInstance("Injector", Injector.class);
        return result.orElseGet(() -> new DefaultInjector(getZwangineContextReference()));
    }

    @Override
    protected PropertiesComponent createPropertiesComponent() {
        Optional<PropertiesComponent> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                PropertiesComponent.FACTORY,
                PropertiesComponent.class);

        return result.orElseGet(org.zenithblox.component.properties.PropertiesComponent::new);
    }

    @Override
    protected ZwangineBeanPostProcessor createBeanPostProcessor() {
        return new DefaultZwangineBeanPostProcessor(getZwangineContextReference());
    }

    @Override
    protected ZwangineDependencyInjectionAnnotationFactory createDependencyInjectionAnnotationFactory() {
        return new DefaultDependencyInjectionAnnotationFactory(getZwangineContextReference());
    }

    @Override
    protected ComponentResolver createComponentResolver() {
        return new DefaultComponentResolver();
    }

    @Override
    protected ComponentNameResolver createComponentNameResolver() {
        return new DefaultComponentNameResolver();
    }

    @Override
    protected Registry createRegistry() {
        return new DefaultRegistry();
    }

    @Override
    protected UuidGenerator createUuidGenerator() {
        return new DefaultUuidGenerator();
    }

    @Override
    protected ModelJAXBContextFactory createModelJAXBContextFactory() {
        Optional<ModelJAXBContextFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ModelJAXBContextFactory.FACTORY,
                ModelJAXBContextFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find ModelJAXBContextFactory on classpath. Add zwangine-xml-jaxb to classpath.");
        }
    }

    @Override
    protected NodeIdFactory createNodeIdFactory() {
        return new DefaultNodeIdFactory();
    }

    @Override
    protected ModelineFactory createModelineFactory() {
        Optional<ModelineFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ModelineFactory.FACTORY,
                ModelineFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find ModelineFactory on classpath. Add zwangine-dsl-modeline to classpath.");
        }
    }

    @Override
    protected PeriodTaskResolver createPeriodTaskResolver() {
        // we need a factory finder
        FactoryFinder finder = PluginHelper.getFactoryFinderResolver(getZwangineContextExtension())
                .resolveBootstrapFactoryFinder(getClassResolver(), PeriodTaskResolver.RESOURCE_PATH);
        return new DefaultPeriodTaskResolver(finder);
    }

    @Override
    protected PeriodTaskScheduler createPeriodTaskScheduler() {
        return new DefaultPeriodTaskScheduler();
    }


    @Override
    public void onExchange(KeyValuePair<String , Exchange> exchange) {
        if(callback != null) {
            callback.supplyExchange(exchange);
        }
    }

    @Override
    protected FactoryFinderResolver createFactoryFinderResolver() {
        return new DefaultFactoryFinderResolver();
    }

    @Override
    protected ClassResolver createClassResolver() {
        return new DefaultClassResolver(getZwangineContextReference());
    }

    @Override
    protected ProcessorFactory createProcessorFactory() {
        Optional<ProcessorFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ProcessorFactory.FACTORY,
                ProcessorFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find ProcessorFactory on classpath. Add zwangine-core-processor to classpath.");
        }
    }

    @Override
    protected InternalProcessorFactory createInternalProcessorFactory() {
        Optional<InternalProcessorFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                InternalProcessorFactory.FACTORY,
                InternalProcessorFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find InternalProcessorFactory on classpath. Add zwangine-core-processor to classpath.");
        }
    }

    @Override
    protected InterceptEndpointFactory createInterceptEndpointFactory() {
        return new DefaultInterceptEndpointFactory();
    }

    @Override
    protected WorkflowFactory createWorkflowFactory() {
        return new DefaultWorkflowFactory();
    }

    @Override
    protected DataFormatResolver createDataFormatResolver() {
        return new DefaultDataFormatResolver();
    }

    @Override
    protected HealthCheckResolver createHealthCheckResolver() {
        return new DefaultHealthCheckResolver();
    }

    @Override
    protected MessageHistoryFactory createMessageHistoryFactory() {
        return new DefaultMessageHistoryFactory();
    }

    @Override
    protected InflightRepository createInflightRepository() {
        return new DefaultInflightRepository();
    }

    @Override
    protected AsyncProcessorAwaitManager createAsyncProcessorAwaitManager() {
        return new DefaultAsyncProcessorAwaitManager();
    }

    @Override
    protected WorkflowController createWorkflowController() {
        return new DefaultWorkflowController(getZwangineContextReference());
    }

    @Override
    protected ShutdownStrategy createShutdownStrategy() {
        return new DefaultShutdownStrategy(getZwangineContextReference());
    }

    @Override
    protected PackageScanClassResolver createPackageScanClassResolver() {
        PackageScanClassResolver packageScanClassResolver;
        // use WebSphere specific resolver if running on WebSphere
        if (WebSpherePackageScanClassResolver.isWebSphereClassLoader(this.getClass().getClassLoader())) {
            LOG.info("Using WebSphere specific PackageScanClassResolver");
            packageScanClassResolver
                    = new WebSpherePackageScanClassResolver("META-INF/services.org.zenithblox/TypeConverter");
        } else {
            packageScanClassResolver = new DefaultPackageScanClassResolver();
        }
        return packageScanClassResolver;
    }

    @Override
    protected PackageScanResourceResolver createPackageScanResourceResolver() {
        return new DefaultPackageScanResourceResolver();
    }

    @Override
    protected UnitOfWorkFactory createUnitOfWorkFactory() {
        return new DefaultUnitOfWorkFactory();
    }

    @Override
    protected RuntimeZwangineCatalog createRuntimeZwangineCatalog() {
        Optional<RuntimeZwangineCatalog> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                RuntimeZwangineCatalog.FACTORY,
                RuntimeZwangineCatalog.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find RuntimeZwangineCatalog on classpath. Add zwangine-core-catalog to classpath.");
        }
    }

    @Override
    protected DumpWorkflowsStrategy createDumpWorkflowsStrategy() {
        DumpWorkflowsStrategy answer = getZwangineContextReference().hasService(DumpWorkflowsStrategy.class);
        if (answer != null) {
            return answer;
        }

        // is there any custom which we prioritize over default
        Optional<DumpWorkflowsStrategy> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                DumpWorkflowsStrategy.FACTORY,
                DumpWorkflowsStrategy.class);

        if (result.isEmpty()) {
            // lookup default factory
            result = ResolverHelper.resolveService(
                    getZwangineContextReference(),
                    getZwangineContextExtension().getBootstrapFactoryFinder(),
                    "default-" + DumpWorkflowsStrategy.FACTORY,
                    DumpWorkflowsStrategy.class);
        }

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find DumpWorkflowsStrategy on classpath. Add zwangine-core-engine to classpath.");
        }
    }

    @Override
    protected ZwangineContextNameStrategy createZwangineContextNameStrategy() {
        return new DefaultZwangineContextNameStrategy();
    }

    @Override
    protected ManagementNameStrategy createManagementNameStrategy() {
        return new DefaultManagementNameStrategy(getZwangineContextReference());
    }

    @Override
    protected HeadersMapFactory createHeadersMapFactory() {
        Optional<HeadersMapFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                HeadersMapFactory.FACTORY,
                HeadersMapFactory.class);

        return result.orElseGet(DefaultHeadersMapFactory::new);
    }

    private CliConnectorFactory createCliConnectorFactory() {
        // lookup in registry first
        CliConnectorFactory ccf = getZwangineContextReference().getRegistry().findSingleByType(CliConnectorFactory.class);
        if (ccf != null) {
            return ccf;
        }
        // then classpath scanning
        Optional<CliConnectorFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                CliConnectorFactory.FACTORY,
                CliConnectorFactory.class);
        // cli-connector is optional
        return result.orElse(null);
    }

    @Override
    protected BeanProxyFactory createBeanProxyFactory() {
        Optional<BeanProxyFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                BeanProxyFactory.FACTORY,
                BeanProxyFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException("Cannot find BeanProxyFactory on classpath. Add zwangine-bean to classpath.");
        }
    }

    @Override
    protected AnnotationBasedProcessorFactory createAnnotationBasedProcessorFactory() {
        Optional<AnnotationBasedProcessorFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                AnnotationBasedProcessorFactory.FACTORY,
                AnnotationBasedProcessorFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find AnnotationBasedProcessorFactory on classpath. Add zwangine-core-processor to classpath.");
        }
    }

    @Override
    protected DeferServiceFactory createDeferServiceFactory() {
        Optional<DeferServiceFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                DeferServiceFactory.FACTORY,
                DeferServiceFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find DeferServiceFactory on classpath. Add zwangine-core-processor to classpath.");
        }
    }

    @Override
    protected BeanProcessorFactory createBeanProcessorFactory() {
        Optional<BeanProcessorFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                BeanProcessorFactory.FACTORY,
                BeanProcessorFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException("Cannot find BeanProcessorFactory on classpath. Add zwangine-bean to classpath.");
        }
    }

    @Override
    protected BeanIntrospection createBeanIntrospection() {
        return new DefaultBeanIntrospection();
    }

    @Override
    protected WorkflowsLoader createWorkflowsLoader() {
        Optional<WorkflowsLoader> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                WorkflowsLoader.FACTORY,
                WorkflowsLoader.class);

        return result.orElseGet(DefaultWorkflowsLoader::new);
    }

    @Override
    protected ResourceLoader createResourceLoader() {
        Optional<ResourceLoader> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ResourceLoader.FACTORY,
                ResourceLoader.class);

        return result.orElseGet(DefaultResourceLoader::new);
    }

    @Override
    protected ModelToXMLDumper createModelToXMLDumper() {
        Optional<ModelToXMLDumper> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ModelToXMLDumper.FACTORY,
                ModelToXMLDumper.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException("Cannot find ModelToXMLDumper on classpath. Add zwangine-xml-io to classpath.");
        }
    }

    @Override
    protected ModelToYAMLDumper createModelToYAMLDumper() {
        Optional<ModelToYAMLDumper> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ModelToYAMLDumper.FACTORY,
                ModelToYAMLDumper.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException("Cannot find ModelToYAMLDumper on classpath. Add zwangine-yaml-io to classpath.");
        }
    }

    @Override
    protected RestBindingJaxbDataFormatFactory createRestBindingJaxbDataFormatFactory() {
        Optional<RestBindingJaxbDataFormatFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                RestBindingJaxbDataFormatFactory.FACTORY,
                RestBindingJaxbDataFormatFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException(
                    "Cannot find RestBindingJaxbDataFormatFactory on classpath. Add zwangine-jaxb to classpath.");
        }
    }

    @Override
    protected Tracer createTracer() {
        Tracer tracer = null;
        if (getRegistry() != null) {
            // lookup in registry
            tracer = getRegistry().findSingleByType(Tracer.class);
        }
        if (tracer == null) {
            tracer = getZwangineContextExtension().getContextPlugin(Tracer.class);
        }
        if (tracer == null) {
            tracer = new DefaultTracer();
            tracer.setEnabled(isTracing());
            tracer.setStandby(isTracingStandby());
            // enable both rest/templates if templates is enabled (we only want 1 public option)
            boolean restOrTemplates = isTracingTemplates();
            tracer.setTraceTemplates(restOrTemplates);
            tracer.setTraceRests(restOrTemplates);
            getZwangineContextExtension().addContextPlugin(Tracer.class, tracer);
        }
        return tracer;
    }

    @Override
    protected LanguageResolver createLanguageResolver() {
        return new DefaultLanguageResolver();
    }

    @Override
    protected ConfigurerResolver createConfigurerResolver() {
        return new DefaultConfigurerResolver();
    }

    @Override
    protected UriFactoryResolver createUriFactoryResolver() {
        return new DefaultUriFactoryResolver();
    }

    @Override
    protected RestRegistryFactory createRestRegistryFactory() {
        Optional<RestRegistryFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                RestRegistryFactory.FACTORY,
                RestRegistryFactory.class);

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new IllegalArgumentException("Cannot find RestRegistryFactory on classpath. Add zwangine-rest to classpath.");
        }
    }

    @Override
    protected EndpointRegistry createEndpointRegistry(Map<NormalizedEndpointUri, Endpoint> endpoints) {
        return new DefaultEndpointRegistry(getZwangineContextReference(), endpoints);
    }

    @Override
    protected StreamCachingStrategy createStreamCachingStrategy() {
        return new DefaultStreamCachingStrategy();
    }

    @Override
    protected ExchangeFactory createExchangeFactory() {
        Optional<ExchangeFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ExchangeFactory.FACTORY,
                ExchangeFactory.class);

        return result.orElseGet(PrototypeExchangeFactory::new);
    }

    @Override
    protected ExchangeFactoryManager createExchangeFactoryManager() {
        return new DefaultExchangeFactoryManager();
    }

    @Override
    protected ProcessorExchangeFactory createProcessorExchangeFactory() {
        Optional<ProcessorExchangeFactory> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ProcessorExchangeFactory.FACTORY,
                ProcessorExchangeFactory.class);

        return result.orElseGet(PrototypeProcessorExchangeFactory::new);
    }

    @Override
    protected ReactiveExecutor createReactiveExecutor() {
        Optional<ReactiveExecutor> result = ResolverHelper.resolveService(
                getZwangineContextReference(),
                getZwangineContextExtension().getBootstrapFactoryFinder(),
                ReactiveExecutor.FACTORY,
                ReactiveExecutor.class);

        return result.orElseGet(DefaultReactiveExecutor::new);
    }

    @Override
    protected ValidatorRegistry createValidatorRegistry() {
        return new DefaultValidatorRegistry(getZwangineContextReference());
    }

    @Override
    protected VariableRepositoryFactory createVariableRepositoryFactory() {
        return new DefaultVariableRepositoryFactory(getZwangineContextReference());
    }

    @Override
    protected EndpointServiceRegistry createEndpointServiceRegistry() {
        return new DefaultEndpointServiceRegistry(getZwangineContextReference());
    }

    @Override
    protected StartupConditionStrategy createStartupConditionStrategy() {
        return new DefaultStartupConditionStrategy();
    }

    @Override
    protected TransformerRegistry createTransformerRegistry() {
        return new DefaultTransformerRegistry(getZwangineContextReference());
    }

    @Override
    protected ExecutorServiceManager createExecutorServiceManager() {
        return new BaseExecutorServiceManager(getZwangineContextReference());
    }

    @Override
    public Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, Map<String, Object> parameters)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, String prefixId, Map<String, Object> parameters)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId, WorkflowTemplateContext workflowTemplateContext)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String addWorkflowFromKamelet(
            String workflowId, String workflowTemplateId, String prefixId, String parentWorkflowId, String parentProcessorId,
            Map<String, Object> parameters)
            throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeWorkflowTemplates(String pattern) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTestExcludeWorkflows() {
        throw new UnsupportedOperationException();
    }
}
