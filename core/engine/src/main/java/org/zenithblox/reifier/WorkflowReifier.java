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
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.PropertyDefinition;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.processor.ContractAdvice;
import org.zenithblox.processor.WorkflowWorkflow;
import org.zenithblox.reifier.rest.RestBindingReifier;
import org.zenithblox.spi.*;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;

public class WorkflowReifier extends ProcessorReifier<WorkflowDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowReifier.class);

    private static final String[] RESERVED_PROPERTIES = new String[] {
            Workflow.ID_PROPERTY, Workflow.CUSTOM_ID_PROPERTY, Workflow.PARENT_PROPERTY,
            Workflow.DESCRIPTION_PROPERTY, Workflow.GROUP_PROPERTY, Workflow.NODE_PREFIX_ID_PROPERTY,
            Workflow.REST_PROPERTY, Workflow.CONFIGURATION_ID_PROPERTY };

    public WorkflowReifier(ZwangineContext zwangineContext, ProcessorDefinition<?> definition) {
        super(zwangineContext, (WorkflowDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        throw new UnsupportedOperationException("Not implemented for WorkflowDefinition");
    }

    public Workflow createWorkflow() {
        try {
            return doCreateWorkflow();
        } catch (FailedToCreateWorkflowException e) {
            throw e;
        } catch (Exception e) {
            // wrap in exception which provide more details about which workflow
            // was failing
            throw new FailedToCreateWorkflowException(definition.getId(), definition.toString(), e);
        }
    }

    // Implementation methods
    // -------------------------------------------------------------------------
    protected Workflow doCreateWorkflow() throws Exception {
        // resolve endpoint
        Endpoint endpoint = definition.getInput().getEndpoint();
        if (endpoint == null) {
            EndpointConsumerResolver def = definition.getInput().getEndpointConsumerBuilder();
            if (def != null) {
                endpoint = def.resolve(zwangineContext);
            } else {
                endpoint = resolveEndpoint(definition.getInput().getEndpointUri());
            }
        }

        // create workflow
        String id = definition.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
        String desc = definition.getDescriptionText();

        Workflow workflow = PluginHelper.getWorkflowFactory(zwangineContext).createWorkflow(zwangineContext, definition, id,
                desc, endpoint, definition.getResource());

        // configure error handler
        workflow.setErrorHandlerFactory(definition.getErrorHandlerFactory());

        // configure variable
        String variable = parseString(definition.getInput().getVariableReceive());
        if (variable != null) {
            // when using variable we need to turn on original message
            workflow.setAllowUseOriginalMessage(true
            );
        }

        // configure tracing
        if (definition.getTrace() != null) {
            Boolean isTrace = parseBoolean(definition.getTrace());
            if (isTrace != null) {
                workflow.setTracing(isTrace);
                if (isTrace) {
                    LOG.debug("Tracing is enabled on workflow: {}", definition.getId());
                    // tracing is added in the DefaultChannel so we can enable
                    // it on the fly
                }
            }
        }

        // configure message history
        if (definition.getMessageHistory() != null) {
            Boolean isMessageHistory = parseBoolean(definition.getMessageHistory());
            if (isMessageHistory != null) {
                workflow.setMessageHistory(isMessageHistory);
                if (isMessageHistory) {
                    LOG.debug("Message history is enabled on workflow: {}", definition.getId());
                }
            }
        }

        // configure Log EIP mask
        if (definition.getLogMask() != null) {
            Boolean isLogMask = parseBoolean(definition.getLogMask());
            if (isLogMask != null) {
                workflow.setLogMask(isLogMask);
                if (isLogMask) {
                    LOG.debug("Security mask for Logging is enabled on workflow: {}", definition.getId());
                }
            }
        }

        // configure stream caching
        if (definition.getStreamCache() != null) {
            Boolean isStreamCache = parseBoolean(definition.getStreamCache());
            if (isStreamCache != null) {
                workflow.setStreamCaching(isStreamCache);
                if (isStreamCache) {
                    LOG.debug("StreamCaching is enabled on workflow: {}", definition.getId());
                }
            }
        }

        // configure delayer
        if (definition.getDelayer() != null) {
            Long delayer = parseDuration(definition.getDelayer());
            if (delayer != null) {
                workflow.setDelayer(delayer);
                if (delayer > 0) {
                    LOG.debug("Delayer is enabled with: {} ms. on workflow: {}", delayer, definition.getId());
                } else {
                    LOG.debug("Delayer is disabled on workflow: {}", definition.getId());
                }
            }
        }

        // configure auto startup
        Boolean isAutoStartup = parseBoolean(definition.getAutoStartup());

        // configure startup order
        Integer startupOrder = definition.getStartupOrder();

        // configure shutdown
        if (definition.getShutdownWorkflow() != null) {
            LOG.debug("Using ShutdownWorkflow {} on workflow: {}", definition.getShutdownWorkflow(), definition.getId());
            workflow.setShutdownWorkflow(parse(ShutdownWorkflow.class, definition.getShutdownWorkflow()));
        }
        if (definition.getShutdownRunningTask() != null) {
            LOG.debug("Using ShutdownRunningTask {} on workflow: {}", definition.getShutdownRunningTask(), definition.getId());
            workflow.setShutdownRunningTask(parse(ShutdownRunningTask.class, definition.getShutdownRunningTask()));
        }

        // should inherit the intercept strategies we have defined
        workflow.getInterceptStrategies().addAll(definition.getInterceptStrategies());

        // notify workflow context created
        for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
            strategy.onWorkflowContextCreate(workflow);
        }

        // validate workflow has output processors
        if (!hasOutputs(definition.getOutputs(), true)) {
            String at = definition.getInput().toString();
            Exception cause = new IllegalArgumentException(
                    "Workflow " + definition.getId() + " has no output processors."
                                                           + " You need to add outputs to the workflow such as to(\"log:foo\").");
            throw new FailedToCreateWorkflowException(definition.getId(), definition.toString(), at, cause);
        }

        List<ProcessorDefinition<?>> list = new ArrayList<>(definition.getOutputs());
        for (ProcessorDefinition<?> output : list) {
            try {
                ProcessorReifier<?> reifier = ProcessorReifier.reifier(workflow, output);

                // ensure node has id assigned
                String outputId
                        = output.idOrCreate(zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
                String eip = reifier.getClass().getSimpleName().replace("Reifier", "");
                StartupStep step = zwangineContext.getZwangineContextExtension().getStartupStepRecorder()
                        .beginStep(ProcessorReifier.class, outputId, "Create " + eip + " Processor");

                reifier.addWorkflows();

                zwangineContext.getZwangineContextExtension().getStartupStepRecorder().endStep(step);
            } catch (Exception e) {
                throw new FailedToCreateWorkflowException(definition.getId(), definition.toString(), output.toString(), e);
            }
        }

        // now lets turn all the event driven consumer processors into a single workflow
        List<Processor> eventDrivenProcessors = workflow.getEventDrivenProcessors();
        if (eventDrivenProcessors.isEmpty()) {
            return null;
        }

        // Set workflow properties
        Map<String, Object> workflowProperties = computeWorkflowProperties();

        // always use a workflow even if there are only 1 processor as the workflow
        // handles preparing the response from the exchange in regard to IN vs OUT messages etc
        WorkflowWorkflow target = new WorkflowWorkflow(zwangineContext, eventDrivenProcessors);
        target.setWorkflowId(id);

        // and wrap it in a unit of work so the UoW is on the top, so the entire workflow will be in the same UoW
        InternalProcessor internal = PluginHelper.getInternalProcessorFactory(zwangineContext)
                .addUnitOfWorkProcessorAdvice(zwangineContext, target, workflow);

        // configure workflow policy
        if (definition.getWorkflowPolicies() != null && !definition.getWorkflowPolicies().isEmpty()) {
            for (WorkflowPolicy policy : definition.getWorkflowPolicies()) {
                LOG.debug("WorkflowPolicy is enabled: {} on workflow: {}", policy, definition.getId());
                workflow.getWorkflowPolicyList().add(policy);
            }
        }
        if (definition.getWorkflowPolicyRef() != null) {
            StringTokenizer policyTokens = new StringTokenizer(definition.getWorkflowPolicyRef(), ",");
            while (policyTokens.hasMoreTokens()) {
                String ref = policyTokens.nextToken().trim();
                WorkflowPolicy policy = mandatoryLookup(ref, WorkflowPolicy.class);
                LOG.debug("WorkflowPolicy is enabled: {} on workflow: {}", policy, definition.getId());
                workflow.getWorkflowPolicyList().add(policy);
            }
        }
        if (zwangineContext.getWorkflowPolicyFactories() != null) {
            for (WorkflowPolicyFactory factory : zwangineContext.getWorkflowPolicyFactories()) {
                WorkflowPolicy policy = factory.createWorkflowPolicy(zwangineContext, definition.getId(), definition);
                if (policy != null) {
                    LOG.debug("WorkflowPolicy is enabled: {} on workflow: {}", policy, definition.getId());
                    workflow.getWorkflowPolicyList().add(policy);
                }
            }
        }
        // and then optionally add workflow policy processor if a custom policy is set
        List<WorkflowPolicy> workflowPolicyList = workflow.getWorkflowPolicyList();
        if (workflowPolicyList != null && !workflowPolicyList.isEmpty()) {
            for (WorkflowPolicy policy : workflowPolicyList) {
                // add policy as service if we have not already done that (eg possible if two workflows have the same service)
                // this ensures Zwangine can control the lifecycle of the policy
                if (!zwangineContext.hasService(policy)) {
                    try {
                        // inject workflow
                        if (policy instanceof WorkflowAware ra) {
                            ra.setWorkflow(workflow);
                        }
                        zwangineContext.addService(policy);
                    } catch (Exception e) {
                        throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                    }
                }
            }

            internal.addWorkflowPolicyAdvice(workflowPolicyList);
        }

        // wrap in workflow inflight processor to track number of inflight exchanges for the workflow
        internal.addWorkflowInflightRepositoryAdvice(zwangineContext.getInflightRepository(), workflow.getWorkflowId());

        // wrap in JMX instrumentation processor that is used for performance stats
        ManagementInterceptStrategy managementInterceptStrategy = workflow.getManagementInterceptStrategy();
        if (managementInterceptStrategy != null) {
            internal.addManagementInterceptStrategy(managementInterceptStrategy.createProcessor("workflow"));
        }

        // wrap in workflow lifecycle
        internal.addWorkflowLifecycleAdvice();

        // add advices
        if (definition.getRestBindingDefinition() != null) {
            try {
                // when disabling bean or processor we should also disable rest-dsl binding advice
                boolean disabled
                        = "true".equalsIgnoreCase(workflow.getZwangineContext().getGlobalOption(DISABLE_BEAN_OR_PROCESS_PROCESSORS));
                if (!disabled) {
                    internal.addAdvice(
                            new RestBindingReifier(workflow, definition.getRestBindingDefinition()).createRestBindingAdvice());
                }
            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        }

        // wrap in contract
        if (definition.getInputType() != null || definition.getOutputType() != null) {
            Contract contract = new Contract();
            if (definition.getInputType() != null) {
                contract.setInputType(parseString(definition.getInputType().getUrn()));
                contract.setValidateInput(parseBoolean(definition.getInputType().getValidate(), false));
            }
            if (definition.getOutputType() != null) {
                contract.setOutputType(parseString(definition.getOutputType().getUrn()));
                contract.setValidateOutput(parseBoolean(definition.getOutputType().getValidate(), false));
            }
            internal.addAdvice(new ContractAdvice(contract));
            // make sure to enable data type as its in use when using
            // input/output types on workflows
            zwangineContext.setUseDataType(true);
        }

        // wrap with variable
        if (variable != null) {
            internal.addAdvice(new VariableAdvice(variable));
        }

        // and create the workflow that wraps all of this
        workflow.setProcessor(internal);
        workflow.getProperties().putAll(workflowProperties);
        workflow.setStartupOrder(startupOrder);
        if (isAutoStartup != null) {
            LOG.debug("Using AutoStartup {} on workflow: {}", isAutoStartup, definition.getId());
            workflow.setAutoStartup(isAutoStartup);
        }

        // after the workflow is created then set the workflow on the policy processor(s) so we get hold of it
        internal.setWorkflowOnAdvices(workflow);

        // invoke init on workflow policy
        if (workflowPolicyList != null && !workflowPolicyList.isEmpty()) {
            for (WorkflowPolicy policy : workflowPolicyList) {
                policy.onInit(workflow);
            }
        }

        // inject the workflow error handler for processors that are error handler aware
        // this needs to be done here at the end because the workflow may be transactional and have a transaction error handler
        // automatic be configured which some EIPs like Multicast/RecipientList needs to be using for special fine-grained error handling
        ErrorHandlerFactory builder = workflow.getErrorHandlerFactory();
        Processor errorHandler = ((ModelZwangineContext) zwangineContext).getModelReifierFactory().createErrorHandler(workflow,
                builder, null);
        prepareErrorHandlerAware(workflow, errorHandler);

        // only during startup phase
        if (zwangineContext.getStatus().ordinal() < ServiceStatus.Started.ordinal()) {
            // okay workflow has been created from the model, then the model is no longer needed, and we can de-reference
            zwangineContext.getZwangineContextExtension().addBootstrap(workflow::clearWorkflowModel);
        }

        if (definition.getWorkflowTemplateContext() != null) {
            // make workflow stop beans from the local repository (workflow templates / kamelets)
            Service wrapper = new ServiceSupport() {
                @Override
                protected void doStop() throws Exception {
                    close();
                }

                @Override
                public void close() throws IOException {
                    BeanRepository repo = definition.getWorkflowTemplateContext().getLocalBeanRepository();
                    if (repo instanceof Closeable obj) {
                        IOHelper.close(obj);
                    }
                    super.close();
                }
            };
            workflow.addService(wrapper, true);
        }

        return workflow;
    }

    private void prepareErrorHandlerAware(Workflow workflow, Processor errorHandler) {
        List<Processor> processors = workflow.filter("*");
        for (Processor p : processors) {
            if (p instanceof ErrorHandlerAware errorHandlerAware) {
                errorHandlerAware.setErrorHandler(errorHandler);
            }
        }
    }

    protected Map<String, Object> computeWorkflowProperties() {
        Map<String, Object> workflowProperties = new HashMap<>();
        workflowProperties.put(Workflow.ID_PROPERTY, definition.getId());
        workflowProperties.put(Workflow.CUSTOM_ID_PROPERTY, Boolean.toString(definition.hasCustomIdAssigned()));
        workflowProperties.put(Workflow.PARENT_PROPERTY, Integer.toHexString(definition.hashCode()));
        workflowProperties.put(Workflow.DESCRIPTION_PROPERTY, definition.getDescriptionText());
        if (definition.getGroup() != null) {
            workflowProperties.put(Workflow.GROUP_PROPERTY, definition.getGroup());
        }
        if (definition.getNodePrefixId() != null) {
            workflowProperties.put(Workflow.NODE_PREFIX_ID_PROPERTY, definition.getNodePrefixId());
        }
        String rest = Boolean.toString(definition.isRest() != null && definition.isRest());
        workflowProperties.put(Workflow.REST_PROPERTY, rest);
        String template = Boolean.toString(definition.isTemplate() != null && definition.isTemplate());
        workflowProperties.put(Workflow.TEMPLATE_PROPERTY, template);
        String kamelet = Boolean.toString(definition.isKamelet() != null && definition.isKamelet());
        workflowProperties.put(Workflow.KAMELET_PROPERTY, kamelet);
        if (definition.getAppliedWorkflowConfigurationIds() != null) {
            workflowProperties.put(Workflow.CONFIGURATION_ID_PROPERTY,
                    String.join(",", definition.getAppliedWorkflowConfigurationIds()));
        }

        List<PropertyDefinition> properties = definition.getWorkflowProperties();
        if (properties != null) {
            for (PropertyDefinition prop : properties) {
                try {
                    final String key = parseString(prop.getKey());
                    final String val = parseString(prop.getValue());
                    for (String property : RESERVED_PROPERTIES) {
                        if (property.equalsIgnoreCase(key)) {
                            throw new IllegalArgumentException(
                                    "Cannot set workflow property " + property + " as it is a reserved property");
                        }
                    }
                    workflowProperties.put(key, val);
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                }
            }
        }
        return workflowProperties;
    }

    /**
     * Advice for moving message body into a variable when using variableReceive mode
     */
    private static class VariableAdvice implements ZwangineInternalProcessorAdvice<Object> {

        private final String name;

        public VariableAdvice(String name) {
            this.name = name;
        }

        @Override
        public Object before(Exchange exchange) throws Exception {
            // move body to variable
            ExchangeHelper.setVariableFromMessageBodyAndHeaders(exchange, name, exchange.getMessage());
            exchange.getMessage().setBody(null);
            return null;
        }

        @Override
        public void after(Exchange exchange, Object data) throws Exception {
            // noop
        }

        @Override
        public boolean hasState() {
            return false;
        }
    }

}
