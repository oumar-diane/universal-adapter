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
import org.zenithblox.spi.IdempotentRepository;
import org.zenithblox.spi.Metadata;

/**
 * Filters out duplicate messages
 */
@Metadata(label = "eip,routing")
public class IdempotentConsumerDefinition extends OutputExpressionNode {

    private IdempotentRepository idempotentRepositoryBean;

    @Metadata(javaType = "org.zenithblox.spi.IdempotentRepository")
    private String idempotentRepository;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private String eager;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private String completionEager;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private String skipDuplicate;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "true")
    private String removeOnFailure;

    public IdempotentConsumerDefinition() {
    }

    protected IdempotentConsumerDefinition(IdempotentConsumerDefinition source) {
        super(source);
        this.idempotentRepositoryBean = source.idempotentRepositoryBean;
        this.idempotentRepository = source.idempotentRepository;
        this.eager = source.eager;
        this.completionEager = source.completionEager;
        this.skipDuplicate = source.skipDuplicate;
        this.removeOnFailure = source.removeOnFailure;
    }

    public IdempotentConsumerDefinition(Expression messageIdExpression, IdempotentRepository idempotentRepository) {
        super(messageIdExpression);
        this.idempotentRepositoryBean = idempotentRepository;
    }

    @Override
    public IdempotentConsumerDefinition copyDefinition() {
        return new IdempotentConsumerDefinition(this);
    }

    @Override
    public String toString() {
        return "IdempotentConsumer[" + getExpression() + " -> " + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "idempotentConsumer";
    }

    @Override
    public String getLabel() {
        return "idempotentConsumer[" + getExpression() + "]";
    }

    // Fluent API
    // -------------------------------------------------------------------------

    /**
     * Sets the reference name of the message id repository
     *
     * @param  ref the reference name of message id repository
     * @return     builder
     */
    public IdempotentConsumerDefinition idempotentRepository(String ref) {
        setIdempotentRepository(ref);
        return this;
    }

    /**
     * Sets the message id repository for the idempotent consumer
     *
     * @param  idempotentRepository the repository instance of idempotent
     * @return                      builder
     */
    public IdempotentConsumerDefinition idempotentRepository(IdempotentRepository idempotentRepository) {
        this.idempotentRepositoryBean = idempotentRepository;
        return this;
    }

    /**
     * Sets whether to eagerly add the key to the idempotent repository or wait until the exchange is complete. Eager is
     * default enabled.
     *
     * @param  eager <tt>true</tt> to add the key before processing, <tt>false</tt> to wait until the exchange is
     *               complete.
     * @return       builder
     */
    public IdempotentConsumerDefinition eager(boolean eager) {
        setEager(Boolean.toString(eager));
        return this;
    }

    /**
     * Sets whether to complete the idempotent consumer eager or when the exchange is done.
     * <p/>
     * If this option is <tt>true</tt> to complete eager, then the idempotent consumer will trigger its completion when
     * the exchange reached the end of the block of the idempotent consumer pattern. So if the exchange is continued
     * workflowd after the block ends, then whatever happens there does not affect the state.
     * <p/>
     * If this option is <tt>false</tt> (default) to <b>not</b> complete eager, then the idempotent consumer will
     * complete when the exchange is done being workflowd. So if the exchange is continued workflowd after the block ends,
     * then whatever happens there <b>also</b> affect the state. For example if the exchange failed due to an exception,
     * then the state of the idempotent consumer will be a rollback.
     *
     * @param  completionEager whether to complete eager or complete when the exchange is done
     * @return                 builder
     */
    public IdempotentConsumerDefinition completionEager(boolean completionEager) {
        setCompletionEager(Boolean.toString(completionEager));
        return this;
    }

    /**
     * Sets whether to remove or keep the key on failure.
     * <p/>
     * The default behavior is to remove the key on failure.
     *
     * @param  removeOnFailure <tt>true</tt> to remove the key, <tt>false</tt> to keep the key if the exchange fails.
     * @return                 builder
     */
    public IdempotentConsumerDefinition removeOnFailure(boolean removeOnFailure) {
        setRemoveOnFailure(Boolean.toString(removeOnFailure));
        return this;
    }

    /**
     * Sets whether to skip duplicates or not.
     * <p/>
     * The default behavior is to skip duplicates.
     * <p/>
     * A duplicate message would have the Exchange property {@link org.zenithblox.Exchange#DUPLICATE_MESSAGE} set to a
     * {@link Boolean#TRUE} value. A none duplicate message will not have this property set.
     *
     * @param  skipDuplicate <tt>true</tt> to skip duplicates, <tt>false</tt> to allow duplicates.
     * @return               builder
     */
    public IdempotentConsumerDefinition skipDuplicate(boolean skipDuplicate) {
        setSkipDuplicate(Boolean.toString(skipDuplicate));
        return this;
    }

    /**
     * Expression used to calculate the correlation key to use for duplicate check. The Exchange which has the same
     * correlation key is regarded as a duplicate and will be rejected.
     */
    @Override
    public void setExpression(ExpressionDefinition expression) {
        // override to include javadoc what the expression is used for
        super.setExpression(expression);
    }

    public IdempotentRepository getIdempotentRepositoryBean() {
        return idempotentRepositoryBean;
    }

    public String getIdempotentRepository() {
        return idempotentRepository;
    }

    public void setIdempotentRepository(String idempotentRepository) {
        this.idempotentRepository = idempotentRepository;
    }

    public void setIdempotentRepository(IdempotentRepository idempotentRepository) {
        this.idempotentRepositoryBean = idempotentRepository;
    }

    public String getEager() {
        return eager;
    }

    public void setEager(String eager) {
        this.eager = eager;
    }

    public String getSkipDuplicate() {
        return skipDuplicate;
    }

    public void setSkipDuplicate(String skipDuplicate) {
        this.skipDuplicate = skipDuplicate;
    }

    public String getRemoveOnFailure() {
        return removeOnFailure;
    }

    public void setRemoveOnFailure(String removeOnFailure) {
        this.removeOnFailure = removeOnFailure;
    }

    public String getCompletionEager() {
        return completionEager;
    }

    public void setCompletionEager(String completionEager) {
        this.completionEager = completionEager;
    }

}
