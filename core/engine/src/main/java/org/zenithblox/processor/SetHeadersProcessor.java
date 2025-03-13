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

import java.util.List;

/**
 * A processor which sets multiple headers on the IN or OUT message with an {@link org.zenithblox.Expression}
 */
public class SetHeadersProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private final List<Expression> headerNames;
    private final List<Expression> expressions;

    public SetHeadersProcessor(List<Expression> headerNames, List<Expression> expressions) {
        this.headerNames = headerNames;
        this.expressions = expressions;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            int headerIndex = 0;
            for (Expression expression : expressions) {
                Object newHeader = expression.evaluate(exchange, Object.class);

                if (exchange.getException() != null) {
                    // the expression threw an exception so we should break-out
                    callback.done(true);
                    return true;
                }
                Message message = exchange.getMessage();
                String key = headerNames.get(headerIndex++).evaluate(exchange, String.class);
                message.setHeader(key, newHeader);
            }

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
        StringBuilder sb = new StringBuilder(256);
        sb.append("setHeaders[");
        int headerIndex = 0;
        for (Expression expression : expressions) {
            if (headerIndex > 0) {
                sb.append("; ");
            }
            sb.append(headerNames.get(headerIndex++).toString());
            sb.append(", ");
            sb.append(expression.toString());
        }
        sb.append("]");
        return sb.toString();
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

    public List<Expression> getHeaderNames() {
        return headerNames;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

}
