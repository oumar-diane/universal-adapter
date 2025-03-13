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
package org.zenithblox.model.validator;

import org.zenithblox.Expression;
import org.zenithblox.model.ExpressionNodeHelper;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Validator;

/**
 * Represents a predicate {@link Validator} which leverages expression or predicates to perform content validation. A
 * {@link org.zenithblox.processor.validator.ProcessorValidator} will be created internally with a
 * {@link org.zenithblox.processor.validation.PredicateValidatingProcessor} which validates the message according to
 * specified expression/predicates. {@see ValidatorDefinition} {@see Validator}
 */
@Metadata(label = "validation")
public class PredicateValidatorDefinition extends ValidatorDefinition {

    private ExpressionDefinition expression;

    public PredicateValidatorDefinition() {
    }

    protected PredicateValidatorDefinition(PredicateValidatorDefinition source) {
        super(source);
        this.expression = source.expression != null ? source.expression.copyDefinition() : null;
    }

    @Override
    public ValidatorDefinition copyDefinition() {
        return new PredicateValidatorDefinition(this);
    }

    public ExpressionDefinition getExpression() {
        return expression;
    }

    public void setExpression(ExpressionDefinition expression) {
        // favour using the helper to set the expression as it can unwrap some
        // unwanted builders when using Java DSL
        this.expression = ExpressionNodeHelper.toExpressionDefinition((Expression) expression);
    }

}
