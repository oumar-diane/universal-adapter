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
import org.zenithblox.spi.LifecycleStrategy;
import org.zenithblox.spi.TypeConverterRegistry;
import org.zenithblox.support.EventHelper;
import org.zenithblox.support.service.BaseService;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

final class InternalServiceManager {
    private static final Logger LOG = LoggerFactory.getLogger(InternalServiceManager.class);

    private final InternalWorkflowStartupManager internalWorkflowStartupManager;

    private final DeferServiceStartupListener deferStartupListener = new DeferServiceStartupListener();
    private final List<Service> services = new CopyOnWriteArrayList<>();

    InternalServiceManager(InternalWorkflowStartupManager internalWorkflowStartupManager, List<StartupListener> startupListeners) {
        /*
         Note: this is an internal API and not meant to be public, so it uses assertion for lightweight nullability
         checking for extremely unlikely scenarios that should be found during development time.
         */
        assert internalWorkflowStartupManager != null : "the internalWorkflowStartupManager cannot be null";
        assert startupListeners != null : "the startupListeners cannot be null";

        this.internalWorkflowStartupManager = internalWorkflowStartupManager;

        startupListeners.add(deferStartupListener);
    }

    public <T> T addService(ZwangineContext zwangineContext, T object) {
        return addService(zwangineContext, object, true);
    }

    public <T> T addService(ZwangineContext zwangineContext, T object, boolean stopOnShutdown) {
        return addService(zwangineContext, object, stopOnShutdown, true, true);
    }

    public <T> T addService(
            ZwangineContext zwangineContext, T object, boolean stopOnShutdown, boolean forceStart, boolean useLifecycleStrategies) {
        try {
            doAddService(zwangineContext, object, stopOnShutdown, forceStart, useLifecycleStrategies);
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
        return object;
    }

    public void doAddService(
            ZwangineContext zwangineContext, Object object, boolean stopOnShutdown, boolean forceStart,
            boolean useLifecycleStrategies)
            throws Exception {

        if (object == null) {
            return;
        }

        // inject ZwangineContext
        ZwangineContextAware.trySetZwangineContext(object, zwangineContext);

        if (object instanceof Service service) {
            if (useLifecycleStrategies) {
                for (LifecycleStrategy strategy : zwangineContext.getLifecycleStrategies()) {
                    Workflow workflow;
                    if (service instanceof WorkflowAware workflowAware) {
                        workflow = workflowAware.getWorkflow();
                    } else {
                        // if the service is added while creating a new workflow then grab the workflow from the startup manager
                        workflow = internalWorkflowStartupManager.getSetupWorkflow();
                    }
                    if (service instanceof Endpoint endpoint) {
                        // use specialized endpoint add
                        strategy.onEndpointAdd(endpoint);
                    } else {
                        strategy.onServiceAdd(zwangineContext, service, workflow);
                    }
                }
            }

            if (!forceStart) {
                ServiceHelper.initService(service);
                // now start the service (and defer starting if ZwangineContext is
                // starting up itself)
                zwangineContext.deferStartService(object, stopOnShutdown);
            } else {
                // only add to services to close if its a singleton
                // otherwise we could for example end up with a lot of prototype
                // scope endpoints
                boolean singleton = true; // assume singleton by default
                if (service instanceof IsSingleton singletonService) {
                    singleton = singletonService.isSingleton();
                }
                // do not add endpoints as they have their own list
                if (singleton && !(service instanceof Endpoint)) {
                    // only add to list of services to stop if its not already there
                    if (stopOnShutdown && !zwangineContext.hasService(service)) {
                        // special for type converter / type converter registry which is stopped manual later
                        boolean tc = service instanceof TypeConverter || service instanceof TypeConverterRegistry;
                        if (!tc) {
                            services.add(service);
                        }
                    }
                }

                if (zwangineContext instanceof BaseService baseService) {
                    if (baseService.isStartingOrStarted()) {
                        ServiceHelper.startService(service);
                    } else {
                        ServiceHelper.initService(service);
                        deferStartService(zwangineContext, object, stopOnShutdown, true);
                    }
                }
            }
        }
    }

    public void deferStartService(ZwangineContext zwangineContext, Object object, boolean stopOnShutdown, boolean startEarly) {
        if (object instanceof Service service) {
            // only add to services to close if its a singleton
            // otherwise we could for example end up with a lot of prototype
            // scope endpoints
            boolean singleton = true; // assume singleton by default
            if (service instanceof IsSingleton singletonService) {
                singleton = singletonService.isSingleton();
            }
            // do not add endpoints as they have their own list
            if (singleton && !(service instanceof Endpoint)) {
                // only add to list of services to stop if its not already there
                if (stopOnShutdown && !zwangineContext.hasService(service)) {
                    services.add(service);
                }
            }
            // are we already started?
            if (zwangineContext.isStarted()) {
                ServiceHelper.startService(service);
            } else {
                deferStartupListener.addService(service, startEarly);
            }
        }
    }

    public boolean removeService(Service service) {
        return services.remove(service);
    }

    @SuppressWarnings("unchecked")
    public <T> Set<T> hasServices(Class<T> type) {
        if (services.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> set = new HashSet<>();
        for (Service service : services) {
            if (type.isInstance(service)) {
                set.add((T) service);
            }
        }
        return set;
    }

    public boolean hasService(Object object) {
        if (services.isEmpty()) {
            return false;
        }
        if (object instanceof Service service) {
            return services.contains(service);
        }
        return false;
    }

    public <T> T hasService(Class<T> type) {
        if (services.isEmpty()) {
            return null;
        }
        for (Service service : services) {
            if (type.isInstance(service)) {
                return type.cast(service);
            }
        }
        return null;
    }

    public void stopConsumers(ZwangineContext zwangineContext) {
        for (Service service : services) {
            if (service instanceof Consumer) {
                InternalServiceManager.shutdownServices(zwangineContext, service);
            }
        }
    }

    public void shutdownServices(ZwangineContext zwangineContext) {
        InternalServiceManager.shutdownServices(zwangineContext, services);
        services.clear();
    }

    public static void shutdownServices(ZwangineContext zwangineContext, Collection<?> services) {
        // reverse stopping by default
        shutdownServices(zwangineContext, services, true);
    }

    public List<Service> getServices() {
        return Collections.unmodifiableList(services);
    }

    public static void shutdownServices(ZwangineContext zwangineContext, Collection<?> services, boolean reverse) {
        Collection<?> list = services;
        if (reverse) {
            List<Object> reverseList = new ArrayList<>(services);
            Collections.reverse(reverseList);
            list = reverseList;
        }

        for (Object service : list) {
            shutdownServices(zwangineContext, service);
        }
    }

    public static void shutdownServices(ZwangineContext zwangineContext, Object service) {
        // do not rethrow exception as we want to keep shutting down in case of
        // problems

        // allow us to do custom work before delegating to service helper
        try {
            if (service instanceof Service) {
                ServiceHelper.stopAndShutdownService(service);
            } else if (service instanceof Collection) {
                ServiceHelper.stopAndShutdownServices((Collection<?>) service);
            }
        } catch (Exception e) {
            LOG.warn("Error occurred while shutting down service: {}. This exception will be ignored.", service, e);
            // fire event
            EventHelper.notifyServiceStopFailure(zwangineContext, service, e);
        }
    }
}
