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
package org.zenithblox.model;

import org.zenithblox.Endpoint;
import org.zenithblox.builder.EndpointProducerBuilder;
import org.zenithblox.spi.Metadata;
import org.zenithblox.util.URISupport;

/**
 * Sends the message to an endpoint
 */
public abstract class SendDefinition<Type extends ProcessorDefinition<Type>> extends NoOutputDefinition<Type>
        implements EndpointRequiredDefinition {

    private String endpointUriToString;
    protected Endpoint endpoint;
    protected EndpointProducerBuilder endpointProducerBuilder;

    @Metadata(required = true)
    protected String uri;

    public SendDefinition() {
    }

    public SendDefinition(String uri) {
        this.uri = uri;
    }

    protected SendDefinition(SendDefinition source) {
        super(source);
        this.endpointUriToString = source.endpointUriToString;
        this.endpoint = source.endpoint;
        this.endpointProducerBuilder = source.endpointProducerBuilder;
        this.uri = source.uri;
    }

    @Override
    public String getEndpointUri() {
        if (endpointProducerBuilder != null) {
            return endpointProducerBuilder.getUri();
        } else if (endpoint != null) {
            return endpoint.getEndpointUri();
        } else {
            return uri;
        }
    }

    public String getUri() {
        return uri;
    }

    /**
     * Sets the uri of the endpoint to send to.
     *
     * @param uri the uri of the endpoint
     */
    public void setUri(String uri) {
        clear();
        this.uri = uri;
    }

    /**
     * Gets the endpoint if an {@link Endpoint} instance was set.
     * <p/>
     * This implementation may return <tt>null</tt> which means you need to use {@link #getEndpointUri()} to get
     * information about the endpoint.
     *
     * @return the endpoint instance, or <tt>null</tt>
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(Endpoint endpoint) {
        clear();
        this.endpoint = endpoint;
        this.uri = endpoint != null ? endpoint.getEndpointUri() : null;
    }

    public EndpointProducerBuilder getEndpointProducerBuilder() {
        return endpointProducerBuilder;
    }

    public void setEndpointProducerBuilder(EndpointProducerBuilder endpointProducerBuilder) {
        clear();
        this.endpointProducerBuilder = endpointProducerBuilder;
    }

    public String getPattern() {
        return null;
    }

    @Override
    public String getLabel() {
        if (endpointUriToString == null) {
            String value = null;
            try {
                value = getEndpointUri();
            } catch (RuntimeException e) {
                // ignore any exception and use null for building the string value
            }
            // ensure to sanitize uri so we do not show sensitive information such as passwords
            endpointUriToString = URISupport.sanitizeUri(value);
        }

        String uri = endpointUriToString;
        return uri != null ? uri : "no uri supplied";
    }

    protected void clear() {
        this.endpointUriToString = null;
        this.endpointProducerBuilder = null;
        this.endpoint = null;
        this.uri = null;
    }
}
