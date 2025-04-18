/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.model;

import org.zenithblox.ExchangePattern;
import org.zenithblox.Message;
import org.zenithblox.Processor;
import org.zenithblox.spi.Metadata;

import java.util.concurrent.ExecutorService;

/**
 * Workflows a copy of a message (or creates a new message) to a secondary destination while continue routing the original
 * message.
 */
@Metadata(label = "eip,routing")
public class WireTapDefinition<Type extends ProcessorDefinition<Type>> extends ToDynamicDefinition
        implements ExecutorServiceAwareDefinition<WireTapDefinition<Type>> {

    private ExecutorService executorServiceBean;
    private Processor onPrepareProcessor;

    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String copy;
    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String dynamicUri;
    @Metadata(label = "advanced", javaType = "org.zenithblox.Processor")
    private String onPrepare;
    @Metadata(label = "advanced", javaType = "java.util.concurrent.ExecutorService")
    private String executorService;

    public WireTapDefinition() {
    }

    public WireTapDefinition(WireTapDefinition<?> source) {
        super(source);
        this.executorServiceBean = source.executorServiceBean;
        this.onPrepareProcessor = source.onPrepareProcessor;
        this.copy = source.copy;
        this.dynamicUri = source.dynamicUri;
        this.onPrepare = source.onPrepare;
        this.executorService = source.executorService;
    }

    @Override
    public String getPattern() {
        return ExchangePattern.InOnly.name();
    }

    @Override
    public String toString() {
        return "WireTap[" + getUri() + "]";
    }

    @Override
    public String getShortName() {
        return "wireTap";
    }

    @Override
    public String getLabel() {
        return "wireTap[" + getUri() + "]";
    }

    @Override
    @SuppressWarnings("unchecked")
    public Type end() {
        // allow end() to return to previous type, so you can continue in the DSL
        return (Type) super.end();
    }

    @Override
    public void addOutput(ProcessorDefinition<?> output) {
        // add outputs on parent as this wiretap does not support outputs
        getParent().addOutput(output);
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Uses a custom thread pool
     *
     * @param  executorService a custom {@link ExecutorService} to use as thread pool for sending tapped exchanges
     * @return                 the builder
     */
    @Override
    public WireTapDefinition<Type> executorService(ExecutorService executorService) {
        this.executorServiceBean = executorService;
        return this;
    }

    /**
     * Uses a custom thread pool
     *
     * @param  executorService reference to lookup a custom {@link ExecutorService} to use as thread pool for sending
     *                         tapped exchanges
     * @return                 the builder
     */
    @Override
    public WireTapDefinition<Type> executorService(String executorService) {
        setExecutorService(executorService);
        return this;
    }

    /**
     * Uses a copy of the original exchange
     *
     * @return the builder
     */
    public WireTapDefinition<Type> copy() {
        return copy(true);
    }

    /**
     * Uses a copy of the original exchange
     *
     * @param  copy if it is true zwangine will copy the original exchange, if it is false zwangine will not copy the original
     *              exchange
     * @return      the builder
     */
    public WireTapDefinition<Type> copy(boolean copy) {
        return copy(Boolean.toString(copy));
    }

    /**
     * Uses a copy of the original exchange
     *
     * @param  copy if it is true zwangine will copy the original exchange, if it is false zwangine will not copy the original
     *              exchange
     * @return      the builder
     */
    public WireTapDefinition<Type> copy(String copy) {
        setCopy(copy);
        return this;
    }

    /**
     * Whether the uri is dynamic or static. If the uri is dynamic then the simple language is used to evaluate a
     * dynamic uri to use as the wire-tap destination, for each incoming message. This works similar to how the
     * <tt>toD</tt> EIP pattern works. If static then the uri is used as-is as the wire-tap destination.
     *
     * @param  dynamicUri whether to use dynamic or static uris
     * @return            the builder
     */
    public WireTapDefinition<Type> dynamicUri(boolean dynamicUri) {
        return dynamicUri(Boolean.toString(dynamicUri));
    }

    /**
     * Whether the uri is dynamic or static. If the uri is dynamic then the simple language is used to evaluate a
     * dynamic uri to use as the wire-tap destination, for each incoming message. This works similar to how the
     * <tt>toD</tt> EIP pattern works. If static then the uri is used as-is as the wire-tap destination.
     *
     * @param  dynamicUri whether to use dynamic or static uris
     * @return            the builder
     */
    public WireTapDefinition<Type> dynamicUri(String dynamicUri) {
        setDynamicUri(dynamicUri);
        return this;
    }

    /**
     * Uses the {@link Processor} when preparing the {@link org.zenithblox.Exchange} to be sent. This can be used to
     * deep-clone messages that should be sent, or any custom logic needed before the exchange is sent.
     *
     * @param  onPrepare the processor
     * @return           the builder
     */
    public WireTapDefinition<Type> onPrepare(Processor onPrepare) {
        this.onPrepareProcessor = onPrepare;
        return this;
    }

    /**
     * Uses the {@link Processor} when preparing the {@link org.zenithblox.Exchange} to be sent. This can be used to
     * deep-clone messages that should be sent, or any custom logic needed before the exchange is sent.
     *
     * @param  onPrepare reference to the processor to lookup in the {@link org.zenithblox.spi.Registry}
     * @return           the builder
     */
    public WireTapDefinition<Type> onPrepare(String onPrepare) {
        setOnPrepare(onPrepare);
        return this;
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers, when uris are reused.
     *
     * Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic
     * endpoint is unique then it's best to turn off caching by setting this to -1, which allows Zwangine to not cache both
     * the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use.
     * This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches.
     *
     * However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use
     * the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on
     * the default size (1000).
     *
     * If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help
     * reduce memory usage to avoid storing too many non-frequent used producers.
     *
     * @param  cacheSize the cache size, use <tt>0</tt> for default cache size, or <tt>-1</tt> to turn cache off.
     * @return           the builder
     */
    @Override
    public WireTapDefinition<Type> cacheSize(int cacheSize) {
        return cacheSize(Integer.toString(cacheSize));
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers, when uris are reused.
     *
     * Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic
     * endpoint is unique then it's best to turn off caching by setting this to -1, which allows Zwangine to not cache both
     * the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use.
     * This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches.
     *
     * However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use
     * the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on
     * the default size (1000).
     *
     * If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help
     * reduce memory usage to avoid storing too many non-frequent used producers.
     *
     * @param  cacheSize the cache size, use <tt>0</tt> for default cache size, or <tt>-1</tt> to turn cache off.
     * @return           the builder
     */
    @Override
    public WireTapDefinition<Type> cacheSize(String cacheSize) {
        setCacheSize(cacheSize);
        return this;
    }

    /**
     * Ignore the invalid endpoint exception when try to create a producer with that endpoint
     *
     * @return the builder
     */
    public WireTapDefinition<Type> ignoreInvalidEndpoint() {
        setIgnoreInvalidEndpoint(Boolean.toString(true));
        return this;
    }

    /**
     * To use a variable as the source for the message body to send. This makes it handy to use variables for user data
     * and to easily control what data to use for sending and receiving.
     *
     * Important: When using send variable then the message body is taken from this variable instead of the current
     * {@link Message}, however the headers from the {@link Message} will still be used as well. In other words, the
     * variable is used instead of the message body, but everything else is as usual.
     */
    public WireTapDefinition<Type> variableReceive(String variableReceive) {
        throw new IllegalArgumentException("WireTap does not support variableReceive");
    }

    /**
     * To use a variable as the source for the message body to send. This makes it handy to use variables for user data
     * and to easily control what data to use for sending and receiving.
     *
     * Important: When using send variable then the message body is taken from this variable instead of the current
     * message, however the headers from the message will still be used as well. In other words, the variable is used
     * instead of the message body, but everything else is as usual.
     */
    public WireTapDefinition<Type> variableSend(String variableSend) {
        setVariableSend(variableSend);
        return this;
    }

    // Properties
    // -------------------------------------------------------------------------

    public Processor getOnPrepareProcessor() {
        return onPrepareProcessor;
    }

    @Override
    public ExecutorService getExecutorServiceBean() {
        return executorServiceBean;
    }

    @Override
    public String getExecutorServiceRef() {
        return executorService;
    }

    @Override
    public String getUri() {
        return super.getUri();
    }

    /**
     * The uri of the endpoint to wiretap to. The uri can be dynamic computed using the simple language.
     */
    @Override
    public void setUri(String uri) {
        super.setUri(uri);
    }

    public String getCopy() {
        return copy;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }

    public String getDynamicUri() {
        return dynamicUri;
    }

    public void setDynamicUri(String dynamicUri) {
        this.dynamicUri = dynamicUri;
    }

    public String getOnPrepare() {
        return onPrepare;
    }

    public void setOnPrepare(String onPrepare) {
        this.onPrepare = onPrepare;
    }

    public String getExecutorService() {
        return executorService;
    }

    public void setExecutorService(String executorService) {
        this.executorService = executorService;
    }

    @Override
    public WireTapDefinition copyDefinition() {
        return new WireTapDefinition(this);
    }
}
