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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.ZwangineBeanPostProcessor;
import org.zenithblox.spi.ZwangineDependencyInjectionAnnotationFactory;
import org.zenithblox.support.PluginHelper;

import java.util.function.Supplier;

/**
 * Default implementation of the {@link ZwangineDependencyInjectionAnnotationFactory}.
 */
public class DefaultDependencyInjectionAnnotationFactory
        implements ZwangineDependencyInjectionAnnotationFactory, ZwangineContextAware {

    private ZwangineContext zwangineContext;

    public DefaultDependencyInjectionAnnotationFactory(ZwangineContext zwangineContext) {
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
    @SuppressWarnings("unchecked")
    public Runnable createBindToRegistryFactory(
            String id, Object bean, Class<?> beanType, String beanName, boolean beanPostProcess,
            String initMethod, String destroyMethod) {
        return () -> {
            if (beanPostProcess) {
                try {
                    final ZwangineBeanPostProcessor beanPostProcessor = PluginHelper.getBeanPostProcessor(zwangineContext);
                    beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
                    beanPostProcessor.postProcessAfterInitialization(bean, beanName);
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeException(e);
                }
            }
            ZwangineContextAware.trySetZwangineContext(bean, zwangineContext);
            if (bean instanceof Supplier) {
                // must be Supplier<Object> to ensure correct binding
                Supplier<Object> sup = (Supplier<Object>) bean;
                zwangineContext.getRegistry().bind(id, beanType, sup);
            } else {
                if (initMethod != null || destroyMethod != null) {
                    zwangineContext.getRegistry().bind(id, bean, initMethod, destroyMethod);
                } else {
                    zwangineContext.getRegistry().bind(id, bean);
                }
            }
        };
    }
}
