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
import java.util.stream.Collectors;

/**
 * Workflow to be executed when Circuit Breaker EIP executes fallback
 */
@Metadata(label = "eip,routing,error")
public class OnFallbackDefinition extends OptionalIdentifiedDefinition<OnFallbackDefinition>
        implements CopyableDefinition<OnFallbackDefinition>, Block, OutputNode {

    private ProcessorDefinition<?> parent;
    @Metadata(label = "advanced", defaultValue = "false", javaType = "java.lang.Boolean")
    private String fallbackViaNetwork;
    private List<ProcessorDefinition<?>> outputs = new ArrayList<>();

    public OnFallbackDefinition() {
    }

    protected OnFallbackDefinition(OnFallbackDefinition source) {
        super(source);
        this.parent = source.parent;
        this.fallbackViaNetwork = source.fallbackViaNetwork;
        this.outputs = ProcessorDefinitionHelper.deepCopyDefinitions(source.outputs);
    }

    @Override
    public OnFallbackDefinition copyDefinition() {
        return new OnFallbackDefinition(this);
    }

    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        this.outputs = outputs;
    }

    @Override
    public String toString() {
        if (Boolean.toString(true).equals(fallbackViaNetwork)) {
            return "OnFallbackViaNetwork[" + getOutputs() + "]";
        } else if (fallbackViaNetwork == null || Boolean.toString(false).equals(fallbackViaNetwork)) {
            return "OnFallback[" + getOutputs() + "]";
        } else {
            return "OnFallback[viaNetwork=" + fallbackViaNetwork + "," + getOutputs() + "]";
        }
    }

    @Override
    public String getShortName() {
        return "onFallback";
    }

    @Override
    public String getLabel() {
        String name;
        if (Boolean.toString(true).equals(fallbackViaNetwork)) {
            name = "OnFallbackViaNetwork";
        } else if (fallbackViaNetwork == null || Boolean.toString(false).equals(fallbackViaNetwork)) {
            name = "onFallback";
        } else {
            name = "onFallback(viaNetwork=" + fallbackViaNetwork + ")";
        }
        return getOutputs().stream().map(ProcessorDefinition::getLabel)
                .collect(Collectors.joining(",", name + "[", "]"));
    }

    public String getFallbackViaNetwork() {
        return fallbackViaNetwork;
    }

    /**
     * Whether the fallback goes over the network.
     * <p/>
     * If the fallback will go over the network it is another possible point of failure. It is important to execute the
     * fallback command on a separate thread-pool, otherwise if the main command were to become latent and fill the
     * thread-pool this would prevent the fallback from running if the two commands share the same pool.
     */
    public void setFallbackViaNetwork(String fallbackViaNetwork) {
        this.fallbackViaNetwork = fallbackViaNetwork;
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
        outputs.add(output);
    }

}
