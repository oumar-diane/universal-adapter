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
import org.zenithblox.spi.*;
import org.zenithblox.support.AsyncProcessorSupport;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.cache.DefaultProducerCache;
import org.zenithblox.support.cache.EmptyProducerCache;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Processor for forwarding exchanges to a dynamic endpoint destination.
 *
 * @see org.zenithblox.processor.SendProcessor
 */
public class SendDynamicProcessor extends AsyncProcessorSupport implements IdAware, WorkflowIdAware, ZwangineContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(SendDynamicProcessor.class);

    protected SendDynamicAware dynamicAware;
    protected volatile String scheme;
    protected ZwangineContext zwangineContext;
    protected final String uri;
    protected final Expression expression;
    protected String variableSend;
    protected String variableReceive;
    protected ExchangePattern pattern;
    protected ProducerCache producerCache;
    protected HeadersMapFactory headersMapFactory;
    protected String id;
    protected String workflowId;
    protected boolean ignoreInvalidEndpoint;
    protected int cacheSize;
    protected boolean allowOptimisedComponents = true;
    protected boolean autoStartupComponents = true;

    public SendDynamicProcessor(String uri, Expression expression) {
        this.uri = uri;
        this.expression = expression;
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
    public boolean process(Exchange exchange, final AsyncCallback callback) {
        if (!isStarted()) {
            exchange.setException(new IllegalStateException("SendProcessor has not been started: " + this));
            callback.done(true);
            return true;
        }

        // we should preserve existing MEP so remember old MEP
        // if you want to permanently to change the MEP then use .setExchangePattern in the DSL
        final ExchangePattern existingPattern = exchange.getPattern();

        // which endpoint to send to
        final Endpoint endpoint;
        final ExchangePattern destinationExchangePattern;

        // use dynamic endpoint so calculate the endpoint to use
        Object recipient = null;
        Processor preAwareProcessor = null;
        Processor postAwareProcessor = null;
        String staticUri = null;
        boolean prototype = cacheSize < 0;
        try {
            recipient = expression.evaluate(exchange, Object.class);
            if (dynamicAware != null) {
                // if its the same scheme as the pre-resolved dynamic aware then we can optimise to use it
                String originalUri = uri;
                String uri = resolveUri(exchange, recipient);
                String scheme = resolveScheme(exchange, uri);
                if (dynamicAware.getScheme().equals(scheme)) {
                    SendDynamicAware.DynamicAwareEntry entry = dynamicAware.prepare(exchange, uri, originalUri);
                    if (entry != null) {
                        staticUri = dynamicAware.resolveStaticUri(exchange, entry);
                        preAwareProcessor = dynamicAware.createPreProcessor(exchange, entry);
                        postAwareProcessor = dynamicAware.createPostProcessor(exchange, entry);
                        if (staticUri != null) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("Optimising toD via SendDynamicAware component: {} to use static uri: {}", scheme,
                                        URISupport.sanitizeUri(staticUri));
                            }
                        }
                    }
                }
            }
            Object targetRecipient = staticUri != null ? staticUri : recipient;
            targetRecipient = prepareRecipient(exchange, targetRecipient);
            if (targetRecipient == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Send dynamic evaluated as null so cannot send to any endpoint");
                }
                // no endpoint to send to, so ignore
                callback.done(true);
                return true;
            }
            Endpoint existing = getExistingEndpoint(exchange, targetRecipient);
            if (existing == null) {
                endpoint = resolveEndpoint(exchange, targetRecipient, prototype);
            } else {
                endpoint = existing;
                // we have an existing endpoint then its not a prototype scope
                prototype = false;
            }
            destinationExchangePattern = EndpointHelper.resolveExchangePatternFromUrl(endpoint.getEndpointUri());
        } catch (Exception e) {
            if (isIgnoreInvalidEndpoint()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Endpoint uri is invalid: {}. This exception will be ignored.", recipient, e);
                }
            } else {
                exchange.setException(e);
            }
            callback.done(true);
            return true;
        }

        // if we should store the received message body in a variable,
        // then we need to preserve the original message body
        Object body = null;
        Map<String, Object> headers = null;
        if (variableReceive != null) {
            try {
                body = exchange.getMessage().getBody();
                // do a defensive copy of the headers
                headers = headersMapFactory.newMap(exchange.getMessage().getHeaders());
            } catch (Exception throwable) {
                exchange.setException(throwable);
                callback.done(true);
                return true;
            }
        }
        final Object originalBody = body;
        final Map<String, Object> originalHeaders = headers;

        // send the exchange to the destination using the producer cache
        final Processor preProcessor = preAwareProcessor;
        final Processor postProcessor = postAwareProcessor;
        // destination exchange pattern overrides pattern
        final ExchangePattern pattern = destinationExchangePattern != null ? destinationExchangePattern : this.pattern;
        final boolean stopEndpoint = prototype;
        return producerCache.doInAsyncProducer(endpoint, exchange, callback, (p, e, c) -> {
            final Exchange target = configureExchange(e, pattern, endpoint);
            try {
                if (preProcessor != null) {
                    preProcessor.process(target);
                }
                // replace message body with variable
                if (variableSend != null) {
                    Object value = ExchangeHelper.getVariable(exchange, variableSend);
                    exchange.getMessage().setBody(value);
                }
            } catch (Exception t) {
                e.setException(t);
                // restore previous MEP
                target.setPattern(existingPattern);
                // we failed
                c.done(true);
            }

            LOG.debug(">>>> {} {}", endpoint, e);
            return p.process(target, doneSync -> {
                // restore previous MEP
                target.setPattern(existingPattern);
                try {
                    if (postProcessor != null) {
                        postProcessor.process(target);
                    }
                } catch (Exception e1) {
                    target.setException(e1);
                }
                // stop endpoint if prototype as it was only used once
                if (stopEndpoint) {
                    ServiceHelper.stopAndShutdownService(endpoint);
                }
                // result should be stored in variable instead of message body
                if (ExchangeHelper.shouldSetVariableResult(target, variableReceive)) {
                    ExchangeHelper.setVariableFromMessageBodyAndHeaders(target, variableReceive, target.getMessage());
                    target.getMessage().setBody(originalBody);
                    target.getMessage().setHeaders(originalHeaders);
                }
                // signal we are done
                c.done(doneSync);
            });
        });
    }

    protected static String resolveUri(Exchange exchange, Object recipient) throws NoTypeConversionAvailableException {
        if (recipient == null) {
            return null;
        }

        String uri;
        // trim strings as end users might have added spaces between separators
        if (recipient instanceof String string) {
            uri = string.trim();
        } else if (recipient instanceof Endpoint endpoint) {
            uri = endpoint.getEndpointKey();
        } else {
            // convert to a string type we can work with
            uri = exchange.getContext().getTypeConverter().mandatoryConvertTo(String.class, exchange, recipient);
        }

        // in case path has property placeholders then try to let property component resolve those
        try {
            uri = EndpointHelper.resolveEndpointUriPropertyPlaceholders(exchange.getContext(), uri);
        } catch (Exception e) {
            throw new ResolveEndpointFailedException(uri, e);
        }

        return uri;
    }

    protected static String resolveScheme(Exchange exchange, String uri) {
        return ExchangeHelper.resolveScheme(uri);
    }

    protected static Object prepareRecipient(Exchange exchange, Object recipient) throws NoTypeConversionAvailableException {
        if (recipient instanceof Endpoint || recipient instanceof NormalizedEndpointUri) {
            return recipient;
        } else if (recipient instanceof String string) {
            // trim strings as end users might have added spaces between separators
            recipient = string.trim();
        }
        if (recipient != null) {
            ZwangineContext ecc = exchange.getContext();
            String uri;
            if (recipient instanceof String string) {
                uri = string;
            } else {
                // convert to a string type we can work with
                uri = ecc.getTypeConverter().mandatoryConvertTo(String.class, exchange, recipient);
            }
            // make sure the uri has a scheme and a path
            int colon = uri.indexOf(':');
            if (colon == -1 || colon == uri.length() - 1) {
                throw new ResolveEndpointFailedException(uri, "Endpoint should include scheme:path");
            }
            // optimize and normalize endpoint
            return ecc.getZwangineContextExtension().normalizeUri(uri);
        }
        return null;
    }

    protected static Endpoint getExistingEndpoint(Exchange exchange, Object recipient) {
        return ProcessorHelper.getExistingEndpoint(exchange, recipient);
    }

    protected static Endpoint resolveEndpoint(Exchange exchange, Object recipient, boolean prototype) {
        return prototype
                ? ExchangeHelper.resolvePrototypeEndpoint(exchange, recipient)
                : ExchangeHelper.resolveEndpoint(exchange, recipient);
    }

    protected Exchange configureExchange(Exchange exchange, ExchangePattern pattern, Endpoint endpoint) {
        if (pattern != null) {
            exchange.setPattern(pattern);
        }
        // set property which endpoint we send to
        exchange.setProperty(ExchangePropertyKey.TO_ENDPOINT, endpoint.getEndpointUri());
        return exchange;
    }

    @Override
    protected void doInit() throws Exception {
        expression.init(zwangineContext);

        if ((isAllowOptimisedComponents() || isAutoStartupComponents()) && uri != null) {
            // in case path has property placeholders then try to let property component resolve those
            String u = EndpointHelper.resolveEndpointUriPropertyPlaceholders(zwangineContext, uri);
            // find out which component it is
            scheme = ExchangeHelper.resolveScheme(u);
        }

        if (isAllowOptimisedComponents() && uri != null) {
            try {
                if (scheme != null) {
                    // find out if the component can be optimised for send-dynamic
                    SendDynamicAwareResolver resolver = new SendDynamicAwareResolver();
                    dynamicAware = resolver.resolve(zwangineContext, scheme);
                    if (dynamicAware == null) {
                        // okay fallback and try with default component name
                        Component comp = zwangineContext.getComponent(scheme, false, isAutoStartupComponents());
                        if (comp != null) {
                            String defaultScheme = comp.getDefaultName();
                            if (!scheme.equals(defaultScheme)) {
                                dynamicAware = resolver.resolve(zwangineContext, defaultScheme);
                                dynamicAware.setScheme(scheme);
                            }
                        }
                    }
                    if (dynamicAware != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Detected SendDynamicAware component: {} optimising toD: {}", scheme,
                                    URISupport.sanitizeUri(uri));
                        }
                    }
                }
            } catch (Exception e) {
                // ignore
                if (LOG.isDebugEnabled()) {
                    LOG.debug(
                            "Error creating optimised SendDynamicAwareResolver for uri: {} due to {}. This exception is ignored",
                            URISupport.sanitizeUri(uri), e.getMessage(), e);
                }
            }
        }
        ServiceHelper.initService(dynamicAware);

        headersMapFactory = zwangineContext.getZwangineContextExtension().getHeadersMapFactory();
    }

    @Override
    protected void doStart() throws Exception {
        // ensure the component is started
        if (autoStartupComponents && scheme != null) {
            zwangineContext.getComponent(scheme);
        }

        if (producerCache == null) {
            if (cacheSize < 0) {
                producerCache = new EmptyProducerCache(this, zwangineContext);
                LOG.debug("DynamicSendTo {} is not using ProducerCache", this);
            } else {
                producerCache = new DefaultProducerCache(this, zwangineContext, cacheSize);
                LOG.debug("DynamicSendTo {} using ProducerCache with cacheSize={}", this, cacheSize);
            }
        }

        ServiceHelper.startService(dynamicAware, producerCache);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(producerCache, dynamicAware);
    }

    public EndpointUtilizationStatistics getEndpointUtilizationStatistics() {
        return producerCache.getEndpointUtilizationStatistics();
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public SendDynamicAware getDynamicAware() {
        return dynamicAware;
    }

    public String getUri() {
        return uri;
    }

    public Expression getExpression() {
        return expression;
    }

    public ExchangePattern getPattern() {
        return pattern;
    }

    public void setPattern(ExchangePattern pattern) {
        this.pattern = pattern;
    }

    public String getVariableSend() {
        return variableSend;
    }

    public void setVariableSend(String variableSend) {
        this.variableSend = variableSend;
    }

    public String getVariableReceive() {
        return variableReceive;
    }

    public void setVariableReceive(String variableReceive) {
        this.variableReceive = variableReceive;
    }

    public boolean isIgnoreInvalidEndpoint() {
        return ignoreInvalidEndpoint;
    }

    public void setIgnoreInvalidEndpoint(boolean ignoreInvalidEndpoint) {
        this.ignoreInvalidEndpoint = ignoreInvalidEndpoint;
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    public boolean isAllowOptimisedComponents() {
        return allowOptimisedComponents;
    }

    public void setAllowOptimisedComponents(boolean allowOptimisedComponents) {
        this.allowOptimisedComponents = allowOptimisedComponents;
    }

    public boolean isAutoStartupComponents() {
        return autoStartupComponents;
    }

    public void setAutoStartupComponents(boolean autoStartupComponents) {
        this.autoStartupComponents = autoStartupComponents;
    }
}
