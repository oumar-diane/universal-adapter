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
package org.zenithblox.model.transformer;

import org.zenithblox.spi.Metadata;

/**
 * Represents an endpoint {@link org.zenithblox.spi.Transformer} which leverages zwangine
 * {@link org.zenithblox.Endpoint} to perform transformation. A
 * {@link org.zenithblox.processor.transformer.ProcessorTransformer} will be created internally with a
 * {@link org.zenithblox.processor.SendProcessor} which forwards the message to the specified Endpoint. One of the
 * Endpoint 'ref' or 'uri' needs to be specified.
 */
@Metadata(label = "transformation")
public class EndpointTransformerDefinition extends TransformerDefinition {

    private String ref;
    private String uri;

    public EndpointTransformerDefinition() {
    }

    protected EndpointTransformerDefinition(EndpointTransformerDefinition source) {
        super(source);
        this.ref = source.ref;
        this.uri = source.uri;
    }

    @Override
    public EndpointTransformerDefinition copyDefinition() {
        return new EndpointTransformerDefinition(this);
    }

    public String getRef() {
        return ref;
    }

    /**
     * Set the reference of the Endpoint.
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getUri() {
        return uri;
    }

    /**
     * Set the URI of the Endpoint.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

}
