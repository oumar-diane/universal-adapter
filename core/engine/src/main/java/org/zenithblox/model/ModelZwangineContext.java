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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;

import java.util.List;

/**
 * Model level interface for the {@link ZwangineContext}
 */
public interface ModelZwangineContext extends ZwangineContext, Model {

    /**
     * Start all workflows from this model.
     */
    void startWorkflowDefinitions() throws Exception;

    /**
     * Start the given set of workflows.
     */
    void startWorkflowDefinitions(List<WorkflowDefinition> workflowDefinitions) throws Exception;

    /**
     * Creates an expression from the model.
     */
    Expression createExpression(ExpressionDefinition definition);

    /**
     * Creates a predicate from the model.
     */
    Predicate createPredicate(ExpressionDefinition definition);

    /**
     * Registers the workflow input validator
     */
    void registerValidator(ValidatorDefinition validator);

    /**
     * Registers the workflow transformer
     */
    void registerTransformer(TransformerDefinition transformer);
}
