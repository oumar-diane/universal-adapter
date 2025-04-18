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

import org.zenithblox.Predicate;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * Intercepts a message at each step in the workflow
 */
@Metadata(label = "configuration")
public class InterceptDefinition extends OutputDefinition<InterceptDefinition> {

    @Metadata(description = "To use an expression to only trigger intercepting in specific situations")
    @AsPredicate
    private OnWhenDefinition onWhen;

    public InterceptDefinition() {
    }

    protected InterceptDefinition(InterceptDefinition source) {
        super(source);
        this.onWhen = source.onWhen != null ? source.onWhen.copyDefinition() : null;
    }

    @Override
    public InterceptDefinition copyDefinition() {
        return new InterceptDefinition(this);
    }

    @Override
    public String toString() {
        return "Intercept[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "intercept";
    }

    @Override
    public String getLabel() {
        return "intercept";
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isTopLevelOnly() {
        return true;
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    public OnWhenDefinition getOnWhen() {
        return onWhen;
    }

    public void setOnWhen(OnWhenDefinition onWhen) {
        this.onWhen = onWhen;
    }

    /**
     * Applies this interceptor only if the given predicate is true
     *
     * @param      predicate the predicate
     * @return               the builder
     * @deprecated           use {@link #onWhen(Predicate)}
     */
    @Deprecated
    public InterceptDefinition when(@AsPredicate Predicate predicate) {
        return onWhen(predicate);
    }

    /**
     * Applies this interceptor only if the given predicate is true
     *
     * @param  predicate the predicate
     * @return           the builder
     */
    public InterceptDefinition onWhen(@AsPredicate Predicate predicate) {
        setOnWhen(new OnWhenDefinition(predicate));
        return this;
    }

}
