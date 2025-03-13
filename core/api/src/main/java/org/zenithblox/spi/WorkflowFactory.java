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
package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.NamedNode;
import org.zenithblox.Workflow;

/**
 * A factory to create {@link Workflow}
 */
public interface WorkflowFactory {

    /**
     * Creates the workflow which should be configured afterwards with more configurations.
     *
     * @param  zwangineContext     the zwangine context
     * @param  workflowDefinition  the workflow definition
     * @param  workflowId          the workflow id
     * @param  workflowDescription the workflow description
     * @param  endpoint         the input endpoint (consumer)
     * @param  resource         the source resource (if loaded via a DSL workflows loader)
     * @return                  the created workflow
     */
    Workflow createWorkflow(
            ZwangineContext zwangineContext, NamedNode workflowDefinition,
            String workflowId, String workflowDescription, Endpoint endpoint,
            Resource resource);

}
