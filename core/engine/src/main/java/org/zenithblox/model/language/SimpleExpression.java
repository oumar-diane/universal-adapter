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

import org.zenithblox.Expression;
import org.zenithblox.spi.Metadata;

/**
 * Evaluates a Zwangine simple expression.
 */
@Metadata(firstVersion = "1.1.0", label = "language,core,java", title = "Simple")
public class SimpleExpression extends TypedExpressionDefinition {

    public SimpleExpression() {
    }

    protected SimpleExpression(SimpleExpression source) {
        super(source);
    }

    public SimpleExpression(String expression) {
        super(expression);
    }

    public SimpleExpression(Expression expression) {
        super(expression);
    }

    private SimpleExpression(Builder builder) {
        super(builder);
    }

    @Override
    public SimpleExpression copyDefinition() {
        return new SimpleExpression(this);
    }

    @Override
    public String getLanguage() {
        return "simple";
    }

    /**
     * {@code Builder} is a specific builder for {@link SimpleExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, SimpleExpression> {

        @Override
        public SimpleExpression end() {
            return new SimpleExpression(this);
        }
    }
}
