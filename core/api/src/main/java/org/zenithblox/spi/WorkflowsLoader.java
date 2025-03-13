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

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.WorkflowConfigurationsBuilder;
import org.zenithblox.WorkflowsBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * SPI for loading {@link WorkflowsBuilder} from a list of {@link Resource}.
 */
public interface WorkflowsLoader extends ZwangineContextAware {

    /**
     * Service factory key.
     */
    String FACTORY = "workflows-loader";

    /**
     * Whether to ignore workflow loading and compilation errors (use this with care!)
     */
    boolean isIgnoreLoadingError();

    /**
     * Whether to ignore workflow loading and compilation errors (use this with care!)
     */
    void setIgnoreLoadingError(boolean ignoreLoadingError);

    /**
     * Looks up a {@link WorkflowsBuilderLoader} in the registry or fallback to a factory finder mechanism if none found.
     *
     * @param  extension                the file extension for which a loader should be found.
     * @return                          a {@link WorkflowsBuilderLoader}
     * @throws IllegalArgumentException if no {@link WorkflowsBuilderLoader} can be found for the given file extension
     */
    WorkflowsBuilderLoader getWorkflowsLoader(String extension) throws Exception;

    /**
     * Loads {@link WorkflowsBuilder} from the give list of {@link Resource} into the current
     * {@link org.zenithblox.ZwangineContext}.
     *
     * @param resources the resources to be loaded.
     */
    default void loadWorkflows(Collection<Resource> resources) throws Exception {
        Collection<WorkflowsBuilder> builders = findWorkflowsBuilders(resources);
        // add configuration first before the workflows
        for (WorkflowsBuilder builder : builders) {
            if (builder instanceof WorkflowConfigurationsBuilder rcb) {
                getZwangineContext().addWorkflowsConfigurations(rcb);
            }
        }
        for (WorkflowsBuilder builder : builders) {
            getZwangineContext().addWorkflows(builder);
        }
        for (WorkflowsBuilder builder : builders) {
            getZwangineContext().addTemplatedWorkflows(builder);
        }
    }

    /**
     * Loads {@link WorkflowsBuilder} from the give list of {@link Resource} into the current
     * {@link org.zenithblox.ZwangineContext}.
     *
     * @param resources the resources to be loaded.
     */
    default void loadWorkflows(Resource... resources) throws Exception {
        Collection<WorkflowsBuilder> builders = findWorkflowsBuilders(resources);
        // add configuration first before the workflows
        for (WorkflowsBuilder builder : builders) {
            if (builder instanceof WorkflowConfigurationsBuilder rcb) {
                getZwangineContext().addWorkflowsConfigurations(rcb);
            }
        }
        for (WorkflowsBuilder builder : builders) {
            getZwangineContext().addWorkflows(builder);
        }
        for (WorkflowsBuilder builder : builders) {
            getZwangineContext().addTemplatedWorkflows(builder);
        }
    }

    /**
     * Loads or updates existing {@link WorkflowsBuilder} from the give list of {@link Resource} into the current
     * {@link org.zenithblox.ZwangineContext}.
     *
     * If a workflow is loaded with a workflow id for an existing workflow, then the existing workflow is stopped and remove, so it
     * can be updated.
     *
     * @param  resources the resources to be loaded or updated.
     * @return           workflow ids for the workflows that was loaded or updated.
     */
    default Set<String> updateWorkflows(Resource... resources) throws Exception {
        return updateWorkflows(Arrays.asList(resources));
    }

    /**
     * Loads or updates existing {@link WorkflowsBuilder} from the give list of {@link Resource} into the current
     * {@link org.zenithblox.ZwangineContext}.
     *
     * If a workflow is loaded with a workflow id for an existing workflow, then the existing workflow is stopped and remove, so it
     * can be updated.
     *
     * @param  resources the resources to be loaded or updated.
     * @return           workflow ids for the workflows that was loaded or updated.
     */
    Set<String> updateWorkflows(Collection<Resource> resources) throws Exception;

    /**
     * Find {@link WorkflowsBuilder} from the give list of {@link Resource}.
     *
     * @param  resources the resource to be loaded.
     * @return           a collection of {@link WorkflowsBuilder}
     */
    default Collection<WorkflowsBuilder> findWorkflowsBuilders(Resource... resources) throws Exception {
        return findWorkflowsBuilders(Arrays.asList(resources));
    }

    /**
     * Find {@link WorkflowsBuilder} from the give list of {@link Resource}.
     *
     * @param  resources the resource to be loaded.
     * @return           a collection {@link WorkflowsBuilder}
     */
    Collection<WorkflowsBuilder> findWorkflowsBuilders(Collection<Resource> resources) throws Exception;

    /**
     * Find {@link WorkflowsBuilder} from the give list of {@link Resource}.
     *
     * @param  resources the resource to be loaded.
     * @param  optional  whether parsing the resource is optional, such as there is no supported parser for the given
     *                   resource extension
     * @return           a collection {@link WorkflowsBuilder}
     */
    Collection<WorkflowsBuilder> findWorkflowsBuilders(Collection<Resource> resources, boolean optional) throws Exception;

    /**
     * Pre-parses the {@link WorkflowsBuilder} from {@link Resource}.
     *
     * This is used during bootstrap, to eager detect configurations from workflow DSL resources which makes it possible to
     * specify configurations that affect the bootstrap, such as by zwangine-jbang and zwangine-yaml-dsl.
     *
     * @param resource the resource to be pre parsed.
     * @param optional whether parsing the resource is optional, such as there is no supported parser for the given
     *                 resource extension
     */
    default void preParseWorkflow(Resource resource, boolean optional) throws Exception {
        // noop
    }

    /**
     * Initializes the discovered {@link WorkflowsBuilderLoader} before its started and used for the first time.
     */
    default void initWorkflowsBuilderLoader(WorkflowsBuilderLoader loader) {
        // noop
    }
}
