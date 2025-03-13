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
import org.zenithblox.spi.ZwangineBeanPostProcessor;
import org.zenithblox.spi.ZwangineBeanPostProcessorInjector;
import org.zenithblox.support.DefaultEndpoint;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.ReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.zenithblox.support.ObjectHelper.invokeMethod;
import static org.zenithblox.util.ObjectHelper.isEmpty;

/**
 * A bean post processor which implements the <a href="http://zwangine.zwangine.org/bean-integration.html">Bean
 * Integration</a> features in Zwangine. Features such as the <a href="http://zwangine.zwangine.org/bean-injection.html">Bean
 * Injection</a> of objects like {@link org.zenithblox.Endpoint} and {@link org.zenithblox.ProducerTemplate}
 * together with support for <a href="http://zwangine.zwangine.org/pojo-consuming.html">POJO Consuming</a> via the
 * {@link org.zenithblox.Consume} annotation along with <a href="http://zwangine.zwangine.org/pojo-producing.html">POJO
 * Producing</a> via the {@link org.zenithblox.Produce} annotation along with other annotations such as
 * {@link DynamicWorkflow} for creating <a href="http://zwangine.zwangine.org/dynamicworkflowr-annotation.html">a
 * Dynamic workflowr via annotations</a>. {@link org.zenithblox.RecipientList} for creating
 * <a href="http://zwangine.zwangine.org/recipientlist-annotation.html">a Recipient List workflowr via annotations</a>.
 * {@link org.zenithblox.RoutingSlip} for creating <a href="http://zwangine.zwangine.org/routingslip-annotation.html">a
 * Routing Slip workflowr via annotations</a>.
 * <p/>
 * Components such as zwangine-spring can leverage this post-processor to hook in Zwangine bean post-processing into their
 * bean processing framework.
 */
public class DefaultZwangineBeanPostProcessor implements ZwangineBeanPostProcessor, ZwangineContextAware {

    protected static final Logger LOG = LoggerFactory.getLogger(DefaultZwangineBeanPostProcessor.class);
    protected final List<ZwangineBeanPostProcessorInjector> beanPostProcessorInjectors = new ArrayList<>();
    protected ZwanginePostProcessorHelper zwanginePostProcessorHelper;
    protected ZwangineContext zwangineContext;
    protected boolean enabled = true;
    protected boolean unbindEnabled;
    protected Predicate<BindToRegistry> lazyBeanStrategy;

    public DefaultZwangineBeanPostProcessor() {
    }

