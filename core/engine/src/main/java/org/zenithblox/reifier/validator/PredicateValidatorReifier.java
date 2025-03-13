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
package org.zenithblox.reifier.validator;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Predicate;
import org.zenithblox.model.validator.PredicateValidatorDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.processor.validator.ProcessorValidator;
import org.zenithblox.spi.Validator;
import org.zenithblox.support.processor.PredicateValidatingProcessor;

public class PredicateValidatorReifier extends ValidatorReifier<PredicateValidatorDefinition> {

    public PredicateValidatorReifier(ZwangineContext zwangineContext, ValidatorDefinition definition) {
        super(zwangineContext, (PredicateValidatorDefinition) definition);
    }

    @Override
    protected Validator doCreateValidator() {
        Predicate pred = createPredicate(definition.getExpression());
        PredicateValidatingProcessor processor = new PredicateValidatingProcessor(pred);
        return new ProcessorValidator(zwangineContext).setProcessor(processor).setType(definition.getType());
    }

}
