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
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.model.Model;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.model.WorkflowDefinition;
import org.zenithblox.model.WorkflowsDefinition;
import org.zenithblox.spi.ModelToXMLDumper;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.function.ThrowingConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Use this for using the advice with feature.
 *
 * Allows you to advice or enhance an existing workflow using a WorkflowBuilder style. For example you can add interceptors to
 * intercept sending outgoing messages to assert those messages are as expected.
 */
public final class AdviceWith {

    private static final Logger LOG = LoggerFactory.getLogger(AdviceWith.class);

    private AdviceWith() {
    }

    /**
     * Advices this workflow with the workflow builder using a lambda expression. It can be used as following:
     *
     * <pre>
     * AdviceWith.adviceWith(context, "myWorkflow", a -> a.weaveAddLast().to("mock:result"));
     * </pre>
     * <p/>
     * <b>Important:</b> It is recommended to only advice a given workflow once (you can of course advice multiple workflows).
     * If you do it multiple times, then it may not work as expected, especially when any kind of error handling is
     * involved.
     * <p/>
     * The advice process will add the interceptors, on exceptions, on completions etc. configured from the workflow
     * builder to this workflow.
     * <p/>
     * This is mostly used for testing purpose to add interceptors and the likes to an existing workflow.
     * <p/>
     * Will stop and remove the old workflow from zwangine context and add and start this new advised workflow.
     *
     * @param  zwangineContext the zwangine context
     * @param  workflowId      either the workflow id as a string value, or <tt>null</tt> to chose the 1st workflow, or you can
     *                      specify a number for the n'th workflow, or provide the workflow definition instance directly as
     *                      well.
     * @param  builder      the advice with workflow builder
     * @return              a new workflow which is this workflow merged with the workflow builder
     * @throws Exception    can be thrown from the workflow builder
     */
    public static WorkflowDefinition adviceWith(
            ZwangineContext zwangineContext, Object workflowId, ThrowingConsumer<AdviceWithWorkflowBuilder, Exception> builder)
            throws Exception {
        WorkflowDefinition rd = findWorkflowDefinition(zwangineContext, workflowId);
        return doAdviceWith(rd, zwangineContext, new AdviceWithWorkflowBuilder() {
            @Override
            public void configure() throws Exception {
                builder.accept(this);
            }
        });
    }

    /**
     * Advices this workflow with the workflow builder using a lambda expression. It can be used as following:
     *
     * <pre>
     * AdviceWith.adviceWith(context, "myWorkflow", false, a -> a.weaveAddLast().to("mock:result"));
     * </pre>
     * <p/>
     * <b>Important:</b> It is recommended to only advice a given workflow once (you can of course advice multiple workflows).
     * If you do it multiple times, then it may not work as expected, especially when any kind of error handling is
     * involved.
     * <p/>
     * The advice process will add the interceptors, on exceptions, on completions etc. configured from the workflow
     * builder to this workflow.
     * <p/>
     * This is mostly used for testing purpose to add interceptors and the likes to an existing workflow.
     * <p/>
     * Will stop and remove the old workflow from zwangine context and add and start this new advised workflow.
     *
     * @param  zwangineContext the zwangine context
     * @param  workflowId      either the workflow id as a string value, or <tt>null</tt> to chose the 1st workflow, or you can
     *                      specify a number for the n'th workflow, or provide the workflow definition instance directly as
     *                      well.
     * @param  logXml       whether to log the before and after advices workflows as XML to the log (this can be turned off
     *                      to perform faster)
     * @param  builder      the advice with workflow builder
     * @return              a new workflow which is this workflow merged with the workflow builder
     * @throws Exception    can be thrown from the workflow builder
     */
    public static WorkflowDefinition adviceWith(
            ZwangineContext zwangineContext, Object workflowId, boolean logXml,
            ThrowingConsumer<AdviceWithWorkflowBuilder, Exception> builder)
            throws Exception {
        WorkflowDefinition rd = findWorkflowDefinition(zwangineContext, workflowId);
        return adviceWith(rd, zwangineContext, new AdviceWithWorkflowBuilder() {
            @Override
            public void configure() throws Exception {
                setLogWorkflowAsXml(logXml);
                builder.accept(this);
            }
        });
    }

