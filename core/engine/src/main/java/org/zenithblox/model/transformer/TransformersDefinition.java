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

import org.zenithblox.model.CopyableDefinition;
import org.zenithblox.model.ProcessorDefinitionHelper;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * To configure transformers.
 */
@Metadata(label = "transformation", title = "Transformations")
public class TransformersDefinition implements CopyableDefinition<TransformersDefinition> {

    private List<TransformerDefinition> transformers;

    public TransformersDefinition() {
    }

    protected TransformersDefinition(TransformersDefinition source) {
        this.transformers = ProcessorDefinitionHelper.deepCopyDefinitions(source.transformers);
    }

    @Override
    public TransformersDefinition copyDefinition() {
        return new TransformersDefinition(this);
    }

    /**
     * The configured transformers
     */
    public void setTransformers(List<TransformerDefinition> transformers) {
        this.transformers = transformers;
    }

    public List<TransformerDefinition> getTransformers() {
        return transformers;
    }
}
