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
@Deprecated(since = "3.19.0")
public class DnsServiceCallServiceDiscoveryConfiguration extends ServiceCallServiceDiscoveryConfiguration {
    @Metadata(defaultValue = "_tcp")
    private String proto = "_tcp";
    private String domain;

    public DnsServiceCallServiceDiscoveryConfiguration() {
        this(null);
    }

    public DnsServiceCallServiceDiscoveryConfiguration(ServiceCallDefinition parent) {
        super(parent, "dns-service-discovery");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public String getProto() {
        return proto;
    }

    /**
     * The transport protocol of the desired service.
     */
    public void setProto(String proto) {
        this.proto = proto;
    }

    public String getDomain() {
        return domain;
    }

    /**
     * The domain name;
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * The transport protocol of the desired service.
     */
    public DnsServiceCallServiceDiscoveryConfiguration proto(String proto) {
        setProto(proto);
        return this;
    }

    /**
     * The domain name;
     */
    public DnsServiceCallServiceDiscoveryConfiguration domain(String domain) {
        setDomain(domain);
        return this;
    }
}
