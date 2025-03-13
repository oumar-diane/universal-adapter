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
import org.zenithblox.WorkflowTemplateContext;
import org.zenithblox.model.cloud.ServiceCallConfigurationDefinition;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.transformer.TransformerDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.spi.ModelReifierFactory;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Model interface
 */
public interface Model {

    /**
     * Adds the given model lifecycle strategy to be used.
     *
     * @param modelLifecycleStrategy the strategy
     */
    void addModelLifecycleStrategy(ModelLifecycleStrategy modelLifecycleStrategy);

    /**
     * Returns the model lifecycle strategies used to handle lifecycle notifications
     *
     * @return the lifecycle strategies
     */
    List<ModelLifecycleStrategy> getModelLifecycleStrategies();

    /**
     * Adds a collection of workflow configuration definitions to the context
     *
     * @param workflowsConfigurations the workflow configuration(s) definition to add
     */
    void addWorkflowConfigurations(List<WorkflowConfigurationDefinition> workflowsConfigurations);

    /**
     * Adds a single workflow configuration definition to the context
     *
     * @param workflowsConfiguration the workflow configuration to add
     */
    void addWorkflowConfiguration(WorkflowConfigurationDefinition workflowsConfiguration);

    /**
     * Returns a list of the current workflow configuration definitions
     *
     * @return list of the current workflow configuration definitions
     */
    List<WorkflowConfigurationDefinition> getWorkflowConfigurationDefinitions();

    /**
     * Removes a workflow configuration from the context
     *
     * @param  workflowConfigurationDefinition workflow configuration to remove
     * @throws Exception                    if the workflow configuration could not be removed for whatever reason
     */
    void removeWorkflowConfiguration(WorkflowConfigurationDefinition workflowConfigurationDefinition) throws Exception;

    /**
     * Gets the workflow configuration definition with the given id
     *
     * @param  id id of the workflow configuration
     * @return    the workflow configuration definition or <tt>null</tt> if not found
     */
    WorkflowConfigurationDefinition getWorkflowConfigurationDefinition(String id);

    /**
     * Returns a list of the current workflow definitions
     *
     * @return list of the current workflow definitions
     */
    List<WorkflowDefinition> getWorkflowDefinitions();

    /**
     * Gets the workflow definition with the given id
     *
     * @param  id id of the workflow
     * @return    the workflow definition or <tt>null</tt> if not found
     */
    WorkflowDefinition getWorkflowDefinition(String id);

    /**
     * Adds a collection of workflow definitions to the context
     * <p/>
     * <b>Important: </b> Each workflow in the same {@link ZwangineContext} must have an <b>unique</b> workflow id. If you use
     * the API from {@link ZwangineContext} or {@link Model} to add workflows, then any new workflows which has a workflow id that
     * matches an old workflow, then the old workflow is replaced by the new workflow.
     *
     * @param  workflowDefinitions the workflow(s) definition to add
     * @throws Exception        if the workflow definitions could not be added for whatever reason
     */
    void addWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception;

    /**
     * Add a workflow definition to the context
     * <p/>
     * <b>Important: </b> Each workflow in the same {@link ZwangineContext} must have an <b>unique</b> workflow id. If you use
     * the API from {@link ZwangineContext} or {@link Model} to add workflows, then any new workflows which has a workflow id that
     * matches an old workflow, then the old workflow is replaced by the new workflow.
     *
     * @param  workflowDefinition the workflow definition to add
     * @throws Exception       if the workflow definition could not be added for whatever reason
     */
    void addWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception;

    /**
     * Removes a collection of workflow definitions from the context - stopping any previously running workflows if any of
     * them are actively running
     *
     * @param  workflowDefinitions workflow(s) definitions to remove
     * @throws Exception        if the workflow definitions could not be removed for whatever reason
     */
    void removeWorkflowDefinitions(Collection<WorkflowDefinition> workflowDefinitions) throws Exception;

    /**
     * Removes a workflow definition from the context - stopping any previously running workflows if any of them are actively
     * running
     *
     * @param  workflowDefinition workflow definition to remove
     * @throws Exception       if the workflow definition could not be removed for whatever reason
     */
    void removeWorkflowDefinition(WorkflowDefinition workflowDefinition) throws Exception;

