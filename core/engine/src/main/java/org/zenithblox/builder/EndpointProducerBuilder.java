/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.builder;

import org.zenithblox.ZwangineContext;
import org.zenithblox.EndpointProducerResolver;
import org.zenithblox.Expression;

import java.util.Map;

/**
 * Type-safe endpoint DSL for building producer endpoints.
 *
 * @see EndpointConsumerBuilder
 */
public interface EndpointProducerBuilder extends EndpointProducerResolver {

    /**
     * Builds the encoded url of this endpoint. This API is only intended for Zwangine internally.
     */
    String getUri();

    /**
     * Builds the raw url of this endpoint. This API is only intended for Zwangine internally.
     */
    String getRawUri();

    /**
     * Adds an option to this endpoint. This API is only intended for Zwangine internally.
     */
    void doSetProperty(String name, Object value);

    /**
     * Adds a multi-value option to this endpoint. This API is only intended for Zwangine internally.
     */
    void doSetMultiValueProperty(String name, String key, Object value);

    /**
     * Adds multi-value options to this endpoint. This API is only intended for Zwangine internally.
     */
    void doSetMultiValueProperties(String name, String prefix, Map<String, Object> values);

    /**
     * Builds a dynamic expression of this endpoint url. This API is only intended for Zwangine internally.
     */
    Expression expr(ZwangineContext zwangineContext);

}
