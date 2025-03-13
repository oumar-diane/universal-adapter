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
package org.zenithblox.saga;

import org.zenithblox.*;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * An in-memory implementation of a saga coordinator.
 */
public class InMemorySagaCoordinator implements ZwangineSagaCoordinator {

    private enum Status {
        RUNNING,
        COMPENSATING,
        COMPENSATED,
        COMPLETING,
        COMPLETED
    }

    private static final Logger LOG = LoggerFactory.getLogger(InMemorySagaCoordinator.class);

    private final ZwangineContext zwangineContext;
    private final InMemorySagaService sagaService;
    private final String sagaId;
    private final List<ZwangineSagaStep> steps;
    private final Map<ZwangineSagaStep, Map<String, Object>> optionValues;
    private final AtomicReference<Status> currentStatus;

    public InMemorySagaCoordinator(ZwangineContext zwangineContext, InMemorySagaService sagaService, String sagaId) {
        this.zwangineContext = ObjectHelper.notNull(zwangineContext, "zwangineContext");
        this.sagaService = ObjectHelper.notNull(sagaService, "sagaService");
        this.sagaId = ObjectHelper.notNull(sagaId, "sagaId");
        this.steps = new CopyOnWriteArrayList<>();
        this.optionValues = new ConcurrentHashMap<>();
        this.currentStatus = new AtomicReference<>(Status.RUNNING);
    }

    @Override
    public String getId() {
        return sagaId;
    }

