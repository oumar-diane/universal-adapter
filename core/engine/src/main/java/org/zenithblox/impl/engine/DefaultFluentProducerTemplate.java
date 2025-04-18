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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This implementation is based on the usage pattern, that a top level DefaultFluentProducerTemplate instance is created
 * as singleton and provided to the Zwangine end user (such as injected into a POJO).
 * <p>
 * The top level instance is then cloned once per message that is being built using the fluent method calls and then
 * reset when the message has been sent.
 * <p>
 * Each cloned instance is not thread-safe as its assumed that its a single thread that calls the fluent method to build
 * up the message to be sent.
 */
public class DefaultFluentProducerTemplate extends ServiceSupport implements FluentProducerTemplate {

    // transient state of endpoint, headers, exchange properties, variables, and body which needs to be thread local scoped to be thread-safe
    private Map<String, Object> headers;
    private Map<String, Object> exchangeProperties;
    private Map<String, Object> variables;
    private Object body;
    private Supplier<Exchange> exchangeSupplier;
    private Supplier<Processor> processorSupplier;
    private Consumer<ProducerTemplate> templateCustomizer;

    private final ZwangineContext context;
    private final ClassValue<Processor> resultProcessors;
    private Endpoint defaultEndpoint;
    private int maximumCacheSize;
    private boolean eventNotifierEnabled;
    private volatile Endpoint endpoint;
    private volatile String endpointUri;
    private volatile ProducerTemplate template;
    private volatile boolean cloned;
    private volatile boolean useDefaultEndpoint = true;

    public DefaultFluentProducerTemplate(ZwangineContext context) {
        this.context = context;
        this.eventNotifierEnabled = true;
        this.resultProcessors = new ClassValue<>() {
            @Override
            protected Processor computeValue(Class<?> type) {
                return new ProducerTemplateResultProcessor(type);
            }
        };
    }

    private DefaultFluentProducerTemplate(ZwangineContext context,
                                          ClassValue<Processor> resultProcessors,
                                          Endpoint defaultEndpoint, int maximumCacheSize, boolean eventNotifierEnabled,
                                          ProducerTemplate template, Endpoint endpoint, String endpointUri) {
        this.context = context;
        this.resultProcessors = resultProcessors;
        this.defaultEndpoint = defaultEndpoint;
        this.maximumCacheSize = maximumCacheSize;
        this.eventNotifierEnabled = eventNotifierEnabled;
        this.template = template;
        this.endpoint = endpoint;
        this.endpointUri = endpointUri;
        this.cloned = true;
    }

    private DefaultFluentProducerTemplate newClone() {
        return new DefaultFluentProducerTemplate(
                context, resultProcessors, defaultEndpoint, maximumCacheSize, eventNotifierEnabled, template, endpoint,
                endpointUri);
    }

    private DefaultFluentProducerTemplate checkCloned() {
        if (!cloned) {
            return newClone();
        } else {
            return this;
        }
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return context;
    }

    @Override
    public int getCurrentCacheSize() {
        if (template == null) {
            return 0;
        }
        return template.getCurrentCacheSize();
    }

    @Override
    public void cleanUp() {
        if (template != null) {
            template.cleanUp();
        }
    }

    @Override
    public void setDefaultEndpointUri(String endpointUri) {
        setDefaultEndpoint(getZwangineContext().getEndpoint(endpointUri));
    }

    @Override
    public Endpoint getDefaultEndpoint() {
        return defaultEndpoint;
    }

    @Override
    public void setDefaultEndpoint(Endpoint defaultEndpoint) {
        if (this.defaultEndpoint != null && isStarted()) {
            throw new IllegalArgumentException("Not allowed after template has been started");
        }
        this.defaultEndpoint = defaultEndpoint;
    }

    @Override
    public int getMaximumCacheSize() {
        return maximumCacheSize;
    }

    @Override
    public void setMaximumCacheSize(int maximumCacheSize) {
        if (this.maximumCacheSize != 0 && isStarted()) {
            throw new IllegalArgumentException("Not allowed after template has been started");
        }
        this.maximumCacheSize = maximumCacheSize;
    }

    @Override
    public boolean isEventNotifierEnabled() {
        return eventNotifierEnabled;
    }

    @Override
    public void setEventNotifierEnabled(boolean eventNotifierEnabled) {
        if (isStarted()) {
            throw new IllegalArgumentException("Not allowed after template has been started");
        }
        this.eventNotifierEnabled = eventNotifierEnabled;
    }

