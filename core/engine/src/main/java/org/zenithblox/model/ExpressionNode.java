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
import org.zenithblox.ExpressionFactory;
import org.zenithblox.Predicate;
import org.zenithblox.builder.ExpressionClause;
import org.zenithblox.model.language.ExpressionDefinition;

import java.util.Collections;
import java.util.List;

/**
 * A base {@link ExpressionNode} which does <b>not</b> support any outputs.
 * <p/>
 * This node is to be extended by definitions which need to support an expression but the definition should not contain
 * any outputs, such as {@link org.zenithblox.model.TransformDefinition}.
 */
public abstract class ExpressionNode extends ProcessorDefinition<ExpressionNode>
        implements HasExpressionType {

    private ExpressionDefinition expression;

    public ExpressionNode() {
    }

    public ExpressionNode(ExpressionDefinition expression) {
        setExpression(expression);
    }

    public ExpressionNode(Expression expression) {
        setExpression(expression);
    }

    public ExpressionNode(Predicate predicate) {
        setPredicate(predicate);
    }

    protected ExpressionNode(ExpressionNode source) {
        super(source);
        this.expression = source.expression != null ? source.expression.copyDefinition() : null;
    }

    public ExpressionDefinition getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        if (expression != null) {
            setExpression(ExpressionNodeHelper.toExpressionDefinition(expression));
        }
    }

    private void setPredicate(Predicate predicate) {
        if (predicate != null) {
            setExpression(ExpressionNodeHelper.toExpressionDefinition(predicate));
        }
    }

    public void setExpression(ExpressionDefinition expression) {
        // favour using the helper to set the expression as it can unwrap some
        // unwanted builders when using Java DSL
        this.expression = expression;
    }

    @Override
    public ExpressionDefinition getExpressionType() {
        return getExpression();
    }

    @Override
    public void setExpressionType(ExpressionDefinition expressionType) {
        setExpression(expressionType);
    }

    @Override
    public String getLabel() {
        if (getExpression() == null) {
            return "";
        }
        return getExpression().getLabel();
    }

    @Override
    public void configureChild(ProcessorDefinition<?> output) {
        // reuse the logic from pre create processor
        preCreateProcessor();
    }

    @Override
    public void preCreateProcessor() {
        Expression exp = getExpression();
        if (getExpression() != null && getExpression().getExpressionValue() != null) {
            exp = getExpression().getExpressionValue();
        }

        if (exp instanceof ExpressionClause clause) {
            if (clause.getExpressionType() != null) {
                // if using the Java DSL then the expression may have been set
                // using the
                // ExpressionClause which is a fancy builder to define
                // expressions and predicates
                // using fluent builders in the DSL. However we need afterwards
                // a callback to
                // reset the expression to the expression type the
                // ExpressionClause did build for us
                ExpressionFactory model = clause.getExpressionType();
                if (model instanceof ExpressionDefinition expressionDefinition) {
                    setExpression(expressionDefinition);
                }
            }
        }

        if (getExpression() != null && getExpression().getExpression() == null) {
            // use toString from predicate or expression so we have some
            // information to show in the workflow model
            if (getExpression().getPredicate() != null) {
                getExpression().setExpression(getExpression().getPredicate().toString());
            } else if (getExpression().getExpressionValue() != null) {
                getExpression().setExpression(getExpression().getExpressionValue().toString());
            }
        }
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return Collections.emptyList();
    }

    @Override
    public ExpressionNode id(String id) {
        if (!(this instanceof OutputNode)) {
            // let parent handle assigning the id, as we do not support outputs
            getParent().id(id);
            return this;
        } else {
            return super.id(id);
        }
    }

}