    /**
     * Returns a list of the current workflow template definitions
     *
     * @return list of the current workflow template definitions
     */
    List<WorkflowTemplateDefinition> getWorkflowTemplateDefinitions();

    /**
     * Gets the workflow template definition with the given id
     *
     * @param  id id of the workflow template
     * @return    the workflow template definition or <tt>null</tt> if not found
     */
    WorkflowTemplateDefinition getWorkflowTemplateDefinition(String id);

    /**
     * Adds a collection of workflow template definitions to the context
     * <p/>
     * <b>Important: </b> Each workflow in the same {@link ZwangineContext} must have an <b>unique</b> workflow template id.
     *
     * @param  workflowTemplateDefinitions the workflow template(s) definition to add
     * @throws Exception                if the workflow template definitions could not be added for whatever reason
     */
    void addWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception;

    /**
     * Add a workflow definition to the context
     * <p/>
     * <b>Important: </b> Each workflow template in the same {@link ZwangineContext} must have an <b>unique</b> workflow id.
     *
     * @param  workflowTemplateDefinition the workflow template definition to add
     * @throws Exception               if the workflow template definition could not be added for whatever reason
     */
    void addWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception;

    /**
     * Removes a collection of workflow template definitions from the context
     *
     * @param  workflowTemplateDefinitions workflow template(s) definitions to remove
     * @throws Exception                if the workflow template definitions could not be removed for whatever reason
     */
    void removeWorkflowTemplateDefinitions(Collection<WorkflowTemplateDefinition> workflowTemplateDefinitions) throws Exception;

    /**
     * Removes a workflow template definition from the context
     *
     * @param  workflowTemplateDefinition workflow template definition to remove
     * @throws Exception               if the workflow template definition could not be removed for whatever reason
     */
    void removeWorkflowTemplateDefinition(WorkflowTemplateDefinition workflowTemplateDefinition) throws Exception;

    /**
     * Removes the workflow templates matching the pattern - stopping any previously running workflows if any of them are
     * actively running
     *
     * @param pattern pattern, such as * for all, or foo* to remove all foo templates
     */
    void removeWorkflowTemplateDefinitions(String pattern) throws Exception;

    /**
     * Add a converter to translate a {@link WorkflowTemplateDefinition} to a {@link WorkflowDefinition}.
     *
     * @param templateIdPattern the workflow template ut to whom a pattern should eb applied
     * @param converter         the {@link WorkflowTemplateDefinition.Converter} used to convert a
     *                          {@link WorkflowTemplateDefinition} to a {@link WorkflowDefinition}
     */
    void addWorkflowTemplateDefinitionConverter(String templateIdPattern, WorkflowTemplateDefinition.Converter converter);

    /**
     * Adds a new workflow from a given workflow template
     *
     * @param  workflowId         the id of the new workflow to add (optional)
     * @param  workflowTemplateId the id of the workflow template (mandatory)
     * @param  parameters      parameters to use for the workflow template when creating the new workflow
     * @return                 the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception       is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(String workflowId, String workflowTemplateId, Map<String, Object> parameters) throws Exception;

    /**
     * Adds a new workflow from a given workflow template
     *
     * @param  workflowId         the id of the new workflow to add (optional)
     * @param  workflowTemplateId the id of the workflow template (mandatory)
     * @param  prefixId        prefix to use when assigning workflow and node IDs (optional)
     * @param  parameters      parameters to use for the workflow template when creating the new workflow
     * @return                 the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception       is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId,
            Map<String, Object> parameters)
            throws Exception;

    /**
     * Adds a new workflow from a given workflow template
     *
     * @param  workflowId              the id of the new workflow to add (optional)
     * @param  workflowTemplateId      the id of the workflow template (mandatory)
     * @param  prefixId             prefix to use when assigning workflow and node IDs (optional)
     * @param  workflowTemplateContext the workflow template context (mandatory)
     * @return                      the id of the workflow added (for example when an id was auto assigned)
     * @throws Exception            is thrown if error creating and adding the new workflow
     */
    String addWorkflowFromTemplate(
            String workflowId, String workflowTemplateId, String prefixId,
            WorkflowTemplateContext workflowTemplateContext)
            throws Exception;

