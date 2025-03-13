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

import org.zenithblox.*;
import org.zenithblox.model.InterceptDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.processor.FilterProcessor;
import org.zenithblox.processor.Pipeline;
import org.zenithblox.spi.InterceptStrategy;

public class InterceptReifier extends ProcessorReifier<InterceptDefinition> {

    public InterceptReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (InterceptDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        // create the output processor
        Processor child = this.createChildProcessor(true);

        Predicate when = null;
        if (definition.getOnWhen() != null) {
            definition.getOnWhen().preCreateProcessor();
            when = createPredicate(definition.getOnWhen().getExpression());
        }
        if (when != null) {
            child = new FilterProcessor(getZwangineContext(), when, child);
        }
        final Processor output = child;

        // add the output as an intercept strategy to the workflow context so its
        // invoked on each processing step
        workflow.getInterceptStrategies().add(new InterceptStrategy() {
            private Processor interceptedTarget;

            public Processor wrapProcessorInInterceptors(
                    ZwangineContext context, NamedNode definition, Processor target, Processor nextTarget)
                    throws Exception {

                // store the target we are intercepting
                this.interceptedTarget = target;

                if (interceptedTarget != null) {
                    // wrap in a workflow so we continue routing to the next
                    return Pipeline.newInstance(context, output, interceptedTarget);
                } else {
                    return output;
                }
            }

            @Override
            public String toString() {
                return "intercept[" + (interceptedTarget != null ? interceptedTarget : output) + "]";
            }
        });

        // remove me from the workflow, so I am not invoked in a regular workflow path
        ((WorkflowDefinition) workflow.getWorkflow()).getOutputs().remove(definition);
        // and return no processor to invoke next from me
        return null;
    }

}
