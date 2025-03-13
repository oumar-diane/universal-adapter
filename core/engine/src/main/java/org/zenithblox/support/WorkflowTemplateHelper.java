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
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceLoader;
import org.zenithblox.spi.WorkflowTemplateLoaderListener;
import org.zenithblox.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Helper for working with workflow templates.
 */
public final class WorkflowTemplateHelper {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowTemplateHelper.class);

    private WorkflowTemplateHelper() {
    }

    /**
     * Loads the workflow template with the given template id from a given location. After the template is loaded, it is
     * automatically added to the {@link ZwangineContext}.
     *
     * @param  zwangineContext the zwangine context
     * @param  listener     optional listener for when a workflow template is being loaded
     * @param  templateId   the template id
     * @param  location     location of the workflow template to load as a resource such as from classpath or file system
     * @throws Exception    is thrown if any kind of error loading the workflow template
     */
    public static void loadWorkflowTemplateFromLocation(
            ZwangineContext zwangineContext, WorkflowTemplateLoaderListener listener,
            String templateId, String location)
            throws Exception {

        if (location == null) {
            throw new IllegalArgumentException("Location is empty");
        }

        boolean found = false;
        final ResourceLoader resourceLoader = PluginHelper.getResourceLoader(zwangineContext);
        for (String path : location.split(",")) {
            // using dot as current dir must be expanded into absolute path
            if (".".equals(path) || "file:.".equals(path)) {
                path = new File(".").getAbsolutePath();
                path = "file:" + FileUtil.onlyPath(path);
            }
            String name = path;
            Resource res = null;
            // first try resource as-is if the path has an extension
            String ext = FileUtil.onlyExt(path);
            if (ext != null && !ext.isEmpty()) {
                res = resourceLoader.resolveResource(name);
            }
            if (res == null || !res.exists()) {
                if (!path.endsWith("/")) {
                    path += "/";
                }
                name = path + templateId + ".kamelet.yaml";
                res = resourceLoader.resolveResource(name);
            }
            if (res.exists()) {
                try {
                    if (listener != null) {
                        listener.loadWorkflowTemplate(res);
                    }
                } catch (Exception e) {
                    LOG.warn("WorkflowTemplateLoaderListener error due to {}. This exception is ignored", e.getMessage(), e);
                }
                PluginHelper.getWorkflowsLoader(zwangineContext).loadWorkflows(res);
                found = true;
                break;
            }
        }
        if (!found) {
            // fallback to old behaviour
            String path = location;
            // using dot as current dir must be expanded into absolute path
            if (".".equals(path) || "file:.".equals(path)) {
                path = new File(".").getAbsolutePath();
                path = "file:" + FileUtil.onlyPath(path);
            }
            if (!path.endsWith("/")) {
                path += "/";
            }
            String target = path + templateId + ".kamelet.yaml";
            PluginHelper.getWorkflowsLoader(zwangineContext).loadWorkflows(
                    resourceLoader.resolveResource(target));
        }
    }
}
