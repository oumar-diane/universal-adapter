/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.component.file.cluster;

import org.zenithblox.cluster.ZwangineClusterMember;
import org.zenithblox.support.cluster.AbstractZwangineClusterView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FileLockClusterView extends AbstractZwangineClusterView {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLockClusterView.class);

    private static final Lock LOCK = new ReentrantLock();
    private final ClusterMember localMember;
    private final Path path;
    private RandomAccessFile lockFile;
    private FileLock lock;
    private ScheduledFuture<?> task;

    FileLockClusterView(FileLockClusterService cluster, String namespace) {
        super(cluster, namespace);

        this.localMember = new ClusterMember();
        this.path = Paths.get(cluster.getRoot(), namespace);

    }

    @Override
    public Optional<ZwangineClusterMember> getLeader() {
        return this.localMember.isLeader() ? Optional.of(this.localMember) : Optional.empty();
    }

    @Override
    public ZwangineClusterMember getLocalMember() {
        return this.localMember;
    }

    @Override
    public List<ZwangineClusterMember> getMembers() {
        // It may be useful to lock only a region of the file an then have views
        // appending their id to the file on different regions so we can
        // have a list of members. Root/Header region that is used for locking
        // purpose may also contains the lock holder.
        return Collections.emptyList();
    }

    @Override
    protected void doStart() throws Exception {
        if (lockFile != null) {
            closeInternal();

            fireLeadershipChangedEvent((ZwangineClusterMember) null);
        }

        if (!Files.exists(path.getParent())) {
            Files.createDirectories(path.getParent());
        }

        FileLockClusterService service = getClusterService().unwrap(FileLockClusterService.class);
        ScheduledExecutorService executor = service.getExecutor();

        task = executor.scheduleAtFixedRate(this::tryLock,
                TimeUnit.MILLISECONDS.convert(service.getAcquireLockDelay(), service.getAcquireLockDelayUnit()),
                TimeUnit.MILLISECONDS.convert(service.getAcquireLockInterval(), service.getAcquireLockIntervalUnit()),
                TimeUnit.MILLISECONDS);
    }

    @Override
    protected void doStop() throws Exception {
        closeInternal();
    }

    // *********************************
    //
    // *********************************

    private void closeInternal() throws Exception {
        if (task != null) {
            task.cancel(true);
        }

        if (lock != null) {
            lock.release();
        }

        closeLockFile();
    }

    private void closeLockFile() {
        if (lockFile != null) {
            try {
                lockFile.close();
            } catch (Exception ignore) {
                // Ignore
            }
            lockFile = null;
        }
    }

    private void tryLock() {
        if (isStarting() || isStarted()) {
            Exception reason = null;

            try {
                if (localMember.isLeader()) {
                    LOGGER.trace("Holding the lock on file {} (lock={})", path, lock);
                    return;
                }

                LOCK.lock();
                try {
                    if (lock != null) {
                        LOGGER.info("Lock on file {} lost (lock={})", path, lock);
                        fireLeadershipChangedEvent((ZwangineClusterMember) null);
                    }

                    LOGGER.debug("Try to acquire a lock on {}", path);
                    lockFile = new RandomAccessFile(path.toFile(), "rw");

                    lock = null;
                    lock = lockFile.getChannel().tryLock(0, Math.max(1, lockFile.getChannel().size()), false);

                    if (lock != null) {
                        LOGGER.info("Lock on file {} acquired (lock={})", path, lock);
                        fireLeadershipChangedEvent(localMember);
                    } else {
                        LOGGER.debug("Lock on file {} not acquired ", path);
                    }
                } finally {
                    LOCK.unlock();
                }
            } catch (OverlappingFileLockException e) {
                reason = new IOException(e);
            } catch (Exception e) {
                reason = e;
            }

            if (lock == null) {
                LOGGER.debug("Lock on file {} not acquired ", path, reason);
                closeLockFile();
            }
        }
    }

    private final class ClusterMember implements ZwangineClusterMember {
        @Override
        public boolean isLeader() {
            LOCK.lock();
            try {
                return lock != null && lock.isValid();
            } finally {
                LOCK.unlock();
            }
        }

        @Override
        public boolean isLocal() {
            return true;
        }

        @Override
        public String getId() {
            return getClusterService().getId();
        }
    }
}
