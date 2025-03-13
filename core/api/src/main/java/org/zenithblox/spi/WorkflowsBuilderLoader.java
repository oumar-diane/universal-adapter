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
import org.zenithblox.WorkflowsBuilder;
import org.zenithblox.StaticService;

/**
 * SPI for loading {@link WorkflowsBuilder} from a {@link Resource}.
 */
public interface WorkflowsBuilderLoader extends StaticService, ZwangineContextAware {

    /**
     * Service factory base path for language specific loaders.
     */
    String FACTORY_PATH = "META-INF/services.org.zenithblox/workflows-loader/";

    /**
     * The supported file extension.
     * <p/>
     * Implementations should support a single extension only.
     */
    String getSupportedExtension();

    /**
     * Whether the file extension is supported
     */
    default boolean isSupportedExtension(String extension) {
        return getSupportedExtension().equals(extension);
    }

    /**
     * Loads {@link WorkflowsBuilder} from {@link Resource}.
     *
     * @param  resource the resource to be loaded.
     * @return          a {@link WorkflowsBuilder}
     */
    WorkflowsBuilder loadWorkflowsBuilder(Resource resource) throws Exception;

    /**
     * Pre-parses the {@link WorkflowsBuilder} from {@link Resource}.
     *
     * This is used during bootstrap, to eager detect configurations from workflow DSL resources which makes it possible to
     * specify configurations that affect the bootstrap, such as by zwangine-jbang and zwangine-yaml-dsl.
     *
     * @param resource the resource to be pre parsed.
     */
    default void preParseWorkflow(Resource resource) throws Exception {
        // noop
    }

}
