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
package org.zenithblox.cloud;

/**
 * An interface to represent an object which wishes to be injected with a {@link ServiceFilter}
 */
@Deprecated(since = "4.8.0")
public interface ServiceFilterAware {

    /**
     * Injects the {@link ServiceFilter}
     *
     * @param serviceFilter the ServiceFilter
     */
    void setServiceFilter(ServiceFilter serviceFilter);

    /**
     * Get the {@link ServiceFilter}
     *
     * @return the ServiceFilter
     */
    ServiceFilter getServiceFilter();
}
