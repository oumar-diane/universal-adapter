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
package org.zenithblox.support.processor;

import org.zenithblox.*;
import org.zenithblox.support.AsyncCallbackToCompletableFutureAdapter;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * A delegate pattern which delegates synchronous processing to a nested {@link org.zenithblox.Processor}.
 * <p/>
 * <b>Important:</b> This implementation <b>does</b> support the asynchronous routing engine, <b>only</b> if the logic
 * in the {@link #process(org.zenithblox.Exchange)} does not invoke EIPs; as it forces using synchronous processing
 * during the {@link #process(org.zenithblox.Exchange)} method call. If you are implementing an EIP pattern please use
 * this as the delegate, for simple EIPs.
 *
 * @see DelegateAsyncProcessor
 * @see DelegateProcessor
 */
public class DelegateSyncProcessor extends ServiceSupport
        implements org.zenithblox.DelegateProcessor, AsyncProcessor, Navigate<Processor> {
    protected final Processor processor;

    public DelegateSyncProcessor(Processor processor) {
        this.processor = processor;
    }

    @Override
    public String toString() {
        return "DelegateSync[" + processor + "]";
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        // force calling the sync method
        try {
            processor.process(exchange);
        } catch (Exception | LinkageError e) {
            // we catch all exceptions and try to catch relatively manageable low-level errors
            exchange.setException(e);
        } finally {
            // we are bridging a sync processor as async so callback with true
            callback.done(true);
        }
        return true;
    }

    @Override
    public CompletableFuture<Exchange> processAsync(Exchange exchange) {
        AsyncCallbackToCompletableFutureAdapter<Exchange> callback = new AsyncCallbackToCompletableFutureAdapter<>(exchange);
        process(exchange, callback);
        return callback.getFuture();
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        processor.process(exchange);
    }

    @Override
    public boolean hasNext() {
        return processor != null;
    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        List<Processor> answer = new ArrayList<>(1);
        answer.add(processor);
        return answer;
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
        ServiceHelper.startService(processor);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(processor);
    }
}
