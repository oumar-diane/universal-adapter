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

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.OnExceptionDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.CatchProcessor;
import org.zenithblox.processor.FatalFallbackErrorHandler;
import org.zenithblox.spi.ClassResolver;

import java.util.ArrayList;
import java.util.List;

public class OnExceptionReifier extends ProcessorReifier<OnExceptionDefinition> {

    public OnExceptionReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (OnExceptionDefinition) definition);
    }

    @Override
    public void addWorkflows() throws Exception {
        // must validate configuration before creating processor
        definition.validateConfiguration();

        if (parseBoolean(definition.getUseOriginalMessage(), false)) {
            // ensure allow original is turned on
            workflow.setAllowUseOriginalMessage(true);
        }

        // lets attach this on exception to the workflow error handler
        Processor child = createOutputsProcessor();
        if (child != null) {
            // wrap in our special safe fallback error handler if OnException
            // have child output
            Processor errorHandler = new FatalFallbackErrorHandler(child, false);
            String id = getId(definition);
            workflow.setOnException(id, errorHandler);
        }
        // lookup the error handler builder
        ErrorHandlerFactory builder = workflow.getErrorHandlerFactory();
        // and add this as error handlers
        workflow.addErrorHandler(builder, definition);
    }

    @Override
    public CatchProcessor createProcessor() throws Exception {
        // load exception classes
        List<Class<? extends Throwable>> classes = null;
        if (definition.getExceptions() != null && !definition.getExceptions().isEmpty()) {
            classes = createExceptionClasses(zwangineContext.getClassResolver());
        }

        if (parseBoolean(definition.getUseOriginalMessage(), false)) {
            // ensure allow original is turned on
            workflow.setAllowUseOriginalMessage(true);
        }

        // must validate configuration before creating processor
        definition.validateConfiguration();

        Processor childProcessor = this.createChildProcessor(false);

        Predicate when = null;
        if (definition.getOnWhen() != null) {
            definition.getOnWhen().preCreateProcessor();
            when = createPredicate(definition.getOnWhen().getExpression());
        }

        return new CatchProcessor(getZwangineContext(), classes, childProcessor, when);
    }

    protected List<Class<? extends Throwable>> createExceptionClasses(ClassResolver resolver) throws ClassNotFoundException {
        List<String> list = definition.getExceptions();
        List<Class<? extends Throwable>> answer = new ArrayList<>(list.size());
        for (String name : list) {
            Class<? extends Throwable> type = resolver.resolveMandatoryClass(name, Throwable.class);
            answer.add(type);
        }
        return answer;
    }

}
