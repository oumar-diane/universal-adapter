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
package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.impl.engine.ZwangineInternalProcessor;
import org.zenithblox.impl.engine.DefaultChannel;
import org.zenithblox.impl.engine.SharedZwangineInternalProcessor;
import org.zenithblox.spi.*;
import org.zenithblox.spi.annotations.JdkService;

@JdkService(InternalProcessorFactory.FACTORY)
public class DefaultInternalProcessorFactory implements InternalProcessorFactory {

    public InternalProcessor addUnitOfWorkProcessorAdvice(ZwangineContext zwangineContext, Processor processor, Workflow workflow) {
        ZwangineInternalProcessor internal = new ZwangineInternalProcessor(zwangineContext, processor);
        internal.addAdvice(new ZwangineInternalProcessor.UnitOfWorkProcessorAdvice(workflow, zwangineContext));
        return internal;
    }

    public InternalProcessor addChildUnitOfWorkProcessorAdvice(
            ZwangineContext zwangineContext, Processor processor, Workflow workflow, UnitOfWork parent) {
        ZwangineInternalProcessor internal = new ZwangineInternalProcessor(zwangineContext, processor);
        internal.addAdvice(new ZwangineInternalProcessor.ChildUnitOfWorkProcessorAdvice(workflow, zwangineContext, parent));
        return internal;
    }

    public SharedInternalProcessor createSharedZwangineInternalProcessor(ZwangineContext zwangineContext) {
        return new SharedZwangineInternalProcessor(
                zwangineContext, new ZwangineInternalProcessor.UnitOfWorkProcessorAdvice(null, zwangineContext));
    }

    public Channel createChannel(ZwangineContext zwangineContext) {
        return new DefaultChannel(zwangineContext);
    }

    public AsyncProducer createInterceptSendToEndpointProcessor(
            InterceptSendToEndpoint endpoint, Endpoint delegate, AsyncProducer producer, boolean skip, Predicate onWhen) {
        return new InterceptSendToEndpointProcessor(endpoint, delegate, producer, skip, onWhen);
    }

    public AsyncProcessor createWrapProcessor(Processor processor, Processor wrapped) {
        return new WrapProcessor(processor, wrapped);
    }

    public AsyncProducer createUnitOfWorkProducer(Producer producer) {
        return new UnitOfWorkProducer(producer);
    }

}
