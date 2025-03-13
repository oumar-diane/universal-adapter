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

import org.zenithblox.spi.Metadata;
import org.zenithblox.util.TimeUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Extract a sample of the messages passing through a workflow
 */
@Metadata(label = "eip,routing")
public class SamplingDefinition extends NoOutputDefinition<SamplingDefinition> {

    @Metadata(defaultValue = "1000", javaType = "java.time.Duration")
    private String samplePeriod;
    @Metadata(javaType = "java.lang.Long")
    private String messageFrequency;

    public SamplingDefinition() {
    }

    protected SamplingDefinition(SamplingDefinition source) {
        super(source);
        this.samplePeriod = source.samplePeriod;
        this.messageFrequency = source.messageFrequency;
    }

    public SamplingDefinition(String samplePeriod) {
        this.samplePeriod = samplePeriod;
    }

    public SamplingDefinition(Duration period) {
        this.samplePeriod = TimeUtils.printDuration(period);
    }

    public SamplingDefinition(long samplePeriod, TimeUnit units) {
        this(Duration.ofMillis(units.toMillis(samplePeriod)));
    }

    public SamplingDefinition(long messageFrequency) {
        this.messageFrequency = Long.toString(messageFrequency);
    }

    @Override
    public SamplingDefinition copyDefinition() {
        return new SamplingDefinition(this);
    }

    @Override
    public String getShortName() {
        return "sample";
    }

    @Override
    public String toString() {
        return "Sample[" + description() + " -> " + getOutputs() + "]";
    }

    protected String description() {
        if (messageFrequency != null) {
            return "1 Exchange per " + getMessageFrequency() + " messages received";
        } else {
            return "1 Exchange per " + TimeUtils.printDuration(TimeUtils.toDuration(samplePeriod));
        }
    }

    @Override
    public String getLabel() {
        return "sample[" + description() + "]";
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the sample message count which only a single {@link org.zenithblox.Exchange} will pass through after this
     * many received.
     *
     * @param  messageFrequency the message frequency
     * @return                  the builder
     */
    public SamplingDefinition sampleMessageFrequency(long messageFrequency) {
        setMessageFrequency(messageFrequency);
        return this;
    }

    /**
     * Sets the sample period during which only a single {@link org.zenithblox.Exchange} will pass through.
     *
     * @param  samplePeriod the period
     * @return              the builder
     */
    public SamplingDefinition samplePeriod(Duration samplePeriod) {
        setSamplePeriod(samplePeriod);
        return this;
    }

    /**
     * Sets the sample period during which only a single {@link org.zenithblox.Exchange} will pass through.
     *
     * @param  samplePeriod the period
     * @return              the builder
     */
    public SamplingDefinition samplePeriod(String samplePeriod) {
        setSamplePeriod(samplePeriod);
        return this;
    }

    /**
     * Sets the sample period during which only a single {@link org.zenithblox.Exchange} will pass through.
     *
     * @param  samplePeriod the period
     * @return              the builder
     */
    public SamplingDefinition samplePeriod(long samplePeriod) {
        setSamplePeriod(samplePeriod);
        return this;
    }

    // Properties
    // -------------------------------------------------------------------------

    public String getSamplePeriod() {
        return samplePeriod;
    }

    /**
     * Sets the sample period during which only a single Exchange will pass through.
     */
    public void setSamplePeriod(String samplePeriod) {
        this.samplePeriod = samplePeriod;
    }

    public void setSamplePeriod(long samplePeriod) {
        setSamplePeriod(Duration.ofMillis(samplePeriod));
    }

    public void setSamplePeriod(Duration samplePeriod) {
        this.samplePeriod = TimeUtils.printDuration(samplePeriod);
    }

    public String getMessageFrequency() {
        return messageFrequency;
    }

    /**
     * Sets the sample message count which only a single Exchange will pass through after this many received.
     */
    public void setMessageFrequency(String messageFrequency) {
        this.messageFrequency = messageFrequency;
    }

    public void setMessageFrequency(long messageFrequency) {
        this.messageFrequency = Long.toString(messageFrequency);
    }

}
