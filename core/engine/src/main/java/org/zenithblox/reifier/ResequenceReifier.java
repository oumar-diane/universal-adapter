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

import org.zenithblox.AsyncProcessor;
import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ResequenceDefinition;
import org.zenithblox.model.config.BatchResequencerConfig;
import org.zenithblox.model.config.ResequencerConfig;
import org.zenithblox.model.config.StreamResequencerConfig;
import org.zenithblox.processor.Resequencer;
import org.zenithblox.processor.StreamResequencer;
import org.zenithblox.processor.resequencer.DefaultExchangeComparator;
import org.zenithblox.processor.resequencer.ExpressionResultComparator;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.ObjectHelper;

public class ResequenceReifier extends ProcessorReifier<ResequenceDefinition> {

    public ResequenceReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (ResequenceDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        // if configured from XML then streamConfig has been set with the configuration
        ResequencerConfig resequencer = definition.getResequencerConfig();
        StreamResequencerConfig stream = definition.getStreamConfig();
        BatchResequencerConfig batch = definition.getBatchConfig();
        if (resequencer instanceof StreamResequencerConfig streamResequencerConfig) {
            stream = streamResequencerConfig;
        } else if (resequencer instanceof BatchResequencerConfig batchResequencerConfig) {
            batch = batchResequencerConfig;
        }

        if (stream != null) {
            return createStreamResequencer(stream);
        } else {
            // default as batch mode
            if (batch == null) {
                batch = BatchResequencerConfig.getDefault();
            }
            return createBatchResequencer(batch);
        }
    }

    /**
     * Creates a batch {@link Resequencer} instance applying the given <code>config</code>.
     *
     * @param  config    batch resequencer configuration.
     * @return           the configured batch resequencer.
     * @throws Exception can be thrown
     */
    protected Resequencer createBatchResequencer(BatchResequencerConfig config) throws Exception {
        Processor processor = this.createChildProcessor(true);
        Expression expression = createExpression(definition.getExpression());

        // and wrap in unit of work
        AsyncProcessor target = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, processor, workflow);

        ObjectHelper.notNull(config, "config", this);
        ObjectHelper.notNull(expression, "expression", this);

        boolean isReverse = parseBoolean(config.getReverse(), false);
        boolean isAllowDuplicates = parseBoolean(config.getAllowDuplicates(), false);

        Resequencer resequencer = new Resequencer(zwangineContext, target, expression, isAllowDuplicates, isReverse);
        Integer num = parseInt(config.getBatchSize());
        if (num != null) {
            resequencer.setBatchSize(num);
        }
        Long dur = parseDuration(config.getBatchTimeout());
        if (dur != null) {
            resequencer.setBatchTimeout(dur);
        }
        resequencer.setReverse(isReverse);
        resequencer.setAllowDuplicates(isAllowDuplicates);
        if (config.getIgnoreInvalidExchanges() != null) {
            resequencer.setIgnoreInvalidExchanges(parseBoolean(config.getIgnoreInvalidExchanges(), false));
        }
        return resequencer;
    }

    /**
     * Creates a {@link StreamResequencer} instance applying the given <code>config</code>.
     *
     * @param  config    stream resequencer configuration.
     * @return           the configured stream resequencer.
     * @throws Exception can be thrwon
     */
    protected StreamResequencer createStreamResequencer(StreamResequencerConfig config) throws Exception {
        Processor processor = this.createChildProcessor(true);
        Expression expression = createExpression(definition.getExpression());

        AsyncProcessor target = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, processor, workflow);

        ObjectHelper.notNull(config, "config", this);
        ObjectHelper.notNull(expression, "expression", this);

        ExpressionResultComparator comparator;
        if (config.getComparator() != null) {
            comparator = mandatoryLookup(config.getComparator(), ExpressionResultComparator.class);
        } else {
            comparator = config.getComparatorBean();
            if (comparator == null) {
                comparator = new DefaultExchangeComparator();
            }
        }
        comparator.setExpression(expression);

        StreamResequencer resequencer = new StreamResequencer(zwangineContext, target, comparator, expression);
        Long dur = parseDuration(config.getTimeout());
        if (dur != null) {
            resequencer.setTimeout(dur);
        }
        dur = parseDuration(config.getDeliveryAttemptInterval());
        if (dur != null) {
            resequencer.setDeliveryAttemptInterval(dur);
        }
        Integer num = parseInt(config.getCapacity());
        if (num != null) {
            resequencer.setCapacity(num);
        }
        resequencer.setRejectOld(parseBoolean(config.getRejectOld(), false));
        if (config.getIgnoreInvalidExchanges() != null) {
            resequencer.setIgnoreInvalidExchanges(parseBoolean(config.getIgnoreInvalidExchanges(), false));
        }
        return resequencer;
    }

}
