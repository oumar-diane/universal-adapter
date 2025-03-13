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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Processor;

import java.util.Map;

/**
 * Allows SPI to plugin a {@link RestApiProcessorFactory} that creates the Zwangine {@link Processor} responsible for
 * servicing and generating the REST API documentation.
 * <p/>
 * For example the <tt>zwangine-openapi-java</tt> component provides such a factory that uses OpenAPI/Swagger to generate
 * the documentation.
 */
public interface RestApiProcessorFactory {

    /**
     * Creates a new REST API <a href="http://zwangine.zwangine.org/processor.html">Processor </a>, which provides API
     * listing of the REST services
     *
     * @param  zwangineContext  the zwangine context
     * @param  contextPath   the context-path
     * @param  configuration the rest configuration
     * @param  parameters    additional parameters
     * @return               a newly created REST API provider
     * @throws Exception     can be thrown
     */
    Processor createApiProcessor(
            ZwangineContext zwangineContext, String contextPath,
            RestConfiguration configuration, Map<String, Object> parameters)
            throws Exception;

}
