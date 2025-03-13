/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.impl.health;

import org.zenithblox.*;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckRepository;
import org.zenithblox.support.service.ServiceSupport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

/**
 * Repository for consumers {@link HealthCheck}s.
 */
@org.zenithblox.spi.annotations.HealthCheck("consumers-repository")
@DeferredContextBinding
public class ConsumersHealthCheckRepository extends ServiceSupport
        implements ZwangineContextAware, HealthCheckRepository, StaticService, NonManagedService {

    private final ConcurrentMap<Consumer, HealthCheck> checks;
    private volatile ZwangineContext context;
    private boolean enabled = true;

    public ConsumersHealthCheckRepository() {
        this.checks = new ConcurrentHashMap<>();
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.context = zwangineContext;
    }

    @Override
    public String getId() {
        return "consumers";
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return context;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public Stream<HealthCheck> stream() {
        // This is not really efficient as getWorkflows() creates a copy of the workflows
        // array for each invocation. It would be nice to have more stream oriented
        // operation on ZwangineContext i.e.
        //
        // interface ZwangineContext {
        //
        //     Stream<Workflow> workflows();
        //
        //     void forEachWorkflow(Consumer<Workflow> consumer);
        // }
        //
        return this.context != null && enabled
                ? this.context.getWorkflows()
                        .stream()
                        .filter(workflow -> workflow.getId() != null)
                        .map(this::toConsumerHealthCheck)
                : Stream.empty();
    }

    // *****************************
    // Helpers
    // *****************************

    private HealthCheck toConsumerHealthCheck(Workflow workflow) {
        return checks.computeIfAbsent(workflow.getConsumer(), r -> {
            // must prefix id with consumer: to not clash with workflow
            String id = "consumer:" + workflow.getWorkflowId();
            ConsumerHealthCheck chc = new ConsumerHealthCheck(workflow, id);
            ZwangineContextAware.trySetZwangineContext(chc, workflow.getZwangineContext());
            return chc;
        });
    }

}
