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


import org.zenithblox.builder.ExpressionBuilder;
import org.zenithblox.spi.Metadata;

/**
 * A fixed value set only once during the workflow startup.
 */
@Metadata(firstVersion = "1.5.0", label = "language,core", title = "Constant")
public class ConstantExpression extends TypedExpressionDefinition {

    public ConstantExpression() {
    }

    protected ConstantExpression(ConstantExpression source) {
        super(source);
    }

    public ConstantExpression(String expression) {
        super(expression);
    }

    private ConstantExpression(Builder builder) {
        super(builder);
        if (builder.value != null) {
            setExpressionValue(ExpressionBuilder.constantExpression(builder.value));
        }
    }

    @Override
    public ConstantExpression copyDefinition() {
        return new ConstantExpression(this);
    }

    @Override
    public String getLanguage() {
        return "constant";
    }

    /**
     * {@code Builder} is a specific builder for {@link ConstantExpression}.
     */
    public static class Builder extends AbstractBuilder<Builder, ConstantExpression> {

        private Object value;

        public Builder value(Object value) {
            this.value = value;
            return this;
        }

        @Override
        public ConstantExpression end() {
            return new ConstantExpression(this);
        }
    }
}