    /**
     * Adds a new workflow from a given kamelet
     *
     * @param  workflowId           the id of the new workflow to add (optional)
     * @param  workflowTemplateId   the id of the kamelet workflow template (mandatory)
     * @param  prefixId          prefix to use when assigning workflow and node IDs (optional)
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
     * Adds a new workflow from a given templated workflow definition
     *
     * @param  templatedWorkflowDefinition the templated workflow definition to add as a workflow (mandatory)
     * @throws Exception                is thrown if error creating and adding the new workflow
     */
    void addWorkflowFromTemplatedWorkflow(TemplatedWorkflowDefinition templatedWorkflowDefinition) throws Exception;

    /**
     * Adds new workflows from a given templated workflow definitions
     *
     * @param  templatedWorkflowDefinitions the templated workflow definitions to add as a workflow (mandatory)
     * @throws Exception                 is thrown if error creating and adding the new workflow
     */
    default void addWorkflowFromTemplatedWorkflows(
            Collection<TemplatedWorkflowDefinition> templatedWorkflowDefinitions)
            throws Exception {
        ObjectHelper.notNull(templatedWorkflowDefinitions, "templatedWorkflowDefinitions");
        for (TemplatedWorkflowDefinition templatedWorkflowDefinition : templatedWorkflowDefinitions) {
            addWorkflowFromTemplatedWorkflow(templatedWorkflowDefinition);
        }
    }

    /**
     * Returns a list of the current REST definitions
     *
     * @return list of the current REST definitions
     */
    List<RestDefinition> getRestDefinitions();

    /**
     * Adds a collection of rest definitions to the context
     *
     * @param  restDefinitions the rest(s) definition to add
     * @param  addToWorkflows     whether the rests should also automatically be added as workflows
     * @throws Exception       if the rest definitions could not be created for whatever reason
     */
    void addRestDefinitions(Collection<RestDefinition> restDefinitions, boolean addToWorkflows) throws Exception;

    /**
     * Sets the data formats that can be referenced in the workflows.
     *
     * @param dataFormats the data formats
     */
    void setDataFormats(Map<String, DataFormatDefinition> dataFormats);

    /**
     * Gets the data formats that can be referenced in the workflows.
     *
     * @return the data formats available
     */
    Map<String, DataFormatDefinition> getDataFormats();

    /**
     * Resolve a data format definition given its name
     *
     * @param  name the data format definition name or a reference to it in the {@link org.zenithblox.spi.Registry}
     * @return      the resolved data format definition, or <tt>null</tt> if not found
     */
    DataFormatDefinition resolveDataFormatDefinition(String name);

    /**
     * Gets the processor definition from any of the workflows which with the given id
     *
     * @param  id id of the processor definition
     * @return    the processor definition or <tt>null</tt> if not found
     */
    ProcessorDefinition<?> getProcessorDefinition(String id);

    /**
     * Gets the processor definition from any of the workflows which with the given id
     *
     * @param  id                 id of the processor definition
     * @param  type               the processor definition type
     * @return                    the processor definition or <tt>null</tt> if not found
     * @throws ClassCastException is thrown if the type is not correct type
     */
    <T extends ProcessorDefinition<T>> T getProcessorDefinition(String id, Class<T> type);

    /**
     * Sets the validators that can be referenced in the workflows.
     *
     * @param validators the validators
     */
    void setValidators(List<ValidatorDefinition> validators);

    /**
     * Gets the Resilience4j configuration by the given name. If no name is given the default configuration is returned,
     * see <tt>setResilience4jConfiguration</tt>
     *
     * @param  id id of the configuration, or <tt>null</tt> to return the default configuration
     * @return    the configuration, or <tt>null</tt> if no configuration has been registered
     */
    Resilience4jConfigurationDefinition getResilience4jConfiguration(String id);

    /**
     * Sets the default Resilience4j configuration
     *
     * @param configuration the configuration
     */
    void setResilience4jConfiguration(Resilience4jConfigurationDefinition configuration);

    /**
     * Sets the Resilience4j configurations
     *
     * @param configurations the configuration list
     */
    void setResilience4jConfigurations(List<Resilience4jConfigurationDefinition> configurations);

    /**
     * Adds the Resilience4j configuration
     *
     * @param id            name of the configuration
     * @param configuration the configuration
     */
    void addResilience4jConfiguration(String id, Resilience4jConfigurationDefinition configuration);

