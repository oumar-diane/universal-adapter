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

import org.zenithblox.Expression;
import org.zenithblox.model.config.BatchResequencerConfig;
import org.zenithblox.model.config.ResequencerConfig;
import org.zenithblox.model.config.StreamResequencerConfig;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.processor.resequencer.ExpressionResultComparator;
import org.zenithblox.spi.Metadata;
import org.zenithblox.util.TimeUtils;

import java.time.Duration;
import java.util.List;

/**
 * Resequences (re-order) messages based on an expression
 */
@Metadata(label = "eip,routing")
public class ResequenceDefinition extends OutputDefinition<ResequenceDefinition> implements HasExpressionType {

    private BatchResequencerConfig batchConfig;
    private StreamResequencerConfig streamConfig;

    private ResequencerConfig resequencerConfig;
    @Metadata(required = true)
    private ExpressionDefinition expression;

    public ResequenceDefinition() {
    }

    protected ResequenceDefinition(ResequenceDefinition source) {
        super(source);
        this.batchConfig = source.batchConfig != null ? source.batchConfig.copyDefinition() : null;
        this.streamConfig = source.streamConfig != null ? source.streamConfig.copyDefinition() : null;
        this.resequencerConfig = source.resequencerConfig != null ? source.resequencerConfig.copyDefinition() : null;
        this.expression = source.expression != null ? source.expression.copyDefinition() : null;
    }

    public ResequenceDefinition(Expression expression) {
        if (expression != null) {
            setExpression(ExpressionNodeHelper.toExpressionDefinition(expression));
        }
    }