    public DefaultZwangineBeanPostProcessor(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isUnbindEnabled() {
        return unbindEnabled;
    }

    @Override
    public void setUnbindEnabled(boolean unbindEnabled) {
        this.unbindEnabled = unbindEnabled;
    }

    @Override
    public Predicate<BindToRegistry> getLazyBeanStrategy() {
        return lazyBeanStrategy;
    }

    @Override
    public void setLazyBeanStrategy(Predicate<BindToRegistry> lazyBeanStrategy) {
        this.lazyBeanStrategy = lazyBeanStrategy;
    }

    @Override
    public void addZwangineBeanPostProjectInjector(ZwangineBeanPostProcessorInjector injector) {
        this.beanPostProcessorInjectors.add(injector);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        LOG.trace("Zwangine bean processing before initialization for bean: {}", beanName);

        // some beans cannot be post processed at this given time, so we gotta check beforehand
        if (!canPostProcessBean(bean, beanName)) {
            return bean;
        }

        // always do injection of zwangine context
        if (bean instanceof ZwangineContextAware contextAware && canSetZwangineContext(bean, beanName)) {
            DeferredContextBinding deferredBinding = bean.getClass().getAnnotation(DeferredContextBinding.class);
            ZwangineContext context = getOrLookupZwangineContext();

            if (context == null && deferredBinding == null) {
                LOG.warn("No ZwangineContext defined yet so cannot inject into bean: {}", beanName);
            } else if (context != null) {
                contextAware.setZwangineContext(context);
            }
        }

        if (enabled) {
            // do bean binding on simple types first, and then afterward on complex types
            injectZwangineContextPass(bean, beanName);
            injectFirstPass(bean, beanName, type -> !isComplexUserType(type));
            injectSecondPass(bean, beanName, type -> isComplexUserType(type));
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        LOG.trace("Zwangine bean processing after initialization for bean: {}", beanName);

        // some beans cannot be post processed at this given time, so we gotta check beforehand
        if (!canPostProcessBean(bean, beanName)) {
            return bean;
        }

        if (bean instanceof DefaultEndpoint defaultEndpoint) {
            defaultEndpoint.setEndpointUriIfNotSpecified(beanName);
        }

        // there is no complex processing so we dont have to check for enabled or disabled

        return bean;
    }

    /**
     * Strategy to get the {@link ZwangineContext} to use.
     */
    public ZwangineContext getOrLookupZwangineContext() {
        return zwangineContext;
    }

    /**
     * Strategy to get the {@link ZwanginePostProcessorHelper}
     */
    public ZwanginePostProcessorHelper getPostProcessorHelper() {
        if (zwanginePostProcessorHelper == null) {
            zwanginePostProcessorHelper = new ZwanginePostProcessorHelper(getOrLookupZwangineContext());
        }
        return zwanginePostProcessorHelper;
    }

    protected boolean canPostProcessBean(Object bean, String beanName) {
        if ("properties".equals(beanName)) {
            // we cannot process the properties component
            // its instantiated very eager during creation of zwangine context
            return false;
        }
        return bean != null;
    }

    /**
     * Whether support for the annotation {@link BindToRegistry} is supported. This is only intended for standalone
     * runtimes such as zwangine-main, zwangine-quarkus, etc.
     */
    protected boolean bindToRegistrySupported() {
        return true;
    }

    protected boolean canSetZwangineContext(Object bean, String beanName) {
        if (bean instanceof ZwangineContextAware zwangineContextAware) {
            ZwangineContext context = zwangineContextAware.getZwangineContext();
            if (context != null) {
                LOG.trace("ZwangineContext already set on bean with id [{}]. Will keep existing ZwangineContext on bean.", beanName);
                return false;
            }
        }

        return true;
    }

    protected void injectZwangineContextPass(Object bean, String beanName) {
        // initial pass to inject ZwangineContext
        injectFields(bean, beanName, type -> type.isAssignableFrom(ZwangineContext.class));
    }

    protected void injectFirstPass(Object bean, String beanName, Function<Class<?>, Boolean> filter) {
        // on first pass do field and methods first
        injectFields(bean, beanName, filter);
        injectMethods(bean, beanName, filter);

        if (bindToRegistrySupported()) {
            injectClass(bean, beanName);
            injectNestedClasses(bean, beanName);
            injectBindToRegistryFields(bean, beanName, filter);
            injectBindToRegistryMethods(bean, beanName, filter);
        }

        // zwangine endpoint specific fields last
        injectEndpointFields(bean, beanName, filter);
    }

    protected void injectSecondPass(Object bean, String beanName, Function<Class<?>, Boolean> filter) {
        // on second pass do bind to fields first
        injectFields(bean, beanName, filter);

        if (bindToRegistrySupported()) {
            injectClass(bean, beanName);
            injectNestedClasses(bean, beanName);
            injectBindToRegistryFields(bean, beanName, filter);
            injectBindToRegistryMethods(bean, beanName, filter);
        }
        injectMethods(bean, beanName, filter);

        // zwangine endpoint specific fields last
        injectEndpointFields(bean, beanName, filter);
    }

    protected void injectEndpointFields(final Object bean, final String beanName, Function<Class<?>, Boolean> accept) {
        ReflectionHelper.doWithFields(bean.getClass(), field -> {
            if (accept != null && !accept.apply(field.getType())) {
                return;
            }

            EndpointInject endpointInject = field.getAnnotation(EndpointInject.class);
            if (endpointInject != null) {
                injectField(field, endpointInject.value(), endpointInject.property(), bean, beanName);
            }

            Produce produce = field.getAnnotation(Produce.class);
            if (produce != null) {
                injectField(field, produce.value(), produce.property(), bean, beanName, produce.binding());
            }
        });
    }

    protected void injectFields(final Object bean, final String beanName, Function<Class<?>, Boolean> accept) {
        ReflectionHelper.doWithFields(bean.getClass(), field -> {
            if (accept != null && !accept.apply(field.getType())) {
                return;
            }

            PropertyInject propertyInject = field.getAnnotation(PropertyInject.class);
            if (propertyInject != null) {
                injectFieldProperty(field, propertyInject.value(), propertyInject.defaultValue(), propertyInject.separator(),
                        bean);
            }

            BeanInject beanInject = field.getAnnotation(BeanInject.class);
            if (beanInject != null) {
                injectFieldBean(field, beanInject.value(), bean, beanName);
            }

            BeanConfigInject beanConfigInject = field.getAnnotation(BeanConfigInject.class);
            if (beanConfigInject != null) {
                injectFieldBeanConfig(field, beanConfigInject.value(), bean, beanName);
            }

            // custom bean injector on the field
            for (ZwangineBeanPostProcessorInjector injector : beanPostProcessorInjectors) {
                injector.onFieldInject(field, bean, beanName);
            }
        });
    }

    protected void injectBindToRegistryFields(final Object bean, final String beanName, Function<Class<?>, Boolean> accept) {
        ReflectionHelper.doWithFields(bean.getClass(), field -> {
            if (accept != null && !accept.apply(field.getType())) {
                return;
            }

            BindToRegistry bind = field.getAnnotation(BindToRegistry.class);
            if (bind != null) {
                bindToRegistry(field, bind.value(), bean, beanName, bind.beanPostProcess(), false, bind.initMethod(),
                        bind.destroyMethod());
            }
        });
    }

    public void injectField(
            Field field, String endpointUri, String endpointProperty,
            Object bean, String beanName) {
        injectField(field, endpointUri, endpointProperty, bean, beanName, true);
    }

    public void injectField(
            Field field, String endpointUri, String endpointProperty,
            Object bean, String beanName, boolean binding) {
        ReflectionHelper.setField(field, bean,
                getPostProcessorHelper().getInjectionValue(field.getType(), endpointUri, endpointProperty,
                        field.getName(), bean, beanName, binding));
    }

    public void injectFieldBean(Field field, String name, Object bean, String beanName) {
        ReflectionHelper.setField(field, bean,
                getPostProcessorHelper().getInjectionBeanValue(field.getType(), name));
    }

    public void injectFieldBeanConfig(Field field, String name, Object bean, String beanName) {
        ReflectionHelper.setField(field, bean,
                getPostProcessorHelper().getInjectionBeanConfigValue(field.getType(), name));
    }

    public void injectFieldProperty(
            Field field, String propertyName, String propertyDefaultValue, String propertySeparator,
            Object bean) {
        ReflectionHelper.setField(field, bean,
                getPostProcessorHelper().getInjectionPropertyValue(field.getType(), field.getGenericType(), propertyName,
                        propertyDefaultValue,
                        propertySeparator));
    }

    protected void injectMethods(final Object bean, final String beanName, Function<Class<?>, Boolean> accept) {
        ReflectionHelper.doWithMethods(bean.getClass(), method -> {
            if (accept != null && !accept.apply(method.getReturnType())) {
                return;
            }

            setterInjection(method, bean, beanName);
            getPostProcessorHelper().consumerInjection(method, bean, beanName);

            // custom bean injector on the method
            for (ZwangineBeanPostProcessorInjector injector : beanPostProcessorInjectors) {
                injector.onMethodInject(method, bean, beanName);
            }
        });
    }

    protected void injectBindToRegistryMethods(final Object bean, final String beanName, Function<Class<?>, Boolean> accept) {
        // sort the methods so the simplest are used first

        final List<Method> methods = new ArrayList<>();
        ReflectionHelper.doWithMethods(bean.getClass(), method -> {
            if (accept != null && !accept.apply(method.getReturnType())) {
                return;
            }

            BindToRegistry bind = method.getAnnotation(BindToRegistry.class);
            if (bind != null) {
                methods.add(method);
            }
        });

        // sort methods on shortest number of parameters as we want to process the simplest first
        methods.sort(Comparator.comparingInt(Method::getParameterCount));

        // then do a more complex sorting where we check interdependency among the methods
        methods.sort((m1, m2) -> {
            Class<?>[] types1 = m1.getParameterTypes();
            Class<?>[] types2 = m2.getParameterTypes();

            // favour methods that has no parameters
            if (types1.length == 0 && types2.length == 0) {
                return 0;
            } else if (types1.length == 0) {
                return -1;
            } else if (types2.length == 0) {
                return 1;
            }

            // okay then compare, so we favour methods that does not use parameter types that are returned from other methods
            boolean usedByOthers1 = false;
            for (Class<?> clazz : types1) {
                usedByOthers1 |= methods.stream()
                        .anyMatch(m -> m.getParameterCount() > 0 && clazz.isAssignableFrom(m.getReturnType()));
            }
            boolean usedByOthers2 = false;
            for (Class<?> clazz : types2) {
                usedByOthers2 |= methods.stream()
                        .anyMatch(m -> m.getParameterCount() > 0 && clazz.isAssignableFrom(m.getReturnType()));
            }
            return Boolean.compare(usedByOthers1, usedByOthers2);
        });

        LOG.trace("Discovered {} @BindToRegistry methods", methods.size());

        // bind each method
        methods.forEach(method -> {
            BindToRegistry ann = method.getAnnotation(BindToRegistry.class);
            bindToRegistry(method, ann.value(), bean, beanName, ann.beanPostProcess(), isLazyBean(ann), ann.initMethod(),
                    ann.destroyMethod());
        });
    }

    protected void injectClass(final Object bean, final String beanName) {
        Class<?> clazz = bean.getClass();
        BindToRegistry ann = clazz.getAnnotation(BindToRegistry.class);
        if (ann != null) {
            bindToRegistry(clazz, ann.value(), bean, beanName, ann.beanPostProcess(), isLazyBean(ann), ann.initMethod(),
                    ann.destroyMethod());
        }
    }

    protected void injectNestedClasses(final Object bean, final String beanName) {
        ReflectionHelper.doWithClasses(bean.getClass(), clazz -> {
            BindToRegistry ann = clazz.getAnnotation(BindToRegistry.class);
            if (ann != null) {
                // it is a nested class so we don't have a bean instance for it
                bindToRegistry(clazz, ann.value(), null, null, ann.beanPostProcess(), isLazyBean(ann), ann.initMethod(),
                        ann.destroyMethod());
            }
        });
    }

    protected void setterInjection(Method method, Object bean, String beanName) {
        PropertyInject propertyInject = method.getAnnotation(PropertyInject.class);
        if (propertyInject != null) {
            setterPropertyInjection(method, propertyInject.value(), propertyInject.defaultValue(), propertyInject.separator(),
                    bean);
        }

        BeanInject beanInject = method.getAnnotation(BeanInject.class);
        if (beanInject != null) {
            setterBeanInjection(method, beanInject.value(), bean, beanName);
        }

        BeanConfigInject beanConfigInject = method.getAnnotation(BeanConfigInject.class);
        if (beanConfigInject != null) {
            setterBeanConfigInjection(method, beanConfigInject.value(), bean, beanName);
        }

        EndpointInject endpointInject = method.getAnnotation(EndpointInject.class);
        if (endpointInject != null) {
            setterInjection(method, bean, beanName, endpointInject.value(), endpointInject.property());
        }

        Produce produce = method.getAnnotation(Produce.class);
        if (produce != null) {
            setterInjection(method, bean, beanName, produce.value(), produce.property());
        }
    }

    protected boolean isLazyBean(BindToRegistry ann) {
        if (lazyBeanStrategy == null) {
            return ann.lazy();
        }
        return lazyBeanStrategy.test(ann);
    }

    public void setterInjection(Method method, Object bean, String beanName, String endpointUri, String endpointProperty) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            LOG.warn("Ignoring badly annotated method for injection due to incorrect number of parameters: {}", method);
        } else {
            String propertyName = org.zenithblox.util.ObjectHelper.getPropertyName(method);
            Object value = getPostProcessorHelper().getInjectionValue(parameterTypes[0], endpointUri, endpointProperty,
                    propertyName, bean, beanName);
            invokeMethod(method, bean, value);
        }
    }

