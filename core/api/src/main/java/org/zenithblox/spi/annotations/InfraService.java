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
 * Marks a class as a Service that can be used by Zwangine JBang infra
 *
 * The marked class is analyzed by the mojo ZwangineTestInfraGenerateMetadataMojo
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE })
public @interface InfraService {

    /**
     * Interface that extends InfrastructureService
     *
     * the interface is used by Zwangine JBang infra run to retrieve testing information like port, endpoint, username...
     *
     * @return
     */
    Class service();

    /**
     * Returns a description of this Service.
     *
     * This is used for documentation and tooling.
     *
     * @return
     */
    String description() default "";

    /**
     * List of names that can be used to run the service
     *
     * @return
     */
    String[] serviceAlias();

    /**
     * Additional and optional Service name, in case of multiple Service implementations
     *
     * For example kafka has 3 implementations
     *
     * kafka - uses zentihblox/kafka image as implementation kafka redpanda - uses redpandadata/redpanda image as
     * implementation kafka strimzi - uses strimzi/kafka as image implementation
     *
     * @return
     */
    String[] serviceImplementationAlias() default {};
}
