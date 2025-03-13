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

/**
 * Expression for which a result type can be provided along with the source of the input (body, header or property).
 */
public abstract class SingleInputTypedExpressionDefinition extends TypedExpressionDefinition {

    private String source;

    protected SingleInputTypedExpressionDefinition() {
    }

    protected SingleInputTypedExpressionDefinition(SingleInputTypedExpressionDefinition source) {
        super(source);
        this.source = source.source;
    }

    protected SingleInputTypedExpressionDefinition(String expression) {
        super(expression);
    }

    protected SingleInputTypedExpressionDefinition(Expression expression) {
        super(expression);
    }

    protected SingleInputTypedExpressionDefinition(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.source = builder.source;
    }

    public String getSource() {
        return source;
    }

    /**
     * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind of
     * source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which is the
     * message body.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * {@code AbstractBuilder} is the base builder for typed expression.
     */
    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<
            T extends AbstractBuilder<T, E>, E extends SingleInputTypedExpressionDefinition>
            extends TypedExpressionDefinition.AbstractBuilder<T, E> {

        private String source;

        /**
         * Source to use, instead of message body. You can prefix with variable:, header:, or property: to specify kind
         * of source. Otherwise, the source is assumed to be a variable. Use empty or null to use default source, which
         * is the message body.
         */
        public T source(String source) {
            this.source = source;
            return (T) this;
        }

        /**
         * Name of variable to use as source, instead of the message body
         */
        public T variableName(String variableName) {
            this.source = "variable:" + variableName;
            return (T) this;
        }

        /**
         * Name of header to use as input, instead of the message body
         */
        public T headerName(String headerName) {
            this.source = "header:" + headerName;
            return (T) this;
        }

        /**
         * Name of property to use as input, instead of the message body.
         */
        public T propertyName(String propertyName) {
            this.source = "property:" + propertyName;
            return (T) this;
        }
    }
}
