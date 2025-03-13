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
package org.zenithblox.model.validator;

import org.zenithblox.model.ProcessorDefinitionHelper;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * To configure validators.
 */
@Metadata(label = "validation", title = "Validations")
public class ValidatorsDefinition {

    private List<ValidatorDefinition> validators;

    public ValidatorsDefinition() {
    }

    protected ValidatorsDefinition(ValidatorsDefinition source) {
        this.validators = ProcessorDefinitionHelper.deepCopyDefinitions(source.validators);
    }

    /**
     * The configured transformers
     */
    public void setValidators(List<ValidatorDefinition> validators) {
        this.validators = validators;
    }

    public List<ValidatorDefinition> getValidators() {
        return validators;
    }

}
