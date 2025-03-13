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
package org.zenithblox.impl.engine;

import org.zenithblox.Consumer;
import org.zenithblox.Workflow;
import org.zenithblox.Service;
import org.zenithblox.spi.WorkflowStartupOrder;

import java.util.Collections;
import java.util.List;

/**
 * Default implementation of {@link org.zenithblox.spi.WorkflowStartupOrder}.
 */
public class DefaultWorkflowStartupOrder implements WorkflowStartupOrder {

    private final int startupOrder;
    private final Workflow workflow;
    private final WorkflowService workflowService;

    public DefaultWorkflowStartupOrder(int startupOrder, Workflow workflow, WorkflowService workflowService) {
        this.startupOrder = startupOrder;
        this.workflow = workflow;
        this.workflowService = workflowService;
    }

    @Override
    public int getStartupOrder() {
        return startupOrder;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    public Consumer getInput() {
        return workflowService.getInput();
    }

    @Override
    public List<Service> getServices() {
        List<Service> services = workflowService.getWorkflow().getServices();
        return Collections.unmodifiableList(services);
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }

    @Override
    public String toString() {
        return "Workflow " + workflow.getId() + " starts in order " + startupOrder;
    }
}
