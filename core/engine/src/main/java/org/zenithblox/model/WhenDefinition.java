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
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;

/**
 * Triggers a workflow when the expression evaluates to true
 */
@Metadata(label = "eip,routing")
@AsPredicate
public class WhenDefinition extends BasicOutputExpressionNode
        implements DisabledAwareDefinition {

    private ProcessorDefinition<?> parent;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean",
              description = "Disables this EIP from the workflow during build time. Once an EIP has been disabled then it cannot be enabled late at runtime.")
    private String disabled;

    public WhenDefinition() {
    }

    public WhenDefinition(WhenDefinition source) {
        super(source);
        this.parent = source.parent;
        this.disabled = source.disabled;
    }

    public WhenDefinition(Predicate predicate) {
        super(predicate);
    }

    public WhenDefinition(Expression expression) {
        super(expression);
    }

    public WhenDefinition(ExpressionDefinition expression) {
        super(expression);
    }

    @Override
    public ProcessorDefinition<?> getParent() {
        return parent;
    }

    public void setParent(ProcessorDefinition<?> parent) {
        this.parent = parent;
    }

    @Override
    public WhenDefinition copyDefinition() {
        return new WhenDefinition(this);
    }

    @Override
    public String toString() {
        return "When[" + description() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "when";
    }

    @Override
    public String getLabel() {
        return "when[" + description() + "]";
    }

    protected String description() {
        StringBuilder sb = new StringBuilder(256);
        if (getExpression() != null) {
            String language = getExpression().getLanguage();
            if (language != null) {
                sb.append(language).append("{");
            }
            sb.append(getExpression().getLabel());
            if (language != null) {
                sb.append("}");
            }
        }
        return sb.toString();
    }

    @Override
    public void setId(String id) {
        if (getOutputs().isEmpty()) {
            super.setId(id);
        } else {
            var last = getOutputs().get(getOutputs().size() - 1);
            last.setId(id);
        }
    }

    @Override
    public String getDisabled() {
        return disabled;
    }

    @Override
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

}
