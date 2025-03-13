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
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NoFactoryAvailableException;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckRepository;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.spi.FactoryFinder;

/**
 * Default health check resolver that looks for health checks factories in
 * <b>META-INF/services/org/zentihblox/zwangine/health-check/</b>.
 */
public class DefaultHealthCheckResolver implements HealthCheckResolver, ZwangineContextAware {

    public static final String HEALTH_CHECK_RESOURCE_PATH = "META-INF/services/org.zenithblox/health-check/";

    protected FactoryFinder healthCheckFactory;
    private ZwangineContext zwangineContext;

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public HealthCheck resolveHealthCheck(String id) {
        // lookup in registry first
        HealthCheck answer = zwangineContext.getRegistry().lookupByNameAndType(id + "-health-check", HealthCheck.class);
        if (answer == null) {
            answer = zwangineContext.getRegistry().lookupByNameAndType(id, HealthCheck.class);
        }
        if (answer != null) {
            return answer;
        }

        Class<?> type = null;
        try {
            type = findHealthCheck(id, zwangineContext);
        } catch (NoFactoryAvailableException e) {
            // ignore
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no HealthCheck registered for id: " + id, e);
        }

        if (type != null) {
            if (HealthCheck.class.isAssignableFrom(type)) {
                return (HealthCheck) zwangineContext.getInjector().newInstance(type, false);
            } else {
                throw new IllegalArgumentException(
                        "Resolving health-check: " + id + " detected type conflict: Not a HealthCheck implementation. Found: "
                                                   + type.getName());
            }
        }

        return null;
    }

    @Override
    public HealthCheckRepository resolveHealthCheckRepository(String id) {
        // lookup in registry first
        HealthCheckRepository answer
                = zwangineContext.getRegistry().lookupByNameAndType(id + "-health-check-repository", HealthCheckRepository.class);
        if (answer == null) {
            answer = zwangineContext.getRegistry().lookupByNameAndType(id, HealthCheckRepository.class);
        }
        if (answer != null) {
            return answer;
        }

        Class<?> type = null;
        try {
            type = findHealthCheckRepository(id, zwangineContext);
        } catch (NoFactoryAvailableException e) {
            // ignore
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no HealthCheckRepository registered for id: " + id, e);
        }

        if (type != null) {
            if (HealthCheckRepository.class.isAssignableFrom(type)) {
                return (HealthCheckRepository) zwangineContext.getInjector().newInstance(type, false);
            } else {
                throw new IllegalArgumentException(
                        "Resolving health-check-repository: " + id
                                                   + " detected type conflict: Not a HealthCheckRepository implementation. Found: "
                                                   + type.getName());
            }
        }

        return null;
    }

    protected Class<?> findHealthCheck(String name, ZwangineContext context) throws Exception {
        if (healthCheckFactory == null) {
            healthCheckFactory = context.getZwangineContextExtension().getFactoryFinder(HEALTH_CHECK_RESOURCE_PATH);
        }
        return healthCheckFactory.findOptionalClass(name + "-check").orElse(null);
    }

    protected Class<?> findHealthCheckRepository(String name, ZwangineContext context) throws Exception {
        if (healthCheckFactory == null) {
            healthCheckFactory = context.getZwangineContextExtension().getFactoryFinder(HEALTH_CHECK_RESOURCE_PATH);
        }
        return healthCheckFactory.findOptionalClass(name + "-repository").orElse(null);
    }

}
