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
package org.zenithblox.model;

import org.zenithblox.Expression;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Metadata;

/**
 * A key value pair where the value is an expression.
 *
 * @see PropertyDefinition
 */
@Metadata(label = "configuration")
public class PropertyExpressionDefinition implements HasExpressionType {

    private String key;
    @Metadata(required = true)
    private ExpressionDefinition expression;

    public PropertyExpressionDefinition() {
    }

    protected PropertyExpressionDefinition(PropertyExpressionDefinition source) {
        this.key = source.key;
        this.expression = source.expression != null ? source.expression.copyDefinition() : null;
    }

    public PropertyExpressionDefinition(String key, Expression expression) {
        this.key = key;
        if (expression != null) {
            setExpression(ExpressionNodeHelper.toExpressionDefinition(expression));
        }
    }

    public PropertyExpressionDefinition copyDefinition() {
        return new PropertyExpressionDefinition(this);
    }

    /**
     * Property key
     */
    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public ExpressionDefinition getExpression() {
        return expression;
    }

    /**
     * Property values as an expression
     */
    public void setExpression(ExpressionDefinition expression) {
        this.expression = expression;
    }

    @Override
    public ExpressionDefinition getExpressionType() {
        return getExpression();
    }

    @Override
    public void setExpressionType(ExpressionDefinition expressionType) {
        setExpression(expressionType);
    }
}
