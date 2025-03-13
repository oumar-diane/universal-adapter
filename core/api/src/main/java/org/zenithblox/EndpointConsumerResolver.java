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

/**
 * An interface to represent an object that can be resolved as a consumer {@link Endpoint}
 */
public interface EndpointConsumerResolver {

    /**
     * Resolves this object as an endpoint.
     *
     * @param  context                 the zwangine context
     * @return                         a built {@link Endpoint}
     * @throws NoSuchEndpointException is thrown if the endpoint
     */
    Endpoint resolve(ZwangineContext context) throws NoSuchEndpointException;

    /**
     * Resolves this object as an endpoint.
     *
     * @param  context                 the zwangine context
     * @param  endpointType            the expected type
     * @return                         a built {@link Endpoint}
     * @throws NoSuchEndpointException is thrown if the endpoint
     */
    <T extends Endpoint> T resolve(ZwangineContext context, Class<T> endpointType) throws NoSuchEndpointException;

}
