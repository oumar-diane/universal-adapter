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
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NonManagedService;
import org.zenithblox.StaticService;
import org.zenithblox.health.HealthCheck;
import org.zenithblox.health.HealthCheckRepository;
import org.zenithblox.support.service.ServiceSupport;

import java.util.Set;
import java.util.stream.Stream;

/**
 * {@link HealthCheckRepository} that uses the Zwangine {@link org.zenithblox.spi.Registry}.
 *
 * Zwangine will use this by default, so there is no need to register this manually.
 */
public class HealthCheckRegistryRepository extends ServiceSupport
        implements ZwangineContextAware, HealthCheckRepository, StaticService, NonManagedService {
    private ZwangineContext context;
    private boolean enabled = true;

    @Override
    public String getId() {
        return "registry-health-check-repository";
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.context = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return context;
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
    public Stream<HealthCheck> stream() {
        if (context != null && enabled) {
            Set<HealthCheck> set = this.context.getRegistry().findByType(HealthCheck.class);
            return set.stream()
                    .map(this::toHealthCheck);
        } else {
            return Stream.empty();
        }
    }

    private HealthCheck toHealthCheck(HealthCheck hc) {
        return hc;
    }

}
