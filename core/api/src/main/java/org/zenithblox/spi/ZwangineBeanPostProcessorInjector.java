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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Used for custom injection when doing {@link ZwangineBeanPostProcessor} bean post-processing. Can be used to support
 * 3rd-party annotations for dependency injections.
 */
public interface ZwangineBeanPostProcessorInjector {

    /**
     * Field injection
     *
     * @param field    the field
     * @param bean     the bean instance where the field is present
     * @param beanName optional bean id of the bean
     */
    void onFieldInject(Field field, Object bean, String beanName);

    /**
     * Method injection
     *
     * @param method   the method
     * @param bean     the bean instance where the method is present
     * @param beanName optional bean id of the bean
     */
    void onMethodInject(Method method, Object bean, String beanName);

}
