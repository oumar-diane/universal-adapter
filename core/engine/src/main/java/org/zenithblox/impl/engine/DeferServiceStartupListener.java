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
import org.zenithblox.support.service.ServiceHelper;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * A {@link org.zenithblox.StartupListener} that defers starting {@link Service}s, until as late as possible during
 * the startup process of {@link ZwangineContext}.
 */
public class DeferServiceStartupListener implements StartupListener, Ordered {

    private final Set<Service> earlyServices = new CopyOnWriteArraySet<>();
    private final Set<Service> services = new CopyOnWriteArraySet<>();

    public void addService(Service service, boolean startEarly) {
        if (startEarly) {
            earlyServices.add(service);
        } else {
            services.add(service);
        }
    }

    @Override
    public void onZwangineContextStarting(ZwangineContext context, boolean alreadyStarted) throws Exception {
        doStart(earlyServices, context, alreadyStarted);
    }

    @Override
    public void onZwangineContextStarted(ZwangineContext context, boolean alreadyStarted) throws Exception {
        doStart(services, context, alreadyStarted);
    }

    protected void doStart(Set<Service> services, ZwangineContext context, boolean alreadyStarted) throws Exception {
        // new services may be added while starting a service
        // so use a while loop to get the newly added services as well
        while (!services.isEmpty()) {
            Service service = services.iterator().next();
            try {
                ServiceHelper.startService(service);
            } catch (Exception e) {
                if (service instanceof Endpoint endpoint) {
                    throw new ResolveEndpointFailedException(endpoint.getEndpointUri(), e);
                } else {
                    throw e;
                }
            } finally {
                services.remove(service);
            }
        }
    }

    @Override
    public int getOrder() {
        // we want to be last, so the other startup listeners run first
        return Ordered.LOWEST;
    }
}
