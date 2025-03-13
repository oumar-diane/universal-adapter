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
import org.zenithblox.spi.AnnotationBasedProcessorFactory;
import org.zenithblox.spi.annotations.JdkService;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.util.ObjectHelper;

import java.util.concurrent.ExecutorService;

@JdkService(AnnotationBasedProcessorFactory.FACTORY)
public final class DefaultAnnotationBasedProcessorFactory implements AnnotationBasedProcessorFactory {

    @Override
    public AsyncProcessor createDynamicWorkflowr(ZwangineContext zwangineContext, org.zenithblox.DynamicWorkflow annotation) {
        DynamicRouter dynamicRouter = new DynamicRouter(zwangineContext);
        dynamicRouter.setDelimiter(annotation.delimiter());
        dynamicRouter.setIgnoreInvalidEndpoints(annotation.ignoreInvalidEndpoints());
        dynamicRouter.setCacheSize(annotation.cacheSize());
        return dynamicRouter;
    }

    @Override
    public AsyncProcessor createRecipientList(ZwangineContext zwangineContext, org.zenithblox.RecipientList annotation) {
        RecipientList recipientList
                = new org.zenithblox.processor.RecipientList(zwangineContext, annotation.delimiter());
        recipientList.setStopOnException(annotation.stopOnException());
        recipientList.setIgnoreInvalidEndpoints(annotation.ignoreInvalidEndpoints());
        recipientList.setParallelProcessing(annotation.parallelProcessing());
        recipientList.setParallelAggregate(annotation.parallelAggregate());
        recipientList.setStreaming(annotation.streaming());
        recipientList.setTimeout(annotation.timeout());
        recipientList.setCacheSize(annotation.cacheSize());
        recipientList.setShareUnitOfWork(annotation.shareUnitOfWork());

        if (ObjectHelper.isNotEmpty(annotation.executorService())) {
            ExecutorService executor = zwangineContext.getExecutorServiceManager().newThreadPool(this, "@RecipientList",
                    annotation.executorService());
            recipientList.setExecutorService(executor);
        }

        if (annotation.parallelProcessing() && recipientList.getExecutorService() == null) {
            // we are running in parallel so we need a thread pool
            ExecutorService executor = zwangineContext.getExecutorServiceManager().newDefaultThreadPool(this, "@RecipientList");
            recipientList.setExecutorService(executor);
        }

        if (ObjectHelper.isNotEmpty(annotation.aggregationStrategy())) {
            AggregationStrategy strategy
                    = ZwangineContextHelper.mandatoryLookup(zwangineContext, annotation.aggregationStrategy(),
                            AggregationStrategy.class);
            recipientList.setAggregationStrategy(strategy);
        }

        if (ObjectHelper.isNotEmpty(annotation.onPrepare())) {
            Processor onPrepare = ZwangineContextHelper.mandatoryLookup(zwangineContext, annotation.onPrepare(), Processor.class);
            recipientList.setOnPrepare(onPrepare);
        }

        return recipientList;
    }

    @Override
    public AsyncProcessor createRoutingSlip(ZwangineContext zwangineContext, org.zenithblox.RoutingSlip annotation) {
        org.zenithblox.processor.RoutingSlip routingSlip = new org.zenithblox.processor.RoutingSlip(zwangineContext);
        routingSlip.setDelimiter(annotation.delimiter());
        routingSlip.setIgnoreInvalidEndpoints(annotation.ignoreInvalidEndpoints());
        routingSlip.setCacheSize(annotation.cacheSize());
        return routingSlip;
    }
}
