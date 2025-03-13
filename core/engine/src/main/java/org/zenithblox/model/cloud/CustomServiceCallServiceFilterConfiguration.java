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
import org.zenithblox.cloud.ServiceFilter;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

@Metadata(label = "routing,cloud,service-filter")
@Configurer(extended = true)
@Deprecated(since = "3.19.0")
public class CustomServiceCallServiceFilterConfiguration extends ServiceCallServiceFilterConfiguration {
    private String serviceFilterRef;
    private ServiceFilter serviceFilter;

    public CustomServiceCallServiceFilterConfiguration() {
        this(null);
    }

    public CustomServiceCallServiceFilterConfiguration(ServiceCallDefinition parent) {
        super(parent, "custom-service-filter");
    }

    // *************************************************************************
    // Properties
    // *************************************************************************

    public String getServiceFilterRef() {
        return serviceFilterRef;
    }

    /**
     * Reference of a ServiceFilter
     */
    public void setServiceFilterRef(String serviceFilterRef) {
        this.serviceFilterRef = serviceFilterRef;
    }

    public ServiceFilter getServiceFilter() {
        return serviceFilter;
    }

    /**
     * Set the ServiceFilter
     */
    public void setServiceFilter(ServiceFilter serviceFilter) {
        this.serviceFilter = serviceFilter;
    }

    // *************************************************************************
    // Fluent API
    // *************************************************************************

    /**
     * Reference of a ServiceFilter
     */
    public CustomServiceCallServiceFilterConfiguration serviceFilter(String serviceFilter) {
        setServiceFilterRef(serviceFilter);
        return this;
    }

    /**
     * Set the ServiceFilter
     */
    public CustomServiceCallServiceFilterConfiguration serviceFilter(ServiceFilter serviceFilter) {
        setServiceFilter(serviceFilter);
        return this;
    }

    // *************************************************************************
    // Factory
    // *************************************************************************

    @Override
    public ServiceFilter newInstance(ZwangineContext zwangineContext) throws Exception {
        ServiceFilter answer = serviceFilter;
        if (serviceFilterRef != null) {
            answer = zwangineContext.getRegistry().lookupByNameAndType(serviceFilterRef, ServiceFilter.class);
        }

        return answer;
    }
}
