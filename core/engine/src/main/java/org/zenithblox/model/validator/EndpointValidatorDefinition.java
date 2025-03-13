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
package org.zenithblox.model.validator;

import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Validator;

/**
 * Represents an endpoint {@link Validator} which leverages zwangine validator component such as
 * <a href="http://zwangine.zentihblox.org/validation.html">Validator Component</a> and
 * <a href="http://zwangine.zentihblox.org/bean-validation.html">Bean Validator Component</a> to perform content validation. A
 * {@link org.zenithblox.processor.validator.ProcessorValidator} will be created internally with a
 * {@link org.zenithblox.processor.SendProcessor} which forwards the message to the validator Endpoint.
 * {@see ValidatorDefinition} {@see Validator}
 */
@Metadata(label = "validation")
public class EndpointValidatorDefinition extends ValidatorDefinition {

    private String ref;
    private String uri;

    public EndpointValidatorDefinition() {
    }

    public EndpointValidatorDefinition(EndpointValidatorDefinition source) {
        super(source);
        this.ref = source.ref;
        this.uri = source.ref;
    }

    @Override
    public EndpointValidatorDefinition copyDefinition() {
        return new EndpointValidatorDefinition(this);
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
