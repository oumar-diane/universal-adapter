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

import java.lang.annotation.*;

/**
 * An annotation used to mark classes to indicate code capable of configuring its options via a getter/setters that can
 * be called via Zwangines {@link org.zenithblox.spi.PropertyConfigurer}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE })
public @interface Configurer {

    /**
     * Whether to let the Zwangine compiler plugin to generate java source code for fast configuration.
     */
    boolean generateConfigurer() default true;

    /**
     * Whether this configurer should include extended configurer methods. For example API based components would
     * require this.
     */
    boolean extended() default false;

    /**
     * Whether this configurer is only used during bootstrap
     */
    boolean bootstrap() default false;

    /**
     * Whether to only include fields that are have @Metadata annotations.
     */
    boolean metadataOnly() default false;

}
