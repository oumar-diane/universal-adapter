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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.Service;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.TransactedDefinition;
import org.zenithblox.processor.WrapProcessor;
import org.zenithblox.spi.Policy;

public class TransactedReifier extends AbstractPolicyReifier<TransactedDefinition> {

    public TransactedReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (TransactedDefinition) definition);
    }

    public TransactedReifier(ZwangineContext zwangineContext, ProcessorDefinition<?> definition) {
        super(zwangineContext, (TransactedDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Policy policy = resolvePolicy();
        org.zenithblox.util.ObjectHelper.notNull(policy, "policy", this);

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

    public Policy resolvePolicy() {
        return resolvePolicy(definition.getPolicy(), definition.getRef(), definition.getType());
    }

}
