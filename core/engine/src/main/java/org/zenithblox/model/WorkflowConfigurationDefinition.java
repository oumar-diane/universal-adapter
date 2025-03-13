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

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.model.errorhandler.RefErrorHandlerDefinition;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceAware;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Reusable configuration for Zwangine workflow(s).
 */
@Metadata(label = "configuration")
public class WorkflowConfigurationDefinition extends OptionalIdentifiedDefinition<WorkflowConfigurationDefinition>
        implements PreconditionContainer, ResourceAware {

    private Resource resource;
    private ErrorHandlerDefinition errorHandler;
    private List<InterceptDefinition> intercepts = new ArrayList<>();
    private List<InterceptFromDefinition> interceptFroms = new ArrayList<>();
    private List<InterceptSendToEndpointDefinition> interceptSendTos = new ArrayList<>();
    private List<OnExceptionDefinition> onExceptions = new ArrayList<>();
    private List<OnCompletionDefinition> onCompletions = new ArrayList<>();
    @Metadata(label = "advanced")
    private String precondition;

    public WorkflowConfigurationDefinition() {
    }

    @Override
    public String toString() {
        return "WorkflowsConfiguration: " + getId();
    }

    @Override
    public String getShortName() {
        return "workflowsConfiguration";
    }

    @Override
    public String getLabel() {
        return "WorkflowsConfiguration " + getId();
    }

    @Override
    public Resource getResource() {
        return resource;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public ErrorHandlerDefinition getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandlerDefinition errorHandler) {
        this.errorHandler = errorHandler;
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

    public List<InterceptDefinition> getIntercepts() {
        return intercepts;
    }

    public void setIntercepts(List<InterceptDefinition> intercepts) {
        this.intercepts = intercepts;
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

    /**
     * The predicate of the precondition in simple language to evaluate in order to determine if this workflow
     * configuration should be included or not.
     */
    @Override
    public String getPrecondition() {
        return precondition;
    }

    /**
     * The predicate of the precondition in simple language to evaluate in order to determine if this workflow
     * configuration should be included or not.
     */
    @Override
    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the error handler to use, for workflows that has not already been configured with an error handler.
     *
     * @param  ref reference to existing error handler
     * @return     the builder
     */
    public WorkflowConfigurationDefinition errorHandler(String ref) {
        ErrorHandlerDefinition def = new ErrorHandlerDefinition();
        def.setErrorHandlerType(new RefErrorHandlerDefinition(ref));
        setErrorHandler(def);
        return this;
    }

    /**
     * Sets the error handler to use, for workflows that has not already been configured with an error handler.
     *
     * @param  errorHandler the error handler
     * @return              the builder
     */
    public WorkflowConfigurationDefinition errorHandler(ErrorHandlerFactory errorHandler) {
        ErrorHandlerDefinition def = new ErrorHandlerDefinition();
        def.setErrorHandlerType(errorHandler);
        setErrorHandler(def);
        return this;
    }

    /**
     * Sets the predicate of the precondition in simple language to evaluate in order to determine if this workflow
     * configuration should be included or not.
     *
     * @param  precondition the predicate corresponding to the test to evaluate.
     * @return              the builder
     */
    public WorkflowConfigurationDefinition precondition(String precondition) {
        setPrecondition(precondition);
        return this;
    }

    /**
     * <a href="http://zwangine.zentihblox.org/exception-clause.html">Exception clause</a> for catching certain exceptions and
     * handling them.
     *
     * @param  exceptionType the exception to catch
     * @return               the exception builder to configure
     */
    public OnExceptionDefinition onException(Class<? extends Throwable> exceptionType) {
        OnExceptionDefinition answer = new OnExceptionDefinition(exceptionType);
        answer.setWorkflowConfiguration(this);
        onExceptions.add(answer);
        return answer;
    }

    /**
     * <a href="http://zwangine.zentihblox.org/exception-clause.html">Exception clause</a> for catching certain exceptions and
     * handling them.
     *
     * @param  exceptions list of exceptions to catch
     * @return            the exception builder to configure
     */
    @SafeVarargs
    public final OnExceptionDefinition onException(Class<? extends Throwable>... exceptions) {
        OnExceptionDefinition answer = new OnExceptionDefinition(Arrays.asList(exceptions));
        answer.setWorkflowConfiguration(this);
        onExceptions.add(answer);
        return answer;
    }

    /**
     * <a href="http://zwangine.zentihblox.org/oncompletion.html">On completion</a> callback for doing custom routing when the
     * {@link org.zenithblox.Exchange} is complete.
     *
     * @return the on completion builder to configure
     */
    public OnCompletionDefinition onCompletion() {
        OnCompletionDefinition answer = new OnCompletionDefinition();
        answer.setWorkflowConfiguration(this);
        // is global scoped by default
        answer.setWorkflowScoped(false);
        onCompletions.add(answer);
        return answer;
    }

    /**
     * Adds a workflow for an interceptor that intercepts every processing step.
     *
     * @return the builder
     */
    public InterceptDefinition intercept() {
        InterceptDefinition answer = new InterceptDefinition();
        answer.setWorkflowConfiguration(this);
        intercepts.add(answer);
        return answer;
    }

    /**
     * Adds a workflow for an interceptor that intercepts incoming messages on any inputs in this workflow
     *
     * @return the builder
     */
    public InterceptFromDefinition interceptFrom() {
        InterceptFromDefinition answer = new InterceptFromDefinition();
        answer.setWorkflowConfiguration(this);
        interceptFroms.add(answer);
        return answer;
    }

    /**
     * Adds a workflow for an interceptor that intercepts incoming messages on the given endpoint.
     *
     * @param  uri endpoint uri
     * @return     the builder
     */
    public InterceptFromDefinition interceptFrom(String uri) {
        InterceptFromDefinition answer = new InterceptFromDefinition(uri);
        answer.setWorkflowConfiguration(this);
        interceptFroms.add(answer);
        return answer;
    }

    /**
     * Applies a workflow for an interceptor if an exchange is send to the given endpoint
     *
     * @param  uri endpoint uri
     * @return     the builder
     */
    public InterceptSendToEndpointDefinition interceptSendToEndpoint(String uri) {
        InterceptSendToEndpointDefinition answer = new InterceptSendToEndpointDefinition(uri);
        answer.setWorkflowConfiguration(this);
        interceptSendTos.add(answer);
        return answer;
    }

}
