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
package org.zenithblox;

import org.zenithblox.spi.BeanRepository;
import org.zenithblox.spi.HasZwangineContext;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The context used during creating a {@link Workflow} from a workflow template.
 */
public interface WorkflowTemplateContext extends HasZwangineContext {

    /**
     * Used for template beans to supply the local bean the workflow template should use when creating workflow(s).
     */
    @FunctionalInterface
    interface BeanSupplier<T> {
        T get(WorkflowTemplateContext rtc);
    }

    /**
     * Binds the bean to the repository (if possible).
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatically inject the context if possible.
     *
     * @param id   the id of the bean
     * @param bean the bean
     */
    default void bind(String id, Object bean) {
        bind(id, bean.getClass(), bean);
    }

    /**
     * Binds the bean to the repository (if possible).
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatically inject the context if possible.
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean the bean
     */
    void bind(String id, Class<?> type, Object bean);

    /**
     * Binds the bean (via a supplier) to the repository (if possible).
     * <p/>
     * Zwangine will cache the result from the supplier from first lookup (singleton scope). If you do not need cached then
     * use {@link #bindAsPrototype(String, Class, Supplier)} instead.
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatically inject the context if possible.
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean a supplier for the bean
     */
    void bind(String id, Class<?> type, Supplier<Object> bean);

    /**
     * Binds the bean (via a supplier) to the repository (if possible).
     * <p/>
     * Notice that the supplier will be called each time the bean is being looked up (not cached).
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatically inject the context if possible.
     *
     * @param id   the id of the bean
     * @param type the type of the bean to associate the binding
     * @param bean a supplier for the bean
     */
    void bindAsPrototype(String id, Class<?> type, Supplier<Object> bean);

    /**
     * Registers an optional destroy method to invoke on the bean when stopping Zwangine.
     *
     * @param id     the id of the bean
     * @param method the destroy method name
     */
    void registerDestroyMethod(String id, String method);

    /**
     * Gets the property with the given name
     *
     * @param  name name of property
     * @return      the property value or <tt>null</tt> if no property exists
     */
    Object getProperty(String name);

    /**
     * Gets the environment variable parameter that matches the given property name. The match is performed by
     * normalizing the given property name as an OS environment variable name. The environment variable name may use
     * pure uppercase or zwangineCase converted to underscore property names. As an example bucketNameOrArn property
     * matches BUCKETNAMEORARN and BUCKET_NAME_OR_ARN environment variables.
     *
     * @param  name name of property
     * @return      the property value or <tt>null</tt> if no property exists
     */
    Object getEnvironmentVariable(String name);

    /**
     * Gets the property with the given name
     *
     * @param  name                    name of property
     * @param  type                    the type of the property
     * @return                         the property value or <tt>null</tt> if no property exists
     * @throws TypeConversionException is thrown if error during type conversion
     */
    <T> T getProperty(String name, Class<?> type);

    /**
     * Sets a parameter
     *
     * @param name  the parameter name
     * @param value the parameter value
     */
    void setParameter(String name, Object value);

    /**
     * The parameters to use for the workflow template when creating the new workflow
     */
    Map<String, Object> getParameters();

    /**
     * Whether the workflow template has a parameter with the given name
     *
     * @param  name the parameter name
     * @return      true if exists
     */
    boolean hasParameter(String name);

    /**
     * Whether the workflow template has an environment variable parameter that matches the given parameter name. The match
     * is performed by normalizing the given name as an OS environment variable name. The environment variable name may
     * use pure uppercase or zwangineCase converted to underscore property names. As an example bucketNameOrArn property
     * matches BUCKETNAMEORARN and BUCKET_NAME_OR_ARN environment variables.
     *
     * @param  name the parameter name
     * @return      true if exists
     */
    boolean hasEnvironmentVariable(String name);

    /**
     * Gets the local bean repository for the workflow template when creating the new workflow
     */
    BeanRepository getLocalBeanRepository();

    /**
     * Sets a custom configurer which allows to do configuration while the workflow template is being used to create a
     * workflow. This gives control over the creating process, such as binding local beans and doing other kind of
     * customization.
     *
     * @param configurer the configurer with callback to invoke with the given workflow template context
     */
    void setConfigurer(Consumer<WorkflowTemplateContext> configurer);

    /**
     * Gets the custom configurer.
     */
    Consumer<WorkflowTemplateContext> getConfigurer();

}
