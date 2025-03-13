/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.impl.health;

import org.zenithblox.ZwangineContext;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckResolver;
import org.zenithblox.spi.PackageScanResourceResolver;
import org.zenithblox.spi.Resource;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * To load custom {@link HealthCheck} by classpath scanning.
 */
public class DefaultHealthChecksLoader {

    public static final String META_INF_SERVICES = "META-INF/services.org.zenithblox/health-check";
    private static final Logger LOG = LoggerFactory.getLogger(DefaultHealthChecksLoader.class);

    protected final ZwangineContext zwangineContext;
    protected final PackageScanResourceResolver resolver;
    protected final HealthCheckResolver healthCheckResolver;

    public DefaultHealthChecksLoader(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        this.resolver = PluginHelper.getPackageScanResourceResolver(zwangineContext);
        this.healthCheckResolver = PluginHelper.getHealthCheckResolver(zwangineContext);
    }

    public Collection<HealthCheck> loadHealthChecks() {
        Collection<HealthCheck> answer = new ArrayList<>();

        LOG.trace("Searching for {} health checks", META_INF_SERVICES);

        try {
            Collection<Resource> resources = resolver.findResources(META_INF_SERVICES + "/*-check");
            if (LOG.isDebugEnabled()) {
                LOG.debug("Discovered {} health checks from classpath scanning", resources.size());
            }
            for (Resource resource : resources) {
                LOG.trace("Resource: {}", resource);
                if (acceptResource(resource)) {
                    String id = extractId(resource);
                    LOG.trace("Loading HealthCheck: {}", id);
                    HealthCheck hc = healthCheckResolver.resolveHealthCheck(id);
                    if (hc != null) {
                        LOG.debug("Loaded HealthCheck: {}/{}", hc.getGroup(), hc.getId());
                        answer.add(hc);
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn("Error during scanning for custom health-checks on classpath due to: {}. This exception is ignored.",
                    e.getMessage());
        }

        return answer;
    }

    protected boolean acceptResource(Resource resource) {
        String loc = resource.getLocation();
        if (loc == null) {
            return false;
        }

        // this is an out of the box health-check
        if (loc.endsWith("context-check")) {
            return false;
        }

        return true;
    }

    protected String extractId(Resource resource) {
        String loc = resource.getLocation();
        loc = StringHelper.after(loc, META_INF_SERVICES + "/");
        // remove -check suffix
        if (loc != null && loc.endsWith("-check")) {
            loc = loc.substring(0, loc.length() - 6);
        }
        return loc;
    }

}
