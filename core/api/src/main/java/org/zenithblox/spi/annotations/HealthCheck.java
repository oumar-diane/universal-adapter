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
package org.zenithblox.spi.annotations;

import java.lang.annotation.*;

/**
 * Marks a class as a custom health-check or health-check repository.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE })
@ServiceFactory("health-check")
public @interface HealthCheck {

    /**
     * The ID of the health check.
     *
     * Use <var>-check</var> as prefix for health checks, and use <var>-repository</var> as prefix for health-check
     * repository. For example to use myfoo as the ID for a health-check, then set this value as <var>myfoo-check</var>.
     */
    String value();

}
