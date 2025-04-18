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

import org.zenithblox.Component;
import org.zenithblox.Consumer;
import org.zenithblox.Processor;

/**
 * A base class for an endpoint which the default consumer mode is to use a {@link org.zenithblox.PollingConsumer}
 */
public abstract class DefaultPollingEndpoint extends ScheduledPollEndpoint {

    protected DefaultPollingEndpoint() {
    }

    protected DefaultPollingEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer result = new DefaultScheduledPollConsumer(this, processor);
        configureConsumer(result);
        return result;
    }
}
