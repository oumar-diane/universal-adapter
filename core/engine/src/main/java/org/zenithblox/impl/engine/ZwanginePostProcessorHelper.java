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
import org.zenithblox.spi.BeanProxyFactory;
import org.zenithblox.spi.PropertiesComponent;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.spi.Registry;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;

import static org.zenithblox.support.ObjectHelper.invokeMethod;

/**
 * A helper class for Zwangine based injector or bean post-processing hooks.
 */
public class ZwanginePostProcessorHelper implements ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ZwanginePostProcessorHelper.class);

    private ZwangineContext zwangineContext;

    public ZwanginePostProcessorHelper() {
    }

    public ZwanginePostProcessorHelper(ZwangineContext zwangineContext) {
        this.setZwangineContext(zwangineContext);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public void consumerInjection(Method method, Object bean, String beanName) {
        Consume consume = method.getAnnotation(Consume.class);
        if (consume != null) {
            LOG.debug("Creating a consumer for: {}", consume);
            subscribeMethod(method, bean, beanName, consume.value(), consume.property(), consume.predicate());
        }
    }

    public void subscribeMethod(
            Method method, Object bean, String beanName, String endpointUri, String endpointProperty, String predicate) {
        // lets bind this method to a listener
        String injectionPointName = method.getName();
        Endpoint endpoint = getEndpointInjection(bean, endpointUri, endpointProperty, injectionPointName, true);
        if (endpoint != null) {
            boolean multipleConsumer = false;
            if (endpoint instanceof MultipleConsumersSupport consumersSupport) {
                multipleConsumer = consumersSupport.isMultipleConsumersSupported();
            }
            try {
                SubscribeMethodProcessor processor = getConsumerProcessor(endpoint);
                // if multiple consumer then create a new consumer per subscribed method
                if (multipleConsumer || processor == null) {
                    // create new processor and new consumer which happens the first time
                    processor = new SubscribeMethodProcessor(endpoint);
                    // make sure processor is registered in registry so we can reuse it (eg we can look it up)
                    endpoint.getZwangineContext().addService(processor, true);
                    processor.addMethod(bean, method, endpoint, predicate);
                    Consumer consumer = endpoint.createConsumer(processor);
                    startService(consumer, endpoint.getZwangineContext(), bean, beanName);
                } else {
                    // add method to existing processor
                    processor.addMethod(bean, method, endpoint, predicate);
                }
                if (predicate != null) {
                    LOG.debug("Subscribed method: {} to consume from endpoint: {} with predicate: {}", method, endpoint,
                            predicate);
                } else {
                    LOG.debug("Subscribed method: {} to consume from endpoint: {}", method, endpoint);
                }
            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        }
    }

    /**
     * Stats the given service
     */
    protected void startService(Service service, ZwangineContext zwangineContext, Object bean, String beanName) throws Exception {
        // defer starting the service until ZwangineContext has started all its initial services
        if (zwangineContext != null) {
            zwangineContext.deferStartService(service, true);
        } else {
            // mo ZwangineContext then start service manually
            ServiceHelper.startService(service);
        }

        boolean singleton = isSingleton(bean, beanName);
        if (!singleton) {
            LOG.debug("Service is not singleton so you must remember to stop it manually {}", service);
        }
    }

    protected SubscribeMethodProcessor getConsumerProcessor(Endpoint endpoint) {
        Set<SubscribeMethodProcessor> processors = endpoint.getZwangineContext().hasServices(SubscribeMethodProcessor.class);
        return processors.stream().filter(s -> s.getEndpoint() == endpoint).findFirst().orElse(null);
    }

    public Endpoint getEndpointInjection(
            Object bean, String uri, String propertyName,
            String injectionPointName, boolean mandatory) {
        Endpoint answer;
        if (ObjectHelper.isEmpty(uri)) {
            // if no uri then fallback and try the endpoint property
            answer = doGetEndpointInjection(bean, propertyName, injectionPointName);
        } else {
            answer = doGetEndpointInjection(uri, injectionPointName, mandatory);
        }
        // it may be a delegate endpoint via ref component
        if (answer instanceof DelegateEndpoint delegateEndpoint) {
            answer = delegateEndpoint.getEndpoint();
        }
        return answer;
    }

    private Endpoint doGetEndpointInjection(String uri, String injectionPointName, boolean mandatory) {
        return ZwangineContextHelper.getEndpointInjection(getZwangineContext(), uri, injectionPointName, mandatory);
    }

    /**
     * Gets the injection endpoint from a bean property.
     *
     * @param bean         the bean
     * @param propertyName the property name on the bean
     */
    private Endpoint doGetEndpointInjection(Object bean, String propertyName, String injectionPointName) {
        // fall back and use the method name if no explicit property name was given
        if (ObjectHelper.isEmpty(propertyName)) {
            propertyName = injectionPointName;
        }

        // we have a property name, try to lookup a getter method on the bean with that name using this strategy
        // 1. first the getter with the name as given
        // 2. then the getter with Endpoint as postfix
        // 3. then if start with on then try step 1 and 2 again, but omit the on prefix
        try {
            Object value = PluginHelper.getBeanIntrospection(getZwangineContext()).getOrElseProperty(bean,
                    propertyName, null, false);
            if (value == null) {
                // try endpoint as postfix
                value = PluginHelper.getBeanIntrospection(getZwangineContext()).getOrElseProperty(bean,
                        propertyName + "Endpoint", null, false);
            }
            if (value == null && propertyName.startsWith("on")) {
                // retry but without the on as prefix
                propertyName = propertyName.substring(2);
                return doGetEndpointInjection(bean, propertyName, injectionPointName);
            }
            if (value == null) {
                return null;
            } else if (value instanceof Endpoint endpoint) {
                return endpoint;
            } else {
                String uriOrRef = getZwangineContext().getTypeConverter().mandatoryConvertTo(String.class, value);
                return getZwangineContext().getEndpoint(uriOrRef);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error getting property " + propertyName + " from bean " + bean + " due " + e.getMessage(), e);
        }
    }

    /**
     * Creates the object to be injected for an {@link org.zenithblox.EndpointInject} or
     * {@link org.zenithblox.Produce} injection point
     */
    public Object getInjectionValue(
            Class<?> type, String endpointUri, String endpointProperty,
            String injectionPointName, Object bean, String beanName) {
        return getInjectionValue(type, endpointUri, endpointProperty, injectionPointName, bean, beanName, true);
    }

    /**
     * Creates the object to be injected for an {@link org.zenithblox.EndpointInject} or
     * {@link org.zenithblox.Produce} injection point
     */
    @SuppressWarnings("unchecked")
    public Object getInjectionValue(
            Class<?> type, String endpointUri, String endpointProperty,
            String injectionPointName, Object bean, String beanName, boolean binding) {
        if (type.isAssignableFrom(ProducerTemplate.class)) {
            return createInjectionProducerTemplate(endpointUri, endpointProperty, injectionPointName, bean);
        } else if (type.isAssignableFrom(FluentProducerTemplate.class)) {
            return createInjectionFluentProducerTemplate(endpointUri, endpointProperty, injectionPointName, bean);
        } else if (type.isAssignableFrom(ConsumerTemplate.class)) {
            return createInjectionConsumerTemplate(endpointUri, endpointProperty, injectionPointName);
        } else {
            Endpoint endpoint = getEndpointInjection(bean, endpointUri, endpointProperty, injectionPointName, true);
            if (endpoint != null) {
                if (type.isInstance(endpoint)) {
                    return endpoint;
                } else if (type.isAssignableFrom(Producer.class)) {
                    return createInjectionProducer(endpoint, bean, beanName);
                } else if (type.isAssignableFrom(PollingConsumer.class)) {
                    return createInjectionPollingConsumer(endpoint, bean, beanName);
                } else if (type.isInterface()) {
                    // lets create a proxy
                    try {
                        // use proxy service
                        BeanProxyFactory factory = PluginHelper.getBeanProxyFactory(endpoint.getZwangineContext());
                        return factory.createProxy(endpoint, binding, type);
                    } catch (Exception e) {
                        throw createProxyInstantiationRuntimeException(type, endpoint, e);
                    }
                } else {
                    throw new IllegalArgumentException(
                            "Invalid type: " + type.getName()
                                                       + " which cannot be injected via @EndpointInject/@Produce for: "
                                                       + endpoint);
                }
            }
            return null;
        }
    }

    public Object getInjectionPropertyValue(
            Class<?> type, Type genericType, String propertyName, String propertyDefaultValue, String separator) {
        try {
            String key;
            String prefix = PropertiesComponent.PREFIX_TOKEN;
            String suffix = PropertiesComponent.SUFFIX_TOKEN;
            if (!propertyName.contains(prefix)) {
                // must enclose the property name with prefix/suffix to have it resolved
                key = prefix + propertyName + suffix;
            } else {
                // key has already prefix/suffix so use it as-is as it may be a compound key
                key = propertyName;
            }
            String value = getZwangineContext().resolvePropertyPlaceholders(key);
            if (value != null) {
                if (separator != null && !separator.isBlank()) {
                    Object values = convertValueUsingSeparator(zwangineContext, type, genericType, value, separator);
                    return getZwangineContext().getTypeConverter().mandatoryConvertTo(type, values);
                }
                return getZwangineContext().getTypeConverter().mandatoryConvertTo(type, value);
            } else {
                return null;
            }
        } catch (Exception e) {
            if (ObjectHelper.isNotEmpty(propertyDefaultValue)) {
                try {
                    if (separator != null && !separator.isBlank()) {
                        Object values
                                = convertValueUsingSeparator(zwangineContext, type, genericType, propertyDefaultValue, separator);
                        return getZwangineContext().getTypeConverter().mandatoryConvertTo(type, values);
                    }
                    return getZwangineContext().getTypeConverter().mandatoryConvertTo(type, propertyDefaultValue);
                } catch (Exception e2) {
                    throw RuntimeZwangineException.wrapRuntimeZwangineException(e2);
                }
            }
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    private static Object convertValueUsingSeparator(
            ZwangineContext zwangineContext, Class<?> type, Type genericType,
            String value, String separator)
            throws NoTypeConversionAvailableException {
        if (type.isArray()) {
            return convertArrayUsingSeparator(zwangineContext, type, value, separator);
        } else if (Collection.class.isAssignableFrom(type)) {
            return convertCollectionUsingSeparator(zwangineContext, type, genericType, value, separator);
        } else if (Map.class.isAssignableFrom(type)) {
            return convertMapUsingSeparator(zwangineContext, genericType, value, separator);
        } else {
            return null;
        }
    }

    private static Map<String, Object> convertMapUsingSeparator(
            ZwangineContext zwangineContext, Type genericType, String value, String separator)
            throws NoTypeConversionAvailableException {
        Class<?> ct = Object.class;
        if (genericType != null) {
            String name = StringHelper.between(genericType.getTypeName(), "<", ">");
            name = StringHelper.afterLast(name, ",");
            if (name != null) {
                Class<?> clazz = zwangineContext.getClassResolver().resolveClass(name.trim());
                if (clazz != null) {
                    ct = clazz;
                }
            }
        }
        Map<String, Object> values = new LinkedHashMap<>();
        String[] arr = value.split(separator);
        for (String s : arr) {
            String v = s.trim(); // trim values as user may have whitespace noise
            if (v.contains("=")) {
                String k = StringHelper.before(v, "=").trim();
                String e = StringHelper.after(v, "=").trim();
                values.put(k, zwangineContext.getTypeConverter().mandatoryConvertTo(ct, e));
            }
        }
        return values;
    }

    private static Collection<?> convertCollectionUsingSeparator(
            ZwangineContext zwangineContext, Class<?> type, Type genericType, String value, String separator)
            throws NoTypeConversionAvailableException {
        Class<?> ct = Object.class;
        if (genericType != null) {
            String name = StringHelper.between(genericType.getTypeName(), "<", ">");
            if (name != null) {
                Class<?> clazz = zwangineContext.getClassResolver().resolveClass(name.trim());
                if (clazz != null) {
                    ct = clazz;
                }
            }
        }
        boolean set = type.isAssignableFrom(Set.class);
        Collection<Object> values = set ? new LinkedHashSet<>() : new ArrayList<>();
        String[] arr = value.split(separator);
        for (String s : arr) {
            String v = s.trim(); // trim values as user may have whitespace noise
            values.add(zwangineContext.getTypeConverter().mandatoryConvertTo(ct, v));
        }
        return values;
    }

    private static Object[] convertArrayUsingSeparator(ZwangineContext zwangineContext, Class<?> type, String value, String separator)
            throws NoTypeConversionAvailableException {
        String[] arr = value.split(separator);
        Object[] values = new Object[arr.length];
        Class<?> ct = type.getComponentType();
        for (int i = 0; i < arr.length; i++) {
            String v = arr[i].trim(); // trim values as user may have whitespace noise
            values[i] = zwangineContext.getTypeConverter().mandatoryConvertTo(ct, v);
        }
        return values;
    }

    public Object getInjectionBeanValue(Class<?> type, String name) {
        if (ObjectHelper.isEmpty(name)) {
            // is it zwangine context itself?
            if (getZwangineContext() != null && type.isAssignableFrom(getZwangineContext().getClass())) {
                return getZwangineContext();
            }
            Object found = getZwangineContext() != null ? getZwangineContext().getRegistry().findSingleByType(type) : null;
            if (found == null) {
                // this may be a common type so lets check this first
                if (getZwangineContext() != null && type.isAssignableFrom(Registry.class)) {
                    return getZwangineContext().getRegistry();
                }
                if (getZwangineContext() != null && type.isAssignableFrom(TypeConverter.class)) {
                    return getZwangineContext().getTypeConverter();
                }
                // for templates then create a new instance and let zwangine manage its lifecycle
                Service answer = null;
                if (getZwangineContext() != null && type.isAssignableFrom(FluentProducerTemplate.class)) {
                    answer = getZwangineContext().createFluentProducerTemplate();
                }
                if (getZwangineContext() != null && type.isAssignableFrom(ProducerTemplate.class)) {
                    answer = getZwangineContext().createProducerTemplate();
                }
                if (getZwangineContext() != null && type.isAssignableFrom(ConsumerTemplate.class)) {
                    answer = getZwangineContext().createConsumerTemplate();
                }
                if (answer != null) {
                    // lets make zwangine context manage its lifecycle
                    try {
                        getZwangineContext().addService(answer);
                    } catch (Exception e) {
                        throw RuntimeZwangineException.wrapRuntimeException(e);
                    }
                    return answer;
                }
                throw new NoSuchBeanTypeException(type);
            } else {
                return found;
            }
        } else {
            return ZwangineContextHelper.mandatoryLookup(getZwangineContext(), name, type);
        }
    }

    public Object getInjectionBeanConfigValue(Class<?> type, String name) {
        ZwangineContext ecc = getZwangineContext();

        // is it a map or properties
        boolean mapType = false;
        Map map = null;
        if (type.isAssignableFrom(Map.class)) {
            map = new LinkedHashMap();
            mapType = true;
        } else if (type.isAssignableFrom(Properties.class)) {
            map = new Properties();
            mapType = true;
        }

        // create an instance of type
        Object bean = null;
        if (map == null) {
            bean = ecc.getRegistry().findSingleByType(type);
            if (bean == null) {
                // attempt to create a new instance
                try {
                    bean = ecc.getInjector().newInstance(type);
                } catch (Exception e) {
                    // ignore
                    return null;
                }
            }
        }

        // root key
        String rootKey = name;
        // clip trailing dot
        if (rootKey.endsWith(".")) {
            rootKey = rootKey.substring(0, rootKey.length() - 1);
        }
        String uRootKey = rootKey.toUpperCase(Locale.US);

        // get all properties and transfer to map
        Properties props = ecc.getPropertiesComponent().loadProperties();
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        for (String key : props.stringPropertyNames()) {
            String uKey = key.toUpperCase(Locale.US);
            // need to ignore case
            if (uKey.startsWith(uRootKey)) {
                // strip prefix
                String sKey = key.substring(rootKey.length());
                if (sKey.startsWith(".")) {
                    sKey = sKey.substring(1);
                }
                map.put(sKey, props.getProperty(key));
            }
        }
        if (mapType) {
            return map;
        }

        // lookup configurer if there is any
        // use FQN class name first, then simple name, and root key last
        PropertyConfigurer configurer = null;
        String[] names = new String[] {
                type.getName() + "-configurer", type.getSimpleName() + "-configurer", rootKey + "-configurer" };
        for (String n : names) {
            configurer = PluginHelper.getConfigurerResolver(ecc).resolvePropertyConfigurer(n, ecc);
            if (configurer != null) {
                break;
            }
        }

        new PropertyBindingSupport.Builder()
                .withZwangineContext(ecc)
                .withIgnoreCase(true)
                .withTarget(bean)
                .withConfigurer(configurer)
                .withProperties(map)
                .bind();

        return bean;
    }

    public Object getInjectionBeanMethodValue(
            ZwangineContext context,
            Method method, Object bean, String beanName, String annotationName) {
        Class<?> returnType = method.getReturnType();
        if (returnType == Void.TYPE) {
            throw new IllegalArgumentException(
                    "@" + annotationName + " on class: " + method.getDeclaringClass()
                                               + " method: " + method.getName() + " with void return type is not allowed");
        }

        Object value;
        Object[] parameters = bindToRegistryParameterMapping(context, method);
        if (parameters != null) {
            value = invokeMethod(method, bean, parameters);
        } else {
            value = invokeMethod(method, bean);
        }
        return value;
    }

    private Object[] bindToRegistryParameterMapping(ZwangineContext context, Method method) {
        if (method.getParameterCount() == 0) {
            return null;
        }

        // map each parameter if possible
        Object[] parameters = new Object[method.getParameterCount()];
        for (int i = 0; i < method.getParameterCount(); i++) {
            Class<?> type = method.getParameterTypes()[i];
            Type genericType = method.getGenericParameterTypes()[i];
            if (type.isAssignableFrom(ZwangineContext.class)) {
                parameters[i] = context;
            } else if (type.isAssignableFrom(Registry.class)) {
                parameters[i] = context.getRegistry();
            } else if (type.isAssignableFrom(TypeConverter.class)) {
                parameters[i] = context.getTypeConverter();
            } else {
                // we also support @BeanInject and @PropertyInject annotations
                Annotation[] anns = method.getParameterAnnotations()[i];
                if (anns.length == 1) {
                    // we dont assume there are multiple annotations on the same parameter so grab first
                    Annotation ann = anns[0];
                    if (ann.annotationType() == PropertyInject.class) {
                        PropertyInject pi = (PropertyInject) ann;
                        Object result
                                = getInjectionPropertyValue(type, genericType, pi.value(), pi.defaultValue(), pi.separator());
                        parameters[i] = result;
                    } else if (ann.annotationType() == BeanConfigInject.class) {
                        BeanConfigInject pi = (BeanConfigInject) ann;
                        Object result = getInjectionBeanConfigValue(type, pi.value());
                        parameters[i] = result;
                    } else if (ann.annotationType() == BeanInject.class) {
                        BeanInject bi = (BeanInject) ann;
                        Object result = getInjectionBeanValue(type, bi.value());
                        parameters[i] = result;
                    }
                } else {
                    // okay attempt to default to singleton instances from the registry
                    Set<?> instances = context.getRegistry().findByType(type);
                    if (instances.size() == 1) {
                        parameters[i] = instances.iterator().next();
                    } else if (instances.size() > 1) {
                        // there are multiple instances of the same type, so barf
                        throw new IllegalArgumentException(
                                "Multiple beans of the same type: " + type
                                                           + " exists in the Zwangine registry. Specify the bean name on @BeanInject to bind to a single bean, at the method: "
                                                           + method);
                    }
                }
            }

            // each parameter must be mapped
            if (parameters[i] == null) {
                int pos = i + 1;
                throw new IllegalArgumentException("@BindToProperty cannot bind parameter #" + pos + " on method: " + method);
            }
        }

        return parameters;
    }

    /**
     * Factory method to create a {@link org.zenithblox.ProducerTemplate} to be injected into a POJO
     */
    protected ProducerTemplate createInjectionProducerTemplate(
            String endpointUri, String endpointProperty,
            String injectionPointName, Object bean) {
        // endpoint is optional for this injection point
        Endpoint endpoint = getEndpointInjection(bean, endpointUri, endpointProperty, injectionPointName, false);
        ZwangineContext context = endpoint != null ? endpoint.getZwangineContext() : getZwangineContext();
        ProducerTemplate answer = new DefaultProducerTemplate(context, endpoint);
        // start the template so its ready to use
        try {
            // no need to defer the template as it can adjust to the endpoint at runtime
            startService(answer, context, bean, null);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    /**
     * Factory method to create a {@link org.zenithblox.FluentProducerTemplate} to be injected into a POJO
     */
    protected FluentProducerTemplate createInjectionFluentProducerTemplate(
            String endpointUri, String endpointProperty,
            String injectionPointName, Object bean) {
        // endpoint is optional for this injection point
        Endpoint endpoint = getEndpointInjection(bean, endpointUri, endpointProperty, injectionPointName, false);
        ZwangineContext context = endpoint != null ? endpoint.getZwangineContext() : getZwangineContext();
        FluentProducerTemplate answer = new DefaultFluentProducerTemplate(context);
        answer.setDefaultEndpoint(endpoint);
        // start the template so its ready to use
        try {
            // no need to defer the template as it can adjust to the endpoint at runtime
            startService(answer, context, bean, null);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    /**
     * Factory method to create a {@link org.zenithblox.ConsumerTemplate} to be injected into a POJO
     */
    protected ConsumerTemplate createInjectionConsumerTemplate(
            String endpointUri, String endpointProperty,
            String injectionPointName) {
        ConsumerTemplate answer = new DefaultConsumerTemplate(getZwangineContext());
        // start the template so its ready to use
        try {
            startService(answer, null, null, null);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return answer;
    }

    /**
     * Factory method to create a started {@link org.zenithblox.PollingConsumer} to be injected into a POJO
     */
    protected PollingConsumer createInjectionPollingConsumer(Endpoint endpoint, Object bean, String beanName) {
        try {
            PollingConsumer consumer = endpoint.createPollingConsumer();
            startService(consumer, endpoint.getZwangineContext(), bean, beanName);
            return consumer;
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    /**
     * A Factory method to create a started {@link org.zenithblox.Producer} to be injected into a POJO
     */
    protected Producer createInjectionProducer(Endpoint endpoint, Object bean, String beanName) {
        try {
            return PluginHelper.getDeferServiceFactory(endpoint.getZwangineContext()).createProducer(endpoint);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    protected RuntimeException createProxyInstantiationRuntimeException(Class<?> type, Endpoint endpoint, Exception e) {
        return new ProxyInstantiationException(type, endpoint, e);
    }

    /**
     * Implementations can override this method to determine if the bean is singleton.
     *
     * @param  bean the bean
     * @return      <tt>true</tt> if its singleton scoped, for prototype scoped <tt>false</tt> is returned.
     */
    protected boolean isSingleton(Object bean, String beanName) {
        if (bean instanceof IsSingleton singleton) {
            return singleton.isSingleton();
        }
        return true;
    }

    /**
     * Find the best init method to use for the given bean
     */
    public static String initMethodCandidate(Object bean) {
        if (bean instanceof Service) {
            return "start";
        }
        return null;
    }

    /**
     * Find the best destroy method to use for the given bean
     */
    public static String destroyMethodCandidate(Object bean) {
        if (bean instanceof Service) {
            return "stop";
        } else if (bean instanceof Closeable) {
            return "close";
        }
        return null;
    }
}
