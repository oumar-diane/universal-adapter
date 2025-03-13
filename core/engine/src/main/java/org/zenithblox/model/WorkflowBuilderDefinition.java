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
import org.zenithblox.WorkflowsBuilder;
import org.zenithblox.builder.WorkflowBuilder;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.util.ObjectHelper;

/**
 * To refer to a Java {@link org.zenithblox.builder.WorkflowBuilder} instance to use.
 */
@Metadata(label = "configuration")
public class WorkflowBuilderDefinition extends IdentifiedType {

    private String ref;

    public WorkflowBuilderDefinition() {
    }

    public WorkflowBuilderDefinition(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "WorkflowBuilderRef[" + getRef() + "]";
    }

    public String getRef() {
        return ref;
    }

    /**
     * Reference to the workflow builder instance
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    public WorkflowBuilder createWorkflowBuilder(ZwangineContext zwangineContext) {
        ObjectHelper.notNull(zwangineContext, "zwangineContext", this);
        ObjectHelper.notNull(ref, "ref", this);
        return ZwangineContextHelper.lookup(zwangineContext, ref, WorkflowBuilder.class);
    }

    public WorkflowsBuilder createWorkflows(ZwangineContext zwangineContext) {
        ObjectHelper.notNull(zwangineContext, "zwangineContext", this);
        ObjectHelper.notNull(ref, "ref", this);
        return ZwangineContextHelper.lookup(zwangineContext, ref, WorkflowsBuilder.class);
    }
}
