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

import org.zenithblox.*;
import org.zenithblox.language.simple.BaseSimpleParser;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;
import org.zenithblox.language.simple.types.UnaryOperatorType;
import org.zenithblox.util.ObjectHelper;

/**
 * Represents an unary expression in the AST
 */
public class UnaryExpression extends BaseSimpleNode {

    private final UnaryOperatorType operator;
    private SimpleNode left;

    public UnaryExpression(SimpleToken token) {
        super(token);
        operator = UnaryOperatorType.asOperator(token.getText());
    }

    @Override
    public String toString() {
        if (left != null) {
            return left + token.getText();
        } else {
            return token.getText();
        }
    }

    /**
     * Accepts the left node to this operator
     *
     * @param left the left node to accept
     */
    public void acceptLeft(SimpleNode left) {
        this.left = left;
    }

    public UnaryOperatorType getOperator() {
        return operator;
    }

    public SimpleNode getLeft() {
        return left;
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, String expression) {
        ObjectHelper.notNull(left, "left node", this);

        final Expression leftExp = left.createExpression(zwangineContext, expression);

        if (operator == UnaryOperatorType.INC) {
            return createIncExpression(zwangineContext, leftExp);
        } else if (operator == UnaryOperatorType.DEC) {
            return createDecExpression(zwangineContext, leftExp);
        }

        throw new SimpleParserException("Unknown unary operator " + operator, token.getIndex());
    }

    private Expression createIncExpression(ZwangineContext zwangineContext, final Expression leftExp) {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Number num = leftExp.evaluate(exchange, Number.class);
                if (num != null) {
                    long val = num.longValue();
                    val++;

                    // convert value back to same type as input as we want to preserve type
                    Object left = leftExp.evaluate(exchange, Object.class);
                    try {
                        left = zwangineContext.getTypeConverter().mandatoryConvertTo(left.getClass(), exchange, val);
                    } catch (NoTypeConversionAvailableException e) {
                        throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                    }

                    // and return the result
                    return zwangineContext.getTypeConverter().convertTo(type, left);
                }
                // cannot convert the expression as a number
                Exception cause = new ZwangineExchangeException("Cannot evaluate " + leftExp + " as a number", exchange);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(cause);
            }

            @Override
            public String toString() {
                return left + operator.toString();
            }
        };
    }

    private Expression createDecExpression(ZwangineContext zwangineContext, final Expression leftExp) {
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                Number num = leftExp.evaluate(exchange, Number.class);
                if (num != null) {
                    long val = num.longValue();
                    val--;

                    // convert value back to same type as input as we want to preserve type
                    Object left = leftExp.evaluate(exchange, Object.class);
                    try {
                        left = zwangineContext.getTypeConverter().mandatoryConvertTo(left.getClass(), exchange, val);
                    } catch (NoTypeConversionAvailableException e) {
                        throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                    }

                    // and return the result
                    return zwangineContext.getTypeConverter().convertTo(type, left);
                }
                // cannot convert the expression as a number
                Exception cause = new ZwangineExchangeException("Cannot evaluate " + leftExp + " as a number", exchange);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(cause);
            }

            @Override
            public String toString() {
                return left + operator.toString();
            }
        };
    }

    @Override
    public String createCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        return BaseSimpleParser.CODE_START + doCreateCode(zwangineContext, expression) + BaseSimpleParser.CODE_END;
    }

    private String doCreateCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        ObjectHelper.notNull(left, "left node", this);

        final String number = left.createCode(zwangineContext, expression);

        if (operator == UnaryOperatorType.INC) {
            return "increment(exchange, " + number + ")";
        } else if (operator == UnaryOperatorType.DEC) {
            return "decrement(exchange, " + number + ")";
        }

        throw new SimpleParserException("Unknown unary operator " + operator, token.getIndex());
    }
}
