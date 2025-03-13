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

import org.zenithblox.spi.Resource;
import org.zenithblox.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for triggering on-demand reloading of Zwangine workflows in a running Zwangine application. The strategy is triggered
 * on-demand and reload all files from a directory (and subdirectories).
 */
public class WorkflowOnDemandReloadStrategy extends WorkflowWatcherReloadStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowOnDemandReloadStrategy.class);

    public WorkflowOnDemandReloadStrategy() {
        setScheduler(false);
    }

    public WorkflowOnDemandReloadStrategy(String directory) {
        super(directory);
        setScheduler(false);
    }

    public WorkflowOnDemandReloadStrategy(String directory, boolean recursive) {
        super(directory, recursive);
        setScheduler(false);
    }

    /**
     * Triggers on-demand reloading
     */
    public void onReload() {
        onReload("JMX Management");
    }

    /**
     * Triggers on-demand reloading
     */
    @Override
    public void onReload(Object source) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
            setLastError(null);
            // use bootstrap classloader from zwangine so its consistent
            ClassLoader acl = getZwangineContext().getApplicationContextClassLoader();
            if (acl != null) {
                Thread.currentThread().setContextClassLoader(acl);
            }
            doOnReload(source);
            incSucceededCounter();
        } catch (Exception e) {
            setLastError(e);
            incFailedCounter();
            LOG.warn("Error reloading workflows due to {}. This exception is ignored.", e.getMessage(), e);
        } finally {
            if (cl != null) {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    protected void doOnReload(Object source) throws Exception {
        List<Resource> properties = new ArrayList<>();
        List<Resource> workflows = new ArrayList<>();

        File dir = new File(getFolder());
        for (Path path : ResourceHelper.findInFileSystem(dir.toPath(), getPattern())) {
            Resource res = ResourceHelper.resolveResource(getZwangineContext(), "file:" + path.toString());
            String ext = FileUtil.onlyExt(path.getFileName().toString());
            if ("properties".equals(ext)) {
                properties.add(res);
            } else {
                workflows.add(res);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("On-demand reload scanned {} files (properties: {}, workflows: {})",
                    properties.size() + workflows.size(), properties.size(), workflows.size());
        }

        // reload properties first
        boolean reloaded = false;
        for (Resource res : properties) {
            reloaded |= onPropertiesReload(res, false);
        }
        boolean removeEverything = workflows.isEmpty();
        if (reloaded || !workflows.isEmpty()) {
            // trigger workflows to also reload if properties was reloaded
            onWorkflowReload(workflows, removeEverything);
        } else {
            // rare situation where all workflows are deleted
            onWorkflowReload(null, removeEverything);
        }
    }

}
