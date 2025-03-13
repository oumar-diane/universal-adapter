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
import org.zenithblox.support.DefaultAsyncProducer;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceHelper;

/**
 * Ensures a {@link Producer} is executed within an {@link org.zenithblox.spi.UnitOfWork}.
 */
public final class UnitOfWorkProducer extends DefaultAsyncProducer {

    private final Producer producer;
    private final AsyncProcessor processor;

    /**
     * The producer which should be executed within an {@link org.zenithblox.spi.UnitOfWork}.
     *
     * @param producer the producer
     */
    public UnitOfWorkProducer(Producer producer) {
        super(producer.getEndpoint());
        this.producer = producer;
        // wrap in unit of work
        ZwangineContext ecc = producer.getEndpoint().getZwangineContext();
        this.processor = PluginHelper.getInternalProcessorFactory(ecc)
                .addUnitOfWorkProcessorAdvice(ecc, producer, null);
    }

    @Override
    public Endpoint getEndpoint() {
        return producer.getEndpoint();
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        return processor.process(exchange, callback);
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(processor);
    }

    @Override
    protected void doInit() throws Exception {
        ServiceHelper.initService(processor);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        ServiceHelper.startService(processor);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        ServiceHelper.stopService(processor);
    }

    @Override
    public boolean isSingleton() {
        return producer.isSingleton();
    }

    @Override
    public String toString() {
        return "UnitOfWork(" + producer + ")";
    }
}
