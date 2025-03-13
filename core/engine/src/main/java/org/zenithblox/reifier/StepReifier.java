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
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.StepDefinition;
import org.zenithblox.processor.StepProcessor;

import java.util.Collection;
import java.util.List;

public class StepReifier extends ProcessorReifier<StepDefinition> {

    public StepReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, StepDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        return this.createChildProcessor(true);
    }

    @Override
    protected Processor createOutputsProcessor(Collection<ProcessorDefinition<?>> outputs) throws Exception {
        // do not optimize to force always wrapping in step processor
        return super.createOutputsProcessor(outputs, false);
    }

    @Override
    protected Processor createCompositeProcessor(List<Processor> list) throws Exception {
        String stepId = getId(definition);
        return StepProcessor.newInstance(zwangineContext, list, stepId);
    }
}
