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

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Strategy for computing {@link ObjectName} names for the various beans that Zwangine register for management.
 */
public interface ManagementObjectNameStrategy {

    ObjectName getObjectName(Object managedObject) throws MalformedObjectNameException;

    ObjectName getObjectNameForZwangineContext(String managementName, String name) throws MalformedObjectNameException;

    ObjectName getObjectNameForZwangineHealth(ZwangineContext context) throws MalformedObjectNameException;

    ObjectName getObjectNameForZwangineContext(ZwangineContext context) throws MalformedObjectNameException;

    ObjectName getObjectNameForWorkflowController(ZwangineContext context, WorkflowController controller)
            throws MalformedObjectNameException;

    ObjectName getObjectNameForComponent(Component component, String name) throws MalformedObjectNameException;

    ObjectName getObjectNameForEndpoint(Endpoint endpoint) throws MalformedObjectNameException;

    ObjectName getObjectNameForDataFormat(ZwangineContext context, DataFormat endpoint) throws MalformedObjectNameException;

    ObjectName getObjectNameForProcessor(ZwangineContext context, Processor processor, NamedNode definition)
            throws MalformedObjectNameException;

    ObjectName getObjectNameForStep(ZwangineContext context, Processor processor, NamedNode definition)
            throws MalformedObjectNameException;

    ObjectName getObjectNameForWorkflow(Workflow workflow) throws MalformedObjectNameException;

    ObjectName getObjectNameForConsumer(ZwangineContext context, Consumer consumer) throws MalformedObjectNameException;

    ObjectName getObjectNameForProducer(ZwangineContext context, Producer producer) throws MalformedObjectNameException;

    ObjectName getObjectNameForTracer(ZwangineContext context, Service tracer) throws MalformedObjectNameException;

    ObjectName getObjectNameForService(ZwangineContext context, Service service) throws MalformedObjectNameException;

    ObjectName getObjectNameForClusterService(ZwangineContext context, ZwangineClusterService service)
            throws MalformedObjectNameException;

    ObjectName getObjectNameForThreadPool(ZwangineContext context, ThreadPoolExecutor threadPool, String id, String sourceId)
            throws MalformedObjectNameException;

    ObjectName getObjectNameForEventNotifier(ZwangineContext context, EventNotifier eventNotifier)
            throws MalformedObjectNameException;
}
