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

import org.zenithblox.Expression;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Metadata;

import java.util.Collections;
import java.util.List;

/**
 * Workflow messages based on dynamic rules
 */
@Metadata(label = "eip,routing")
public class DynamicWorkflowrDefinition<Type extends ProcessorDefinition<Type>> extends ExpressionNode {

    public static final String DEFAULT_DELIMITER = ",";

    @Metadata(defaultValue = ",")
    private String uriDelimiter;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean")
    private String ignoreInvalidEndpoints;
    @Metadata(label = "advanced", javaType = "java.lang.Integer")
    private String cacheSize;

    public DynamicWorkflowrDefinition() {
    }

    protected DynamicWorkflowrDefinition(DynamicWorkflowrDefinition source) {
        super(source);
        this.uriDelimiter = source.uriDelimiter;
        this.ignoreInvalidEndpoints = source.ignoreInvalidEndpoints;
        this.cacheSize = source.cacheSize;
    }

    public DynamicWorkflowrDefinition(Expression expression) {
        super(expression);
    }

    @Override
    public DynamicWorkflowrDefinition copyDefinition() {
        return new DynamicWorkflowrDefinition(this);
    }

    @Override
    public String toString() {
        return "DynamicWorkflowr[" + getExpression() + "]";
    }

    @Override
    public String getShortName() {
        return "dynamicWorkflowr";
    }

    @Override
    public String getLabel() {
        return "dynamicWorkflowr[" + getExpression() + "]";
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return Collections.emptyList();
    }

    /**
     * Expression to call that returns the endpoint(s) to workflow to in the dynamic routing.
     * <p/>
     * <b>Important:</b> The expression will be called in a while loop fashion, until the expression returns
     * <tt>null</tt> which means the dynamic workflowr is finished.
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    public void setUriDelimiter(String uriDelimiter) {
        this.uriDelimiter = uriDelimiter;
    }

    public String getUriDelimiter() {
        return uriDelimiter;
    }

    public void setIgnoreInvalidEndpoints(String ignoreInvalidEndpoints) {
        this.ignoreInvalidEndpoints = ignoreInvalidEndpoints;
    }

    public String getIgnoreInvalidEndpoints() {
        return ignoreInvalidEndpoints;
    }

    // Fluent API
    // -------------------------------------------------------------------------

    public String getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(String cacheSize) {
        this.cacheSize = cacheSize;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Type end() {
        // allow end() to return to previous type so you can continue in the DSL
        return (Type) super.end();
    }

    /**
     * Ignore the invalidate endpoint exception when try to create a producer with that endpoint
     *
     * @return the builder
     */
    public DynamicWorkflowrDefinition<Type> ignoreInvalidEndpoints() {
        setIgnoreInvalidEndpoints(Boolean.toString(true));
        return this;
    }

    /**
     * Sets the uri delimiter to use
     *
     * @param  uriDelimiter the delimiter
     * @return              the builder
     */
    public DynamicWorkflowrDefinition<Type> uriDelimiter(String uriDelimiter) {
        setUriDelimiter(uriDelimiter);
        return this;
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers when using this dynamic workflowr, when uris are reused.
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
    public DynamicWorkflowrDefinition<Type> cacheSize(int cacheSize) {
        setCacheSize(Integer.toString(cacheSize));
        return this;
    }

    /**
     * Sets the maximum size used by the {@link org.zenithblox.spi.ProducerCache} which is used to cache and reuse
     * producers when using this dynamic workflowr, when uris are reused.
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
    public DynamicWorkflowrDefinition<Type> cacheSize(String cacheSize) {
        setCacheSize(cacheSize);
        return this;
    }

}
