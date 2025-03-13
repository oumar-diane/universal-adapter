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

/**
 * Marker interface for cluster events
 */
public interface ZwangineClusterEventListener {

    interface Leadership extends ZwangineClusterEventListener {

        /**
         * Notify a change in the leadership for a particular cluster.
         *
         * @param view   the cluster view
         * @param leader the optional new leader
         */
        void leadershipChanged(ZwangineClusterView view, ZwangineClusterMember leader);

    }

    interface Membership extends ZwangineClusterEventListener {

        /**
         * Notify a change (addition) in the cluster composition.
         *
         * @param view   the cluster view
         * @param member the member that has been added
         */
        void memberAdded(ZwangineClusterView view, ZwangineClusterMember member);

        /**
         * Notify a change (removal) in the cluster composition.
         *
         * @param view   the cluster view
         * @param member the member that has been removed
         */
        void memberRemoved(ZwangineClusterView view, ZwangineClusterMember member);

    }
}
