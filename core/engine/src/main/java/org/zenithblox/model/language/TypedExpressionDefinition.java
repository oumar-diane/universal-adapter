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
 * Expression for which a result type can be provided.
 */
public abstract class TypedExpressionDefinition extends ExpressionDefinition {

    private String resultTypeName;
    private Class<?> resultType;

    protected TypedExpressionDefinition() {
    }

    protected TypedExpressionDefinition(TypedExpressionDefinition source) {
        super(source);
        this.resultTypeName = source.resultTypeName;
        this.resultType = source.resultType;
    }

    protected TypedExpressionDefinition(String expression) {
        super(expression);
    }

    protected TypedExpressionDefinition(Expression expression) {
        super(expression);
    }

    protected TypedExpressionDefinition(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.resultTypeName = builder.resultTypeName;
        this.resultType = builder.resultType;
    }

    public String getResultTypeName() {
        return resultTypeName;
    }

    /**
     * Sets the class of the result type (type from output)
     */
    public void setResultTypeName(String resultTypeName) {
        this.resultTypeName = resultTypeName;
    }

    public Class<?> getResultType() {
        return resultType;
    }

    /**
     * Sets the class name of the result type (type from output)
     */
    public void setResultType(Class<?> resultType) {
        this.resultType = resultType;
    }

    /**
     * {@code AbstractBuilder} is the base builder for typed expression.
     */
    @SuppressWarnings("unchecked")
    public abstract static class AbstractBuilder<T extends AbstractBuilder<T, E>, E extends TypedExpressionDefinition>
            extends ExpressionDefinition.AbstractBuilder<T, E> {

        private String resultTypeName;
        private Class<?> resultType;

        /**
         * Sets the class of the result type (type from output)
         */
        public T resultTypeName(String resultTypeName) {
            this.resultTypeName = resultTypeName;
            return (T) this;
        }

        /**
         * Sets the class name of the result type (type from output)
         */
        public T resultType(Class<?> resultType) {
            this.resultType = resultType;
            return (T) this;
        }
    }
}
