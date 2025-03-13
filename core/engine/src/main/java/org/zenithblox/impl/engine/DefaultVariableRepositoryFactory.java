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

import org.zenithblox.ZwangineContext;
import org.zenithblox.Workflow;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.StaticService;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.VariableRepository;
import org.zenithblox.spi.VariableRepositoryFactory;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.GlobalVariableRepository;
import org.zenithblox.support.LifecycleStrategySupport;
import org.zenithblox.support.WorkflowVariableRepository;
import org.zenithblox.support.service.ServiceSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Default {@link VariableRepositoryFactory}.
 */
public class DefaultVariableRepositoryFactory extends ServiceSupport implements VariableRepositoryFactory, StaticService {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultVariableRepositoryFactory.class);

    public static final String RESOURCE_PATH = "META-INF/services/org/zentihblox/zwangine/variable-repository/";

    private final ZwangineContext zwangineContext;
    private VariableRepository global;
    private VariableRepository workflow;
    private FactoryFinder factoryFinder;

    public DefaultVariableRepositoryFactory(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public VariableRepository getVariableRepository(String id) {
        // ensure we are started if in use
        if (!isStarted()) {
            start();
        }

        if (global != null && "global".equals(id)) {
            return global;
        }
        if (workflow != null && "workflow".equals(id)) {
            return workflow;
        }

        VariableRepository repo = ZwangineContextHelper.lookup(zwangineContext, id, VariableRepository.class);
        if (repo == null) {
            repo = ZwangineContextHelper.lookup(zwangineContext, id + "-variable-repository", VariableRepository.class);
        }
        if (repo == null) {
            // try via factory finder
            Class<?> clazz = factoryFinder.findClass(id).orElse(null);
            if (clazz != null && VariableRepository.class.isAssignableFrom(clazz)) {
                repo = (VariableRepository) zwangineContext.getInjector().newInstance(clazz, true);
                zwangineContext.getRegistry().bind(id, repo);
                try {
                    zwangineContext.addService(repo);
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeException(e);
                }
            }
        }

        return repo;
    }

    @Override
    protected void doBuild() throws Exception {
        super.doBuild();
        this.factoryFinder = zwangineContext.getZwangineContextExtension().getBootstrapFactoryFinder(RESOURCE_PATH);
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        // let's see if there is a custom global repo
        VariableRepository repo = getVariableRepository("global");
        if (repo != null) {
            if (!(repo instanceof GlobalVariableRepository)) {
                LOG.info("Using VariableRepository: {} as global repository", repo.getId());
            }
            global = repo;
        } else {
            global = new GlobalVariableRepository();
            zwangineContext.getRegistry().bind(GLOBAL_VARIABLE_REPOSITORY_ID, global);
        }
        // let's see if there is a custom workflow repo
        repo = getVariableRepository("workflow");
        if (repo != null) {
            if (!(repo instanceof WorkflowVariableRepository)) {
                LOG.info("Using VariableRepository: {} as workflow repository", repo.getId());
            }
            workflow = repo;
        } else {
            workflow = new WorkflowVariableRepository();
            zwangineContext.getRegistry().bind(ROUTE_VARIABLE_REPOSITORY_ID, workflow);
        }

        if (!zwangineContext.hasService(global)) {
            zwangineContext.addService(global);
        }
        if (!zwangineContext.hasService(workflow)) {
            zwangineContext.addService(workflow);
            zwangineContext.addLifecycleStrategy(new LifecycleStrategySupport() {
                @Override
                public void onWorkflowsRemove(Collection<Workflow> workflows) {
                    // remove all variables from this workflow
                    for (Workflow r : workflows) {
                        workflow.removeVariable(r.getWorkflowId() + ":*");
                    }
                }
            });
        }
    }

}
