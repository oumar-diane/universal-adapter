/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Message;
import org.zenithblox.ValidationException;
import org.zenithblox.support.service.ServiceSupport;

/**
 * <a href="http://zwangine.zwangine.org/validator.html">Validator</a> performs message content validation according to the
 * declared data type. {@link org.zenithblox.processor.ContractAdvice} applies Validator if input/output type is
 * declared on a workflow with validation enabled.
 *
 * @see {@link org.zenithblox.processor.ContractAdvice} {@link org.zenithblox.model.InputTypeDefinition}
 *      {@link org.zenithblox.model.OutputTypeDefinition}
 */
public abstract class Validator extends ServiceSupport implements ZwangineContextAware {

    private ZwangineContext zwangineContext;
    private DataType type;

    /**
     * Perform data validation with specified type.
     *
     * @param  message             message to apply validation
     * @param  type                the data type
     * @throws ValidationException thrown if any validation error is detected
     */
    public abstract void validate(Message message, DataType type) throws ValidationException;

    /**
     * Get 'from' data type.
     */
    public DataType getType() {
        return type;
    }

    /**
     * Set data type.
     *
     * @param type data type
     */
    public Validator setType(String type) {
        this.type = new DataType(type);
        return this;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return this.zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext context) {
        this.zwangineContext = context;
    }

    @Override
    public String toString() {
        return String.format("%s[type='%s']", this.getClass().getSimpleName(), type);
    }

}
