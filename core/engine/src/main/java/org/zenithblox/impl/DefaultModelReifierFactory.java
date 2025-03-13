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
package org.zenithblox.impl;

import org.zenithblox.*;
import org.zenithblox.model.DataFormatDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.errorhandler.DefaultErrorHandlerDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.reifier.WorkflowReifier;
import org.zenithblox.reifier.dataformat.DataFormatReifier;
import org.zenithblox.reifier.errorhandler.ErrorHandlerReifier;
import org.zenithblox.reifier.language.ExpressionReifier;
import org.zenithblox.reifier.transformer.TransformerReifier;
import org.zenithblox.reifier.validator.ValidatorReifier;
import org.zenithblox.spi.DataFormat;
import org.zenithblox.spi.ModelReifierFactory;
import org.zenithblox.spi.Transformer;
import org.zenithblox.spi.Validator;

/**
 * Default {@link ModelReifierFactory}
 */
public class DefaultModelReifierFactory implements ModelReifierFactory {

    @Override
    public Workflow createWorkflow(ZwangineContext zwangineContext, Object workflowDefinition) {
        return new WorkflowReifier(zwangineContext, (ProcessorDefinition<?>) workflowDefinition).createWorkflow();
    }

    @Override
    public DataFormat createDataFormat(ZwangineContext zwangineContext, Object dataFormatDefinition) {
        return DataFormatReifier.reifier(zwangineContext, (DataFormatDefinition) dataFormatDefinition).createDataFormat();
    }

    @Override
    public Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception {
        return createErrorHandler(workflow, workflow.getErrorHandlerFactory(), processor);
    }

    @Override
    public Processor createErrorHandler(Workflow workflow, ErrorHandlerFactory errorHandlerFactory, Processor processor)
            throws Exception {
        return ErrorHandlerReifier.reifier(workflow, errorHandlerFactory).createErrorHandler(processor);
    }

    @Override
    public ErrorHandlerFactory createDefaultErrorHandler() {
        return new DefaultErrorHandlerDefinition();
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, Object expressionDefinition) {
        return ExpressionReifier.reifier(zwangineContext, (ExpressionDefinition) expressionDefinition).createExpression();
    }

    @Override
    public Predicate createPredicate(ZwangineContext zwangineContext, Object expressionDefinition) {
        return ExpressionReifier.reifier(zwangineContext, (ExpressionDefinition) expressionDefinition).createPredicate();
    }

    @Override
    public Transformer createTransformer(ZwangineContext zwangineContext, Object transformerDefinition) {
        return TransformerReifier.reifier(zwangineContext, (TransformerDefinition) transformerDefinition).createTransformer();
    }

    @Override
    public Validator createValidator(ZwangineContext zwangineContext, Object transformerDefinition) {
        return ValidatorReifier.reifier(zwangineContext, (ValidatorDefinition) transformerDefinition).createValidator();
    }
}