    @Override
    public ResequenceDefinition copyDefinition() {
        return new ResequenceDefinition(this);
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    // Fluent API
    // -------------------------------------------------------------------------
    /**
     * Configures the stream-based resequencing algorithm using the default configuration.
     *
     * @return the builder
     */
    public ResequenceDefinition stream() {
        return stream(StreamResequencerConfig.getDefault());
    }

    /**
     * Configures the batch-based resequencing algorithm using the default configuration.
     *
     * @return the builder
     */
    public ResequenceDefinition batch() {
        return batch(BatchResequencerConfig.getDefault());
    }

    /**
     * Configures the stream-based resequencing algorithm using the given {@link StreamResequencerConfig}.
     *
     * @param  config the config
     * @return        the builder
     */
    public ResequenceDefinition stream(StreamResequencerConfig config) {
        this.streamConfig = config;
        this.batchConfig = null;
        return this;
    }

    /**
     * Configures the batch-based resequencing algorithm using the given {@link BatchResequencerConfig}.
     *
     * @param  config the config
     * @return        the builder
     */
    public ResequenceDefinition batch(BatchResequencerConfig config) {
        this.batchConfig = config;
        this.streamConfig = null;
        return this;
    }

    /**
     * Sets the timeout
     *
     * @param  timeout timeout in millis
     * @return         the builder
     */
    public ResequenceDefinition timeout(long timeout) {
        return timeout(Duration.ofMillis(timeout));
    }

    /**
     * Sets the timeout
     *
     * @param  timeout timeout
     * @return         the builder
     */
    public ResequenceDefinition timeout(Duration timeout) {
        return timeout(TimeUtils.printDuration(timeout, true));
    }

    /**
     * Sets the timeout
     *
     * @param  timeout timeout
     * @return         the builder
     */
    public ResequenceDefinition timeout(String timeout) {
        if (streamConfig != null) {
            streamConfig.setTimeout(timeout);
        } else {
            // initialize batch mode as its default mode
            if (batchConfig == null) {
                batch();
            }
            batchConfig.setBatchTimeout(timeout);
        }
        return this;
    }

    /**
     * Sets the interval in milli seconds the stream resequencer will at most wait while waiting for condition of being
     * able to deliver.
     *
     * @param  deliveryAttemptInterval interval in millis
     * @return                         the builder
     */
    public ResequenceDefinition deliveryAttemptInterval(long deliveryAttemptInterval) {
        if (streamConfig == null) {
            throw new IllegalStateException("deliveryAttemptInterval() only supported for stream resequencer");
        }
        streamConfig.setDeliveryAttemptInterval(Long.toString(deliveryAttemptInterval));
        return this;
    }

    /**
     * Sets the rejectOld flag to throw an error when a message older than the last delivered message is processed
     *
     * @return the builder
     */
    public ResequenceDefinition rejectOld() {
        if (streamConfig == null) {
            throw new IllegalStateException("rejectOld() only supported for stream resequencer");
        }
        streamConfig.setRejectOld(Boolean.toString(true));
        return this;
    }

    /**
     * Sets the in batch size for number of exchanges received
     *
     * @param  batchSize the batch size
     * @return           the builder
     */
    public ResequenceDefinition size(int batchSize) {
        if (streamConfig != null) {
            throw new IllegalStateException("size() only supported for batch resequencer");
        }
        // initialize batch mode as its default mode
        if (batchConfig == null) {
            batch();
        }
        batchConfig.setBatchSize(Integer.toString(batchSize));
        return this;
    }

    /**
     * Sets the capacity for the stream resequencer
     *
     * @param  capacity the capacity
     * @return          the builder
     */
    public ResequenceDefinition capacity(int capacity) {
        if (streamConfig == null) {
            throw new IllegalStateException("capacity() only supported for stream resequencer");
        }
        streamConfig.setCapacity(Integer.toString(capacity));
        return this;

    }

    /**
     * Enables duplicates for the batch resequencer mode
     *
     * @return the builder
     */
    public ResequenceDefinition allowDuplicates() {
        if (streamConfig != null) {
            throw new IllegalStateException("allowDuplicates() only supported for batch resequencer");
        }
        // initialize batch mode as its default mode
        if (batchConfig == null) {
            batch();
        }
        batchConfig.setAllowDuplicates(Boolean.toString(true));
        return this;
    }

    /**
     * Enables reverse mode for the batch resequencer mode.
     * <p/>
     * This means the expression for determine the sequence order will be reversed. Can be used for Z..A or 9..0
     * ordering.
     *
     * @return the builder
     */
    public ResequenceDefinition reverse() {
        if (streamConfig != null) {
            throw new IllegalStateException("reverse() only supported for batch resequencer");
        }
        // initialize batch mode as its default mode
        if (batchConfig == null) {
            batch();
        }
        batchConfig.setReverse(Boolean.toString(true));
        return this;
    }

    /**
     * If an incoming {@link org.zenithblox.Exchange} is invalid, then it will be ignored.
     *
     * @return builder
     */
    public ResequenceDefinition ignoreInvalidExchanges() {
        if (streamConfig != null) {
            streamConfig.setIgnoreInvalidExchanges(Boolean.toString(true));
        } else {
            // initialize batch mode as its default mode
            if (batchConfig == null) {
                batch();
            }
            batchConfig.setIgnoreInvalidExchanges(Boolean.toString(true));
        }
        return this;
    }

    /**
     * Sets the comparator to use for stream resequencer
     *
     * @param  comparator the comparator
     * @return            the builder
     */
    public ResequenceDefinition comparator(ExpressionResultComparator comparator) {
        if (streamConfig == null) {
            throw new IllegalStateException("comparator() only supported for stream resequencer");
        }
        streamConfig.setComparatorBean(comparator);
        return this;
    }

    @Override
    public String toString() {
        return "Resequencer[" + getExpression() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "resequence";
    }

    @Override
    public String getLabel() {
        return "resequencer[" + (getExpression() != null ? getExpression().getLabel() : "") + "]";
    }

    public ResequencerConfig getResequencerConfig() {
        return resequencerConfig;
    }

    /**
     * To configure the resequencer in using either batch or stream configuration. Will by default use batch
     * configuration.
     */
    public void setResequencerConfig(ResequencerConfig resequencerConfig) {
        this.resequencerConfig = resequencerConfig;
    }

    public BatchResequencerConfig getBatchConfig() {
        if (batchConfig == null && resequencerConfig != null && resequencerConfig instanceof BatchResequencerConfig) {
            return (BatchResequencerConfig) resequencerConfig;
        }
        return batchConfig;
    }

    public StreamResequencerConfig getStreamConfig() {
        if (streamConfig == null && resequencerConfig != null && resequencerConfig instanceof StreamResequencerConfig) {
            return (StreamResequencerConfig) resequencerConfig;
        }
        return streamConfig;
    }

    public void setBatchConfig(BatchResequencerConfig batchConfig) {
        this.batchConfig = batchConfig;
    }

    public void setStreamConfig(StreamResequencerConfig streamConfig) {
        this.streamConfig = streamConfig;
    }

    public ExpressionDefinition getExpression() {
        return expression;
    }

    /**
     * Expression to use for re-ordering the messages, such as a header with a sequence number
     */
    public void setExpression(ExpressionDefinition expression) {
        this.expression = expression;
    }

    /**
     * Expression to use for re-ordering the messages, such as a header with a sequence number
     */
    public void setExpression(Expression expression) {
        setExpression(new ExpressionDefinition(expression));
    }

    @Override
    public ExpressionDefinition getExpressionType() {
        return getExpression();
    }

    /**
     * Expression to use for re-ordering the messages, such as a header with a sequence number
     */
    @Override
    public void setExpressionType(ExpressionDefinition expressionType) {
        setExpression(expressionType);
    }
}
