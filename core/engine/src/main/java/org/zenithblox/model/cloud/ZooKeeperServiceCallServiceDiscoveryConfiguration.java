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

import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

@Metadata(label = "routing,cloud,service-discovery")
@Configurer(extended = true)
public class ZooKeeperServiceCallServiceDiscoveryConfiguration extends ServiceCallServiceDiscoveryConfiguration {
    private String nodes;
    private String namespace;
    private String reconnectBaseSleepTime;
    private String reconnectMaxSleepTime;
    private String reconnectMaxRetries;
    private String sessionTimeout;
    private String connectionTimeout;
    private String basePath;

    public ZooKeeperServiceCallServiceDiscoveryConfiguration() {
        this(null);
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration(ServiceCallDefinition parent) {
        super(parent, "zookeeper-service-discovery");
    }

    // *************************************************************************
    // Getter/Setter
    // *************************************************************************

    public String getNodes() {
        return nodes;
    }

    /**
     * A comma separate list of servers to connect to in the form host:port
     */
    public void setNodes(String nodes) {
        this.nodes = nodes;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * As ZooKeeper is a shared space, users of a given cluster should stay within a pre-defined namespace. If a
     * namespace is set here, all paths will get pre-pended with the namespace
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getReconnectBaseSleepTime() {
        return reconnectBaseSleepTime;
    }

    /**
     * Initial amount of time to wait between retries.
     */
    public void setReconnectBaseSleepTime(String reconnectBaseSleepTime) {
        this.reconnectBaseSleepTime = reconnectBaseSleepTime;
    }

    public String getReconnectMaxSleepTime() {
        return reconnectMaxSleepTime;
    }

    /**
     * Max time in ms to sleep on each retry
     */
    public void setReconnectMaxSleepTime(String reconnectMaxSleepTime) {
        this.reconnectMaxSleepTime = reconnectMaxSleepTime;
    }

    public String getReconnectMaxRetries() {
        return reconnectMaxRetries;
    }

    /**
     * Max number of times to retry
     */
    public void setReconnectMaxRetries(String reconnectMaxRetries) {
        this.reconnectMaxRetries = reconnectMaxRetries;
    }

    public String getSessionTimeout() {
        return sessionTimeout;
    }

    /**
     * Session timeout.
     */
    public void setSessionTimeout(String sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public String getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Connection timeout.
     */
    public void setConnectionTimeout(String connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public String getBasePath() {
        return basePath;
    }

    /**
     * Set the base path to store in ZK
     */
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    public ZooKeeperServiceCallServiceDiscoveryConfiguration nodes(String nodes) {
        setNodes(nodes);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration namespace(String namespace) {
        setNamespace(namespace);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration reconnectBaseSleepTime(String reconnectBaseSleepTime) {
        setReconnectBaseSleepTime(reconnectBaseSleepTime);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration reconnectMaxSleepTime(String reconnectMaxSleepTime) {
        setReconnectMaxSleepTime(reconnectMaxSleepTime);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration reconnectMaxRetries(int reconnectMaxRetries) {
        setReconnectMaxRetries(Integer.toString(reconnectMaxRetries));
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration sessionTimeout(String sessionTimeout) {
        setSessionTimeout(sessionTimeout);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration connectionTimeout(String connectionTimeout) {
        setConnectionTimeout(connectionTimeout);
        return this;
    }

    public ZooKeeperServiceCallServiceDiscoveryConfiguration basePath(String basePath) {
        setBasePath(basePath);
        return this;
    }
}