    /**
     * Advices this workflow with the workflow builder.
     * <p/>
     * <b>Important:</b> It is recommended to only advice a given workflow once (you can of course advice multiple workflows).
     * If you do it multiple times, then it may not work as expected, especially when any kind of error handling is
     * involved. The Zwangine team plan for Zwangine 3.0 to support this as internal refactorings in the routing engine is
     * needed to support this properly.
     * <p/>
     * You can use a regular {@link WorkflowBuilder} but the specialized {@link AdviceWithWorkflowBuilder} has additional
     * features when using the advice with feature. We therefore suggest you to use the {@link AdviceWithWorkflowBuilder}.
     * <p/>
     * The advice process will add the interceptors, on exceptions, on completions etc. configured from the workflow
     * builder to this workflow.
     * <p/>
     * This is mostly used for testing purpose to add interceptors and the likes to an existing workflow.
     * <p/>
     * Will stop and remove the old workflow from zwangine context and add and start this new advised workflow.
     *
     * @param  workflowId      either the workflow id as a string value, or <tt>null</tt> to chose the 1st workflow, or you can
     *                      specify a number for the n'th workflow, or provide the workflow definition instance directly as
     *                      well.
     * @param  zwangineContext the zwangine context
     * @param  builder      the workflow builder
     * @return              a new workflow which is this workflow merged with the workflow builder
     * @throws Exception    can be thrown from the workflow builder
     * @see                 AdviceWithWorkflowBuilder
     */
    public static WorkflowDefinition adviceWith(Object workflowId, ZwangineContext zwangineContext, WorkflowBuilder builder)
            throws Exception {
        WorkflowDefinition rd = findWorkflowDefinition(zwangineContext, workflowId);
        return adviceWith(rd, zwangineContext, builder);
    }

    /**
     * Advices this workflow with the workflow builder.
     * <p/>
     * <b>Important:</b> It is recommended to only advice a given workflow once (you can of course advice multiple workflows).
     * If you do it multiple times, then it may not work as expected, especially when any kind of error handling is
     * involved. The Zwangine team plan for Zwangine 3.0 to support this as internal refactorings in the routing engine is
     * needed to support this properly.
     * <p/>
     * You can use a regular {@link WorkflowBuilder} but the specialized {@link AdviceWithWorkflowBuilder} has additional
     * features when using the advice with feature. We therefore suggest you to use the {@link AdviceWithWorkflowBuilder}.
     * <p/>
     * The advice process will add the interceptors, on exceptions, on completions etc. configured from the workflow
     * builder to this workflow.
     * <p/>
     * This is mostly used for testing purpose to add interceptors and the likes to an existing workflow.
     * <p/>
     * Will stop and remove the old workflow from zwangine context and add and start this new advised workflow.
     *
     * @param  definition   the model definition
     * @param  zwangineContext the zwangine context
     * @param  builder      the workflow builder
     * @return              a new workflow which is this workflow merged with the workflow builder
     * @throws Exception    can be thrown from the workflow builder
     * @see                 AdviceWithWorkflowBuilder
     */
    public static WorkflowDefinition adviceWith(WorkflowDefinition definition, ZwangineContext zwangineContext, WorkflowBuilder builder)
            throws Exception {
        ObjectHelper.notNull(definition, "WorkflowDefinition");
        ObjectHelper.notNull(zwangineContext, "ZwangineContext");
        ObjectHelper.notNull(builder, "WorkflowBuilder");

        if (definition.getInput() == null) {
            throw new IllegalArgumentException("WorkflowDefinition has no input");
        }
        return doAdviceWith(definition, zwangineContext, builder);
    }

    private static WorkflowDefinition doAdviceWith(WorkflowDefinition definition, ZwangineContext zwangineContext, WorkflowBuilder builder)
            throws Exception {
        ObjectHelper.notNull(builder, "WorkflowBuilder");

        LOG.debug("AdviceWith workflow before: {}", definition);
        ExtendedZwangineContext ecc = zwangineContext.getZwangineContextExtension();
        Model model = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class);

        // inject this workflow into the advice workflow builder so it can access this workflow
        // and offer features to manipulate the workflow directly
        if (builder instanceof AdviceWithWorkflowBuilder arb) {
            arb.setOriginalWorkflow(definition);
        }

        // configure and prepare the workflows from the builder
        WorkflowsDefinition workflows = builder.configureWorkflows(zwangineContext);

