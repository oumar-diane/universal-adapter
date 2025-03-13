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
import org.zenithblox.support.DefaultMessage;
import org.zenithblox.support.ExchangeHelper;

/**
 * A processor which sets the body on the IN or OUT message with an {@link Expression}
 */
public class SetBodyProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private final Expression expression;

    public SetBodyProcessor(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            Object newBody = expression.evaluate(exchange, Object.class);

            if (exchange.getException() != null) {
                // the expression threw an exception so we should break-out
                callback.done(true);
                return true;
            }

            Message old = exchange.getMessage();

            // create a new message container so we do not drag specialized message objects along
            // but that is only needed if the old message is a specialized message
            boolean copyNeeded = !(old.getClass().equals(DefaultMessage.class));

            if (copyNeeded) {
                Message msg = new DefaultMessage(exchange.getContext());
                msg.copyFromWithNewBody(old, newBody);

                // replace message on exchange
                ExchangeHelper.replaceMessage(exchange, msg, false);
            } else {
                // no copy needed so set replace value directly
                old.setBody(newBody);
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
        return "setBody[" + expression + "]";
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

    public Expression getExpression() {
        return expression;
    }

}
