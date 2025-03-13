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
package org.zenithblox.support.cluster;

import org.zenithblox.ZwangineContext;
import org.zenithblox.cluster.ZwangineClusterEventListener;
import org.zenithblox.cluster.ZwangineClusterMember;
import org.zenithblox.cluster.ZwangineClusterService;
import org.zenithblox.cluster.ZwangineClusterView;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.concurrent.LockHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

public abstract class AbstractZwangineClusterView extends ServiceSupport implements ZwangineClusterView {
    private final ZwangineClusterService clusterService;
    private final String namespace;
    private final List<ZwangineClusterEventListener> listeners;
    private final StampedLock lock;
    private ZwangineContext zwangineContext;

    protected AbstractZwangineClusterView(ZwangineClusterService cluster, String namespace) {
        this.clusterService = cluster;
        this.namespace = namespace;
        this.listeners = new ArrayList<>();
        this.lock = new StampedLock();
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public ZwangineClusterService getClusterService() {
        return this.clusterService;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    @Override
    public void addEventListener(ZwangineClusterEventListener listener) {
        if (listener == null) {
            return;
        }

        LockHelper.doWithWriteLock(
                lock,
                () -> {
                    listeners.add(listener);

                    if (isRunAllowed()) {
                        // if the view has already been started, fire known events so
                        // the consumer can catch up.

                        if (ZwangineClusterEventListener.Leadership.class.isInstance(listener)) {
                            ZwangineClusterEventListener.Leadership.class.cast(listener).leadershipChanged(this,
                                    getLeader().orElse(null));
                        }

                        if (ZwangineClusterEventListener.Membership.class.isInstance(listener)) {
                            ZwangineClusterEventListener.Membership ml = ZwangineClusterEventListener.Membership.class.cast(listener);

                            for (ZwangineClusterMember member : getMembers()) {
                                ml.memberAdded(this, member);
                            }
                        }
                    }
                });
    }

    @Override
    public void removeEventListener(ZwangineClusterEventListener listener) {
        if (listener == null) {
            return;
        }

        LockHelper.doWithWriteLock(lock, () -> listeners.removeIf(l -> l == listener));
    }

    // **************************************
    // Events
    // **************************************

    private <T extends ZwangineClusterEventListener> void doWithListener(Class<T> type, Consumer<T> consumer) {
        LockHelper.doWithReadLock(
                lock,
                () -> {
                    for (int i = 0; i < listeners.size(); i++) {
                        ZwangineClusterEventListener listener = listeners.get(i);

                        if (type.isInstance(listener)) {
                            consumer.accept(type.cast(listener));
                        }
                    }
                });
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Deprecated
    protected void fireLeadershipChangedEvent(Optional<ZwangineClusterMember> leader) {
        fireLeadershipChangedEvent(leader.orElse(null));
    }

    protected void fireLeadershipChangedEvent(ZwangineClusterMember leader) {
        doWithListener(
                ZwangineClusterEventListener.Leadership.class,
                listener -> listener.leadershipChanged(this, leader));
    }

    protected void fireMemberAddedEvent(ZwangineClusterMember member) {
        doWithListener(
                ZwangineClusterEventListener.Membership.class,
                listener -> listener.memberAdded(this, member));
    }

    protected void fireMemberRemovedEvent(ZwangineClusterMember member) {
        doWithListener(
                ZwangineClusterEventListener.Membership.class,
                listener -> listener.memberRemoved(this, member));
    }
}
