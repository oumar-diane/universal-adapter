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
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.support.service.ServiceHelper;

/**
 * A {@link org.zenithblox.Producer} which is created and started lazy, on the first message processed.
 */
public final class LazyStartProducer extends DefaultAsyncProducer implements DelegateProcessor {

    private volatile AsyncProducer delegate;

    public LazyStartProducer(Endpoint endpoint) {
        super(endpoint);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        try {
            // create and start producer lazy
            if (delegate == null) {
                lock.lock();
                try {
                    if (delegate == null) {
                        AsyncProducer newDelegate = AsyncProcessorConverterHelper.convert(getEndpoint().createProducer());
                        if (!ServiceHelper.isStarted(newDelegate)) {
                            ServiceHelper.startService(newDelegate);
                        }
                        delegate = newDelegate;
                    }
                } finally {
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            // error creating or starting delegated failed, so allow to re-create on next call
            delegate = null;
            exchange.setException(e);
            callback.done(true);
            return true;
        }
        return delegate.process(exchange, callback);
    }

    @Override
    public boolean isSingleton() {
        if (delegate != null) {
            return delegate.isSingleton();
        } else {
            return getEndpoint().isSingleton();
        }
    }

    @Override
    protected void doStart() {
        // noop as we dont want to start the delegate but its started on the first message processed
    }

    @Override
    protected void doStop() {
        ServiceHelper.stopService(delegate);
    }

    @Override
    protected void doSuspend() {
        ServiceHelper.suspendService(delegate);
    }

    @Override
    protected void doResume() {
        ServiceHelper.resumeService(delegate);
    }

    @Override
    protected void doShutdown() {
        ServiceHelper.stopAndShutdownService(delegate);
    }

    @Override
    public Processor getProcessor() {
        return delegate;
    }
}
