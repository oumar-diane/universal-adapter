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

import org.zenithblox.util.concurrent.ThreadHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

/**
 * Resequences elements based on a given {@link SequenceElementComparator}. This resequencer is designed for
 * resequencing element streams. Stream-based resequencing has the advantage that the number of elements to be
 * resequenced need not be known in advance. Resequenced elements are delivered via a {@link SequenceSender}.
 * <p>
 * The resequencer's behaviour for a given comparator is controlled by the <code>timeout</code> property. This is the
 * timeout (in milliseconds) for a given element managed by this resequencer. An out-of-sequence element can only be
 * marked as <i>ready-for-delivery</i> if it either times out or if it has an immediate predecessor (in that case it is
 * in-sequence). If an immediate predecessor of a waiting element arrives the timeout task for the waiting element will
 * be cancelled (which marks it as <i>ready-for-delivery</i>).
 * <p>
 * If the maximum out-of-sequence time difference between elements within a stream is known, the <code>timeout</code>
 * value should be set to this value. In this case it is guaranteed that all elements of a stream will be delivered in
 * sequence via the {@link SequenceSender}. The lower the <code>timeout</code> value is compared to the out-of-sequence
 * time difference between elements within a stream the higher the probability is for out-of-sequence elements delivered
 * by this resequencer. Delivery of elements must be explicitly triggered by applications using the {@link #deliver()}
 * or {@link #deliverNext()} methods. Only elements that are <i>ready-for-delivery</i> are delivered by these methods.
 * The longer an application waits to trigger a delivery the more elements may become <i>ready-for-delivery</i>.
 * <p>
 * The resequencer remembers the last-delivered element. If an element arrives which is the immediate successor of the
 * last-delivered element it is <i>ready-for-delivery</i> immediately. After delivery the last-delivered element is
 * adjusted accordingly. If the last-delivered element is <code>null</code> i.e. the resequencer was newly created the
 * first arriving element needs <code>timeout</code> milliseconds in any case for becoming <i>ready-for-delivery</i>.
 * <p>
 */
public class ResequencerEngine<E> {

    /**
     * The element that most recently hash been delivered or <code>null</code> if no element has been delivered yet.
     */
    private Element<E> lastDelivered;

    /**
     * Minimum amount of time to wait for out-of-sequence elements.
     */
    private long timeout;

    /**
     * A sequence of elements for sorting purposes.
     */
    private final Sequence<Element<E>> sequence;

    /**
     * A timer for scheduling timeout notifications.
     */
    private Timer timer;

    /**
     * A strategy for sending sequence elements.
     */
    private SequenceSender<E> sequenceSender;

    /**
     * Indicates whether an error should be thrown if message older (based on Comparator) than the last delivered
     * message is received.
     */
    private Boolean rejectOld;

    /**
     * List containing wait conditions to be evaluated whenever the sequence is modified. Access to this field should be
     * done inside a lock block.
     */
    private final Map<CountDownLatch, Predicate<Sequence<?>>> waitConditions = new HashMap<>();

    private final Lock lock = new ReentrantLock();

    /**
     * Creates a new resequencer instance with a default timeout of 2000 milliseconds.
     *
     * @param comparator a sequence element comparator.
     */
    public ResequencerEngine(SequenceElementComparator<E> comparator) {
        this.sequence = createSequence(comparator);
        this.timeout = 2000L;
        this.lastDelivered = null;
    }

    public void start() {
        timer = new Timer(
                ThreadHelper.resolveThreadName("Zwangine Thread ${counter} - ${name}", "Stream Resequencer Timer"), true);
    }

    /**
     * Stops this resequencer (i.e. this resequencer's {@link Timer} instance).
     */
    public void stop() {
        timer.cancel();
    }

