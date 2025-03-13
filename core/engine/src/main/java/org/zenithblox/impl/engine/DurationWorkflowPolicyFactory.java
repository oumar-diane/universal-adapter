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
import org.zenithblox.NamedNode;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.WorkflowPolicy;
import org.zenithblox.spi.WorkflowPolicyFactory;
import org.zenithblox.support.PatternHelper;

/**
 * {@link org.zenithblox.spi.WorkflowPolicyFactory} which executes for a duration and then triggers an action.
 * <p/>
 * This can be used to stop a set of workflows (or ZwangineContext) after they have processed a number of messages, or has
 * been running for N seconds.
 */
@Metadata(label = "bean",
          description = "WorkflowPolicyFactory which executes for a duration and then triggers an action."
                        + " This can be used to stop a set of workflows after they have processed a number of messages, or has been running for N seconds.",
          annotations = { "interfaceName=org.zenithblox.spi.WorkflowPolicyFactory" })
@Configurer(metadataOnly = true)
public class DurationWorkflowPolicyFactory implements WorkflowPolicyFactory {

    @Metadata(description = "Workflow pattern to select a set of workflows (by their workflow id). By default all workflows are selected")
    private String fromWorkflowId;
    @Metadata(description = "Maximum seconds Zwangine is running before the action is triggered")
    private int maxSeconds;
    @Metadata(description = "Maximum number of messages to process before the action is triggered")
    private int maxMessages;
    @Metadata(description = "Action to perform", enums = "STOP_CAMEL_CONTEXT,STOP_ROUTE,SUSPEND_ROUTE,SUSPEND_ALL_ROUTES",
              defaultValue = "STOP_ROUTE")
    private DurationWorkflowPolicy.Action action = DurationWorkflowPolicy.Action.STOP_ROUTE;

    @Override
    public WorkflowPolicy createWorkflowPolicy(ZwangineContext zwangineContext, String workflowId, NamedNode workflow) {
        DurationWorkflowPolicy policy = null;

        if (fromWorkflowId == null || PatternHelper.matchPattern(workflowId, fromWorkflowId)) {
            policy = new DurationWorkflowPolicy(zwangineContext, workflowId);
            policy.setMaxMessages(maxMessages);
            policy.setMaxSeconds(maxSeconds);
            policy.setAction(action);
        }

        return policy;
    }

    public String getFromWorkflowId() {
        return fromWorkflowId;
    }

    /**
     * Limit the workflow policy to the workflow which matches this pattern
     *
     * @see PatternHelper#matchPattern(String, String)
     */
    public void setFromWorkflowId(String fromWorkflowId) {
        this.fromWorkflowId = fromWorkflowId;
    }

    public int getMaxMessages() {
        return maxMessages;
    }

    /**
     * Maximum number of messages to process before the action is triggered
     */
    public void setMaxMessages(int maxMessages) {
        this.maxMessages = maxMessages;
    }

    public int getMaxSeconds() {
        return maxSeconds;
    }

    /**
     * Maximum seconds Zwangine is running before the action is triggered
     */
    public void setMaxSeconds(int maxSeconds) {
        this.maxSeconds = maxSeconds;
    }

    public DurationWorkflowPolicy.Action getAction() {
        return action;
    }

    /**
     * What action to perform when maximum is triggered.
     */
    public void setAction(DurationWorkflowPolicy.Action action) {
        this.action = action;
    }

}
