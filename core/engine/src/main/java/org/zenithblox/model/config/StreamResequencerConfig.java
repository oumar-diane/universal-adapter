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
package org.zenithblox.model.config;

import org.zenithblox.processor.resequencer.ExpressionResultComparator;
import org.zenithblox.spi.Metadata;

/**
 * Configures stream-processing resequence eip.
 */
@Metadata(label = "configuration,eip")
public class StreamResequencerConfig extends ResequencerConfig {

    private ExpressionResultComparator comparatorBean;

    @Metadata(defaultValue = "1000", javaType = "java.lang.Integer")
    private String capacity;
    @Metadata(defaultValue = "1000", javaType = "java.time.Duration")
    private String timeout;
    @Metadata(label = "advanced", defaultValue = "1000", javaType = "java.time.Duration")
    private String deliveryAttemptInterval;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String ignoreInvalidExchanges;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String rejectOld;
    @Metadata(label = "advanced", javaType = "org.zenithblox.processor.resequencer.ExpressionResultComparator")
    private String comparator;

    /**
     * Creates a new {@link StreamResequencerConfig} instance using default values for <code>capacity</code> (1000) and
     * <code>timeout</code> (1000L). Elements of the sequence are compared using the default
     * {@link ExpressionResultComparator}.
     */
    public StreamResequencerConfig() {
        this(1000, 1000L);
    }

    /**
     * Creates a new {@link StreamResequencerConfig} instance using the given values for <code>capacity</code> and
     * <code>timeout</code>. Elements of the sequence are compared using the default {@link ExpressionResultComparator}.
     *
     * @param capacity capacity of the resequencer's inbound queue.
     * @param timeout  minimum time to wait for missing elements (messages).
     */
    public StreamResequencerConfig(int capacity, long timeout) {
        this(capacity, timeout, null, null);
    }

    /**
     * Creates a new {@link StreamResequencerConfig} instance using the given values for <code>capacity</code> and
     * <code>timeout</code>. Elements of the sequence are compared with the given {@link ExpressionResultComparator}.
     *
     * @param capacity   capacity of the resequencer's inbound queue.
     * @param timeout    minimum time to wait for missing elements (messages).
     * @param comparator comparator for sequence comparison
     */
    public StreamResequencerConfig(int capacity, long timeout, ExpressionResultComparator comparator) {
        this(capacity, timeout, null, comparator);
    }

    /**
     * Creates a new {@link StreamResequencerConfig} instance using the given values for <code>capacity</code> and
     * <code>timeout</code>. Elements of the sequence are compared using the default {@link ExpressionResultComparator}.
     *
     * @param capacity  capacity of the resequencer's inbound queue.
     * @param timeout   minimum time to wait for missing elements (messages).
     * @param rejectOld if true, throws an exception when messages older than the last delivered message are processed
     */
    public StreamResequencerConfig(int capacity, long timeout, Boolean rejectOld) {
        this(capacity, timeout, rejectOld, null);
    }

    /**
     * Creates a new {@link StreamResequencerConfig} instance using the given values for <code>capacity</code> and
     * <code>timeout</code>. Elements of the sequence are compared with the given {@link ExpressionResultComparator}.
     *
     * @param capacity   capacity of the resequencer's inbound queue.
     * @param timeout    minimum time to wait for missing elements (messages).
     * @param rejectOld  if true, throws an exception when messages older than the last delivered message are processed
     * @param comparator comparator for sequence comparison
     */
    public StreamResequencerConfig(int capacity, long timeout, Boolean rejectOld, ExpressionResultComparator comparator) {
        this.capacity = Integer.toString(capacity);
        this.timeout = Long.toString(timeout);
        this.rejectOld = rejectOld != null ? Boolean.toString(rejectOld) : null;
        this.comparatorBean = comparator;
    }

    protected StreamResequencerConfig(StreamResequencerConfig source) {
        this.comparatorBean = source.comparatorBean;
        this.capacity = source.capacity;
        this.timeout = source.timeout;
        this.deliveryAttemptInterval = source.deliveryAttemptInterval;
        this.ignoreInvalidExchanges = source.ignoreInvalidExchanges;
        this.rejectOld = source.rejectOld;
        this.comparator = source.comparator;
    }

    @Override
    public StreamResequencerConfig copyDefinition() {
        return new StreamResequencerConfig(this);
    }

    /**
     * Returns a new {@link StreamResequencerConfig} instance using default values for <code>capacity</code> (1000) and
     * <code>timeout</code> (1000L). Elements of the sequence are compared using the default
     * {@link ExpressionResultComparator}.
     *
     * @return a default {@link StreamResequencerConfig}.
     */
    public static StreamResequencerConfig getDefault() {
        return new StreamResequencerConfig();
    }

    public String getCapacity() {
        return capacity;
    }

    /**
     * Sets the capacity of the resequencer inbound queue.
     */
    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getTimeout() {
        return timeout;
    }

    /**
     * Sets minimum time (milliseconds) to wait for missing elements (messages).
     */
    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getDeliveryAttemptInterval() {
        return deliveryAttemptInterval;
    }

    /**
     * Sets the interval in milliseconds the stream resequencer will at most wait while waiting for condition of being
     * able to deliver.
     */
    public void setDeliveryAttemptInterval(String deliveryAttemptInterval) {
        this.deliveryAttemptInterval = deliveryAttemptInterval;
    }

    public String getIgnoreInvalidExchanges() {
        return ignoreInvalidExchanges;
    }

    /**
     * Whether to ignore invalid exchanges
     */
    public void setIgnoreInvalidExchanges(String ignoreInvalidExchanges) {
        this.ignoreInvalidExchanges = ignoreInvalidExchanges;
    }

    public ExpressionResultComparator getComparatorBean() {
        return comparatorBean;
    }

    /**
     * To use a custom comparator
     */
    public void setComparatorBean(ExpressionResultComparator comparatorBean) {
        this.comparatorBean = comparatorBean;
    }

    public String getComparator() {
        return comparator;
    }

    /**
     * To use a custom comparator as a org.zenithblox.processor.resequencer.ExpressionResultComparator type.
     */
    public void setComparator(String comparator) {
        this.comparator = comparator;
    }

    /**
     * If true, throws an exception when messages older than the last delivered message are processed
     */
    public void setRejectOld(String value) {
        this.rejectOld = value;
    }

    public String getRejectOld() {
        return rejectOld;
    }

}
