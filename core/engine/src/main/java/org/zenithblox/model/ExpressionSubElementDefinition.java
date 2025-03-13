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
import org.zenithblox.Predicate;
import org.zenithblox.model.language.ExpressionDefinition;

/**
 * Represents an expression sub element
 */
public class ExpressionSubElementDefinition implements HasExpressionType {

    private ExpressionDefinition expressionType;

    public ExpressionSubElementDefinition() {
    }

    public ExpressionSubElementDefinition(ExpressionSubElementDefinition source) {
        this.expressionType = source.expressionType != null ? source.expressionType.copyDefinition() : null;
    }

    public ExpressionSubElementDefinition(ExpressionDefinition expressionType) {
        this.expressionType = expressionType;
    }

    public ExpressionSubElementDefinition(Expression expression) {
        this.expressionType = new ExpressionDefinition(expression);
    }

    public ExpressionSubElementDefinition(Predicate predicate) {
        this.expressionType = new ExpressionDefinition(predicate);
    }

    public ExpressionSubElementDefinition copyDefinition() {
        return new ExpressionSubElementDefinition(this);
    }

    @Override
    public ExpressionDefinition getExpressionType() {
        return expressionType;
    }

    @Override
    public void setExpressionType(ExpressionDefinition expressionType) {
        this.expressionType = expressionType;
    }

    @Override
    public String toString() {
        if (expressionType != null) {
            return expressionType.toString();
        }
        return super.toString();
    }
}
