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
package org.zenithblox.reifier;

import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ThrowExceptionDefinition;
import org.zenithblox.processor.ThrowExceptionProcessor;

public class ThrowExceptionReifier extends ProcessorReifier<ThrowExceptionDefinition> {

    public ThrowExceptionReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (ThrowExceptionDefinition) definition);
    }

    @Override
    public Processor createProcessor() {
        Exception exception = definition.getException();
        String ref = parseString(definition.getRef());
        if (exception == null && ref != null) {
            exception = lookupByNameAndType(ref, Exception.class);
        }

        Class<? extends Exception> exceptionClass = definition.getExceptionClass();
        if (exceptionClass == null && definition.getExceptionType() != null) {
            exceptionClass = parse(Class.class, definition.getExceptionType());
        }

        if (exception == null && exceptionClass == null) {
            throw new IllegalArgumentException("exception/ref or exceptionClass/exceptionType must be configured on: " + this);
        }
        return new ThrowExceptionProcessor(exception, exceptionClass, parseString(definition.getMessage()));
    }

}
