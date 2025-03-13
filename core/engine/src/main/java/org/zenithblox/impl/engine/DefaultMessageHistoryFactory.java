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

import org.zenithblox.*;
import org.zenithblox.spi.MessageHistoryFactory;
import org.zenithblox.support.DefaultMessageHistory;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.service.ServiceSupport;

public class DefaultMessageHistoryFactory extends ServiceSupport implements MessageHistoryFactory {

    private ZwangineContext zwangineContext;
    private boolean copyMessage;
    private String nodePattern;
    private volatile String[] nodePatternParts;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public MessageHistory newMessageHistory(String workflowId, NamedNode node, Exchange exchange) {
        if (nodePatternParts != null) {
            String name = node.getShortName();
            for (String part : nodePatternParts) {
                boolean match = PatternHelper.matchPattern(name, part);
                if (!match) {
                    return null;
                }
            }
        }

        Message msg = null;
        if (copyMessage) {
            msg = exchange.getMessage().copy();
        }

        DefaultMessageHistory answer = new DefaultMessageHistory(workflowId, node, msg);
        return answer;
    }

    public boolean isEnabled() {
        return zwangineContext != null ? zwangineContext.isMessageHistory() : false;
    }

    @Override
    public boolean isCopyMessage() {
        return copyMessage;
    }

    @Override
    public void setCopyMessage(boolean copyMessage) {
        this.copyMessage = copyMessage;
    }

    @Override
    public String getNodePattern() {
        return nodePattern;
    }

    @Override
    public void setNodePattern(String nodePattern) {
        this.nodePattern = nodePattern;
        if (nodePattern != null) {
            this.nodePatternParts = nodePattern.split(",");
        }
    }

}
