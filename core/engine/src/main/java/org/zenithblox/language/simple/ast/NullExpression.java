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
import org.zenithblox.language.simple.types.SimpleParserException;
import org.zenithblox.language.simple.types.SimpleToken;

/**
 * Represents a null expression.
 */
public class NullExpression extends BaseSimpleNode {

    private static final Expression NULL_EXPRESSION = new Expression() {
        @Override
        public <T> T evaluate(Exchange exchange, Class<T> type) {
            return null;
        }

        @Override
        public String toString() {
            return "null";
        }
    };

    public NullExpression(SimpleToken token) {
        super(token);
    }

    @Override
    public Expression createExpression(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        return NULL_EXPRESSION;
    }

    @Override
    public String createCode(ZwangineContext zwangineContext, String expression) throws SimpleParserException {
        return "null";
    }
}
