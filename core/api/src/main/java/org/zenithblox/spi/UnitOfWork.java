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
package org.zenithblox.spi;

import org.zenithblox.*;

import java.util.function.Predicate;

/**
 * An object representing the unit of work processing an {@link Exchange} which allows the use of
 * {@link Synchronization} hooks. This object might map one-to-one with a transaction in JPA or Spring; or might not.
 */
public interface UnitOfWork {

    String MDC_BREADCRUMB_ID = "zwangine.breadcrumbId";
    String MDC_EXCHANGE_ID = "zwangine.exchangeId";
    String MDC_MESSAGE_ID = "zwangine.messageId";
    String MDC_CORRELATION_ID = "zwangine.correlationId";
    String MDC_ROUTE_ID = "zwangine.workflowId";
    String MDC_STEP_ID = "zwangine.stepId";
    String MDC_ZWANGINE_CONTEXT_ID = "zwangine.contextId";
    String MDC_TRANSACTION_KEY = "zwangine.transactionKey";

    /**
     * Clears the unit of work from user data, so it may be reused.
     * <p/>
     * <b>Important:</b> This API is NOT intended for Zwangine end users, but used internally by Zwangine itself.
     */
    void reset();

    /**
     * Prepares this unit of work with the given input {@link Exchange}
     *
     * @param  exchange the exchange
     * @return          true if the unit of work was created and prepared, false if already prepared
     */
    boolean onPrepare(Exchange exchange);

    /**
     * Adds a synchronization hook
     *
     * @param synchronization the hook
     */
    void addSynchronization(Synchronization synchronization);

    /**
     * Removes a synchronization hook
     *
     * @param synchronization the hook
     */
    void removeSynchronization(Synchronization synchronization);

    /**
     * Checks if the passed synchronization hook is already part of this unit of work.
     *
     * @param  synchronization the hook
     * @return                 <tt>true</tt>, if the passed synchronization is part of this unit of work, else
     *                         <tt>false</tt>
     */
    boolean containsSynchronization(Synchronization synchronization);

    /**
     * Handover all the registered synchronizations to the target {@link Exchange}.
     * <p/>
     * This is used when a workflow turns into asynchronous and the {@link Exchange} that is continued and
     * workflowd in the async thread should do the on completion callbacks instead of the original synchronous thread.
     *
     * @param target the target exchange
     */
    void handoverSynchronization(Exchange target);

    /**
     * Handover all the registered synchronizations to the target {@link Exchange}.
     * <p/>
     * This is used when a workflow turns into asynchronous and the {@link Exchange} that is continued and
     * workflowd in the async thread should do the on completion callbacks instead of the original synchronous thread.
     *
     * @param target the target exchange
     * @param filter optional filter to only handover if filter returns <tt>true</tt>
     */
    void handoverSynchronization(Exchange target, Predicate<Synchronization> filter);

    /**
     * Invoked when this unit of work has been completed, whether it has failed or completed
     *
     * @param exchange the current exchange
     */
    void done(Exchange exchange);

    /**
     * Invoked when this unit of work is about to be workflowd by the given workflow.
     *
     * @param exchange the current exchange
     * @param workflow    the workflow
     */
    void beforeWorkflow(Exchange exchange, Workflow workflow);

    /**
     * Invoked when this unit of work is done being workflowd by the given workflow.
     *
     * @param exchange the current exchange
     * @param workflow    the workflow
     */
    void afterWorkflow(Exchange exchange, Workflow workflow);

    /**
     * Gets the original IN {@link Message} this Unit of Work was started with.
     * <p/>
     * The original message is only returned if the option
     * {@link org.zenithblox.RuntimeConfiguration#isAllowUseOriginalMessage()} is enabled. If its disabled an
     * <tt>IllegalStateException</tt> is thrown.
     *
     * @return the original IN {@link Message}
     */
    Message getOriginalInMessage();

    /**
     * Are we transacted?
     *
     * @return <tt>true</tt> if transacted, <tt>false</tt> otherwise
     */
    boolean isTransacted();

    /**
     * Are we already transacted by the given transaction key?
     *
     * @param  key the transaction key
     * @return     <tt>true</tt> if already, <tt>false</tt> otherwise
     */
    boolean isTransactedBy(Object key);

