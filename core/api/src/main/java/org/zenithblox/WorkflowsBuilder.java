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
package org.zenithblox;

import java.util.Set;

/**
 * A workflows builder is capable of building workflows using the builder and model classes.
 * <p/>
 * Eventually the workflows are added to a {@link org.zenithblox.ZwangineContext} where they run inside.
 *
 * This interface is not intended to be used by Zwangine end users. Instead, Zwangine users will use
 * <tt>org.zenithblox.builder.WorkflowBuilder</tt> to build workflows in Java DSL.
 */
public interface WorkflowsBuilder {

    /**
     * Adds the workflows from this Workflow Builder to the ZwangineContext.
     *
     * @param  context   the Zwangine context
     * @throws Exception is thrown if initialization of workflows failed
     */
    void addWorkflowsToZwangineContext(ZwangineContext context) throws Exception;

    /**
     * Adds the templated workflows from this Workflow Builder to the ZwangineContext.
     *
     * @param  context   the Zwangine context
     * @throws Exception is thrown if initialization of workflows failed
     */
    void addTemplatedWorkflowsToZwangineContext(ZwangineContext context) throws Exception;

    /**
     * Adds or updates the workflows from this Workflow Builder to the ZwangineContext.
     *
     * @param  context   the Zwangine context
     * @return           workflow ids for the workflows that was updated
     * @throws Exception is thrown if initialization of workflows failed
     */
    Set<String> updateWorkflowsToZwangineContext(ZwangineContext context) throws Exception;

}
