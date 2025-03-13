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
 * Removes a named property from the message exchange
 */
@Metadata(label = "eip,transformation")
public class RemovePropertyDefinition extends NoOutputDefinition<RemovePropertyDefinition> {

    private String name;

    public RemovePropertyDefinition() {
    }

    protected RemovePropertyDefinition(RemovePropertyDefinition source) {
        super(source);
        this.name = source.name;
    }

    @Override
    public RemovePropertyDefinition copyDefinition() {
        return new RemovePropertyDefinition(this);
    }

    public RemovePropertyDefinition(String propertyName) {
        this.name = propertyName;
    }

    @Override
    public String toString() {
        return "RemoveProperty[" + name + "]";
    }

    @Override
    public String getShortName() {
        return "removeProperty";
    }

    @Override
    public String getLabel() {
        return "removeProperty[" + name + "]";
    }

    public String getName() {
        return name;
    }

    /**
     * Name of property to remove.
     */
    public void setName(String name) {
        this.name = name;
    }
}
