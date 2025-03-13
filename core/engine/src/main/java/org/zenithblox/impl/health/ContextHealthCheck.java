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
import org.zenithblox.Ordered;
import org.zenithblox.health.HealthCheckResultBuilder;

import java.util.Map;

/**
 * {@link org.zenithblox.health.HealthCheck} that checks the status of the {@link ZwangineContext} whether its started or
 * not.
 */
@org.zenithblox.spi.annotations.HealthCheck("context-check")
public final class ContextHealthCheck extends AbstractHealthCheck {

    public ContextHealthCheck() {
        super("zwangine", "context");
    }

    @Override
    public int getOrder() {
        // context should always be first
        return Ordered.HIGHEST;
    }

    @Override
    public boolean isLiveness() {
        // context is also liveness to ensure we have at least one liveness check
        return true;
    }

    @Override
    protected void doCall(HealthCheckResultBuilder builder, Map<String, Object> options) {
        builder.unknown();

        if (getZwangineContext() != null) {
            builder.detail("context.name", getZwangineContext().getName());
            builder.detail("context.version", getZwangineContext().getVersion());
            builder.detail("context.status", getZwangineContext().getStatus().name());
            builder.detail("context.phase", getZwangineContext().getZwangineContextExtension().getStatusPhase());

            if (getZwangineContext().getStatus().isStarted()) {
                builder.up();
            } else {
                // not ready also during graceful shutdown
                builder.down();
            }
        }
    }
}
