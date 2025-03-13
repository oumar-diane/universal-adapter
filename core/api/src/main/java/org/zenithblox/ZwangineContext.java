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

import org.zenithblox.clock.EventClock;
import org.zenithblox.spi.*;
import org.zenithblox.support.jsse.SSLContextParameters;
import org.zenithblox.vault.VaultConfiguration;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface used to represent the ZwangineContext used to configure workflows and the policies to use during message
 * exchanges between endpoints.
 * <p/>
 * The ZwangineContext offers the following methods {@link ZwangineContextLifecycle} to control the lifecycle:
 * <ul>
 * <li>{@link #start()} - to start</li>
 * <li>{@link #stop()} - to shutdown (will stop all workflows/components/endpoints etc and clear internal state/cache)</li>
 * <li>{@link #suspend()} - to pause routing messages</li>
 * <li>{@link #resume()} - to resume after a suspend</li>
 * </ul>
 * <p/>
 * <b>Notice:</b> {@link #stop()} and {@link #suspend()} will gracefully stop/suspend workflows ensuring any messages in
 * progress will be given time to complete. See more details at {@link org.zenithblox.spi.ShutdownStrategy}.
 * <p/>
 * If you are doing a hot restart then it's advised to use the suspend/resume methods which ensure a faster restart but
 * also allows any internal state to be kept as is. The stop/start approach will do a <i>cold</i> restart of Zwangine,
 * where all internal state is reset.
 * <p/>
 * End users are advised to use suspend/resume. Using stop is for shutting down Zwangine and it's not guaranteed that when
 * it's being started again using the start method that Zwangine will operate consistently.
 * <p/>
 * You can use the {@link ZwangineContext#getZwangineContextExtension()} to obtain the extension point for the
 * {@link ZwangineContext}. This extension point exposes internal APIs via {@link ExtendedZwangineContext}.
 */
public interface ZwangineContext extends ZwangineContextLifecycle, RuntimeConfiguration {

    /**
     * Gets the {@link ExtendedZwangineContext} that contains the extension points for internal context APIs. These APIs
     * are intended for internal usage within Zwangine and end-users should avoid using them.
     *
     * @return this {@link ExtendedZwangineContext} extension point for this context.
     */
    ExtendedZwangineContext getZwangineContextExtension();

    /**
     * If ZwangineContext during the start procedure was vetoed, and therefore causing Zwangine to not start.
     */
    boolean isVetoStarted();

    /**
     * Gets the name (id) of this ZwangineContext.
     *
     * @return the name
     */
    String getName();

    /**
     * Gets the description of this ZwangineContext.
     *
     * @return the description, or null if no description has been set.
     */
    String getDescription();

    /**
     * Gets the current name strategy
     *
     * @return name strategy
     */
    ZwangineContextNameStrategy getNameStrategy();

    /**
     * Sets a custom name strategy
     *
     * @param nameStrategy name strategy
     */
    void setNameStrategy(ZwangineContextNameStrategy nameStrategy);

    /**
     * Gets the current management name strategy
     *
     * @return management name strategy
     */
    ManagementNameStrategy getManagementNameStrategy();

    /**
     * Sets a custom management name strategy
     *
     * @param nameStrategy name strategy
     */
    void setManagementNameStrategy(ManagementNameStrategy nameStrategy);

    /**
     * Gets the name this {@link ZwangineContext} was registered in JMX.
     * <p/>
     * The reason that a {@link ZwangineContext} can have a different name in JMX is the fact to remedy for name clash in
     * JMX when having multiple {@link ZwangineContext}s in the same JVM. Zwangine will automatic reassign and use a free name
     * to avoid failing to start.
     *
     * @return the management name
     */
    String getManagementName();

    /**
     * Sets the name this {@link ZwangineContext} will be registered in JMX.
     */
    void setManagementName(String name);

    /**
     * Gets the version of this ZwangineContext.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Gets the uptime for this context.
     *
     * @return the uptime
     */
    Duration getUptime();

    /**
     * Gets a clock instance that keeps track of time for relevant ZwangineContext events
     *
     * @return A clock instance
     */
    EventClock<ContextEvents> getClock();

    // Service Methods
    //-----------------------------------------------------------------------

    /**
     * Adds a service to this ZwangineContext, which allows this ZwangineContext to control the lifecycle, ensuring the
     * service is stopped when the ZwangineContext stops.
     * <p/>
     * The service will also have {@link ZwangineContext} injected if its {@link ZwangineContextAware}. The service will also
     * be enlisted in JMX for management (if JMX is enabled). The service will be started, if its not already started.
     *
     * @param  object    the service
     * @throws Exception can be thrown when starting the service
     */
    void addService(Object object) throws Exception;

    /**
     * Adds a service to this ZwangineContext.
     * <p/>
     * The service will also have {@link ZwangineContext} injected if its {@link ZwangineContextAware}. The service will also
     * be enlisted in JMX for management (if JMX is enabled). The service will be started, if its not already started.
     * <p/>
     * If the option <tt>closeOnShutdown</tt> is <tt>true</tt> then this ZwangineContext will control the lifecycle,
     * ensuring the service is stopped when the ZwangineContext stops. If the option <tt>closeOnShutdown</tt> is
     * <tt>false</tt> then this ZwangineContext will not stop the service when the ZwangineContext stops.
     *
     * @param  object         the service
     * @param  stopOnShutdown whether to stop the service when this ZwangineContext shutdown.
     * @throws Exception      can be thrown when starting the service
     */
    void addService(Object object, boolean stopOnShutdown) throws Exception;

    /**
     * Adds a service to this ZwangineContext.
     * <p/>
     * The service will also have {@link ZwangineContext} injected if its {@link ZwangineContextAware}. The service will also
     * be enlisted in JMX for management (if JMX is enabled). The service will be started, if its not already started.
     * <p/>
     * If the option <tt>closeOnShutdown</tt> is <tt>true</tt> then this ZwangineContext will control the lifecycle,
     * ensuring the service is stopped when the ZwangineContext stops. If the option <tt>closeOnShutdown</tt> is
     * <tt>false</tt> then this ZwangineContext will not stop the service when the ZwangineContext stops.
     *
     * @param  object         the service
     * @param  stopOnShutdown whether to stop the service when this ZwangineContext shutdown.
     * @param  forceStart     whether to force starting the service right now, as otherwise the service may be deferred
     *                        being started to later using {@link #deferStartService(Object, boolean)}
     * @throws Exception      can be thrown when starting the service
     */
    void addService(Object object, boolean stopOnShutdown, boolean forceStart) throws Exception;

    /**
     * Adds a service to this ZwangineContext (prototype scope).
     * <p/>
     * The service will also have {@link ZwangineContext} injected if its {@link ZwangineContextAware}. The service will be
     * started, if its not already started.
     *
     * @param  object    the service
     * @throws Exception can be thrown when starting the service
     */
    void addPrototypeService(Object object) throws Exception;

    /**
     * Removes a service from this ZwangineContext.
     * <p/>
     * The service is assumed to have been previously added using {@link #addService(Object)} method. This method will
     * <b>not</b> change the service lifecycle.
     *
     * @param  object    the service
     * @throws Exception can be thrown if error removing the service
     * @return           <tt>true</tt> if the service was removed, <tt>false</tt> if no service existed
     */
    boolean removeService(Object object) throws Exception;

    /**
     * Has the given service already been added to this ZwangineContext?
     *
     * @param  object the service
     * @return        <tt>true</tt> if already added, <tt>false</tt> if not.
     */
    boolean hasService(Object object);

    /**
     * Finds the first service matching the filter
     *
     * @param  filter the filter
     * @return        the service if found or null if none found
     */
    Service hasService(java.util.function.Predicate<Service> filter);

    /**
     * Has the given service type already been added to this ZwangineContext?
     *
     * @param  type the class type
     * @return      the service instance or <tt>null</tt> if not already added.
     */
    <T> T hasService(Class<T> type);

    /**
     * Has the given service type already been added to this ZwangineContext?
     *
     * @param  type the class type
     * @return      the services instance or empty set.
     */
    <T> Set<T> hasServices(Class<T> type);

    /**
     * Defers starting the service until {@link ZwangineContext} is (almost started) or started and has initialized all its
     * prior services and workflows.
     * <p/>
     * If {@link ZwangineContext} is already started then the service is started immediately.
     *
     * @param  object         the service
     * @param  stopOnShutdown whether to stop the service when this ZwangineContext shutdown. Setting this to <tt>true</tt>
     *                        will keep a reference to the service in this {@link ZwangineContext} until the ZwangineContext
     *                        is stopped. So do not use it for short lived services.
     * @throws Exception      can be thrown when starting the service, which is only attempted if {@link ZwangineContext}
     *                        has already been started when calling this method.
     */
    void deferStartService(Object object, boolean stopOnShutdown) throws Exception;

    /**
     * Adds the given listener to be invoked when {@link ZwangineContext} have just been started.
     * <p/>
     * This allows listeners to do any custom work after the workflows and other services have been started and are
     * running.
     * <p/>
     * <b>Important:</b> The listener will always be invoked, also if the {@link ZwangineContext} has already been started,
     * see the {@link org.zenithblox.StartupListener#onZwangineContextStarted(ZwangineContext, boolean)} method.
     *
     * @param  listener  the listener
     * @throws Exception can be thrown if {@link ZwangineContext} is already started and the listener is invoked and cause
     *                   an exception to be thrown
     */
    void addStartupListener(StartupListener listener) throws Exception;

    // Component Management Methods
    //-----------------------------------------------------------------------

    /**
     * Adds a component to the context.
     *
     * Notice the component will be auto-started if Zwangine is already started.
     *
     * @param componentName the name the component is registered as
     * @param component     the component
     */
    void addComponent(String componentName, Component component);

    /**
     * Is the given component already registered?
     *
     * @param  componentName the name of the component
     * @return               the registered Component or <tt>null</tt> if not registered
     */
    Component hasComponent(String componentName);

    /**
     * Gets a component from the ZwangineContext by name.
     * <p/>
     * Notice the returned component will be auto-started. If you do not intend to do that then use
     * {@link #getComponent(String, boolean, boolean)}.
     *
     * @param  componentName the name of the component
     * @return               the component
     */
    Component getComponent(String componentName);

    /**
     * Gets a component from the ZwangineContext by name.
     * <p/>
     * Notice the returned component will be auto-started. If you do not intend to do that then use
     * {@link #getComponent(String, boolean, boolean)}.
     *
     * @param  name                 the name of the component
     * @param  autoCreateComponents whether or not the component should be lazily created if it does not already exist
     * @return                      the component
     */
    Component getComponent(String name, boolean autoCreateComponents);

    /**
     * Gets a component from the ZwangineContext by name.
     *
     * @param  name                 the name of the component
     * @param  autoCreateComponents whether or not the component should be lazily created if it does not already exist
     * @param  autoStart            whether to auto start the component if {@link ZwangineContext} is already started.
     * @return                      the component
     */
    Component getComponent(String name, boolean autoCreateComponents, boolean autoStart);

    /**
     * Gets a component from the ZwangineContext by name and specifying the expected type of component.
     *
     * @param  name          the name to lookup
     * @param  componentType the expected type
     * @return               the component
     */
    <T extends Component> T getComponent(String name, Class<T> componentType);

    /**
     * Gets a readonly list of names of the components currently registered
     *
     * @return a readonly list with the names of the components
     */
    Set<String> getComponentNames();

    /**
     * Removes a previously added component.
     * <p/>
     * The component being removed will be stopped first.
     *
     * @param  componentName the component name to remove
     * @return               the previously added component or null if it had not been previously added.
     */
    Component removeComponent(String componentName);

    // Endpoint Management Methods
    //-----------------------------------------------------------------------

    /**
     * Gets the {@link org.zenithblox.spi.EndpointRegistry}
     */
    EndpointRegistry getEndpointRegistry();

    /**
     * Resolves the given name to an {@link Endpoint} of the specified type. If the name has a singleton endpoint
     * registered, then the singleton is returned. Otherwise, a new {@link Endpoint} is created and registered in the
     * {@link org.zenithblox.spi.EndpointRegistry}.
     *
     * @param  uri the URI of the endpoint
     * @return     the endpoint
     */
    Endpoint getEndpoint(String uri);

    /**
     * Resolves the given name to an {@link Endpoint} of the specified type. If the name has a singleton endpoint
     * registered, then the singleton is returned. Otherwise, a new {@link Endpoint} is created and registered in the
     * {@link org.zenithblox.spi.EndpointRegistry}.
     *
     * @param  uri        the URI of the endpoint
     * @param  parameters the parameters to customize the endpoint
     * @return            the endpoint
     */
    Endpoint getEndpoint(String uri, Map<String, Object> parameters);

    /**
     * Resolves the given name to an {@link Endpoint} of the specified type. If the name has a singleton endpoint
     * registered, then the singleton is returned. Otherwise, a new {@link Endpoint} is created and registered in the
     * {@link org.zenithblox.spi.EndpointRegistry}.
     *
     * @param  name         the name of the endpoint
     * @param  endpointType the expected type
     * @return              the endpoint
     */
    <T extends Endpoint> T getEndpoint(String name, Class<T> endpointType);

    /**
     * Returns a read-only {@link Collection} of all of the endpoints from the
     * {@link org.zenithblox.spi.EndpointRegistry}
     *
     * @return all endpoints
     */
    Collection<Endpoint> getEndpoints();

    /**
     * Is the given endpoint already registered in the {@link org.zenithblox.spi.EndpointRegistry}
     *
     * @param  uri the URI of the endpoint
     * @return     the registered endpoint or <tt>null</tt> if not registered
     */
    Endpoint hasEndpoint(String uri);

    /**
     * Adds and starts the endpoint to the {@link org.zenithblox.spi.EndpointRegistry} using the given URI.
     *
     * @param  uri       the URI to be used to resolve this endpoint
     * @param  endpoint  the endpoint to be started and added to the registry
     * @return           the old endpoint that was previously registered or <tt>null</tt> if none was registered
     * @throws Exception if the new endpoint could not be started or the old endpoint could not be stopped
     */
    Endpoint addEndpoint(String uri, Endpoint endpoint) throws Exception;

    /**
     * Removes the endpoint from the {@link org.zenithblox.spi.EndpointRegistry}.
     * <p/>
     * The endpoint being removed will be stopped first.
     *
     * @param  endpoint  the endpoint
     * @throws Exception if the endpoint could not be stopped
     */
    void removeEndpoint(Endpoint endpoint) throws Exception;

    /**
     * Removes all endpoints with the given URI from the {@link org.zenithblox.spi.EndpointRegistry}.
     * <p/>
     * The endpoints being removed will be stopped first.
     *
     * @param  pattern   an uri or pattern to match
     * @return           a collection of endpoints removed which could be empty if there are no endpoints found for the
     *                   given <tt>pattern</tt>
     * @throws Exception if at least one endpoint could not be stopped
     * @see              org.zenithblox.support.EndpointHelper#matchEndpoint(ZwangineContext, String, String) for pattern
     */
    Collection<Endpoint> removeEndpoints(String pattern) throws Exception;

    /**
     * Gets the global endpoint configuration, where you can configure common endpoint options.
     */
    GlobalEndpointConfiguration getGlobalEndpointConfiguration();

    // Workflow Management Methods
    //-----------------------------------------------------------------------

    /**
     * Sets a custom {@link WorkflowController} to use
     *
     * @param workflowController the workflow controller
     */
    void setWorkflowController(WorkflowController workflowController);

    /**
     * Gets the {@link WorkflowController}
     *
     * @return the workflow controller.
     */
    WorkflowController getWorkflowController();

    /**
     * Returns the current workflows in this ZwangineContext
     *
     * @return the current workflows
     */
    List<Workflow> getWorkflows();

    /**
     * Returns the total number of workflows in this ZwangineContext
     */
    int getWorkflowsSize();

    /**
     * Gets the workflow with the given id
     *
     * @param  id id of the workflow
     * @return    the workflow or <tt>null</tt> if not found
     */
    Workflow getWorkflow(String id);

    /**
     * Gets the processor from any of the workflows which with the given id
     *
     * @param  id id of the processor
     * @return    the processor or <tt>null</tt> if not found
     */
    Processor getProcessor(String id);

    /**
     * Gets the processor from any of the workflows which with the given id
     *
     * @param  id                           id of the processor
     * @param  type                         the processor type
     * @return                              the processor or <tt>null</tt> if not found
     * @throws ClassCastException is thrown if the type is not correct type
     */
    <T extends Processor> T getProcessor(String id, Class<T> type);

    /**
     * Adds a collection of workflows to this ZwangineContext using the given builder to build them.
     * <p/>
     * <b>Important:</b> The added workflows will <b>only</b> be started, if {@link ZwangineContext} is already started. You
     * may want to check the state of {@link ZwangineContext} before adding the workflows, using the
     * {@link org.zenithblox.ZwangineContext#getStatus()} method.
     * <p/>
     * <b>Important: </b> Each workflow in the same {@link org.zenithblox.ZwangineContext} must have an <b>unique</b> workflow
     * id. If you use the API from {@link org.zenithblox.ZwangineContext} or
     * {@link org.zenithblox.model.ModelZwangineContext} to add workflows, then any new workflows which has a workflow id that
     * matches an old workflow, then the old workflow is replaced by the new workflow.
     *
     * @param  builder   the builder which will create the workflows and add them to this ZwangineContext
     * @throws Exception if the workflows could not be created for whatever reason
     */
    void addWorkflows(WorkflowsBuilder builder) throws Exception;

    /**
     * Adds the templated workflows from the workflows builder. For example in Java DSL you can use
     * {@link org.zenithblox.builder.TemplatedWorkflowBuilder}.
     *
     * @param  builder   the builder which has templated workflows
     * @throws Exception if the workflows could not be created for whatever reason
     */
    void addTemplatedWorkflows(WorkflowsBuilder builder) throws Exception;

    /**
     * Adds the workflows configurations (global configuration for all workflows) from the workflows builder.
     *
     * @param  builder   the builder which has workflows configurations
     * @throws Exception if the workflows configurations could not be created for whatever reason
     */
    void addWorkflowsConfigurations(WorkflowConfigurationsBuilder builder) throws Exception;

    /**
     * Removes the given workflow (the workflow <b>must</b> be stopped before it can be removed).
     * <p/>
     * A workflow which is removed will be unregistered from JMX, have its services stopped/shutdown and the workflow
     * definition etc. will also be removed. All the resources related to the workflow will be stopped and cleared.
     * <p/>
     * <b>Important:</b> When removing a workflow, the {@link Endpoint}s which are in the static cache of
     * {@link org.zenithblox.spi.EndpointRegistry} and are <b>only</b> used by the workflow (not used by other workflows)
     * will also be removed. But {@link Endpoint}s which may have been created as part of routing messages by the workflow,
     * and those endpoints are enlisted in the dynamic cache of {@link org.zenithblox.spi.EndpointRegistry} are
     * <b>not</b> removed. To remove those dynamic kind of endpoints, use the {@link #removeEndpoints(String)} method.
     * If not removing those endpoints, they will be kept in the dynamic cache of
     * {@link org.zenithblox.spi.EndpointRegistry}, but my eventually be removed (evicted) when they have not been in
     * use for a longer period of time; and the dynamic cache upper limit is hit, and it evicts the least used
     * endpoints.
     * <p/>
     * End users can use this method to remove unwanted workflows or temporary workflows which no longer is in demand.
     *
     * @param  workflowId   the workflow id
     * @return           <tt>true</tt> if the workflow was removed, <tt>false</tt> if the workflow could not be removed
     *                   because it's not stopped
     * @throws Exception is thrown if the workflow could not be shutdown for whatever reason
     */
    boolean removeWorkflow(String workflowId) throws Exception;

    /**
     * Adds a new workflow from a given workflow template.
     *
     * Zwangine end users should favour using {@link org.zenithblox.builder.TemplatedWorkflowBuilder} which is a fluent
     * builder with more functionality than this API.
     *
     * @param  workflowId         the id of the new workflow to add (optional)
     * @param  workflowTemplateId the id of the workflow template (mandatory)
     * @param  parameters      parameters to use for the workflow template when creating the new workflow
     * @return                 the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception       is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, Map<String, Object> parameters) throws Exception;

    /**
     * Adds a new workflow from a given workflow template.
     *
     * Zwangine end users should favour using {@link org.zenithblox.builder.TemplatedWorkflowBuilder} which is a fluent
     * builder with more functionality than this API.
     *
     * @param  workflowId         the id of the new workflow to add (optional)
     * @param  workflowTemplateId the id of the workflow template (mandatory)
     * @param  prefixId        prefix to use for all node ids (not workflow id). Use null for no prefix. (optional)
     * @param  parameters      parameters to use for the workflow template when creating the new workflow
     * @return                 the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception       is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId,
            Map<String, Object> parameters)
            throws Exception;

    /**
     * Adds a new workflow from a given workflow template.
     *
     * Zwangine end users should favour using {@link org.zenithblox.builder.TemplatedWorkflowBuilder} which is a fluent
     * builder with more functionality than this API.
     *
     * @param  workflowId              the id of the new workflow to add (optional)
     * @param  workflowTemplateId      the id of the workflow template (mandatory)
     * @param  prefixId             prefix to use for all node ids (not workflow id). Use null for no prefix. (optional)
     * @param  workflowTemplateContext the workflow template context (mandatory)
     * @return                      the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception            is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId, WorkflowTemplateContext workflowTemplateContext)
            throws Exception;

    /**
     * Adds a new workflow from a given kamelet
     *
     * @param  workflowId           the id of the new workflow to add (optional)
     * @param  workflowTemplateId   the id of the kamelet workflow template (mandatory)
     * @param  prefixId          prefix to use for all node ids (not workflow id). Use null for no prefix. (optional)
     * @param  parentWorkflowId     the id of the workflow which is using the kamelet (such as from / to)
     * @param  parentProcessorId the id of the processor which is using the kamelet (such as to)
     * @param  parameters        parameters to use for the workflow template when creating the new workflow
     * @return                   the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception         is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromKamelet(
            String workflowId, String workflowTemplateId, String prefixId,
            String parentWorkflowId, String parentProcessorId,
            Map<String, Object> parameters)
            throws Exception;

    /**
     * Removes the workflow templates matching the pattern
     *
     * @param  pattern   pattern, such as * for all, or foo* to remove all foo templates
     * @throws Exception is thrown if error during removing workflow templates
     */
    void removeWorkflowTemplates(String pattern) throws Exception;

    /**
     * Adds the given workflow policy factory
     *
     * @param workflowPolicyFactory the factory
     */
    void addWorkflowPolicyFactory(WorkflowPolicyFactory workflowPolicyFactory);

    /**
     * Gets the workflow policy factories
     *
     * @return the list of current workflow policy factories
     */
    List<WorkflowPolicyFactory> getWorkflowPolicyFactories();

    // Rest Methods
    //-----------------------------------------------------------------------

    /**
     * Sets a custom {@link org.zenithblox.spi.RestConfiguration}
     *
     * @param restConfiguration the REST configuration
     */
    void setRestConfiguration(RestConfiguration restConfiguration);

    /**
     * Gets the default REST configuration
     *
     * @return the configuration, or <tt>null</tt> if none has been configured.
     */
    RestConfiguration getRestConfiguration();

    /**
     * Sets a custom {@link VaultConfiguration}
     *
     * @param vaultConfiguration the vault configuration
     */
    void setVaultConfiguration(VaultConfiguration vaultConfiguration);

    /**
     * Gets the vault configuration
     *
     * @return the configuration, or <tt>null</tt> if none has been configured.
     */
    VaultConfiguration getVaultConfiguration();

    /**
     * Gets the {@link org.zenithblox.spi.RestRegistry} to use
     */
    RestRegistry getRestRegistry();

    /**
     * Sets a custom {@link org.zenithblox.spi.RestRegistry} to use.
     */
    void setRestRegistry(RestRegistry restRegistry);

    // Properties
    //-----------------------------------------------------------------------

    /**
     * Returns the type converter used to coerce types from one type to another.
     * <p/>
     * Notice that this {@link ZwangineContext} should be at least initialized before you can get the type converter.
     *
     * @return the converter
     */
    TypeConverter getTypeConverter();

    /**
     * Returns the type converter registry where type converters can be added or looked up
     *
     * @return the type converter registry
     */
    TypeConverterRegistry getTypeConverterRegistry();

    /**
     * Configures the type converter registry to use, where type converters can be added or looked up.
     *
     * @param typeConverterRegistry the registry to use
     */
    void setTypeConverterRegistry(TypeConverterRegistry typeConverterRegistry);

    /**
     * Returns the registry used to lookup components by name and type such as SimpleRegistry, Spring
     * ApplicationContext, JNDI, or the OSGi Service Registry.
     *
     * @return the registry
     */
    Registry getRegistry();

    /**
     * Returns the registry used to lookup components by name and as the given type
     *
     * @param  type the registry type such as org.zenithblox.impl.JndiRegistry
     * @return      the registry, or <tt>null</tt> if the given type was not found as a registry implementation
     */
    <T> T getRegistry(Class<T> type);

    /**
     * Returns the injector used to instantiate objects by type
     *
     * @return the injector
     */
    Injector getInjector();

    /**
     * Sets the injector to use
     */
    void setInjector(Injector injector);

    /**
     * Returns the lifecycle strategies used to handle lifecycle notifications
     *
     * @return the lifecycle strategies
     */
    List<LifecycleStrategy> getLifecycleStrategies();

    /**
     * Adds the given lifecycle strategy to be used.
     *
     * @param lifecycleStrategy the strategy
     */
    void addLifecycleStrategy(LifecycleStrategy lifecycleStrategy);

    /**
     * Resolves a language for creating expressions
     *
     * @param  language                name of the language
     * @return                         the resolved language
     * @throws NoSuchLanguageException is thrown if language could not be resolved
     */
    Language resolveLanguage(String language) throws NoSuchLanguageException;

    /**
     * Parses the given text and resolve any property placeholders - using {{key}}.
     * <p/>
     * <b>Important:</b> If resolving placeholders on an endpoint uri, then you SHOULD use
     * EndpointHelper#resolveEndpointUriPropertyPlaceholders instead.
     *
     * @param  text                     the text such as an endpoint uri or the likes
     * @return                          the text with resolved property placeholders
     * @throws IllegalArgumentException is thrown if property placeholders was used and there was an error resolving
     *                                  them
     */
    String resolvePropertyPlaceholders(String text);

    /**
     * To get a variable by name.
     *
     * @param  name the variable name. Can be prefixed with repo-id:name to lookup the variable from a specific
     *              repository. If no repo-id is provided, then global repository will be used.
     * @return      the variable, or <tt>null</tt> if not found.
     */
    Object getVariable(String name);

    /**
     * To get a variable by name and covert to the given type.
     *
     * @param  name the variable name. Can be prefixed with repo-id:name to lookup the variable from a specific
     *              repository. If no repo-id is provided, then global repository will be used.
     * @param  type the type to convert the variable to
     * @return      the variable, or <tt>null</tt> if not found.
     */
    <T> T getVariable(String name, Class<T> type);

    /**
     * Sets a variable
     *
     * @param name  the variable name. Can be prefixed with repo-id:name to store the variable in a specific repository.
     *              If no repo-id is provided, then global repository will be used.
     * @param value the value of the variable
     */
    void setVariable(String name, Object value);

    /**
     * Returns the configured properties component or create one if none has been configured.
     *
     * @return the properties component
     */
    PropertiesComponent getPropertiesComponent();

    /**
     * Sets a custom properties component to be used.
     */
    void setPropertiesComponent(PropertiesComponent propertiesComponent);

    /**
     * Gets a readonly list with the names of the languages currently registered.
     *
     * @return a readonly list with the names of the languages
     */
    Set<String> getLanguageNames();

    /**
     * Creates a new {@link ProducerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a>
     * <p/>
     * <b>Important:</b> Make sure to call {@link org.zenithblox.ProducerTemplate#stop()} when you are done using the
     * template, to clean up any resources.
     * <p/>
     * Will use cache size defined in Zwangine property with key {@link Exchange#MAXIMUM_CACHE_POOL_SIZE}. If no key was
     * defined then it will fallback to a default size of 1000. You can also use the
     * {@link org.zenithblox.ProducerTemplate#setMaximumCacheSize(int)} method to use a custom value before starting
     * the template.
     *
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    ProducerTemplate createProducerTemplate();

    /**
     * Creates a new {@link ProducerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a>
     * <p/>
     * <b>Important:</b> Make sure to call {@link ProducerTemplate#stop()} when you are done using the template, to
     * clean up any resources.
     *
     * @param  maximumCacheSize      the maximum cache size
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    ProducerTemplate createProducerTemplate(int maximumCacheSize);

    /**
     * Creates a new {@link FluentProducerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a>
     * <p/>
     * <b>Important:</b> Make sure to call {@link org.zenithblox.FluentProducerTemplate#stop()} when you are done
     * using the template, to clean up any resources.
     * <p/>
     * Will use cache size defined in Zwangine property with key {@link Exchange#MAXIMUM_CACHE_POOL_SIZE}. If no key was
     * defined then it will fallback to a default size of 1000. You can also use the
     * {@link org.zenithblox.FluentProducerTemplate#setMaximumCacheSize(int)} method to use a custom value before
     * starting the template.
     *
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    FluentProducerTemplate createFluentProducerTemplate();

    /**
     * Creates a new {@link FluentProducerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a>
     * <p/>
     * <b>Important:</b> Make sure to call {@link FluentProducerTemplate#stop()} when you are done using the template,
     * to clean up any resources.
     *
     * @param  maximumCacheSize      the maximum cache size
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    FluentProducerTemplate createFluentProducerTemplate(int maximumCacheSize);

    /**
     * Creates a new {@link ConsumerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a> as it also applies for ConsumerTemplate.
     * <p/>
     * <b>Important:</b> Make sure to call {@link ConsumerTemplate#stop()} when you are done using the template, to
     * clean up any resources.
     * <p/>
     * Will use cache size defined in Zwangine property with key {@link Exchange#MAXIMUM_CACHE_POOL_SIZE}. If no key was
     * defined then it will fallback to a default size of 1000. You can also use the
     * {@link org.zenithblox.ConsumerTemplate#setMaximumCacheSize(int)} method to use a custom value before starting
     * the template.
     *
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    ConsumerTemplate createConsumerTemplate();

    /**
     * Creates a new {@link ConsumerTemplate} which is <b>started</b> and therefore ready to use right away.
     * <p/>
     * See this FAQ before use:
     * <a href="http://zwangine.zwangine.org/why-does-zwangine-use-too-many-threads-with-producertemplate.html"> Why does Zwangine
     * use too many threads with ProducerTemplate?</a> as it also applies for ConsumerTemplate.
     * <p/>
     * <b>Important:</b> Make sure to call {@link ConsumerTemplate#stop()} when you are done using the template, to
     * clean up any resources.
     *
     * @param  maximumCacheSize      the maximum cache size
     * @return                       the template
     * @throws RuntimeZwangineException is thrown if error starting the template
     */
    ConsumerTemplate createConsumerTemplate(int maximumCacheSize);

    /**
     * Resolve an existing data format, or creates a new by the given its name
     *
     * @param  name the data format name or a reference to it in the {@link Registry}
     * @return      the resolved data format, or <tt>null</tt> if not found
     */
    DataFormat resolveDataFormat(String name);

    /**
     * Creates a new instance of the given data format given its name.
     *
     * @param  name the data format name or a reference to a data format factory in the {@link Registry}
     * @return      the created data format, or <tt>null</tt> if not found
     */
    DataFormat createDataFormat(String name);

    /**
     * Gets a readonly list of names of the data formats currently registered
     *
     * @return a readonly list with the names of the data formats
     */
    Set<String> getDataFormatNames();

    /**
     * Resolve a transformer given a scheme
     *
     * @param  name the transformer name, usually a combination of some scheme and name.
     * @return      the resolved transformer, or <tt>null</tt> if not found
     */
    Transformer resolveTransformer(String name);

    /**
     * Resolve a transformer given from/to data type.
     *
     * @param  from from data type
     * @param  to   to data type
     * @return      the resolved transformer, or <tt>null</tt> if not found
     */
    Transformer resolveTransformer(DataType from, DataType to);

    /**
     * Gets the {@link TransformerRegistry}
     *
     * @return the TransformerRegistry
     */
    TransformerRegistry getTransformerRegistry();

    /**
     * Resolve a validator given from/to data type.
     *
     * @param  type the data type
     * @return      the resolved validator, or <tt>null</tt> if not found
     */
    Validator resolveValidator(DataType type);

    /**
     * Gets the {@link ValidatorRegistry}
     *
     * @return the ValidatorRegistry
     */
    ValidatorRegistry getValidatorRegistry();

    /**
     * Sets global options that can be referenced in the zwangine context
     * <p/>
     * <b>Important:</b> This has nothing to do with property placeholders, and is just a plain set of key/value pairs
     * which are used to configure global options on ZwangineContext, such as a maximum debug logging length etc. For
     * property placeholders use {@link #resolvePropertyPlaceholders(String)} method and see more details at the
     * <a href="http://zwangine.zwangine.org/using-propertyplaceholder.html">property placeholder</a> documentation.
     *
     * @param globalOptions global options that can be referenced in the zwangine context
     */
    void setGlobalOptions(Map<String, String> globalOptions);

    /**
     * Gets global options that can be referenced in the zwangine context.
     * <p/>
     * <b>Important:</b> This has nothing to do with property placeholders, and is just a plain set of key/value pairs
     * which are used to configure global options on ZwangineContext, such as a maximum debug logging length etc. For
     * property placeholders use {@link #resolvePropertyPlaceholders(String)} method and see more details at the
     * <a href="http://zwangine.zwangine.org/using-propertyplaceholder.html">property placeholder</a> documentation.
     *
     * @return global options for this context
     */
    Map<String, String> getGlobalOptions();

    /**
     * Gets the global option value that can be referenced in the zwangine context
     * <p/>
     * <b>Important:</b> This has nothing to do with property placeholders, and is just a plain set of key/value pairs
     * which are used to configure global options on ZwangineContext, such as a maximum debug logging length etc. For
     * property placeholders use {@link #resolvePropertyPlaceholders(String)} method and see more details at the
     * <a href="http://zwangine.zwangine.org/using-propertyplaceholder.html">property placeholder</a> documentation.
     *
     * @return the string value of the global option
     */
    String getGlobalOption(String key);

    /**
     * Returns the class resolver to be used for loading/lookup of classes.
     *
     * @return the resolver
     */
    ClassResolver getClassResolver();

    /**
     * Sets the class resolver to be use
     *
     * @param resolver the resolver
     */
    void setClassResolver(ClassResolver resolver);

    /**
     * Gets the management strategy
     *
     * @return the management strategy
     */
    ManagementStrategy getManagementStrategy();

    /**
     * Sets the management strategy to use
     *
     * @param strategy the management strategy
     */
    void setManagementStrategy(ManagementStrategy strategy);

    /**
     * Disables using JMX as {@link org.zenithblox.spi.ManagementStrategy}.
     * <p/>
     * <b>Important:</b> This method must be called <b>before</b> the {@link ZwangineContext} is started.
     *
     * @throws IllegalStateException is thrown if the {@link ZwangineContext} is not in stopped state.
     */
    void disableJMX() throws IllegalStateException;

    /**
     * Gets the inflight repository
     *
     * @return the repository
     */
    InflightRepository getInflightRepository();

    /**
     * Sets a custom inflight repository to use
     *
     * @param repository the repository
     */
    void setInflightRepository(InflightRepository repository);

    /**
     * Gets the application ZwangineContext class loader which may be helpful for running zwangine in other containers
     *
     * @return the application ZwangineContext class loader
     */
    ClassLoader getApplicationContextClassLoader();

    /**
     * Sets the application ZwangineContext class loader
     *
     * @param classLoader the class loader
     */
    void setApplicationContextClassLoader(ClassLoader classLoader);

    /**
     * Gets the current shutdown strategy.
     * <p/>
     * The shutdown strategy is <b>not</b> intended for Zwangine end users to use for stopping workflows. Instead use
     * {@link WorkflowController} via {@link ZwangineContext}.
     *
     * @return the strategy
     */
    ShutdownStrategy getShutdownStrategy();

    /**
     * Sets a custom shutdown strategy
     *
     * @param shutdownStrategy the custom strategy
     */
    void setShutdownStrategy(ShutdownStrategy shutdownStrategy);

    /**
     * Gets the current {@link org.zenithblox.spi.ExecutorServiceManager}
     *
     * @return the manager
     */
    ExecutorServiceManager getExecutorServiceManager();

    /**
     * Sets a custom {@link org.zenithblox.spi.ExecutorServiceManager}
     *
     * @param executorServiceManager the custom manager
     */
    void setExecutorServiceManager(ExecutorServiceManager executorServiceManager);

    /**
     * Gets the current {@link org.zenithblox.spi.MessageHistoryFactory}
     *
     * @return the factory
     */
    MessageHistoryFactory getMessageHistoryFactory();

    /**
     * Sets a custom {@link org.zenithblox.spi.MessageHistoryFactory}
     *
     * @param messageHistoryFactory the custom factory
     */
    void setMessageHistoryFactory(MessageHistoryFactory messageHistoryFactory);

    /**
     * Gets the current {@link Tracer}
     *
     * @return the tracer
     */
    Tracer getTracer();

    /**
     * Sets a custom {@link Tracer}
     */
    void setTracer(Tracer tracer);

    /**
     * Whether to set tracing on standby. If on standby then the tracer is installed and made available. Then the tracer
     * can be enabled later at runtime via JMX or via {@link Tracer#setEnabled(boolean)}.
     */
    void setTracingStandby(boolean tracingStandby);

    /**
     * Whether to set tracing on standby. If on standby then the tracer is installed and made available. Then the tracer
     * can be enabled later at runtime via JMX or via {@link Tracer#setEnabled(boolean)}.
     */
    boolean isTracingStandby();

    /**
     * Whether to set backlog tracing on standby. If on standby then the backlog tracer is installed and made available.
     * Then the backlog tracer can be enabled later at runtime via JMX or via Java API.
     */
    void setBacklogTracingStandby(boolean backlogTracingStandby);

    /**
     * Whether to set backlog tracing on standby. If on standby then the backlog tracer is installed and made available.
     * Then the backlog tracer can be enabled later at runtime via JMX or via Java API.
     */
    boolean isBacklogTracingStandby();

    /**
     * Whether to set backlog debugger on standby. If on standby then the backlog debugger is installed and made
     * available. Then the backlog debugger can be enabled later at runtime via JMX or via Java API.
     */
    void setDebugStandby(boolean debugStandby);

    /**
     * Whether to set backlog debugger on standby. If on standby then the backlog debugger is installed and made
     * available. Then the backlog debugger can be enabled later at runtime via JMX or via Java API.
     */
    boolean isDebugStandby();

    /**
     * Whether backlog tracing should trace inner details from workflow templates (or kamelets). Turning this off can
     * reduce the verbosity of tracing when using many workflow templates, and allow to focus on tracing your own Zwangine
     * workflows only.
     */
    void setBacklogTracingTemplates(boolean backlogTracingTemplates);

    /**
     * Whether backlog tracing should trace inner details from workflow templates (or kamelets). Turning this on increases
     * the verbosity of tracing by including events from internal workflows in the templates or kamelets.
     */
    boolean isBacklogTracingTemplates();

    /**
     * Gets the current {@link UuidGenerator}
     *
     * @return the uuidGenerator
     */
    UuidGenerator getUuidGenerator();

    /**
     * Sets a custom {@link UuidGenerator} (should only be set once)
     *
     * @param uuidGenerator the UUID Generator
     */
    void setUuidGenerator(UuidGenerator uuidGenerator);

    /**
     * Whether to load custom type converters by scanning classpath. This is used for backwards compatibility with Zwangine
     * 2.x. Its recommended to migrate to use fast type converter loading by setting <tt>@Converter(loader = true)</tt>
     * on your custom type converter classes.
     */
    Boolean isLoadTypeConverters();

    /**
     * Whether to load custom type converters by scanning classpath. This is used for backwards compatibility with Zwangine
     * 2.x. Its recommended to migrate to use fast type converter loading by setting <tt>@Converter(loader = true)</tt>
     * on your custom type converter classes.
     *
     * @param loadTypeConverters whether to load custom type converters using classpath scanning.
     */
    void setLoadTypeConverters(Boolean loadTypeConverters);

    /**
     * Whether to load custom health checks by scanning classpath.
     */
    Boolean isLoadHealthChecks();

    /**
     * Whether to load custom health checks by scanning classpath.
     */
    void setLoadHealthChecks(Boolean loadHealthChecks);

    /**
     * Whether to capture precise source location:line-number for all EIPs in Zwangine workflows.
     *
     * Enabling this will impact parsing Java based workflows (also Groovy, etc.) on startup as this uses
     * {@link StackTraceElement} to calculate the location from the Zwangine workflow, which comes with a performance cost.
     * This only impact startup, not the performance of the workflows at runtime.
     */
    Boolean isSourceLocationEnabled();

    /**
     * Whether to capture precise source location:line-number for all EIPs in Zwangine workflows.
     *
     * Enabling this will impact parsing Java based workflows (also Groovy, etc.) on startup as this uses
     * {@link StackTraceElement} to calculate the location from the Zwangine workflow, which comes with a performance cost.
     * This only impact startup, not the performance of the workflows at runtime.
     */
    void setSourceLocationEnabled(Boolean sourceLocationEnabled);

    /**
     * Whether zwangine-k style modeline is also enabled when not using zwangine-k. Enabling this allows to use a zwangine-k like
     * experience by being able to configure various settings using modeline directly in your workflow source code.
     */
    Boolean isModeline();

    /**
     * Whether zwangine-k style modeline is also enabled when not using zwangine-k. Enabling this allows to use a zwangine-k like
     * experience by being able to configure various settings using modeline directly in your workflow source code.
     */
    void setModeline(Boolean modeline);


    /**
     * Whether or not type converter statistics is enabled.
     * <p/>
     * By default the type converter utilization statistics is disabled. <b>Notice:</b> If enabled then there is a
     * slight performance impact under very heavy load.
     *
     * @return <tt>true</tt> if enabled, <tt>false</tt> if disabled (default).
     */
    Boolean isTypeConverterStatisticsEnabled();

    /**
     * Sets whether or not type converter statistics is enabled.
     * <p/>
     * By default the type converter utilization statistics is disabled. <b>Notice:</b> If enabled then there is a
     * slight performance impact under very heavy load.
     * <p/>
     * You can enable/disable the statistics at runtime using the
     * {@link TypeConverterRegistry#getStatistics()#setTypeConverterStatisticsEnabled(Boolean)}
     * method, or from JMX on the {@link org.zenithblox.api.management.mbean.ManagedTypeConverterRegistryMBean} mbean.
     *
     * @param typeConverterStatisticsEnabled <tt>true</tt> to enable, <tt>false</tt> to disable
     */
    void setTypeConverterStatisticsEnabled(Boolean typeConverterStatisticsEnabled);

    /**
     * Whether or not <a href="http://www.slf4j.org/api/org/slf4j/MDC.html">MDC</a> logging is being enabled.
     *
     * @return <tt>true</tt> if MDC logging is enabled
     */
    Boolean isUseMDCLogging();

    /**
     * Set whether <a href="http://www.slf4j.org/api/org/slf4j/MDC.html">MDC</a> is enabled.
     *
     * @param useMDCLogging <tt>true</tt> to enable MDC logging, <tt>false</tt> to disable
     */
    void setUseMDCLogging(Boolean useMDCLogging);

    /**
     * Gets the pattern used for determine which custom MDC keys to propagate during message routing when the routing
     * engine continues routing asynchronously for the given message. Setting this pattern to <tt>*</tt> will propagate
     * all custom keys. Or setting the pattern to <tt>foo*,bar*</tt> will propagate any keys starting with either foo or
     * bar. Notice that a set of standard Zwangine MDC keys are always propagated which starts with <tt>zwangine.</tt> as key
     * name.
     * <p/>
     * The match rules are applied in this order (case insensitive):
     * <ul>
     * <li>exact match, returns true</li>
     * <li>wildcard match (pattern ends with a * and the name starts with the pattern), returns true</li>
     * <li>regular expression match, returns true</li>
     * <li>otherwise returns false</li>
     * </ul>
     */
    String getMDCLoggingKeysPattern();

    /**
     * Sets the pattern used for determine which custom MDC keys to propagate during message routing when the routing
     * engine continues routing asynchronously for the given message. Setting this pattern to <tt>*</tt> will propagate
     * all custom keys. Or setting the pattern to <tt>foo*,bar*</tt> will propagate any keys starting with either foo or
     * bar. Notice that a set of standard Zwangine MDC keys are always propagated which starts with <tt>zwangine.</tt> as key
     * name.
     * <p/>
     * The match rules are applied in this order (case insensitive):
     * <ul>
     * <li>exact match, returns true</li>
     * <li>wildcard match (pattern ends with a * and the name starts with the pattern), returns true</li>
     * <li>regular expression match, returns true</li>
     * <li>otherwise returns false</li>
     * </ul>
     *
     * @param pattern the pattern
     */
    void setMDCLoggingKeysPattern(String pattern);

    /**
     * To use a custom tracing logging format.
     *
     * The default format (arrow, workflowId, label) is: %-4.4s [%-12.12s] [%-33.33s]
     */
    String getTracingLoggingFormat();

    /**
     * To use a custom tracing logging format.
     *
     * The default format (arrow, workflowId, label) is: %-4.4s [%-12.12s] [%-33.33s]
     *
     * @param format the logging format
     */
    void setTracingLoggingFormat(String format);

    /**
     * Whether tracing should trace inner details from workflow templates (or kamelets). Turning this on increases the
     * verbosity of tracing by including events from internal workflows in the templates or kamelets.
     */
    void setTracingTemplates(boolean tracingTemplates);

    /**
     * Whether tracing should trace inner details from workflow templates (or kamelets). Turning this off can reduce the
     * verbosity of tracing when using many workflow templates, and allow to focus on tracing your own Zwangine workflows only.
     */
    boolean isTracingTemplates();

    /**
     * If dumping is enabled then Zwangine will during startup dump all loaded workflows (incl rests and workflow templates)
     * represented as XML DSL into the log. This is intended for trouble shooting or to assist during development.
     *
     * Sensitive information that may be configured in the workflow endpoints could potentially be included in the dump
     * output and is therefore not recommended to be used for production usage.
     *
     * This requires to have zwangine-xml-jaxb on the classpath to be able to dump the workflows as XML.
     *
     * @return <tt>xml</tt>, or <tt>yaml</tt> if dumping is enabled
     */
    String getDumpWorkflows();

    /**
     * If dumping is enabled then Zwangine will during startup dump all loaded workflows (incl rests and workflow templates)
     * represented as XML/YAML DSL into the log. This is intended for trouble shooting or to assist during development.
     *
     * Sensitive information that may be configured in the workflow endpoints could potentially be included in the dump
     * output and is therefore not recommended being used for production usage.
     *
     * This requires to have zwangine-xml-io/zwangine-yaml-io on the classpath to be able to dump the workflows as XML/YAML.
     *
     * @param format xml or yaml (additional configuration can be specified using query parameters, eg
     *               ?include=all&uriAsParameters=true)
     */
    void setDumpWorkflows(String format);

    /**
     * Whether to enable using data type on Zwangine messages.
     * <p/>
     * Data type are automatic turned on if one or more workflows has been explicit configured with input and output types.
     * Otherwise, data type is default off.
     *
     * @return <tt>true</tt> if data type is enabled
     */
    Boolean isUseDataType();

    /**
     * Whether to enable using data type on Zwangine messages.
     * <p/>
     * Data type are automatic turned on if one or more workflows has been explicit configured with input and output types.
     * Otherwise, data type is default off.
     *
     * @param useDataType <tt>true</tt> to enable data type on Zwangine messages.
     */
    void setUseDataType(Boolean useDataType);

    /**
     * Whether or not breadcrumb is enabled.
     *
     * @return <tt>true</tt> if breadcrumb is enabled
     */
    Boolean isUseBreadcrumb();

    /**
     * Set whether breadcrumb is enabled.
     *
     * @param useBreadcrumb <tt>true</tt> to enable breadcrumb, <tt>false</tt> to disable
     */
    void setUseBreadcrumb(Boolean useBreadcrumb);

    /**
     * Gets the {@link StreamCachingStrategy} to use.
     */
    StreamCachingStrategy getStreamCachingStrategy();

    /**
     * Sets a custom {@link StreamCachingStrategy} to use.
     */
    void setStreamCachingStrategy(StreamCachingStrategy streamCachingStrategy);

    /**
     * Gets the {@link org.zenithblox.spi.RuntimeEndpointRegistry} to use, or <tt>null</tt> if none is in use.
     */
    RuntimeEndpointRegistry getRuntimeEndpointRegistry();

    /**
     * Sets a custom {@link org.zenithblox.spi.RuntimeEndpointRegistry} to use.
     */
    void setRuntimeEndpointRegistry(RuntimeEndpointRegistry runtimeEndpointRegistry);

    /**
     * Sets the global SSL context parameters.
     */
    void setSSLContextParameters(SSLContextParameters sslContextParameters);

    /**
     * Gets the global SSL context parameters if configured.
     */
    SSLContextParameters getSSLContextParameters();

    /**
     * Controls the level of information logged during startup (and shutdown) of {@link ZwangineContext}.
     */
    void setStartupSummaryLevel(StartupSummaryLevel startupSummaryLevel);

    /**
     * Controls the level of information logged during startup (and shutdown) of {@link ZwangineContext}.
     */
    StartupSummaryLevel getStartupSummaryLevel();

}
