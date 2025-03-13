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

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.spi.Metadata;

/**
 * Zwangine error handling.
 */
@Metadata(label = "configuration,error")
public class ErrorHandlerDefinition extends IdentifiedType {

    private ErrorHandlerFactory errorHandlerType;

    public ErrorHandlerFactory getErrorHandlerType() {
        return errorHandlerType;
    }

    /**
     * The specific error handler in use.
     */
    public void setErrorHandlerType(ErrorHandlerFactory errorHandlerType) {
        this.errorHandlerType = errorHandlerType;
    }

    @Override
    public String toString() {
        return "ErrorHandler[" + description() + "]";
    }

    protected String description() {
        return errorHandlerType != null ? errorHandlerType.toString() : "";
    }

}
