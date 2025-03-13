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

import org.zenithblox.Predicate;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.Metadata;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Workflow to be executed when normal workflow processing completes
 */
@Metadata(label = "configuration")
public class OnCompletionDefinition extends OutputDefinition<OnCompletionDefinition>
        implements ExecutorServiceAwareDefinition<OnCompletionDefinition> {

    private ExecutorService executorServiceBean;
    private boolean workflowScoped = true;

    @Metadata(label = "advanced", javaType = "org.zenithblox.model.OnCompletionMode", defaultValue = "AfterConsumer",
              enums = "AfterConsumer,BeforeConsumer")
    private String mode;
    @Metadata(javaType = "java.lang.Boolean")
    private String onCompleteOnly;
    @Metadata(javaType = "java.lang.Boolean")
    private String onFailureOnly;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String parallelProcessing;
    @Metadata(label = "advanced", javaType = "java.util.concurrent.ExecutorService")
    private String executorService;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String useOriginalMessage;
    @Metadata(description = "To use an expression to only trigger routing this completion steps in specific situations")
    @AsPredicate
    private OnWhenDefinition onWhen;

    public OnCompletionDefinition() {
    }

    protected OnCompletionDefinition(OnCompletionDefinition source) {
        super(source);
        this.executorServiceBean = source.executorServiceBean;
        this.workflowScoped = source.workflowScoped;
        this.mode = source.mode;
        this.onCompleteOnly = source.onCompleteOnly;
        this.onFailureOnly = source.onFailureOnly;
        this.parallelProcessing = source.parallelProcessing;
        this.executorService = source.executorService;
        this.useOriginalMessage = source.useOriginalMessage;
        this.onWhen = source.onWhen != null ? source.onWhen.copyDefinition() : null;
    }

    @Override
    public OnCompletionDefinition copyDefinition() {
        return new OnCompletionDefinition(this);
    }

    public void setWorkflowScoped(boolean workflowScoped) {
        this.workflowScoped = workflowScoped;
    }

    public boolean isWorkflowScoped() {
        return workflowScoped;
    }

    @Override
    public void setParent(ProcessorDefinition<?> parent) {
        if (workflowScoped) {
            super.setParent(parent);
        }
    }

    @Override
    public String toString() {
        return "onCompletion[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "onCompletion";
    }

    @Override
    public String getLabel() {
        return "onCompletion";
    }

    @Override
    public boolean isAbstract() {
        return true;
    }

    @Override
    public boolean isTopLevelOnly() {
        return true;
    }

    /**
     * Removes all existing global {@link org.zenithblox.model.OnCompletionDefinition} from the definition.
     * <p/>
     * This is used to let workflow scoped <tt>onCompletion</tt> overrule any global <tt>onCompletion</tt>. Do not remove
     * an existing workflow-scoped because it is now possible (CAMEL-16374) to have several.
     *
     * @param definition the parent definition that is the workflow
     */
    public void removeAllOnCompletionDefinition(ProcessorDefinition<?> definition) {
        definition.getOutputs().removeIf(out -> out instanceof OnCompletionDefinition &&
                !((OnCompletionDefinition) out).isWorkflowScoped());
    }

    @Override
    public ProcessorDefinition<?> end() {
        // pop parent block, as we added our self as block to parent when
        // synchronized was defined in the workflow
        getParent().popBlock();
        return super.end();
    }

    /**
     * Sets the mode to be after workflow is done (default due backwards compatible).
     * <p/>
     * This executes the on completion work <i>after</i> the workflow consumer have written response back to the callee (if
     * its InOut mode).
     *
     * @return the builder
     */
    public OnCompletionDefinition modeAfterConsumer() {
        setMode(OnCompletionMode.AfterConsumer.name());
        return this;
    }

    /**
     * Sets the mode to be before consumer is done.
     * <p/>
     * This allows the on completion work to execute <i>before</i> the workflow consumer, writes any response back to the
     * callee (if its InOut mode).
     *
     * @return the builder
     */
    public OnCompletionDefinition modeBeforeConsumer() {
        setMode(OnCompletionMode.BeforeConsumer.name());
        return this;
    }

    /**
     * Will only synchronize when the {@link org.zenithblox.Exchange} completed successfully (no errors).
     *
     * @return the builder
     */
    public OnCompletionDefinition onCompleteOnly() {
        boolean isOnFailureOnly = Boolean.toString(true).equals(onFailureOnly);
        if (isOnFailureOnly) {
            throw new IllegalArgumentException(
                    "Both onCompleteOnly and onFailureOnly cannot be true. Only one of them can be true. On node: " + this);
        }
        // must define return type as OutputDefinition and not this type to
        // avoid end user being able
        // to invoke onFailureOnly/onCompleteOnly more than once
        setOnCompleteOnly(Boolean.toString(true));
        setOnFailureOnly(Boolean.toString(false));
        return this;
    }

    /**
     * Will only synchronize when the {@link org.zenithblox.Exchange} ended with failure (exception or FAULT message).
     *
     * @return the builder
     */
    public OnCompletionDefinition onFailureOnly() {
        boolean isOnCompleteOnly = Boolean.toString(true).equals(onCompleteOnly);
        if (isOnCompleteOnly) {
            throw new IllegalArgumentException(
                    "Both onCompleteOnly and onFailureOnly cannot be true. Only one of them can be true. On node: " + this);
        }
        // must define return type as OutputDefinition and not this type to
        // avoid end user being able
        // to invoke onFailureOnly/onCompleteOnly more than once
        setOnCompleteOnly(Boolean.toString(false));
        setOnFailureOnly(Boolean.toString(true));
        return this;
    }

    /**
     * Sets an additional predicate that should be true before the onCompletion is triggered.
     * <p/>
     * To be used for fine grained controlling whether a completion callback should be invoked or not
     *
     * @param  predicate predicate that determines true or false
     * @return           the builder
     */
    public OnCompletionDefinition onWhen(@AsPredicate Predicate predicate) {
        setOnWhen(new OnWhenDefinition(predicate));
        return this;
    }

    /**
     * Will use the original input message body when an {@link org.zenithblox.Exchange} for this on completion.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the split message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     *
     * @return     the builder
     * @deprecated use {@link #useOriginalMessage()}
     */
    @Deprecated(since = "4.6.0")
    public OnCompletionDefinition useOriginalBody() {
        setUseOriginalMessage(Boolean.toString(true));
        return this;
    }

    /**
     * Will use the original input message when an {@link org.zenithblox.Exchange} for this on completion.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the split message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     *
     * @return the builder
     */
    public OnCompletionDefinition useOriginalMessage() {
        setUseOriginalMessage(Boolean.toString(true));
        return this;
    }

    /**
     * To use a custom Thread Pool to be used for parallel processing. Notice if you set this option, then parallel
     * processing is automatic implied, and you do not have to enable that option as well.
     */
    @Override
    public OnCompletionDefinition executorService(ExecutorService executorService) {
        this.executorServiceBean = executorService;
        return this;
    }

    /**
     * Refers to a custom Thread Pool to be used for parallel processing. Notice if you set this option, then parallel
     * processing is automatic implied, and you do not have to enable that option as well.
     */
    @Override
    public OnCompletionDefinition executorService(String executorService) {
        setExecutorService(executorService);
        return this;
    }

    /**
     * If enabled then the on completion process will run asynchronously by a separate thread from a thread pool. By
     * default this is false, meaning the on completion process will run synchronously using the same caller thread as
     * from the workflow.
     *
     * @return the builder
     */
    public OnCompletionDefinition parallelProcessing() {
        setParallelProcessing(Boolean.toString(true));
        return this;
    }

    /**
     * If enabled then the on completion process will run asynchronously by a separate thread from a thread pool. By
     * default this is false, meaning the on completion process will run synchronously using the same caller thread as
     * from the workflow.
     *
     * @return the builder
     */
    public OnCompletionDefinition parallelProcessing(boolean parallelProcessing) {
        setParallelProcessing(Boolean.toString(parallelProcessing));
        return this;
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    @Override
    public ExecutorService getExecutorServiceBean() {
        return executorServiceBean;
    }

    @Override
    public String getExecutorServiceRef() {
        return executorService;
    }

    public String getMode() {
        return mode;
    }

    /**
     * Sets the on completion mode.
     * <p/>
     * The default value is AfterConsumer
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getOnCompleteOnly() {
        return onCompleteOnly;
    }

    public void setOnCompleteOnly(String onCompleteOnly) {
        this.onCompleteOnly = onCompleteOnly;
    }

    public String getOnFailureOnly() {
        return onFailureOnly;
    }

    public void setOnFailureOnly(String onFailureOnly) {
        this.onFailureOnly = onFailureOnly;
    }

    public OnWhenDefinition getOnWhen() {
        return onWhen;
    }

    public void setOnWhen(OnWhenDefinition onWhen) {
        this.onWhen = onWhen;
    }

    public String getUseOriginalMessage() {
        return useOriginalMessage;
    }

    /**
     * Will use the original input message body when an {@link org.zenithblox.Exchange} for this on completion.
     * <p/>
     * The original input message is defensively copied, and the copied message body is converted to
     * {@link org.zenithblox.StreamCache} if possible (stream caching is enabled, can be disabled globally or on the
     * original workflow), to ensure the body can be read when the original message is being used later. If the body is
     * converted to {@link org.zenithblox.StreamCache} then the message body on the current
     * {@link org.zenithblox.Exchange} is replaced with the {@link org.zenithblox.StreamCache} body. If the body is
     * not converted to {@link org.zenithblox.StreamCache} then the body will not be able to re-read when accessed
     * later.
     * <p/>
     * <b>Important:</b> The original input means the input message that are bounded by the current
     * {@link org.zenithblox.spi.UnitOfWork}. An unit of work typically spans one workflow, or multiple workflows if they
     * are connected using internal endpoints such as direct or seda. When messages is passed via external endpoints
     * such as JMS or HTTP then the consumer will create a new unit of work, with the message it received as input as
     * the original input. Also some EIP patterns such as splitter, multicast, will create a new unit of work boundary
     * for the messages in their sub-workflow (eg the split message); however these EIPs have an option named
     * <tt>shareUnitOfWork</tt> which allows to combine with the parent unit of work in regard to error handling and
     * therefore use the parent original message.
     * <p/>
     * By default this feature is off.
     *
     * @return the builder
     */
    public void setUseOriginalMessage(String useOriginalMessage) {
        this.useOriginalMessage = useOriginalMessage;
    }

    public String getParallelProcessing() {
        return parallelProcessing;
    }

    public void setParallelProcessing(String parallelProcessing) {
        this.parallelProcessing = parallelProcessing;
    }

    public String getExecutorService() {
        return executorService;
    }

    public void setExecutorService(String executorService) {
        this.executorService = executorService;
    }
}
