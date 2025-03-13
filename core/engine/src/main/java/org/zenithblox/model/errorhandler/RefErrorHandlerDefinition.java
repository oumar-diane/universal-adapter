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
package org.zenithblox.model.errorhandler;

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.spi.Metadata;

/**
 * References to an existing or custom error handler.
 */
public class RefErrorHandlerDefinition extends BaseErrorHandlerDefinition {

    public static final String DEFAULT_ERROR_HANDLER_BUILDER = "ZwangineDefaultErrorHandlerBuilder";

    @Metadata(javaType = "org.zenithblox.ErrorHandlerFactory")
    private String ref;

    public RefErrorHandlerDefinition() {
    }

    public RefErrorHandlerDefinition(RefErrorHandlerDefinition source) {
        this.ref = source.ref;
    }

    @Override
    public RefErrorHandlerDefinition copyDefinition() {
        return new RefErrorHandlerDefinition(this);
    }

    public RefErrorHandlerDefinition(String ref) {
        this.ref = ref;
    }

    @Override
    public boolean supportTransacted() {
        return false;
    }

    @Override
    public ErrorHandlerFactory cloneBuilder() {
        // clone not needed
        return this;
    }

    public String getRef() {
        return ref;
    }

    /**
     * References to an existing or custom error handler.
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * References to an existing or custom error handler.
     */
    public RefErrorHandlerDefinition ref(String ref) {
        setRef(ref);
        return this;
    }

}
