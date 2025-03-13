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
import org.zenithblox.builder.ExpressionBuilder;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Metadata;

/**
 * Sets a named property on the message exchange
 */
@Metadata(label = "eip,transformation")
public class SetPropertyDefinition extends ExpressionNode {

    private String name;

    public SetPropertyDefinition() {
    }

    protected SetPropertyDefinition(SetPropertyDefinition source) {
        super(source);
        this.name = source.name;
    }

    public SetPropertyDefinition(String name, ExpressionDefinition expression) {
        super(expression);
        setName(name);
    }

    public SetPropertyDefinition(String name, Expression expression) {
        super(expression);
        setName(name);
    }

    public SetPropertyDefinition(String name, String value) {
        super(ExpressionBuilder.constantExpression(value));
        setName(name);
    }

    @Override
    public SetPropertyDefinition copyDefinition() {
        return new SetPropertyDefinition(this);
    }

    @Override
    public String toString() {
        return "SetProperty[" + getName() + ", " + getExpression() + "]";
    }

    @Override
    public String getShortName() {
        return "setProperty";
    }

    @Override
    public String getLabel() {
        return "setProperty[" + getName() + "]";
    }

    /**
     * Expression to return the value of the message exchange property
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    /**
     * Name of exchange property to set a new value.
     * <p/>
     * The <tt>simple</tt> language can be used to define a dynamic evaluated exchange property name to be used.
     * Otherwise a constant name will be used.
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
