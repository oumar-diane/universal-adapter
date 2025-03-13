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

import org.zenithblox.BeanScope;
import org.zenithblox.ZwangineContext;
import org.zenithblox.Processor;

import java.lang.reflect.Method;

/**
 * Factory for creating a {@link Processor} that can invoke a method on a bean and supporting using Zwangine bean parameter
 * bindings.
 * <p/>
 * This requires to have zwangine-bean on the classpath.
 */
public interface BeanProcessorFactory {

    /**
     * Service factory key.
     */
    String FACTORY = "bean-processor-factory";

    /**
     * Creates the bean processor from the existing bean instance
     *
     * @param  zwangineContext the zwangine context
     * @param  bean         the bean
     * @param  method       the method to invoke
     * @return              the created processor
     * @throws Exception    is thrown if error creating the processor
     */
    Processor createBeanProcessor(ZwangineContext zwangineContext, Object bean, Method method) throws Exception;

    /**
     * Creates the bean processor from a given set of parameters that can refer to the bean via an existing bean, a
     * reference to a bean, or its class name etc.
     *
     * @param  zwangineContext the zwangine context
     * @param  bean         the bean instance
     * @param  beanType     or the bean class name
     * @param  beanClass    or the bean class
     * @param  ref          or bean reference to lookup the bean from the registry
     * @param  method       optional name of method to invoke
     * @param  scope        the scope of the bean
     * @return              the created processor
     * @throws Exception    is thrown if error creating the processor
     */
    Processor createBeanProcessor(
            ZwangineContext zwangineContext, Object bean, String beanType, Class<?> beanClass, String ref,
            String method, BeanScope scope)
            throws Exception;

}
