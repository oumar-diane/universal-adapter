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
package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * A {@link ZwangineContext} creation and destruction tracker.
 */
public class ZwangineContextTracker implements Closeable {

    private static final Logger LOG = LoggerFactory.getLogger(ZwangineContextTracker.class);

    private static final List<ZwangineContextTracker> TRACKERS = new CopyOnWriteArrayList<>();

    private static final Lock LOCK = new ReentrantLock();

    @FunctionalInterface
    public interface Filter extends Predicate<ZwangineContext> {

        boolean accept(ZwangineContext zwangineContext);

        @Override
        default boolean test(ZwangineContext zwangineContext) {
            return accept(zwangineContext);
        }
    }

    private final Filter filter;

    public ZwangineContextTracker() {
        filter = zwangineContext -> !zwangineContext.getClass().getName().contains("Proxy");
    }

    public ZwangineContextTracker(Filter filter) {
        this.filter = filter;
    }

    /**
     * Called to determine whether this tracker should accept the given context.
     */
    public boolean accept(ZwangineContext zwangineContext) {
        return filter == null || filter.accept(zwangineContext);
    }

    /**
     * Called when a context is created.
     */
    public void contextCreated(ZwangineContext zwangineContext) {
        // do nothing
    }

    /**
     * Called when a context has been shutdown.
     */
    public void contextDestroyed(ZwangineContext zwangineContext) {
        // do nothing
    }

    /**
     * Opens the tracker to start tracking when new {@link ZwangineContext} is created or destroyed.
     */
    public final void open() {
        TRACKERS.add(this);
    }

    /**
     * Closes the tracker so it not longer tracks.
     */
    @Override
    public final void close() {
        TRACKERS.remove(this);
    }

    public static void notifyContextCreated(ZwangineContext zwangineContext) {
        LOCK.lock();
        try {
            for (ZwangineContextTracker tracker : TRACKERS) {
                try {
                    if (tracker.accept(zwangineContext)) {
                        tracker.contextCreated(zwangineContext);
                    }
                } catch (Exception e) {
                    LOG.warn("Error calling ZwangineContext tracker. This exception is ignored.", e);
                }
            }
        } finally {
            LOCK.unlock();
        }
    }

    public static void notifyContextDestroyed(ZwangineContext zwangineContext) {
        LOCK.lock();
        try {
            for (ZwangineContextTracker tracker : TRACKERS) {
                try {
                    if (tracker.accept(zwangineContext)) {
                        tracker.contextDestroyed(zwangineContext);
                    }
                } catch (Exception e) {
                    LOG.warn("Error calling ZwangineContext tracker. This exception is ignored.", e);
                }
            }
        } finally {
            LOCK.unlock();
        }
    }
}
