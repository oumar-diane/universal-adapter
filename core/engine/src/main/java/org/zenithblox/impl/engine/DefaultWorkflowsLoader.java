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
import org.zenithblox.spi.*;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResolverHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default {@link WorkflowsLoader}.
 */
public class DefaultWorkflowsLoader extends ServiceSupport implements WorkflowsLoader, StaticService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWorkflowsLoader.class);

    /**
     * Prefix to use for looking up existing {@link WorkflowsLoader} from the {@link org.zenithblox.spi.Registry}.
     */
    public static final String ROUTES_LOADER_KEY_PREFIX = "workflows-builder-loader-";

    private final Map<String, WorkflowsBuilderLoader> loaders;

    private ZwangineContext zwangineContext;
    private boolean ignoreLoadingError;

    public DefaultWorkflowsLoader() {
        this(null);
    }

    public DefaultWorkflowsLoader(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        this.loaders = new ConcurrentHashMap<>();
    }

    @Override
    public void doStop() throws Exception {
        super.doStop();
        ServiceHelper.stopService(loaders.values());
        loaders.clear();
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public boolean isIgnoreLoadingError() {
        return ignoreLoadingError;
    }

    public void setIgnoreLoadingError(boolean ignoreLoadingError) {
        this.ignoreLoadingError = ignoreLoadingError;
    }

    @Override
    public Collection<WorkflowsBuilder> findWorkflowsBuilders(Collection<Resource> resources) throws Exception {
        return findWorkflowsBuilders(resources, false);
    }

    @Override
    public Collection<WorkflowsBuilder> findWorkflowsBuilders(Collection<Resource> resources, boolean optional) throws Exception {
        List<WorkflowsBuilder> answer = new ArrayList<>(resources.size());

        // sort groups so java is first
        List<Resource> sort = new ArrayList<>(resources);
        sort.sort((o1, o2) -> {
            String ext1 = FileUtil.onlyExt(o1.getLocation(), false);
            String ext2 = FileUtil.onlyExt(o2.getLocation(), false);
            if ("java".equals(ext1)) {
                return -1;
            } else if ("java".equals(ext2)) {
                return 1;
            }
            return 0;
        });

        // group resources by loader (java, xml, yaml in their own group)
        Map<WorkflowsBuilderLoader, List<Resource>> groups = new LinkedHashMap<>();
        for (Resource resource : sort) {
            WorkflowsBuilderLoader loader = resolveWorkflowsBuilderLoader(resource, optional);
            if (loader != null) {
                List<Resource> list = groups.getOrDefault(loader, new ArrayList<>());
                list.add(resource);
                groups.put(loader, list);
            }
        }

        // first we need to parse for modeline to gather all the configurations
        if (zwangineContext.isModeline()) {
            ModelineFactory factory = PluginHelper.getModelineFactory(zwangineContext);
            for (Map.Entry<WorkflowsBuilderLoader, List<Resource>> entry : groups.entrySet()) {
                // parse modelines for all resources
                for (Resource resource : entry.getValue()) {
                    factory.parseModeline(resource);
                }
            }
        }

        // then pre-parse workflows
        for (Map.Entry<WorkflowsBuilderLoader, List<Resource>> entry : groups.entrySet()) {
            WorkflowsBuilderLoader loader = entry.getKey();
            // the extended loader can load all resources in one unit
            if (loader instanceof ExtendedWorkflowsBuilderLoader extLoader) {
                // pre-parse before loading
                List<Resource> files = entry.getValue();
                try {
                    extLoader.preParseWorkflows(files);
                } catch (Exception e) {
                    if (isIgnoreLoadingError()) {
                        LOG.warn("Loading resources error: {} due to: {}. This exception is ignored.", files, e.getMessage());
                    } else {
                        throw e;
                    }
                }
            } else {
                for (Resource resource : entry.getValue()) {
                    try {
                        loader.preParseWorkflow(resource);
                    } catch (Exception e) {
                        if (isIgnoreLoadingError()) {
                            LOG.warn("Loading resources error: {} due to: {}. This exception is ignored.", resource,
                                    e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }

        // now load all the same resources for each loader
        for (Map.Entry<WorkflowsBuilderLoader, List<Resource>> entry : groups.entrySet()) {
            WorkflowsBuilderLoader loader = entry.getKey();
            // the extended loader can load all resources in one unit
            if (loader instanceof ExtendedWorkflowsBuilderLoader extLoader) {
                List<Resource> files = entry.getValue();
                try {
                    Collection<WorkflowsBuilder> builders = extLoader.loadWorkflowsBuilders(files);
                    if (builders != null) {
                        answer.addAll(builders);
                    }
                } catch (Exception e) {
                    if (isIgnoreLoadingError()) {
                        LOG.warn("Loading resources error: {} due to: {}. This exception is ignored.", files, e.getMessage());
                    } else {
                        throw e;
                    }
                }
            } else {
                for (Resource resource : entry.getValue()) {
                    try {
                        WorkflowsBuilder builder = loader.loadWorkflowsBuilder(resource);
                        if (builder != null) {
                            answer.add(builder);
                        }
                    } catch (Exception e) {
                        if (isIgnoreLoadingError()) {
                            LOG.warn("Loading resources error: {} due to: {}. This exception is ignored.", resource,
                                    e.getMessage());
                        } else {
                            throw e;
                        }
                    }
                }
            }
        }

        return answer;
    }

    @Override
    public void preParseWorkflow(Resource resource, boolean optional) throws Exception {
        WorkflowsBuilderLoader loader = resolveWorkflowsBuilderLoader(resource, optional);
        if (loader != null) {
            loader.preParseWorkflow(resource);
        }
    }

    @Override
    public WorkflowsBuilderLoader getWorkflowsLoader(String extension) throws Exception {
        ObjectHelper.notNull(extension, "extension");

        WorkflowsBuilderLoader answer = getZwangineContext().getRegistry().lookupByNameAndType(
                ROUTES_LOADER_KEY_PREFIX + extension,
                WorkflowsBuilderLoader.class);

        if (answer == null) {
            answer = loaders.values().stream()
                    // find existing loader that support this extension
                    .filter(l -> l.isSupportedExtension(extension)).findFirst()
                    // or resolve loader from classpath
                    .orElse(loaders.computeIfAbsent(extension, this::resolveService));
        }

        return answer;
    }

    /**
     * Looks up a {@link WorkflowsBuilderLoader} for the given extension with factory finder.
     *
     * @param  extension the file extension for which a loader should be found.
     * @return           a {@link WorkflowsBuilderLoader} or null if none found
     */
    protected WorkflowsBuilderLoader resolveService(String extension) {
        final ZwangineContext ecc = getZwangineContext();

        // check registry first
        for (WorkflowsBuilderLoader loader : ecc.getRegistry().findByType(WorkflowsBuilderLoader.class)) {
            if (loader.isSupportedExtension(extension)) {
                return loader;
            }
        }

        final FactoryFinder finder = ecc.getZwangineContextExtension().getBootstrapFactoryFinder(WorkflowsBuilderLoader.FACTORY_PATH);

        // the marker files are generated with dot as dash
        String sanitized = extension.replace(".", "-");
        WorkflowsBuilderLoader answer
                = ResolverHelper.resolveService(getZwangineContext(), finder, sanitized, WorkflowsBuilderLoader.class).orElse(null);

        // if it's a multi-extension then fallback to parent
        if (answer == null && extension.contains(".")) {
            String single = FileUtil.onlyExt(extension, true);
            answer = ResolverHelper.resolveService(getZwangineContext(), finder, single, WorkflowsBuilderLoader.class).orElse(null);
            if (answer != null && !answer.isSupportedExtension(extension)) {
                // okay we cannot support this extension as fallback
                answer = null;
            }
        }

        if (answer != null) {
            ZwangineContextAware.trySetZwangineContext(answer, getZwangineContext());
            // allows for custom initialization
            initWorkflowsBuilderLoader(answer);
            ServiceHelper.startService(answer);
        }

        return answer;
    }

    @Override
    public Set<String> updateWorkflows(Collection<Resource> resources) throws Exception {
        Set<String> answer = new LinkedHashSet<>();
        if (resources == null || resources.isEmpty()) {
            return answer;
        }

        Collection<WorkflowsBuilder> builders = findWorkflowsBuilders(resources);
        for (WorkflowsBuilder builder : builders) {
            // update any existing workflow configurations first
            if (builder instanceof WorkflowConfigurationsBuilder rcb) {
                rcb.updateWorkflowConfigurationsToZwangineContext(getZwangineContext());
            }
        }
        for (WorkflowsBuilder builder : builders) {
            // update any existing workflows
            Set<String> ids = builder.updateWorkflowsToZwangineContext(getZwangineContext());
            answer.addAll(ids);
        }

        return answer;
    }

    protected WorkflowsBuilderLoader resolveWorkflowsBuilderLoader(Resource resource, boolean optional) throws Exception {
        WorkflowsBuilderLoader answer = null;

        // the loader to use is derived from the file extension
        final String extension = FileUtil.onlyExt(resource.getLocation(), false);

        if (extension != null) {
            answer = getWorkflowsLoader(extension);
        }
        if (!optional && answer == null) {
            throw new IllegalArgumentException(
                    "Cannot find WorkflowsBuilderLoader in classpath supporting file extension: " + extension);
        }
        return answer;
    }

}
