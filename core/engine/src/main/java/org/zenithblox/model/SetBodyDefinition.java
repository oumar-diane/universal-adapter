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
 * Sets the contents of the message body
 */
@Metadata(label = "eip,transformation")
public class SetBodyDefinition extends ExpressionNode {

    public SetBodyDefinition() {
    }

    protected SetBodyDefinition(SetBodyDefinition source) {
        super(source);
    }

    public SetBodyDefinition(Expression expression) {
        super(expression);
    }

    @Override
    public SetBodyDefinition copyDefinition() {
        return new SetBodyDefinition(this);
    }

    @Override
    public String toString() {
        return "SetBody[" + getExpression() + "]";
    }

    @Override
    public String getShortName() {
        return "setBody";
    }

    @Override
    public String getLabel() {
        return "setBody[" + getExpression() + "]";
    }

    /**
     * Expression that returns the new body to use
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }
}