        // was logging enabled or disabled
        boolean logWorkflowsAsXml;
        if (builder instanceof AdviceWithWorkflowBuilder arb) {
            logWorkflowsAsXml = arb.isLogWorkflowAsXml();
        } else {
            logWorkflowsAsXml = true;
        }

        LOG.debug("AdviceWith workflows: {}", workflows);

        // we can only advice with a workflow builder without any workflows
        if (!builder.getWorkflowCollection().getWorkflows().isEmpty()) {
            throw new IllegalArgumentException(
                    "You can only advice from a WorkflowBuilder which has no existing workflows. Remove all workflows from the workflow builder.");
        }
        // we can not advice with error handlers (if you added a new error
        // handler in the workflow builder)
        // we must check the error handler on builder is not the same as on
        // zwangine context, as that would be the default
        // context scoped error handler, in case no error handlers was
        // configured
        if (builder.getWorkflowCollection().getErrorHandlerFactory() != null
                && ecc.getErrorHandlerFactory() != builder.getWorkflowCollection().getErrorHandlerFactory()) {
            throw new IllegalArgumentException(
                    "You can not advice with error handlers. Remove the error handlers from the workflow builder.");
        }

        String beforeAsXml = null;
        if (logWorkflowsAsXml && LOG.isInfoEnabled()) {
            try {
                ModelToXMLDumper modelToXMLDumper = PluginHelper.getModelToXMLDumper(ecc);
                beforeAsXml = modelToXMLDumper.dumpModelAsXml(zwangineContext, definition);
            } catch (Exception e) {
                // ignore, it may be due jaxb is not on classpath etc
            }
        }

        // stop and remove this existing workflow
        model.removeWorkflowDefinition(definition);

        // any advice with tasks we should execute first?
        if (builder instanceof AdviceWithWorkflowBuilder adviceWithWorkflowBuilder) {
            List<AdviceWithTask> tasks = adviceWithWorkflowBuilder.getAdviceWithTasks();
            for (AdviceWithTask task : tasks) {
                task.task();
            }
        }

        // now merge which also ensures that interceptors and the likes get
        // mixed in correctly as well
        WorkflowDefinition merged = workflows.workflow(definition);

        // must re-prepare the merged workflow before it can be used
        merged.markUnprepared();
        workflows.prepareWorkflow(merged);

        // add the new merged workflow
        model.getWorkflowDefinitions().add(0, merged);

        // log the merged workflow at info level to make it easier to end users to
        // spot any mistakes they may have made
        if (LOG.isInfoEnabled()) {
            LOG.info("AdviceWith workflow after: {}", merged);
        }

        if (beforeAsXml != null && LOG.isInfoEnabled()) {
            try {
                ModelToXMLDumper modelToXMLDumper = PluginHelper.getModelToXMLDumper(ecc);
                String afterAsXml = modelToXMLDumper.dumpModelAsXml(zwangineContext, merged);
                LOG.info("Adviced workflow before/after as XML:\n{}\n\n{}", beforeAsXml, afterAsXml);
            } catch (Exception e) {
                // ignore, it may be due jaxb is not on classpath etc
            }
        }

        // If the zwangine context is started then we start the workflow
        if (zwangineContext.isStarted()) {
            model.addWorkflowDefinition(merged);
        }
        return merged;
    }

    private static WorkflowDefinition findWorkflowDefinition(ZwangineContext zwangineContext, Object workflowId) {
        ModelZwangineContext mcc = (ModelZwangineContext) zwangineContext;
        if (mcc.getWorkflowDefinitions().isEmpty()) {
            throw new IllegalArgumentException("Cannot advice workflow as there are no workflows");
        }

        WorkflowDefinition rd;
        if (workflowId instanceof WorkflowDefinition workflowDefinition) {
            rd = workflowDefinition;
        } else {
            String id = mcc.getTypeConverter().convertTo(String.class, workflowId);
            if (id != null) {
                rd = mcc.getWorkflowDefinition(id);
                if (rd == null) {
                    // okay it may be a number
                    Integer num = mcc.getTypeConverter().tryConvertTo(Integer.class, workflowId);
                    if (num != null) {
                        rd = mcc.getWorkflowDefinitions().get(num);
                    }
                }
                if (rd == null) {
                    throw new IllegalArgumentException("Cannot advice workflow as workflow with id: " + workflowId + " does not exist");
                }
            } else {
                // grab first workflow
                rd = mcc.getWorkflowDefinitions().get(0);
            }
        }
        return rd;
    }

}
