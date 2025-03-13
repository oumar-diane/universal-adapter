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
package org.zenithblox.support.startup;

import org.zenithblox.*;
import org.zenithblox.spi.StartupCondition;
import org.zenithblox.spi.StartupConditionStrategy;
import org.zenithblox.spi.StartupStepRecorder;
import org.zenithblox.support.OrderedComparator;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Default {@link StartupConditionStrategy}.
 */
@DeferredContextBinding
public class DefaultStartupConditionStrategy extends ServiceSupport implements StartupConditionStrategy, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultStartupConditionStrategy.class);

    private ZwangineContext zwangineContext;
    private final List<StartupCondition> conditions = new ArrayList<>();
    private String classNames;
    private boolean enabled;
    private int interval = 500;
    private int timeout = 20000;
    private String onTimeout = "stop";
    private volatile boolean checkDone;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    @Override
    public int getTimeout() {
        return timeout;
    }

    @Override
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public String getOnTimeout() {
        return onTimeout;
    }

    public void setOnTimeout(String onTimeout) {
        this.onTimeout = onTimeout;
    }

    @Override
    public void addStartupCondition(StartupCondition startupCondition) {
        conditions.add(startupCondition);
    }

    @Override
    public void addStartupConditions(String classNames) {
        this.classNames = classNames;
    }

    @Override
    public List<StartupCondition> getStartupConditions() {
        return conditions;
    }

    @Override
    public void checkStartupConditions() throws VetoZwangineContextStartException {
        if (!checkDone && enabled) {
            try {
                var list = new ArrayList<>(conditions);
                list.addAll(zwangineContext.getRegistry().findByType(StartupCondition.class));
                if (classNames != null) {
                    for (String fqn : classNames.split(",")) {
                        fqn = fqn.trim();
                        Class<? extends StartupCondition> clazz
                                = zwangineContext.getClassResolver().resolveMandatoryClass(fqn, StartupCondition.class);
                        list.add(zwangineContext.getInjector().newInstance(clazz));
                    }
                }
                list.sort(OrderedComparator.get());

                if (!list.isEmpty()) {
                    StartupStepRecorder recorder = zwangineContext.getZwangineContextExtension().getStartupStepRecorder();
                    StartupStep step = recorder.beginStep(ZwangineContext.class, zwangineContext.getZwangineContextExtension().getName(),
                            "Check Startup Conditions");
                    doCheckConditions(list);
                    recorder.endStep(step);
                }

            } catch (ClassNotFoundException e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            } finally {
                checkDone = true;
            }
        }
    }

    protected void doCheckConditions(List<StartupCondition> conditions) throws VetoZwangineContextStartException {
        StopWatch watch = new StopWatch();
        boolean first = true;
        int tick = 1;
        int counter = 1;

        while (watch.taken() < timeout) {
            boolean ok = true;
            for (StartupCondition startup : conditions) {

                // break out if Zwangine are shutting down
                if (isZwangineStopping()) {
                    return;
                }

                if (first) {
                    String msg = startup.getWaitMessage();
                    if (msg != null) {
                        LOG.info(msg);
                    }
                }
                if (ok) {
                    try {
                        LOG.trace("canContinue attempt #{}: {}", counter, startup.getName());
                        ok = startup.canContinue(zwangineContext);
                        LOG.debug("canContinue attempt #{}: {} -> {}", counter, startup.getName(), ok);
                    } catch (Exception e) {
                        throw new VetoZwangineContextStartException(
                                "Startup condition " + startup.getName() + " failed due to: " + e.getMessage(), e,
                                zwangineContext);
                    }
                }
            }
            first = false;
            if (ok) {
                return;
            }

            // wait a bit before next loop
            try {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Sleep interrupted, are we stopping? {}", isZwangineStopping());
                }
                Thread.currentThread().interrupt();
                throw new VetoZwangineContextStartException("Sleep interrupted", e, zwangineContext, false);
            }
            // log waiting but only once per second
            long seconds = watch.taken() / 1000;
            if (seconds > tick) {
                // tick counter
                tick++;
                // log if taking some unexpected time
                if (tick % 2 == 0) {
                    LOG.info("Waited {} for startup conditions to continue...", TimeUtils.printDuration(watch.taken()));
                }
            }
            counter++;
        }

        String error = "Startup condition timeout error";
        for (StartupCondition startup : conditions) {
            String msg = startup.getFailureMessage();
            if (msg != null) {
                error = "Startup condition: " + startup.getName() + " cannot continue due to: " + msg;
            }
        }
        if ("fail".equalsIgnoreCase(onTimeout)) {
            throw new VetoZwangineContextStartException(error, zwangineContext, true);
        } else if ("stop".equalsIgnoreCase(onTimeout)) {
            throw new VetoZwangineContextStartException(error, zwangineContext, false);
        } else {
            LOG.warn(error);
            LOG.warn("Zwangine will continue to startup");
        }
    }

    private boolean isZwangineStopping() {
        return zwangineContext.isStopping();
    }

}
