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
package org.zenithblox.language.simple.ast;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.language.simple.BaseSimpleParser;
import org.zenithblox.language.simple.types.LogicalOperatorType;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.builder.PredicateBuilder;
import org.zenithblox.util.ObjectHelper;

/**
 * Represents a logical expression in the AST
 */
public class LogicalExpression extends BaseSimpleNode {

    private final LogicalOperatorType operator;
    private SimpleNode left;
    private SimpleNode right;

    public LogicalExpression(SimpleToken token) {
        super(token);
        operator = LogicalOperatorType.asOperator(token.getText());
    }

    @Override
    public String toString() {
        return left + " " + token.getText() + " " + right;
    }

    public boolean acceptLeftNode(SimpleNode lef) {
        this.left = lef;
        return true;
    }

    public boolean acceptRightNode(SimpleNode right) {
        this.right = right;
        return true;
    }

    public LogicalOperatorType getOperator() {
        return operator;
    }

    public SimpleNode getLeft() {
        return left;
    }

    public SimpleNode getRight() {
        return right;
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, String expression) {
        ObjectHelper.notNull(left, "left node", this);
        ObjectHelper.notNull(right, "right node", this);

        final Expression leftExp = left.createExpression(zwangineContext, expression);
        final Expression rightExp = right.createExpression(zwangineContext, expression);

        if (operator == LogicalOperatorType.AND) {
            return createAndExpression(leftExp, rightExp);
        } else if (operator == LogicalOperatorType.OR) {
            return createOrExpression(leftExp, rightExp);
        }

        throw new SimpleParserException("Unknown logical operator " + operator, token.getIndex());
    }

    private Expression createAndExpression(final Expression leftExp, final Expression rightExp) {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Predicate predicate = ExpressionToPredicateAdapter.toPredicate(leftExp);
                predicate = PredicateBuilder.and(predicate, ExpressionToPredicateAdapter.toPredicate(rightExp));

                boolean answer = predicate.matches(exchange);
                return exchange.getContext().getTypeConverter().convertTo(type, answer);
            }

            @Override
            public String toString() {
                return left + " " + token.getText() + " " + right;
            }
        };
    }

    private Expression createOrExpression(final Expression leftExp, final Expression rightExp) {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Predicate predicate = ExpressionToPredicateAdapter.toPredicate(leftExp);
                predicate = PredicateBuilder.or(predicate, ExpressionToPredicateAdapter.toPredicate(rightExp));

                boolean answer = predicate.matches(exchange);
                return exchange.getContext().getTypeConverter().convertTo(type, answer);
            }

            @Override
            public String toString() {
                return left + " " + token.getText() + " " + right;
            }
        };
    }

    @Override
    public String createCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        return BaseSimpleParser.CODE_START + doCreateCode(zwangineContext, expression) + BaseSimpleParser.CODE_END;
    }

    private String doCreateCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        ObjectHelper.notNull(left, "left node", this);
        ObjectHelper.notNull(right, "right node", this);

        final String leftExp = left.createCode(zwangineContext, expression);
        final String rightExp = right.createCode(zwangineContext, expression);

        if (operator == LogicalOperatorType.AND) {
            return leftExp + " && " + rightExp;
        } else if (operator == LogicalOperatorType.OR) {
            return leftExp + " || " + rightExp;
        }

        throw new SimpleParserException("Unknown logical operator " + operator, token.getIndex());
    }
}
