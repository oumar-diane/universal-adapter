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

import org.zenithblox.WorkflowsBuilder;

import java.util.Collection;

/**
 * An extended {@link WorkflowsBuilderLoader} that is capable of loading from multiple resources in one unit (such as
 * compiling them together).
 */
public interface ExtendedWorkflowsBuilderLoader extends WorkflowsBuilderLoader {

    /**
     * Pre-parses the {@link WorkflowsBuilder} from multiple {@link Resource}s.
     *
     * This is used during bootstrap, to eager detect configurations from workflow DSL resources which makes it possible to
     * specify configurations that affect the bootstrap, such as by zwangine-jbang and zwangine-yaml-dsl.
     *
     * @param resources the resources to be pre parsed.
     */
    default void preParseWorkflows(Collection<Resource> resources) throws Exception {
        // by default parse one-by-one
        for (Resource resource : resources) {
            preParseWorkflow(resource);
        }
    }

    /**
     * Loads {@link WorkflowsBuilder} from multiple {@link Resource}s.
     *
     * @param  resources the resources to be loaded.
     * @return           a set of loaded {@link WorkflowsBuilder}s
     */
    Collection<WorkflowsBuilder> loadWorkflowsBuilders(Collection<Resource> resources) throws Exception;

}
