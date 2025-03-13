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
import org.zenithblox.spi.Injector;
import org.zenithblox.support.ObjectHelper;
import org.zenithblox.support.PluginHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A default implementation of {@link Injector} which just uses reflection to instantiate new objects using their zero
 * argument constructor, and then performing bean post processing using {@link ZwangineBeanPostProcessor}.
 */
public class DefaultInjector implements Injector {

    // use the reflection injector
    protected final ZwangineContext zwangineContext;
    protected final ZwangineBeanPostProcessor postProcessor;

    public DefaultInjector(ZwangineContext context) {
        this.zwangineContext = context;
        this.postProcessor = PluginHelper.getBeanPostProcessor(zwangineContext);
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        return newInstance(type, true);
    }

    @Override
    public <T> T newInstance(Class<T> type, String factoryMethod) {
        return newInstance(type, null, factoryMethod);
    }

    @Override
    public <T> T newInstance(Class<T> type, Class<?> factoryClass, String factoryMethod) {
        Class<?> target = factoryClass != null ? factoryClass : type;
        T answer = null;
        try {
            // lookup factory method
            Method fm = target.getMethod(factoryMethod);
            if (Modifier.isStatic(fm.getModifiers()) && Modifier.isPublic(fm.getModifiers())
                    && fm.getReturnType() != Void.class) {
                Object obj = fm.invoke(null);
                answer = type.cast(obj);
            }
            // inject zwangine context if needed
            ZwangineContextAware.trySetZwangineContext(answer, zwangineContext);
        } catch (Exception e) {
            throw new RuntimeZwangineException("Error invoking factory method: " + factoryMethod + " on class: " + target, e);
        }
        return answer;
    }

    @Override
    public <T> T newInstance(Class<T> type, boolean postProcessBean) {
        T answer = ObjectHelper.newInstance(type);
        // inject zwangine context if needed
        ZwangineContextAware.trySetZwangineContext(answer, zwangineContext);
        if (postProcessBean) {
            applyBeanPostProcessing(answer);
        }
        return answer;
    }

    @Override
    public boolean supportsAutoWiring() {
        return false;
    }

    protected <T> void applyBeanPostProcessing(T bean) {
        try {
            postProcessor.postProcessBeforeInitialization(bean, bean.getClass().getName());
            postProcessor.postProcessAfterInitialization(bean, bean.getClass().getName());
        } catch (Exception e) {
            throw new RuntimeZwangineException("Error during post processing of bean: " + bean, e);
        }
    }
}
