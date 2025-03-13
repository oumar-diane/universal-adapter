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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.NamedNode;
import org.zenithblox.Workflow;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.WorkflowFactory;

/**
 * Default {@link WorkflowFactory}.
 */
public class DefaultWorkflowFactory implements WorkflowFactory {

    @Override
    public Workflow createWorkflow(
            ZwangineContext zwangineContext, NamedNode workflowDefinition, String workflowId, String workflowDescription,
            Endpoint endpoint, Resource resource) {
        return new DefaultWorkflow(zwangineContext, workflowDefinition, workflowId, workflowDescription, endpoint, resource);
    }
}
