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
 * Throws an exception
 */
@Metadata(label = "error")
public class ThrowExceptionDefinition extends NoOutputDefinition<ThrowExceptionDefinition> {

    private Exception exception;
    private Class<? extends Exception> exceptionClass;

    private String message;
    private String exceptionType;
    @Metadata(label = "advanced")
    private String ref;

    public ThrowExceptionDefinition() {
    }

    protected ThrowExceptionDefinition(ThrowExceptionDefinition source) {
        super(source);
        this.exception = source.exception;
        this.exceptionClass = source.exceptionClass;
        this.message = source.message;
        this.exceptionType = source.exceptionType;
        this.ref = source.ref;
    }

    @Override
    public ThrowExceptionDefinition copyDefinition() {
        return new ThrowExceptionDefinition(this);
    }

    @Override
    public String toString() {
        return "ThrowException[" + description() + "]";
    }

    protected String description() {
        if (exception != null) {
            return exception.getClass().getCanonicalName();
        } else if (ref != null) {
            return "ref:" + ref;
        } else {
            return "";
        }
    }

    @Override
    public String getShortName() {
        return "throwException";
    }

    @Override
    public String getLabel() {
        return "throwException[" + description() + "]";
    }

    public String getRef() {
        return ref;
    }

    /**
     * Reference to the exception instance to lookup from the registry to throw
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    /**
     * To create a new exception instance and use the given message as caused message (supports simple language)
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    /**
     * The class of the exception to create using the message.
     *
     * @see #setMessage(String)
     */
    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public Class<? extends Exception> getExceptionClass() {
        return exceptionClass;
    }

    /**
     * The class of the exception to create using the message.
     *
     * @see #setMessage(String)
     */
    public void setExceptionClass(Class<? extends Exception> exceptionClass) {
        this.exceptionClass = exceptionClass;
    }
}
