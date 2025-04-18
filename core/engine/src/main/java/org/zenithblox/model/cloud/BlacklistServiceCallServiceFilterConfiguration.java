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
package org.zenithblox.model.cloud;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.util.ObjectHelper;

import java.util.*;

/**
 * @deprecated
 */
@Metadata(label = "routing,cloud,service-filter")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class BlacklistServiceCallServiceFilterConfiguration extends ServiceCallServiceFilterConfiguration {
    private List<String> servers;

    public BlacklistServiceCallServiceFilterConfiguration() {
        this(null);
    }

    public BlacklistServiceCallServiceFilterConfiguration(ServiceCallDefinition parent) {
        super(parent, "blacklist-service-filter");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public List<String> getServers() {
        return servers;
    }

    /**
     * Sets the server blacklist. Each entry can be a list of servers separated by comma in the format:
     * [service@]host:port,[service@]host2:port,[service@]host3:port
     *
     * @param  servers a list of servers.
     * @return         this instance
     */
    public void setServers(List<String> servers) {
        this.servers = servers;
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * Sets the server blacklist. Each entry can be a list of servers separated by comma in the format:
     * [service@]host:port,[service@]host2:port,[service@]host3:port
     *
     * @param  servers a list of servers.
     * @return         this instance
     */
    public BlacklistServiceCallServiceFilterConfiguration servers(List<String> servers) {
        setServers(servers);
        return this;
    }

    /**
     * Sets the server blacklist.
     *
     * @param  servers a list of servers separated by comma in the format:
     *                 [service@]host:port,[service@]host2:port,[service@]host3:port
     * @return         this instance
     */
    public BlacklistServiceCallServiceFilterConfiguration servers(String servers) {
        if (ObjectHelper.isNotEmpty(servers)) {
            String[] parts = servers.split(",");

            if (this.servers == null) {
                this.servers = new ArrayList<>();
            }

            this.servers.addAll(Arrays.asList(parts));
        }

        return this;
    }

    // *************************************************************************
    // Utilities
    // *************************************************************************

    @Override
    protected void postProcessFactoryParameters(ZwangineContext zwangineContext, Map<String, Object> parameters) throws Exception {
        List<String> servers = List.class.cast(parameters.get("servers"));

        if (ObjectHelper.isNotEmpty(servers)) {
            final ListIterator<String> it = servers.listIterator();
            while (it.hasNext()) {
                it.set(zwangineContext.resolvePropertyPlaceholders(it.next()));
            }

            parameters.put("servers", servers);
        }
    }
}
