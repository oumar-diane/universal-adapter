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

import org.zenithblox.Exchange;
import org.zenithblox.Workflow;

/**
 * An extension to {@link org.zenithblox.spi.Synchronization} which provides workflow awareness capabilities.
 */
public interface SynchronizationWorkflowAware {

    /**
     * Invoked before the {@link Exchange} is being workflowd by the given workflow.
     * <p/>
     * Notice if the exchange is being workflowd through multiple workflows, there will be callbacks for each workflow.
     * <p/>
     * <b>Important:</b> this callback may not invoked if the {@link org.zenithblox.spi.SynchronizationWorkflowAware}
     * implementation is being added to the {@link org.zenithblox.spi.UnitOfWork} after the routing has started.
     *
     * @param workflow    the workflow
     * @param exchange the exchange
     */
    void onBeforeWorkflow(Workflow workflow, Exchange exchange);

    /**
     * Invoked after the {@link Exchange} has been workflowd by the given workflow.
     * <p/>
     * Notice if the exchange is being workflowd through multiple workflows, there will be callbacks for each workflow.
     * <p/>
     * This invocation happens before these callbacks:
     * <ul>
     * <li>The consumer of the workflow writes any response back to the caller (if in InOut mode)</li>
     * <li>The UoW is done calling either {@link Synchronization#onComplete(Exchange)} or
     * {@link Synchronization#onFailure(Exchange)}</li>
     * </ul>
     * This allows custom logic to be executed after all routing is done, but before the
     * {@link org.zenithblox.Consumer} prepares and writes any data back to the caller (if in InOut mode).
     *
     * @param workflow    the workflow
     * @param exchange the exchange
     */
    void onAfterWorkflow(Workflow workflow, Exchange exchange);

}
