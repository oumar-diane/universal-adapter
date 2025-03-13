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
 * Marks a field or property as being a producer to an {@link org.zenithblox.Endpoint} either via its
 * <a href="http://zwangine.zwangine.org/uris.html">URI</a> or via the name of the endpoint reference which is then resolved
 * in a registry such as the Spring Application Context.
 * <p/>
 * Methods invoked on the producer object are then converted to a message {@link org.zenithblox.Exchange} via the
 * <a href="http://zwangine.zwangine.org/bean-integration.html">Bean Integration</a> mechanism.
 *
 * @see InOnly
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.FIELD, ElementType.METHOD })
public @interface Produce {

    /**
     * The uri to produce to
     */
    String value() default "";

    /**
     * Use the field or getter on the bean to provide the uri to produce to
     */
    String property() default "";

    /**
     * Whether to use bean parameter binding
     */
    boolean binding() default true;
}
