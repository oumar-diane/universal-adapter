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
package org.zenithblox.language.simple;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.language.simple.ast.*;
import org.zenithblox.language.simple.types.SimpleIllegalSyntaxException;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;
import org.zenithblox.language.simple.types.TokenType;
import org.zenithblox.support.LanguageHelper;
import org.zenithblox.support.builder.ExpressionBuilder;
import org.zenithblox.util.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A parser to parse simple language as a Zwangine {@link Expression}
 */
public class SimpleExpressionParser extends BaseSimpleParser {

    // use caches to avoid re-parsing the same expressions over and over again
    private final Map<String, Expression> cacheExpression;

    public SimpleExpressionParser(ZwangineContext zwangineContext, String expression, boolean allowEscape,
                                  Map<String, Expression> cacheExpression) {
        super(zwangineContext, expression, allowEscape);
        this.cacheExpression = cacheExpression;
    }

    public Expression parseExpression() {
        try {
            parseTokens();
            return doParseExpression();
        } catch (SimpleParserException e) {
            // catch parser exception and turn that into a syntax exceptions
            throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
        } catch (Exception e) {
            // include exception in rethrown exception
            throw new SimpleIllegalSyntaxException(expression, -1, e.getMessage(), e);
        }
    }

    public String parseCode() {
        try {
            parseTokens();
            return doParseCode();
        } catch (SimpleParserException e) {
            // catch parser exception and turn that into a syntax exceptions
            throw new SimpleIllegalSyntaxException(expression, e.getIndex(), e.getMessage(), e);
        } catch (Exception e) {
            // include exception in rethrown exception
            throw new SimpleIllegalSyntaxException(expression, -1, e.getMessage(), e);
        }
    }

    /**
     * First step parsing into a list of nodes.
     *
     * This is used as SPI for zwangine-csimple to do AST transformation and parse into java source code.
     */
    protected List<SimpleNode> parseTokens() {
        clear();

        // parse the expression using the following grammar
        nextToken();
        while (!token.getType().isEol()) {
            // an expression supports just template (eg text), functions, or unary operator
            templateText();
            functionText();
            unaryOperator();
            nextToken();
        }

        // now after parsing, we need a bit of work to do, to make it easier to turn the tokens
        // into an ast, and then from the ast, to Zwangine expression(s).
        // hence why there are a number of tasks going on below to accomplish this

        // turn the tokens into the ast model
        parseAndCreateAstModel();
        // compact and stack blocks (eg function start/end)
        prepareBlocks();
        // compact and stack unary operators
        prepareUnaryExpressions();

        return nodes;
    }

    /**
     * Second step parsing into an expression
     */
    protected Expression doParseExpression() {
        // create and return as a Zwangine expression
        List<Expression> expressions = createExpressions();
        if (expressions.isEmpty()) {
            // return an empty string as response as there was nothing to parse
            return ExpressionBuilder.constantExpression("");
        } else if (expressions.size() == 1) {
            return expressions.get(0);
        } else {
            // concat expressions as evaluating an expression is like a template language
            return ExpressionBuilder.concatExpression(expressions, expression);
        }
    }

    protected void parseAndCreateAstModel() {
        // we loop the tokens and create a sequence of ast nodes

        // counter to keep track of number of functions in the tokens
        AtomicInteger functions = new AtomicInteger();

        LiteralNode imageToken = null;
        for (SimpleToken token : tokens) {
            // break if eol
            if (token.getType().isEol()) {
                break;
            }

            // create a node from the token
            SimpleNode node = createNode(token, functions);
            if (node != null) {
                // a new token was created so the current image token need to be added first
                if (imageToken != null) {
                    nodes.add(imageToken);
                    imageToken = null;
                }
                // and then add the created node
                nodes.add(node);
                // continue to next
                continue;
            }

            // if no token was created, then it's a character/whitespace/escaped symbol
            // which we need to add together in the same image
            if (imageToken == null) {
                imageToken = new LiteralExpression(token);
            }
            imageToken.addText(token.getText());
        }

        // append any leftover image tokens (when we reached eol)
        if (imageToken != null) {
            nodes.add(imageToken);
        }
    }

