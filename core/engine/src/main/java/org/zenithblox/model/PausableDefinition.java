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

import org.zenithblox.resume.ConsumerListener;
import org.zenithblox.spi.Metadata;

import java.util.function.Predicate;

/**
 * Pausable EIP to support resuming processing from last known offset.
 */
@Metadata(label = "eip,routing")
public class PausableDefinition extends NoOutputDefinition<PausableDefinition> {

    private ConsumerListener<?, ?> consumerListenerBean;
    private Predicate<?> untilCheckBean;

    @Metadata(required = true, javaType = "org.zenithblox.resume.ConsumerListener")
    private String consumerListener;
    @Metadata(required = true, javaType = "java.util.function.Predicate")
    private String untilCheck;

    public PausableDefinition() {
    }

    protected PausableDefinition(PausableDefinition source) {
        super(source);
        this.consumerListenerBean = source.consumerListenerBean;
        this.untilCheckBean = source.untilCheckBean;
        this.consumerListener = source.consumerListener;
        this.untilCheck = source.untilCheck;
    }

    @Override
    public PausableDefinition copyDefinition() {
        return new PausableDefinition(this);
    }

    @Override
    public String getShortName() {
        return "pausable";
    }

    @Override
    public String getLabel() {
        return "pausable";
    }

    public ConsumerListener<?, ?> getConsumerListenerBean() {
        return consumerListenerBean;
    }

    public void setConsumerListener(ConsumerListener<?, ?> consumerListenerBean) {
        this.consumerListenerBean = consumerListenerBean;
    }

    public String getConsumerListener() {
        return consumerListener;
    }

    public void setConsumerListener(String consumerListener) {
        this.consumerListener = consumerListener;
    }

    public Predicate<?> getUntilCheckBean() {
        return untilCheckBean;
    }

    public void setUntilCheck(Predicate<?> untilCheckBean) {
        this.untilCheckBean = untilCheckBean;
    }

    public String getUntilCheck() {
        return untilCheck;
    }

    public void setUntilCheck(String untilCheck) {
        this.untilCheck = untilCheck;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the consumer listener to use
     */
    public PausableDefinition consumerListener(String consumerListenerRef) {
        setConsumerListener(consumerListenerRef);
        return this;
    }

    /**
     * Sets the consumer listener to use
     */
    public PausableDefinition consumerListener(ConsumerListener<?, ?> consumerListener) {
        setConsumerListener(consumerListener);
        return this;
    }

    /**
     * References to a java.util.function.Predicate to use for until checks.
     *
     * The predicate is responsible for evaluating whether the processing can resume or not. Such predicate should
     * return true if the consumption can resume, or false otherwise. The exact point of when the predicate is called is
     * dependent on the component, and it may be called on either one of the available events. Implementations should
     * not assume the predicate to be called at any specific point.
     */
    public PausableDefinition untilCheck(String untilCheck) {
        setUntilCheck(untilCheck);
        return this;
    }

    /**
     * The java.util.function.Predicate to use for until checks.
     *
     * The predicate is responsible for evaluating whether the processing can resume or not. Such predicate should
     * return true if the consumption can resume, or false otherwise. The exact point of when the predicate is called is
     * dependent on the component, and it may be called on either one of the available events. Implementations should
     * not assume the predicate to be called at any specific point.
     */
    public PausableDefinition untilCheck(Predicate<?> untilCheck) {
        setUntilCheck(untilCheck);
        return this;
    }

}
