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

import org.zenithblox.*;
import org.zenithblox.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * A useful base class for {@link LifecycleStrategy} implementations.
 */
public abstract class LifecycleStrategySupport implements LifecycleStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(LifecycleStrategySupport.class);

    // *******************************
    //
    // Helpers (adapters)
    //
    // ********************************

    public static LifecycleStrategy adapt(OnZwangineContextEvent handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextInitializing(ZwangineContext context) throws VetoZwangineContextStartException {
                if (handler instanceof OnZwangineContextInitializing onZwangineContextInitializing) {
                    onZwangineContextInitializing.onContextInitializing(context);
                }
            }

            @Override
            public void onContextInitialized(ZwangineContext context) throws VetoZwangineContextStartException {
                if (handler instanceof OnZwangineContextInitialized onZwangineContextInitialized) {
                    onZwangineContextInitialized.onContextInitialized(context);
                }
            }

            @Override
            public void onContextStarting(ZwangineContext context) throws VetoZwangineContextStartException {
                if (handler instanceof OnZwangineContextStarting onZwangineContextStarting) {
                    onZwangineContextStarting.onContextStarting(context);
                }
            }

            @Override
            public void onContextStarted(ZwangineContext context) {
                if (handler instanceof OnZwangineContextStarted onZwangineContextStarted) {
                    onZwangineContextStarted.onContextStarted(context);
                }
            }

            @Override
            public void onContextStopping(ZwangineContext context) {
                if (handler instanceof OnZwangineContextStopping onZwangineContextStopping) {
                    onZwangineContextStopping.onContextStopping(context);
                }
            }

            @Override
            public void onContextStopped(ZwangineContext context) {
                if (handler instanceof OnZwangineContextStopped onZwangineContextStopped) {
                    onZwangineContextStopped.onContextStopped(context);
                }
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextInitializing handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextInitializing(ZwangineContext context) throws VetoZwangineContextStartException {
                handler.onContextInitializing(context);
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextInitialized handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextInitialized(ZwangineContext context) throws VetoZwangineContextStartException {
                handler.onContextInitialized(context);
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextStarting handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextStarting(ZwangineContext context) throws VetoZwangineContextStartException {
                handler.onContextStarting(context);
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextStarted handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextStarted(ZwangineContext context) {
                handler.onContextStarted(context);
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextStopping handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextStopping(ZwangineContext context) {
                handler.onContextStopping(context);
            }
        };
    }

    public static LifecycleStrategy adapt(OnZwangineContextStopped handler) {
        return new LifecycleStrategySupport() {
            @Override
            public void onContextStopped(ZwangineContext context) {
                handler.onContextStopped(context);
            }
        };
    }

    // *******************************
    //
    // Helpers (functional)
    //
    // ********************************

    public static OnZwangineContextInitializing onZwangineContextInitializing(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    public static OnZwangineContextInitialized onZwangineContextInitialized(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    public static OnZwangineContextStarting onZwangineContextStarting(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    public static OnZwangineContextStarted onZwangineContextStarted(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    public static OnZwangineContextStopping onZwangineContextStopping(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    public static OnZwangineContextStopped onZwangineContextStopped(Consumer<ZwangineContext> consumer) {
        return consumer::accept;
    }

    // *******************************
    //
    // Helpers
    //
    // ********************************

    @Override
    public void onComponentAdd(String name, Component component) {
        // noop
    }

    @Override
    public void onComponentRemove(String name, Component component) {
        // noop
    }

    @Override
    public void onEndpointAdd(Endpoint endpoint) {
        // noop
    }

    @Override
    public void onEndpointRemove(Endpoint endpoint) {
        // noop
    }

    @Override
    public void onServiceAdd(ZwangineContext context, Service service, org.zenithblox.Workflow workflow) {
        // noop
    }

    @Override
    public void onServiceRemove(ZwangineContext context, Service service, org.zenithblox.Workflow workflow) {
        // noop
    }

    @Override
    public void onWorkflowsAdd(Collection<org.zenithblox.Workflow> workflows) {
        // noop
    }

    @Override
    public void onWorkflowsRemove(Collection<org.zenithblox.Workflow> workflows) {
        // noop
    }

    @Override
    public void onWorkflowContextCreate(Workflow workflow) {
        // noop
    }

    @Override
    public void onThreadPoolAdd(
            ZwangineContext zwangineContext, ThreadPoolExecutor threadPool, String id,
            String sourceId, String workflowId, String threadPoolProfileId) {
        // noop
    }

    @Override
    public void onThreadPoolRemove(ZwangineContext zwangineContext, ThreadPoolExecutor threadPool) {
        // noop
    }

    protected static void doAutoWire(String name, String kind, Object target, ZwangineContext zwangineContext) {
        PropertyConfigurer pc = PluginHelper.getConfigurerResolver(zwangineContext)
                .resolvePropertyConfigurer(name + "-" + kind, zwangineContext);
        if (pc instanceof PropertyConfigurerGetter getter) {
            String[] names = getter.getAutowiredNames();
            if (names != null) {
                for (String option : names) {
                    // is there already a configured value?
                    Object value = getter.getOptionValue(target, option, true);
                    if (value == null) {
                        Class<?> type = getter.getOptionType(option, true);
                        if (type != null) {
                            value = zwangineContext.getRegistry().findSingleByType(type);
                        }
                        if (value != null) {
                            boolean hit = pc.configure(zwangineContext, target, option, value, true);
                            if (hit) {
                                LOG.info(
                                        "Autowired property: {} on {}: {} as exactly one instance of type: {} ({}) found in the registry",
                                        option, kind, name, type.getName(), value.getClass().getName());
                            }
                        }
                    }
                }
            }
        }
    }
}
