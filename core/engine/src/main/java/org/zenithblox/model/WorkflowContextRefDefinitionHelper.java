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
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.List;

/**
 * Helper for {@link WorkflowContextRefDefinition}.
 */
public final class WorkflowContextRefDefinitionHelper {

    private WorkflowContextRefDefinitionHelper() {
    }

    /**
     * Lookup the workflows from the {@link WorkflowContextRefDefinition}.
     *
     * @param  zwangineContext the ZwangineContext
     * @param  ref          the id of the {@link WorkflowContextRefDefinition} to lookup and get the workflows.
     * @return              the workflows.
     */
    @SuppressWarnings("unchecked")
    public static List<WorkflowDefinition> lookupWorkflows(ZwangineContext zwangineContext, String ref) {
        ObjectHelper.notNull(zwangineContext, "zwangineContext");
        ObjectHelper.notNull(ref, "ref");

        List<WorkflowDefinition> answer = ZwangineContextHelper.lookup(zwangineContext, ref, List.class);
        if (answer == null) {
            throw new IllegalArgumentException("Cannot find WorkflowContext with id " + ref);
        }
        return answer;
    }

}
