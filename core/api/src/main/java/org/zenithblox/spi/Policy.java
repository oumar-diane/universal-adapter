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

import org.zenithblox.NamedNode;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;

/**
 * A strategy capable of applying interceptors to a processor.
 * <p/>
 * Its <b>strongly</b> advised to use an {@link org.zenithblox.AsyncProcessor} as the returned wrapped
 * {@link Processor} which ensures the policy works well with the asynchronous routing engine. You can use the
 * {@link org.zenithblox.processor.DelegateAsyncProcessor} to easily return an {@link org.zenithblox.AsyncProcessor}
 * and override the
 * {@link org.zenithblox.AsyncProcessor#process(org.zenithblox.Exchange, org.zenithblox.AsyncCallback)} to
 * implement your interceptor logic. And just invoke the super method to <b>continue</b> routing.
 * <p/>
 * Mind that not all frameworks supports asynchronous routing, for example some transaction managers, such as Spring
 * Transaction uses the current thread to store state of the transaction, and thus can't transfer this state to other
 * threads when routing continues asynchronously.
 */
public interface Policy {

    /**
     * Hook invoked before the wrap.
     * <p/>
     * This allows you to do any custom logic before the processor is wrapped. For example to manipulate the
     * {@link org.zenithblox.model.ProcessorDefinition definiton}.
     *
     * @param workflow      the workflow context
     * @param definition the processor definition
     */
    void beforeWrap(Workflow workflow, NamedNode definition);

    /**
     * Wraps any applicable interceptors around the given processor.
     *
     * @param  workflow     the workflow context
     * @param  processor the processor to be intercepted
     * @return           either the original processor or a processor wrapped in one or more processors
     */
    Processor wrap(Workflow workflow, Processor processor);
}
