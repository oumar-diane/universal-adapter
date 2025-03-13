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
package org.zenithblox.support;

import org.zenithblox.Message;
import org.zenithblox.MessageHistory;
import org.zenithblox.NamedNode;

/**
 * Default {@link org.zenithblox.MessageHistory}.
 */
public class DefaultMessageHistory implements MessageHistory {

    private final String workflowId;
    private final NamedNode node;
    private final String nodeId;
    private final MonotonicClock clock = new MonotonicClock();
    private final Message message;
    private boolean acceptDebugger;
    private long elapsed;

    public DefaultMessageHistory(String workflowId, NamedNode node) {
        this(workflowId, node, null);
    }

    public DefaultMessageHistory(String workflowId, NamedNode node, Message message) {
        this.workflowId = workflowId;
        this.node = node;
        this.nodeId = node.getId();
        this.message = message;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public NamedNode getNode() {
        return node;
    }

    @Override
    public long getTime() {
        return clock.getCreated();
    }

    @Override
    public long getElapsed() {
        return elapsed;
    }

    @Override
    public void nodeProcessingDone() {
        elapsed = clock.elapsed();
    }

    @Override
    public Message getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "DefaultMessageHistory["
               + "workflowId=" + workflowId
               + ", node=" + nodeId
               + ']';
    }
}
