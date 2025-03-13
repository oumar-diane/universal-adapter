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

import java.util.concurrent.ScheduledExecutorService;

/**
 * A <a href="http://zwangine.zwangine.org/delayer.html">Delayer</a> which delays processing the exchange until the correct
 * amount of time has elapsed using an expression to determine the delivery time.
 * <p/>
 * This implementation will block while waiting.
 */
public class Delayer extends DelayProcessorSupport implements Traceable, IdAware, WorkflowIdAware {
    private String workflowId;
    private String id;
    private Expression delay;
    private long delayValue;

    public Delayer(ZwangineContext zwangineContext, Processor processor, Expression delay,
                   ScheduledExecutorService executorService, boolean shutdownExecutorService) {
        super(zwangineContext, processor, executorService, shutdownExecutorService);
        this.delay = delay;
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
        return "delay[" + delay + "]";
    }

    public Expression getDelay() {
        return delay;
    }

    public long getDelayValue() {
        return delayValue;
    }

    public void setDelay(Expression delay) {
        this.delay = delay;
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    protected long calculateDelay(Exchange exchange) {
        long time = 0;
        if (delay != null) {
            Long longValue = delay.evaluate(exchange, Long.class);
            if (longValue != null) {
                delayValue = longValue;
                time = longValue;
            } else {
                delayValue = 0;
            }
        }
        if (time <= 0) {
            // no delay
            return 0;
        }

        return time;
    }

}
