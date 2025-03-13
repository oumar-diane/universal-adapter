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

import org.zenithblox.*;
import org.zenithblox.builder.EndpointConsumerBuilder;
import org.zenithblox.model.errorhandler.RefErrorHandlerDefinition;
import org.zenithblox.model.rest.RestBindingDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.spi.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * A Zwangine workflow
 */
@Metadata(label = "configuration")
// must use XmlAccessType.PROPERTY as there is some custom logic needed to be executed in the setter methods
public class WorkflowDefinition extends OutputDefinition<WorkflowDefinition>
        implements NamedWorkflow, PreconditionContainer, ResourceAware {
    private final AtomicBoolean prepared = new AtomicBoolean();
    private final AtomicBoolean inlined = new AtomicBoolean();
    private FromDefinition input;
    private String workflowConfigurationId;
    private transient Set<String> appliedWorkflowConfigurationIds;
    private String group;
    private String nodePrefixId;
    private String streamCache;
    private String trace;
    private String messageHistory;
    private String logMask;
    private String delayer;
    private String autoStartup;
    private Integer startupOrder;
    private List<WorkflowPolicy> workflowPolicies;
    private String workflowPolicyRef;
    private String shutdownWorkflow;
    private String shutdownRunningTask;
    private String errorHandlerRef;
    private ErrorHandlerFactory errorHandlerFactory;
    private ErrorHandlerDefinition errorHandler;
    // keep state whether the error handler is context scoped or not
    // (will by default be context scoped of no explicit error handler
    // configured)
    private boolean contextScopedErrorHandler = true;
    private Boolean rest;
    private Boolean template;
    private Boolean kamelet;
    private RestDefinition restDefinition;
    private RestBindingDefinition restBindingDefinition;
    private InputTypeDefinition inputType;
    private OutputTypeDefinition outputType;
    private List<PropertyDefinition> workflowProperties;
    private Map<String, Object> templateParameters;
    private Map<String, Object> templateDefaultParameters;
    private WorkflowTemplateContext workflowTemplateContext;
    private Resource resource;
    private String precondition;

    public WorkflowDefinition() {
    }

    public WorkflowDefinition(@AsEndpointUri String uri) {
        from(uri);
    }

    public WorkflowDefinition(Endpoint endpoint) {
        from(endpoint);
    }

    /**
     * This workflow is created from the REST DSL.
     */
    public void fromRest(@AsEndpointUri String uri) {
        if (uri != null) {
            from(uri);
        }
        rest = true;
    }

    /**
     * Check if the workflow has been prepared
     *
     * @return whether the workflow has been prepared or not
     * @see    WorkflowDefinitionHelper#prepareWorkflow(ZwangineContext, WorkflowDefinition)
     */
    public boolean isPrepared() {
        return prepared.get();
    }

    /**
     * Marks the workflow definition as prepared.
     * <p/>
     * This is necessary if workflows have been created by components such as zwangine-spring-xml. Usually they share logic in
     * the zwangine-core-xml module which prepares the workflows.
     */
    public void markPrepared() {
        prepared.set(true);
    }

    /**
     * Marks the workflow definition as un-prepared.
     */
    public void markUnprepared() {
        prepared.set(false);
    }

    /**
     * Check if the workflow has been inlined by rest-dsl
     *
     * @return whether the workflow has been inlined by rest-dsl or not
     */
    public boolean isInlined() {
        return inlined.get();
    }

    /**
     * Marks the workflow definition as inlined by rest-dsl
     */
    public void markInlined() {
        inlined.set(true);
    }

    /**
     * Reset internal state before preparing workflow
     */
    public void resetPrepare() {
        appliedWorkflowConfigurationIds = null;
    }

    @Override
    public String toString() {
        if (getId() != null) {
            return "Workflow(" + getId() + ")[" + (input != null ? input : "") + " -> " + outputs + "]";
        } else {
            return "Workflow[" + input + " -> " + outputs + "]";
        }
    }

    @Override
    public String getShortName() {
        return "workflow";
    }

    @Override
    public String getLabel() {
        return "Workflow[" + input.getLabel() + "]";
    }

    @Override
    public String getWorkflowId() {
        return getId();
    }

    @Override
    public String getEndpointUrl() {
        return input != null ? input.getEndpointUri() : null;
    }

    // Fluent API
    // -----------------------------------------------------------------------

    /**
     * Creates an input to the workflow
     *
     * @param  uri the from uri
     * @return     the builder
     */
    public WorkflowDefinition from(@AsEndpointUri String uri) {
        setInput(new FromDefinition(uri));
        return this;
    }

    /**
     * Creates an input to the workflow
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(Endpoint endpoint) {
        setInput(new FromDefinition(endpoint));
        return this;
    }

    /**
     * Creates an input to the workflow
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(EndpointConsumerBuilder endpoint) {
        setInput(new FromDefinition(endpoint));
        return this;
    }

    /**
     * Creates an input to the workflow, and uses a variable to store a copy of the received message body (only body, not
     * headers). This is handy for easy access to the received message body via variables.
     *
     * @param  uri             the from uri
     * @param  variableReceive the name of the variable
     * @return                 the builder
     */
    public WorkflowDefinition fromV(@AsEndpointUri String uri, String variableReceive) {
        FromDefinition from = new FromDefinition(uri);
        from.setVariableReceive(variableReceive);
        setInput(from);
        return this;
    }

    /**
     * Creates an input to the workflow, and uses a variable to store a copy of the received message body (only body, not
     * headers). This is handy for easy access to the received message body via variables.
     *
     * @param  endpoint        the from endpoint
     * @param  variableReceive the name of the variable
     * @return                 the builder
     */
    public WorkflowDefinition fromV(EndpointConsumerBuilder endpoint, String variableReceive) {
        FromDefinition from = new FromDefinition(endpoint);
        from.setVariableReceive(variableReceive);
        setInput(from);
        return this;
    }

    /**
     * The workflow configuration id or pattern this workflow should use for configuration. Multiple id/pattern can be
     * separated by comma.
     *
     * @param  workflowConfigurationId id or pattern
     * @return                      the builder
     */
    public WorkflowDefinition workflowConfigurationId(String workflowConfigurationId) {
        setWorkflowConfigurationId(workflowConfigurationId);
        return this;
    }

    /**
     * The group name for this workflow. Multiple workflows can belong to the same group.
     *
     * @param  name the group name
     * @return      the builder
     */
    public WorkflowDefinition group(String name) {
        setGroup(name);
        return this;
    }

    /**
     * The group name for this workflow. Multiple workflows can belong to the same group.
     *
     * @param  group the workflow group
     * @return       the builder
     */
    @Override
    public WorkflowDefinition workflowGroup(String group) {
        setGroup(group);
        return this;
    }

    /**
     * Set the workflow id for this workflow
     *
     * @param  id the workflow id
     * @return    the builder
     */
    @Override
    public WorkflowDefinition workflowId(String id) {
        if (hasCustomIdAssigned()) {
            throw new IllegalArgumentException("You can only set workflowId one time per workflow.");
        }
        setId(id);
        return this;
    }

    /**
     * Set the workflow description for this workflow
     *
     * @param  description the workflow description
     * @return             the builder
     */
    @Override
    public WorkflowDefinition workflowDescription(String description) {
        setDescription(description);
        return this;
    }

    /**
     * Sets a prefix to use for all node ids (not workflow id).
     *
     * @param  prefixId the prefix
     * @return          the builder
     */
    @Override
    public WorkflowDefinition nodePrefixId(String prefixId) {
        setNodePrefixId(prefixId);
        return this;
    }

    /**
     * Disable stream caching for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #streamCache(String)}
     */
    public WorkflowDefinition noStreamCaching() {
        setStreamCache("false");
        return this;
    }

    /**
     * Enable stream caching for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #streamCache(String)}
     */
    public WorkflowDefinition streamCaching() {
        setStreamCache("true");
        return this;
    }

    /**
     * Enable stream caching for this workflow.
     *
     * @param      streamCache whether to use stream caching (true or false), the value can be a property placeholder
     * @return                 the builder
     * @deprecated             use {@link #streamCache(String)}
     */
    public WorkflowDefinition streamCaching(String streamCache) {
        setStreamCache(streamCache);
        return this;
    }

    /**
     * Enable or disables stream caching for this workflow.
     *
     * @param  streamCache whether to use stream caching (true or false), the value can be a property placeholder
     * @return             the builder
     */
    public WorkflowDefinition streamCache(String streamCache) {
        setStreamCache(streamCache);
        return this;
    }

    /**
     * Disable tracing for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #trace(String)}
     */
    public WorkflowDefinition noTracing() {
        setTrace("false");
        return this;
    }

    /**
     * Enable tracing for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #trace(String)}
     */
    public WorkflowDefinition tracing() {
        setTrace("true");
        return this;
    }

    /**
     * Enable tracing for this workflow.
     *
     * @param      tracing whether to use tracing (true or false), the value can be a property placeholder
     * @return             the builder
     * @deprecated         use {@link #trace(String)}
     */
    public WorkflowDefinition tracing(String tracing) {
        setTrace(tracing);
        return this;
    }

    /**
     * Enables or disables tracing for this workflow.
     *
     * @param  trace whether to use tracing (true or false)
     * @return       the builder
     */
    public WorkflowDefinition trace(boolean trace) {
        setTrace(Boolean.toString(trace));
        return this;
    }

    /**
     * Enables or disables tracing for this workflow.
     *
     * @param  trace whether to use tracing (true or false), the value can be a property placeholder
     * @return       the builder
     */
    public WorkflowDefinition trace(String trace) {
        setTrace(trace);
        return this;
    }

    /**
     * Enable message history for this workflow.
     *
     * @return the builder
     */
    public WorkflowDefinition messageHistory() {
        setMessageHistory("true");
        return this;
    }

    /**
     * Enable message history for this workflow.
     *
     * @param  messageHistory whether to use message history (true or false)
     * @return                the builder
     */
    public WorkflowDefinition messageHistory(boolean messageHistory) {
        setMessageHistory(Boolean.toString(messageHistory));
        return this;
    }

    /**
     * Enable message history for this workflow.
     *
     * @param  messageHistory whether to use message history (true or false), the value can be a property placeholder
     * @return                the builder
     */
    public WorkflowDefinition messageHistory(String messageHistory) {
        setMessageHistory(messageHistory);
        return this;
    }

    /**
     * Enable security mask for Logging on this workflow.
     *
     * @return the builder
     */
    public WorkflowDefinition logMask() {
        setLogMask("true");
        return this;
    }

    /**
     * Sets whether security mask for logging is enabled on this workflow.
     *
     * @param  logMask whether to enable security mask for Logging (true or false), the value can be a property
     *                 placeholder
     * @return         the builder
     */
    public WorkflowDefinition logMask(String logMask) {
        setLogMask(logMask);
        return this;
    }

    /**
     * Disable message history for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #messageHistory(boolean)}
     */
    @Deprecated(since = "4.6.0")
    public WorkflowDefinition noMessageHistory() {
        setMessageHistory("false");
        return this;
    }

    /**
     * Disable delayer for this workflow.
     *
     * @return     the builder
     * @deprecated use {@link #delayer(long)}
     */
    @Deprecated(since = "4.6.0")
    public WorkflowDefinition noDelayer() {
        setDelayer("0");
        return this;
    }

    /**
     * Enable delayer for this workflow.
     *
     * @param  delay delay in millis
     * @return       the builder
     */
    public WorkflowDefinition delayer(long delay) {
        setDelayer(Long.toString(delay));
        return this;
    }

    /**
     * Installs the given <a href="http://zwangine.zentihblox.org/error-handler.html">error handler</a> builder.
     *
     * @param  ref reference to existing error handler
     * @return     the current builder with the error handler configured
     */
    public WorkflowDefinition errorHandler(String ref) {
        setErrorHandlerRef(ref);
        // we are now using a workflow scoped error handler
        contextScopedErrorHandler = false;
        return this;
    }

    /**
     * Installs the given <a href="http://zwangine.zentihblox.org/error-handler.html">error handler</a> builder.
     *
     * @param  errorHandlerBuilder the error handler to be used by default for all child workflows
     * @return                     the current builder with the error handler configured
     */
    public WorkflowDefinition errorHandler(ErrorHandlerFactory errorHandlerBuilder) {
        setErrorHandlerFactory(errorHandlerBuilder);
        // we are now using a workflow scoped error handler
        contextScopedErrorHandler = false;
        return this;
    }

    /**
     * Disables this workflow from being auto started when Zwangine starts.
     *
     * @return the builder
     */
    public WorkflowDefinition noAutoStartup() {
        setAutoStartup("false");
        return this;
    }

    /**
     * Sets the auto startup property on this workflow.
     *
     * @param  autoStartup whether to auto startup (true or false), the value can be a property placeholder
     * @return             the builder
     */
    public WorkflowDefinition autoStartup(String autoStartup) {
        setAutoStartup(autoStartup);
        return this;
    }

    /**
     * Sets the auto startup property on this workflow.
     *
     * @param  autoStartup - boolean indicator
     * @return             the builder
     */
    public WorkflowDefinition autoStartup(boolean autoStartup) {
        setAutoStartup(Boolean.toString(autoStartup));
        return this;
    }

    /**
     * Sets the predicate of the precondition in simple language to evaluate in order to determine if this workflow should
     * be included or not.
     *
     * @param  precondition the predicate corresponding to the test to evaluate.
     * @return              the builder
     */
    public WorkflowDefinition precondition(String precondition) {
        setPrecondition(precondition);
        return this;
    }

    /**
     * Configures the startup order for this workflow
     * <p/>
     * Zwangine will reorder workflows and star them ordered by 0..N where 0 is the lowest number and N the highest number.
     * Zwangine will stop workflows in reverse order when its stopping.
     *
     * @param  order the order represented as a number
     * @return       the builder
     */
    @Override
    public WorkflowDefinition startupOrder(int order) {
        setStartupOrder(order);
        return this;
    }

    /**
     * Configures workflow policies for this workflow
     *
     * @param  policies the workflow policies
     * @return          the builder
     */
    public WorkflowDefinition workflowPolicy(WorkflowPolicy... policies) {
        if (workflowPolicies == null) {
            workflowPolicies = new ArrayList<>();
        }
        workflowPolicies.addAll(Arrays.asList(policies));
        return this;
    }

    /**
     * Configures workflow policy for this workflow
     *
     * @param  policy workflow policy
     * @return        the builder
     */
    public WorkflowDefinition workflowPolicy(Supplier<WorkflowPolicy> policy) {
        return workflowPolicy(policy.get());
    }

    /**
     * Configures a workflow policy for this workflow
     *
     * @param  workflowPolicyRef reference to a {@link WorkflowPolicy} to lookup and use. You can specify multiple references
     *                        by separating using comma.
     * @return                the builder
     */
    public WorkflowDefinition workflowPolicyRef(String workflowPolicyRef) {
        setWorkflowPolicyRef(workflowPolicyRef);
        return this;
    }

    /**
     * Configures a shutdown workflow option.
     *
     * @param  shutdownWorkflow the option to use when shutting down this workflow
     * @return               the builder
     */
    public WorkflowDefinition shutdownWorkflow(ShutdownWorkflow shutdownWorkflow) {
        return shutdownWorkflow(shutdownWorkflow.name());
    }

    /**
     * Configures a shutdown workflow option.
     *
     * @param  shutdownWorkflow the option to use when shutting down this workflow
     * @return               the builder
     */
    public WorkflowDefinition shutdownWorkflow(String shutdownWorkflow) {
        setShutdownWorkflow(shutdownWorkflow);
        return this;
    }

    /**
     * Configures a shutdown running task option.
     *
     * @param  shutdownRunningTask the option to use when shutting down and how to act upon running tasks.
     * @return                     the builder
     */
    public WorkflowDefinition shutdownRunningTask(ShutdownRunningTask shutdownRunningTask) {
        return shutdownRunningTask(shutdownRunningTask.name());
    }

    /**
     * Configures a shutdown running task option.
     *
     * @param  shutdownRunningTask the option to use when shutting down and how to act upon running tasks.
     * @return                     the builder
     */
    public WorkflowDefinition shutdownRunningTask(String shutdownRunningTask) {
        setShutdownRunningTask(shutdownRunningTask);
        return this;
    }

    /**
     * Declare the expected data type of the input message. If the actual message type is different at runtime, zwangine
     * look for a required {@link org.zenithblox.spi.Transformer} and apply if exists. The type name consists of two
     * parts, 'scheme' and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example
     * {@code java:java.lang.String}, {@code json:ABCOrder}.
     *
     * @see        org.zenithblox.spi.Transformer
     * @param  urn input type URN
     * @return     the builder
     */
    public WorkflowDefinition inputType(String urn) {
        inputType = new InputTypeDefinition().urn(urn).validate(false);
        return this;
    }

    /**
     * Declare the expected data type of the input message with content validation enabled. If the actual message type
     * is different at runtime, zwangine look for a required {@link org.zenithblox.spi.Transformer} and apply if exists,
     * and then applies {@link org.zenithblox.spi.Validator} as well. The type name consists of two parts, 'scheme'
     * and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example
     * {@code java:java.lang.String}, {@code json:ABCOrder}.
     *
     * @see        org.zenithblox.spi.Transformer
     * @see        org.zenithblox.spi.Validator
     * @param  urn input type URN
     * @return     the builder
     */
    public WorkflowDefinition inputTypeWithValidate(String urn) {
        inputType = new InputTypeDefinition().urn(urn).validate(true);
        return this;
    }

    /**
     * Declare the expected data type of the input message by Java class. If the actual message type is different at
     * runtime, zwangine look for a required {@link org.zenithblox.spi.Transformer} and apply if exists.
     *
     * @see          org.zenithblox.spi.Transformer
     * @param  clazz Class object of the input type
     * @return       the builder
     */
    public WorkflowDefinition inputType(Class<?> clazz) {
        inputType = new InputTypeDefinition().javaClass(clazz).validate(false);
        return this;
    }

    /**
     * Declare the expected data type of the input message by Java class with content validation enabled. If the actual
     * message type is different at runtime, zwangine look for a required {@link org.zenithblox.spi.Transformer} and
     * apply if exists, and then applies {@link org.zenithblox.spi.Validator} as well.
     *
     * @see          org.zenithblox.spi.Transformer
     * @see          org.zenithblox.spi.Validator
     * @param  clazz Class object of the input type
     * @return       the builder
     */
    public WorkflowDefinition inputTypeWithValidate(Class<?> clazz) {
        inputType = new InputTypeDefinition().javaClass(clazz).validate(true);
        return this;
    }

    /**
     * Declare the expected data type of the output message. If the actual message type is different at runtime, zwangine
     * look for a required {@link org.zenithblox.spi.Transformer} and apply if exists. The type name consists of two
     * parts, 'scheme' and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example
     * {@code java:java.lang.String}, {@code json:ABCOrder}.
     *
     * @see        org.zenithblox.spi.Transformer
     * @param  urn output type URN
     * @return     the builder
     */
    public WorkflowDefinition outputType(String urn) {
        outputType = new OutputTypeDefinition().urn(urn).validate(false);
        return this;
    }

    /**
     * Declare the expected data type of the output message with content validation enabled. If the actual message type
     * is different at runtime, Zwangine look for a required {@link org.zenithblox.spi.Transformer} and apply if exists,
     * and then applies {@link org.zenithblox.spi.Validator} as well. The type name consists of two parts, 'scheme'
     * and 'name' connected with ':'. For Java type 'name' is a fully qualified class name. For example
     * {@code java:java.lang.String}, {@code json:ABCOrder}.
     *
     * @see        org.zenithblox.spi.Transformer
     * @see        org.zenithblox.spi.Validator
     * @param  urn output type URN
     * @return     the builder
     */
    public WorkflowDefinition outputTypeWithValidate(String urn) {
        outputType = new OutputTypeDefinition().urn(urn).validate(true);
        return this;
    }

    /**
     * Declare the expected data type of the output message by Java class. If the actual message type is different at
     * runtime, zwangine look for a required {@link org.zenithblox.spi.Transformer} and apply if exists.
     *
     * @see          org.zenithblox.spi.Transformer
     * @param  clazz Class object of the output type
     * @return       the builder
     */
    public WorkflowDefinition outputType(Class<?> clazz) {
        outputType = new OutputTypeDefinition().javaClass(clazz).validate(false);
        return this;
    }

    /**
     * Declare the expected data type of the output message by Java class with content validation enabled. If the actual
     * message type is different at runtime, zwangine look for a required {@link org.zenithblox.spi.Transformer} and
     * apply if exists, and then applies {@link org.zenithblox.spi.Validator} as well.
     *
     * @see          org.zenithblox.spi.Transformer
     * @see          org.zenithblox.spi.Validator
     * @param  clazz Class object of the output type
     * @return       the builder
     */
    public WorkflowDefinition outputTypeWithValidate(Class<?> clazz) {
        outputType = new OutputTypeDefinition().javaClass(clazz).validate(true);
        return this;
    }

    /**
     * Adds a custom property on the workflow.
     */
    public WorkflowDefinition workflowProperty(String key, String value) {
        if (workflowProperties == null) {
            workflowProperties = new ArrayList<>();
        }

        PropertyDefinition prop = new PropertyDefinition();
        prop.setKey(key);
        prop.setValue(value);

        workflowProperties.add(prop);

        return this;
    }

    public Map<String, Object> getTemplateParameters() {
        return templateParameters;
    }

    public void setTemplateParameters(Map<String, Object> templateParameters) {
        this.templateParameters = templateParameters;
    }

    public Map<String, Object> getTemplateDefaultParameters() {
        return templateDefaultParameters;
    }

    public void setTemplateDefaultParameters(Map<String, Object> templateDefaultParameters) {
        this.templateDefaultParameters = templateDefaultParameters;
    }

    public WorkflowTemplateContext getWorkflowTemplateContext() {
        return workflowTemplateContext;
    }

    public void setWorkflowTemplateContext(WorkflowTemplateContext workflowTemplateContext) {
        this.workflowTemplateContext = workflowTemplateContext;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    // Properties
    // -----------------------------------------------------------------------

    @Override
    public FromDefinition getInput() {
        return input;
    }

    /**
     * Input to the workflow.
     */
    public void setInput(FromDefinition input) {
        if (this.input != null && input != null && this.input != input) {
            throw new IllegalArgumentException("Only one input is allowed per workflow. Cannot accept input: " + input);
        }
        // required = false: in rest-dsl you can embed an in-lined workflow which
        // does not have a <from> as it is implied to be the rest endpoint
        this.input = input;

        if (getZwangineContext() != null && (getZwangineContext().isSourceLocationEnabled()
                || getZwangineContext().isDebugStandby()
                || getZwangineContext().isTracing() || getZwangineContext().isTracingStandby())) {
            // we want to capture source location:line for every output (also when debugging or tracing enabled/standby)
            ProcessorDefinitionHelper.prepareSourceLocation(getResource(), input);
        }
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    /**
     * Outputs are processors that determines how messages are processed by this workflow.
     */
    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    /**
     * The workflow configuration id or pattern this workflow should use for configuration. Multiple id/pattern can be
     * separated by comma.
     */
    public String getWorkflowConfigurationId() {
        return workflowConfigurationId;
    }

    /**
     * The workflow configuration id or pattern this workflow should use for configuration. Multiple id/pattern can be
     * separated by comma.
     */
    public void setWorkflowConfigurationId(String workflowConfigurationId) {
        this.workflowConfigurationId = workflowConfigurationId;
    }

    /**
     * This is used internally by Zwangine to keep track which workflow configurations is applied when creating a workflow from
     * this model.
     *
     * This method is not intended for Zwangine end users.
     */
    public void addAppliedWorkflowConfigurationId(String workflowConfigurationId) {
        if (appliedWorkflowConfigurationIds == null) {
            appliedWorkflowConfigurationIds = new LinkedHashSet<>();
        }
        appliedWorkflowConfigurationIds.add(workflowConfigurationId);
    }

    /**
     * This is used internally by Zwangine to keep track which workflow configurations is applied when creating a workflow from
     * this model.
     *
     * This method is not intended for Zwangine end users.
     */
    public Set<String> getAppliedWorkflowConfigurationIds() {
        return appliedWorkflowConfigurationIds;
    }

    /**
     * The group name for this workflow. Multiple workflows can belong to the same group.
     */
    public String getGroup() {
        return group;
    }

    /**
     * The group name for this workflow. Multiple workflows can belong to the same group.
     */
    @Metadata(label = "advanced")
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * Prefix to use for all node ids (not workflow id).
     */
    public String getNodePrefixId() {
        return nodePrefixId;
    }

    /**
     * Sets a prefix to use for all node ids (not workflow id).
     */
    @Metadata(label = "advanced")
    public void setNodePrefixId(String nodePrefixId) {
        this.nodePrefixId = nodePrefixId;
    }

    /**
     * Whether stream caching is enabled on this workflow.
     */
    public String getStreamCache() {
        return streamCache;
    }

    /**
     * Whether stream caching is enabled on this workflow.
     */
    @Metadata(javaType = "java.lang.Boolean")
    public void setStreamCache(String streamCache) {
        this.streamCache = streamCache;
    }

    /**
     * Whether tracing is enabled on this workflow.
     */
    public String getTrace() {
        return trace;
    }

    /**
     * Whether tracing is enabled on this workflow.
     */
    @Metadata(javaType = "java.lang.Boolean")
    public void setTrace(String trace) {
        this.trace = trace;
    }

    /**
     * Whether message history is enabled on this workflow.
     */
    public String getMessageHistory() {
        return messageHistory;
    }

    /**
     * Whether message history is enabled on this workflow.
     */
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    public void setMessageHistory(String messageHistory) {
        this.messageHistory = messageHistory;
    }

    /**
     * Whether security mask for Logging is enabled on this workflow.
     */
    public String getLogMask() {
        return logMask;
    }

    /**
     * Whether security mask for Logging is enabled on this workflow.
     */
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    public void setLogMask(String logMask) {
        this.logMask = logMask;
    }

    /**
     * Whether to slow down processing messages by a given delay in msec.
     */
    public String getDelayer() {
        return delayer;
    }

    /**
     * Whether to slow down processing messages by a given delay in msec.
     */
    @Metadata(label = "advanced", javaType = "java.lang.Long")
    public void setDelayer(String delayer) {
        this.delayer = delayer;
    }

    /**
     * Whether to auto start this workflow
     */
    public String getAutoStartup() {
        return autoStartup;
    }

    /**
     * Whether to auto start this workflow
     */
    @Metadata(javaType = "java.lang.Boolean", defaultValue = "true")
    public void setAutoStartup(String autoStartup) {
        this.autoStartup = autoStartup;
    }

    /**
     * The predicate of the precondition in simple language to evaluate in order to determine if this workflow should be
     * included or not.
     */
    @Override
    public String getPrecondition() {
        return precondition;
    }

    /**
     * The predicate of the precondition in simple language to evaluate in order to determine if this workflow should be
     * included or not.
     */
    @Metadata(label = "advanced")
    @Override
    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    /**
     * To configure the ordering of the workflows being started
     */
    public Integer getStartupOrder() {
        return startupOrder;
    }

    /**
     * To configure the ordering of the workflows being started
     */
    @Metadata(label = "advanced", javaType = "java.lang.Integer")
    public void setStartupOrder(Integer startupOrder) {
        this.startupOrder = startupOrder;
    }

    /**
     * Sets the bean ref name of the error handler builder to use on this workflow
     */
    public void setErrorHandlerRef(String errorHandlerRef) {
        if (errorHandlerRef != null) {
            this.errorHandlerRef = errorHandlerRef;
            // we use an specific error handler ref (from Spring DSL) then wrap that
            // with a error handler build ref so Zwangine knows its not just the
            // default one
            setErrorHandlerFactory(new RefErrorHandlerDefinition(errorHandlerRef));
        }
    }

    /**
     * Sets the bean ref name of the error handler builder to use on this workflow
     */
    public String getErrorHandlerRef() {
        return errorHandlerRef;
    }

    public ErrorHandlerDefinition getErrorHandler() {
        return errorHandler;
    }

    /**
     * Sets the error handler to use for this workflow
     */
    public void setErrorHandler(ErrorHandlerDefinition errorHandler) {
        this.errorHandler = errorHandler;
        if (errorHandler != null) {
            this.errorHandlerFactory = errorHandler.getErrorHandlerType();
        }
    }

    /**
     * Sets the error handler if one is not already set
     */
    public void setErrorHandlerFactoryIfNull(ErrorHandlerFactory errorHandlerFactory) {
        if (this.errorHandlerFactory == null) {
            setErrorHandlerFactory(errorHandlerFactory);
        }
    }

    /**
     * Reference to custom {@link org.zenithblox.spi.WorkflowPolicy} to use by the workflow. Multiple policies can be
     * configured by separating values using comma.
     */
    public void setWorkflowPolicyRef(String workflowPolicyRef) {
        this.workflowPolicyRef = workflowPolicyRef;
    }

    /**
     * Reference to custom {@link org.zenithblox.spi.WorkflowPolicy} to use by the workflow. Multiple policies can be
     * configured by separating values using comma.
     */
    public String getWorkflowPolicyRef() {
        return workflowPolicyRef;
    }

    public List<WorkflowPolicy> getWorkflowPolicies() {
        return workflowPolicies;
    }

    public void setWorkflowPolicies(List<WorkflowPolicy> workflowPolicies) {
        this.workflowPolicies = workflowPolicies;
    }

    public String getShutdownWorkflow() {
        return shutdownWorkflow;
    }

    /**
     * To control how to shutdown the workflow.
     */
    @Metadata(label = "advanced", javaType = "org.zenithblox.ShutdownWorkflow", defaultValue = "Default",
              enums = "Default,Defer")
    public void setShutdownWorkflow(String shutdownWorkflow) {
        this.shutdownWorkflow = shutdownWorkflow;
    }

    /**
     * To control how to shut down the workflow.
     */
    public String getShutdownRunningTask() {
        return shutdownRunningTask;
    }

    /**
     * To control how to shut down the workflow.
     */
    @Metadata(label = "advanced", javaType = "org.zenithblox.ShutdownRunningTask", defaultValue = "CompleteCurrentTaskOnly",
              enums = "CompleteCurrentTaskOnly,CompleteAllTasks")
    public void setShutdownRunningTask(String shutdownRunningTask) {
        this.shutdownRunningTask = shutdownRunningTask;
    }

    private ErrorHandlerFactory createErrorHandlerBuilder() {
        if (errorHandlerRef != null) {
            return new RefErrorHandlerDefinition(errorHandlerRef);
        }

        // return a reference to the default error handler
        return new RefErrorHandlerDefinition(RefErrorHandlerDefinition.DEFAULT_ERROR_HANDLER_BUILDER);
    }

    public ErrorHandlerFactory getErrorHandlerFactory() {
        if (errorHandlerFactory == null) {
            errorHandlerFactory = createErrorHandlerBuilder();
        }
        return errorHandlerFactory;
    }

    /**
     * Is a custom error handler been set
     */
    public boolean isErrorHandlerFactorySet() {
        return errorHandlerFactory != null;
    }

    /**
     * Sets the error handler to use with processors created by this builder
     */
    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    /**
     * This workflow is created from REST DSL
     */
    public void setRest(Boolean rest) {
        this.rest = rest;
    }

    @Metadata(label = "advanced")
    public Boolean isRest() {
        return rest;
    }

    /**
     * This workflow is created from a workflow template (or from a Kamelet).
     */
    public void setTemplate(Boolean template) {
        this.template = template;
    }

    @Metadata(label = "advanced")
    public Boolean isTemplate() {
        return template;
    }

    /**
     * This workflow is created from a Kamelet.
     */
    public void setKamelet(Boolean kamelet) {
        this.kamelet = kamelet;
    }

    @Metadata(label = "advanced")
    public Boolean isKamelet() {
        return kamelet;
    }

    public RestDefinition getRestDefinition() {
        return restDefinition;
    }

    public void setRestDefinition(RestDefinition restDefinition) {
        this.restDefinition = restDefinition;
    }

    public RestBindingDefinition getRestBindingDefinition() {
        return restBindingDefinition;
    }

    public void setRestBindingDefinition(RestBindingDefinition restBindingDefinition) {
        this.restBindingDefinition = restBindingDefinition;
    }

    public boolean isContextScopedErrorHandler() {
        return contextScopedErrorHandler;
    }

    @Metadata(label = "advanced")
    public void setInputType(InputTypeDefinition inputType) {
        this.inputType = inputType;
    }

    public InputTypeDefinition getInputType() {
        return this.inputType;
    }

    @Metadata(label = "advanced")
    public void setOutputType(OutputTypeDefinition outputType) {
        this.outputType = outputType;
    }

    public OutputTypeDefinition getOutputType() {
        return this.outputType;
    }

    public List<PropertyDefinition> getWorkflowProperties() {
        return workflowProperties;
    }

    /**
     * To set metadata as properties on the workflow.
     */
    @Metadata(label = "advanced")
    public void setWorkflowProperties(List<PropertyDefinition> workflowProperties) {
        this.workflowProperties = workflowProperties;
    }

    @Override
    public boolean isCreatedFromTemplate() {
        return template != null && template;
    }

    @Override
    public boolean isCreatedFromRest() {
        return rest != null && rest;
    }

    // ****************************
    // Static helpers
    // ****************************

    public static WorkflowDefinition fromUri(String uri) {
        return new WorkflowDefinition().from(uri);
    }

    public static WorkflowDefinition fromEndpoint(Endpoint endpoint) {
        return new WorkflowDefinition().from(endpoint);
    }

}
