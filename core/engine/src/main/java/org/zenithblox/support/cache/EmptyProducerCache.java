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
package org.zenithblox.support.cache;

import org.zenithblox.AsyncProducer;
import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.FailedToCreateProducerException;
import org.zenithblox.support.service.ServiceHelper;

public class EmptyProducerCache extends DefaultProducerCache {

    private final Object source;
    private final ZwangineContext ecc;

    public EmptyProducerCache(Object source, ZwangineContext zwangineContext) {
        super(source, zwangineContext, -1);
        this.source = source;
        this.ecc = zwangineContext;
        setExtendedStatistics(false);
    }

    @Override
    public AsyncProducer acquireProducer(Endpoint endpoint) {
        // always create a new producer
        AsyncProducer answer;
        try {
            answer = endpoint.createAsyncProducer();
            boolean startingWorkflows
                    = ecc.getZwangineContextExtension().isSetupWorkflows() || ecc.getWorkflowController().isStartingWorkflows();
            if (startingWorkflows && answer.isSingleton()) {
                // if we are currently starting a workflow, then add as service and enlist in JMX
                // - but do not enlist non-singletons in JMX
                // - note addService will also start the service
                getZwangineContext().addService(answer);
            } else {
                // must then start service so producer is ready to be used
                ServiceHelper.startService(answer);
            }
        } catch (Exception e) {
            throw new FailedToCreateProducerException(endpoint, e);
        }
        return answer;
    }

    @Override
    public void releaseProducer(Endpoint endpoint, AsyncProducer producer) {
        // stop and shutdown the producer as its not cache or reused
        ServiceHelper.stopAndShutdownService(producer);
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public String toString() {
        return "EmptyProducerCache for source: " + source;
    }

}
