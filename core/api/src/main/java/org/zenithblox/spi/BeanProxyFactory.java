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

import org.zenithblox.Endpoint;

/**
 * A factory for creating a {@link java.lang.reflect.Proxy} for a bean.
 * <p/>
 * This requires to have zwangine-bean on the classpath.
 */
public interface BeanProxyFactory {

    /**
     * Service factory key.
     */
    String FACTORY = "bean-proxy-factory";

    /**
     * Creates a proxy bean facade with the interfaces that when invoked will send the data as a message to a Zwangine
     * endpoint.
     *
     * @param  endpoint         the endpoint to send to when the proxy is invoked
     * @param  binding          whether to use bean parameter binding which would be needed if invoking a bean method
     *                          with multiple parameters
     * @param  interfaceClasses the interface(s) to use as bean facade
     * @throws Exception        is thrown if error creating the proxy
     * @return                  the created bean proxy
     */
    <T> T createProxy(Endpoint endpoint, boolean binding, Class<T>... interfaceClasses) throws Exception;

}
