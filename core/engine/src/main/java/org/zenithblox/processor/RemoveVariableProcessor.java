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
import org.zenithblox.spi.VariableRepository;
import org.zenithblox.spi.VariableRepositoryFactory;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.util.StringHelper;

/**
 * A processor which removes the variable
 */
public class RemoveVariableProcessor extends AsyncProcessorSupport
        implements Traceable, IdAware, WorkflowIdAware, ZwangineContextAware {
    private ZwangineContext zwangineContext;
    private String id;
    private String workflowId;
    private final Expression variableName;
    private VariableRepositoryFactory factory;

    public RemoveVariableProcessor(Expression variableName) {
        this.variableName = variableName;
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
            String key = variableName.evaluate(exchange, String.class);
            String id = StringHelper.before(key, ":");
            if (id != null) {
                VariableRepository repo = factory.getVariableRepository(id);
                if (repo != null) {
                    key = StringHelper.after(key, ":");
                    repo.removeVariable(key);
                } else {
                    exchange.setException(
                            new IllegalArgumentException("VariableRepository with id: " + id + " does not exist"));
                }
            } else {
                exchange.removeVariable(key);
            }
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    protected void doBuild() throws Exception {
        super.doBuild();
        factory = getZwangineContext().getZwangineContextExtension().getContextPlugin(VariableRepositoryFactory.class);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "removeVariable[" + variableName + "]";
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

}
