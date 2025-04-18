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

import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * A useful base class for output types
 */
@Metadata(label = "configuration")
public class OutputDefinition<Type extends ProcessorDefinition<Type>> extends ProcessorDefinition<Type> implements OutputNode {

    protected List<ProcessorDefinition<?>> outputs = new ArrayList<>();

    public OutputDefinition() {
    }

    protected OutputDefinition(OutputDefinition<?> source) {
        super(source);
        this.outputs = ProcessorDefinitionHelper.deepCopyDefinitions(source.outputs);
    }

    @Override
    public ProcessorDefinition<?> copyDefinition() {
        throw new UnsupportedOperationException("Should be implemented in child classes");
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        this.outputs = outputs;
        if (outputs != null) {
            for (ProcessorDefinition<?> output : outputs) {
                configureChild(output);
            }
        }
    }

    @Override
    public String getShortName() {
        return "output";
    }

    @Override
    public String toString() {
        return getShortName() + " -> [" + outputs + "]";
    }

}
