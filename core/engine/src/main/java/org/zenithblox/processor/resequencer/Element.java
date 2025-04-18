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
package org.zenithblox.processor.resequencer;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A container for objects to be resequenced. This container can be scheduled for timing out. Non-scheduled objects or
 * already timed-out objects are ready for being released by the {@link ResequencerEngine}.
 */
class Element<E> implements TimeoutHandler {

    private final Lock lock = new ReentrantLock();
    /**
     * The contained object.
     */
    private final E object;

    /**
     * Not <code>null</code> if this element is currently beeing scheduled for timing out.
     */
    private Timeout timeout;

    /**
     * Creates a new container instance.
     *
     * @param object contained object.
     */
    Element(E object) {
        this.object = object;
    }

    /**
     * Returns the contained object.
     *
     * @return the contained object.
     */
    public E getObject() {
        return object;
    }

    /**
     * Returns <code>true</code> if this element is currently scheduled for timing out.
     *
     * @return <code>true</code> if scheduled or <code>false</code> if not scheduled or already timed-out.
     */
    public boolean scheduled() {
        lock.lock();
        try {
            return timeout != null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Schedules the given timeout task. Before this methods calls the {@link Timeout#schedule()} method it sets this
     * element as timeout listener.
     *
     * @param t a timeout task.
     */
    public void schedule(Timeout t) {
        lock.lock();
        try {
            this.timeout = t;
            this.timeout.setTimeoutHandler(this);
            this.timeout.schedule();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancels the scheduled timeout for this element. If this element is not scheduled or has already timed-out this
     * method has no effect.
     */
    public void cancel() {
        lock.lock();
        try {
            if (timeout != null) {
                timeout.cancel();
            }
            timeout(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Marks this element as timed-out.
     *
     * @param t timeout task that caused the notification.
     */
    @Override
    public void timeout(Timeout t) {
        lock.lock();
        try {
            this.timeout = null;
        } finally {
            lock.unlock();
        }
    }

}