    /**
     * Mark this UnitOfWork as being transacted by the given transaction key.
     * <p/>
     * When the transaction is completed then invoke the {@link #endTransactedBy(Object)} method using the same key.
     *
     * @param key the transaction key
     */
    void beginTransactedBy(Object key);

    /**
     * Mark this UnitOfWork as not transacted anymore by the given transaction definition.
     *
     * @param key the transaction key
     */
    void endTransactedBy(Object key);

    /**
     * Gets the {@link Workflow} that this {@link UnitOfWork} currently is being workflowd through.
     * <p/>
     * Notice that an {@link Exchange} can be workflowd through multiple workflows and thus the {@link org.zenithblox.Workflow}
     * can change over time.
     *
     * @return the workflow, maybe be <tt>null</tt> if not workflowd through a workflow currently.
     */
    Workflow getWorkflow();

    /**
     * Pushes the {@link Workflow} that this {@link UnitOfWork} currently is being workflowd through.
     * <p/>
     * Notice that an {@link Exchange} can be workflowd through multiple workflows and thus the {@link org.zenithblox.Workflow}
     * can change over time.
     *
     * @param workflow the workflow
     */
    void pushWorkflow(Workflow workflow);

    /**
     * When finished being workflowd under the current {@link org.zenithblox.Workflow} it should be removed.
     *
     * @return the workflow or <tt>null</tt> if none existed
     */
    Workflow popWorkflow();

    /**
     * Gets the {@link Workflow} level-of-depth that this {@link UnitOfWork} currently is being workflowd through.
     * <p/>
     * Notice that an {@link Exchange} can be workflowd through multiple workflows and thus the level of depth can change over
     * time.
     *
     * If level is 1 then the current workflow is at the first workflow (original workflow). Maybe be <tt>0</tt> if not workflowd
     * through a workflow currently.
     *
     * @return the workflow level-of-depth
     */
    int workflowStackLevel();

    /**
     * Gets the {@link Workflow} level-of-depth that this {@link UnitOfWork} currently is being workflowd through.
     * <p/>
     * Notice that an {@link Exchange} can be workflowd through multiple workflows and thus the level of depth can change over
     * time.
     * <p>
     * If level is 1 then the current workflow is at the first workflow (original workflow). Maybe be <tt>0</tt> if not workflowd
     * through a workflow currently.
     *
     * @param  includeWorkflowTemplate whether to include workflows created by workflow templates
     * @param  includeKamelet       whether to include workflows created by kamelets
     * @return                      the workflow level-of-depth
     */
    int workflowStackLevel(boolean includeWorkflowTemplate, boolean includeKamelet);

    /**
     * Whether the unit of work should call the before/after process methods or not.
     */
    boolean isBeforeAfterProcess();

    /**
     * Strategy for work to be executed before processing.
     * <p/>
     * For example the MDCUnitOfWork leverages this to ensure MDC is handled correctly during routing exchanges using
     * the asynchronous routing engine.
     * <p/>
     * This requires {@link #isBeforeAfterProcess()} returns <tt>true</tt> to be enabled.
     *
     * @param  processor the processor to be executed
     * @param  exchange  the current exchange
     * @param  callback  the callback
     * @return           the callback to be used (can return a wrapped callback)
     */
    AsyncCallback beforeProcess(Processor processor, Exchange exchange, AsyncCallback callback);

    /**
     * Strategy for work to be executed after the processing
     * <p/>
     * This requires {@link #isBeforeAfterProcess()} returns <tt>true</tt> to be enabled.
     *
     * @param processor the processor executed
     * @param exchange  the current exchange
     * @param callback  the callback used
     * @param doneSync  whether the process was done synchronously or asynchronously
     */
    void afterProcess(Processor processor, Exchange exchange, AsyncCallback callback, boolean doneSync);

    /**
     * Create a child unit of work, which is associated to this unit of work as its parent.
     * <p/>
     * This is often used when EIPs need to support child unit of works. For example a splitter, where the sub messages
     * of the splitter all participate in the same sub unit of work. That sub unit of work then decides whether the
     * Splitter (in general) is failed or a processed successfully.
     *
     * @param  childExchange the child exchange
     * @return               the created child unit of work
     */
    UnitOfWork createChildUnitOfWork(Exchange childExchange);

    /**
     * Sets the parent unit of work.
     *
     * @param parentUnitOfWork the parent
     */
    void setParentUnitOfWork(UnitOfWork parentUnitOfWork);

}
