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
package org.zenithblox.model;

import org.zenithblox.ZwangineContext;
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.spi.BeanRepository;
import org.zenithblox.support.LocalBeanRegistry;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.StringHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Default {@link WorkflowTemplateContext}.
 */
public final class DefaultWorkflowTemplateContext implements WorkflowTemplateContext {

    private final ZwangineContext zwangineContext;
    private final LocalBeanRegistry registry;
    private final Map<String, Object> parameters;
    private Consumer<WorkflowTemplateContext> configurer;

    public DefaultWorkflowTemplateContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        // we just need the simple registry that also supports supplier style
        this.registry = new LocalBeanRegistry();
        this.parameters = new HashMap<>();
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return this.zwangineContext;
    }

    @Override
    public void bind(String id, Object bean) {
        if (bean instanceof BeanSupplier) {
            // need to unwrap bean supplier as regular supplier
            BeanSupplier<Object> bs = (BeanSupplier<Object>) bean;
            registry.bind(id, (Supplier<Object>) () -> bs.get(DefaultWorkflowTemplateContext.this));
        } else {
            registry.bind(id, bean);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void bind(String id, Class<?> type, Object bean) {
        if (bean instanceof BeanSupplier) {
            // need to unwrap bean supplier as regular supplier
            BeanSupplier<Object> bs = (BeanSupplier<Object>) bean;
            registry.bind(id, type, () -> bs.get(this));
        } else {
            registry.bind(id, type, bean);
        }
    }

    @Override
    public void bind(String id, Class<?> type, Supplier<Object> bean) {
        registry.bind(id, type, bean);
    }

    @Override
    public void bindAsPrototype(String id, Class<?> type, Supplier<Object> bean) {
        registry.bindAsPrototype(id, type, bean);
    }

    @Override
    public void registerDestroyMethod(String id, String method) {
        registry.registerDestroyMethod(id, method);
    }

    @Override
    public Object getProperty(String name) {
        return parameters.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(String name, Class<?> type) {
        Object value = parameters.get(name);
        return (T) zwangineContext.getTypeConverter().tryConvertTo(type, value);
    }

    @Override
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    @Override
    public boolean hasParameter(String name) {
        if (parameters.containsKey(name)) {
            return true;
        }
        // lookup key with both dash and zwangine style
        name = StringHelper.dashToZwangineCase(name);
        if (parameters.containsKey(name)) {
            return true;
        }
        name = StringHelper.zwangineCaseToDash(name);
        if (parameters.containsKey(name)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasEnvironmentVariable(String name) {
        String normalizedKey = IOHelper.normalizeEnvironmentVariable(name);
        if (parameters.containsKey(normalizedKey)) {
            return true;
        }
        normalizedKey = IOHelper.normalizeEnvironmentVariable(StringHelper.zwangineCaseToDash(name));
        if (parameters.containsKey(normalizedKey)) {
            return true;
        }
        return false;
    }

    @Override
    public Object getEnvironmentVariable(String name) {
        String normalizedKey = IOHelper.normalizeEnvironmentVariable(name);
        if (parameters.containsKey(normalizedKey)) {
            return parameters.get(normalizedKey);
        }
        normalizedKey = IOHelper.normalizeEnvironmentVariable(StringHelper.zwangineCaseToDash(name));
        if (parameters.containsKey(normalizedKey)) {
            return parameters.get(normalizedKey);
        }
        return null;
    }

    @Override
    public BeanRepository getLocalBeanRepository() {
        return registry;
    }

    @Override
    public Consumer<WorkflowTemplateContext> getConfigurer() {
        return configurer;
    }

    @Override
    public void setConfigurer(Consumer<WorkflowTemplateContext> configurer) {
        this.configurer = configurer;
    }
}
