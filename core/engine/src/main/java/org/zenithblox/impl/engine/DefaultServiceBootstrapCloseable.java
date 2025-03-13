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
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.Service;
import org.zenithblox.spi.*;
import org.zenithblox.support.PluginHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default {@link BootstrapCloseable} which will collect all registered {@link Service} which is
 * {@link BootstrapCloseable} and run their task and remove the service from {@link ZwangineContext}.
 */
public class DefaultServiceBootstrapCloseable implements BootstrapCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultServiceBootstrapCloseable.class);

    private final ZwangineContext zwangineContext;
    private final ExtendedZwangineContext zwangineContextExtension;

    public DefaultServiceBootstrapCloseable(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        this.zwangineContextExtension = zwangineContext.getZwangineContextExtension();
    }

    @Override
    public void close() {
        // clear bootstrap configurers
        ConfigurerStrategy.clearBootstrapConfigurers();

        Set<Service> set
                = zwangineContextExtension.getServices().stream().filter(s -> s instanceof BootstrapCloseable)
                        .collect(Collectors.toSet());
        // its a bootstrap service
        for (Service service : set) {
            try {
                if (service instanceof BootstrapCloseable closeable) {
                    closeable.close();
                }
                // service is no longer needed as it was only intended during bootstrap
                zwangineContext.removeService(service);
            } catch (Exception e) {
                LOG.warn("Error during closing bootstrap service. This exception is ignored", e);
            }
        }

        // clear bootstrap configurer resolver
        ConfigurerResolver cr = PluginHelper.getBootstrapConfigurerResolver(zwangineContextExtension);
        if (cr instanceof BootstrapCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOG.warn("Error during closing bootstrap service. This exception is ignored", e);
            }
        }

        // clear processor factory
        ProcessorFactory pf = PluginHelper.getProcessorFactory(zwangineContextExtension);
        if (pf instanceof BootstrapCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOG.warn("Error during closing bootstrap service. This exception is ignored", e);
            }
        }

        // clear bootstrap factory finder
        FactoryFinder ff = zwangineContextExtension.getBootstrapFactoryFinder();
        if (ff instanceof BootstrapCloseable closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOG.warn("Error during closing bootstrap service. This exception is ignored", e);
            }
        }
        zwangineContextExtension.setBootstrapFactoryFinder(null);
    }

}
