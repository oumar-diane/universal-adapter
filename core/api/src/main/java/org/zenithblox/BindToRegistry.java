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
 * Used for binding a bean to the registry.
 *
 * This annotation is not supported with zwangine-spring or zwangine-spring-boot as they have their own set of annotations for
 * registering beans in spring bean registry. Instead, this annotation is intended for Zwangine standalone such as
 * zwangine-main or zwangine-quarkus or similar runtimes.
 *
 * If no name is specified then the bean will have its name auto computed based on the class name, field name, or method
 * name where the annotation is configured.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
public @interface BindToRegistry {

    /**
     * The name of the bean
     */
    String value() default "";

    /**
     * Whether to perform bean post-processing (dependency injection) on the bean
     */
    boolean beanPostProcess() default false;

    /**
     * Whether to create the bean instance lazy (on-demand) instead of creating eager. Using lazy can be useful when you
     * only need to create beans if they are explicit in-use.
     *
     * NOTE: lazy does not support init or destroy methods.
     */
    boolean lazy() default false;

    /**
     * The optional name of a method to call on the bean instance during initialization.
     *
     * If no destroy method has been configured, then Zwangine will auto-detect as follows: If the bean is {@link Service}
     * then start method is used.
     */
    String initMethod() default "";

    /**
     * The optional name of a method to call on the bean instance during destruction.
     *
     * If no destroy method has been configured, then Zwangine will auto-detect as follows: If the bean is {@link Service}
     * then stop method is used. If the bean is {@link java.io.Closeable} then close method is used.
     */
    String destroyMethod() default "";
}
