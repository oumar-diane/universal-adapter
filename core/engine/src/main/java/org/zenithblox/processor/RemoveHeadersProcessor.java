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

import org.zenithblox.AsyncCallback;
import org.zenithblox.Exchange;
import org.zenithblox.Traceable;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.AsyncProcessorSupport;

/**
 * A processor which removes one ore more headers from the IN or OUT message
 */
public class RemoveHeadersProcessor extends AsyncProcessorSupport implements Traceable, IdAware, WorkflowIdAware {
    private String id;
    private String workflowId;
    private final String pattern;
    private final String[] excludePattern;

    public RemoveHeadersProcessor(String pattern, String[] excludePattern) {
        this.pattern = pattern;
        this.excludePattern = excludePattern;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            exchange.getMessage().removeHeaders(pattern, excludePattern);
        } catch (Exception e) {
            exchange.setException(e);
        }

        callback.done(true);
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getTraceLabel() {
        return "removeHeaders[" + pattern + "]";
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

    public String getPattern() {
        return pattern;
    }

    public String[] getExcludePattern() {
        return excludePattern;
    }

}
