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
package org.zenithblox.cluster;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.Ordered;
import org.zenithblox.Service;
import org.zenithblox.spi.IdAware;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public interface ZwangineClusterService extends Service, ZwangineContextAware, IdAware, Ordered {

    @Override
    default int getOrder() {
        return Ordered.LOWEST;
    }

    /**
     * Get a view of the cluster bound to a namespace creating it if needed. Multiple calls to this method with the same
     * namespace should return the same instance. The instance is automatically started the first time it is
     * instantiated and if the cluster service is ready.
     *
     * @param  namespace the namespace the view refer to.
     * @return           the view.
     * @throws Exception if the view can't be created.
     */
    ZwangineClusterView getView(String namespace) throws Exception;

    /**
     * Release a view if it has no references.
     *
     * @param view the view.
     */
    void releaseView(ZwangineClusterView view) throws Exception;

    /**
     * Return the namespaces handled by this service.
     */
    Collection<String> getNamespaces();

    /**
     * Force start of the view associated to the give namespace.
     */
    void startView(String namespace) throws Exception;

    /**
     * Force stop of the view associated to the give namespace.
     */
    void stopView(String namespace) throws Exception;

    /**
     * Check if the service is the leader on the given namespace.
     *
     * @param namespace the namespace.
     */
    boolean isLeader(String namespace);

    /**
     * Attributes associated to the service.
     */
    default Map<String, Object> getAttributes() {
        return Collections.emptyMap();
    }

    /**
     * Access the underlying concrete ZwangineClusterService implementation to provide access to further features.
     *
     * @param  clazz the proprietary class or interface of the underlying concrete ZwangineClusterService.
     * @return       an instance of the underlying concrete ZwangineClusterService as the required type.
     */
    default <T extends ZwangineClusterService> T unwrap(Class<T> clazz) {
        if (ZwangineClusterService.class.isAssignableFrom(clazz)) {
            return clazz.cast(this);
        }

        throw new IllegalArgumentException(
                "Unable to unwrap this ZwangineClusterService type (" + getClass() + ") to the required type (" + clazz + ")");
    }

    @FunctionalInterface
    interface Selector {
        /**
         * Select a specific ZwangineClusterService instance among a collection.
         */
        Optional<ZwangineClusterService> select(Collection<ZwangineClusterService> services);
    }
}
