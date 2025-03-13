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
import org.zenithblox.model.transformer.LoadTransformerDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.processor.transformer.AnnotationTransformerLoader;
import org.zenithblox.processor.transformer.DefaultTransformerLoader;
import org.zenithblox.spi.Transformer;

public class LoadTransformerReifier extends TransformerReifier<LoadTransformerDefinition> {

    public LoadTransformerReifier(ZwangineContext zwangineContext, TransformerDefinition definition) {
        super(zwangineContext, (LoadTransformerDefinition) definition);
    }

    @Override
    protected Transformer doCreateTransformer() {
        if (definition.getDefaults() != null && parseBoolean(definition.getDefaults(), false)) {
            return new DefaultTransformerLoader();
        } else {
            AnnotationTransformerLoader transformerLoader = new AnnotationTransformerLoader();
            transformerLoader.setZwangineContext(zwangineContext);
            transformerLoader.setPackageName(definition.getPackageScan());
            return transformerLoader;
        }
    }

}
