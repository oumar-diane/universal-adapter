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

import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.PredicateExceptionFactory;

/**
 * Validates a message based on an expression
 */
@Metadata(label = "eip,transformation")
@AsPredicate
public class ValidateDefinition extends ExpressionNode {

    private PredicateExceptionFactory factory;

    @Metadata(label = "advanced", javaType = "org.zenithblox.spi.PredicateExceptionFactory")
    private String predicateExceptionFactory;

    public ValidateDefinition() {
    }

    protected ValidateDefinition(ValidateDefinition source) {
        super(source);
        this.factory = source.factory;
        this.predicateExceptionFactory = source.predicateExceptionFactory;
    }

    public ValidateDefinition(Expression expression) {
        super(expression);
    }

    public ValidateDefinition(Predicate predicate) {
        super(predicate);
    }

    @Override
    public ValidateDefinition copyDefinition() {
        return new ValidateDefinition(this);
    }

    @Override
    public String toString() {
        return "Validate[" + getExpression() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "validate";
    }

    @Override
    public String getLabel() {
        return "validate[" + getExpression() + "]";
    }

    /**
     * Expression to use for validation as a predicate. The expression should return either <tt>true</tt> or
     * <tt>false</tt>. If returning <tt>false</tt> the message is invalid and an exception is thrown.
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    public PredicateExceptionFactory getFactory() {
        return factory;
    }

    public String getPredicateExceptionFactory() {
        return predicateExceptionFactory;
    }

    /**
     * The bean id of custom PredicateExceptionFactory to use for creating the exception when the validation fails.
     *
     * By default, Zwangine will throw PredicateValidationException. By using a custom factory you can control which
     * exception to throw instead.
     */
    public void setPredicateExceptionFactory(String predicateExceptionFactory) {
        this.predicateExceptionFactory = predicateExceptionFactory;
    }

    /**
     * The custom PredicateExceptionFactory to use for creating the exception when the validation fails.
     *
     * By default, Zwangine will throw PredicateValidationException. By using a custom factory you can control which
     * exception to throw instead.
     */
    public ValidateDefinition predicateExceptionFactory(PredicateExceptionFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * The bean id of the custom PredicateExceptionFactory to use for creating the exception when the validation fails.
     *
     * By default, Zwangine will throw PredicateValidationException. By using a custom factory you can control which
     * exception to throw instead.
     */
    public ValidateDefinition predicateExceptionFactory(String ref) {
        this.predicateExceptionFactory = ref;
        return this;
    }

}
