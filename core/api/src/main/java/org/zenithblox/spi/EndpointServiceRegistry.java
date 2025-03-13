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

import org.zenithblox.StaticService;

import java.util.List;
import java.util.Map;

/**
 * Registry for {@link EndpointServiceLocation} to make it easy to find information about usage of external services
 * such as databases, message brokers, cloud systems, that Zwangine is connecting to.
 *
 * @see EndpointServiceLocation
 */
public interface EndpointServiceRegistry extends StaticService {

    /**
     * Details about the endpoint service
     */
    interface EndpointService {

        /**
         * The Zwangine component for this endpoint service (such as jms, kafka, aws-s3).
         */
        String getComponent();

        /**
         * The endpoint uri of this endpoint service.
         */
        String getEndpointUri();

        /**
         * Gets the remote address such as URL, hostname, or connection-string that are component specific
         *
         * @return the address or null if no address can be determined.
         */
        String getServiceUrl();

        /**
         * Get the protocol the service is using such as http, amqp, tcp.
         */
        String getServiceProtocol();

        /**
         * Optional metadata that is relevant to the service as key value pairs. Notice that the metadata is not
         * supposed to contain sensitive security details such as access token, api keys, or passwords. Only share
         * information that can be safely accessed and written to logs.
         *
         * @return optional metadata or null if no data
         */
        default Map<String, String> getServiceMetadata() {
            return null;
        }

        /**
         * Is this service hosted (in this Zwangine application). For example an embedded HTTP server.
         */
        boolean isHostedService();

        /**
         * Direction of the service.
         *
         * IN = Zwangine (consumer) receives events from the external system. OUT = Zwangine (producer) sending messages to
         * the external system.
         */
        String getDirection();

        /**
         * Usage of the endpoint service, such as how many messages it has received / sent to
         * <p/>
         * This information is only available if {@link org.zenithblox.ManagementStatisticsLevel} is configured as
         * {@link org.zenithblox.ManagementStatisticsLevel#Extended}.
         */
        long getHits();

        /**
         * The workflow id where this service is used as workflow consumer, or used in the workflow by a send processor.
         */
        String getWorkflowId();

    }

    /**
     * List all endpoint services from this registry.
     */
    List<EndpointService> listAllEndpointServices();

    /**
     * Number of endpoint services in the registry.
     */
    int size();

}
