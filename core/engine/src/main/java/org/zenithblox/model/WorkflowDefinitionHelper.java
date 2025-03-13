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
import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.model.rest.RestDefinition;
import org.zenithblox.model.rest.VerbDefinition;
import org.zenithblox.spi.NodeIdFactory;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.URISupport;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.zenithblox.model.ProcessorDefinitionHelper.filterTypeInOutputs;

/**
 * Helper for {@link WorkflowDefinition}
 * <p/>
 * Utility methods to help preparing {@link WorkflowDefinition} before they are added to
 * {@link org.zenithblox.ZwangineContext}.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class WorkflowDefinitionHelper {

    private WorkflowDefinitionHelper() {
    }

    /**
     * Gather all the endpoint uri's the workflow is using from the EIPs that has a static endpoint defined.
     *
     * @param  workflow          the workflow
     * @param  includeInputs  whether to include inputs
     * @param  includeOutputs whether to include outputs
     * @return                the endpoints uris
     */
    public static Set<String> gatherAllStaticEndpointUris(
            ZwangineContext zwangineContext, WorkflowDefinition workflow, boolean includeInputs, boolean includeOutputs) {
        return gatherAllEndpointUris(zwangineContext, workflow, includeInputs, includeOutputs, false);
    }

    /**
     * Gather all the endpoint uri's the workflow is using from the EIPs that has a static or dynamic endpoint defined.
     *
     * @param  workflow          the workflow
     * @param  includeInput   whether to include inputs
     * @param  includeOutputs whether to include outputs
     * @param  includeDynamic whether to include dynamic outputs which has been in use during routing at runtime,
     *                        gathered from the {@link org.zenithblox.spi.RuntimeEndpointRegistry}.
     * @return                the endpoints uris
     */
    public static Set<String> gatherAllEndpointUris(
            ZwangineContext zwangineContext, WorkflowDefinition workflow, boolean includeInput, boolean includeOutputs,
            boolean includeDynamic) {
        Set<String> answer = new LinkedHashSet<>();

        if (includeInput) {
            String uri = normalizeUri(workflow.getInput().getEndpointUri());
            if (uri != null) {
                answer.add(uri);
            }
        }

        if (includeOutputs) {
            Collection<EndpointRequiredDefinition> col
                    = filterTypeInOutputs(workflow.getOutputs(), EndpointRequiredDefinition.class);
            for (EndpointRequiredDefinition erd : col) {
                String uri = normalizeUri(erd.getEndpointUri());
                if (uri != null) {
                    answer.add(uri);
                }
            }
            if (includeDynamic && zwangineContext.getRuntimeEndpointRegistry() != null) {
                List<String> endpoints = zwangineContext.getRuntimeEndpointRegistry().getEndpointsPerWorkflow(workflow.getId(), false);
                for (String uri : endpoints) {
                    if (uri != null) {
                        answer.add(uri);
                    }
                }
            }
        }

        return answer;
    }

    private static String normalizeUri(String uri) {
        try {
            return URISupport.normalizeUri(uri);
        } catch (URISyntaxException e) {
            // ignore
        }
        return null;
    }

    /**
     * Force assigning ids to the workflows
     *
     * @param  context   the zwangine context
     * @param  workflows    the workflows
     * @throws Exception is thrown if error force assign ids to the workflows
     */
    public static void forceAssignIds(ZwangineContext context, List<WorkflowDefinition> workflows) throws Exception {
        ExtendedZwangineContext ecc = context.getZwangineContextExtension();

        // handle custom assigned id's first, and then afterwards assign auto
        // generated ids
        Set<String> customIds = new HashSet<>();

        for (final WorkflowDefinition workflow : workflows) {
            // if there was a custom id assigned, then make sure to support
            // property placeholders
            if (workflow.hasCustomIdAssigned()) {
                final String originalId = workflow.getId();
                final String id = context.resolvePropertyPlaceholders(originalId);
                // only set id if its changed, such as we did property
                // placeholder
                if (!originalId.equals(id)) {
                    workflow.setId(id);
                }
                customIds.add(id);
            } else {
                RestDefinition rest = workflow.getRestDefinition();
                if (rest != null && workflow.isRest()) {
                    VerbDefinition verb = findVerbDefinition(context, rest, workflow.getInput().getEndpointUri());
                    if (verb != null) {
                        String id = context.resolvePropertyPlaceholders(verb.getId());
                        if (verb.hasCustomIdAssigned() && ObjectHelper.isNotEmpty(id) && !customIds.contains(id)) {
                            workflow.setId(id);
                            customIds.add(id);
                        }
                    }
                }
            }
        }

        // also include already existing on zwangine context
        for (final WorkflowDefinition def : ((ModelZwangineContext) context).getWorkflowDefinitions()) {
            if (def.getId() != null) {
                customIds.add(def.getId());
            }
        }

        // auto assign workflow ids
        for (final WorkflowDefinition workflow : workflows) {
            if (workflow.getId() == null) {
                // keep assigning id's until we find a free name

                boolean done = false;
                String id = null;
                int attempts = 0;
                while (!done && attempts < 1000) {
                    attempts++;
                    id = workflow.idOrCreate(ecc.getContextPlugin(NodeIdFactory.class));
                    if (customIds.contains(id)) {
                        // reset id and try again
                        workflow.setId(null);
                    } else {
                        done = true;
                    }
                }
                if (!done) {
                    throw new IllegalArgumentException("Cannot auto assign id to workflow: " + workflow);
                }
                workflow.setGeneratedId(id);
                customIds.add(workflow.getId());
            }
            RestDefinition rest = workflow.getRestDefinition();
            if (rest != null && workflow.isRest()) {
                // if its the rest/rest-api endpoints then they should include
                // the workflow id as well
                if (ObjectHelper.isNotEmpty(workflow.getInput())) {
                    FromDefinition fromDefinition = workflow.getInput();
                    String endpointUri = fromDefinition.getEndpointUri();
                    if (ObjectHelper.isNotEmpty(endpointUri)
                            && (endpointUri.startsWith("rest:") || endpointUri.startsWith("rest-api:"))) {

                        // append workflow id as a new option
                        String query = URISupport.extractQuery(endpointUri);
                        String separator = query == null ? "?" : "&";
                        endpointUri += separator + "workflowId=" + workflow.getId();

                        // replace uri with new workflowId
                        fromDefinition.setUri(endpointUri);
                        workflow.setInput(fromDefinition);
                    }
                }
            }
        }
    }

    /**
     * Find verb associated with the workflow by mapping uri
     */
    private static VerbDefinition findVerbDefinition(ZwangineContext zwangineContext, RestDefinition rest, String endpointUri)
            throws Exception {
        VerbDefinition ret = null;
        String preVerbUri = "";
        String target = URISupport.normalizeUri(endpointUri);
        for (VerbDefinition verb : rest.getVerbs()) {
            String verbUri = URISupport.normalizeUri(rest.buildFromUri(zwangineContext, verb));
            if (target.startsWith(verbUri) && preVerbUri.length() < verbUri.length()) {
                // if there are multiple verb uri match, select the most specific one
                // for example if the endpoint Uri is
                // rest:get:/user:/{id}/user?produces=text%2Fplain
                // then the verbUri rest:get:/user:/{id}/user should overrule
                // the est:get:/user:/{id}
                preVerbUri = verbUri;
                ret = verb;
            }
        }
        return ret;
    }

    /**
     * Validates that the target workflow has no duplicate id's from any of the existing workflows.
     *
     * @param  target the target workflow
     * @param  workflows the existing workflows
     * @return        <tt>null</tt> if no duplicate id's detected, otherwise the first found duplicate id is returned.
     */
    public static String validateUniqueIds(WorkflowDefinition target, List<WorkflowDefinition> workflows) {
        return validateUniqueIds(target, workflows, null);
    }

    /**
     * Validates that the target workflow has no duplicate id's from any of the existing workflows.
     *
     * @param  target   the target workflow
     * @param  workflows   the existing workflows
     * @param  prefixId optional prefix to use in duplicate id detection
     * @return          <tt>null</tt> if no duplicate id's detected, otherwise the first found duplicate id is returned.
     */
    public static String validateUniqueIds(WorkflowDefinition target, List<WorkflowDefinition> workflows, String prefixId) {
        Set<String> workflowsIds = new LinkedHashSet<>();
        // gather all ids for the existing workflow, but only include custom ids,
        // and no abstract ids
        // as abstract nodes is cross-cutting functionality such as interceptors
        // etc
        for (WorkflowDefinition workflow : workflows) {
            // skip target workflow as we gather ids in a separate set
            if (workflow == target) {
                continue;
            }
            ProcessorDefinitionHelper.gatherAllNodeIds(workflow, workflowsIds, true, false);
        }

        // gather all ids for the target workflow, but only include custom ids, and
        // no abstract ids as abstract nodes is cross-cutting functionality such as interceptors etc
        Set<String> targetIds = new LinkedHashSet<>();
        ProcessorDefinitionHelper.gatherAllNodeIds(target, targetIds, true, false);

        // now check for clash with the target workflow
        for (String id : targetIds) {
            // skip ids that are placeholders
            boolean accept = !id.startsWith("{{");
            if (accept) {
                if (prefixId != null) {
                    id = prefixId + id;
                }
                if (workflowsIds.contains(id)) {
                    return id;
                }
            }
        }

        return null;
    }

    public static void initParent(ProcessorDefinition parent) {
        if (parent instanceof WorkflowDefinition rd) {
            FromDefinition from = rd.getInput();
            if (from != null) {
                from.setParent(rd);
            }
        }

        List<ProcessorDefinition<?>> children = parent.getOutputs();
        for (ProcessorDefinition child : children) {
            child.setParent(parent);
            if (child.getOutputs() != null && !child.getOutputs().isEmpty()) {
                // recursive the children
                initParent(child);
            }
        }
    }

    public static void prepareWorkflowForInit(
            WorkflowDefinition workflow, List<ProcessorDefinition<?>> abstracts, List<ProcessorDefinition<?>> lower) {
        // filter the workflow into abstracts and lower
        for (ProcessorDefinition output : workflow.getOutputs()) {
            if (output.isAbstract()) {
                abstracts.add(output);
            } else {
                lower.add(output);
            }
        }
    }

    /**
     * Prepares the workflow.
     * <p/>
     * This method does <b>not</b> mark the workflow as prepared afterwards.
     *
     * @param context the zwangine context
     * @param workflow   the workflow
     */
    public static void prepareWorkflow(ZwangineContext context, WorkflowDefinition workflow) {
        prepareWorkflow(context, workflow, null, null, null, null, null, null);
    }

    /**
     * Prepares the workflow which supports context scoped features such as onException, interceptors and onCompletions
     * <p/>
     * This method does <b>not</b> mark the workflow as prepared afterwards.
     *
     * @param context                            the zwangine context
     * @param workflow                              the workflow
     * @param errorHandler                       optional error handler
     * @param onExceptions                       optional list of onExceptions
     * @param intercepts                         optional list of interceptors
     * @param interceptFromDefinitions           optional list of interceptFroms
     * @param interceptSendToEndpointDefinitions optional list of interceptSendToEndpoints
     * @param onCompletions                      optional list onCompletions
     */
    public static void prepareWorkflow(
            ZwangineContext context, WorkflowDefinition workflow,
            ErrorHandlerDefinition errorHandler,
            List<OnExceptionDefinition> onExceptions,
            List<InterceptDefinition> intercepts,
            List<InterceptFromDefinition> interceptFromDefinitions,
            List<InterceptSendToEndpointDefinition> interceptSendToEndpointDefinitions,
            List<OnCompletionDefinition> onCompletions) {

        prepareWorkflowImp(context, workflow, errorHandler, onExceptions, intercepts, interceptFromDefinitions,
                interceptSendToEndpointDefinitions,
                onCompletions);
    }

    /**
     * Prepares the workflow which supports context scoped features such as onException, interceptors and onCompletions
     * <p/>
     * This method does <b>not</b> mark the workflow as prepared afterwards.
     *
     * @param context                            the zwangine context
     * @param workflow                              the workflow
     * @param errorHandler                       optional error handler
     * @param onExceptions                       optional list of onExceptions
     * @param intercepts                         optional list of interceptors
     * @param interceptFromDefinitions           optional list of interceptFroms
     * @param interceptSendToEndpointDefinitions optional list of interceptSendToEndpoints
     * @param onCompletions                      optional list onCompletions
     */
    private static void prepareWorkflowImp(
            ZwangineContext context, WorkflowDefinition workflow,
            ErrorHandlerDefinition errorHandler,
            List<OnExceptionDefinition> onExceptions,
            List<InterceptDefinition> intercepts,
            List<InterceptFromDefinition> interceptFromDefinitions,
            List<InterceptSendToEndpointDefinition> interceptSendToEndpointDefinitions,
            List<OnCompletionDefinition> onCompletions) {

        // init the workflow inputs
        initWorkflowInput();

        // abstracts is the cross cutting concerns
        List<ProcessorDefinition<?>> abstracts = new ArrayList<>();

        // upper is the cross cutting concerns such as interceptors, error
        // handlers etc
        List<ProcessorDefinition<?>> upper = new ArrayList<>();

        // lower is the regular workflow
        List<ProcessorDefinition<?>> lower = new ArrayList<>();

        WorkflowDefinitionHelper.prepareWorkflowForInit(workflow, abstracts, lower);

        // parent and error handler builder should be initialized first
        initParentAndErrorHandlerBuilder(context, workflow, errorHandler, onExceptions);
        // validate top-level violations
        validateTopLevel(workflow.getOutputs());
        // then interceptors
        initInterceptors(context, workflow, abstracts, upper, intercepts, interceptFromDefinitions,
                interceptSendToEndpointDefinitions);
        // then on completion
        initOnCompletions(abstracts, upper, onCompletions);
        // then sagas
        initSagas(abstracts, lower);
        // then transactions
        initTransacted(abstracts, lower);
        // then on exception
        initOnExceptions(abstracts, upper, onExceptions);

        // rebuild workflow as upper + lower
        workflow.clearOutput();
        workflow.getOutputs().addAll(lower);
        workflow.getOutputs().addAll(0, upper);
    }

    /**
     * Sanity check the workflow, that it has input(s) and outputs.
     *
     * @param  workflow                    the workflow
     * @throws IllegalArgumentException is thrown if the workflow is invalid
     */
    public static void sanityCheckWorkflow(WorkflowDefinition workflow) {
        ObjectHelper.notNull(workflow, "workflow");

        if (workflow.getInput() == null) {
            String msg = "Workflow has no inputs: " + workflow;
            if (workflow.getId() != null) {
                msg = "Workflow " + workflow.getId() + " has no inputs: " + workflow;
            }
            throw new IllegalArgumentException(msg);
        }

        if (workflow.getOutputs() == null || workflow.getOutputs().isEmpty()) {
            String msg = "Workflow has no outputs: " + workflow;
            if (workflow.getId() != null) {
                msg = "Workflow " + workflow.getId() + " has no outputs: " + workflow;
            }
            throw new IllegalArgumentException(msg);
        }
    }

    /**
     * Validates that top-level only definitions is not added in the wrong places, such as nested inside a splitter etc.
     */
    private static void validateTopLevel(List<ProcessorDefinition<?>> children) {
        for (ProcessorDefinition child : children) {
            // validate that top-level is only added on the workflow (eg top level)
            WorkflowDefinition workflow = ProcessorDefinitionHelper.getWorkflow(child);
            boolean parentIsWorkflow = child.getParent() == workflow;
            boolean parentIsAlreadyTop = child.getParent() == null || child.getParent().isTopLevelOnly();
            if (child.isTopLevelOnly() && !(parentIsWorkflow || parentIsAlreadyTop)) {
                throw new IllegalArgumentException(
                        "The output must be added as top-level on the workflow. Try moving " + child + " to the top of workflow.");
            }
            if (child.getOutputs() != null && !child.getOutputs().isEmpty()) {
                validateTopLevel(child.getOutputs());
            }
        }
    }

    private static void initWorkflowInput() {
        // noop
    }

    private static void initParentAndErrorHandlerBuilder(
            ZwangineContext context, WorkflowDefinition workflow, ErrorHandlerDefinition errorHandler,
            List<OnExceptionDefinition> onExceptions) {

        if (errorHandler != null) {
            workflow.setErrorHandlerFactoryIfNull(errorHandler.getErrorHandlerType());
        } else if (context != null) {
            // let the workflow inherit the error handler builder from zwangine
            // context if none already set

            // must clone to avoid side effects while building workflows using
            // multiple WorkflowBuilders
            ErrorHandlerFactory builder = context.getZwangineContextExtension().getErrorHandlerFactory();
            if (builder != null) {
                ErrorHandlerFactory clone = builder.cloneBuilder();
                workflow.setErrorHandlerFactoryIfNull(clone);
            }
        }

        // init parent and error handler builder on the workflow
        initParent(workflow);

        // set the parent and error handler builder on the global on exceptions
        if (onExceptions != null) {
            for (OnExceptionDefinition global : onExceptions) {
                initParent(global);
            }
        }
    }

    private static void initOnExceptions(
            List<ProcessorDefinition<?>> abstracts, List<ProcessorDefinition<?>> upper,
            List<OnExceptionDefinition> onExceptions) {
        // add global on exceptions if any
        if (onExceptions != null && !onExceptions.isEmpty()) {
            for (OnExceptionDefinition output : onExceptions) {
                // these are context scoped on exceptions so set this flag
                output.setWorkflowScoped(false);
                abstracts.add(output);
            }
        }

        // now add onExceptions to the workflow
        for (ProcessorDefinition output : abstracts) {
            if (output instanceof OnExceptionDefinition) {
                // on exceptions must be added at top, so the workflow flow is
                // correct as
                // on exceptions should be the first outputs

                // find the index to add the on exception, it should be in the
                // top
                // but it should add itself after any existing onException
                int index = 0;
                for (int i = 0; i < upper.size(); i++) {
                    ProcessorDefinition up = upper.get(i);
                    if (!(up instanceof OnExceptionDefinition)) {
                        index = i;
                        break;
                    } else {
                        index++;
                    }
                }
                upper.add(index, output);
            }
        }
    }

    private static void initInterceptors(
            ZwangineContext context, WorkflowDefinition workflow, List<ProcessorDefinition<?>> abstracts,
            List<ProcessorDefinition<?>> upper,
            List<InterceptDefinition> intercepts, List<InterceptFromDefinition> interceptFromDefinitions,
            List<InterceptSendToEndpointDefinition> interceptSendToEndpointDefinitions) {

        // move the abstracts interceptors into the dedicated list
        for (ProcessorDefinition processor : abstracts) {
            if (processor instanceof InterceptSendToEndpointDefinition interceptSendToEndpointDefinition) {
                if (interceptSendToEndpointDefinitions == null) {
                    interceptSendToEndpointDefinitions = new ArrayList<>();
                }
                interceptSendToEndpointDefinitions.add(interceptSendToEndpointDefinition);
            } else if (processor instanceof InterceptFromDefinition interceptFromDefinition) {
                if (interceptFromDefinitions == null) {
                    interceptFromDefinitions = new ArrayList<>();
                }
                interceptFromDefinitions.add(interceptFromDefinition);
            } else if (processor instanceof InterceptDefinition interceptDefinition) {
                if (intercepts == null) {
                    intercepts = new ArrayList<>();
                }
                intercepts.add(interceptDefinition);
            }
        }

        doInitInterceptors(context, workflow, upper, intercepts, interceptFromDefinitions, interceptSendToEndpointDefinitions);
    }

    private static void doInitInterceptors(
            ZwangineContext context, WorkflowDefinition workflow, List<ProcessorDefinition<?>> upper,
            List<InterceptDefinition> intercepts,
            List<InterceptFromDefinition> interceptFromDefinitions,
            List<InterceptSendToEndpointDefinition> interceptSendToEndpointDefinitions) {

        // configure intercept
        if (intercepts != null && !intercepts.isEmpty()) {
            for (InterceptDefinition intercept : intercepts) {
                // init the parent
                initParent(intercept);
                // add as first output so intercept is handled before the actual
                // workflow and that gives
                // us the needed head start to init and be able to intercept all
                // the remaining processing steps
                upper.add(0, intercept);
            }
        }

        // configure intercept from
        if (interceptFromDefinitions != null && !interceptFromDefinitions.isEmpty()) {
            for (InterceptFromDefinition intercept : interceptFromDefinitions) {

                // should we only apply interceptor for a given endpoint uri
                boolean match = true;
                if (intercept.getUri() != null) {

                    // the uri can have property placeholders so resolve them
                    // first
                    String pattern;
                    try {
                        pattern = context.resolvePropertyPlaceholders(intercept.getUri());
                    } catch (Exception e) {
                        throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                    }
                    boolean isRefPattern = pattern.startsWith("ref*") || pattern.startsWith("ref:");

                    match = false;

                    // a bit more logic to lookup the endpoint as it can be
                    // uri/ref based
                    String uri = workflow.getInput().getEndpointUri();
                    // if the pattern is not a ref itself, then resolve the ref
                    // uris, so we can match the actual uri's with each other
                    if (!isRefPattern) {
                        if (uri != null && uri.startsWith("ref:")) {
                            // its a ref: so lookup the endpoint to get its url
                            String ref = uri.substring(4);
                            uri = ZwangineContextHelper.getMandatoryEndpoint(context, ref).getEndpointUri();
                        }
                    }

                    // the workflow input uri can have property placeholders, so set them
                    // as local properties on PropertiesComponent to have them resolved
                    Properties properties = null;
                    if (workflow.getTemplateParameters() != null && !workflow.getTemplateParameters().isEmpty()) {
                        properties = context.getTypeConverter().tryConvertTo(Properties.class, workflow.getTemplateParameters());
                    }
                    try {
                        if (properties != null) {
                            context.getPropertiesComponent().setLocalProperties(properties);
                        }
                        if (EndpointHelper.matchEndpoint(context, uri, pattern)) {
                            match = true;
                        }
                    } finally {
                        if (properties != null) {
                            context.getPropertiesComponent().setLocalProperties(null);
                        }
                    }
                }

                if (match) {
                    // init the parent
                    initParent(intercept);
                    // add as first output so intercept is handled before the
                    // actual workflow and that gives
                    // us the needed head start to init and be able to intercept
                    // all the remaining processing steps
                    upper.add(0, intercept);
                }
            }
        }

        // configure intercept send to endpoint
        if (interceptSendToEndpointDefinitions != null && !interceptSendToEndpointDefinitions.isEmpty()) {
            for (InterceptSendToEndpointDefinition intercept : interceptSendToEndpointDefinitions) {
                intercept.afterPropertiesSet();
                // init the parent
                initParent(intercept);
                // add as first output so intercept is handled before the actual
                // workflow and that gives
                // us the needed head start to init and be able to intercept all
                // the remaining processing steps
                upper.add(0, intercept);
            }
        }
    }

    private static void initOnCompletions(
            List<ProcessorDefinition<?>> abstracts, List<ProcessorDefinition<?>> upper,
            List<OnCompletionDefinition> onCompletions) {
        List<OnCompletionDefinition> completions = new ArrayList<>();

        // find the workflow scoped onCompletions
        for (ProcessorDefinition out : abstracts) {
            if (out instanceof OnCompletionDefinition onCompletionDefinition) {
                completions.add(onCompletionDefinition);
            }
        }

        // only add global onCompletion if there are no workflow already
        if (completions.isEmpty() && onCompletions != null) {
            completions = onCompletions;
            // init the parent
            for (OnCompletionDefinition global : completions) {
                initParent(global);
            }
        }

        // are there any completions to init at all?
        if (completions.isEmpty()) {
            return;
        }

        upper.addAll(completions);
    }

    private static void initSagas(List<ProcessorDefinition<?>> abstracts, List<ProcessorDefinition<?>> lower) {
        SagaDefinition saga = null;

        // add to correct type
        for (ProcessorDefinition<?> type : abstracts) {
            if (type instanceof SagaDefinition sagaDefinition) {
                if (saga == null) {
                    saga = sagaDefinition;
                } else {
                    throw new IllegalArgumentException("The workflow can only have one saga defined");
                }
            }
        }

        if (saga != null) {
            // the outputs should be moved to the transacted policy
            saga.getOutputs().addAll(0, lower);
            // and add it as the single output
            lower.clear();
            lower.add(saga);
        }
    }

    private static void initTransacted(List<ProcessorDefinition<?>> abstracts, List<ProcessorDefinition<?>> lower) {
        TransactedDefinition transacted = null;

        // add to correct type
        for (ProcessorDefinition<?> type : abstracts) {
            if (type instanceof TransactedDefinition transactedDefinition) {
                if (transacted == null) {
                    transacted = transactedDefinition;
                } else {
                    throw new IllegalArgumentException("The workflow can only have one transacted defined");
                }
            }
        }

        if (transacted != null) {
            // the outputs should be moved to the start of the transacted policy
            transacted.getOutputs().addAll(0, lower);
            // and add it as the single output
            lower.clear();
            lower.add(transacted);
        }
    }

    /**
     * Force assigning ids to the give node and all its children (recursively).
     * <p/>
     * This is needed when doing tracing or the likes, where each node should have its id assigned so the tracing can
     * pinpoint exactly.
     *
     * @param context   the zwangine context
     * @param processor the node
     */
    public static void forceAssignIds(ZwangineContext context, final ProcessorDefinition processor) {
        // force id on the child
        processor.idOrCreate(context.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));

        // if there was a custom id assigned, then make sure to support property
        // placeholders
        if (processor.hasCustomIdAssigned()) {
            try {
                final String originalId = processor.getId();
                String id = context.resolvePropertyPlaceholders(originalId);
                // only set id if its changed, such as we did property
                // placeholder
                if (!originalId.equals(id)) {
                    processor.setId(id);
                }
            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        }

        List<ProcessorDefinition<?>> children = processor.getOutputs();
        if (children != null && !children.isEmpty()) {
            for (ProcessorDefinition child : children) {
                forceAssignIds(context, child);
            }
        }
    }

    public static void forceAssignIds(ZwangineContext context, final FromDefinition input) {
        // force id on input
        if (input != null) {
            input.idOrCreate(context.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class));
        }
    }

    public static String getWorkflowMessage(String workflow) {
        // cut the workflow after 60 chars, so it won't be too big in the message
        // users just need to be able to identify the workflow, so they know where to look
        if (workflow.length() > 60) {
            workflow = workflow.substring(0, 60) + "...";
        }

        // ensure to sanitize uri's in the workflow, so we do not show sensitive information such as passwords
        workflow = URISupport.sanitizeUri(workflow);
        return workflow;
    }

    public static Predicate<WorkflowConfigurationDefinition> workflowsByIdOrPattern(
            WorkflowDefinition workflow, String id) {
        return g -> {
            if (workflow.getWorkflowConfigurationId() != null) {
                // if the workflow has a workflow configuration assigned then use pattern matching
                return PatternHelper.matchPattern(g.getId(), id);
            } else {
                // global configurations have no id assigned or is a wildcard
                return g.getId() == null || g.getId().equals(id);
            }
        };
    }

    public static Consumer<WorkflowConfigurationDefinition> getWorkflowConfigurationDefinitionConsumer(
            WorkflowDefinition workflow, AtomicReference<ErrorHandlerDefinition> gcErrorHandler, List<OnExceptionDefinition> oe,
            List<InterceptDefinition> icp, List<InterceptFromDefinition> ifrom, List<InterceptSendToEndpointDefinition> ito,
            List<OnCompletionDefinition> oc) {
        return g -> {
            // there can only be one global error handler, so override previous, meaning
            // that we will pick the last in the sort (take precedence)
            if (g.getErrorHandler() != null) {
                gcErrorHandler.set(g.getErrorHandler());
            }

            String aid = g.getId() == null ? "<default>" : g.getId();
            // remember the id that was used on the workflow
            workflow.addAppliedWorkflowConfigurationId(aid);
            oe.addAll(g.getOnExceptions());
            icp.addAll(g.getIntercepts());
            ifrom.addAll(g.getInterceptFroms());
            ito.addAll(g.getInterceptSendTos());
            oc.addAll(g.getOnCompletions());
        };
    }
}
