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
package org.zenithblox.processor.idempotent;

import org.zenithblox.*;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.IdempotentRepository;
import org.zenithblox.spi.WorkflowIdAware;
import org.zenithblox.spi.Synchronization;
import org.zenithblox.support.AsyncProcessorConverterHelper;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An implementation of the <a href="http://zwangine.zwangine.org/idempotent-consumer.html">Idempotent Consumer</a> pattern.
 * <p/>
 * This implementation supports idempotent repositories implemented as
 * {@link org.zenithblox.spi.IdempotentRepository}.
 *
 * @see org.zenithblox.spi.IdempotentRepository
 */
public class IdempotentConsumer extends AsyncProcessorSupport
        implements ZwangineContextAware, Navigate<Processor>, IdAware, WorkflowIdAware {

    private static final Logger LOG = LoggerFactory.getLogger(IdempotentConsumer.class);

    private ZwangineContext zwangineContext;
    private String id;
    private String workflowId;
    private final Expression messageIdExpression;
    private final AsyncProcessor processor;
    private final IdempotentRepository idempotentRepository;
    private final boolean eager;
    private final boolean completionEager;
    private final boolean skipDuplicate;
    private final boolean removeOnFailure;
    private final AtomicLong duplicateMessageCount = new AtomicLong();

    public IdempotentConsumer(Expression messageIdExpression, IdempotentRepository idempotentRepository,
                              boolean eager, boolean completionEager, boolean skipDuplicate, boolean removeOnFailure,
                              Processor processor) {
        this.messageIdExpression = messageIdExpression;
        this.idempotentRepository = idempotentRepository;
        this.eager = eager;
        this.completionEager = completionEager;
        this.skipDuplicate = skipDuplicate;
        this.removeOnFailure = removeOnFailure;
        this.processor = AsyncProcessorConverterHelper.convert(processor);
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    @Override
    public boolean process(final Exchange exchange, final AsyncCallback callback) {
        final AsyncCallback target;

        final String messageId;
        try {
            messageId = messageIdExpression.evaluate(exchange, String.class);
            if (messageId == null) {
                exchange.setException(new NoMessageIdException(exchange, messageIdExpression));
                callback.done(true);
                return true;
            }
        } catch (Exception e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }

        try {
            boolean newKey;
            if (eager) {
                // add the key to the repository
                newKey = idempotentRepository.add(exchange, messageId);
            } else {
                // check if we already have the key
                newKey = !idempotentRepository.contains(exchange, messageId);
            }

            if (!newKey) {
                // mark the exchange as duplicate
                exchange.setProperty(ExchangePropertyKey.DUPLICATE_MESSAGE, Boolean.TRUE);

                // we already have this key so its a duplicate message
                onDuplicate(exchange, messageId);

                if (skipDuplicate) {
                    // if we should skip duplicate then we are done
                    LOG.debug("Ignoring duplicate message with id: {} for exchange: {}", messageId, exchange);
                    callback.done(true);
                    return true;
                }
            }

            final Synchronization onCompletion
                    = new IdempotentOnCompletion(idempotentRepository, messageId, eager, removeOnFailure);

            if (completionEager) {
                // the callback will eager complete
                target = new IdempotentConsumerCallback(exchange, onCompletion, callback);
            } else {
                // we can use existing callback as target
                target = callback;
                // the scope is to do the idempotent completion work as an unit of work on the exchange when its done being workflowd
                exchange.getExchangeExtension().addOnCompletion(onCompletion);
            }
        } catch (Exception e) {
            exchange.setException(e);
            callback.done(true);
            return true;
        }

        // process the exchange
        return processor.process(exchange, target);
    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        List<Processor> answer = new ArrayList<>(1);
        answer.add(processor);
        return answer;
    }

    @Override
    public boolean hasNext() {
        return processor != null;
    }

    // Properties
    // -------------------------------------------------------------------------
    public Expression getMessageIdExpression() {
        return messageIdExpression;
    }

    public IdempotentRepository getIdempotentRepository() {
        return idempotentRepository;
    }

    public Processor getProcessor() {
        return processor;
    }

    public long getDuplicateMessageCount() {
        return duplicateMessageCount.get();
    }

    // Implementation methods
    // -------------------------------------------------------------------------

    @Override
    protected void doStart() throws Exception {
        // must add before start so it will have ZwangineContext injected first
        if (!zwangineContext.hasService(idempotentRepository)) {
            zwangineContext.addService(idempotentRepository);
        }
        ServiceHelper.startService(processor, idempotentRepository);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(processor, idempotentRepository);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(processor, idempotentRepository);
        zwangineContext.removeService(idempotentRepository);
    }

    public boolean isEager() {
        return eager;
    }

    public boolean isCompletionEager() {
        return completionEager;
    }

    public boolean isSkipDuplicate() {
        return skipDuplicate;
    }

    public boolean isRemoveOnFailure() {
        return removeOnFailure;
    }

    /**
     * Resets the duplicate message counter to <code>0L</code>.
     */
    public void resetDuplicateMessageCount() {
        duplicateMessageCount.set(0L);
    }

    private void onDuplicate(Exchange exchange, String messageId) {
        duplicateMessageCount.incrementAndGet();

        onDuplicateMessage(exchange, messageId);
    }

    /**
     * Clear the idempotent repository
     */
    public void clear() {
        idempotentRepository.clear();
    }

    /**
     * A strategy method to allow derived classes to overload the behaviour of processing a duplicate message
     *
     * @param exchange  the exchange
     * @param messageId the message ID of this exchange
     */
    protected void onDuplicateMessage(Exchange exchange, String messageId) {
        // noop
    }

    /**
     * {@link org.zenithblox.AsyncCallback} that is invoked when the idempotent consumer block ends
     */
    private static class IdempotentConsumerCallback implements AsyncCallback {
        private final Exchange exchange;
        private final Synchronization onCompletion;
        private final AsyncCallback callback;

        IdempotentConsumerCallback(Exchange exchange, Synchronization onCompletion, AsyncCallback callback) {
            this.exchange = exchange;
            this.onCompletion = onCompletion;
            this.callback = callback;
        }

        @Override
        public void done(boolean doneSync) {
            try {
                if (exchange.isFailed()) {
                    onCompletion.onFailure(exchange);
                } else {
                    onCompletion.onComplete(exchange);
                }
            } finally {
                callback.done(doneSync);
            }
        }

        @Override
        public String toString() {
            return "IdempotentConsumerCallback";
        }
    }
}
