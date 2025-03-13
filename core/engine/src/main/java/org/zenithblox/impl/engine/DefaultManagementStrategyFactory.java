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
import org.zenithblox.spi.LifecycleStrategy;
import org.zenithblox.spi.ManagementStrategy;
import org.zenithblox.spi.ManagementStrategyFactory;

import java.util.Map;

/**
 * Factory for creating non JMX {@link ManagementStrategy}.
 */
public class DefaultManagementStrategyFactory implements ManagementStrategyFactory {

    @Override
    public ManagementStrategy create(ZwangineContext context, Map<String, Object> properties) throws Exception {
        return new DefaultManagementStrategy(context);
    }

    @Override
    public LifecycleStrategy createLifecycle(ZwangineContext context) throws Exception {
        // not in use for non JMX
        return null;
    }

    @Override
    public void setupManagement(ZwangineContext zwangineContext, ManagementStrategy strategy, LifecycleStrategy lifecycle) {
        zwangineContext.setManagementStrategy(strategy);

        if (!zwangineContext.getLifecycleStrategies().isEmpty()) {
            // zwangine-spring may re-initialize JMX during startup,
            // so remove any previous JMX initialized lifecycle strategy
            zwangineContext.getLifecycleStrategies()
                    .removeIf(s -> s.getClass().getName().startsWith("org.zenithblox.management"));
        }
    }
}
