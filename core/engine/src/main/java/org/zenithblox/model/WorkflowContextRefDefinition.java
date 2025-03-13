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
package org.zenithblox.model;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * To refer to an XML file with workflows defined using the xml-dsl
 */
@Metadata(label = "configuration")
public class WorkflowContextRefDefinition {

    private String ref;

    public WorkflowContextRefDefinition() {
    }

    public WorkflowContextRefDefinition(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "WorkflowContextRef[" + getRef() + "]";
    }

    public String getRef() {
        return ref;
    }

    /**
     * Reference to the workflows in the xml dsl
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public List<WorkflowDefinition> lookupWorkflows(ZwangineContext zwangineContext) {
        return WorkflowContextRefDefinitionHelper.lookupWorkflows(zwangineContext, ref);
    }

}
