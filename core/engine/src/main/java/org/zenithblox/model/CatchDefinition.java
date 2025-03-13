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

import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.Predicate;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;

import java.util.ArrayList;
import java.util.List;

/**
 * Catches exceptions as part of a try, catch, finally block
 */
@Metadata(label = "error")
public class CatchDefinition extends OutputDefinition<CatchDefinition> {

    private List<Class<? extends Throwable>> exceptionClasses;

    private List<String> exceptions = new ArrayList<>();
    @Metadata(description = "Used for triggering doCatch in specific situations")
    @AsPredicate
    private OnWhenDefinition onWhen;

    public CatchDefinition() {
    }

    protected CatchDefinition(CatchDefinition source) {
        super(source);
        this.exceptionClasses = source.exceptionClasses;
        this.exceptions = source.exceptions != null ? new ArrayList<>(source.exceptions) : null;
        this.onWhen = source.onWhen != null ? source.onWhen.copyDefinition() : null;
    }

    public CatchDefinition(List<Class<? extends Throwable>> exceptionClasses) {
        exception(exceptionClasses);
    }

    public CatchDefinition(Class<? extends Throwable> exceptionType) {
        exception(exceptionType);
    }

    @Override
    public CatchDefinition copyDefinition() {
        return new CatchDefinition(this);
    }

    @Override
    public String toString() {
        return "DoCatch[ " + getExceptionClasses() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "doCatch";
    }

    @Override
    public String getLabel() {
        return "doCatch[ " + getExceptionClasses() + "]";
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    public List<Class<? extends Throwable>> getExceptionClasses() {
        return exceptionClasses;
    }

    public void setExceptionClasses(List<Class<? extends Throwable>> exceptionClasses) {
        this.exceptionClasses = exceptionClasses;
    }


    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * The exception(s) to catch.
     *
     * @param  exception one or more exceptions
     * @return           the builder
     */
    public CatchDefinition exception(Class<? extends Throwable> exception) {
        return exception(List.of(exception));
    }

    /**
     * The exception(s) to catch.
     *
     * @param  exception1 fist exception
     * @param  exception2 second exception
     * @return            the builder
     */
    public CatchDefinition exception(Class<? extends Throwable> exception1, Class<? extends Throwable> exception2) {
        return exception(List.of(exception1, exception2));
    }

    /**
     * The exception(s) to catch.
     *
     * @param  exception1 fist exception
     * @param  exception2 second exception
     * @param  exception3 third exception
     * @return            the builder
     */
    public CatchDefinition exception(
            Class<? extends Throwable> exception1, Class<? extends Throwable> exception2,
            Class<? extends Throwable> exception3) {
        return exception(List.of(exception1, exception2, exception3));
    }

    /**
     * The exception(s) to catch.
     *
     * @param  exceptions one or more exceptions
     * @return            the builder
     */
    @SafeVarargs
    public final CatchDefinition exception(Class<? extends Throwable>... exceptions) {
        return exception(List.of(exceptions));
    }

    /**
     * The exception(s) to catch.
     *
     * @param  exceptions one or more exceptions
     * @return            the builder
     */
    public CatchDefinition exception(List<Class<? extends Throwable>> exceptions) {
        if (exceptionClasses == null) {
            exceptionClasses = new ArrayList<>();
        }
        for (Class<? extends Throwable> c : exceptions) {
            this.exceptionClasses.add(c);
            this.exceptions.add(c.getName());
        }
        return this;
    }

    /**
     * Sets an additional predicate that should be true before the onCatch is triggered.
     * <p/>
     * To be used for fine grained controlling whether a thrown exception should be intercepted by this exception type
     * or not.
     *
     * @param  predicate predicate that determines true or false
     * @return           the builder
     */
    public CatchDefinition onWhen(@AsPredicate Predicate predicate) {
        setOnWhen(new OnWhenDefinition(predicate));
        return this;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public OnWhenDefinition getOnWhen() {
        return onWhen;
    }

    public void setOnWhen(OnWhenDefinition onWhen) {
        this.onWhen = onWhen;
    }

}