    @Override
    public FluentProducerTemplate withHeaders(Map<String, Object> headers) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withHeaders and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.headers;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.headers = map;
        }
        map.putAll(headers);
        return clone;
    }

    @Override
    public FluentProducerTemplate withHeader(String key, Object value) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withHeader and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.headers;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.headers = map;
        }
        map.put(key, value);
        return clone;
    }

    @Override
    public FluentProducerTemplate withExchangeProperties(Map<String, Object> properties) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException(
                    "Cannot use both withExchangeProperties and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.exchangeProperties;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.exchangeProperties = map;
        }
        map.putAll(properties);
        return clone;
    }

    @Override
    public FluentProducerTemplate withExchangeProperty(String key, Object value) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException(
                    "Cannot use both withExchangeProperty and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.exchangeProperties;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.exchangeProperties = map;
        }
        map.put(key, value);
        return clone;
    }

    @Override
    public FluentProducerTemplate withVariables(Map<String, Object> variables) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withVariables and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.variables;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.variables = map;
        }
        map.putAll(variables);
        return clone;
    }

    @Override
    public FluentProducerTemplate withVariable(String key, Object value) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withVariable and withProcessor with FluentProducerTemplate");
        }

        Map<String, Object> map = clone.variables;
        if (map == null) {
            map = new LinkedHashMap<>();
            clone.variables = map;
        }
        map.put(key, value);
        return clone;
    }

    @Override
    public FluentProducerTemplate withBody(Object body) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withBody and withProcessor with FluentProducerTemplate");
        }
        clone.body = body;
        return clone;
    }

    @Override
    public FluentProducerTemplate withBodyAs(Object body, Class<?> type) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.processorSupplier != null) {
            throw new IllegalArgumentException("Cannot use both withBodyAs and withProcessor with FluentProducerTemplate");
        }

        clone.body = type != null
                ? clone.context.getTypeConverter().convertTo(type, body)
                : body;
        return clone;
    }

    @Override
    public FluentProducerTemplate withDefaultEndpoint(String endpointUri) {
        if (cloned) {
            throw new IllegalArgumentException("Default endpoint must be set before template has been used");
        }
        this.defaultEndpoint = getZwangineContext().getEndpoint(endpointUri);
        return this;
    }

    @Override
    public FluentProducerTemplate withDefaultEndpoint(EndpointProducerResolver resolver) {
        if (cloned) {
            throw new IllegalArgumentException("Default endpoint must be set before template has been used");
        }
        this.defaultEndpoint = resolver.resolve(getZwangineContext());
        return this;
    }

    @Override
    public FluentProducerTemplate withDefaultEndpoint(Endpoint endpoint) {
        if (cloned) {
            throw new IllegalArgumentException("Default endpoint must be set before template has been used");
        }
        this.defaultEndpoint = endpoint;
        return this;
    }

    @Override
    public FluentProducerTemplate withTemplateCustomizer(final Consumer<ProducerTemplate> templateCustomizer) {
        if (this.templateCustomizer != null && isStarted()) {
            throw new IllegalArgumentException("Not allowed after template has been started");
        }
        this.templateCustomizer = templateCustomizer;

        if (template != null) {
            // need to re-initialize template since we have a customizer
            ServiceHelper.stopService(template);
            templateCustomizer.accept(template);
            ServiceHelper.startService(template);
        }

        return this;
    }

    @Override
    public FluentProducerTemplate withExchange(final Exchange exchange) {
        return withExchange(() -> exchange);
    }

    @Override
    public FluentProducerTemplate withExchange(final Supplier<Exchange> exchangeSupplier) {
        DefaultFluentProducerTemplate clone = checkCloned();

        clone.exchangeSupplier = exchangeSupplier;
        return clone;
    }

    @Override
    public FluentProducerTemplate withProcessor(final Processor processor) {
        return withProcessor(() -> processor);
    }

    @Override
    public FluentProducerTemplate withProcessor(final Supplier<Processor> processorSupplier) {
        DefaultFluentProducerTemplate clone = checkCloned();

        if (clone.body != null) {
            throw new IllegalArgumentException("Cannot use both withBody and withProcessor with FluentProducerTemplate");
        }
        clone.processorSupplier = processorSupplier;
        return clone;
    }

    @Override
    public FluentProducerTemplate to(String endpointUri) {
        DefaultFluentProducerTemplate clone = checkCloned();

        clone.useDefaultEndpoint = false;
        clone.endpointUri = endpointUri;
        clone.endpoint = context.getEndpoint(endpointUri);
        return clone;
    }

    @Override
    public FluentProducerTemplate to(Endpoint endpoint) {
        DefaultFluentProducerTemplate clone = checkCloned();

        clone.useDefaultEndpoint = false;
        clone.endpoint = endpoint;
        return clone;
    }

    // ************************
    // REQUEST
    // ************************

    @Override
    public Object request() throws ZwangineExecutionException {
        return request(Object.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T request(Class<T> type) throws ZwangineExecutionException {
        if (exchangeSupplier != null && exchangeSupplier.get() != null) {
            throw new IllegalArgumentException(
                    "withExchange not supported on FluentProducerTemplate.request method. Use send method instead.");
        }

        DefaultFluentProducerTemplate clone = checkCloned();

        // Determine the target endpoint
        final Endpoint target = clone.target();

        // Create the default processor if not provided.
        Processor processor = clone.processorSupplier != null ? clone.processorSupplier.get() : null;
        final Processor processorSupplier = processor != null ? processor : clone.defaultProcessor();

        T result;
        if (type == Exchange.class) {
            result = (T) clone.template().request(target, processorSupplier);
        } else if (type == Message.class) {
            Exchange exchange = clone.template().request(target, processorSupplier);
            result = (T) exchange.getMessage();
        } else {
            Exchange exchange = clone.template().send(
                    target,
                    ExchangePattern.InOut,
                    processorSupplier,
                    clone.resultProcessors.get(type));

            result = clone.context.getTypeConverter().convertTo(
                    type,
                    ExchangeHelper.extractResultBody(exchange, exchange.getPattern()));
        }

        // reset cloned flag so when we use it again it has to set values again
        cloned = false;
        useDefaultEndpoint = true;

        return result;
    }

    @Override
    public Future<Object> asyncRequest() {
        return asyncRequest(Object.class);
    }

    @Override
    public <T> Future<T> asyncRequest(Class<T> type) {
        DefaultFluentProducerTemplate clone = checkCloned();

        // Determine the target endpoint
        final Endpoint target = clone.target();

        Future<T> result = clone.template().asyncSend(target, exchange -> {
            // Make a copy of the headers and body so that async processing won't
            // be invalidated by subsequent reuse of the template
            Object bodyCopy = clone.body;

            exchange.setPattern(ExchangePattern.InOut);
            exchange.getMessage().setBody(bodyCopy);
            if (clone.headers != null) {
                exchange.getMessage().setHeaders(new HashMap<>(clone.headers));
            }
            if (clone.exchangeProperties != null) {
                exchange.getProperties().putAll(clone.exchangeProperties);
            }
            if (clone.variables != null) {
                clone.variables.forEach((k, v) -> ExchangeHelper.setVariable(exchange, k, v));
            }
        }).thenApply(answer -> answer.getMessage().getBody(type));

        // reset cloned flag so when we use it again it has to set values again
        cloned = false;
        useDefaultEndpoint = true;

        return result;
    }

    // ************************
    // SEND
    // ************************

    @Override
    public Exchange send() throws ZwangineExecutionException {
        DefaultFluentProducerTemplate clone = checkCloned();

        // Determine the target endpoint
        final Endpoint target = clone.target();

        Exchange result;
        Exchange exchange = clone.exchangeSupplier != null ? clone.exchangeSupplier.get() : null;
        if (exchange != null) {
            result = clone.template().send(target, exchange);
        } else {
            Processor proc = clone.processorSupplier != null ? clone.processorSupplier.get() : null;
            final Processor processor = proc != null ? proc : clone.defaultProcessor();
            result = clone.template().send(target, processor);
        }

        // reset cloned flag so when we use it again it has to set values again
        cloned = false;
        useDefaultEndpoint = true;

        return result;
    }

    @Override
    public Future<Exchange> asyncSend() {
        DefaultFluentProducerTemplate clone = checkCloned();

        // Determine the target endpoint
        final Endpoint target = clone.target();

        Future<Exchange> result;
        Exchange exchange = clone.exchangeSupplier != null ? clone.exchangeSupplier.get() : null;
        if (exchange != null) {
            result = clone.template().asyncSend(target, exchange);
        } else {
            Processor proc = clone.processorSupplier != null ? clone.processorSupplier.get() : null;
            final Processor processor = proc != null ? proc : clone.defaultAsyncProcessor();
            result = clone.template().asyncSend(target, processor);
        }

        // reset cloned flag so when we use it again it has to set values again
        cloned = false;
        useDefaultEndpoint = true;

        return result;
    }

    // ************************
    // HELPERS
    // ************************

    /**
     * Create the FluentProducerTemplate by setting the zwangine context
     *
     * @param  context the zwangine context
     * @return         a new created instance of the fluent producer template
     */
    public static FluentProducerTemplate on(ZwangineContext context) {
        DefaultFluentProducerTemplate fluent = new DefaultFluentProducerTemplate(context);
        // we create a new private instance so mark it as cloned
        fluent.cloned = true;
        fluent.start();
        return fluent;
    }

    /**
     * Create the FluentProducerTemplate by setting the zwangine context and default endpoint
     *
     * @param  context  the zwangine context
     * @param  endpoint the default endpoint
     * @return          a new created instance of the fluent producer template
     */
    public static FluentProducerTemplate on(ZwangineContext context, Endpoint endpoint) {
        DefaultFluentProducerTemplate fluent = new DefaultFluentProducerTemplate(context);
        fluent.withDefaultEndpoint(endpoint);
        // we create a new private instance so mark it as cloned
        fluent.cloned = true;
        fluent.start();
        return fluent;
    }

    /**
     * Create the FluentProducerTemplate by setting the zwangine context and default endpoint
     *
     * @param  context  the zwangine context
     * @param  resolver the default endpoint
     * @return          a new created instance of the fluent producer template
     */
    public static FluentProducerTemplate on(ZwangineContext context, EndpointProducerResolver resolver) {
        DefaultFluentProducerTemplate fluent = new DefaultFluentProducerTemplate(context);
        fluent.withDefaultEndpoint(resolver);
        // we create a new private instance so mark it as cloned
        fluent.cloned = true;
        fluent.start();
        return fluent;
    }

    /**
     * Create the FluentProducerTemplate by setting the zwangine context and default endpoint
     *
     * @param  context  the zwangine context
     * @param  endpoint the default endpoint
     * @return          a new created instance of the fluent producer template
     */
    public static FluentProducerTemplate on(ZwangineContext context, String endpoint) {
        DefaultFluentProducerTemplate fluent = new DefaultFluentProducerTemplate(context);
        fluent.withDefaultEndpoint(endpoint);
        // we create a new private instance so mark it as cloned
        fluent.cloned = true;
        fluent.start();
        return fluent;
    }

    private ProducerTemplate template() {
        return template;
    }

    private Processor defaultProcessor() {
        return exchange -> {
            if (headers != null) {
                exchange.getIn().getHeaders().putAll(headers);
            }
            if (exchangeProperties != null) {
                exchange.getProperties().putAll(exchangeProperties);
            }
            if (body != null) {
                exchange.getIn().setBody(body);
            }
            if (variables != null) {
                variables.forEach((k, v) -> ExchangeHelper.setVariable(exchange, k, v));
            }
        };
    }

    private Processor defaultAsyncProcessor() {
        final Map<String, Object> headersCopy = ObjectHelper.isNotEmpty(this.headers) ? new HashMap<>(this.headers) : null;
        final Map<String, Object> propertiesCopy
                = ObjectHelper.isNotEmpty(this.exchangeProperties) ? new HashMap<>(this.exchangeProperties) : null;
        final Map<String, Object> variablesCopy
                = ObjectHelper.isNotEmpty(this.variables) ? new HashMap<>(this.variables) : null;
        final Object bodyCopy = this.body;
        return exchange -> {
            if (headersCopy != null) {
                exchange.getIn().getHeaders().putAll(headersCopy);
            }
            if (propertiesCopy != null) {
                exchange.getProperties().putAll(propertiesCopy);
            }
            if (bodyCopy != null) {
                exchange.getIn().setBody(bodyCopy);
            }
            if (variablesCopy != null) {
                variablesCopy.forEach((k, v) -> ExchangeHelper.setVariable(exchange, k, v));
            }
        };
    }

    private Endpoint target() {
        if (!useDefaultEndpoint && endpoint != null) {
            return endpoint;
        }
        if (defaultEndpoint != null) {
            return defaultEndpoint;
        }

        if (template != null && template.getDefaultEndpoint() != null) {
            return template.getDefaultEndpoint();
        }

        throw new IllegalArgumentException(
                "No endpoint configured on FluentProducerTemplate. You can configure an endpoint with to(uri)");
    }

    @Override
    protected void doInit() throws Exception {
        ObjectHelper.notNull(context, "ZwangineContext");
        template = context.createProducerTemplate(maximumCacheSize);
        if (defaultEndpoint != null) {
            template.setDefaultEndpoint(defaultEndpoint);
        }
        template.setEventNotifierEnabled(eventNotifierEnabled);
        if (templateCustomizer != null) {
            templateCustomizer.accept(template);
        }
        ServiceHelper.initService(template);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(template);
    }

    @Override
    protected void doStop() throws Exception {
        this.endpoint = null;
        this.endpointUri = null;
        this.exchangeSupplier = null;
        this.processorSupplier = null;
        this.templateCustomizer = null;
        ServiceHelper.stopService(template);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownService(template);
        template = null;
    }
}
