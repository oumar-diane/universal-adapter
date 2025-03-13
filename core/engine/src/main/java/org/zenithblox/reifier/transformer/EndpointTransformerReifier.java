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
package org.zenithblox.reifier.transformer;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.ExchangePattern;
import org.zenithblox.model.transformer.EndpointTransformerDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.processor.SendProcessor;
import org.zenithblox.processor.transformer.ProcessorTransformer;
import org.zenithblox.spi.Transformer;

public class EndpointTransformerReifier extends TransformerReifier<EndpointTransformerDefinition> {

    public EndpointTransformerReifier(ZwangineContext zwangineContext, TransformerDefinition definition) {
        super(zwangineContext, (EndpointTransformerDefinition) definition);
    }

    @Override
    protected Transformer doCreateTransformer() {
        Endpoint endpoint = definition.getUri() != null
                ? zwangineContext.getEndpoint(definition.getUri())
                : lookupByNameAndType(parseString(definition.getRef()), Endpoint.class);
        SendProcessor processor = new SendProcessor(endpoint, ExchangePattern.InOut);
        return new ProcessorTransformer(zwangineContext).setProcessor(processor)
                .setName(parseString(definition.getScheme()), parseString(definition.getName()))
                .setFrom(parseString(definition.getFromType()))
                .setTo(parseString(definition.getToType()));
    }

}
