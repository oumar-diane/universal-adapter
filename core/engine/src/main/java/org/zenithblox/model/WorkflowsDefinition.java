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

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Endpoint;
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.builder.EndpointConsumerBuilder;
import org.zenithblox.spi.AsEndpointUri;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;
import org.zenithblox.support.OrderedComparator;
import org.zenithblox.util.OrderedLocationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.zenithblox.model.WorkflowDefinitionHelper.getWorkflowConfigurationDefinitionConsumer;
import static org.zenithblox.model.WorkflowDefinitionHelper.workflowsByIdOrPattern;

/**
 * A series of Zwangine workflows
 */
@Metadata(label = "configuration")
public class WorkflowsDefinition extends OptionalIdentifiedDefinition<WorkflowsDefinition>
        implements WorkflowContainer, ZwangineContextAware, ResourceAware {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowsDefinition.class);

    private List<InterceptDefinition> intercepts = new ArrayList<>();
    private List<InterceptFromDefinition> interceptFroms = new ArrayList<>();
    private List<InterceptSendToEndpointDefinition> interceptSendTos = new ArrayList<>();
    private List<OnExceptionDefinition> onExceptions = new ArrayList<>();
    private List<OnCompletionDefinition> onCompletions = new ArrayList<>();
    private ZwangineContext zwangineContext;
    private ErrorHandlerFactory errorHandlerFactory;
    private Resource resource;

    private List<WorkflowDefinition> workflows = new ArrayList<>();

    public WorkflowsDefinition() {
    }

    @Override
    public String toString() {
        return "Workflows: " + workflows;
    }

    @Override
    public String getShortName() {
        return "workflows";
    }

    @Override
    public String getLabel() {
        return "Workflows " + getId();
    }

    // Properties
    // -----------------------------------------------------------------------
    @Override
    public List<WorkflowDefinition> getWorkflows() {
        return workflows;
    }

    @Override
    public void setWorkflows(List<WorkflowDefinition> workflows) {
        this.workflows = workflows;
    }

    public List<InterceptFromDefinition> getInterceptFroms() {
        return interceptFroms;
    }

    public void setInterceptFroms(List<InterceptFromDefinition> interceptFroms) {
        this.interceptFroms = interceptFroms;
    }

    public List<InterceptSendToEndpointDefinition> getInterceptSendTos() {
        return interceptSendTos;
    }

    public void setInterceptSendTos(List<InterceptSendToEndpointDefinition> interceptSendTos) {
        this.interceptSendTos = interceptSendTos;
    }

    public List<InterceptDefinition> getIntercepts() {
        return intercepts;
    }

    public void setIntercepts(List<InterceptDefinition> intercepts) {
        this.intercepts = intercepts;
    }

    public List<OnExceptionDefinition> getOnExceptions() {
        return onExceptions;
    }

    public void setOnExceptions(List<OnExceptionDefinition> onExceptions) {
        this.onExceptions = onExceptions;
    }

    public List<OnCompletionDefinition> getOnCompletions() {
        return onCompletions;
    }

    public void setOnCompletions(List<OnCompletionDefinition> onCompletions) {
        this.onCompletions = onCompletions;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public ErrorHandlerFactory getErrorHandlerFactory() {
        return errorHandlerFactory;
    }

    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Creates a new workflow
     *
     * Prefer to use the from methods when creating a new workflow.
     *
     * @return the builder
     */
    public WorkflowDefinition workflow() {
        WorkflowDefinition workflow = createWorkflow();
        return workflow(workflow);
    }

    /**
     * Creates a new workflow from the given URI input
     *
     * @param  uri the from uri
     * @return     the builder
     */
    public WorkflowDefinition from(@AsEndpointUri String uri) {
        WorkflowDefinition workflow = createWorkflow();
        workflow.from(uri);
        return workflow(workflow);
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
        WorkflowDefinition workflow = createWorkflow();
        workflow.fromV(uri, variableReceive);
        return workflow(workflow);
    }

    /**
     * Creates a new workflow from the given endpoint
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(Endpoint endpoint) {
        WorkflowDefinition workflow = createWorkflow();
        workflow.from(endpoint);
        return workflow(workflow);
    }

    /**
     * Creates a new workflow from the given endpoint
     *
     * @param  endpoint the from endpoint
     * @return          the builder
     */
    public WorkflowDefinition from(EndpointConsumerBuilder endpoint) {
        WorkflowDefinition workflow = createWorkflow();
        workflow.from(endpoint);
        return workflow(workflow);
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
        WorkflowDefinition workflow = createWorkflow();
        workflow.fromV(endpoint, variableReceive);
        return workflow(workflow);
    }

    /**
     * Creates a new workflow using the given workflow.
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     *
     * @param  workflow the workflow
     * @return       the builder
     */
    public WorkflowDefinition workflow(WorkflowDefinition workflow) {
        // must set the error handler if not already set on the workflow
        ErrorHandlerFactory handler = getErrorHandlerFactory();
        if (handler != null) {
            workflow.setErrorHandlerFactoryIfNull(handler);
        }
        getWorkflows().add(workflow);
        return workflow;
    }

    public void prepareWorkflow(WorkflowDefinition workflow) {
        if (workflow.isPrepared()) {
            return;
        }

        // reset before preparing workflow
        workflow.resetPrepare();

        // remember the source resource
        workflow.setResource(resource);

        // merge global and workflow scoped together
        final AtomicReference<ErrorHandlerDefinition> gcErrorHandler = new AtomicReference<>();
        List<OnExceptionDefinition> oe = new ArrayList<>(onExceptions);
        List<InterceptDefinition> icp = new ArrayList<>(intercepts);
        List<InterceptFromDefinition> ifrom = new ArrayList<>(interceptFroms);
        List<InterceptSendToEndpointDefinition> ito = new ArrayList<>(interceptSendTos);
        List<OnCompletionDefinition> oc = new ArrayList<>(onCompletions);
        if (getZwangineContext() != null) {
            List<WorkflowConfigurationDefinition> globalConfigurations
                    = ((ModelZwangineContext) getZwangineContext()).getWorkflowConfigurationDefinitions();
            if (globalConfigurations != null) {
                String[] ids;
                if (workflow.getWorkflowConfigurationId() != null) {
                    // if the WorkflowConfigurationId was configured with property placeholder it should be resolved first
                    // and include properties sources from the template parameters
                    if (workflow.getTemplateParameters() != null && workflow.getWorkflowConfigurationId().startsWith("{{")) {
                        OrderedLocationProperties props = new OrderedLocationProperties();
                        props.putAll("TemplateProperties", new HashMap<>(workflow.getTemplateParameters()));
                        zwangineContext.getPropertiesComponent().setLocalProperties(props);
                        try {
                            ids = zwangineContext.getZwangineContextExtension()
                                    .resolvePropertyPlaceholders(workflow.getWorkflowConfigurationId(), true)
                                    .split(",");
                        } finally {
                            zwangineContext.getPropertiesComponent().setLocalProperties(null);
                        }
                    } else {
                        ids = workflow.getWorkflowConfigurationId().split(",");
                    }
                } else {
                    ids = new String[] { "*" };
                }

                // if there are multiple ids configured then we should apply in that same order
                for (String id : ids) {
                    // sort according to ordered
                    globalConfigurations.stream().sorted(OrderedComparator.get())
                            .filter(workflowsByIdOrPattern(workflow, id))
                            .forEach(getWorkflowConfigurationDefinitionConsumer(workflow, gcErrorHandler, oe, icp, ifrom, ito, oc));
                }

                // set error handler before prepare
                if (errorHandlerFactory == null && gcErrorHandler.get() != null) {
                    ErrorHandlerDefinition ehd = gcErrorHandler.get();
                    workflow.setErrorHandlerFactoryIfNull(ehd.getErrorHandlerType());
                }
            }
        }

        // if the workflow does not already have an error handler set then use workflow configured error handler
        // if one was configured
        ErrorHandlerDefinition ehd = null;
        if (errorHandlerFactory == null && gcErrorHandler.get() != null) {
            ehd = gcErrorHandler.get();
        }

        // must prepare the workflow before we can add it to the workflows list
        WorkflowDefinitionHelper.prepareWorkflow(getZwangineContext(), workflow, ehd, oe, icp, ifrom, ito, oc);

        if (LOG.isDebugEnabled() && workflow.getAppliedWorkflowConfigurationIds() != null) {
            LOG.debug("Workflow: {} is using workflow configurations ids: {}", workflow.getId(),
                    workflow.getAppliedWorkflowConfigurationIds());
        }

        // mark this workflow as prepared
        workflow.markPrepared();
    }

    /**
     * Creates and adds an interceptor that is triggered on every step in the workflow processing.
     *
     * @return the interceptor builder to configure
     */
    public InterceptDefinition intercept() {
        InterceptDefinition answer = new InterceptDefinition();
        getIntercepts().add(0, answer);
        return answer;
    }

    /**
     * Creates and adds an interceptor that is triggered when an exchange is received as input to any workflows (eg from
     * all the <tt>from</tt>)
     *
     * @return the interceptor builder to configure
     */
    public InterceptFromDefinition interceptFrom() {
        InterceptFromDefinition answer = new InterceptFromDefinition();
        getInterceptFroms().add(answer);
        return answer;
    }

    /**
     * Creates and adds an interceptor that is triggered when an exchange is received as input to the workflow defined with
     * the given endpoint (eg from the <tt>from</tt>)
     *
     * @param  uri uri of the endpoint
     * @return     the interceptor builder to configure
     */
    public InterceptFromDefinition interceptFrom(@AsEndpointUri final String uri) {
        InterceptFromDefinition answer = new InterceptFromDefinition(uri);
        getInterceptFroms().add(answer);
        return answer;
    }

    /**
     * Creates and adds an interceptor that is triggered when an exchange is send to the given endpoint
     *
     * @param  uri uri of the endpoint
     * @return     the builder
     */
    public InterceptSendToEndpointDefinition interceptSendToEndpoint(@AsEndpointUri final String uri) {
        InterceptSendToEndpointDefinition answer = new InterceptSendToEndpointDefinition(uri);
        getInterceptSendTos().add(answer);
        return answer;
    }

    /**
     * Adds an on exception
     *
     * @param  exception the exception
     * @return           the builder
     */
    public OnExceptionDefinition onException(Class<? extends Throwable> exception) {
        OnExceptionDefinition answer = new OnExceptionDefinition(exception);
        answer.setWorkflowScoped(false);
        getOnExceptions().add(answer);
        return answer;
    }

    /**
     * Adds an on completion
     *
     * @return the builder
     */
    public OnCompletionDefinition onCompletion() {
        OnCompletionDefinition answer = new OnCompletionDefinition();
        answer.setWorkflowScoped(false);
        getOnCompletions().add(answer);
        return answer;
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected WorkflowDefinition createWorkflow() {
        WorkflowDefinition workflow = new WorkflowDefinition();
        workflow.setZwangineContext(getZwangineContext());
        ErrorHandlerFactory handler = getErrorHandlerFactory();
        if (handler != null) {
            workflow.setErrorHandlerFactoryIfNull(handler);
        }
        if (resource != null) {
            workflow.setResource(resource);
        }
        return workflow;
    }
}
