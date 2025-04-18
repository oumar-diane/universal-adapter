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

import org.zenithblox.model.language.*;

/**
 * {@code LanguageBuilderFactory} is a factory class of builder of all supported languages.
 */
public final class LanguageBuilderFactory {

    /**
     * Uses the Constant language
     */
    public ConstantExpression.Builder constant() {
        return new ConstantExpression.Builder();
    }

    /**
     * Uses the Constant language
     */
    public ConstantExpression.Builder constant(Object value) {
        var builder = constant();
        builder.value(value);
        return builder;
    }

    /**
     * Uses the CSimple language
     */
    public CSimpleExpression.Builder csimple() {
        return new CSimpleExpression.Builder();
    }

    /**
     * Uses the CSimple language
     */
    public CSimpleExpression.Builder csimple(String expression) {
        var builder = csimple();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Datasonnet language
     */
    public DatasonnetExpression.Builder datasonnet() {
        return new DatasonnetExpression.Builder();
    }

    /**
     * Uses the Datasonnet language
     */
    public DatasonnetExpression.Builder datasonnet(String expression) {
        var builder = datasonnet();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the ExchangeProperty language
     */
    public ExchangePropertyExpression.Builder exchangeProperty() {
        return new ExchangePropertyExpression.Builder();
    }

    /**
     * Uses the ExchangeProperty language
     */
    public ExchangePropertyExpression.Builder exchangeProperty(String name) {
        var builder = exchangeProperty();
        builder.expression(name);
        return builder;
    }

    /**
     * Uses the Groovy language
     */
    public GroovyExpression.Builder groovy() {
        return new GroovyExpression.Builder();
    }

    /**
     * Uses the Groovy language
     */
    public GroovyExpression.Builder groovy(String expression) {
        var builder = groovy();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Header language
     */
    public HeaderExpression.Builder header() {
        return new HeaderExpression.Builder();
    }

    /**
     * Uses the Header language
     */
    public HeaderExpression.Builder header(String name) {
        var builder = header();
        builder.expression(name);
        return builder;
    }

    /**
     * Uses the Hl7Terser language
     */
    public Hl7TerserExpression.Builder hl7terser() {
        return new Hl7TerserExpression.Builder();
    }

    /**
     * Uses the Hl7Terser language
     */
    public Hl7TerserExpression.Builder hl7terser(String expression) {
        var builder = hl7terser();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the JavaScript language
     */
    public JavaScriptExpression.Builder js() {
        return new JavaScriptExpression.Builder();
    }

    /**
     * Uses the JavaScript language
     */
    public JavaScriptExpression.Builder js(String expression) {
        var builder = js();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Java language
     */
    public JavaExpression.Builder java() {
        return new JavaExpression.Builder();
    }

    /**
     * Uses the Java language
     */
    public JavaExpression.Builder java(String expression) {
        var builder = java();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the JOOR language
     */
    @Deprecated(since = "4.3.0")
    public JoorExpression.Builder joor() {
        return new JoorExpression.Builder();
    }

    /**
     * Uses the JQ language
     */
    public JqExpression.Builder jq() {
        return new JqExpression.Builder();
    }

    /**
     * Uses the JQ language
     */
    public JqExpression.Builder jq(String expression) {
        var builder = jq();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the JsonPath language
     */
    public JsonPathExpression.Builder jsonpath() {
        return new JsonPathExpression.Builder();
    }

    /**
     * Uses the JsonPath language
     */
    public JsonPathExpression.Builder jsonpath(String expression) {
        var builder = jsonpath();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses a custom language
     */
    public LanguageExpression.Builder language() {
        return new LanguageExpression.Builder();
    }

    /**
     * Uses a custom language
     */
    public LanguageExpression.Builder language(String language, String expression) {
        var builder = language();
        builder.language(language);
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the bean language
     */
    public MethodCallExpression.Builder bean() {
        return new MethodCallExpression.Builder();
    }

    /**
     * Uses the bean language
     */
    public MethodCallExpression.Builder bean(String ref) {
        var builder = bean();
        builder.ref(ref);
        return builder;
    }

    /**
     * Uses the bean language
     */
    public MethodCallExpression.Builder bean(Class<?> beanType) {
        var builder = bean();
        builder.beanType(beanType);
        return builder;
    }

    /**
     * Uses the bean language
     */
    public MethodCallExpression.Builder bean(Object instance) {
        var builder = bean();
        builder.instance(instance);
        return builder;
    }

    /**
     * Uses the MVEL language
     */
    public MvelExpression.Builder mvel() {
        return new MvelExpression.Builder();
    }

    /**
     * Uses the MVEL language
     */
    public MvelExpression.Builder mvel(String expression) {
        var builder = mvel();
        builder.expression(expression);
        return builder;

    }

    /**
     * Uses the OGNL language
     */
    public OgnlExpression.Builder ognl() {
        return new OgnlExpression.Builder();
    }

    /**
     * Uses the OGNL language
     */
    public OgnlExpression.Builder ognl(String expression) {
        var builder = ognl();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Python language
     */
    public PythonExpression.Builder python() {
        return new PythonExpression.Builder();
    }

    /**
     * Uses the Python language
     */
    public PythonExpression.Builder python(String expression) {
        var builder = python();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Ref language
     */
    public RefExpression.Builder ref() {
        return new RefExpression.Builder();
    }

    /**
     * Uses the Ref language
     */
    public RefExpression.Builder ref(String ref) {
        var builder = ref();
        builder.expression(ref);
        return builder;
    }

    /**
     * Uses the Simple language
     */
    public SimpleExpression.Builder simple() {
        return new SimpleExpression.Builder();
    }

    /**
     * Uses the Simple language
     */
    public SimpleExpression.Builder simple(String expression) {
        var builder = simple();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the SpEL language
     */
    public SpELExpression.Builder spel() {
        return new SpELExpression.Builder();
    }

    /**
     * Uses the SpEL language
     */
    public SpELExpression.Builder spel(String expression) {
        var builder = spel();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Tokenizer language
     */
    public TokenizerExpression.Builder tokenize() {
        return new TokenizerExpression.Builder();
    }

    /**
     * Uses the Tokenizer language
     */
    public TokenizerExpression.Builder tokenize(String expression) {
        var builder = tokenize();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Variable language
     */
    public VariableExpression.Builder variable() {
        return new VariableExpression.Builder();
    }

    /**
     * Uses the Variable language
     */
    public VariableExpression.Builder variable(String name) {
        var builder = variable();
        builder.expression(name);
        return builder;
    }

    /**
     * Uses the XMLTokenizer language
     */
    public XMLTokenizerExpression.Builder xtokenize() {
        return new XMLTokenizerExpression.Builder();
    }

    /**
     * Uses the XMLTokenizer language
     */
    public XMLTokenizerExpression.Builder xtokenize(String expression) {
        var builder = xtokenize();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the XPath language
     */
    public XPathExpression.Builder xpath() {
        return new XPathExpression.Builder();
    }

    /**
     * Uses the XPath language
     */
    public XPathExpression.Builder xpath(String expression) {
        var builder = xpath();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the XQuery language
     */
    public XQueryExpression.Builder xquery() {
        return new XQueryExpression.Builder();
    }

    /**
     * Uses the XQuery language
     */
    public XQueryExpression.Builder xquery(String expression) {
        var builder = xquery();
        builder.expression(expression);
        return builder;
    }

    /**
     * Uses the Wasm language
     */
    public WasmExpression.Builder wasm() {
        return new WasmExpression.Builder();
    }

    /**
     * Uses the Wasm language
     */
    public WasmExpression.Builder wasm(String module, String expression) {
        var builder = wasm();
        builder.module(module);
        builder.expression(expression);
        return builder;
    }
}
