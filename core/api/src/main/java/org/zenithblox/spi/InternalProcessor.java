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

import org.zenithblox.AsyncProcessor;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;

import java.util.List;

/**
 * Internal {@link Processor} that Zwangine routing engine used during routing for cross cutting functionality such as:
 * <ul>
 * <li>Execute {@link UnitOfWork}</li>
 * <li>Keeping track which workflow currently is being workflowd</li>
 * <li>Execute {@link WorkflowPolicy}</li>
 * <li>Gather JMX performance statics</li>
 * <li>Tracing</li>
 * <li>Debugging</li>
 * <li>Message History</li>
 * <li>Stream Caching</li>
 * <li>{@link Transformer}</li>
 * </ul>
 * ... and more.
 */
public interface InternalProcessor extends AsyncProcessor {

    /**
     * Adds an {@link ZwangineInternalProcessorAdvice} advice to the list of advices to execute by this internal processor.
     *
     * @param advice the advice to add
     */
    void addAdvice(ZwangineInternalProcessorAdvice<?> advice);

    /**
     * Gets the advice with the given type.
     *
     * @param  type the type of the advice
     * @return      the advice if exists, or <tt>null</tt> if no advices has been added with the given type.
     */
    <T> T getAdvice(Class<T> type);

    /**
     * Adds advice for handling {@link WorkflowPolicy} for the workflow
     */
    void addWorkflowPolicyAdvice(List<WorkflowPolicy> workflowPolicyList);

    /**
     * Adds advice for tracking inflight exchanges for the given workflow
     */
    void addWorkflowInflightRepositoryAdvice(InflightRepository inflightRepository, String workflowId);

    /**
     * Add advice for setting up {@link UnitOfWork} with the lifecycle of the workflow.
     */
    void addWorkflowLifecycleAdvice();

    /**
     * Add advice for JMX management for the workflow
     */
    void addManagementInterceptStrategy(ManagementInterceptStrategy.InstrumentationProcessor processor);

    /**
     * To make it possible for advices to access the created workflow.
     */
    void setWorkflowOnAdvices(Workflow workflow);

}