    public void setterPropertyInjection(
            Method method, String propertyValue, String propertyDefaultValue, String propertySeparator,
            Object bean) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            LOG.warn("Ignoring badly annotated method for injection due to incorrect number of parameters: {}", method);
        } else {
            String propertyName = org.zenithblox.util.ObjectHelper.getPropertyName(method);
            Class<?> type = parameterTypes[0];
            Type genericType = method.getGenericParameterTypes()[0];
            Object value = getPostProcessorHelper().getInjectionPropertyValue(type, genericType, propertyValue,
                    propertyDefaultValue, propertySeparator);
            invokeMethod(method, bean, value);
        }
    }

    public void setterBeanInjection(Method method, String name, Object bean, String beanName) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            LOG.warn("Ignoring badly annotated method for injection due to incorrect number of parameters: {}", method);
        } else {
            Object value = getPostProcessorHelper().getInjectionBeanValue(parameterTypes[0], name);
            invokeMethod(method, bean, value);
        }
    }

    public void setterBeanConfigInjection(Method method, String name, Object bean, String beanName) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length != 1) {
            LOG.warn("Ignoring badly annotated method for injection due to incorrect number of parameters: {}", method);
        } else {
            Object value = getPostProcessorHelper().getInjectionBeanConfigValue(parameterTypes[0], name);
            invokeMethod(method, bean, value);
        }
    }

    private void bindToRegistry(
            Class<?> clazz, String name, Object bean, String beanName,
            boolean beanPostProcess, boolean lazy,
            String initMethod, String destroyMethod) {
        if (isEmpty(name)) {
            name = clazz.getSimpleName();
        }
        boolean postProcess = beanPostProcess;
        if (bean == null) {
            if (lazy) {
                postProcess = false; // we do post-processing lazy
                bean = (Supplier<Object>) () -> {
                    Object answer = getOrLookupZwangineContext().getInjector().newInstance(clazz);
                    if (answer != null && beanPostProcess) {
                        try {
                            final ZwangineBeanPostProcessor beanPostProcessor = PluginHelper.getBeanPostProcessor(zwangineContext);
                            beanPostProcessor.postProcessBeforeInitialization(answer, beanName);
                            beanPostProcessor.postProcessAfterInitialization(answer, beanName);
                        } catch (Exception e) {
                            throw RuntimeZwangineException.wrapRuntimeException(e);
                        }
                    }
                    return answer;
                };
            } else {
                // no bean so then create an instance from its type
                bean = getOrLookupZwangineContext().getInjector().newInstance(clazz);
            }
        }
        if (unbindEnabled) {
            getOrLookupZwangineContext().getRegistry().unbind(name);
        }
        if (isEmpty(initMethod)) {
            initMethod = ZwanginePostProcessorHelper.initMethodCandidate(bean);
        }
        if (isEmpty(destroyMethod)) {
            destroyMethod = ZwanginePostProcessorHelper.destroyMethodCandidate(bean);
        }
        // use dependency injection factory to perform the task of binding the bean to registry
        Runnable task = PluginHelper.getDependencyInjectionAnnotationFactory(getOrLookupZwangineContext())
                .createBindToRegistryFactory(name, bean, clazz, beanName, postProcess, initMethod, destroyMethod);
        task.run();
    }

    private void bindToRegistry(
            Field field, String name, Object bean, String beanName,
            boolean beanPostProcess, boolean lazy,
            String initMethod, String destroyMethod) {
        if (isEmpty(name)) {
            name = field.getName();
        }
        boolean postProcess = beanPostProcess;
        Object value;
        if (lazy) {
            postProcess = false; // we do post-processing lazy
            value = (Supplier<Object>) () -> {
                Object answer = ReflectionHelper.getField(field, bean);
                if (answer != null && beanPostProcess) {
                    try {
                        final ZwangineBeanPostProcessor beanPostProcessor = PluginHelper.getBeanPostProcessor(zwangineContext);
                        beanPostProcessor.postProcessBeforeInitialization(answer, beanName);
                        beanPostProcessor.postProcessAfterInitialization(answer, beanName);
                    } catch (Exception e) {
                        throw RuntimeZwangineException.wrapRuntimeException(e);
                    }
                }
                return answer;
            };
        } else {
            value = ReflectionHelper.getField(field, bean);
        }
        if (value != null) {
            if (isEmpty(initMethod)) {
                initMethod = ZwanginePostProcessorHelper.initMethodCandidate(bean);
            }
            if (isEmpty(destroyMethod)) {
                destroyMethod = ZwanginePostProcessorHelper.destroyMethodCandidate(bean);
            }
            if (unbindEnabled) {
                getOrLookupZwangineContext().getRegistry().unbind(name);
            }
            // use dependency injection factory to perform the task of binding the bean to registry
            Runnable task = PluginHelper.getDependencyInjectionAnnotationFactory(getOrLookupZwangineContext())
                    .createBindToRegistryFactory(name, value, field.getType(), beanName, postProcess, initMethod,
                            destroyMethod);
            task.run();
        }
    }

    private void bindToRegistry(
            Method method, String name, Object bean, String beanName,
            boolean beanPostProcess, boolean lazy,
            String initMethod, String destroyMethod) {
        if (isEmpty(name)) {
            name = method.getName();
        }
        boolean postProcess = beanPostProcess;
        Object value;
        if (lazy) {
            postProcess = false; // we do post-processing lazy
            value = (Supplier<Object>) () -> {
                Object answer = getPostProcessorHelper()
                        .getInjectionBeanMethodValue(getOrLookupZwangineContext(), method, bean, beanName, "BindToRegistry");
                if (answer != null && beanPostProcess) {
                    try {
                        final ZwangineBeanPostProcessor beanPostProcessor = PluginHelper.getBeanPostProcessor(zwangineContext);
                        beanPostProcessor.postProcessBeforeInitialization(answer, beanName);
                        beanPostProcessor.postProcessAfterInitialization(answer, beanName);
                    } catch (Exception e) {
                        throw RuntimeZwangineException.wrapRuntimeException(e);
                    }
                }
                return answer;
            };
        } else {
            value = getPostProcessorHelper()
                    .getInjectionBeanMethodValue(getOrLookupZwangineContext(), method, bean, beanName, "BindToRegistry");
        }
        if (value != null) {
            if (unbindEnabled) {
                getOrLookupZwangineContext().getRegistry().unbind(name);
            }
            // use dependency injection factory to perform the task of binding the bean to registry
            Runnable task = PluginHelper.getDependencyInjectionAnnotationFactory(getOrLookupZwangineContext())
                    .createBindToRegistryFactory(name, value, method.getReturnType(), beanName, postProcess,
                            initMethod, destroyMethod);
            task.run();
        }
    }

    private static boolean isComplexUserType(Class<?> type) {
        // lets consider all non java, as complex types
        return type != null && !type.isPrimitive() && !type.getName().startsWith("java.");
    }

}
