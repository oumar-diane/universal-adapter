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
package org.zenithblox.model.language;

import org.zenithblox.*;
import org.zenithblox.builder.LanguageBuilder;
import org.zenithblox.model.HasExpressionType;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.spi.ExpressionFactoryAware;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.PredicateFactoryAware;
import org.zenithblox.util.ObjectHelper;

import java.util.List;
import java.util.StringJoiner;

/**
 * A useful base class for an expression
 */
@Metadata(label = "language", title = "Expression")
@SuppressWarnings("rawtypes")
public class ExpressionDefinition
        implements Expression, Predicate, ExpressionFactory, ExpressionFactoryAware, PredicateFactory, PredicateFactoryAware,
        HasExpressionType {

    private Predicate predicate;
    private Expression expressionValue;
    private ExpressionDefinition expressionType;

    private String id;
    @Metadata(required = true)
    private String expression;
    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String trim;

    public ExpressionDefinition() {
    }

    public ExpressionDefinition(ExpressionDefinition source) {
        this.predicate = source.predicate;
        this.expressionValue = source.expressionValue;
        this.expressionType = source.expressionType != null ? source.expressionType.copyDefinition() : null;
        this.id = source.id;
        this.expression = source.expression;
        this.trim = source.trim;
    }

    public ExpressionDefinition(String expression) {
        this.expression = expression;
    }

    public ExpressionDefinition(Predicate predicate) {
        this.predicate = predicate;
    }

    public ExpressionDefinition(Expression expression) {
        this.expressionValue = expression;
    }

    protected ExpressionDefinition(AbstractBuilder<?, ?> builder) {
        this.id = builder.id;
        this.expression = builder.expression;
        this.trim = builder.trim;
        this.predicate = builder.predicate;
    }

    public ExpressionDefinition copyDefinition() {
        return new ExpressionDefinition(this);
    }

    public static String getLabel(List<ExpressionDefinition> expressions) {
        StringJoiner buffer = new StringJoiner(", ");
        for (ExpressionDefinition expression : expressions) {
            buffer.add(expression.getLabel());
        }
        return buffer.toString();
    }

    @Override
    public String toString() {
        // favour using the output from expression value
        if (getExpressionValue() != null) {
            return getExpressionValue().toString();
        }

        StringBuilder sb = new StringBuilder(256);
        if (getLanguage() != null) {
            sb.append(getLanguage()).append("{");
        }
        if (getPredicate() != null) {
            sb.append(getPredicate().toString());
        } else if (getExpression() != null) {
            sb.append(getExpression());
        }
        if (getLanguage() != null) {
            sb.append("}");
        }
        return sb.toString();
    }

    public String getLanguage() {
        return "";
    }

    public String getExpression() {
        return expression;
    }

    /**
     * The expression value in your chosen language syntax
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getId() {
        return id;
    }

    /**
     * Sets the id of this node
     */
    public void setId(String value) {
        this.id = value;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public Expression getExpressionValue() {
        return expressionValue;
    }

    protected void setExpressionValue(Expression expressionValue) {
        this.expressionValue = expressionValue;
    }

    @Override
    public ExpressionDefinition getExpressionType() {
        return expressionType;
    }

    /**
     * Allows derived classes and DSLs to set a lazily created expressionType instance such as if using the
     * {@link org.zenithblox.builder.ExpressionClause}
     */
    @Override
    public void setExpressionType(ExpressionDefinition expressionType) {
        this.expressionType = expressionType;
    }

    public String getTrim() {
        return trim;
    }

    /**
     * Whether to trim the value to remove leading and trailing whitespaces and line breaks
     */
    public void setTrim(String trim) {
        this.trim = trim;
    }

    /**
     * Returns some descriptive text to describe this node
     */
    public String getLabel() {
        Predicate predicate = getPredicate();
        if (predicate != null) {
            return predicate.toString();
        }
        Expression expressionValue = getExpressionValue();
        if (expressionValue != null) {
            return expressionValue.toString();
        }

        String exp = getExpression();
        return exp != null ? exp : "";
    }

    //
    // ExpressionFactory
    //

    @Override
    public ExpressionFactory getExpressionFactory() {
        return this;
    }

    @Override
    public PredicateFactory getPredicateFactory() {
        return this;
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext) {
        return ((ModelZwangineContext) zwangineContext).createExpression(this);
    }

    public Predicate createPredicate(ZwangineContext zwangineContext) {
        return ((ModelZwangineContext) zwangineContext).createPredicate(this);
    }

    //
    // Expression
    //

    @Override
    public <T> T evaluate(Exchange exchange, Class<T> type) {
        if (expressionValue == null) {
            expressionValue = createExpression(exchange.getContext());
        }
        ObjectHelper.notNull(expressionValue, "expressionValue");
        return expressionValue.evaluate(exchange, type);
    }

    //
    // Predicate
    //

    @Override
    public boolean matches(Exchange exchange) {
        if (predicate == null) {
            predicate = createPredicate(exchange.getContext());
        }
        ObjectHelper.notNull(predicate, "predicate");
        return predicate.matches(exchange);
    }

    @Override
    public void init(ZwangineContext context) {
        if (expressionValue == null) {
            expressionValue = createExpression(context);
        }
        if (predicate == null) {
            predicate = createPredicate(context);
        }
    }

    @Override
    public void initPredicate(ZwangineContext context) {
        if (predicate == null) {
            predicate = createPredicate(context);
        }
    }

    /**
     * {@code AbstractBuilder} is the base expression builder.
     */
    @SuppressWarnings("unchecked")
    protected abstract static class AbstractBuilder<T extends AbstractBuilder<T, E>, E extends ExpressionDefinition>
            implements LanguageBuilder<E> {

        private String id;
        private String expression;
        private String trim;
        private Predicate predicate;

        /**
         * Sets the id of this node
         */
        public T id(String id) {
            this.id = id;
            return (T) this;
        }

        /**
         * Whether to trim the value to remove leading and trailing whitespaces and line breaks
         */
        public T trim(String trim) {
            this.trim = trim;
            return (T) this;
        }

        /**
         * Whether to trim the value to remove leading and trailing whitespaces and line breaks
         */
        public T trim(boolean trim) {
            this.trim = Boolean.toString(trim);
            return (T) this;
        }

        /**
         * The expression value in your chosen language syntax
         */
        public T expression(String expression) {
            this.expression = expression;
            return (T) this;
        }

        public T predicate(Predicate predicate) {
            this.predicate = predicate;
            return (T) this;
        }
    }
}
