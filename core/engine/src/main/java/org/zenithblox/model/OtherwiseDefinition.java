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
 * Workflow to be executed when all other choices evaluate to false
 */
@Metadata(label = "eip,routing")
public class OtherwiseDefinition extends OptionalIdentifiedDefinition<OtherwiseDefinition>
        implements CopyableDefinition<OtherwiseDefinition>, Block, DisabledAwareDefinition, OutputNode {

    private ProcessorDefinition<?> parent;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean",
              description = "Disables this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled late at runtime.")
    private String disabled;
    private List<ProcessorDefinition<?>> outputs = new ArrayList<>();

    public OtherwiseDefinition() {
    }

    protected OtherwiseDefinition(OtherwiseDefinition source) {
        super(source);
        this.parent = source.parent;
        this.outputs = ProcessorDefinitionHelper.deepCopyDefinitions(source.outputs);
    }

    @Override
    public OtherwiseDefinition copyDefinition() {
        return new OtherwiseDefinition(this);
    }

    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        this.outputs = outputs;
    }

    @Override
    public ProcessorDefinition<?> getParent() {
        return parent;
    }

    public void setParent(ProcessorDefinition<?> parent) {
        this.parent = parent;
    }

    @Override
    public void addOutput(ProcessorDefinition<?> output) {
        output.setParent(parent);
        outputs.add(output);
    }

    @Override
    public void setId(String id) {
        if (outputs.isEmpty()) {
            super.setId(id);
        } else {
            var last = outputs.get(outputs.size() - 1);
            last.setId(id);
        }
    }

    @Override
    public String toString() {
        return "Otherwise[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "otherwise";
    }

    @Override
    public String getLabel() {
        return "otherwise";
    }

    @Override
    public String getDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }
}
