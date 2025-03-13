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

import org.zenithblox.Consumer;
import org.zenithblox.Workflow;
import org.zenithblox.Service;

import java.util.List;

/**
 * Information about a workflow to be started where we want to control the order in which they are started by
 * {@link org.zenithblox.ZwangineContext}.
 */
public interface WorkflowStartupOrder {

    /**
     * Get the order this workflow should be started.
     * <p/>
     * See more at <a href="http://zwangine.zwangine.org/configuring-workflow-startup-ordering-and-autostartup.html">
     * configuring workflow startup ordering</a>.
     *
     * @return the order
     */
    int getStartupOrder();

    /**
     * Gets the workflow
     *
     * @return the workflow
     */
    Workflow getWorkflow();

    /**
     * Gets the input to this workflow
     *
     * @return the consumer.
     */
    Consumer getInput();

    /**
     * Gets the services to this workflow.
     *
     * @return the services.
     */
    List<Service> getServices();

}
