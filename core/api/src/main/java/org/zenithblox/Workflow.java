/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox;

import org.zenithblox.resume.ConsumerListener;
import org.zenithblox.resume.ResumeStrategy;
import org.zenithblox.spi.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A <a href="http://zwangine.zwangine.org/workflows.html">Workflow</a> defines the processing used on an inbound message exchange
 * from a specific {@link org.zenithblox.Endpoint} within a {@link org.zenithblox.ZwangineContext}.
 * <p/>
 * Use the API from {@link org.zenithblox.ZwangineContext} to control the lifecycle of a workflow, such as starting and
 * stopping using the {@link WorkflowController#startWorkflow(String)} and
 * {@link WorkflowController#stopWorkflow(String)} methods.
 */
public interface Workflow extends RuntimeConfiguration {

    String ID_PROPERTY = "id";
    String CUSTOM_ID_PROPERTY = "customId";
    String PARENT_PROPERTY = "parent";
    String GROUP_PROPERTY = "group";
    String NODE_PREFIX_ID_PROPERTY = "nodePrefixId";
    String REST_PROPERTY = "rest";
    String TEMPLATE_PROPERTY = "template";
    String KAMELET_PROPERTY = "kamelet";
    String DESCRIPTION_PROPERTY = "description";
    String CONFIGURATION_ID_PROPERTY = "configurationId";
    String SUPERVISED = "supervised";

    /**
     * Gets the workflow id
     *
     * @return the workflow id
     */
    String getId();

    /**
     * Gets the node prefix id
     */
    String getNodePrefixId();

    /**
     * Whether the workflow id is custom assigned or auto assigned
     *
     * @return true if custom id, false if auto assigned id
     */
    boolean isCustomId();

    /**
     * Whether this workflow is a Rest DSL workflow.
     */
    boolean isCreatedByRestDsl();

    /**
     * Whether this workflow was created from a workflow template (or a Zwanginet).
     */
    boolean isCreatedByWorkflowTemplate();

    /**
     * Gets the workflow group
     *
     * @return the workflow group
     */
    String getGroup();

    /**
     * Gets the uptime in a human-readable format
     *
     * @return the uptime in days/hours/minutes
     */
    String getUptime();

    /**
     * Gets the uptime in milliseconds
     *
     * @return the uptime in milliseconds
     */
    long getUptimeMillis();

    /**
     * Gets the inbound {@link Consumer}
     *
     * @return the inbound consumer
     */
    Consumer getConsumer();

    /**
     * Gets the {@link Processor}
     *
     * @return the processor
     */
    Processor getProcessor();

    /**
     * Sets the {@link Processor}
     */
    void setProcessor(Processor processor);

    /**
     * Whether or not the workflow supports suspension (suspend and resume)
     *
     * @return <tt>true</tt> if this workflow supports suspension
     */
    boolean supportsSuspension();

    /**
     * This property map is used to associate information about the workflow.
     *
     * @return properties
     */
    Map<String, Object> getProperties();

    /**
     * Gets the workflow description (if any has been configured).
     * <p/>
     * The description is configured using the {@link #DESCRIPTION_PROPERTY} as key in the {@link #getProperties()}.
     *
     * @return the description, or <tt>null</tt> if no description has been configured.
     */
    String getDescription();

    /**
     * Gets the workflow configuration id(s) the workflow has been applied with. Multiple ids is separated by comma.
     * <p/>
     * The configuration ids is configured using the {@link #CONFIGURATION_ID_PROPERTY} as key in the
     * {@link #getProperties()}.
     *
     * @return the configuration, or <tt>null</tt> if no configuration has been configured.
     */
    String getConfigurationId();

    /**
     * Gets the source resource that this workflow is located from
     *
     * @return the source, or null if this workflow is not loaded from a resource
     */
    Resource getSourceResource();

    /**
     * The source:line-number where the workflow input is located in the source code
     */
    String getSourceLocation();

    /**
     * The source:line-number in short format that can be used for logging or summary purposes.
     */
    String getSourceLocationShort();

    /**
     * Gets the zwangine context
     *
     * @return the zwangine context
     */
    ZwangineContext getZwangineContext();

    /**
     * Gets the input endpoint for this workflow.
     *
     * @return the endpoint
     */
    Endpoint getEndpoint();

    /**
     * A strategy callback allowing special initialization when services are initializing.
     *
     * @throws Exception is thrown in case of error
     */
    void initializeServices() throws Exception;

    /**
     * Returns the services for this particular workflow
     *
     * @return the services
     */
    List<Service> getServices();

    /**
     * Adds a service to this workflow
     *
     * @param service the service
     */
    void addService(Service service);

    /**
     * Adds a service to this workflow
     *
     * @param service   the service
     * @param forceStop whether to force stopping the service when the workflow stops
     */
    void addService(Service service, boolean forceStop);

    /**
     * Returns a navigator to navigate this workflow by navigating all the {@link Processor}s.
     *
     * @return a navigator for {@link Processor}.
     */
    Navigate<Processor> navigate();

    /**
     * Returns a list of all the {@link Processor}s from this workflow that has id's matching the pattern
     *
     * @param  pattern the pattern to match by ids
     * @return         a list of {@link Processor}, is never <tt>null</tt>.
     */
    List<Processor> filter(String pattern);

    /**
     * Callback preparing the workflow to be started, by warming up the workflow.
     */
    void warmUp();

    /**
     * Gets the last error that happened during changing the workflow lifecycle, i.e. such as when an exception was thrown
     * during starting the workflow.
     * <p/>
     * This is only errors for workflow lifecycle changes, it is not exceptions thrown during routing exchanges by the
     * Zwangine routing engine.
     *
     * @return the error or <tt>null</tt> if no error
     */
    WorkflowError getLastError();

    /**
     * Sets the last error that happened during changing the workflow lifecycle, i.e. such as when an exception was thrown
     * during starting the workflow.
     * <p/>
     * This is only errors for workflow lifecycle changes, it is not exceptions thrown during routing exchanges by the
     * Zwangine routing engine.
     *
     * @param error the error
     */
    void setLastError(WorkflowError error);

    /**
     * Gets the workflow startup order
     */
    Integer getStartupOrder();

    /**
     * Sets the workflow startup order
     */
    void setStartupOrder(Integer startupOrder);

    /**
     * Gets the {@link WorkflowController} for this workflow.
     *
     * @return the workflow controller,
     */
    WorkflowController getWorkflowController();

    /**
     * Sets the {@link WorkflowController} for this workflow.
     *
     * @param controller the WorkflowController
     */
    void setWorkflowController(WorkflowController controller);

    /**
     * Sets whether the workflow should automatically start when Zwangine starts.
     * <p/>
     * Default is <tt>true</tt> to always start up.
     *
     * @param autoStartup whether to start up automatically.
     */
    void setAutoStartup(Boolean autoStartup);

    /**
     * Gets whether the workflow should automatically start when Zwangine starts.
     * <p/>
     * Default is <tt>true</tt> to always start up.
     *
     * @return <tt>true</tt> if workflow should automatically start
     */
    Boolean isAutoStartup();

    /**
     * Gets the workflow id
     */
    String getWorkflowId();

    /**
     * Gets the workflow description
     */
    String getWorkflowDescription();

    /**
     * Get the workflow type.
     *
     * Important: is null after the workflow has been created.
     *
     * @return the workflow type during creation of the workflow, is null after the workflow has been created.
     */
    NamedNode getWorkflow();

    /**
     * Clears the workflow model when its no longer needed.
     */
    void clearWorkflowModel();

    //
    // CREATION TIME
    //

    /**
     * This method retrieves the event driven Processors on this workflow context.
     */
    List<Processor> getEventDrivenProcessors();

    /**
     * This method retrieves the InterceptStrategy instances this workflow context.
     *
     * @return the strategy
     */
    List<InterceptStrategy> getInterceptStrategies();

    /**
     * Sets a special intercept strategy for management.
     * <p/>
     * Is by default used to correlate managed performance counters with processors when the runtime workflow is being
     * constructed
     *
     * @param interceptStrategy the managed intercept strategy
     */
    void setManagementInterceptStrategy(ManagementInterceptStrategy interceptStrategy);

    /**
     * Gets the special managed intercept strategy if any
     *
     * @return the managed intercept strategy, or <tt>null</tt> if not managed
     */
    ManagementInterceptStrategy getManagementInterceptStrategy();

    /**
     * Gets the workflow policy List
     *
     * @return the workflow policy list if any
     */
    List<WorkflowPolicy> getWorkflowPolicyList();

    // called at completion time
    void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory);

    // called at runtime
    ErrorHandlerFactory getErrorHandlerFactory();

    // called at runtime
    Collection<Processor> getOnCompletions();

    // called at completion time
    void setOnCompletion(String onCompletionId, Processor processor);

    // called at runtime
    Collection<Processor> getOnExceptions();

    // called at runtime
    Processor getOnException(String onExceptionId);

    // called at completion time
    void setOnException(String onExceptionId, Processor processor);

    /**
     * Adds error handler for the given exception type
     *
     * @param factory   the error handler factory
     * @param exception the exception to handle
     */
    void addErrorHandler(ErrorHandlerFactory factory, NamedNode exception);

    /**
     * Gets the error handlers
     *
     * @param factory the error handler factory
     */
    Set<NamedNode> getErrorHandlers(ErrorHandlerFactory factory);

    /**
     * Link the error handlers from a factory to another
     *
     * @param source the source factory
     * @param target the target factory
     */
    void addErrorHandlerFactoryReference(ErrorHandlerFactory source, ErrorHandlerFactory target);

    /**
     * Sets the resume strategy for the workflow
     */
    void setResumeStrategy(ResumeStrategy resumeStrategy);

    /**
     * Sets the consumer listener for the workflow
     */
    void setConsumerListener(ConsumerListener<?, ?> consumerListener);

}
