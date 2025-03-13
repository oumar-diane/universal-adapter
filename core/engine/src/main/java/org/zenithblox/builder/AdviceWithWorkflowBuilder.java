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
package org.zenithblox.builder;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Endpoint;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.spi.EndpointStrategy;
import org.zenithblox.spi.MockSendToEndpointStrategyFactory;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.ArrayList;
import java.util.List;

import static org.zenithblox.spi.FactoryFinder.DEFAULT_PATH;

/**
 * A {@link WorkflowBuilder} which has extended capabilities when using the
 * <a href="http://zwangine.zentihblox.org/advicewith.html">advice with</a> feature.
 * <p/>
 * <b>Important:</b> It is recommended to only advice a given workflow once (you can of course advice multiple workflows). If
 * you do it multiple times, then it may not work as expected, especially when any kind of error handling is involved.
 */
public abstract class AdviceWithWorkflowBuilder extends WorkflowBuilder {

    private WorkflowDefinition originalWorkflow;
    private final List<AdviceWithTask> adviceWithTasks = new ArrayList<>();
    private boolean logWorkflowAsXml = true;

    public AdviceWithWorkflowBuilder() {
    }

    public AdviceWithWorkflowBuilder(ZwangineContext context) {
        super(context);
    }

    /**
     * Sets the original workflow to be adviced.
     *
     * @param originalWorkflow the original workflow.
     */
    public void setOriginalWorkflow(WorkflowDefinition originalWorkflow) {
        this.originalWorkflow = originalWorkflow;
    }

    /**
     * Gets the original workflow to be adviced.
     *
     * @return the original workflow.
     */
    public WorkflowDefinition getOriginalWorkflow() {
        return originalWorkflow;
    }

    /**
     * Whether to log the adviced workflows before/after as XML. This is usable to know how the workflow was adviced and
     * changed. However marshalling the workflow model to XML costs CPU resources and you can then turn this off by not
     * logging. This is default enabled.
     */
    public boolean isLogWorkflowAsXml() {
        return logWorkflowAsXml;
    }

    /**
     * Sets whether to log the adviced workflows before/after as XML. This is usable to know how the workflow was adviced and
     * changed. However marshalling the workflow model to XML costs CPU resources and you can then turn this off by not
     * logging. This is default enabled.
     */
    public void setLogWorkflowAsXml(boolean logWorkflowAsXml) {
        this.logWorkflowAsXml = logWorkflowAsXml;
    }

    /**
     * Gets a list of additional tasks to execute after the {@link #configure()} method has been executed during the
     * advice process.
     *
     * @return a list of additional {@link AdviceWithTask} tasks to be executed during the advice process.
     */
    public List<AdviceWithTask> getAdviceWithTasks() {
        return adviceWithTasks;
    }

    /**
     * Mock all endpoints.
     *
     * @throws Exception can be thrown if error occurred
     */
    public void mockEndpoints() throws Exception {
        getContext().getZwangineContextExtension().registerEndpointCallback(createMockEndpointStrategy(null, false));
    }

    /**
     * Mock all endpoints matching the given pattern.
     *
     * @param  pattern   the pattern(s).
     * @throws Exception can be thrown if error occurred
     * @see              EndpointHelper#matchEndpoint(org.zenithblox.ZwangineContext, String, String)
     */
    public void mockEndpoints(String... pattern) throws Exception {
        for (String s : pattern) {
            // the text based input may be property placeholders
            s = getContext().resolvePropertyPlaceholders(s);
            getContext().getZwangineContextExtension().registerEndpointCallback(createMockEndpointStrategy(s, false));
        }
    }

    /**
     * Mock all endpoints matching the given pattern, and <b>skips</b> sending to the original endpoint (detour
     * messages).
     *
     * @param  pattern   the pattern(s).
     * @throws Exception can be thrown if error occurred
     * @see              EndpointHelper#matchEndpoint(org.zenithblox.ZwangineContext, String, String)
     */
    public void mockEndpointsAndSkip(String... pattern) throws Exception {
        for (String s : pattern) {
            // the text based input may be property placeholders
            s = getContext().resolvePropertyPlaceholders(s);
            getContext().getZwangineContextExtension()
                    .registerEndpointCallback(createMockEndpointStrategy(s, true));
        }
    }

