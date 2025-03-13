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
 * Subscribes a method to an {@link Endpoint} either via its <a href="http://zwangine.zwangine.org/uris.html">URI</a> or via
 * the name of the endpoint reference which is then resolved in a registry such as the Spring Application Context.
 * <p/>
 * When a message {@link Exchange} is received from the {@link Endpoint} then the
 * <a href="http://zwangine.zwangine.org/bean-integration.html">Bean Integration</a> mechanism is used to map the incoming
 * {@link Message} to the method parameters.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.METHOD })
public @interface Consume {

    /**
     * The uri to consume from
     */
    String value() default "";

    /**
     * Use the field or getter on the bean to provide the uri to consume from
     */
    String property() default "";

    /**
     * Optional predicate (using simple language) to only consume if the predicate matches . This can be used to filter
     * messages.
     * <p/>
     * Notice that only the first method that matches the predicate will be used. And if no predicate matches then the
     * message is dropped.
     */
    String predicate() default "";
}