    /**
     * Gets the MicroProfile Fault Tolerance configuration by the given name. If no name is given the default
     * configuration is returned, see <tt>setFaultToleranceConfigurationDefinition</tt>
     *
     * @param  id id of the configuration, or <tt>null</tt> to return the default configuration
     * @return    the configuration, or <tt>null</tt> if no configuration has been registered
     */
    FaultToleranceConfigurationDefinition getFaultToleranceConfiguration(String id);

    /**
     * Sets the default MicroProfile Fault Tolerance configuration
     *
     * @param configuration the configuration
     */
    void setFaultToleranceConfiguration(FaultToleranceConfigurationDefinition configuration);

    /**
     * Sets the MicroProfile Fault Tolerance configurations
     *
     * @param configurations the configuration list
     */
    void setFaultToleranceConfigurations(List<FaultToleranceConfigurationDefinition> configurations);

    /**
     * Adds the MicroProfile Fault Tolerance configuration
     *
     * @param id            name of the configuration
     * @param configuration the configuration
     */
    void addFaultToleranceConfiguration(String id, FaultToleranceConfigurationDefinition configuration);

    /**
     * Gets the validators that can be referenced in the workflows.
     *
     * @return the validators available
     */
    List<ValidatorDefinition> getValidators();

    /**
     * Sets the transformers that can be referenced in the workflows.
     *
     * @param transformers the transformers
     */
    void setTransformers(List<TransformerDefinition> transformers);

    /**
     * Gets the transformers that can be referenced in the workflows.
     *
     * @return the transformers available
     */
    List<TransformerDefinition> getTransformers();

    /**
     * Gets the service call configuration by the given name. If no name is given the default configuration is returned,
     * see <tt>setServiceCallConfiguration</tt>
     *
     * @param  serviceName name of service, or <tt>null</tt> to return the default configuration
     * @return             the configuration, or <tt>null</tt> if no configuration has been registered
     */
    ServiceCallConfigurationDefinition getServiceCallConfiguration(String serviceName);

    /**
     * Sets the default service call configuration
     *
     * @param configuration the configuration
     */
    void setServiceCallConfiguration(ServiceCallConfigurationDefinition configuration);

    /**
     * Sets the service call configurations
     *
     * @param configurations the configuration list
     */
    void setServiceCallConfigurations(List<ServiceCallConfigurationDefinition> configurations);

    /**
     * Adds the service call configuration
     *
     * @param serviceName   name of the service
     * @param configuration the configuration
     */
    void addServiceCallConfiguration(String serviceName, ServiceCallConfigurationDefinition configuration);

    /**
     * Used for filtering workflows workflows matching the given pattern, which follows the following rules: - Match by workflow
     * id - Match by workflow input endpoint uri The matching is using exact match, by wildcard and regular expression as
     * documented by {@link PatternHelper#matchPattern(String, String)}. For example to only include workflows which starts
     * with foo in their workflow id's, use: include=foo&#42; And to exclude workflows which starts from JMS endpoints, use:
     * exclude=jms:&#42; Exclude takes precedence over include.
     *
     * @param include the include pattern
     * @param exclude the exclude pattern
     */
    void setWorkflowFilterPattern(String include, String exclude);

    /**
     * Sets a custom workflow filter to use for filtering unwanted workflows when workflows are added.
     *
     * @param filter the filter
     */
    void setWorkflowFilter(Function<WorkflowDefinition, Boolean> filter);

    /**
     * Gets the current workflow filter
     *
     * @return the filter, or <tt>null</tt> if no custom filter has been configured.
     */
    Function<WorkflowDefinition, Boolean> getWorkflowFilter();

    /**
     * Gets the {@link ModelReifierFactory}
     */
    ModelReifierFactory getModelReifierFactory();

    /**
     * Sets a custom {@link ModelReifierFactory}
     */
    void setModelReifierFactory(ModelReifierFactory modelReifierFactory);

    /**
     * Adds the custom bean
     */
    void addCustomBean(BeanFactoryDefinition<?> bean);

    /**
     * Gets the custom beans
     */
    List<BeanFactoryDefinition<?>> getCustomBeans();

}
