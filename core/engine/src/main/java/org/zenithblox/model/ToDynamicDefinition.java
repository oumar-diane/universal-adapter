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
import org.zenithblox.builder.EndpointProducerBuilder;
import org.zenithblox.spi.AsEndpointUri;
import org.zenithblox.spi.Metadata;

/**
 * Sends the message to a dynamic endpoint
 */
@Metadata(label = "eip,routing")
public class ToDynamicDefinition extends NoOutputDefinition<ToDynamicDefinition> {

    protected EndpointProducerBuilder endpointProducerBuilder;

    @Metadata(required = true)
    private String uri;
    private String variableSend;
    private String variableReceive;
    @Metadata(label = "advanced", javaType = "org.zenithblox.ExchangePattern", enums = "InOnly,InOut")
    private String pattern;
    @Metadata(label = "advanced", javaType = "java.lang.Integer")
    private String cacheSize;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String ignoreInvalidEndpoint;
    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String allowOptimisedComponents;
    @Metadata(label = "advanced", defaultValue = "true", javaType = "java.lang.Boolean")
    private String autoStartComponents;

    public ToDynamicDefinition() {
    }

    public ToDynamicDefinition(String uri) {
        this.uri = uri;
    }

    protected ToDynamicDefinition(ToDynamicDefinition source) {
        super(source);
        this.endpointProducerBuilder = source.endpointProducerBuilder;
        this.uri = source.uri;
        this.variableSend = source.variableSend;
        this.variableReceive = source.variableReceive;
        this.pattern = source.pattern;
        this.cacheSize = source.cacheSize;
        this.ignoreInvalidEndpoint = source.ignoreInvalidEndpoint;
        this.allowOptimisedComponents = source.allowOptimisedComponents;
        this.autoStartComponents = source.autoStartComponents;
    }

    @Override
    public String getShortName() {
        return "toD";
    }

    @Override
    public String toString() {
        return "DynamicTo[" + getLabel() + "]";
    }

