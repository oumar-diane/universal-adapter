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

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for nodes which contain an expression and a number of outputs.
 */
public abstract class OutputExpressionNode extends ExpressionNode implements OutputNode {

    private List<ProcessorDefinition<?>> outputs = new ArrayList<>();

    public OutputExpressionNode() {
    }

    public OutputExpressionNode(OutputExpressionNode source) {
        super(source);
        this.outputs = ProcessorDefinitionHelper.deepCopyDefinitions(source.outputs);
    }

    public OutputExpressionNode(ExpressionNode source) {
        super(source);
    }

    public OutputExpressionNode(ExpressionDefinition expression) {
        super(expression);
    }

    public OutputExpressionNode(Expression expression) {
        super(expression);
    }

    public OutputExpressionNode(Predicate predicate) {
        super(predicate);
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        this.outputs = outputs;
    }

}
