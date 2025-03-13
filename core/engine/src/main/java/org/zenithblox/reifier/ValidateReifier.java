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
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ValidateDefinition;
import org.zenithblox.spi.PredicateExceptionFactory;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.processor.PredicateValidatingProcessor;

public class ValidateReifier extends ExpressionReifier<ValidateDefinition> {

    public ValidateReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (ValidateDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Predicate pred = createPredicate(definition.getExpression());
        PredicateValidatingProcessor answer = new PredicateValidatingProcessor(pred);

        PredicateExceptionFactory factory = definition.getFactory();
        if (factory == null && definition.getPredicateExceptionFactory() != null) {
            factory = lookupByNameAndType(definition.getPredicateExceptionFactory(), PredicateExceptionFactory.class);
        } else if (factory == null) {
            factory = ZwangineContextHelper.findSingleByType(zwangineContext, PredicateExceptionFactory.class);
        }
        answer.setPredicateExceptionFactory(factory);
        return answer;
    }

}
