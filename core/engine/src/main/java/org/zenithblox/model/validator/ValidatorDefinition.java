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

import org.zenithblox.model.CopyableDefinition;
import org.zenithblox.model.InputTypeDefinition;
import org.zenithblox.model.OutputTypeDefinition;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Validator;

/**
 * <p>
 * Represents a {@link Validator} which declaratively validates message content according to the input type declared by
 * {@link InputTypeDefinition} and/or output type declared by {@link OutputTypeDefinition}.
 * </p>
 * <p>
 * If you specify type='xml:ABC', the validator will be picked up when current message type is 'xml:ABC'. If you specify
 * type='json', then it will be picked up for all of json validation. {@see Validator} {@see InputTypeDefinition}
 * {@see OutputTypeDefinition}
 */
@Metadata(label = "validation")
public abstract class ValidatorDefinition implements CopyableDefinition<ValidatorDefinition> {

    private String type;

    public String getType() {
        return type;
    }

    public ValidatorDefinition() {
    }

    protected ValidatorDefinition(ValidatorDefinition source) {
        this.type = source.type;
    }

    /**
     * Set the data type name. If you specify 'xml:XYZ', the validator will be picked up if message type is 'xml:XYZ'.
     * If you specify just 'xml', the validator matches with all of 'xml' message type like 'xml:ABC' or 'xml:DEF'.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Set the data type using Java class.
     */
    public void setType(Class<?> clazz) {
        this.type = new DataType(clazz).toString();
    }
}
