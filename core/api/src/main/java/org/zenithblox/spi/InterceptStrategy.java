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

import org.zenithblox.ZwangineContext;
import org.zenithblox.NamedNode;
import org.zenithblox.Processor;

/**
 * The purpose of this interface is to allow an implementation to wrap processors in a workflow with interceptors. For
 * example, a possible usecase is to gather performance statistics at the processor's level.
 * <p/>
 * Its <b>strongly</b> adviced to use an {@link org.zenithblox.AsyncProcessor} as the returned wrapped
 * {@link Processor} which ensures the interceptor works well with the asynchronous routing engine. You can use the
 * {@link org.zenithblox.support.processor.DelegateAsyncProcessor} to easily return an
 * {@link org.zenithblox.AsyncProcessor} and override the
 * {@link org.zenithblox.AsyncProcessor#process(org.zenithblox.Exchange, org.zenithblox.AsyncCallback)} to
 * implement your interceptor logic. And just invoke the super method to <b>continue</b> routing.
 */
public interface InterceptStrategy {

    /**
     * Give implementor an opportunity to wrap the target processor in a workflow.
     * <p/>
     * <b>Important:</b> See the class javadoc for advice on letting interceptor be compatible with the asynchronous
     * routing engine.
     *
     * @param  context    Zwangine context
     * @param  definition the model this interceptor represents
     * @param  target     the processor to be wrapped
     * @param  nextTarget the next processor to be workflowd to
     * @return            processor wrapped with an interceptor or not wrapped.
     * @throws Exception  can be thrown
     */
    Processor wrapProcessorInInterceptors(
            ZwangineContext context, NamedNode definition,
            Processor target, Processor nextTarget)
            throws Exception;
}
