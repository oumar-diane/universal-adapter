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
import org.zenithblox.cluster.ZwangineClusterMember;
import org.zenithblox.cluster.ZwangineClusterView;
import org.zenithblox.cluster.ZwanginePreemptiveClusterService;
import org.zenithblox.cluster.ZwanginePreemptiveClusterView;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A {@link RebalancingZwangineClusterService} adds rebalancing capabilities to an underlying
 * {@link ZwanginePreemptiveClusterService}. Each view is treated as a partition by this cluster service and it makes sure
 * that all services belonging to the cluster own a balanced number of partitions (same number or difference at most 1
 * when not possible).
 */
public class RebalancingZwangineClusterService implements ZwanginePreemptiveClusterService {

    private static final Logger LOG = LoggerFactory.getLogger(RebalancingZwangineClusterService.class);

    protected ScheduledExecutorService serializedExecutor;

    protected ZwanginePreemptiveClusterService delegate;

    protected ZwangineContext zwangineContext;

    protected final long periodMillis;

    public RebalancingZwangineClusterService(ZwanginePreemptiveClusterService delegate, long periodMillis) {
        this.delegate = ObjectHelper.notNull(delegate, "delegate");
        this.periodMillis = periodMillis;
    }

    public RebalancingZwangineClusterService(ZwangineContext zwangineContext, ZwanginePreemptiveClusterService delegate,
                                          long periodMillis) {
        this.zwangineContext = ObjectHelper.notNull(zwangineContext, "zwangineContext");
        this.delegate = ObjectHelper.notNull(delegate, "delegate");
        this.periodMillis = periodMillis;
    }

    @Override
    public void start() {
        delegate.start();
        if (serializedExecutor == null) {
            serializedExecutor = getZwangineContext().getExecutorServiceManager().newSingleThreadScheduledExecutor(this,
                    "RebalancingClusterService");
            serializedExecutor.execute(this::reconcile);
        }
    }

    @Override
    public void stop() {
        if (serializedExecutor != null) {
            serializedExecutor.shutdownNow();
        }
        serializedExecutor = null;

        delegate.stop();
    }

    public ZwanginePreemptiveClusterService getDelegate() {
        return delegate;
    }

    public long getPeriodMillis() {
        return periodMillis;
    }

    public void setDelegate(ZwanginePreemptiveClusterService delegate) {
        this.delegate = delegate;
    }

    protected void reconcile() {
        Integer n = members();
        List<String> partitions = partitionList();
        int k = partitions.size();

        if (n == null || n == 0 || k == 0) {
            rescheduleAfterDelay();
            return;
        }

        int threshold = 0;
        while (threshold <= k) {
            threshold += n;
        }
        threshold -= n;

        int quota = threshold / n;

        List<String> main = new ArrayList<>();
        List<String> remaining = new ArrayList<>();
        for (int i = 0; i < threshold; i++) {
            main.add(partitions.get(i));
        }
        for (int i = threshold; i < partitions.size(); i++) {
            remaining.add(partitions.get(i));
        }

        rebalanceGroup(main, quota);
        rebalanceGroup(remaining, 1);
        rescheduleAfterDelay();
    }

    protected void rebalanceGroup(List<String> partitions, int quota) {
        List<String> owned = owned(partitions);
        if (owned == null) {
            return;
        }

        if (owned.size() < quota) {
            // Open all (to let the controller choose which ones)
            for (String partition : partitions) {
                setDisabled(partition, false);
            }
        } else if (owned.size() > quota) {
            for (int i = 0; i < owned.size() - quota; i++) {
                setDisabled(owned.get(i), true);
            }
        } else {
            // We're fine, but we prevent this instance from stealing locks that are not needed
            Set<String> ownedSet = new HashSet<>(owned);
            for (String partition : partitions) {
                if (!ownedSet.contains(partition)) {
                    setDisabled(partition, true);
                }
            }
        }
    }

    protected void setDisabled(String partition, boolean disabled) {
        try {
            LOG.debug("Setting partition {} to disabled={}...", partition, disabled);
            ZwanginePreemptiveClusterView view = delegate.getView(partition);
            if (view.isDisabled() != disabled) {
                view.setDisabled(disabled);
            }
        } catch (Exception ex) {
            LOG.warn("Could not get view {}", partition, ex);
        }
    }

    protected List<String> owned(List<String> partitions) {
        List<String> owned = new ArrayList<>(partitions.size());
        for (String partition : partitions) {
            try {
                ZwanginePreemptiveClusterView view = delegate.getView(partition);
                if (!view.isDisabled() && view.getLocalMember().isLeader()) {
                    owned.add(partition);
                }
            } catch (Exception ex) {
                LOG.warn("Could not get view {}", partition, ex);
                return null;
            }
        }
        return owned;
    }

    protected List<String> partitionList() {
        ArrayList<String> partitions = new ArrayList<>(this.getNamespaces());
        Collections.sort(partitions);
        return partitions;
    }

    protected Integer members() {
        Set<String> members = null;
        for (String group : this.getNamespaces()) {
            try {
                ZwanginePreemptiveClusterView view = delegate.getView(group);
                Set<String> viewMembers = view.getMembers().stream().map(ZwangineClusterMember::getId).collect(Collectors.toSet());
                if (members != null && !members.equals(viewMembers)) {
                    LOG.debug("View members don't match: {} vs {}", members, viewMembers);
                    return null;
                }
                members = viewMembers;
            } catch (Exception ex) {
                LOG.warn("Could not get view {}", group, ex);
                return null;
            }
        }
        return members != null ? members.size() : 0;
    }

    private void rescheduleAfterDelay() {
        this.serializedExecutor.schedule(this::reconcile,
                this.periodMillis,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public ZwanginePreemptiveClusterView getView(String namespace) throws Exception {
        return delegate.getView(namespace);
    }

    @Override
    public void releaseView(ZwangineClusterView view) throws Exception {
        delegate.releaseView(view);
    }

    @Override
    public Collection<String> getNamespaces() {
        return delegate.getNamespaces();
    }

    @Override
    public void startView(String namespace) throws Exception {
        delegate.startView(namespace);
    }

    @Override
    public void stopView(String namespace) throws Exception {
        delegate.stopView(namespace);
    }

    @Override
    public boolean isLeader(String namespace) {
        return delegate.isLeader(namespace);
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
        delegate.setZwangineContext(zwangineContext);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return this.zwangineContext;
    }

    @Override
    public void setId(String id) {
        delegate.setId(id);
    }

    @Override
    public String getId() {
        return delegate.getId();
    }
}
