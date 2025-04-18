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
package org.zenithblox.spi;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.RuntimeZwangineException;

import java.util.function.Supplier;

/**
 * Represents a {@link BeanRepository} which may also be capable of binding beans to its repository.
 */
public interface Registry extends BeanRepository {

    /**
     * Binds the bean to the repository (if possible).
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatic inject the context if possible.
     *
     * @param  id                    the id of the bean
     * @param  bean                  the bean
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    default void bind(String id, Object bean) throws RuntimeZwangineException {
        if (bean != null) {
            bind(id, bean.getClass(), bean);
        }
    }

    /**
     * Binds the bean to the repository (if possible).
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatic inject the context if possible.
     *
     * @param  id                    the id of the bean
     * @param  bean                  the bean
     * @param  initMethod            optional init method (invoked at bind)
     * @param  destroyMethod         optional destroy method (invoked at unbind or stopping Zwangine)
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    default void bind(String id, Object bean, String initMethod, String destroyMethod) throws RuntimeZwangineException {
        if (bean != null) {
            bind(id, bean.getClass(), bean, initMethod, destroyMethod);
        }
    }

    /**
     * Binds the bean to the repository (if possible).
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatic inject the context if possible.
     *
     * @param  id                    the id of the bean
     * @param  type                  the type of the bean to associate the binding
     * @param  bean                  the bean
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    void bind(String id, Class<?> type, Object bean) throws RuntimeZwangineException;

    /**
     * Binds the bean to the repository (if possible).
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * If the bean is {@link ZwangineContextAware} then the registry will automatic inject the context if possible.
     *
     * @param  id                    the id of the bean
     * @param  type                  the type of the bean to associate the binding
     * @param  bean                  the bean
     * @param  initMethod            optional init method (invoked at bind)
     * @param  destroyMethod         optional destroy method (invoked at unbind or stopping Zwangine)
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    void bind(String id, Class<?> type, Object bean, String initMethod, String destroyMethod) throws RuntimeZwangineException;

    /**
     * Binds the bean (via a supplier) to the repository (if possible).
     * <p/>
     * Zwangine will cache the result from the supplier from first lookup (singleton scope). If you do not need cached then
     * use {@link #bindAsPrototype(String, Class, Supplier)} instead.
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * @param  id                    the id of the bean
     * @param  type                  the type of the bean to associate the binding
     * @param  bean                  a supplier for the bean
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    void bind(String id, Class<?> type, Supplier<Object> bean) throws RuntimeZwangineException;

    /**
     * Binds the bean (via a supplier) to the repository (if possible).
     * <p/>
     * Notice that the supplier will be called each time the bean is being looked up (not cached).
     * <p/>
     * Binding by id and type allows to bind multiple entries with the same id but with different type.
     *
     * @param  id                    the id of the bean
     * @param  type                  the type of the bean to associate the binding
     * @param  bean                  a supplier for the bean
     * @throws RuntimeZwangineException is thrown if binding is not possible
     */
    void bindAsPrototype(String id, Class<?> type, Supplier<Object> bean) throws RuntimeZwangineException;

    /**
     * Removes the bean from the repository (if possible).
     *
     * @param id the id of the bean
     */
    default void unbind(String id) {
        // noop
    }

    /**
     * Strategy to wrap the value to be stored in the registry.
     *
     * @param  value the value
     * @return       the value to store
     */
    default Object wrap(Object value) {
        return value;
    }

}
