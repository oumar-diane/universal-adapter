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

import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.Service;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class to control lifecycle for a set of child {@link org.zenithblox.Service}s.
 */
public abstract class ChildServiceSupport extends ServiceSupport {

    private static final Logger LOG = LoggerFactory.getLogger(ChildServiceSupport.class);

    protected volatile List<Service> childServices;

    @Override
    public void start() {
        lock.lock();
        try {
            if (status == STARTED) {
                LOG.trace("Service: {} already started", this);
                return;
            }
            if (status == STARTING) {
                LOG.trace("Service: {} already starting", this);
                return;
            }
            try {
                ServiceHelper.initService(childServices);
            } catch (Exception e) {
                status = FAILED;
                LOG.trace("Error while initializing service: {}", this, e);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
            try {
                status = STARTING;
                LOG.trace("Starting service: {}", this);
                ServiceHelper.startService(childServices);
                doStart();
                status = STARTED;
                LOG.trace("Service: {} started", this);
            } catch (Exception e) {
                status = FAILED;
                LOG.trace("Error while starting service: {}", this, e);
                ServiceHelper.stopService(childServices);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void stop() {
        lock.lock();
        try {
            if (status == STOPPED || status == SHUTTING_DOWN || status == SHUTDOWN) {
                LOG.trace("Service: {} already stopped", this);
                return;
            }
            if (status == STOPPING) {
                LOG.trace("Service: {} already stopping", this);
                return;
            }
            status = STOPPING;
            LOG.trace("Stopping service: {}", this);
            try {
                doStop();
                ServiceHelper.stopService(childServices);
                status = STOPPED;
                LOG.trace("Service: {} stopped service", this);
            } catch (Exception e) {
                status = FAILED;
                LOG.trace("Error while stopping service: {}", this, e);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void shutdown() {
        lock.lock();
        try {
            if (status == SHUTDOWN) {
                LOG.trace("Service: {} already shut down", this);
                return;
            }
            if (status == SHUTTING_DOWN) {
                LOG.trace("Service: {} already shutting down", this);
                return;
            }
            stop();
            status = SHUTDOWN;
            LOG.trace("Shutting down service: {}", this);
            try {
                doShutdown();
                ServiceHelper.stopAndShutdownServices(childServices);
                LOG.trace("Service: {} shut down", this);
                status = SHUTDOWN;
            } catch (Exception e) {
                status = FAILED;
                LOG.trace("Error shutting down service: {}", this, e);
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void addChildService(Object childService) {
        if (childService instanceof Service service) {
            lock.lock();
            try {
                if (childServices == null) {
                    childServices = new ArrayList<>();
                }
                childServices.add(service);
            } finally {
                lock.unlock();
            }
        }
    }

    protected boolean removeChildService(Object childService) {
        if (childService instanceof Service) {
            lock.lock();
            try {
                if (childServices != null) {
                    return childServices.remove(childService);
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

}
