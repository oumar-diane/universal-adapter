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

import java.util.Comparator;

/**
 * Sorts the contents of the message
 */
@Metadata(label = "eip,transformation")
public class SortDefinition<T> extends ExpressionNode {

    private Comparator<? super T> comparatorBean;
    @Metadata(label = "advanced", javaType = "java.util.Comparator")
    private String comparator;

    public SortDefinition() {
    }

    protected SortDefinition(SortDefinition source) {
        super(source);
        this.comparatorBean = source.comparatorBean;
        this.comparator = source.comparator;
    }

    public SortDefinition(Expression expression) {
        setExpression(ExpressionNodeHelper.toExpressionDefinition(expression));
    }

    public SortDefinition(Expression expression, Comparator<? super T> comparator) {
        this(expression);
        this.comparatorBean = comparator;
    }

    @Override
    public SortDefinition copyDefinition() {
        return new SortDefinition(this);
    }

    @Override
    public String toString() {
        return "sort[" + getExpression() + " by: " + (comparator != null ? "ref:" + comparator : comparatorBean) + "]";
    }

    @Override
    public String getShortName() {
        return "sort";
    }

    @Override
    public String getLabel() {
        return "sort[" + getExpression() + "]";
    }

    /**
     * Optional expression to sort by something else than the message body
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    /**
     * Sets the comparator to use for sorting
     *
     * @param  comparator the comparator to use for sorting
     * @return            the builder
     */
    public SortDefinition<T> comparator(Comparator<T> comparator) {
        this.comparatorBean = comparator;
        return this;
    }

    /**
     * Sets a reference to lookup for the comparator to use for sorting
     *
     * @param  ref reference for the comparator
     * @return     the builder
     */
    public SortDefinition<T> comparator(String ref) {
        setComparator(ref);
        return this;
    }

    public Comparator<? super T> getComparatorBean() {
        return comparatorBean;
    }

    public String getComparator() {
        return comparator;
    }

    public void setComparator(String comparator) {
        this.comparator = comparator;
    }
}
