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
import org.zenithblox.Expression;
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;
import org.zenithblox.support.builder.ExpressionBuilder;

/**
 * Represents literals in the AST.
 */
public class LiteralExpression extends BaseSimpleNode implements LiteralNode {

    protected final StringBuilder text = new StringBuilder(256);

    public LiteralExpression(SimpleToken token) {
        super(token);
    }

    @Override
    public String toString() {
        return getText();
    }

    @Override
    public void addText(String text) {
        this.text.append(text);
    }

    @Override
    public String getText() {
        return text.toString();
    }

    @Override
    public boolean quoteEmbeddedNodes() {
        // we should quote embedded nodes if using the bean function as the nodes can be parameters
        // to a bean method call so we want to ensure their parameter value is quoted to avoid parsing
        // issues with commas in parameter values being mixed up with commas used for parameter separator
        return text.toString().startsWith("bean:");
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, String expression) {
        return ExpressionBuilder.constantExpression(getText());
    }

    @Override
    public String createCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        return getText();
    }
}
