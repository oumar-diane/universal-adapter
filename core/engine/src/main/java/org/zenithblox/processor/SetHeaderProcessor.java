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
import org.zenithblox.util.ObjectHelper;

/**
 * A processor which sets the header on the IN or OUT message with an {@link org.zenithblox.Expression}
 */
public class SetHeaderProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private final Expression headerName;
    private final Expression expression;

    public SetHeaderProcessor(Expression headerName, Expression expression) {
        this.headerName = headerName;
        this.expression = expression;
        ObjectHelper.notNull(headerName, "headerName");
        ObjectHelper.notNull(expression, "expression");
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            Object newHeader = expression.evaluate(exchange, Object.class);

            if (exchange.getException() != null) {
                // the expression threw an exception so we should break-out
                callback.done(true);
                return true;
            }

            Message old = exchange.getMessage();

            String key = headerName.evaluate(exchange, String.class);
            old.setHeader(key, newHeader);

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
        return "setHeader[" + headerName + ", " + expression + "]";
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

    public String getHeaderName() {
        return headerName.toString();
    }

    public Expression getExpression() {
        return expression;
    }

}