    /**
     * Returns the number of elements currently maintained by this resequencer.
     *
     * @return the number of elements currently maintained by this resequencer.
     */
    public int size() {
        lock.lock();
        try {
            return sequence.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Wait for the following condition to happen. Do not call this method while holding a lock on the resequencer
     * engine, as it will deadlock. The predicate will be evaluated while holding a lock on the resequencer engine.
     *
     * @param  pred                 the condition to wait for
     * @throws InterruptedException if the thread is interrupted
     */
    public void waitUntil(Predicate<Sequence<?>> pred) throws InterruptedException {
        CountDownLatch latch;
        lock.lock();
        try {
            if (pred.test(sequence)) {
                return;
            }
            latch = new CountDownLatch(1);
            waitConditions.put(latch, pred);
        } finally {
            lock.unlock();
        }
        latch.await();
    }

    private void evaluateConditions() {
        lock.lock();
        try {
            for (var it = waitConditions.entrySet().iterator(); it.hasNext();) {
                Map.Entry<CountDownLatch, Predicate<Sequence<?>>> e = it.next();
                if (e.getValue().test(sequence)) {
                    e.getKey().countDown();
                    it.remove();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns this resequencer's timeout value.
     *
     * @return the timeout in milliseconds.
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Sets this sequencer's timeout value.
     *
     * @param timeout the timeout in milliseconds.
     */
    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Boolean getRejectOld() {
        return rejectOld;
    }

    public void setRejectOld(Boolean rejectOld) {
        this.rejectOld = rejectOld;
    }

    /**
     * Returns the sequence sender.
     *
     * @return the sequence sender.
     */
    public SequenceSender<E> getSequenceSender() {
        return sequenceSender;
    }

    /**
     * Sets the sequence sender.
     *
     * @param sequenceSender a sequence element sender.
     */
    public void setSequenceSender(SequenceSender<E> sequenceSender) {
        this.sequenceSender = sequenceSender;
    }

    /**
     * Returns the last delivered element.
     *
     * @return the last delivered element or <code>null</code> if no delivery has been made yet.
     */
    E getLastDelivered() {
        if (lastDelivered == null) {
            return null;
        }
        return lastDelivered.getObject();
    }

    /**
     * Sets the last delivered element. This is for testing purposes only.
     *
     * @param o an element.
     */
    void setLastDelivered(E o) {
        lastDelivered = new Element<>(o);
    }

    /**
     * Inserts the given element into this resequencer. If the element is not ready for immediate delivery and has no
     * immediate presecessor then it is scheduled for timing out. After being timed out it is ready for delivery.
     *
     * @param  o                        an element.
     * @throws IllegalArgumentException if the element cannot be used with this resequencer engine
     */
    public void insert(E o) {
        lock.lock();
        try {
            // wrap object into internal element
            Element<E> element = new Element<>(o);

            // validate the exchange has no problem
            if (!sequence.seqComparator().isValid(element)) {
                throw new IllegalArgumentException("Element cannot be used in comparator: " + sequence.seqComparator());
            }

            // validate the exchange shouldn't be 'rejected' (if applicable)
            if (rejectOld != null && rejectOld && beforeLastDelivered(element)) {
                throw new MessageRejectedException(
                        "rejecting message [" + element.getObject()
                                                   + "], it should have been sent before the last delivered message ["
                                                   + lastDelivered.getObject() + "]");
            }

            // add element to sequence in proper order
            sequence.add(element);

            Element<E> successor = sequence.successor(element);

            // check if there is an immediate successor and cancel
            // timer task (no need to wait any more for timeout)
            if (successor != null) {
                successor.cancel();
            }

            // start delivery if current element is successor of last delivered element
            if (!successorOfLastDelivered(element) && sequence.predecessor(element) == null) {
                element.schedule(defineTimeout());
            }

            // evaluate wait conditions
            evaluateConditions();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Delivers all elements which are currently ready to deliver.
     *
     * @throws Exception thrown by {@link SequenceSender#sendElement(Object)}.
     *
     * @see              ResequencerEngine#deliverNext()
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void deliver() throws Exception {
        lock.lock();
        try {
            while (deliverNext()) {
                // do nothing here
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Attempts to deliver a single element from the head of the resequencer queue (sequence). Only elements which have
     * not been scheduled for timing out or which already timed out can be delivered. Elements are delivered via
     * {@link SequenceSender#sendElement(Object)}.
     *
     * @return           <code>true</code> if the element has been delivered <code>false</code> otherwise.
     *
     * @throws Exception thrown by {@link SequenceSender#sendElement(Object)}.
     *
     */
    public boolean deliverNext() throws Exception {
        lock.lock();
        try {
            if (sequence.isEmpty()) {
                return false;
            }
            // inspect element with the lowest sequence value
            Element<E> element = sequence.first();

            // if element is scheduled do not deliver and return
            if (element.scheduled()) {
                return false;
            }

            // remove deliverable element from sequence
            sequence.remove(element);

            // set the delivered element to last delivered element
            lastDelivered = element;

            // deliver the sequence element
            sequenceSender.sendElement(element.getObject());

            // evaluate wait conditions
            evaluateConditions();

            // element has been delivered
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns <code>true</code> if the given element is the immediate successor of the last delivered element.
     *
     * @param  element an element.
     * @return         <code>true</code> if the given element is the immediate successor of the last delivered element.
     */
    private boolean successorOfLastDelivered(Element<E> element) {
        if (lastDelivered == null) {
            return false;
        }
        if (sequence.seqComparator().successor(element, lastDelivered)) {
            return true;
        }
        return false;
    }

    /**
     * Retuns <code>true</code> if the given element is before the last delivered element.
     *
     * @param  element an element.
     * @return         <code>true</code> if the given element is before the last delivered element.
     */
    private boolean beforeLastDelivered(Element<E> element) {
        if (lastDelivered == null) {
            return false;
        }
        if (sequence.seqComparator().compare(element, lastDelivered) < 0) {
            return true;
        }
        return false;
    }

    /**
     * Creates a timeout task based on the timeout setting of this resequencer.
     *
     * @return a new timeout task.
     */
    private Timeout defineTimeout() {
        return new Timeout(timer, timeout);
    }

    private static <E> Sequence<Element<E>> createSequence(SequenceElementComparator<E> comparator) {
        return new Sequence<>(new ElementComparator<>(comparator));
    }

}
