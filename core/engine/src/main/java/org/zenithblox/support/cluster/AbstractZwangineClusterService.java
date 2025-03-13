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
import org.zenithblox.Ordered;
import org.zenithblox.cluster.ZwangineClusterMember;
import org.zenithblox.cluster.ZwangineClusterService;
import org.zenithblox.cluster.ZwangineClusterView;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ReferenceCount;
import org.zenithblox.util.concurrent.LockHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.StampedLock;

public abstract class AbstractZwangineClusterService<T extends ZwangineClusterView> extends ServiceSupport
        implements ZwangineClusterService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractZwangineClusterService.class);

    private final Map<String, ViewHolder<T>> views;
    private final Map<String, Object> attributes;
    private final StampedLock lock;
    private int order;
    private String id;
    private ZwangineContext zwangineContext;

    protected AbstractZwangineClusterService() {
        this(null, null);
    }

    protected AbstractZwangineClusterService(String id) {
        this(id, null);
    }

    protected AbstractZwangineClusterService(String id, ZwangineContext zwangineContext) {
        this.order = Ordered.LOWEST;
        this.id = id;
        this.zwangineContext = zwangineContext;
        this.views = new HashMap<>();
        this.lock = new StampedLock();
        this.attributes = new HashMap<>();
    }

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;

        LockHelper.doWithWriteLock(
                lock,
                () -> {
                    for (ViewHolder<T> holder : views.values()) {
                        holder.get().setZwangineContext(zwangineContext);
                    }
                });
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes.clear();
        this.attributes.putAll(attributes);
    }

    public void setAttribute(String key, Object value) {
        this.attributes.put(key, value);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    protected void doStart() throws Exception {
        LockHelper.doWithReadLockT(
                lock,
                () -> {
                    for (ViewHolder<T> holder : views.values()) {
                        holder.get().start();
                    }
                });
    }

    @Override
    protected void doStop() throws Exception {
        LockHelper.doWithReadLockT(
                lock,
                () -> {
                    for (ViewHolder<T> holder : views.values()) {
                        holder.get().stop();
                    }
                });
    }

    @Override
    public ZwangineClusterView getView(String namespace) throws Exception {
        return LockHelper.callWithWriteLock(
                lock,
                () -> {
                    ViewHolder<T> holder = views.get(namespace);

                    if (holder == null) {
                        T view = createView(namespace);
                        view.setZwangineContext(this.zwangineContext);

                        holder = new ViewHolder<>(view);

                        views.put(namespace, holder);
                    }

                    // Add reference and eventually start the workflow.
                    return holder.retain();
                });
    }

    @Override
    public void releaseView(ZwangineClusterView view) throws Exception {
        LockHelper.doWithWriteLock(
                lock,
                () -> {
                    ViewHolder<T> holder = views.get(view.getNamespace());

                    if (holder != null) {
                        holder.release();
                    }
                });
    }

    @Override
    public Collection<String> getNamespaces() {
        return LockHelper.supplyWithReadLock(
                lock,
                () -> {
                    // copy the key set so it is not modifiable and thread safe
                    // thus a little inefficient.
                    return new HashSet<>(views.keySet());
                });
    }

    @Override
    public void startView(String namespace) throws Exception {
        LockHelper.doWithWriteLockT(
                lock,
                () -> {
                    ViewHolder<T> holder = views.get(namespace);

                    if (holder != null) {
                        LOG.info("Force start of view {}", namespace);
                        holder.startView();
                    } else {
                        LOG.warn("Error forcing start of view {}: it does not exist", namespace);
                    }
                });
    }

    @Override
    public void stopView(String namespace) throws Exception {
        LockHelper.doWithWriteLockT(
                lock,
                () -> {
                    ViewHolder<T> holder = views.get(namespace);

                    if (holder != null) {
                        LOG.info("Force stop of view {}", namespace);
                        holder.stopView();
                    } else {
                        LOG.warn("Error forcing stop of view {}: it does not exist", namespace);
                    }
                });
    }

    @Override
    public boolean isLeader(String namespace) {
        return LockHelper.supplyWithReadLock(
                lock,
                () -> {
                    ViewHolder<T> holder = views.get(namespace);
                    if (holder != null) {
                        ZwangineClusterMember member = holder.get().getLocalMember();
                        if (member != null) {
                            return member.isLeader();
                        }
                    }

                    return false;
                });
    }

    // **********************************
    // Implementation
    // **********************************

    protected abstract T createView(String namespace) throws Exception;

    // **********************************
    // Helpers
    // **********************************

    private final class ViewHolder<V extends ZwangineClusterView> {
        private final V view;
        private final ReferenceCount count;

        ViewHolder(V view) {
            this.view = view;
            this.count = ReferenceCount.on(
                    () -> {
                        try {
                            this.startView();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> {
                        try {
                            this.stopView();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
        }

        V get() {
            return view;
        }

        V retain() {
            LOG.debug("Retain view {}, old-refs={}", view.getNamespace(), count.get());

            count.retain();

            return get();
        }

        void release() {
            LOG.debug("Release view {}, old-refs={}", view.getNamespace(), count.get());

            count.release();
        }

        void startView() throws Exception {
            if (AbstractZwangineClusterService.this.isRunAllowed()) {
                LOG.debug("Start view {}", view.getNamespace());
                view.start();
            } else {
                LOG.debug("Can't start view {} as cluster service is not running, view will be started on service start-up",
                        view.getNamespace());
            }
        }

        void stopView() throws Exception {
            LOG.debug("Stop view {}", view.getNamespace());
            view.stop();
        }
    }
}
