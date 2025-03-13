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
package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.processor.errorhandler.ErrorHandlerSupport;
import org.zenithblox.spi.*;
import org.zenithblox.support.*;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.CastUtils;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.concurrent.AsyncCompletionService;
import org.zenithblox.util.concurrent.Rejectable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.Closeable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.zenithblox.util.ObjectHelper.notNull;

/**
 * Implements the Multicast pattern to send a message exchange to a number of endpoints, each endpoint receiving a copy
 * of the message exchange.
 */
public class MulticastProcessor extends AsyncProcessorSupport
        implements Navigate<Processor>, Traceable, IdAware, WorkflowIdAware, ErrorHandlerAware {

    private static final Logger LOG = LoggerFactory.getLogger(MulticastProcessor.class);

    /**
     * Class that represent each step in the multicast workflow to do
     */
    static final class DefaultProcessorExchangePair implements ProcessorExchangePair {
        private final int index;
        private final Processor processor;
        private final Processor prepared;
        private final Exchange exchange;

        private DefaultProcessorExchangePair(int index, Processor processor, Processor prepared, Exchange exchange) {
            this.index = index;
            this.processor = processor;
            this.prepared = prepared;
            this.exchange = exchange;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public Exchange getExchange() {
            return exchange;
        }

        @Override
        public Producer getProducer() {
            if (processor instanceof Producer producer) {
                return producer;
            }
            return null;
        }

        @Override
        public Processor getProcessor() {
            return prepared;
        }

        @Override
        public void begin() {
            // noop
        }

        @Override
        public void done() {
            // noop
        }

    }

    private final class Scheduler implements Executor {

        @Override
        public void execute(Runnable command) {
            schedule(command, false);
        }
    }

    private final class SyncScheduler implements Executor {

        @Override
        public void execute(Runnable command) {
            schedule(command, true);
        }
    }

    protected final Processor onPrepare;
    protected final ProcessorExchangeFactory processorExchangeFactory;
    private final Lock lock = new ReentrantLock();
    private final AsyncProcessorAwaitManager awaitManager;
    private final ZwangineContext zwangineContext;
    private final InternalProcessorFactory internalProcessorFactory;
    private final Workflow workflow;
    private final ReactiveExecutor reactiveExecutor;
    private Processor errorHandler;
    private String id;
    private String workflowId;
    private final Collection<Processor> processors;
    private final AggregationStrategy aggregationStrategy;
    private final boolean parallelProcessing;
    private boolean synchronous;
    private final boolean streaming;
    private final boolean parallelAggregate;
    private final boolean stopOnException;
    private final ExecutorService executorService;
    private final boolean shutdownExecutorService;
    private final Scheduler scheduler = new Scheduler();
    private final SyncScheduler syncScheduler = new SyncScheduler();
    private ExecutorService aggregateExecutorService;
    private boolean shutdownAggregateExecutorService;
    private final long timeout;
    private final int cacheSize;
    private final Map<Processor, Processor> errorHandlers;
    private final boolean shareUnitOfWork;

    public MulticastProcessor(ZwangineContext zwangineContext, Workflow workflow, Collection<Processor> processors) {
        this(zwangineContext, workflow, processors, null);
    }

    public MulticastProcessor(ZwangineContext zwangineContext, Workflow workflow, Collection<Processor> processors,
                              AggregationStrategy aggregationStrategy) {
        this(zwangineContext, workflow, processors, aggregationStrategy, false, null,
             false, false, false, 0, null,
             false, false, 0);
    }

    public MulticastProcessor(ZwangineContext zwangineContext, Workflow workflow, Collection<Processor> processors,
                              AggregationStrategy aggregationStrategy,
                              boolean parallelProcessing, ExecutorService executorService, boolean shutdownExecutorService,
                              boolean streaming,
                              boolean stopOnException, long timeout, Processor onPrepare, boolean shareUnitOfWork,
                              boolean parallelAggregate, int cacheSize) {
        notNull(zwangineContext, "zwangineContext");
        this.zwangineContext = zwangineContext;
        this.internalProcessorFactory = PluginHelper.getInternalProcessorFactory(zwangineContext);
        this.awaitManager = PluginHelper.getAsyncProcessorAwaitManager(zwangineContext);
        this.workflow = workflow;
        this.reactiveExecutor = zwangineContext.getZwangineContextExtension().getReactiveExecutor();
        this.processors = processors;
        this.aggregationStrategy = aggregationStrategy;
        this.executorService = executorService;
        this.shutdownExecutorService = shutdownExecutorService;
        this.streaming = streaming;
        this.stopOnException = stopOnException;
        // must enable parallel if executor service is provided
        this.parallelProcessing = parallelProcessing || executorService != null;
        this.timeout = timeout;
        this.onPrepare = onPrepare;
        this.shareUnitOfWork = shareUnitOfWork;
        this.parallelAggregate = parallelAggregate;
        this.processorExchangeFactory = zwangineContext.getZwangineContextExtension()
                .getProcessorExchangeFactory().newProcessorExchangeFactory(this);
        this.cacheSize = cacheSize == 0 ? ZwangineContextHelper.getMaximumCachePoolSize(zwangineContext) : cacheSize;
        if (this.cacheSize > 0) {
            this.errorHandlers = LRUCacheFactory.newLRUCache(this.cacheSize);
        } else {
            // no cache
            this.errorHandlers = null;
        }
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public void setErrorHandler(Processor errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public Processor getErrorHandler() {
        return errorHandler;
    }

    @Override
    public String getTraceLabel() {
        return "multicast";
    }

    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    @Override
    protected void doBuild() throws Exception {
        if (processorExchangeFactory != null) {
            processorExchangeFactory.setId(id);
            processorExchangeFactory.setWorkflowId(workflowId);
        }
        ServiceHelper.buildService(processorExchangeFactory);
    }

    @Override
    protected void doInit() throws Exception {
        if (workflow != null) {
            Exchange exchange = new DefaultExchange(getZwangineContext());
            for (Processor processor : getProcessors()) {
                wrapInErrorHandler(workflow, exchange, processor);
            }
        }

        ServiceHelper.initService(processorExchangeFactory);
    }

    @Override
    public boolean process(Exchange exchange, AsyncCallback callback) {
        if (synchronous) {
            try {
                // force synchronous processing using await manager
                awaitManager.process(new AsyncProcessorSupport() {
                    @Override
                    public boolean process(Exchange exchange, AsyncCallback callback) {
                        // must invoke doProcess directly here to avoid calling recursive
                        return doProcess(exchange, callback);
                    }
                }, exchange);
            } catch (Exception e) {
                exchange.setException(e);
            } finally {
                callback.done(true);
            }
            return true;
        } else {
            return doProcess(exchange, callback);
        }
    }

    protected boolean doProcess(Exchange exchange, AsyncCallback callback) {
        Iterable<ProcessorExchangePair> pairs;
        int size = 0;
        try {
            pairs = createProcessorExchangePairs(exchange);
            if (pairs instanceof Collection) {
                pairs = ((Collection<ProcessorExchangePair>) pairs)
                        .stream().filter(Objects::nonNull).toList();
                size = ((Collection<ProcessorExchangePair>) pairs).size();
            }
        } catch (Exception e) {
            exchange.setException(e);
            // unexpected exception was thrown, maybe from iterator etc. so do not regard as exhausted
            // and do the done work
            doDone(exchange, null, null, callback, true, false);
            return true;
        }

        // we need to run in either transacted or reactive mode because the threading model is different
        // when we run in transacted mode, then we synchronous processing on the current thread
        // this can lead to a long execution which can lead to deep stackframes, and therefore we
        // must handle this specially in a while loop structure to ensure the strackframe does not grow deeper
        // the reactive mode will execute each sub task in its own runnable task which is scheduled on the reactive executor
        // which is how the routing engine normally operates
        // if we have parallel processing enabled then we cannot run in transacted mode (requires synchronous processing via same thread)
        MulticastTask state = !isParallelProcessing() && exchange.isTransacted()
                ? new MulticastTransactedTask(exchange, pairs, callback, size)
                : new MulticastReactiveTask(exchange, pairs, callback, size);
        if (isParallelProcessing()) {
            try {
                executorService.submit(() -> reactiveExecutor.scheduleSync(state));
            } catch (RejectedExecutionException e) {
                state.reject();
            }
        } else {
            if (exchange.isTransacted()) {
                reactiveExecutor.scheduleQueue(state);
            } else {
                reactiveExecutor.scheduleMain(state);
            }
        }

        // the remainder of the multicast will be completed async
        // so we break out now, then the callback will be invoked which then
        // continue routing from where we left here
        return false;
    }

    protected void schedule(final Runnable runnable) {
        schedule(runnable, false);
    }

    protected void schedule(final Runnable runnable, boolean sync) {
        if (isParallelProcessing()) {
            Runnable task = prepareParallelTask(runnable);
            try {
                executorService.submit(() -> reactiveExecutor.scheduleSync(task));
            } catch (RejectedExecutionException e) {
                if (runnable instanceof Rejectable rej) {
                    rej.reject();
                }
            }
        } else if (sync) {
            reactiveExecutor.scheduleSync(runnable);
        } else {
            reactiveExecutor.schedule(runnable);
        }
    }

    private Runnable prepareParallelTask(Runnable runnable) {
        Runnable answer = runnable;

        // if MDC is enabled we need to propagate the information
        // to the sub task which is executed on another thread from the thread pool
        if (zwangineContext.isUseMDCLogging()) {
            String pattern = zwangineContext.getMDCLoggingKeysPattern();
            Map<String, String> mdc = MDC.getCopyOfContextMap();
            if (mdc != null && !mdc.isEmpty()) {
                answer = () -> {
                    try {
                        if (pattern == null || "*".equals(pattern)) {
                            mdc.forEach(MDC::put);
                        } else {
                            final String[] patterns = pattern.split(",");
                            mdc.forEach((k, v) -> {
                                if (PatternHelper.matchPatterns(k, patterns)) {
                                    MDC.put(k, v);
                                }
                            });
                        }
                    } finally {
                        runnable.run();
                    }
                };
            }
        }

        return answer;
    }

    protected abstract class MulticastTask implements Runnable, Rejectable {

        final Exchange original;
        final Iterable<ProcessorExchangePair> pairs;
        final AsyncCallback callback;
        final Iterator<ProcessorExchangePair> iterator;
        final ReentrantLock lock = new ReentrantLock();
        final AsyncCompletionService<Exchange> completion;
        final AtomicReference<Exchange> result = new AtomicReference<>();
        final AtomicInteger nbExchangeSent = new AtomicInteger();
        final AtomicInteger nbAggregated = new AtomicInteger();
        final AtomicBoolean allSent = new AtomicBoolean();
        final AtomicBoolean done = new AtomicBoolean();
        final Map<String, String> mdc;
        final ScheduledFuture<?> timeoutTask;

        MulticastTask(Exchange original, Iterable<ProcessorExchangePair> pairs, AsyncCallback callback, int capacity,
                      boolean sync) {
            this.original = original;
            this.pairs = pairs;
            this.callback = callback;
            this.iterator = pairs.iterator();
            if (timeout > 0) {
                timeoutTask = schedule(aggregateExecutorService, this::timeout, timeout, TimeUnit.MILLISECONDS);
            } else {
                timeoutTask = null;
            }
            // if MDC is enabled we must make a copy in this constructor when the task
            // is created by the caller thread, and then propagate back when run is called
            // which can happen from another thread
            if (isParallelProcessing() && original.getContext().isUseMDCLogging()) {
                this.mdc = MDC.getCopyOfContextMap();
            } else {
                this.mdc = null;
            }
            if (capacity > 0) {
                this.completion
                        = new AsyncCompletionService<>(sync ? syncScheduler : scheduler, !isStreaming(), lock, capacity);
            } else {
                this.completion = new AsyncCompletionService<>(sync ? syncScheduler : scheduler, !isStreaming(), lock);
            }
        }

        @Override
        public String toString() {
            return "MulticastTask";
        }

        @Override
        public void run() {
            if (this.mdc != null) {
                this.mdc.forEach(MDC::put);
            }
        }

        protected void aggregate() {
            Lock lock = this.lock;
            if (lock.tryLock()) {
                try {
                    Exchange exchange;
                    while (!done.get() && (exchange = completion.poll()) != null) {
                        doAggregate(result, exchange, original);
                        if (nbAggregated.incrementAndGet() >= nbExchangeSent.get() && allSent.get()) {
                            doDone(result.get(), true);
                        }
                    }
                } catch (Exception e) {
                    original.setException(e);
                    // and do the done work
                    doDone(null, false);
                } finally {
                    lock.unlock();
                }
            }
        }

        protected void timeout() {
            Lock lock = this.lock;
            if (lock.tryLock()) {
                try {
                    while (nbAggregated.get() < nbExchangeSent.get()) {
                        Exchange exchange = completion.pollUnordered();
                        int index = exchange != null ? getExchangeIndex(exchange) : nbExchangeSent.get();
                        while (nbAggregated.get() < index) {
                            int idx = nbAggregated.getAndIncrement();
                            AggregationStrategy strategy = getAggregationStrategy(null);
                            if (strategy != null) {
                                strategy.timeout(result.get() != null ? result.get() : original,
                                        idx, nbExchangeSent.get(), timeout);
                            }
                        }
                        if (exchange != null) {
                            doAggregate(result, exchange, original);
                            nbAggregated.incrementAndGet();
                        }
                    }
                    doTimeoutDone(result.get(), true);
                } catch (Exception e) {
                    original.setException(e);
                    // and do the done work
                    doTimeoutDone(null, false);
                } finally {
                    lock.unlock();
                }
            }
        }

        protected void doTimeoutDone(Exchange exchange, boolean forceExhaust) {
            if (done.compareAndSet(false, true)) {
                MulticastProcessor.this.doDone(original, exchange, pairs, callback, false, forceExhaust);
            }
        }

        protected void doDone(Exchange exchange, boolean forceExhaust) {
            if (done.compareAndSet(false, true)) {
                // cancel timeout if we are done normally (we cannot cancel if called via onTimeout)
                if (timeoutTask != null) {
                    try {
                        timeoutTask.cancel(true);
                    } catch (Exception e) {
                        // ignore
                        LOG.debug("Cancel timeout task caused an exception. This exception is ignored.", e);
                    }
                }
                MulticastProcessor.this.doDone(original, exchange, pairs, callback, false, forceExhaust);
            }
        }

        @Override
        public void reject() {
            original.setException(new RejectedExecutionException("Task rejected executing from ExecutorService"));
            // and do the done work
            doDone(null, false);
        }
    }

    /**
     * Sub task processed reactive via the {@link ReactiveExecutor}.
     */
    protected class MulticastReactiveTask extends MulticastTask {

        public MulticastReactiveTask(Exchange original, Iterable<ProcessorExchangePair> pairs, AsyncCallback callback,
                                     int size) {
            super(original, pairs, callback, size, false);
        }

        @Override
        public void run() {
            super.run();

            try {
                if (done.get()) {
                    return;
                }

                // Get next processor exchange pair to sent, skipping null ones
                ProcessorExchangePair pair = getNextProcessorExchangePair();
                if (pair == null) {
                    doDone(result.get(), true);
                    return;
                }

                boolean hasNext = iterator.hasNext();

                Exchange exchange = pair.getExchange();
                int index = nbExchangeSent.getAndIncrement();
                updateNewExchange(exchange, index, pairs, hasNext);
                if (!hasNext) {
                    allSent.set(true);
                }

                completion.submit(exchangeResult -> {
                    // compute time taken if sending to another endpoint
                    StopWatch watch = beforeSend(pair);

                    AsyncProcessor async = AsyncProcessorConverterHelper.convert(pair.getProcessor());
                    async.process(exchange, doneSync -> {
                        afterSend(pair, watch);

                        // Decide whether to continue with the multicast or not; similar logic to the Workflow
                        // remember to test for stop on exception and aggregate before copying back results
                        String msg = null;
                        if (LOG.isDebugEnabled()) {
                            msg = "Multicast processing failed for number " + index;
                        }
                        boolean continueProcessing = WorkflowHelper.continueProcessing(exchange, msg, LOG);
                        if (stopOnException && !continueProcessing) {
                            if (exchange.getException() != null) {
                                // wrap in exception to explain where it failed
                                exchange.setException(new ZwangineExchangeException(
                                        "Multicast processing failed for number " + index, exchange, exchange.getException()));
                            } else {
                                // we want to stop on exception, and the exception was handled by the error handler
                                // this is similar to what the workflow does, so we should do the same to not surprise end users
                                // so we should set the failed exchange as the result and be done
                                result.set(exchange);
                            }
                            // and do the done work
                            doDone(exchange, true);
                            return;
                        }

                        exchangeResult.accept(exchange);

                        // aggregate exchanges if any
                        aggregate();

                        // next step
                        if (hasNext && !isParallelProcessing()) {
                            schedule(this);
                        }
                    });
                });
                // after submitting this pair then move on to the next pair (if in parallel mode)
                if (hasNext && isParallelProcessing()) {
                    schedule(this);
                }
            } catch (Exception e) {
                original.setException(e);
                doDone(null, false);
            }
        }

        private ProcessorExchangePair getNextProcessorExchangePair() {
            ProcessorExchangePair tpair = null;
            while (tpair == null && iterator.hasNext()) {
                tpair = iterator.next();
            }
            return tpair;
        }
    }

    /**
     * Transacted sub task processed synchronously using {@link Processor#process(Exchange)} with the same thread in a
     * while loop control flow.
     */
    protected class MulticastTransactedTask extends MulticastTask {

        public MulticastTransactedTask(Exchange original, Iterable<ProcessorExchangePair> pairs, AsyncCallback callback,
                                       int size) {
            super(original, pairs, callback, size, true);
        }

        @Override
        public void run() {
            super.run();

            boolean next = true;
            while (next) {
                try {
                    next = doRun();
                } catch (Exception e) {
                    original.setException(e);
                    doDone(null, false);
                    return;
                }
            }
        }

        boolean doRun() throws Exception {
            if (done.get()) {
                return false;
            }

            // Check if the iterator is empty
            // This can happen the very first time we check the existence
            // of an item before queuing the run.
            // or some iterators may return true for hasNext() but then null in next()
            if (!iterator.hasNext()) {
                doDone(result.get(), true);
                return false;
            }

            ProcessorExchangePair pair = iterator.next();
            if (pair == null) {
                return true; // go again to check hasNext
            }

            boolean hasNext = iterator.hasNext();
            Exchange exchange = pair.getExchange();
            int index = nbExchangeSent.getAndIncrement();
            updateNewExchange(exchange, index, pairs, hasNext);
            if (!hasNext) {
                allSent.set(true);
            }

            // process next

            // compute time taken if sending to another endpoint
            StopWatch watch = beforeSend(pair);

            // use synchronous processing in transacted mode
            Processor sync = pair.getProcessor();
            try {
                sync.process(exchange);
            } catch (Exception e) {
                exchange.setException(e);
            } finally {
                afterSend(pair, watch);
            }

            // Decide whether to continue with the multicast or not; similar logic to the Workflow
            // remember to test for stop on exception and aggregate before copying back results
            String msg = null;
            if (LOG.isDebugEnabled()) {
                msg = "Multicast processing failed for number " + index;
            }
            boolean continueProcessing = WorkflowHelper.continueProcessing(exchange, msg, LOG);
            if (stopOnException && !continueProcessing) {
                if (exchange.getException() != null) {
                    // wrap in exception to explain where it failed
                    exchange.setException(new ZwangineExchangeException(
                            "Multicast processing failed for number " + index, exchange, exchange.getException()));
                } else {
                    // we want to stop on exception, and the exception was handled by the error handler
                    // this is similar to what the workflow does, so we should do the same to not surprise end users
                    // so we should set the failed exchange as the result and be done
                    result.set(exchange);
                }
                // and do the done work
                doDone(exchange, true);
                return false;
            }

            completion.submit(exchangeResult -> {
                // accept the exchange as a result
                exchangeResult.accept(exchange);

                // aggregate exchanges if any
                aggregate();
            });

            // after submitting this pair then move on to the next pair (if in parallel mode)
            if (hasNext && isParallelProcessing()) {
                schedule(this);
            }

            // next step
            boolean next = hasNext && !isParallelProcessing();
            LOG.trace("Run next: {}", next);
            return next;
        }
    }

    protected ScheduledFuture<?> schedule(Executor executor, Runnable runnable, long delay, TimeUnit unit) {
        if (executor instanceof ScheduledExecutorService scheduledExecutorService) {
            return scheduledExecutorService.schedule(runnable, delay, unit);
        } else {
            executor.execute(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                runnable.run();
            });
        }
        return null;
    }

    protected StopWatch beforeSend(ProcessorExchangePair pair) {
        StopWatch watch;
        final Exchange e = pair.getExchange();
        final Producer p = pair.getProducer();
        if (p != null) {
            boolean sending = EventHelper.notifyExchangeSending(e.getContext(), e, p.getEndpoint());
            if (sending) {
                watch = new StopWatch();
            } else {
                watch = null;
            }
        } else {
            watch = null;
        }

        // let the prepared process it, remember to begin the exchange pair
        pair.begin();

        // return the watch
        return watch;
    }

    protected void afterSend(ProcessorExchangePair pair, StopWatch watch) {
        // we are done with the exchange pair
        pair.done();

        // okay we are done, so notify the exchange was sent
        final Producer producer = pair.getProducer();
        if (producer != null && watch != null) {
            long timeTaken = watch.taken();
            final Exchange e = pair.getExchange();
            Endpoint endpoint = producer.getEndpoint();
            // emit event that the exchange was sent to the endpoint
            EventHelper.notifyExchangeSent(e.getContext(), e, endpoint, timeTaken);
        }
    }

    /**
     * Common work which must be done when we are done multicasting.
     * <p/>
     * This logic applies for both running synchronous and asynchronous as there are multiple exist points when using
     * the asynchronous routing engine. And therefore we want the logic in one method instead of being scattered.
     *
     * @param original     the original exchange
     * @param subExchange  the current sub exchange, can be <tt>null</tt> for the synchronous part
     * @param pairs        the pairs with the exchanges to process
     * @param callback     the callback
     * @param doneSync     the <tt>doneSync</tt> parameter to call on callback
     * @param forceExhaust whether error handling is exhausted
     */
    protected void doDone(
            Exchange original, Exchange subExchange, final Iterable<ProcessorExchangePair> pairs,
            AsyncCallback callback, boolean doneSync, boolean forceExhaust) {

        AggregationStrategy strategy = getAggregationStrategy(subExchange);
        // invoke the on completion callback
        if (strategy != null) {
            strategy.onCompletion(subExchange, original);
        }

        // cleanup any per exchange aggregation strategy
        removeAggregationStrategyFromExchange(original);

        // we need to know if there was an exception, and if the stopOnException option was enabled
        // also we would need to know if any error handler has attempted redelivery and exhausted
        boolean stoppedOnException = false;
        boolean exception = false;
        boolean exhaust = forceExhaust || subExchange != null
                && (subExchange.getException() != null || subExchange.getExchangeExtension().isRedeliveryExhausted());
        if (original.getException() != null || subExchange != null && subExchange.getException() != null) {
            // there was an exception and we stopped
            stoppedOnException = isStopOnException();
            exception = true;
        }

        // must copy results at this point
        if (subExchange != null) {
            if (stoppedOnException) {
                // if we stopped due an exception then only propagate the exception
                original.setException(subExchange.getException());
            } else {
                // copy the current result to original (preserve original correlation id),
                // so it will contain this result of this eip
                Object correlationId = original.removeProperty(ExchangePropertyKey.CORRELATION_ID);
                ExchangeHelper.copyResults(original, subExchange);
                if (correlationId != null) {
                    original.setProperty(ExchangePropertyKey.CORRELATION_ID, correlationId);
                }
            }
        }

        if (processorExchangeFactory != null && pairs != null) {
            // the exchanges on the pairs was created with a factory, so they should be released
            try {
                for (ProcessorExchangePair pair : pairs) {
                    processorExchangeFactory.release(pair.getExchange());
                }
            } catch (Exception e) {
                LOG.warn("Error releasing exchange due to {}. This exception is ignored.", e.getMessage(), e);
            }
        }
        // we are done so close the pairs iterator
        if (pairs instanceof Closeable closeable) {
            IOHelper.close(closeable, "pairs", LOG);
        }

        // .. and then if there was an exception we need to configure the redelivery exhaust
        // for example the noErrorHandler will not cause redelivery exhaust so if this error
        // handled has been in use, then the exhaust would be false (if not forced)
        if (exception) {
            // multicast uses error handling on its output processors and they have tried to redeliver
            // so we shall signal back to the other error handlers that we are exhausted and they should not
            // also try to redeliver as we would then do that twice
            original.getExchangeExtension().setRedeliveryExhausted(exhaust);
        }

        reactiveExecutor.schedule(callback);
    }

    /**
     * Aggregate the {@link Exchange} with the current result. This method is synchronized and is called directly when
     * parallelAggregate is disabled (by default).
     *
     * @param result        the current result
     * @param exchange      the exchange to be added to the result
     * @param inputExchange the input exchange that was sent as input to this EIP
     */
    protected void doAggregate(AtomicReference<Exchange> result, Exchange exchange, Exchange inputExchange) {
        if (parallelAggregate) {
            doAggregateInternal(getAggregationStrategy(exchange), result, exchange, inputExchange);
        } else {
            doAggregateSync(getAggregationStrategy(exchange), result, exchange, inputExchange);
        }
    }

    /**
     * Aggregate the {@link Exchange} with the current result. This method is synchronized and is called directly when
     * parallelAggregate is disabled (by default).
     *
     * @param strategy      the aggregation strategy to use
     * @param result        the current result
     * @param exchange      the exchange to be added to the result
     * @param inputExchange the input exchange that was sent as input to this EIP
     */
    private void doAggregateSync(
            AggregationStrategy strategy, AtomicReference<Exchange> result, Exchange exchange, Exchange inputExchange) {
        lock.lock();
        try {
            doAggregateInternal(strategy, result, exchange, inputExchange);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Aggregate the {@link Exchange} with the current result. This method is unsynchronized and is called directly when
     * parallelAggregate is enabled. In all other cases, this method is called from the doAggregate which is a
     * synchronized method
     *
     * @param strategy      the aggregation strategy to use
     * @param result        the current result
     * @param exchange      the exchange to be added to the result
     * @param inputExchange the input exchange that was sent as input to this EIP
     */
    private void doAggregateInternal(
            AggregationStrategy strategy, AtomicReference<Exchange> result, Exchange exchange, Exchange inputExchange) {
        if (strategy != null) {
            // prepare the exchanges for aggregation
            Exchange oldExchange = result.get();
            ExchangeHelper.prepareAggregation(oldExchange, exchange);
            result.set(strategy.aggregate(oldExchange, exchange, inputExchange));
        }
    }

    protected void updateNewExchange(Exchange exchange, int index, Iterable<ProcessorExchangePair> allPairs, boolean hasNext) {
        exchange.setProperty(ExchangePropertyKey.MULTICAST_INDEX, index);
        if (hasNext) {
            exchange.setProperty(ExchangePropertyKey.MULTICAST_COMPLETE, Boolean.FALSE);
        } else {
            exchange.setProperty(ExchangePropertyKey.MULTICAST_COMPLETE, Boolean.TRUE);
        }
    }

    protected Integer getExchangeIndex(Exchange exchange) {
        return exchange.getProperty(ExchangePropertyKey.MULTICAST_INDEX, Integer.class);
    }

    protected Iterable<ProcessorExchangePair> createProcessorExchangePairs(Exchange exchange)
            throws Exception {
        List<ProcessorExchangePair> result = new ArrayList<>(processors.size());
        Map<String, Object> txData = null;

        StreamCache streamCache = null;
        if (isParallelProcessing() && exchange.getIn().getBody() instanceof StreamCache streamCacheBody) {
            // in parallel processing case, the stream must be copied, therefore, get the stream
            streamCache = streamCacheBody;
        }

        int index = 0;
        for (Processor processor : processors) {
            // copy exchange, and do not share the unit of work
            Exchange copy = processorExchangeFactory.createCorrelatedCopy(exchange, false);
            copy.getExchangeExtension().setTransacted(exchange.isTransacted());
            // If we are in a transaction, set TRANSACTION_CONTEXT_DATA property for new exchanges to share txData
            // during the transaction.
            if (exchange.isTransacted() && copy.getProperty(Exchange.TRANSACTION_CONTEXT_DATA) == null) {
                if (txData == null) {
                    txData = new ConcurrentHashMap<>();
                }
                copy.setProperty(Exchange.TRANSACTION_CONTEXT_DATA, txData);
            }
            if (streamCache != null) {
                if (index > 0) {
                    // copy it otherwise parallel processing is not possible,
                    // because streams can only be read once
                    StreamCache copiedStreamCache = streamCache.copy(copy);
                    if (copiedStreamCache != null) {
                        copy.getIn().setBody(copiedStreamCache);
                    }
                }
            }

            // If the multi-cast processor has an aggregation strategy
            // then the StreamCache created by the child workflows must not be
            // closed by the unit of work of the child workflow, but by the unit of
            // work of the parent workflow or grand parent workflow or grand grand parent workflow ...(in case of nesting).
            // Set therefore the unit of work of the  parent workflow as stream cache unit of work,
            // if it is not already set.
            if (copy.getProperty(ExchangePropertyKey.STREAM_CACHE_UNIT_OF_WORK) == null) {
                copy.setProperty(ExchangePropertyKey.STREAM_CACHE_UNIT_OF_WORK, exchange.getUnitOfWork());
            }
            // if we share unit of work, we need to prepare the child exchange
            if (isShareUnitOfWork()) {
                prepareSharedUnitOfWork(copy, exchange);
            }

            // and add the pair
            Workflow workflow = ExchangeHelper.getWorkflow(exchange);
            result.add(createProcessorExchangePair(index++, processor, copy, workflow));
        }

        if (exchange.getException() != null) {
            // force any exceptions occurred during creation of exchange paris to be thrown
            // before returning the answer;
            throw exchange.getException();
        }

        return result;
    }

    /**
     * Creates the {@link ProcessorExchangePair} which holds the processor and exchange to be send out.
     * <p/>
     * You <b>must</b> use this method to create the instances of {@link ProcessorExchangePair} as they need to be
     * specially prepared before use.
     *
     * @param  index     the index
     * @param  processor the processor
     * @param  exchange  the exchange
     * @param  workflow     the workflow context
     * @return           prepared for use
     */
    protected ProcessorExchangePair createProcessorExchangePair(
            int index, Processor processor, Exchange exchange,
            Workflow workflow) {
        Processor prepared = processor;

        // set property which endpoint we send to
        setToEndpoint(exchange, prepared);

        // rework error handling to support fine grained error handling
        prepared = wrapInErrorHandler(workflow, exchange, prepared);

        // invoke on prepare on the exchange if specified
        if (onPrepare != null) {
            try {
                onPrepare.process(exchange);
            } catch (Exception e) {
                exchange.setException(e);
            }
        }
        return new DefaultProcessorExchangePair(index, processor, prepared, exchange);
    }

    @SuppressWarnings("unchecked")
    protected Processor wrapInErrorHandler(Workflow workflow, Exchange exchange, Processor processor) {
        Processor answer;
        Processor key = processor;

        if (workflow != this.workflow && this.workflow != null) {
            throw new UnsupportedOperationException("Is this really correct ?");
        }
        Boolean tryBlock = (Boolean) exchange.getProperty(ExchangePropertyKey.TRY_ROUTE_BLOCK);

        // do not wrap in error handler if we are inside a try block
        if (workflow != null && (tryBlock == null || !tryBlock)) {
            // wrap the producer in error handler so we have fine grained error handling on
            // the output side instead of the input side
            // this is needed to support redelivery on that output alone and not doing redelivery
            // for the entire multicast block again which will start from scratch again

            // lookup cached first to reuse and preserve memory
            answer = errorHandlers != null ? errorHandlers.get(key) : null;
            if (answer != null) {
                LOG.trace("Using existing error handler for: {}", key);
                return answer;
            }

            LOG.trace("Creating error handler for: {}", key);
            try {
                processor = wrapInErrorHandler(workflow, key);

                // and wrap in unit of work processor so the copy exchange also can run under UoW
                answer = createUnitOfWorkProcessor(workflow, processor, exchange);

                boolean child = exchange.getProperty(ExchangePropertyKey.PARENT_UNIT_OF_WORK, UnitOfWork.class) != null;

                // must start the error handler
                ServiceHelper.startService(answer);

                // here we don't cache the child unit of work
                if (!child && errorHandlers != null) {
                    errorHandlers.putIfAbsent(key, answer);
                }

            } catch (Exception e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        } else {
            // and wrap in unit of work processor so the copy exchange also can run under UoW
            answer = createUnitOfWorkProcessor(workflow, processor, exchange);
        }

        return answer;
    }

    private Processor wrapInErrorHandler(Workflow workflow, Processor processor) throws Exception {
        // use the error handler from multicast and clone it to use the new processor as its output
        if (errorHandler instanceof ErrorHandlerSupport errorHandlerSupport) {
            return errorHandlerSupport.clone(processor);
        }
        // fallback and use reifier to create the error handler
        return zwangineContext.getZwangineContextExtension().createErrorHandler(workflow, processor);
    }

    /**
     * Strategy to create the unit of work to be used for the sub workflow
     *
     * @param  processor the processor
     * @param  exchange  the exchange
     * @return           the unit of work processor
     */
    protected Processor createUnitOfWorkProcessor(Workflow workflow, Processor processor, Exchange exchange) {
        // and wrap it in a unit of work so the UoW is on the top, so the entire workflow will be in the same UoW
        UnitOfWork parent = exchange.getProperty(ExchangePropertyKey.PARENT_UNIT_OF_WORK, UnitOfWork.class);
        if (parent != null) {
            return internalProcessorFactory.addChildUnitOfWorkProcessorAdvice(zwangineContext, processor, workflow, parent);
        } else {
            return internalProcessorFactory.addUnitOfWorkProcessorAdvice(zwangineContext, processor, workflow);
        }
    }

    /**
     * Prepares the exchange for participating in a shared unit of work
     * <p/>
     * This ensures a child exchange can access its parent {@link UnitOfWork} when it participate in a shared unit of
     * work.
     *
     * @param childExchange  the child exchange
     * @param parentExchange the parent exchange
     */
    protected void prepareSharedUnitOfWork(Exchange childExchange, Exchange parentExchange) {
        childExchange.setProperty(ExchangePropertyKey.PARENT_UNIT_OF_WORK, parentExchange.getUnitOfWork());
    }

    @Override
    protected void doStart() throws Exception {
        if (isParallelProcessing() && executorService == null) {
            throw new IllegalArgumentException("ParallelProcessing is enabled but ExecutorService has not been set");
        }
        if (timeout > 0 && aggregateExecutorService == null) {
            // use unbounded thread pool so we ensure the aggregate on-the-fly task always will have assigned a thread
            // and run the tasks when the task is submitted. If not then the aggregate task may not be able to run
            // and signal completion during processing, which would lead to what would appear as a dead-lock or a slow processing
            String name = getClass().getSimpleName() + "-AggregateTask";
            aggregateExecutorService = createAggregateExecutorService(name);
            shutdownAggregateExecutorService = true;
        }
        ZwangineContextAware.trySetZwangineContext(aggregationStrategy, zwangineContext);
        ServiceHelper.startService(aggregationStrategy, processors, processorExchangeFactory);
    }

    /**
     * Strategy to create the thread pool for the aggregator background task which waits for and aggregates completed
     * tasks when running in parallel mode.
     *
     * @param  name the suggested name for the background thread
     * @return      the thread pool
     */
    protected ExecutorService createAggregateExecutorService(String name) {
        lock.lock();
        try {
            // use a cached thread pool so we each on-the-fly task has a dedicated thread to process completions as they come in
            return zwangineContext.getExecutorServiceManager().newScheduledThreadPool(this, name, 0);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(processors, errorHandlers, aggregationStrategy, processorExchangeFactory);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(processors, errorHandlers, aggregationStrategy, processorExchangeFactory);
        // only clear error handlers when shutting down
        if (errorHandlers != null) {
            errorHandlers.clear();
        }

        if (shutdownExecutorService && executorService != null) {
            getZwangineContext().getExecutorServiceManager().shutdownNow(executorService);
        }
        if (shutdownAggregateExecutorService && aggregateExecutorService != null) {
            getZwangineContext().getExecutorServiceManager().shutdownNow(aggregateExecutorService);
        }
    }

    protected static void setToEndpoint(Exchange exchange, Processor processor) {
        if (processor instanceof Producer producer) {
            exchange.setProperty(ExchangePropertyKey.TO_ENDPOINT, producer.getEndpoint().getEndpointUri());
        }
    }

    protected AggregationStrategy getAggregationStrategy(Exchange exchange) {
        AggregationStrategy answer = null;

        // prefer to use per Exchange aggregation strategy over a global strategy
        if (exchange != null) {
            Map<?, ?> property = exchange.getProperty(ExchangePropertyKey.AGGREGATION_STRATEGY, Map.class);
            Map<Object, AggregationStrategy> map = CastUtils.cast(property);
            if (map != null) {
                answer = map.get(this);
            }
        }
        if (answer == null) {
            // fallback to global strategy
            answer = getAggregationStrategy();
        }
        return answer;
    }

    /**
     * Sets the given {@link AggregationStrategy} on the {@link Exchange}.
     *
     * @param exchange            the exchange
     * @param aggregationStrategy the strategy
     */
    protected void setAggregationStrategyOnExchange(Exchange exchange, AggregationStrategy aggregationStrategy) {
        Map<?, ?> property = exchange.getProperty(ExchangePropertyKey.AGGREGATION_STRATEGY, Map.class);
        Map<Object, AggregationStrategy> map = CastUtils.cast(property);
        if (map == null) {
            map = new ConcurrentHashMap<>();
        } else {
            // it is not safe to use the map directly as the exchange doesn't have the deep copy of it's properties
            // we just create a new copy if we need to change the map
            map = new ConcurrentHashMap<>(map);
        }
        // store the strategy using this processor as the key
        // (so we can store multiple strategies on the same exchange)
        map.put(this, aggregationStrategy);
        exchange.setProperty(ExchangePropertyKey.AGGREGATION_STRATEGY, map);
    }

    /**
     * Removes the associated {@link AggregationStrategy} from the {@link Exchange} which must be done after use.
     *
     * @param exchange the current exchange
     */
    protected void removeAggregationStrategyFromExchange(Exchange exchange) {
        Map<?, ?> property = exchange.getProperty(ExchangePropertyKey.AGGREGATION_STRATEGY, Map.class);
        Map<Object, AggregationStrategy> map = CastUtils.cast(property);
        if (map == null) {
            return;
        }
        // remove the strategy using this processor as the key
        map.remove(this);
        // and remove map if its empty
        if (map.isEmpty()) {
            exchange.removeProperty(ExchangePropertyKey.AGGREGATION_STRATEGY);
        }
    }

    /**
     * Is the multicast processor working in streaming mode?
     * <p/>
     * In streaming mode:
     * <ul>
     * <li>we use {@link Iterable} to ensure we can send messages as soon as the data becomes available</li>
     * <li>for parallel processing, we start aggregating responses as they get send back to the processor; this means
     * the {@link AggregationStrategy} has to take care of handling out-of-order arrival of exchanges</li>
     * </ul>
     */
    public boolean isStreaming() {
        return streaming;
    }

    /**
     * Should the multicast processor stop processing further exchanges in case of an exception occurred?
     */
    public boolean isStopOnException() {
        return stopOnException;
    }

    /**
     * Returns the producers to multicast to
     */
    public Collection<Processor> getProcessors() {
        return processors;
    }

    /**
     * An optional timeout in millis when using parallel processing
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Maximum cache size used for reusing processors
     */
    public int getCacheSize() {
        return cacheSize;
    }

    /**
     * Use {@link #getAggregationStrategy(org.zenithblox.Exchange)} instead.
     */
    public AggregationStrategy getAggregationStrategy() {
        return aggregationStrategy;
    }

    public boolean isParallelProcessing() {
        return parallelProcessing;
    }

    @Deprecated(since = "4.7.0")
    public boolean isParallelAggregate() {
        return parallelAggregate;
    }

    public boolean isShareUnitOfWork() {
        return shareUnitOfWork;
    }

    public ExecutorService getAggregateExecutorService() {
        return aggregateExecutorService;
    }

    public void setAggregateExecutorService(ExecutorService aggregateExecutorService) {
        this.aggregateExecutorService = aggregateExecutorService;
        // we use a custom executor so do not shutdown
        this.shutdownAggregateExecutorService = false;
    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        return new ArrayList<>(processors);
    }

    @Override
    public boolean hasNext() {
        return processors != null && !processors.isEmpty();
    }
}