    private SimpleNode createNode(SimpleToken token, AtomicInteger functions) {
        // expression only support functions and unary operators
        if (token.getType().isFunctionStart()) {
            // starting a new function
            functions.incrementAndGet();
            return new SimpleFunctionStart(token, cacheExpression);
        } else if (functions.get() > 0 && token.getType().isFunctionEnd()) {
            // there must be a start function already, to let this be a end function
            functions.decrementAndGet();
            return new SimpleFunctionEnd(token);
        } else if (token.getType().isUnary()) {
            // there must be a end function as previous, to let this be a unary function
            if (!nodes.isEmpty() && nodes.get(nodes.size() - 1) instanceof SimpleFunctionEnd) {
                return new UnaryExpression(token);
            }
        }

        // by returning null, we will let the parser determine what to do
        return null;
    }

    private List<Expression> createExpressions() {
        List<Expression> answer = new ArrayList<>();
        for (SimpleNode token : nodes) {
            Expression exp = token.createExpression(zwangineContext, expression);
            if (exp != null) {
                answer.add(exp);
            }
        }
        return answer;
    }

    /**
     * Second step parsing into code
     */
    protected String doParseCode() {
        StringBuilder sb = new StringBuilder(256);
        boolean firstIsLiteral = false;
        for (SimpleNode node : nodes) {
            String exp = node.createCode(zwangineContext, expression);
            if (exp != null) {
                if (sb.isEmpty() && node instanceof LiteralNode) {
                    firstIsLiteral = true;
                }
                if (!sb.isEmpty()) {
                    // okay we append together and this requires that the first node to be literal
                    if (!firstIsLiteral) {
                        // then insert an empty string + to force type into string so the compiler
                        // can compile with the + function
                        sb.insert(0, "\"\" + ");
                    }
                    sb.append(" + ");
                }
                parseLiteralNode(sb, node, exp);
            }
        }

        String code = sb.toString();
        code = code.replace(BaseSimpleParser.CODE_START, "");
        code = code.replace(BaseSimpleParser.CODE_END, "");
        return code;
    }

    static void parseLiteralNode(StringBuilder sb, SimpleNode node, String exp) {
        if (node instanceof LiteralNode) {
            exp = StringHelper.removeLeadingAndEndingQuotes(exp);
            sb.append("\"");
            // " should be escaped to \"
            exp = LanguageHelper.escapeQuotes(exp);
            // \n \t \r should be escaped
            exp = exp.replaceAll("\n", "\\\\n");
            exp = exp.replaceAll("\t", "\\\\t");
            exp = exp.replaceAll("\r", "\\\\r");
            if (exp.endsWith("\\") && !exp.endsWith("\\\\")) {
                // there is a single trailing slash which we need to escape
                exp += "\\";
            }
            sb.append(exp);
            sb.append("\"");
        } else {
            sb.append(exp);
        }
    }

    // --------------------------------------------------------------
    // grammar
    // --------------------------------------------------------------

    // the expression parser only understands
    // - template = literal texts with can contain embedded functions
    // - function = simple functions such as ${body} etc.
    // - unary operator = operator attached to the left-hand side node

    protected void templateText() {
        // for template, we accept anything but functions
        while (!token.getType().isFunctionStart() && !token.getType().isFunctionEnd() && !token.getType().isEol()) {
            nextToken();
        }
    }

    protected boolean functionText() {
        if (accept(TokenType.functionStart)) {
            nextToken();
            while (!token.getType().isFunctionEnd() && !token.getType().isEol()) {
                if (token.getType().isFunctionStart()) {
                    // embedded function
                    functionText();
                }
                // we need to loop until we find the ending function quote, an embedded function, or the eol
                nextToken();
            }
            // if its not an embedded function then we expect the end token
            if (!token.getType().isFunctionStart()) {
                expect(TokenType.functionEnd);
            }
            return true;
        }
        return false;
    }

    protected boolean unaryOperator() {
        if (accept(TokenType.unaryOperator)) {
            nextToken();
            // there should be a whitespace after the operator
            expect(TokenType.whiteSpace);
            return true;
        }
        return false;
    }
}
