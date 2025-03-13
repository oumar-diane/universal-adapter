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
package org.zenithblox.model.dataformat;

import org.zenithblox.model.CopyableDefinition;
import org.zenithblox.spi.Metadata;

@Metadata(label = "dataformat,transformation,yaml", title = "YAML Type Filter")
public class YAMLTypeFilterDefinition implements CopyableDefinition<YAMLTypeFilterDefinition> {

    private String value;
    @Metadata(javaType = "org.zenithblox.model.dataformat.YAMLTypeFilterType")
    private String type;

    public YAMLTypeFilterDefinition() {
    }

    protected YAMLTypeFilterDefinition(YAMLTypeFilterDefinition source) {
        this.value = source.value;
        this.type = source.type;
    }

    @Override
    public YAMLTypeFilterDefinition copyDefinition() {
        return new YAMLTypeFilterDefinition(this);
    }

    public String getValue() {
        return value;
    }

    /**
     * Value of type such as class name or regular expression
     */
    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    /**
     * Whether to filter by class type or regular expression
     */
    public void setType(String type) {
        this.type = type;
    }
}
