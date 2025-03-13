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
package org.zenithblox.reifier;

import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.Service;
import org.zenithblox.model.PolicyDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.WrapProcessor;
import org.zenithblox.spi.Policy;
import org.zenithblox.util.ObjectHelper;

public class PolicyReifier extends AbstractPolicyReifier<PolicyDefinition> {

    public PolicyReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (PolicyDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Policy policy = resolvePolicy();
        ObjectHelper.notNull(policy, "policy", definition);

        // before wrap
        policy.beforeWrap(workflow, definition);

        // create processor after the before wrap
        Processor childProcessor = this.createChildProcessor(true);

        // wrap
        Processor target = policy.wrap(workflow, childProcessor);

        if (!(target instanceof Service)) {
            // wrap the target so it becomes a service and we can manage its
            // lifecycle
            target = new WrapProcessor(target, childProcessor);
        }
        return target;
    }

    protected Policy resolvePolicy() {
        return resolvePolicy(definition.getPolicy(), definition.getRef(), definition.getType());
    }

}
