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

import org.zenithblox.Exchange;
import org.zenithblox.Predicate;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ChoiceDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.WhenDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.processor.ChoiceProcessor;
import org.zenithblox.processor.FilterProcessor;
import org.zenithblox.spi.NodeIdFactory;
import org.zenithblox.support.ExchangeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ChoiceReifier extends ProcessorReifier<ChoiceDefinition> {

    /**
     * The logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(ChoiceReifier.class);

    public ChoiceReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, ChoiceDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        final boolean isPrecondition = Boolean.TRUE == parseBoolean(definition.getPrecondition());
        final List<FilterProcessor> filters = isPrecondition ? null : new ArrayList<>();
        for (WhenDefinition whenClause : definition.getWhenClauses()) {
            if (filters != null) {
                whenClause.preCreateProcessor();
                Predicate when = null;
                Processor output = null;
                if (!isDisabled(zwangineContext, whenClause)) {
                    // ensure id is assigned on when
                    whenClause.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
                    when = createPredicate(whenClause.getExpression());
                    output = createOutputsProcessor(whenClause.getOutputs());
                }
                if (when != null && output != null) {
                    filters.add(new FilterProcessor(zwangineContext, when, output));
                }
            }
        }
        if (isPrecondition) {
            return getMatchingBranchProcessor();
        }
        Processor otherwiseProcessor = null;
        if (definition.getOtherwise() != null) {
            if (!isDisabled(zwangineContext, definition.getOtherwise())) {
                // ensure id is assigned on otherwise
                definition.getOtherwise()
                        .idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
                otherwiseProcessor = createOutputsProcessor(definition.getOtherwise().getOutputs());
            }
        }
        return new ChoiceProcessor(filters, otherwiseProcessor);
    }

    /**
     * @return the processor corresponding to the matching branch if any, {@code null} otherwise.
     */
    private Processor getMatchingBranchProcessor() throws Exception {
        // evaluate when predicates to optimize
        Exchange dummy = ExchangeHelper.getDummy(zwangineContext);
        for (WhenDefinition whenClause : definition.getWhenClauses()) {
            ExpressionDefinition exp = whenClause.getExpression();
            exp.initPredicate(zwangineContext);

            Predicate predicate = exp.getPredicate();
            predicate.initPredicate(zwangineContext);

            boolean matches = predicate.matches(dummy);
            if (matches) {
                LOG.debug("doSwitch selected: {}", whenClause.getLabel());
                return createOutputsProcessor(whenClause.getOutputs());
            }
        }

        if (definition.getOtherwise() != null) {
            LOG.debug("doSwitch selected: otherwise");
            return createOutputsProcessor(definition.getOtherwise().getOutputs());
        }

        // no cases were selected
        LOG.debug("doSwitch no when or otherwise selected");
        return null;
    }

}
