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

import org.zenithblox.BindToRegistry;

/**
 * Bean post processor.
 */
public interface ZwangineBeanPostProcessor {

    /**
     * Apply this post processor to the given new bean instance <i>before</i> any bean initialization callbacks (like
     * <code>afterPropertiesSet</code> or a custom init-method). The bean will already be populated with property
     * values. The returned bean instance may be a wrapper around the original.
     *
     * @param  bean      the new bean instance
     * @param  beanName  the name of the bean
     * @return           the bean instance to use, either the original or a wrapped one; if <code>null</code>, no
     *                   subsequent BeanPostProcessors will be invoked
     * @throws Exception is thrown if error post processing bean
     */
    default Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * Apply this post processor to the given new bean instance <i>after</i> any bean initialization callbacks (like
     * <code>afterPropertiesSet</code> or a custom init-method). The bean will already be populated with property
     * values. The returned bean instance may be a wrapper around the original.
     *
     * @param  bean      the new bean instance
     * @param  beanName  the name of the bean
     * @return           the bean instance to use, either the original or a wrapped one; if <code>null</code>, no
     *                   subsequent BeanPostProcessors will be invoked
     * @throws Exception is thrown if error post processing bean
     */
    default Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        return bean;
    }

    /**
     * Can be used to turn off bean post processing.
     *
     * Be careful to turn this off, as this means that beans that use Zwangine annotations such as
     * {@link org.zenithblox.EndpointInject}, {@link org.zenithblox.ProducerTemplate},
     * {@link org.zenithblox.Produce}, {@link org.zenithblox.Consume} etc will not be injected and in use.
     *
     * Turning this off should only be done if you are sure you do not use any of these Zwangine features.
     */
    default void setEnabled(boolean enabled) {
        // noop
    }

    default boolean isEnabled() {
        return true;
    }

    default void setUnbindEnabled(boolean unbindEnabled) {
        // noop
    }

    /**
     * Can be used to unbind any existing beans before binding a bean to the registry.
     *
     * Be careful to enable this, as this will unbind all beans with the given id. This is used in special use-cases
     * such as reloading of Zwangine workflows which triggered updating beans that have their implementation re-compiled and
     * re-loaded.
     */
    default boolean isUnbindEnabled() {
        return false;
    }

    /**
     * Adds a custom bean post injector
     *
     * @param injector the custom injector
     */
    default void addZwangineBeanPostProjectInjector(ZwangineBeanPostProcessorInjector injector) {
        // noop
    }

    /**
     * Custom strategy for handling {@link BindToRegistry} beans and whether they are lazy or not.
     */
    void setLazyBeanStrategy(java.util.function.Predicate<BindToRegistry> strategy);

    /**
     * Custom strategy for handling {@link BindToRegistry} beans and whether they are lazy or not.
     */
    java.util.function.Predicate<BindToRegistry> getLazyBeanStrategy();

}