    @Override
    public String getLabel() {
        return uri;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * The uri of the endpoint to send to. The uri can be dynamic computed using the
     * {@link org.zenithblox.language.simple.SimpleLanguage} expression.
     */
    public ToDynamicDefinition uri(@AsEndpointUri String uri) {
        setUri(uri);
        return this;
    }

    /**
     * The uri of the endpoint to send to.
     *
     * @param endpointProducerBuilder the dynamic endpoint to send to (resolved using simple language by default)
     */
    public ToDynamicDefinition uri(@AsEndpointUri EndpointProducerBuilder endpointProducerBuilder) {
        setEndpointProducerBuilder(endpointProducerBuilder);
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
    public ToDynamicDefinition variableReceive(String variableReceive) {
        setVariableReceive(variableReceive);
        return this;
    }

    /**
     * To use a variable as the source for the message body to send. This makes it handy to use variables for user data
     * and to easily control what data to use for sending and receiving.
     *
     * Important: When using send variable then the message body is taken from this variable instead of the current
     * message, however the headers from the message will still be used as well. In other words, the variable is used
     * instead of the message body, but everything else is as usual.
     */
    public ToDynamicDefinition variableSend(String variableSend) {
        setVariableSend(variableSend);
        return this;
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ToDynamicDefinition pattern(ExchangePattern pattern) {
        return pattern(pattern.name());
    }

    /**
     * Sets the optional {@link ExchangePattern} used to invoke this endpoint
     */
    public ToDynamicDefinition pattern(String pattern) {
        setPattern(pattern);
        return this;
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers when using this recipient list, when uris are reused.
     *
     * Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic
     * endpoint is unique then its best to turn off caching by setting this to -1, which allows Zwangine to not cache both
     * the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use.
     * This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches.
     *
     * However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use
     * the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on
     * the default size (1000).
     *
     * If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help
     * reduce memory usage to avoid storing too many non frequent used producers.
     *
     * @param  cacheSize the cache size, use <tt>0</tt> for default cache size, or <tt>-1</tt> to turn cache off.
     * @return           the builder
     */
    public ToDynamicDefinition cacheSize(int cacheSize) {
        return cacheSize(Integer.toString(cacheSize));
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers when using this recipient list, when uris are reused.
     *
     * Beware that when using dynamic endpoints then it affects how well the cache can be utilized. If each dynamic
     * endpoint is unique then its best to turn off caching by setting this to -1, which allows Zwangine to not cache both
     * the producers and endpoints; they are regarded as prototype scoped and will be stopped and discarded after use.
     * This reduces memory usage as otherwise producers/endpoints are stored in memory in the caches.
     *
     * However if there are a high degree of dynamic endpoints that have been used before, then it can benefit to use
     * the cache to reuse both producers and endpoints and therefore the cache size can be set accordingly or rely on
     * the default size (1000).
     *
     * If there is a mix of unique and used before dynamic endpoints, then setting a reasonable cache size can help
     * reduce memory usage to avoid storing too many non frequent used producers.
     *
     * @param  cacheSize the cache size, use <tt>0</tt> for default cache size, or <tt>-1</tt> to turn cache off.
     * @return           the builder
     */
    public ToDynamicDefinition cacheSize(String cacheSize) {
        setCacheSize(cacheSize);
        return this;
    }

    /**
     * Whether to ignore invalid endpoint URIs and skip sending the message.
     */
    public ToDynamicDefinition ignoreInvalidEndpoint(boolean ignoreInvalidEndpoint) {
        return ignoreInvalidEndpoint(Boolean.toString(ignoreInvalidEndpoint));
    }

    /**
     * Whether to ignore invalid endpoint URIs and skip sending the message.
     */
    public ToDynamicDefinition ignoreInvalidEndpoint(String ignoreInvalidEndpoint) {
        setIgnoreInvalidEndpoint(ignoreInvalidEndpoint);
        return this;
    }

    /**
     * Whether to allow components to optimise toD if they are {@link org.zenithblox.spi.SendDynamicAware}.
     *
     * @return the builder
     */
    public ToDynamicDefinition allowOptimisedComponents(boolean allowOptimisedComponents) {
        return allowOptimisedComponents(Boolean.toString(allowOptimisedComponents));
    }

    /**
     * Whether to allow components to optimise toD if they are {@link org.zenithblox.spi.SendDynamicAware}.
     *
     * @return the builder
     */
    public ToDynamicDefinition allowOptimisedComponents(String allowOptimisedComponents) {
        setAllowOptimisedComponents(allowOptimisedComponents);
        return this;
    }

    /**
     * Whether to auto startup components when toD is starting up.
     *
     * @return the builder
     */
    public ToDynamicDefinition autoStartComponents(String autoStartComponents) {
        setAutoStartComponents(autoStartComponents);
        return this;
    }

    // Properties
    // -------------------------------------------------------------------------

    public String getUri() {
        return uri;
    }

    /**
     * The uri of the endpoint to send to. The uri can be dynamic computed using the
     * {@link org.zenithblox.language.simple.SimpleLanguage} expression.
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    public EndpointProducerBuilder getEndpointProducerBuilder() {
        return endpointProducerBuilder;
    }

    public void setEndpointProducerBuilder(EndpointProducerBuilder endpointProducerBuilder) {
        this.endpointProducerBuilder = endpointProducerBuilder;
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

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    public String getIgnoreInvalidEndpoint() {
        return ignoreInvalidEndpoint;
    }

    public void setIgnoreInvalidEndpoint(String ignoreInvalidEndpoint) {
        this.ignoreInvalidEndpoint = ignoreInvalidEndpoint;
    }

    public String getAllowOptimisedComponents() {
        return allowOptimisedComponents;
    }

    public void setAllowOptimisedComponents(String allowOptimisedComponents) {
        this.allowOptimisedComponents = allowOptimisedComponents;
    }

    public String getAutoStartComponents() {
        return autoStartComponents;
    }

    public void setAutoStartComponents(String autoStartComponents) {
        this.autoStartComponents = autoStartComponents;
    }

    public ToDynamicDefinition copyDefinition() {
        return new ToDynamicDefinition(this);
    }
}
