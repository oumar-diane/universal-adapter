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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.spi.Language;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.builder.PredicateBuilder;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.ObjectHelper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link Processor} which is used for POJO @Consume where you can have multiple @Consume on the same
 * endpoint/consumer and via predicate's can filter and call different methods.
 */
public final class SubscribeMethodProcessor extends AsyncProcessorSupport implements Navigate<Processor> {

    private final Endpoint endpoint;
    private final Map<AsyncProcessor, Predicate> methods = new LinkedHashMap<>();
    private Language simple;

    public SubscribeMethodProcessor(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public void addMethod(final Object pojo, final Method method, final Endpoint endpoint, String predicate) throws Exception {
        Processor answer = PluginHelper.getBeanProcessorFactory(endpoint.getZwangineContext())
                .createBeanProcessor(endpoint.getZwangineContext(), pojo, method);

        // must ensure the consumer is being executed in an unit of work so synchronization callbacks etc is invoked
        answer = PluginHelper.getInternalProcessorFactory(endpoint.getZwangineContext())
                .addUnitOfWorkProcessorAdvice(endpoint.getZwangineContext(), answer, null);
        Predicate p;
        if (ObjectHelper.isEmpty(predicate)) {
            p = PredicateBuilder.constant(true);
        } else {
            p = simple.createPredicate(predicate);
        }
        methods.put((AsyncProcessor) answer, p);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            // evaluate which predicate matches and call the method
            for (Map.Entry<AsyncProcessor, Predicate> entry : methods.entrySet()) {
                Predicate predicate = entry.getValue();
                if (predicate.matches(exchange)) {
                    return entry.getKey().process(exchange, callback);
                }
            }
        } catch (Exception e) {
            exchange.setException(e);
        }
        callback.done(true);
        return true;
    }

    @Override
    protected void doInit() throws Exception {
        simple = getEndpoint().getZwangineContext().resolveLanguage("simple");
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(methods.keySet());
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(methods.keySet());
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(methods.keySet());
    }

    @Override
    public String toString() {
        return "SubscribeMethodProcessor[" + endpoint + "]";
    }

    @Override
    public List<Processor> next() {
        return new ArrayList<>(methods.keySet());
    }

    @Override
    public boolean hasNext() {
        return !methods.isEmpty();
    }
}
