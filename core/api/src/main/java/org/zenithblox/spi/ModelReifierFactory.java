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

import org.zenithblox.*;

/**
 * Factory that uses reifiers to build an entity from a given model.
 */
public interface ModelReifierFactory {

    /**
     * Service factory key for custom factories.
     */
    String FACTORY = "model-reifier-factory";

    /**
     * Creates the workflow from the model.
     */
    Workflow createWorkflow(ZwangineContext zwangineContext, Object workflowDefinition);

    /**
     * Creates the data format from the model.
     */
    DataFormat createDataFormat(ZwangineContext zwangineContext, Object dataFormatDefinition);

    /**
     * Creates the error handler for the workflow processor.
     */
    Processor createErrorHandler(Workflow workflow, Processor processor) throws Exception;

    /**
     * Creates the error handler using the factory for the workflow processor.
     */
    Processor createErrorHandler(Workflow workflow, ErrorHandlerFactory errorHandlerFactory, Processor processor) throws Exception;

    /**
     * Creates the default error handler.
     */
    ErrorHandlerFactory createDefaultErrorHandler();

    /**
     * Creates the expression from the model.
     */
    Expression createExpression(ZwangineContext zwangineContext, Object expressionDefinition);

    /**
     * Creates the predicate from the model.
     */
    Predicate createPredicate(ZwangineContext zwangineContext, Object expressionDefinition);

    /**
     * Creates the transformer from the model.
     */
    Transformer createTransformer(ZwangineContext zwangineContext, Object transformerDefinition);

    /**
     * Creates the validator from the model.
     */
    Validator createValidator(ZwangineContext zwangineContext, Object transformerDefinition);

}
