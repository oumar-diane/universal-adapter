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
package org.zenithblox.support.cache;

import org.zenithblox.AsyncCallback;
import org.zenithblox.Endpoint;
import org.zenithblox.Exchange;
import org.zenithblox.support.EventHelper;
import org.zenithblox.util.StopWatch;

/**
 * Helper class to notify on exchange sending events in async engine
 */
class EventNotifierCallback implements AsyncCallback {
    private final AsyncCallback originalCallback;
    private final StopWatch watch;
    private final Exchange exchange;
    private final Endpoint endpoint;

    public EventNotifierCallback(AsyncCallback originalCallback, Exchange exchange,
                                 Endpoint endpoint) {
        this.originalCallback = originalCallback;
        this.exchange = exchange;
        this.endpoint = endpoint;
        final boolean sending = EventHelper.notifyExchangeSending(exchange.getContext(), exchange, endpoint);
        if (sending) {
            this.watch = new StopWatch();
        } else {
            this.watch = null;
        }
    }

    @Override
    public void done(boolean doneSync) {
        if (watch != null) {
            long timeTaken = watch.taken();
            EventHelper.notifyExchangeSent(exchange.getContext(), exchange, endpoint, timeTaken);
        }
        originalCallback.done(doneSync);
    }
}
