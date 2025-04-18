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

import org.zenithblox.Consumer;
import org.zenithblox.StaticService;

import java.util.List;

/**
 * A registry of all REST services running within the {@link org.zenithblox.ZwangineContext} which have been defined and
 * created using the <a href="http://zwangine.zwangine.org/rest-dsl">Rest DSL</a>.
 */
public interface RestRegistry extends StaticService {

    /**
     * Details about the REST service
     */
    interface RestService {

        /**
         * Gets the consumer of the REST service
         */
        Consumer getConsumer();

        /**
         * Is the rest service based on code-first or contract-first
         */
        boolean isContractFirst();

        /**
         * Gets the state of the REST service (started, stopped, etc)
         */
        String getState();

        /**
         * Gets the absolute url to the REST service (baseUrl + uriTemplate)
         */
        String getUrl();

        /**
         * Gets the base url to the REST service
         */
        String getBaseUrl();

        /**
         * Gets the base path to the REST service
         */
        String getBasePath();

        /**
         * Gets the uri template
         */
        String getUriTemplate();

        /**
         * Gets the HTTP method (GET, POST, PUT etc)
         */
        String getMethod();

        /**
         * Optional details about what media-types the REST service accepts
         */
        String getConsumes();

        /**
         * Optional details about what media-types the REST service returns
         */
        String getProduces();

        /**
         * Optional detail about input binding to a FQN class name.
         * <p/>
         * If the input accepts a list, then <tt>List&lt;class name&gt;</tt> is enclosed the name.
         */
        String getInType();

        /**
         * Optional detail about output binding to a FQN class name.
         * <p/>
         * If the output accepts a list, then <tt>List&lt;class name&gt;</tt> is enclosed the name.
         */
        String getOutType();

        /**
         * Optional description about this rest service.
         */
        String getDescription();

    }

    /**
     * Adds a new REST service to the registry.
     *
     * @param consumer      the consumer
     * @param contractFirst is the rest service based on code-first or contract-first
     * @param url           the absolute url of the REST service
     * @param baseUrl       the base url of the REST service
     * @param basePath      the base path
     * @param uriTemplate   the uri template
     * @param method        the HTTP method
     * @param consumes      optional details about what media-types the REST service accepts
     * @param produces      optional details about what media-types the REST service returns
     * @param inType        optional detail input binding to a FQN class name
     * @param outType       optional detail output binding to a FQN class name
     * @param workflowId       the id of the workflow this rest service will be using
     * @param description   optional description about the service
     */
    void addRestService(
            Consumer consumer, boolean contractFirst, String url, String baseUrl, String basePath, String uriTemplate,
            String method,
            String consumes, String produces, String inType, String outType, String workflowId, String description);

    /**
     * Removes the REST service from the registry
     *
     * @param consumer the consumer
     */
    void removeRestService(Consumer consumer);

    /**
     * List all REST services from this registry.
     *
     * @return all the REST services
     */
    List<RestService> listAllRestServices();

    /**
     * Number of rest services in the registry.
     *
     * @return number of rest services in the registry.
     */
    int size();

    /**
     * Outputs the Rest services API documentation in JSON (requires zwangine-openapi-java on classpath)
     *
     * @return the API docs in JSon, or <tt>null</tt> if zwangine-openapi-java is not on classpath
     */
    String apiDocAsJson();

}
