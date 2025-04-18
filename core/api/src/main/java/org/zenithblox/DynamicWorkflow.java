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

import java.lang.annotation.*;

/**
 * Indicates that this method is to be used as a <a href="http://zwangine.zwangine.org/dynamic-workflowr.html">Dynamic
 * Workflowr</a> routing the incoming message through a series of processing steps.
 *
 * When a message {@link Exchange} is received from an {@link Endpoint} then the
 * <a href="http://zwangine.zwangine.org/bean-integration.html">Bean Integration</a> mechanism is used to map the incoming
 * {@link Message} to the method parameters.
 *
 * The return value of the method is then converted to either a {@link java.util.Collection} or array of objects where
 * each element is converted to an {@link org.zenithblox.Endpoint} or a {@link String}, or if it is not a
 * collection/array then it is converted to an {@link org.zenithblox.Endpoint} or {@link String}.
 *
 * Then for each endpoint or URI the message is workflowd in a pipes and filter fashion.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface DynamicWorkflow {

    /**
     * Sets the uri delimiter to use
     */
    String delimiter() default ",";

    /**
     * Whether to ignore the invalidate endpoint exception when try to create a producer with that endpoint
     */
    boolean ignoreInvalidEndpoints() default false;

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers when using this dynamic workflowr, when uris are reused.
     *
     * Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic
     * endpoint is unique then its best to turn off caching by setting this to -1, which allows Zwangine to not cache both
     * the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use.
     * This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches.
     *
     * However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use
     * the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on
     * the default size (1000).
     *
     * If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help
     * reduce memory usage to avoid storing too many non frequent used producers.
     */
    int cacheSize() default 0;
}
