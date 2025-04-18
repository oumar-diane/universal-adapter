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
import org.zenithblox.model.transformer.CustomTransformerDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.spi.Transformer;

public class CustomTransformerReifier extends TransformerReifier<CustomTransformerDefinition> {

    public CustomTransformerReifier(ZwangineContext zwangineContext, TransformerDefinition definition) {
        super(zwangineContext, (CustomTransformerDefinition) definition);
    }

    @Override
    protected Transformer doCreateTransformer() {
        if (definition.getRef() == null && definition.getClassName() == null) {
            throw new IllegalArgumentException("'ref' or 'className' must be specified for customTransformer");
        }
        Transformer transformer;
        String ref = parseString(definition.getRef());
        if (ref != null) {
            transformer = lookupByNameAndType(ref, Transformer.class);
            if (transformer == null) {
                throw new IllegalArgumentException("Cannot find transformer with ref:" + definition.getRef());
            }
            if (transformer.getName() != null || transformer.getFrom() != null || transformer.getTo() != null) {
                throw new IllegalArgumentException(
                        String.format("Transformer '%s' is already in use. Please check if duplicate transformer exists.",
                                definition.getRef()));
            }
        } else {
            Class<Transformer> transformerClass
                    = zwangineContext.getClassResolver().resolveClass(definition.getClassName(), Transformer.class);
            if (transformerClass == null) {
                throw new IllegalArgumentException("Cannot find transformer class: " + definition.getClassName());
            }
            transformer = zwangineContext.getInjector().newInstance(transformerClass, false);
        }
        transformer.setZwangineContext(zwangineContext);
        return transformer.setName(definition.getScheme(), definition.getName()).setFrom(definition.getFromType())
                .setTo(definition.getToType());
    }

}
