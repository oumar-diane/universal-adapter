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
import org.zenithblox.model.CatchDefinition;
import org.zenithblox.model.FinallyDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.TryDefinition;
import org.zenithblox.processor.TryProcessor;

import java.util.ArrayList;
import java.util.List;

public class TryReifier extends ProcessorReifier<TryDefinition> {

    public TryReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (TryDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        Processor tryProcessor = createOutputsProcessor(definition.getOutputsWithoutCatches());
        if (tryProcessor == null) {
            throw new IllegalArgumentException("Definition has no children on " + this);
        }

        List<Processor> catchProcessors = new ArrayList<>();
        if (definition.getCatchClauses() != null) {
            for (CatchDefinition catchClause : definition.getCatchClauses()) {
                catchProcessors.add(createProcessor(catchClause));
            }
        }

        // user must have configured at least one catch or finally
        if (definition.getFinallyClause() == null && definition.getCatchClauses() == null) {
            throw new IllegalArgumentException("doTry must have one or more catch or finally blocks on " + this);
        }

        // must have finally processor as it set some state after completing the entire doTry block
        FinallyDefinition finallyDefinition = definition.getFinallyClause();
        if (finallyDefinition == null) {
            finallyDefinition = new FinallyDefinition();
            finallyDefinition.setParent(definition);
        }
        Processor finallyProcessor = createProcessor(finallyDefinition);

        return new TryProcessor(zwangineContext, tryProcessor, catchProcessors, finallyProcessor);
    }

}
