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

/**
 * Removes a named variable
 */
@Metadata(label = "eip,transformation")
public class RemoveVariableDefinition extends NoOutputDefinition<RemoveVariableDefinition> {

    private String name;

    public RemoveVariableDefinition() {
    }

    protected RemoveVariableDefinition(RemoveVariableDefinition source) {
        super(source);
        this.name = source.name;
    }

    public RemoveVariableDefinition(String variableName) {
        this.name = variableName;
    }

    @Override
    public RemoveVariableDefinition copyDefinition() {
        return new RemoveVariableDefinition(this);
    }

    @Override
    public String toString() {
        return "RemoveVariable[" + name + "]";
    }

    @Override
    public String getShortName() {
        return "removeVariable";
    }

    @Override
    public String getLabel() {
        return "removeVariable[" + name + "]";
    }

    public String getName() {
        return name;
    }

    /**
     * Name of variable to remove.
     */
    public void setName(String name) {
        this.name = name;
    }
}
