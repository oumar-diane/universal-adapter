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
package org.zenithblox.support.scan;

import org.zenithblox.BindToRegistry;
import org.zenithblox.ZwangineContext;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.Service;
import org.zenithblox.spi.ZwangineBeanPostProcessor;
import org.zenithblox.spi.Injector;
import org.zenithblox.spi.PackageScanClassResolver;
import org.zenithblox.spi.Registry;
import org.zenithblox.support.PluginHelper;

import java.io.Closeable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;

import static org.zenithblox.util.ObjectHelper.isEmpty;
import static org.zenithblox.util.ObjectHelper.isNotEmpty;

/**
 * Helper for Zwangine package scanning.
 */
public class PackageScanHelper {

    private PackageScanHelper() {
    }

    /**
     * Scans the given Java packages for custom beans annotated with {@link BindToRegistry} and create new instances of
     * these class and performs Zwangine dependency injection via {@link org.zenithblox.spi.ZwangineBeanPostProcessor}.
     *
     * @param zwangineContext the zwangine context
     * @param packages     the Java packages to scan
     */
    public static void registerBeans(ZwangineContext zwangineContext, Set<String> packages) {
        if (packages != null && !packages.isEmpty()) {
            Registry registry = zwangineContext.getRegistry();
            if (registry != null) {
                PackageScanClassResolver scanner
                        = zwangineContext.getZwangineContextExtension().getContextPlugin(PackageScanClassResolver.class);
                Injector injector = zwangineContext.getInjector();
                if (scanner != null && injector != null) {
                    Map<Class<?>, Object> created = new HashMap<>();
                    Map<Object, String> initMethods = new HashMap<>();
                    Set<Class<?>> lazy = new HashSet<>();
                    for (String pkg : packages) {
                        Set<Class<?>> classes = scanner.findAnnotated(BindToRegistry.class, pkg);
                        for (Class<?> c : classes) {
                            BindToRegistry ann = c.getAnnotation(BindToRegistry.class);
                            if (ann != null && ann.lazy()) {
                                // phase-1: remember lazy creating beans
                                lazy.add(c);
                            } else {
                                // phase-1: create empty bean instance without any bean post-processing
                                Object b = injector.newInstance(c, false);
                                if (b != null) {
                                    created.put(c, b);
                                }
                            }
                        }
                        for (Class<?> c : lazy) {
                            // phase-2: special for lazy beans that must be registered and created on-demand
                            BindToRegistry ann = c.getAnnotation(BindToRegistry.class);
                            if (ann != null) {
                                String name = ann.value();
                                if (isEmpty(name)) {
                                    name = c.getSimpleName();
                                }
                                String beanName = c.getName();
                                Object bean = (Supplier<Object>) () -> {
                                    Object answer = injector.newInstance(c);
                                    if (answer != null && ann.beanPostProcess()) {
                                        try {
                                            final ZwangineBeanPostProcessor beanPostProcessor
                                                    = PluginHelper.getBeanPostProcessor(zwangineContext);
                                            beanPostProcessor.postProcessBeforeInitialization(answer, beanName);
                                            beanPostProcessor.postProcessAfterInitialization(answer, beanName);
                                        } catch (Exception e) {
                                            throw RuntimeZwangineException.wrapRuntimeException(e);
                                        }
                                    }
                                    return answer;
                                };
                                // - bind to registry if @org.zenithblox.BindToRegistry is present
                                // use dependency injection factory to perform the task of binding the bean to registry
                                Runnable task = PluginHelper.getDependencyInjectionAnnotationFactory(zwangineContext)
                                        .createBindToRegistryFactory(name, bean, c, beanName, false, null, null);
                                task.run();
                            }
                        }
                        for (Entry<Class<?>, Object> entry : created.entrySet()) {
                            Class<?> c = entry.getKey();
                            // phase-3: discover any created beans has @BindToRegistry to register them eager
                            BindToRegistry ann = c.getAnnotation(BindToRegistry.class);
                            if (ann != null) {
                                String name = ann.value();
                                if (isEmpty(name)) {
                                    name = c.getSimpleName();
                                }
                                Object bean = entry.getValue();
                                String beanName = c.getName();
                                String initMethod = ann.initMethod();
                                if (isEmpty(initMethod) && bean instanceof Service) {
                                    initMethod = "start";
                                }
                                String destroyMethod = ann.destroyMethod();
                                if (isEmpty(destroyMethod) && bean instanceof Service) {
                                    destroyMethod = "stop";
                                } else if (isEmpty(destroyMethod) && bean instanceof Closeable) {
                                    destroyMethod = "close";
                                }
                                // - bind to registry if @org.zenithblox.BindToRegistry is present
                                // use dependency injection factory to perform the task of binding the bean to registry
                                // use null for init method as we need to defer calling it at a late phase
                                Runnable task = PluginHelper.getDependencyInjectionAnnotationFactory(zwangineContext)
                                        .createBindToRegistryFactory(name, bean, c, beanName, false, null, destroyMethod);
                                // defer calling init methods until dependency injection in phase-4 is complete
                                if (isNotEmpty(initMethod)) {
                                    initMethods.put(bean, initMethod);
                                }
                                task.run();
                            }
                        }
                        for (Entry<Class<?>, Object> entry : created.entrySet()) {
                            Class<?> c = entry.getKey();
                            // phase-4: now we can do bean post-processing on the created beans
                            Object bean = entry.getValue();
                            String beanName = c.getName();
                            try {
                                // - call org.zenithblox.spi.ZwangineBeanPostProcessor.postProcessBeforeInitialization
                                // - call org.zenithblox.spi.ZwangineBeanPostProcessor.postProcessAfterInitialization
                                PluginHelper.getBeanPostProcessor(zwangineContext).postProcessBeforeInitialization(bean,
                                        beanName);
                                PluginHelper.getBeanPostProcessor(zwangineContext).postProcessAfterInitialization(bean,
                                        beanName);
                            } catch (Exception e) {
                                throw new RuntimeZwangineException("Error post-processing bean: " + beanName, e);
                            }
                        }
                        for (Entry<Object, String> entry : initMethods.entrySet()) {
                            Object bean = entry.getKey();
                            String method = entry.getValue();
                            // phase-5: now call init method on created beans
                            try {
                                org.zenithblox.support.ObjectHelper.invokeMethodSafe(method, bean);
                            } catch (Exception e) {
                                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                            }
                        }
                    }
                }
            }
        }
    }
}
