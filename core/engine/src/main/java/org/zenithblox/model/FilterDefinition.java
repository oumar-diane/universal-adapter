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

import org.zenithblox.Predicate;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;

/**
 * Filter out messages based using a predicate
 */
@Metadata(label = "eip,routing")
@AsPredicate
public class FilterDefinition extends OutputExpressionNode {

    @Metadata(label = "advanced")
    private String statusPropertyName;

    public FilterDefinition() {
    }

    protected FilterDefinition(FilterDefinition source) {
        super(source);
        this.statusPropertyName = source.statusPropertyName;
    }

    public FilterDefinition(ExpressionDefinition expression) {
        super(expression);
    }

    public FilterDefinition(Predicate predicate) {
        super(predicate);
    }

    @Override
    public FilterDefinition copyDefinition() {
        return new FilterDefinition(this);
    }

    @Override
    public String toString() {
        return "Filter[" + getExpression() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "filter";
    }

    @Override
    public String getLabel() {
        return "filter[" + getExpression() + "]";
    }

    public String getStatusPropertyName() {
        return statusPropertyName;
    }

    public void setStatusPropertyName(String statusPropertyName) {
        this.statusPropertyName = statusPropertyName;
    }

    /**
     * Expression to determine if the message should be filtered or not. If the expression returns an empty value or
     * <tt>false</tt> then the message is filtered (dropped), otherwise the message is continued being workflowd.
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    /**
     * Name of exchange property to use for storing the status of the filtering.
     *
     * Setting this allows to know if the filter predicate evaluated as true or false.
     */
    public FilterDefinition statusPropertyName(String statusPropertyName) {
        this.statusPropertyName = statusPropertyName;
        return this;
    }
}
