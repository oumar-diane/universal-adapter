/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.util.ObjectHelper;

/**
 * A processor which sets the variable with an {@link Expression}
 */
public class SetVariableProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware, ZwangineContextAware {

    private ZwangineContext zwangineContext;
    private String id;
    private String workflowId;
    private final Expression variableName;
    private final Expression expression;

    public SetVariableProcessor(Expression variableName, Expression expression) {
        this.variableName = variableName;
        this.expression = expression;
        ObjectHelper.notNull(variableName, "variableName");
        ObjectHelper.notNull(expression, "expression");
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            Object newVariable = expression.evaluate(exchange, Object.class);

            if (exchange.getException() != null) {
                // the expression threw an exception so we should break-out
                callback.done(true);
                return true;
            }

            String key = variableName.evaluate(exchange, String.class);
            ExchangeHelper.setVariable(exchange, key, newVariable);
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "setVariable[" + variableName + ", " + expression + "]";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getVariableName() {
        return variableName.toString();
    }

    public Expression getExpression() {
        return expression;
    }

}
