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
import org.zenithblox.spi.annotations.DslProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Marks the beginning of a try, catch, finally block
 */
@Metadata(label = "eip,routing,error")
public class TryDefinition extends OutputDefinition<TryDefinition> {

    @DslProperty
    private List<CatchDefinition> catchClauses;
    @DslProperty
    private FinallyDefinition finallyClause;
    private boolean initialized;
    private List<ProcessorDefinition<?>> outputsWithoutCatches;
    private int endCounter; // used for detecting multiple nested doTry blocks

    public TryDefinition() {
    }

    protected TryDefinition(TryDefinition source) {
        super(source);
        this.catchClauses = ProcessorDefinitionHelper.deepCopyDefinitions(source.catchClauses);
        this.finallyClause = source.finallyClause != null ? source.finallyClause.copyDefinition() : null;
    }

    @Override
    public TryDefinition copyDefinition() {
        return new TryDefinition(this);
    }

    @Override
    public String toString() {
        return "DoTry[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "doTry";
    }

    @Override
    public String getLabel() {
        return "doTry";
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Handles the given exception
     *
     * @param  exceptionType the exception
     * @return               the try builder
     */
    @SuppressWarnings("unchecked")
    public TryDefinition doCatch(Class<? extends Throwable> exceptionType) {
        // this method is introduced to avoid compiler warnings about the
        // generic Class arrays in the case we've got only one single Class
        // to build a TryDefinition for
        return doCatch(new Class[] { exceptionType });
    }

    /**
     * Handles the given exception(s)
     *
     * @param  exceptionType the exception(s)
     * @return               the try builder
     */
    @SafeVarargs
    public final TryDefinition doCatch(Class<? extends Throwable>... exceptionType) {
        popBlock();
        List<Class<? extends Throwable>> list = Arrays.asList(exceptionType);
        CatchDefinition answer = new CatchDefinition(list);
        addOutput(answer);
        pushBlock(answer);
        return this;
    }

    /**
     * The finally block for a given handle
     *
     * @return the try builder
     */
    public TryDefinition doFinally() {
        popBlock();
        FinallyDefinition answer = new FinallyDefinition();
        addOutput(answer);
        pushBlock(answer);
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
    public TryDefinition onWhen(@AsPredicate Predicate predicate) {
        // we must use a delegate so we can use the fluent builder based on
        // TryDefinition
        // to configure all with try .. catch .. finally
        // set the onWhen predicate on all the catch definitions
        Collection<CatchDefinition> col = ProcessorDefinitionHelper.filterTypeInOutputs(getOutputs(), CatchDefinition.class);
        for (CatchDefinition doCatch : col) {
            doCatch.setOnWhen(new OnWhenDefinition(predicate));
        }
        return this;
    }

    // Properties
    // -------------------------------------------------------------------------

    public void setCatchClauses(List<CatchDefinition> catchClauses) {
        this.catchClauses = catchClauses;
    }

    public List<CatchDefinition> getCatchClauses() {
        if (catchClauses == null) {
            checkInitialized();
        }
        return catchClauses;
    }

    public void setFinallyClause(FinallyDefinition finallyClause) {
        this.finallyClause = finallyClause;
    }

    public FinallyDefinition getFinallyClause() {
        if (finallyClause == null) {
            checkInitialized();
        }
        return finallyClause;
    }

    public List<ProcessorDefinition<?>> getOutputsWithoutCatches() {
        if (outputsWithoutCatches == null) {
            checkInitialized();
        }
        return outputsWithoutCatches;
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return super.getOutputs();
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        initialized = false;
        super.setOutputs(outputs);
    }

    @Override
    public void addOutput(ProcessorDefinition<?> output) {
        initialized = false;
        // reset end counter as we are adding some outputs
        endCounter = 0;
        super.addOutput(output);
    }

    protected ProcessorDefinition<?> onEndDoTry() {
        if (endCounter > 0) {
            return end();
        } else {
            endCounter++;
        }
        return this;
    }

    @Override
    public void preCreateProcessor() {
        // force re-creating initialization to ensure its up-to-date (yaml-dsl creates this EIP specially via @DslProperty)
        initialized = false;
        checkInitialized();
    }

    /**
     * Checks whether or not this object has been initialized
     */
    protected void checkInitialized() {
        if (!initialized) {
            initialized = true;
            outputsWithoutCatches = new ArrayList<>();
            if (catchClauses == null) {
                catchClauses = new ArrayList<>();
            }
            int doFinallyCounter = 0;
            for (ProcessorDefinition<?> output : outputs) {
                if (output instanceof CatchDefinition catchDefinition) {
                    if (!catchClauses.contains(output)) {
                        catchClauses.add(catchDefinition);
                    }
                } else if (output instanceof FinallyDefinition finallyDefinition) {
                    ++doFinallyCounter;
                    finallyClause = finallyDefinition;
                } else {
                    outputsWithoutCatches.add(output);
                }
            }
            if (doFinallyCounter > 1) {
                throw new IllegalArgumentException(
                        "Multiple finally clauses added: " + doFinallyCounter);
            }
            // initialize parent
            for (CatchDefinition cd : catchClauses) {
                cd.setParent(this);
            }
            if (finallyClause != null) {
                finallyClause.setParent(this);
            }
        }
    }

}