    @Override
    public CompletableFuture<Void> beginStep(Exchange exchange, ZwangineSagaStep step) {
        Status status = currentStatus.get();
        if (status != Status.RUNNING) {
            CompletableFuture<Void> res = new CompletableFuture<>();
            res.completeExceptionally(new IllegalStateException("Cannot begin: status is " + status));
            return res;
        }

        this.steps.add(step);

        if (!step.getOptions().isEmpty()) {
            optionValues.putIfAbsent(step, new ConcurrentHashMap<>());
            Map<String, Object> values = optionValues.computeIfAbsent(step, k -> new HashMap<>());
            for (String option : step.getOptions().keySet()) {
                Expression expression = step.getOptions().get(option);
                if (expression != null) {
                    try {
                        Object value = expression.evaluate(exchange, Object.class);
                        if (value != null) {
                            values.put(option, value);
                        }
                    } catch (Exception ex) {
                        return CompletableFuture.supplyAsync(() -> {
                            throw new RuntimeZwangineException("Cannot evaluate saga option '" + option + "'", ex);
                        });
                    }
                }
            }
        }

        if (step.getTimeoutInMilliseconds().isPresent()) {
            sagaService.getExecutorService().schedule(() -> {
                boolean doAction = currentStatus.compareAndSet(Status.RUNNING, Status.COMPENSATING);
                if (doAction) {
                    doCompensate(exchange);
                }
            }, step.getTimeoutInMilliseconds().get(), TimeUnit.MILLISECONDS);
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> compensate(Exchange exchange) {
        boolean doAction = currentStatus.compareAndSet(Status.RUNNING, Status.COMPENSATING);

        if (doAction) {
            doCompensate(exchange);
        } else {
            Status status = currentStatus.get();
            if (status != Status.COMPENSATING && status != Status.COMPENSATED) {
                CompletableFuture<Void> res = new CompletableFuture<>();
                res.completeExceptionally(new IllegalStateException("Cannot compensate: status is " + status));
                return res;
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Override
    public CompletableFuture<Void> complete(Exchange exchange) {
        boolean doAction = currentStatus.compareAndSet(Status.RUNNING, Status.COMPLETING);

        if (doAction) {
            doComplete(exchange);
        } else {
            Status status = currentStatus.get();
            if (status != Status.COMPLETING && status != Status.COMPLETED) {
                CompletableFuture<Void> res = new CompletableFuture<>();
                res.completeExceptionally(new IllegalStateException("Cannot complete: status is " + status));
                return res;
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    public CompletableFuture<Boolean> doCompensate(final Exchange exchange) {
        return doFinalize(exchange, ZwangineSagaStep::getCompensation, "compensation")
                .thenApply(res -> {
                    currentStatus.set(Status.COMPENSATED);
                    return res;
                });
    }

    public CompletableFuture<Boolean> doComplete(final Exchange exchange) {
        return doFinalize(exchange, ZwangineSagaStep::getCompletion, "completion")
                .thenApply(res -> {
                    currentStatus.set(Status.COMPLETED);
                    return res;
                });
    }

    public CompletableFuture<Boolean> doFinalize(
            final Exchange exchange,
            Function<ZwangineSagaStep, Optional<Endpoint>> endpointExtractor, String description) {
        CompletableFuture<Boolean> result = CompletableFuture.completedFuture(true);
        for (ZwangineSagaStep step : reversed(steps)) {
            Optional<Endpoint> endpoint = endpointExtractor.apply(step);
            if (endpoint.isPresent()) {
                result = result.thenCompose(
                        prevResult -> doFinalize(exchange, endpoint.get(), step, 0, description)
                                .thenApply(res -> prevResult && res));
            }
        }
        return result.whenComplete((done, ex) -> {
            if (ex != null) {
                LOG.error("Cannot finalize {} the saga", description, ex);
            } else if (!done) {
                LOG.warn("Unable to finalize {} for all required steps of the saga {}", description, sagaId);
            }
        });
    }

    private CompletableFuture<Boolean> doFinalize(
            Exchange exchange, Endpoint endpoint, ZwangineSagaStep step, int doneAttempts, String description) {
        Exchange target = createExchange(exchange, endpoint, step);

        return CompletableFuture.supplyAsync(() -> {
            Exchange res = zwangineContext.createFluentProducerTemplate().to(endpoint).withExchange(target).send();
            Exception ex = res.getException();
            if (ex != null) {
                throw new RuntimeZwangineException(res.getException());
            }
            return true;
        }, sagaService.getExecutorService()).exceptionally(ex -> {
            LOG.warn("Exception thrown during {} at {}. Attempt {} of {}", description, endpoint.getEndpointUri(),
                    doneAttempts + 1, sagaService.getMaxRetryAttempts(), ex);
            return false;
        }).thenCompose(executed -> {
            int currentAttempt = doneAttempts + 1;
            if (executed) {
                return CompletableFuture.completedFuture(true);
            } else if (currentAttempt >= sagaService.getMaxRetryAttempts()) {
                return CompletableFuture.completedFuture(false);
            } else {
                CompletableFuture<Boolean> future = new CompletableFuture<>();
                sagaService.getExecutorService().schedule(() -> {
                    doFinalize(target, endpoint, step, currentAttempt, description).whenComplete((res, ex) -> {
                        if (ex != null) {
                            future.completeExceptionally(ex);
                        } else {
                            future.complete(res);
                        }
                    });
                }, sagaService.getRetryDelayInMilliseconds(), TimeUnit.MILLISECONDS);
                return future;
            }
        });
    }

    private Exchange createExchange(Exchange parent, Endpoint endpoint, ZwangineSagaStep step) {
        Exchange answer = endpoint.createExchange();
        answer.getMessage().setHeader(Exchange.SAGA_LONG_RUNNING_ACTION, getId());

        // preserve span from parent, so we can link this new exchange to the parent span for distributed tracing
        Object span = parent != null ? parent.getProperty(ExchangePropertyKey.OTEL_ACTIVE_SPAN) : null;
        if (span != null) {
            answer.setProperty(ExchangePropertyKey.OTEL_ACTIVE_SPAN, span);
        }

        Map<String, Object> values = optionValues.get(step);
        if (values != null) {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                answer.getMessage().setHeader(entry.getKey(), entry.getValue());
            }
        }
        return answer;
    }

    private <T> List<T> reversed(List<T> list) {
        List<T> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }
}
