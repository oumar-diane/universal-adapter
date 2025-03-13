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
package org.zenithblox.reifier.validator;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.ExchangePattern;
import org.zenithblox.model.validator.EndpointValidatorDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.processor.validator.ProcessorValidator;
import org.zenithblox.spi.Validator;

public class EndpointValidatorReifier extends ValidatorReifier<EndpointValidatorDefinition> {

    public EndpointValidatorReifier(ZwangineContext zwangineContext, ValidatorDefinition definition) {
        super(zwangineContext, (EndpointValidatorDefinition) definition);
    }

    @Override
    protected Validator doCreateValidator() {
        Endpoint endpoint = definition.getUri() != null
                ? zwangineContext.getEndpoint(definition.getUri()) : lookupByNameAndType(definition.getRef(), Endpoint.class);
        SendProcessor processor = new SendProcessor(endpoint, ExchangePattern.InOut);
        return new ProcessorValidator(zwangineContext).setProcessor(processor).setType(definition.getType());
    }

}
