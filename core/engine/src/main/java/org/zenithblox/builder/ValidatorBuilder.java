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
package org.zenithblox.builder;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Expression;
import org.zenithblox.Predicate;
import org.zenithblox.model.ModelZwangineContext;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.model.validator.CustomValidatorDefinition;
import org.zenithblox.model.validator.EndpointValidatorDefinition;
import org.zenithblox.model.validator.PredicateValidatorDefinition;
import org.zenithblox.model.validator.ValidatorDefinition;
import org.zenithblox.spi.AsPredicate;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Validator;

/**
 * A <a href="http://zwangine.zentihblox.org/dsl.html">Java DSL</a> which is used to build a
 * {@link org.zenithblox.spi.Validator} and register into {@link org.zenithblox.ZwangineContext}. It requires a 'type'
 * to be specified by type() method. And then you can choose a type of validator by withUri(), withPredicate(),
 * withJava() or withBean() method.
 */
public class ValidatorBuilder {

    private String type;
    private String uri;
    private ExpressionDefinition expression;
    private Class<? extends Validator> clazz;
    private String beanRef;

    /**
     * Set the data type name. If you specify 'xml:XYZ', the validator will be picked up if source type is 'xml:XYZ'. If
     * you specify just 'xml', the validator matches with all of 'xml' source type like 'xml:ABC' or 'xml:DEF'.
     *
     * @param type 'from' data type name
     */
    public ValidatorBuilder type(String type) {
        this.type = type;
        return this;
    }

    /**
     * Set the data type using Java class.
     *
     * @param type Java class represents data type
     */
    public ValidatorBuilder type(Class<?> type) {
        this.type = new DataType(type).toString();
        return this;
    }

    /**
     * Set the URI to be used for the endpoint {@link Validator}.
     *
     * @see       EndpointValidatorDefinition, ProcessorValidator
     * @param uri endpoint URI
     */
    public ValidatorBuilder withUri(String uri) {
        resetType();
        this.uri = uri;
        return this;
    }

    /**
     * Set the {@link Expression} to be used for the predicate {@link Validator}.
     *
     * @see              PredicateValidatorDefinition, ProcessorValidator
     * @param expression validation expression
     */
    public ValidatorBuilder withExpression(@AsPredicate Expression expression) {
        resetType();
        this.expression = new ExpressionDefinition(expression);
        return this;
    }

    /**
     * Set the {@link Predicate} to be used for the predicate {@link Validator}.
     *
     * @see             PredicateValidatorDefinition, ProcessorValidator
     * @param predicate validation predicate
     */
    public ValidatorBuilder withExpression(@AsPredicate Predicate predicate) {
        resetType();
        this.expression = new ExpressionDefinition(predicate);
        return this;
    }

    /**
     * Set the Java {@code Class} represents a custom {@code Validator} implementation class.
     *
     * @see         CustomValidatorDefinition
     * @param clazz {@code Class} object represents custom validator implementation
     */
    public ValidatorBuilder withJava(Class<? extends Validator> clazz) {
        resetType();
        this.clazz = clazz;
        return this;
    }

    /**
     * Set the Java Bean name to be used for custom {@code Validator}.
     *
     * @see       CustomValidatorDefinition
     * @param ref bean name for the custom {@code Validator}
     */
    public ValidatorBuilder withBean(String ref) {
        resetType();
        this.beanRef = ref;
        return this;
    }

    private void resetType() {
        this.uri = null;
        this.expression = null;
        this.clazz = null;
        this.beanRef = null;
    }

    /**
     * Configures a new Validator according to the configurations built on this builder and register it into the given
     * {@code ZwangineContext}.
     *
     * @param zwangineContext the given ZwangineContext
     */
    public void configure(ZwangineContext zwangineContext) {
        ValidatorDefinition validator;
        if (uri != null) {
            EndpointValidatorDefinition etd = new EndpointValidatorDefinition();
            etd.setUri(uri);
            validator = etd;
        } else if (expression != null) {
            PredicateValidatorDefinition dtd = new PredicateValidatorDefinition();
            dtd.setExpression(expression);
            validator = dtd;
        } else if (clazz != null) {
            CustomValidatorDefinition ctd = new CustomValidatorDefinition();
            ctd.setClassName(clazz.getName());
            validator = ctd;
        } else if (beanRef != null) {
            CustomValidatorDefinition ctd = new CustomValidatorDefinition();
            ctd.setRef(beanRef);
            validator = ctd;
        } else {
            throw new IllegalArgumentException("No Validator type was specified");
        }

        // force init of validator registry
        zwangineContext.getValidatorRegistry();

        validator.setType(type);
        ((ModelZwangineContext) zwangineContext).registerValidator(validator);
    }
}
