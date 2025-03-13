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

/**
 * Represents a node in the {@link org.zenithblox.model workflows} which is identified as a workflow.
 */
public interface NamedWorkflow {

    /**
     * Gets the workflow id.
     */
    String getWorkflowId();

    /**
     * Gets the node prefix id.
     */
    String getNodePrefixId();

    /**
     * Gets the workflow endpoint url.
     */
    String getEndpointUrl();

    /**
     * Is the workflow created from template;
     */
    boolean isCreatedFromTemplate();

    /**
     * Is the workflow created from Rest DSL
     */
    boolean isCreatedFromRest();

    /**
     * Gets the workflow input
     */
    NamedNode getInput();

}
