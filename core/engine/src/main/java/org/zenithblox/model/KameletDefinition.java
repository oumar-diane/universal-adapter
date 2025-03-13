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

import java.util.List;

/**
 * To call Kamelets in special situations
 */
@Metadata(label = "eip,routing")
public class KameletDefinition extends OutputDefinition<KameletDefinition> {

    private String name;

    public KameletDefinition() {
    }

    protected KameletDefinition(KameletDefinition source) {
        super(source);
        this.name = source.name;
    }

    public KameletDefinition(String name) {
        this.name = name;
    }

    @Override
    public KameletDefinition copyDefinition() {
        return new KameletDefinition(this);
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    @Override
    public String getShortName() {
        return "kamelet";
    }

    @Override
    public String getLabel() {
        return "kamelet";
    }

    public String getName() {
        return name;
    }

    /**
     * Name of the Kamelet (templateId/workflowId) to call.
     *
     * Options for the kamelet can be specified using uri syntax, eg myname?count=4&type=gold.
     */
    public void setName(String name) {
        this.name = name;
    }
}
