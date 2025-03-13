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
package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.catalog.RuntimeZwangineCatalog;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.spi.*;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Convenient helper to get easy access to various extensions from {@link ExtendedZwangineContext}.
 */
public final class PluginHelper {

    private PluginHelper() {
    }

    /**
     * Returns the bean post processor used to do any bean customization.
     */
    public static ZwangineBeanPostProcessor getBeanPostProcessor(ZwangineContext zwangineContext) {
        return getBeanPostProcessor(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Returns the bean post processor used to do any bean customization.
     */
    public static ZwangineBeanPostProcessor getBeanPostProcessor(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ZwangineBeanPostProcessor.class);
    }

    /**
     * Returns the annotation dependency injection factory.
     */
    public static ZwangineDependencyInjectionAnnotationFactory getDependencyInjectionAnnotationFactory(ZwangineContext zwangineContext) {
        return getDependencyInjectionAnnotationFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Returns the annotation dependency injection factory.
     */
    public static ZwangineDependencyInjectionAnnotationFactory getDependencyInjectionAnnotationFactory(
            ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ZwangineDependencyInjectionAnnotationFactory.class);
    }

    /**
     * Gets the {@link ComponentResolver} to use.
     */
    public static ComponentResolver getComponentResolver(ZwangineContext zwangineContext) {
        return getComponentResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ComponentResolver} to use.
     */
    public static ComponentResolver getComponentResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ComponentResolver.class);
    }

    /**
     * Gets the {@link ComponentNameResolver} to use.
     */
    public static ComponentNameResolver getComponentNameResolver(ZwangineContext zwangineContext) {
        return getComponentNameResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ComponentNameResolver} to use.
     */
    public static ComponentNameResolver getComponentNameResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ComponentNameResolver.class);
    }

    /**
     * Gets the {@link LanguageResolver} to use.
     */
    public static LanguageResolver getLanguageResolver(ZwangineContext zwangineContext) {
        return getLanguageResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link LanguageResolver} to use.
     */
    public static LanguageResolver getLanguageResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(LanguageResolver.class);
    }

    /**
     * Gets the {@link ConfigurerResolver} to use.
     */
    public static ConfigurerResolver getConfigurerResolver(ZwangineContext zwangineContext) {
        return getConfigurerResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ConfigurerResolver} to use.
     */
    public static ConfigurerResolver getConfigurerResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ConfigurerResolver.class);
    }

    /**
     * Gets the {@link UriFactoryResolver} to use.
     */
    public static UriFactoryResolver getUriFactoryResolver(ZwangineContext zwangineContext) {
        return getUriFactoryResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link UriFactoryResolver} to use.
     */
    public static UriFactoryResolver getUriFactoryResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(UriFactoryResolver.class);
    }

    /**
     * Gets the default shared thread pool for error handlers which leverages this for asynchronous redelivery tasks.
     */
    public static ScheduledExecutorService getErrorHandlerExecutorService(ZwangineContext zwangineContext) {
        return getErrorHandlerExecutorService(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the default shared thread pool for error handlers which leverages this for asynchronous redelivery tasks.
     */
    public static ScheduledExecutorService getErrorHandlerExecutorService(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ScheduledExecutorService.class);
    }

    /**
     * Gets the bootstrap {@link ConfigurerResolver} to use. This bootstrap resolver is only intended to be used during
     * bootstrap (starting) ZwangineContext.
     */
    public static ConfigurerResolver getBootstrapConfigurerResolver(ZwangineContext zwangineContext) {
        return getBootstrapConfigurerResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the bootstrap {@link ConfigurerResolver} to use. This bootstrap resolver is only intended to be used during
     * bootstrap (starting) ZwangineContext.
     */
    public static ConfigurerResolver getBootstrapConfigurerResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ConfigurerResolver.class);
    }

    /**
     * Gets the factory finder resolver to use
     */
    public static FactoryFinderResolver getFactoryFinderResolver(ZwangineContext zwangineContext) {
        return getFactoryFinderResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the factory finder resolver to use
     */
    public static FactoryFinderResolver getFactoryFinderResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(FactoryFinderResolver.class);
    }

    /**
     * Returns the package scanning class resolver
     */
    public static PackageScanClassResolver getPackageScanClassResolver(ZwangineContext zwangineContext) {
        return getPackageScanClassResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Returns the package scanning class resolver
     */
    public static PackageScanClassResolver getPackageScanClassResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(PackageScanClassResolver.class);
    }

    /**
     * Returns the package scanning resource resolver
     */
    public static PackageScanResourceResolver getPackageScanResourceResolver(ZwangineContext zwangineContext) {
        return getPackageScanResourceResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Returns the package scanning resource resolver
     */
    public static PackageScanResourceResolver getPackageScanResourceResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(PackageScanResourceResolver.class);
    }

    /**
     * Returns the JAXB Context factory used to create Models.
     */
    public static ModelJAXBContextFactory getModelJAXBContextFactory(ZwangineContext zwangineContext) {
        return getModelJAXBContextFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Returns the JAXB Context factory used to create Models.
     */
    public static ModelJAXBContextFactory getModelJAXBContextFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ModelJAXBContextFactory.class);
    }

    /**
     * Gets the {@link ModelineFactory}.
     */
    public static ModelineFactory getModelineFactory(ZwangineContext zwangineContext) {
        return getModelineFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ModelineFactory}.
     */
    public static ModelineFactory getModelineFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ModelineFactory.class);
    }

    /**
     * Gets the current data format resolver
     */
    public static DataFormatResolver getDataFormatResolver(ZwangineContext zwangineContext) {
        return getDataFormatResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current data format resolver
     */
    public static DataFormatResolver getDataFormatResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(DataFormatResolver.class);
    }

    /**
     * Gets the period task resolver
     */
    public static PeriodTaskResolver getPeriodTaskResolver(ZwangineContext zwangineContext) {
        return getPeriodTaskResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the period task resolver
     */
    public static PeriodTaskResolver getPeriodTaskResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(PeriodTaskResolver.class);
    }

    /**
     * Gets the period task scheduler
     */
    public static PeriodTaskScheduler getPeriodTaskScheduler(ZwangineContext zwangineContext) {
        return getPeriodTaskScheduler(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the period task scheduler
     */
    public static PeriodTaskScheduler getPeriodTaskScheduler(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(PeriodTaskScheduler.class);
    }

    /**
     * Gets the current health check resolver
     */
    public static HealthCheckResolver getHealthCheckResolver(ZwangineContext zwangineContext) {
        return getHealthCheckResolver(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current health check resolver
     */
    public static HealthCheckResolver getHealthCheckResolver(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(HealthCheckResolver.class);
    }


    /**
     * Gets the current {@link org.zenithblox.spi.ProcessorFactory}
     *
     * @return the factory, can be <tt>null</tt> if no custom factory has been set
     */
    public static ProcessorFactory getProcessorFactory(ZwangineContext zwangineContext) {
        return getProcessorFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current {@link org.zenithblox.spi.ProcessorFactory}
     *
     * @return the factory, can be <tt>null</tt> if no custom factory has been set
     */
    public static ProcessorFactory getProcessorFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ProcessorFactory.class);
    }

    /**
     * Gets the current {@link org.zenithblox.spi.InternalProcessorFactory}
     */
    public static InternalProcessorFactory getInternalProcessorFactory(ZwangineContext zwangineContext) {
        return getInternalProcessorFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current {@link org.zenithblox.spi.InternalProcessorFactory}
     */
    public static InternalProcessorFactory getInternalProcessorFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(InternalProcessorFactory.class);
    }

    /**
     * Gets the current {@link org.zenithblox.spi.InterceptEndpointFactory}
     */
    public static InterceptEndpointFactory getInterceptEndpointFactory(ZwangineContext zwangineContext) {
        return getInterceptEndpointFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current {@link org.zenithblox.spi.InterceptEndpointFactory}
     */
    public static InterceptEndpointFactory getInterceptEndpointFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(InterceptEndpointFactory.class);
    }

    /**
     * Gets the current {@link org.zenithblox.spi.WorkflowFactory}
     */
    public static WorkflowFactory getWorkflowFactory(ZwangineContext zwangineContext) {
        return getWorkflowFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the current {@link org.zenithblox.spi.WorkflowFactory}
     */
    public static WorkflowFactory getWorkflowFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(WorkflowFactory.class);
    }

    /**
     * Gets the {@link WorkflowsLoader} to be used.
     */
    public static WorkflowsLoader getWorkflowsLoader(ZwangineContext zwangineContext) {
        return getWorkflowsLoader(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link WorkflowsLoader} to be used.
     */
    public static WorkflowsLoader getWorkflowsLoader(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(WorkflowsLoader.class);
    }

    /**
     * Gets the {@link org.zenithblox.AsyncProcessor} await manager.
     */
    public static AsyncProcessorAwaitManager getAsyncProcessorAwaitManager(ZwangineContext zwangineContext) {
        return getAsyncProcessorAwaitManager(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link org.zenithblox.AsyncProcessor} await manager.
     */
    public static AsyncProcessorAwaitManager getAsyncProcessorAwaitManager(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(AsyncProcessorAwaitManager.class);
    }

    /**
     * Gets the {@link RuntimeZwangineCatalog} if available on the classpath.
     */
    public static RuntimeZwangineCatalog getRuntimeZwangineCatalog(ZwangineContext zwangineContext) {
        return getRuntimeZwangineCatalog(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link RuntimeZwangineCatalog} if available on the classpath.
     */
    public static RuntimeZwangineCatalog getRuntimeZwangineCatalog(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(RuntimeZwangineCatalog.class);
    }

    /**
     * Gets the {@link RestBindingJaxbDataFormatFactory} to be used.
     */
    public static RestBindingJaxbDataFormatFactory getRestBindingJaxbDataFormatFactory(ZwangineContext zwangineContext) {
        return getRestBindingJaxbDataFormatFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link RestBindingJaxbDataFormatFactory} to be used.
     */
    public static RestBindingJaxbDataFormatFactory getRestBindingJaxbDataFormatFactory(
            ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(RestBindingJaxbDataFormatFactory.class);
    }

    /**
     * Gets the {@link BeanProxyFactory} to use.
     */
    public static BeanProxyFactory getBeanProxyFactory(ZwangineContext zwangineContext) {
        return getBeanProxyFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link BeanProxyFactory} to use.
     */
    public static BeanProxyFactory getBeanProxyFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(BeanProxyFactory.class);
    }

    /**
     * Gets the {@link UnitOfWorkFactory} to use.
     */
    public static UnitOfWorkFactory getUnitOfWorkFactory(ZwangineContext zwangineContext) {
        return getUnitOfWorkFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link UnitOfWorkFactory} to use.
     */
    public static UnitOfWorkFactory getUnitOfWorkFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(UnitOfWorkFactory.class);
    }

    /**
     * Gets the {@link BeanIntrospection}
     */
    public static BeanIntrospection getBeanIntrospection(ZwangineContext zwangineContext) {
        return getBeanIntrospection(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link BeanIntrospection}
     */
    public static BeanIntrospection getBeanIntrospection(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(BeanIntrospection.class);
    }

    /**
     * Gets the {@link ResourceLoader} to be used.
     */
    public static ResourceLoader getResourceLoader(ZwangineContext zwangineContext) {
        return getResourceLoader(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ResourceLoader} to be used.
     */
    public static ResourceLoader getResourceLoader(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ResourceLoader.class);
    }

    /**
     * Gets the {@link BeanProcessorFactory} to use.
     */
    public static BeanProcessorFactory getBeanProcessorFactory(ZwangineContext zwangineContext) {
        return getBeanProcessorFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link BeanProcessorFactory} to use.
     */
    public static BeanProcessorFactory getBeanProcessorFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(BeanProcessorFactory.class);
    }

    /**
     * Gets the {@link ModelToXMLDumper} to be used.
     */
    public static ModelToXMLDumper getModelToXMLDumper(ZwangineContext zwangineContext) {
        return getModelToXMLDumper(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ModelToXMLDumper} to be used.
     */
    public static ModelToXMLDumper getModelToXMLDumper(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ModelToXMLDumper.class);
    }

    /**
     * Gets the {@link ModelToXMLDumper} to be used.
     */
    public static ModelToYAMLDumper getModelToYAMLDumper(ZwangineContext zwangineContext) {
        return getModelToYAMLDumper(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link ModelToXMLDumper} to be used.
     */
    public static ModelToYAMLDumper getModelToYAMLDumper(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(ModelToYAMLDumper.class);
    }

    /**
     * Gets the {@link DeferServiceFactory} to use.
     */
    public static DeferServiceFactory getDeferServiceFactory(ZwangineContext zwangineContext) {
        return getDeferServiceFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link DeferServiceFactory} to use.
     */
    public static DeferServiceFactory getDeferServiceFactory(ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(DeferServiceFactory.class);
    }

    /**
     * Gets the {@link AnnotationBasedProcessorFactory} to use.
     */
    public static AnnotationBasedProcessorFactory getAnnotationBasedProcessorFactory(ZwangineContext zwangineContext) {
        return getAnnotationBasedProcessorFactory(zwangineContext.getZwangineContextExtension());
    }

    /**
     * Gets the {@link AnnotationBasedProcessorFactory} to use.
     */
    public static AnnotationBasedProcessorFactory getAnnotationBasedProcessorFactory(
            ExtendedZwangineContext extendedZwangineContext) {
        return extendedZwangineContext.getContextPlugin(AnnotationBasedProcessorFactory.class);
    }
}
