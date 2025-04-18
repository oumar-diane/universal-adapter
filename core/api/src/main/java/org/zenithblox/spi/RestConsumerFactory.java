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
import org.zenithblox.Consumer;
import org.zenithblox.Processor;

import java.util.Map;

/**
 * Allows SPI to plugin a {@link RestConsumerFactory} that creates the Zwangine {@link Consumer} responsible for handling
 * incoming HTTP requests from clients that request to access REST services which has been created using the
 * <a href="http://zwangine.zwangine.org/rest-dsl">rest-dsl</a>.
 *
 * @see RestApiConsumerFactory
 * @see RestOpenApiConsumerFactory
 * @see RestApiProcessorFactory
 */
public interface RestConsumerFactory {

    /**
     * Creates a new REST <a href="http://zwangine.zwangine.org/event-driven-consumer.html">Event Driven Consumer</a>, which
     * consumes messages from the endpoint using the given processor
     *
     * @param  zwangineContext  the zwangine context
     * @param  processor     the processor
     * @param  verb          HTTP verb such as GET, POST
     * @param  basePath      base path
     * @param  uriTemplate   uri template
     * @param  consumes      media-types for what this REST service consume as input (accept-type), is <tt>null</tt> or
     *                       <tt>&#42;/&#42;</tt> for anything
     * @param  produces      media-types for what this REST service produces as output, can be <tt>null</tt>
     * @param  configuration REST configuration
     * @param  parameters    additional parameters
     * @return               a newly created REST consumer
     * @throws Exception     can be thrown
     */
    Consumer createConsumer(
            ZwangineContext zwangineContext, Processor processor, String verb, String basePath, String uriTemplate,
            String consumes, String produces, RestConfiguration configuration, Map<String, Object> parameters)
            throws Exception;
}