    /**
     * Replaces the workflow from endpoint with a new uri
     *
     * @param uri uri of the new endpoint
     */
    public void replaceFromWith(String uri) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        // the text based input may be property placeholders
        uri = getContext().resolvePropertyPlaceholders(uri);
        getAdviceWithTasks().add(AdviceWithTasks.replaceFromWith(originalWorkflow, uri));
    }

    /**
     * Replaces the workflow from endpoint with a new endpoint
     *
     * @param endpoint the new endpoint
     */
    public void replaceFromWith(Endpoint endpoint) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        getAdviceWithTasks().add(AdviceWithTasks.replaceFrom(originalWorkflow, endpoint));
    }

    /**
     * Weaves by matching id of the nodes in the workflow (incl onException etc).
     * <p/>
     * Uses the {@link PatternHelper#matchPattern(String, String)} matching algorithm.
     *
     * @param  pattern the pattern
     * @return         the builder
     * @see            PatternHelper#matchPattern(String, String)
     */
    public <T extends ProcessorDefinition<?>> AdviceWithBuilder<T> weaveById(String pattern) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        // the text based input may be property placeholders
        pattern = getContext().resolvePropertyPlaceholders(pattern);
        return new AdviceWithBuilder<>(this, pattern, null, null, null);
    }

    /**
     * Weaves by matching the to string representation of the nodes in the workflow (incl onException etc).
     * <p/>
     * Uses the {@link PatternHelper#matchPattern(String, String)} matching algorithm.
     *
     * @param  pattern the pattern
     * @return         the builder
     * @see            PatternHelper#matchPattern(String, String)
     */
    public <T extends ProcessorDefinition<?>> AdviceWithBuilder<T> weaveByToString(String pattern) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        // the text based input may be property placeholders
        pattern = getContext().resolvePropertyPlaceholders(pattern);
        return new AdviceWithBuilder<>(this, null, pattern, null, null);
    }

    /**
     * Weaves by matching sending to endpoints with the given uri of the nodes in the workflow (incl onException etc).
     * <p/>
     * Uses the {@link PatternHelper#matchPattern(String, String)} matching algorithm.
     *
     * @param  pattern the pattern
     * @return         the builder
     * @see            PatternHelper#matchPattern(String, String)
     */
    public <T extends ProcessorDefinition<?>> AdviceWithBuilder<T> weaveByToUri(String pattern) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        // the text based input may be property placeholders
        pattern = getContext().resolvePropertyPlaceholders(pattern);
        return new AdviceWithBuilder<>(this, null, null, pattern, null);
    }

    /**
     * Weaves by matching type of the nodes in the workflow (incl onException etc).
     *
     * @param  type the processor type
     * @return      the builder
     */
    public <T extends ProcessorDefinition<?>> AdviceWithBuilder<T> weaveByType(Class<T> type) {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        return new AdviceWithBuilder<>(this, null, null, null, type);
    }

    /**
     * Weaves by adding the nodes to the start of the workflow (excl onException etc).
     *
     * @return the builder
     */
    public <T extends ProcessorDefinition<?>> ProcessorDefinition<?> weaveAddFirst() {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        return new AdviceWithBuilder<T>(this, "*", null, null, null).selectFirst().before();
    }

    /**
     * Weaves by adding the nodes to the end of the workflow (excl onException etc).
     *
     * @return the builder
     */
    public <T extends ProcessorDefinition<?>> ProcessorDefinition<?> weaveAddLast() {
        ObjectHelper.notNull(originalWorkflow, "originalWorkflow", this);
        return new AdviceWithBuilder<T>(this, "*", null, null, null).maxDeep(1).selectLast().after();
    }

    private EndpointStrategy createMockEndpointStrategy(String pattern, boolean skip) {
        // the text based input may be property placeholders
        pattern = getContext().resolvePropertyPlaceholders(pattern);
        MockSendToEndpointStrategyFactory factory = getContext().getZwangineContextExtension()
                .getFactoryFinder(DEFAULT_PATH)
                .newInstance(MockSendToEndpointStrategyFactory.FACTORY, MockSendToEndpointStrategyFactory.class)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Cannot find MockSendToEndpointStrategyFactory on classpath. "
                                                                + "Add zwangine-mock to classpath."));
        return factory.mock(pattern, skip);
    }

}
