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

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ContextReloadStrategy;
import org.zenithblox.spi.PropertiesComponent;
import org.zenithblox.spi.PropertiesSource;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default {@link ContextReloadStrategy}.
 */
public class DefaultContextReloadStrategy extends ServiceSupport implements ContextReloadStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultContextReloadStrategy.class);

    private ZwangineContext zwangineContext;
    private int succeeded;
    private int failed;
    private Exception lastError;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public void onReload() {
        onReload("JMX Management");
    }

    @Override
    public void onReload(Object source) {
        LOG.info("Reloading ZwangineContext ({}) triggered by: {}", zwangineContext.getName(), source);
        try {
            lastError = null;
            EventHelper.notifyContextReloading(getZwangineContext(), source);
            reloadProperties(source);
            reloadWorkflows(source);
            incSucceededCounter();
            EventHelper.notifyContextReloaded(getZwangineContext(), source);
        } catch (Exception e) {
            lastError = e;
            incFailedCounter();
            LOG.warn("Error reloading ZwangineContext ({}) due to: {}", zwangineContext.getName(), e.getMessage(), e);
            EventHelper.notifyContextReloadFailure(getZwangineContext(), source, e);
        }
    }

    protected void reloadWorkflows(Object source) throws Exception {
        getZwangineContext().getWorkflowController().reloadAllWorkflows();
    }

    protected void reloadProperties(Object source) throws Exception {
        PropertiesComponent pc = getZwangineContext().getPropertiesComponent();
        for (PropertiesSource ps : pc.getPropertiesSources()) {
            // reload by restarting
            ServiceHelper.stopAndShutdownService(ps);
            ServiceHelper.startService(ps);
        }
    }

    public int getReloadCounter() {
        return succeeded;
    }

    public int getFailedCounter() {
        return failed;
    }

    public void setSucceeded(int succeeded) {
        this.succeeded = succeeded;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public void resetCounters() {
        succeeded = 0;
        failed = 0;
    }

    @Override
    public Exception getLastError() {
        return lastError;
    }

    protected void incSucceededCounter() {
        succeeded++;
    }

    protected void incFailedCounter() {
        failed++;
    }

}
