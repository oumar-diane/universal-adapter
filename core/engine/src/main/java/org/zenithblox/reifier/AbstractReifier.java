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
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.ExpressionSubElementDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.reifier.language.ExpressionReifier;
import org.zenithblox.spi.BeanRepository;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.EndpointHelper;
import org.zenithblox.util.ObjectHelper;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public abstract class AbstractReifier implements BeanRepository {

    protected final Workflow workflow;
    protected final ZwangineContext zwangineContext;

    public AbstractReifier(Workflow workflow) {
        this.workflow = ObjectHelper.notNull(workflow, "Workflow");
        this.zwangineContext = workflow.getZwangineContext();
    }

    public AbstractReifier(ZwangineContext zwangineContext) {
        this.workflow = null;
        this.zwangineContext = ObjectHelper.notNull(zwangineContext, "ZwangineContext");
    }

    protected ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    protected String parseString(String text) {
        return ZwangineContextHelper.parseText(zwangineContext, text);
    }

    protected Boolean parseBoolean(String text) {
        return ZwangineContextHelper.parseBoolean(zwangineContext, text);
    }

    protected boolean parseBoolean(String text, boolean def) {
        Boolean b = parseBoolean(text);
        return b != null ? b : def;
    }

    protected Long parseLong(String text) {
        return ZwangineContextHelper.parseLong(zwangineContext, text);
    }

    protected long parseLong(String text, long def) {
        Long l = parseLong(text);
        return l != null ? l : def;
    }

    protected Long parseDuration(String text) {
        Duration d = ZwangineContextHelper.parseDuration(zwangineContext, text);
        return d != null ? d.toMillis() : null;
    }

    protected long parseDuration(String text, long def) {
        Duration d = ZwangineContextHelper.parseDuration(zwangineContext, text);
        return d != null ? d.toMillis() : def;
    }

    protected Integer parseInt(String text) {
        return ZwangineContextHelper.parseInteger(zwangineContext, text);
    }

    protected int parseInt(String text, int def) {
        Integer i = parseInt(text);
        return i != null ? i : def;
    }

    protected Float parseFloat(String text) {
        return ZwangineContextHelper.parseFloat(zwangineContext, text);
    }

    protected float parseFloat(String text, float def) {
        Float f = parseFloat(text);
        return f != null ? f : def;
    }

    protected <T> T parse(Class<T> clazz, String text) {
        return ZwangineContextHelper.parse(zwangineContext, clazz, text);
    }

    protected <T> T parse(Class<T> clazz, Object text) {
        if (text instanceof String string) {
            text = parseString(string);
        }
        return ZwangineContextHelper.convertTo(zwangineContext, clazz, text);
    }

    protected Expression createExpression(ExpressionDefinition expression) {
        return ExpressionReifier.reifier(zwangineContext, expression).createExpression();
    }

    protected Expression createExpression(ExpressionSubElementDefinition expression) {
        return ExpressionReifier.reifier(zwangineContext, expression).createExpression();
    }

    protected Predicate createPredicate(ExpressionDefinition expression) {
        return ExpressionReifier.reifier(zwangineContext, expression).createPredicate();
    }

    protected Predicate createPredicate(ExpressionSubElementDefinition expression) {
        return ExpressionReifier.reifier(zwangineContext, expression).createPredicate();
    }

    protected Object or(Object a, Object b) {
        return a != null ? a : b;
    }

    protected Object asRef(String s) {
        return s != null ? s.startsWith("#") ? s : "#" + s : null;
    }

    protected BeanRepository getRegistry() {
        return zwangineContext.getRegistry();
    }

    public <T> T mandatoryLookup(String name, Class<T> type) {
        name = parseString(name);

        Object obj = lookupByNameAndType(name, type);
        if (obj == null) {
            throw new NoSuchBeanException(name, type.getName());
        }
        return type.cast(obj);
    }

    @Override
    public Object lookupByName(String name) {
        if (name == null) {
            return null;
        }
        name = parseString(name);

        if (EndpointHelper.isReferenceParameter(name)) {
            return EndpointHelper.resolveReferenceParameter(zwangineContext, name, Object.class, false);
        } else {
            return getRegistry().lookupByName(name);
        }
    }

    public <T> T lookupByNameAndType(String name, Class<T> type) {
        if (name == null) {
            return null;
        }
        name = parseString(name);

        if (EndpointHelper.isReferenceParameter(name)) {
            return EndpointHelper.resolveReferenceParameter(zwangineContext, name, type, false);
        } else {
            return getRegistry().lookupByNameAndType(name, type);
        }
    }

    @Override
    public <T> Map<String, T> findByTypeWithName(Class<T> type) {
        return getRegistry().findByTypeWithName(type);
    }

    @Override
    public <T> Set<T> findByType(Class<T> type) {
        return getRegistry().findByType(type);
    }

    @Override
    public Object unwrap(Object value) {
        return getRegistry().unwrap(value);
    }

    public Endpoint resolveEndpoint(String uri) throws NoSuchEndpointException {
        return ZwangineContextHelper.getMandatoryEndpoint(zwangineContext, uri);
    }

}
