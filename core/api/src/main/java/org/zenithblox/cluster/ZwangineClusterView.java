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
import org.zenithblox.Service;

import java.util.List;
import java.util.Optional;

/**
 * Represents the View of the cluster at some given period of time.
 */
public interface ZwangineClusterView extends Service, ZwangineContextAware {
    /**
     * @return the cluster.
     */
    ZwangineClusterService getClusterService();

    /**
     * @return the namespace for this view.
     */
    String getNamespace();

    /**
     * Provides the leader member if elected.
     *
     * @return the leader member.
     */
    Optional<ZwangineClusterMember> getLeader();

    /**
     * Provides the local member.
     *
     * @return the local member.
     */
    ZwangineClusterMember getLocalMember();

    /**
     * Provides the list of members of the cluster.
     *
     * @return the list of members.
     */
    List<ZwangineClusterMember> getMembers();

    /**
     * Add an event listener.
     *
     * @param listener the event listener.
     */
    void addEventListener(ZwangineClusterEventListener listener);

    /**
     * Remove the event listener.
     *
     * @param listener the event listener.
     */
    void removeEventListener(ZwangineClusterEventListener listener);

    /**
     * Access the underlying concrete ZwangineClusterView implementation to provide access to further features.
     *
     * @param  clazz the proprietary class or interface of the underlying concrete ZwangineClusterView.
     * @return       an instance of the underlying concrete ZwangineClusterView as the required type.
     */
    default <T extends ZwangineClusterView> T unwrap(Class<T> clazz) {
        if (ZwangineClusterView.class.isAssignableFrom(clazz)) {
            return clazz.cast(this);
        }

        throw new IllegalArgumentException(
                "Unable to unwrap this ZwangineClusterView type (" + getClass() + ") to the required type (" + clazz + ")");
    }
}
