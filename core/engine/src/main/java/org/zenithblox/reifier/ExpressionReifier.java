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

import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ExpressionNode;
import org.zenithblox.processor.FilterProcessor;

abstract class ExpressionReifier<T extends ExpressionNode> extends ProcessorReifier<T> {

    protected ExpressionReifier(Workflow workflow, T definition) {
        super(workflow, definition);
    }

    /**
     * Creates the {@link FilterProcessor} from the expression node.
     *
     * @return           the created {@link FilterProcessor}
     * @throws Exception is thrown if error creating the processor
     */
    protected FilterProcessor createFilterProcessor() throws Exception {
        Processor childProcessor = createOutputsProcessor();
        return new FilterProcessor(zwangineContext, createPredicate(), childProcessor);
    }

    /**
     * Creates the {@link Predicate} from the expression node.
     *
     * @return the created predicate
     */
    protected Predicate createPredicate() {
        return createPredicate(definition.getExpression());
    }

}
