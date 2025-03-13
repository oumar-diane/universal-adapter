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

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.support.processor.DelegateAsyncProcessor;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The processor which implements the <a href="http://zwangine.zwangine.org/message-filter.html">Message Filter</a> EIP
 * pattern.
 */
public class FilterProcessor extends DelegateAsyncProcessor implements Traceable, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(FilterProcessor.class);

    private final ZwangineContext context;
    private String id;
    private String workflowId;
    private final Predicate predicate;
    private transient long filtered;
    private String statusPropertyName;

    public FilterProcessor(ZwangineContext context, Predicate predicate, Processor processor) {
        super(processor);
        this.context = context;
        this.predicate = predicate;
    }

    public String getStatusPropertyName() {
        return statusPropertyName;
    }

    public void setStatusPropertyName(String statusPropertyName) {
        this.statusPropertyName = statusPropertyName;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();
        predicate.init(context);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        boolean matches = false;

        try {
            matches = matches(exchange);
            if (statusPropertyName != null) {
                exchange.setProperty(statusPropertyName, matches);
            }
        } catch (Exception e) {
            exchange.setException(e);
        }

        if (matches) {
            return processor.process(exchange, callback);
        } else {
            callback.done(true);
            return true;
        }
    }

    public boolean matches(Exchange exchange) {
        boolean matches = predicate.matches(exchange);

        LOG.debug("Filter matches: {} for exchange: {}", matches, exchange);

        if (matches) {
            filtered++;
        }

        return matches;
    }

    @Override
    public String toString() {
        return id;
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

    @Override
    public String getTraceLabel() {
        return "filter[if: " + predicate + "]";
    }

    public Predicate getPredicate() {
        return predicate;
    }

    /**
     * Gets the number of Exchanges that matched the filter predicate and therefore as filtered.
     */
    public long getFilteredCount() {
        return filtered;
    }

    /**
     * Reset counters.
     */
    public void reset() {
        filtered = 0;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        // restart counter
        reset();
        ServiceHelper.startService(predicate);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(predicate);
        super.doStop();
    }
}
