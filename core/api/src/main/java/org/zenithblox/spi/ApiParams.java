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
package org.zenithblox.spi;

import java.lang.annotation.*;

/**
 * Used to annotate a nested configuration parameter type (such as a nested Configuration object) which can then be used
 * on a API based component, endpoint.
 *
 * This is only applicable for API based components where configurations are separated by API names and methods
 * (grouping).
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE })
public @interface ApiParams {

    /**
     * The API name (grouping) of this configuration class.
     */
    String apiName() default "";

    /**
     * Returns a description of the API.
     *
     * This is used for documentation and tooling only.
     */
    String description() default "";

    /**
     * Whether this API can only be used as a producer.
     *
     * By default its assumed the API can be used as both consumer and producer.
     */
    boolean producerOnly() default false;

    /**
     * Whether this API can only be used as a consumer.
     *
     * By default its assumed the API can be used as both consumer and producer.
     */
    boolean consumerOnly() default false;

    /**
     * The API methods that the API provides of this configuration class.
     */
    ApiMethod[] apiMethods();

    /**
     * Returns the method alias(s) of this api method. The syntax for an alias is pattern=name where pattern is a
     * regular expression.
     *
     * This is used for documentation and tooling only.
     */
    String[] aliases() default "";

}
