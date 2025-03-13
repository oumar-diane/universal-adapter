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
 * A workflow template parameter
 */
@Metadata(label = "configuration")
public class WorkflowTemplateParameterDefinition {

    String name;
    Boolean required;
    String defaultValue;
    String description;

    public WorkflowTemplateParameterDefinition() {
    }

    public WorkflowTemplateParameterDefinition(String name, String defaultValue, String description) {
        this.name = name;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public boolean isRequired() {
        // assumed to be required if not set explicit to false
        return required == null || required;
    }

    public String getName() {
        return name;
    }

    /**
     * The name of the parameter
     */
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getRequired() {
        return required;
    }

    /**
     * Whether the parameter is required or not. A parameter is required unless this option is set to false or a default
     * value has been configured.
     */
    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Default value of the parameter. If a default value is provided then the parameter is implied not to be required.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Description of the parameter
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
