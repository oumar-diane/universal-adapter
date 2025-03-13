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

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.RollbackExchangeException;
import org.zenithblox.Traceable;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;

/**
 * Processor for marking an {@link org.zenithblox.Exchange} to rollback.
 */
public class RollbackProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {

    private String id;
    private String workflowId;
    private boolean markRollbackOnly;
    private boolean markRollbackOnlyLast;
    private String message;

    public RollbackProcessor() {
    }

    public RollbackProcessor(String message) {
        this.message = message;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (isMarkRollbackOnlyLast()) {
            // only mark the last workflow (current) as rollback
            // this is needed when you have multiple transactions in play
            exchange.setRollbackOnlyLast(true);
        } else {
            // default to mark the entire workflow as rollback
            exchange.setRollbackOnly(true);
        }

        if (markRollbackOnly || markRollbackOnlyLast) {
            // do not do anything more as we should only mark the rollback
            callback.done(true);
            return true;
        }

        // throw exception to rollback
        if (message != null) {
            exchange.setException(new RollbackExchangeException(message, exchange));
        } else {
            exchange.setException(new RollbackExchangeException(exchange));
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
        return "rollback";
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

    public String getMessage() {
        return message;
    }

    public boolean isMarkRollbackOnly() {
        return markRollbackOnly;
    }

    public void setMarkRollbackOnly(boolean markRollbackOnly) {
        this.markRollbackOnly = markRollbackOnly;
    }

    public boolean isMarkRollbackOnlyLast() {
        return markRollbackOnlyLast;
    }

    public void setMarkRollbackOnlyLast(boolean markRollbackOnlyLast) {
        this.markRollbackOnlyLast = markRollbackOnlyLast;
    }

}
