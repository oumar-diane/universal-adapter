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
import org.zenithblox.language.simple.types.SimpleIllegalSyntaxException;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;
import org.zenithblox.util.StringHelper;

import java.util.Map;

/**
 * Starts a function
 */
public class SimpleFunctionStart extends BaseSimpleNode implements BlockStart {

    // use caches to avoid re-parsing the same expressions over and over again
    private final Map<String, Expression> cacheExpression;
    private final CompositeNodes block;

    public SimpleFunctionStart(SimpleToken token, Map<String, Expression> cacheExpression) {
        super(token);
        this.block = new CompositeNodes(token);
        this.cacheExpression = cacheExpression;
    }

    public CompositeNodes getBlock() {
        return block;
    }

    public boolean lazyEval(SimpleNode child) {
        String text = child.toString();
        // don't lazy evaluate nested type references as they are static
        return !text.startsWith("${type:");
    }

    @Override
    public String toString() {
        // output a nice toString, so it makes debugging easier, so we can see the entire block
        return "${" + block + "}";
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, String expression) {
        // a function can either be a simple literal function, or contain nested functions
        if (block.getChildren().size() == 1 && block.getChildren().get(0) instanceof LiteralNode) {
            return doCreateLiteralExpression(zwangineContext, expression);
        } else {
            return doCreateCompositeExpression(zwangineContext, expression);
        }
    }

    private Expression doCreateLiteralExpression(ZwangineContext zwangineContext, String expression) {
        SimpleFunctionExpression function = new SimpleFunctionExpression(this.getToken(), cacheExpression);
        LiteralNode literal = (LiteralNode) block.getChildren().get(0);
        function.addText(literal.getText());
        return function.createExpression(zwangineContext, expression);
    }

    private Expression doCreateCompositeExpression(ZwangineContext zwangineContext, String expression) {
        final SimpleToken token = getToken();
        return new Expression() {
            @Override
            public <T> T evaluate(Exchange exchange, Class<T> type) {
                StringBuilder sb = new StringBuilder(256);
                boolean quoteEmbeddedFunctions = false;

                // we need to concat the block so we have the expression
                for (SimpleNode child : block.getChildren()) {
                    // whether a nested function should be lazy evaluated or not
                    boolean lazy = true;
                    if (child instanceof SimpleFunctionStart simpleFunctionStart) {
                        lazy = simpleFunctionStart.lazyEval(child);
                    }
                    if (child instanceof LiteralNode literal) {
                        String text = literal.getText();
                        sb.append(text);
                        quoteEmbeddedFunctions |= literal.quoteEmbeddedNodes();
                        // if its quoted literal then embed that as text
                    } else if (!lazy || child instanceof SingleQuoteStart || child instanceof DoubleQuoteStart) {
                        try {
                            // pass in null when we evaluate the nested expressions
                            Expression nested = child.createExpression(zwangineContext, null);
                            String text = nested.evaluate(exchange, String.class);
                            if (text != null) {
                                if (quoteEmbeddedFunctions && !StringHelper.isQuoted(text)) {
                                    sb.append("'").append(text).append("'");
                                } else {
                                    sb.append(text);
                                }
                            }
                        } catch (SimpleParserException e) {
                            // must rethrow parser exception as illegal syntax with details about the location
                            throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
                        }
                        // if its an inlined function then embed that function as text so it can be evaluated lazy
                    } else if (child instanceof SimpleFunctionStart) {
                        sb.append(child);
                    }
                }

                // we have now concat the block as a String which contains the function expression
                // which we then need to evaluate as a function
                String exp = sb.toString();
                SimpleFunctionExpression function = new SimpleFunctionExpression(token, cacheExpression);
                function.addText(exp);
                try {
                    return function.createExpression(zwangineContext, exp).evaluate(exchange, type);
                } catch (SimpleParserException e) {
                    // must rethrow parser exception as illegal syntax with details about the location
                    throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
                }
            }

            @Override
            public String toString() {
                return expression;
            }
        };
    }

    @Override
    public boolean acceptAndAddNode(SimpleNode node) {
        // only accept literals, quotes or embedded functions
        if (node instanceof LiteralNode || node instanceof SimpleFunctionStart
                || node instanceof SingleQuoteStart || node instanceof DoubleQuoteStart) {
            block.addChild(node);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String createCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        String answer;
        // a function can either be a simple literal function or contain nested functions
        if (block.getChildren().size() == 1 && block.getChildren().get(0) instanceof LiteralNode) {
            answer = doCreateLiteralCode(zwangineContext, expression);
        } else {
            answer = doCreateCompositeCode(zwangineContext, expression);
        }
        return answer;
    }

    private String doCreateLiteralCode(ZwangineContext zwangineContext, String expression) {
        SimpleFunctionExpression function = new SimpleFunctionExpression(this.getToken(), cacheExpression);
        LiteralNode literal = (LiteralNode) block.getChildren().get(0);
        function.addText(literal.getText());
        return function.createCode(zwangineContext, expression);
    }

    private String doCreateCompositeCode(ZwangineContext zwangineContext, String expression) {
        StringBuilder sb = new StringBuilder(256);
        boolean quoteEmbeddedFunctions = false;

        // we need to concat the block, so we have the expression
        for (SimpleNode child : block.getChildren()) {
            if (child instanceof LiteralNode literal) {
                String text = literal.getText();
                sb.append(text);
                quoteEmbeddedFunctions |= literal.quoteEmbeddedNodes();
                // if its quoted literal then embed that as text
            } else if (child instanceof SingleQuoteStart || child instanceof DoubleQuoteStart) {
                try {
                    // pass in null when we evaluate the nested expressions
                    String text = child.createCode(zwangineContext, null);
                    if (text != null) {
                        if (quoteEmbeddedFunctions && !StringHelper.isQuoted(text)) {
                            sb.append("'").append(text).append("'");
                        } else {
                            sb.append(text);
                        }
                    }
                } catch (SimpleParserException e) {
                    // must rethrow parser exception as illegal syntax with details about the location
                    throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
                }
            } else if (child instanceof SimpleFunctionStart) {
                // inlined function
                String inlined = child.createCode(zwangineContext, expression);
                sb.append(inlined);
            }
        }

        // we have now concat the block as a String which contains inlined functions parsed
        // so now we should reparse as a single function
        String exp = sb.toString();
        SimpleFunctionExpression function = new SimpleFunctionExpression(token, cacheExpression);
        function.addText(exp);
        try {
            return function.createCode(zwangineContext, exp);
        } catch (SimpleParserException e) {
            // must rethrow parser exception as illegal syntax with details about the location
            throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
        }
    }

}
