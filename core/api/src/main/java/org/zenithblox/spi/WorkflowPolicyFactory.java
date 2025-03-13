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
import org.zenithblox.NamedNode;

/**
 * A factory to create {@link org.zenithblox.spi.WorkflowPolicy} and assign to workflows automatic.
 */
public interface WorkflowPolicyFactory {

    /**
     * Creates a new {@link org.zenithblox.spi.WorkflowPolicy} which will be assigned to the given workflow.
     *
     * @param  zwangineContext the zwangine context
     * @param  workflowId      the workflow id
     * @param  workflow        the workflow definition
     * @return              the created {@link org.zenithblox.spi.WorkflowPolicy}, or <tt>null</tt> to not use a policy
     *                      for this workflow
     */
    WorkflowPolicy createWorkflowPolicy(ZwangineContext zwangineContext, String workflowId, NamedNode workflow);
}
