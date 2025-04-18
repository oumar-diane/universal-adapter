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
import org.zenithblox.Predicate;
import org.zenithblox.language.simple.ast.*;
import org.zenithblox.language.simple.types.*;
import org.zenithblox.support.ExpressionToPredicateAdapter;
import org.zenithblox.support.builder.PredicateBuilder;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.zenithblox.support.ObjectHelper.isFloatingNumber;
import static org.zenithblox.support.ObjectHelper.isNumber;

/**
 * A parser to parse simple language as a Zwangine {@link Predicate}
 */
public class SimplePredicateParser extends BaseSimpleParser {

    // use caches to avoid re-parsing the same expressions over and over again
    private final Map<String, Expression> cacheExpression;

    public SimplePredicateParser(ZwangineContext zwangineContext, String expression, boolean allowEscape,
                                 Map<String, Expression> cacheExpression) {
        super(zwangineContext, expression, allowEscape);
        this.cacheExpression = cacheExpression;
    }

    public Predicate parsePredicate() {
        try {
            parseTokens();
            return doParsePredicate();
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
    public List<SimpleNode> parseTokens() {
        clear();
        // parse using the following grammar
        nextToken();
        while (!token.getType().isEol()) {
            // predicate supports quotes, functions, operators and whitespaces
            if (!singleQuotedLiteralWithFunctionsText()
                    && !doubleQuotedLiteralWithFunctionsText()
                    && !functionText()
                    && !unaryOperator()
                    && !binaryOperator()
                    && !logicalOperator()
                    && !isBooleanValue()
                    && !token.getType().isWhitespace()
                    && !token.getType().isEol()) {
                // okay the symbol was not one of the above, so its not supported
                // use the previous index as that is where the problem is
                throw new SimpleParserException("Unexpected token " + token, previousIndex);
            }
            // take the next token
            nextToken();
        }

        // now after parsing we need a bit of work to do, to make it easier to turn the tokens
        // into and ast, and then from the ast, to Zwangine predicate(s).
        // hence why there is a number of tasks going on below to accomplish this

        // remove any ignorable white space tokens
        removeIgnorableWhiteSpaceTokens();
        // turn the tokens into the ast model
        parseTokensAndCreateNodes();
        // compact and stack blocks (eg function start/end, quotes start/end, etc.)
        prepareBlocks();
        // compact and stack unary expressions
        prepareUnaryExpressions();
        // compact and stack binary expressions
        prepareBinaryExpressions();
        // compact and stack logical expressions
        prepareLogicalExpressions();

        return nodes;
    }

    /**
     * Second step parsing into a predicate
     */
    protected Predicate doParsePredicate() {
        // create and return as a Zwangine predicate
        List<Predicate> predicates = createPredicates();
        if (predicates.isEmpty()) {
            // return a false predicate as response as there was nothing to parse
            return PredicateBuilder.constant(false);
        } else if (predicates.size() == 1) {
            return predicates.get(0);
        } else {
            return PredicateBuilder.and(predicates);
        }
    }

    /**
     * Second step parsing into code
     */
    protected String doParseCode() {
        StringBuilder sb = new StringBuilder(256);
        for (SimpleNode node : nodes) {
            String exp = node.createCode(zwangineContext, expression);
            SimpleExpressionParser.parseLiteralNode(sb, node, exp);
        }
        String code = sb.toString();
        code = code.replace(BaseSimpleParser.CODE_START, "");
        code = code.replace(BaseSimpleParser.CODE_END, "");
        return code;
    }

    /**
     * Parses the tokens and crates the AST nodes.
     * <p/>
     * After the initial parsing of the input (input -> tokens) then we parse again (tokens -> ast).
     * <p/>
     * In this parsing the balance of the blocks is checked, so that each block has a matching start and end token. For
     * example a single quote block, or a function block etc.
     */
    protected void parseTokensAndCreateNodes() {
        // we loop the tokens and create a sequence of ast nodes

        // we need to keep a bit of state for keeping track of single and double quotes
        // which need to be balanced and have matching start/end pairs
        SimpleNode lastSingle = null;
        SimpleNode lastDouble = null;
        SimpleNode lastFunction = null;
        AtomicBoolean startSingle = new AtomicBoolean();
        AtomicBoolean startDouble = new AtomicBoolean();
        AtomicBoolean startFunction = new AtomicBoolean();

        LiteralNode imageToken = null;
        for (SimpleToken token : tokens) {
            // break if eol
            if (token.getType().isEol()) {
                break;
            }

            // create a node from the token
            SimpleNode node = createNode(token, startSingle, startDouble, startFunction);
            if (node != null) {
                // keep state of last single/double
                if (node instanceof SingleQuoteStart) {
                    lastSingle = node;
                } else if (node instanceof DoubleQuoteStart) {
                    lastDouble = node;
                } else if (node instanceof SimpleFunctionStart) {
                    lastFunction = node;
                }

                // a new token was created so the current image token need to be added first
                if (imageToken != null) {
                    addImageToken(imageToken);
                    imageToken = null;
                }
                // and then add the created node
                nodes.add(node);
                // continue to next
                continue;
            }

            // if no token was created then its a character/whitespace/escaped symbol
            // which we need to add together in the same image
            if (imageToken == null) {
                imageToken = new LiteralExpression(token);
            }
            imageToken.addText(token.getText());
        }

        // append any leftover image tokens (when we reached eol)
        if (imageToken != null) {
            addImageToken(imageToken);
        }

        // validate the single, double quote pairs and functions is in balance
        if (startSingle.get()) {
            int index = evalIndex(lastSingle);
            throw new SimpleParserException("single quote has no ending quote", index);
        }
        if (startDouble.get()) {
            int index = evalIndex(lastDouble);
            throw new SimpleParserException("double quote has no ending quote", index);
        }
        if (startFunction.get()) {
            // we have a start function, but no ending function
            int index = evalIndex(lastFunction);
            throw new SimpleParserException("function has no ending token", index);
        }
    }

    private static int evalIndex(SimpleNode node) {
        return node != null ? node.getToken().getIndex() : 0;
    }

    private void addImageToken(LiteralNode imageToken) {
        // this can be many things but lets check if this is numeric based, then we can optimize this
        String text = imageToken.getText();

        // is this image from within a quoted block (single or double quoted)
        boolean quoted = false;
        if (!nodes.isEmpty()) {
            SimpleNode last = nodes.get(nodes.size() - 1);
            quoted = last instanceof SingleQuoteStart || last instanceof DoubleQuoteStart;
        }

        boolean numeric = false;
        if (!quoted) {
            // if the text is not in a quoted block (literal text), then lets see if
            // its numeric then we can optimize this
            numeric = isNumber(text) || isFloatingNumber(text);
        }
        if (numeric) {
            nodes.add(new NumericExpression(imageToken.getToken(), text));
        } else {
            nodes.add(imageToken);
        }
    }

    /**
     * Creates a node from the given token
     *
     * @param  token         the token
     * @param  startSingle   state of single quoted blocks
     * @param  startDouble   state of double quoted blocks
     * @param  startFunction state of function blocks
     * @return               the created node, or <tt>null</tt> to let a default node be created instead.
     */
    private SimpleNode createNode(
            SimpleToken token, AtomicBoolean startSingle, AtomicBoolean startDouble,
            AtomicBoolean startFunction) {
        if (token.getType().isFunctionStart()) {
            startFunction.set(true);
            return new SimpleFunctionStart(token, cacheExpression);
        } else if (token.getType().isFunctionEnd()) {
            startFunction.set(false);
            return new SimpleFunctionEnd(token);
        }

        // if we are inside a function, then we do not support any other kind of tokens
        // as we want all the tokens to be literal instead
        if (startFunction.get()) {
            return null;
        }

        // okay so far we also want to support quotes
        if (token.getType().isSingleQuote()) {
            return createSingleQuoted(token, startSingle);
        } else if (token.getType().isDoubleQuote()) {
            return createDoubleQuoted(token, startDouble);
        }

        // if we are inside a quote, then we do not support any further kind of tokens
        // as we want to only support embedded functions and all other kinds to be literal tokens
        if (startSingle.get() || startDouble.get()) {
            return null;
        }

        // okay we are not inside a function or quote, so we want to support operators
        // and the special null/boolean value as well
        if (token.getType().isUnary()) {
            return new UnaryExpression(token);
        } else if (token.getType().isBinary()) {
            return new BinaryExpression(token);
        } else if (token.getType().isLogical()) {
            return new LogicalExpression(token);
        } else if (token.getType().isNullValue()) {
            return new NullExpression(token);
        } else if (token.getType().isBooleanValue()) {
            return new BooleanExpression(token);
        }

        // by returning null, we will let the parser determine what to do
        return null;
    }

    private static SimpleNode createDoubleQuoted(SimpleToken token, AtomicBoolean startDouble) {
        SimpleNode answer;
        boolean start = startDouble.get();
        if (!start) {
            answer = new DoubleQuoteStart(token);
        } else {
            answer = new DoubleQuoteEnd(token);
        }
        // flip state on start/end flag
        startDouble.set(!start);
        return answer;
    }

    private static SimpleNode createSingleQuoted(SimpleToken token, AtomicBoolean startSingle) {
        SimpleNode answer;
        boolean start = startSingle.get();
        if (!start) {
            answer = new SingleQuoteStart(token);
        } else {
            answer = new SingleQuoteEnd(token);
        }
        // flip state on start/end flag
        startSingle.set(!start);
        return answer;
    }

    /**
     * Removes any ignorable whitespace tokens.
     * <p/>
     * During the initial parsing (input -> tokens), then there may be excessive whitespace tokens, which can safely be
     * removed, which makes the succeeding parsing easier.
     */
    private void removeIgnorableWhiteSpaceTokens() {
        // white space can be removed if its not part of a quoted text or within function(s)
        boolean quote = false;
        int functionCount = 0;

        Iterator<SimpleToken> it = tokens.iterator();
        while (it.hasNext()) {
            SimpleToken token = it.next();
            if (token.getType().isSingleQuote()) {
                quote = !quote;
            } else if (!quote) {
                if (token.getType().isFunctionStart()) {
                    functionCount++;
                } else if (token.getType().isFunctionEnd()) {
                    functionCount--;
                } else if (token.getType().isWhitespace() && functionCount == 0) {
                    it.remove();
                }
            }
        }
    }

    /**
     * Prepares binary expressions.
     * <p/>
     * This process prepares the binary expressions in the AST. This is done by linking the binary operator with both
     * the right and left hand side nodes, to have the AST graph updated and prepared properly.
     * <p/>
     * So when the AST node is later used to create the {@link Predicate}s to be used by Zwangine then the AST graph has a
     * linked and prepared graph of nodes which represent the input expression.
     */
    private void prepareBinaryExpressions() {
        Deque<SimpleNode> stack = new ArrayDeque<>();

        SimpleNode left = null;
        for (int i = 0; i < nodes.size(); i++) {
            if (left == null) {
                left = i > 0 ? nodes.get(i - 1) : null;
            }
            SimpleNode token = nodes.get(i);
            SimpleNode right = i < nodes.size() - 1 ? nodes.get(i + 1) : null;

            if (token instanceof BinaryExpression binary) {
                // remember the binary operator
                String operator = binary.getOperator().toString();

                if (left == null) {
                    throw new SimpleParserException(
                            "Binary operator " + operator + " has no left hand side token", token.getToken().getIndex());
                }
                if (!binary.acceptLeftNode(left)) {
                    throw new SimpleParserException(
                            "Binary operator " + operator + " does not support left hand side token " + left.getToken(),
                            token.getToken().getIndex());
                }
                if (right == null) {
                    throw new SimpleParserException(
                            "Binary operator " + operator + " has no right hand side token", token.getToken().getIndex());
                }
                if (!binary.acceptRightNode(right)) {
                    throw new SimpleParserException(
                            "Binary operator " + operator + " does not support right hand side token " + right.getToken(),
                            token.getToken().getIndex());
                }

                // pop previous as we need to replace it with this binary operator
                stack.pop();
                stack.push(token);
                // advantage after the right hand side
                i++;
                // this token is now the left for the next loop
                left = token;
            } else {
                // clear left
                left = null;
                stack.push(token);
            }
        }

        nodes.clear();
        nodes.addAll(stack);
        // must reverse as it was added from a stack that is reverse
        Collections.reverse(nodes);
    }

    /**
     * Prepares logical expressions.
     * <p/>
     * This process prepares the logical expressions in the AST. This is done by linking the logical operator with both
     * the right and left hand side nodes, to have the AST graph updated and prepared properly.
     * <p/>
     * So when the AST node is later used to create the {@link Predicate}s to be used by Zwangine then the AST graph has a
     * linked and prepared graph of nodes which represent the input expression.
     */
    private void prepareLogicalExpressions() {
        Deque<SimpleNode> stack = new ArrayDeque<>();

        SimpleNode left = null;
        for (int i = 0; i < nodes.size(); i++) {
            if (left == null) {
                left = i > 0 ? nodes.get(i - 1) : null;
            }
            SimpleNode token = nodes.get(i);
            SimpleNode right = i < nodes.size() - 1 ? nodes.get(i + 1) : null;

            if (token instanceof LogicalExpression logical) {
                // remember the logical operator
                String operator = logical.getOperator().toString();

                if (left == null) {
                    throw new SimpleParserException(
                            "Logical operator " + operator + " has no left hand side token", token.getToken().getIndex());
                }
                if (!logical.acceptLeftNode(left)) {
                    throw new SimpleParserException(
                            "Logical operator " + operator + " does not support left hand side token " + left.getToken(),
                            token.getToken().getIndex());
                }
                if (right == null) {
                    throw new SimpleParserException(
                            "Logical operator " + operator + " has no right hand side token", token.getToken().getIndex());
                }
                if (!logical.acceptRightNode(right)) {
                    throw new SimpleParserException(
                            "Logical operator " + operator + " does not support right hand side token " + left.getToken(),
                            token.getToken().getIndex());
                }

                // pop previous as we need to replace it with this binary operator
                stack.pop();
                stack.push(token);
                // advantage after the right hand side
                i++;
                // this token is now the left for the next loop
                left = token;
            } else {
                // clear left
                left = null;
                stack.push(token);
            }
        }

        nodes.clear();
        nodes.addAll(stack);
        // must reverse as it was added from a stack that is reverse
        Collections.reverse(nodes);
    }

    /**
     * Creates the {@link Predicate}s from the AST nodes.
     *
     * @return the created {@link Predicate}s, is never <tt>null</tt>.
     */
    private List<Predicate> createPredicates() {
        List<Predicate> answer = new ArrayList<>();
        for (SimpleNode node : nodes) {
            Expression exp = node.createExpression(zwangineContext, expression);
            if (exp != null) {
                Predicate predicate = ExpressionToPredicateAdapter.toPredicate(exp);
                answer.add(predicate);
            }
        }
        return answer;
    }

    // --------------------------------------------------------------
    // grammar
    // --------------------------------------------------------------

    // the predicate parser understands a lot more than the expression parser
    // - boolean value = either true or false value (literal)
    // - single quoted = block of nodes enclosed by single quotes
    // - double quoted = block of nodes enclosed by double quotes
    // - single quoted with functions = block of nodes enclosed by single quotes allowing embedded functions
    // - double quoted with functions = block of nodes enclosed by double quotes allowing embedded functions
    // - function = simple functions such as ${body} etc
    // - numeric = numeric value
    // - boolean = boolean value
    // - null = null value
    // - unary operator = operator attached to the left hand side node
    // - binary operator = operator attached to both the left and right hand side nodes
    // - logical operator = operator attached to both the left and right hand side nodes

    protected boolean isBooleanValue() {
        if (accept(TokenType.booleanValue)) {
            return true;
        }
        return false;
    }

    protected boolean singleQuotedLiteralWithFunctionsText() {
        if (accept(TokenType.singleQuote)) {
            nextToken(TokenType.singleQuote, TokenType.eol, TokenType.functionStart, TokenType.functionEnd);
            while (!token.getType().isSingleQuote() && !token.getType().isEol()) {
                // we need to loop until we find the ending single quote, or the eol
                nextToken(TokenType.singleQuote, TokenType.eol, TokenType.functionStart, TokenType.functionEnd);
            }
            expect(TokenType.singleQuote);
            return true;
        }
        return false;
    }

    protected boolean singleQuotedLiteralText() {
        if (accept(TokenType.singleQuote)) {
            nextToken(TokenType.singleQuote, TokenType.eol);
            while (!token.getType().isSingleQuote() && !token.getType().isEol()) {
                // we need to loop until we find the ending single quote, or the eol
                nextToken(TokenType.singleQuote, TokenType.eol);
            }
            expect(TokenType.singleQuote);
            return true;
        }
        return false;
    }

    protected boolean doubleQuotedLiteralWithFunctionsText() {
        if (accept(TokenType.doubleQuote)) {
            nextToken(TokenType.doubleQuote, TokenType.eol, TokenType.functionStart, TokenType.functionEnd);
            while (!token.getType().isDoubleQuote() && !token.getType().isEol()) {
                // we need to loop until we find the ending double quote, or the eol
                nextToken(TokenType.doubleQuote, TokenType.eol, TokenType.functionStart, TokenType.functionEnd);
            }
            expect(TokenType.doubleQuote);
            return true;
        }
        return false;
    }

    protected boolean doubleQuotedLiteralText() {
        if (accept(TokenType.doubleQuote)) {
            nextToken(TokenType.doubleQuote, TokenType.eol);
            while (!token.getType().isDoubleQuote() && !token.getType().isEol()) {
                // we need to loop until we find the ending double quote, or the eol
                nextToken(TokenType.doubleQuote, TokenType.eol);
            }
            expect(TokenType.doubleQuote);
            return true;
        }
        return false;
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

    protected boolean binaryOperator() {
        if (accept(TokenType.binaryOperator)) {
            // remember the binary operator
            BinaryOperatorType operatorType = BinaryOperatorType.asOperator(token.getText());

            nextToken();
            // there should be at least one whitespace after the operator
            expectAndAcceptMore(TokenType.whiteSpace);

            // okay a binary operator may not support all kind if preceding parameters, so we need to limit this
            BinaryOperatorType.ParameterType[] types = BinaryOperatorType.supportedParameterTypes(operatorType);

            // based on the parameter types the binary operator support, we need to set this state into
            // the following booleans so we know how to proceed in the grammar
            boolean literalWithFunctionsSupported;
            boolean literalSupported;
            boolean functionSupported;
            boolean numericSupported;
            boolean booleanSupported;
            boolean nullSupported;
            boolean minusSupported;
            if (types == null || types.length == 0) {
                literalWithFunctionsSupported = true;
                // favor literal with functions over literals without functions
                literalSupported = false;
                functionSupported = true;
                numericSupported = true;
                booleanSupported = true;
                nullSupported = true;
                minusSupported = true;
            } else {
                literalWithFunctionsSupported = false;
                literalSupported = false;
                functionSupported = false;
                numericSupported = false;
                booleanSupported = false;
                nullSupported = false;
                minusSupported = false;
                for (BinaryOperatorType.ParameterType parameterType : types) {
                    literalSupported |= parameterType.isLiteralSupported();
                    literalWithFunctionsSupported |= parameterType.isLiteralWithFunctionSupport();
                    functionSupported |= parameterType.isFunctionSupport();
                    nullSupported |= parameterType.isNumericValueSupported();
                    booleanSupported |= parameterType.isBooleanValueSupported();
                    nullSupported |= parameterType.isNullValueSupported();
                    minusSupported |= parameterType.isMinusValueSupported();
                }
            }

            // then we proceed in the grammar according to the parameter types supported by the given binary operator
            if ((literalWithFunctionsSupported && singleQuotedLiteralWithFunctionsText())
                    || (literalWithFunctionsSupported && doubleQuotedLiteralWithFunctionsText())
                    || (literalSupported && singleQuotedLiteralText())
                    || (literalSupported && doubleQuotedLiteralText())
                    || (functionSupported && functionText())
                    || (numericSupported && numericValue())
                    || (booleanSupported && booleanValue())
                    || (nullSupported && nullValue())
                    || (minusSupported && minusValue())) {
                // then after the right hand side value, there should be a whitespace if there is more tokens
                nextToken();
                if (!token.getType().isEol()) {
                    expect(TokenType.whiteSpace);
                }
            } else {
                throw new SimpleParserException(
                        "Binary operator " + operatorType + " does not support token " + token, token.getIndex());
            }
            return true;
        }
        return false;
    }

    protected boolean logicalOperator() {
        if (accept(TokenType.logicalOperator)) {
            // remember the logical operator
            LogicalOperatorType operatorType = LogicalOperatorType.asOperator(token.getText());

            nextToken();
            // there should be at least one whitespace after the operator
            expectAndAcceptMore(TokenType.whiteSpace);

            // then we expect either some quoted text, another function, or a numeric, boolean or null value
            if (singleQuotedLiteralWithFunctionsText()
                    || doubleQuotedLiteralWithFunctionsText()
                    || functionText()
                    || numericValue()
                    || booleanValue()
                    || nullValue()) {
                // then after the right hand side value, there should be a whitespace if there is more tokens
                nextToken();
                if (!token.getType().isEol()) {
                    expect(TokenType.whiteSpace);
                }
            } else {
                throw new SimpleParserException(
                        "Logical operator " + operatorType + " does not support token " + token, token.getIndex());
            }
            return true;
        }
        return false;
    }

    protected boolean numericValue() {
        return accept(TokenType.numericValue);
        // no other tokens to check so do not use nextToken
    }

    protected boolean booleanValue() {
        return accept(TokenType.booleanValue);
        // no other tokens to check so do not use nextToken
    }

    protected boolean nullValue() {
        return accept(TokenType.nullValue);
        // no other tokens to check so do not use nextToken
    }

    protected boolean minusValue() {
        nextToken();
        return accept(TokenType.numericValue);
        // no other tokens to check so do not use nextToken
    }

}
