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
package org.zenithblox.catalog;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.StaticService;
import org.zenithblox.component.extension.ComponentVerifierExtension;
import org.zenithblox.spi.SendDynamicAware;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Runtime catalog which limited API needed by components that supports {@link ComponentVerifierExtension}.
 */
public interface RuntimeZwangineCatalog extends StaticService, ZwangineContextAware {

    /**
     * Service factory key.
     */
    String FACTORY = "runtime-zwanginecatalog";

    /**
     * Returns the component information as JSON format.
     * <p/>
     * This API is needed by {@link ComponentVerifierExtension}.
     *
     * @param  name the component name
     * @return      component details in JSon
     */
    String componentJSonSchema(String name);

    /**
     * Parses the endpoint uri and constructs a key/value properties of each option.
     * <p/>
     * This API is needed by {@link SendDynamicAware}.
     *
     * @param  uri the endpoint uri
     * @return     properties as key value pairs of each endpoint option
     */
    Map<String, String> endpointProperties(String uri) throws URISyntaxException;

    /**
     * Parses the endpoint uri and constructs a key/value properties of only the lenient properties (eg custom options)
     * <p/>
     * For example using the HTTP components to provide query parameters in the endpoint uri.
     * <p/>
     * This API is needed by {@link SendDynamicAware}.
     *
     * @param  uri the endpoint uri
     * @return     properties as key value pairs of each lenient properties
     */
    Map<String, String> endpointLenientProperties(String uri) throws URISyntaxException;

    /**
     * Validates the properties for the given scheme against component and endpoint
     * <p/>
     * This API is needed by {@link ComponentVerifierExtension}.
     *
     * @param  scheme     the endpoint scheme
     * @param  properties the endpoint properties
     * @return            validation result
     */
    EndpointValidationResult validateProperties(String scheme, Map<String, String> properties);

    /**
     * Creates an endpoint uri in Java style from the information from the properties
     * <p/>
     * This API is needed by {@link SendDynamicAware}.
     *
     * @param  scheme                      the endpoint schema
     * @param  properties                  the properties as key value pairs
     * @param  encode                      whether to URL encode the returned uri or not
     * @return                             the constructed endpoint uri
     * @throws URISyntaxException is thrown if there is encoding error
     */
    String asEndpointUri(String scheme, Map<String, String> properties, boolean encode) throws URISyntaxException;

}
