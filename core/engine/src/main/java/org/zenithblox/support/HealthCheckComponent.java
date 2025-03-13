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
import org.zenithblox.spi.Metadata;

/**
 * Base class for components to support configuring {@link org.zenithblox.health.HealthCheck}.
 */
public abstract class HealthCheckComponent extends DefaultComponent {

    @Metadata(label = "health", defaultValue = "true",
              description = "Used for enabling or disabling all consumer based health checks from this component")
    private boolean healthCheckConsumerEnabled = true;

    @Metadata(label = "health", defaultValue = "true",
              description = "Used for enabling or disabling all producer based health checks from this component."
                            + " Notice: Zwangine has by default disabled all producer based health-checks."
                            + " You can turn on producer checks globally by setting zwangine.health.producersEnabled=true.")
    private boolean healthCheckProducerEnabled = true;

    public HealthCheckComponent() {
    }

    public HealthCheckComponent(ZwangineContext context) {
        super(context);
    }

    public boolean isHealthCheckConsumerEnabled() {
        return healthCheckConsumerEnabled;
    }

    /**
     * Used for enabling or disabling all consumer based health checks from this component
     */
    public void setHealthCheckConsumerEnabled(boolean healthCheckConsumerEnabled) {
        this.healthCheckConsumerEnabled = healthCheckConsumerEnabled;
    }

    public boolean isHealthCheckProducerEnabled() {
        return healthCheckProducerEnabled;
    }

    /**
     * Used for enabling or disabling all producer based health checks from this component. Notice: Zwangine has by default
     * disabled all producer based health-checks. You can turn on producer checks globally by setting
     * zwangine.health.producersEnabled=true.
     */
    public void setHealthCheckProducerEnabled(boolean healthCheckProducerEnabled) {
        this.healthCheckProducerEnabled = healthCheckProducerEnabled;
    }

}
