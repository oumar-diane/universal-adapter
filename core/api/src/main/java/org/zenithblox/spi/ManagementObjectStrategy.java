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

import org.zenithblox.*;
import org.zenithblox.cluster.ZwangineClusterService;
import org.zenithblox.health.HealthCheckRegistry;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Strategy for creating the managed object for the various beans Zwangine register for management.
 */
public interface ManagementObjectStrategy {

    Object getManagedObjectForZwangineContext(ZwangineContext context);

    Object getManagedObjectForZwangineHealth(ZwangineContext context, HealthCheckRegistry healthCheckRegistry);

    Object getManagedObjectForComponent(ZwangineContext context, Component component, String name);

    Object getManagedObjectForDataFormat(ZwangineContext context, DataFormat dataFormat);

    Object getManagedObjectForEndpoint(ZwangineContext context, Endpoint endpoint);

    Object getManagedObjectForWorkflowController(ZwangineContext context, WorkflowController workflowController);

    Object getManagedObjectForWorkflow(ZwangineContext context, Workflow workflow);

    Object getManagedObjectForConsumer(ZwangineContext context, Consumer consumer);

    Object getManagedObjectForProducer(ZwangineContext context, Producer producer);

    Object getManagedObjectForProcessor(
            ZwangineContext context, Processor processor,
            NamedNode definition, Workflow workflow);

    Object getManagedObjectForService(ZwangineContext context, Service service);

    Object getManagedObjectForClusterService(ZwangineContext context, ZwangineClusterService service);

    Object getManagedObjectForThreadPool(
            ZwangineContext context, ThreadPoolExecutor threadPool,
            String id, String sourceId, String workflowId, String threadPoolProfileId);

    Object getManagedObjectForEventNotifier(ZwangineContext context, EventNotifier eventNotifier);
}
