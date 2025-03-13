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

import org.zenithblox.*;
import org.zenithblox.spi.Language;
import org.zenithblox.spi.ScriptingLanguage;
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.PropertyBindingSupport;
import org.zenithblox.support.ScriptHelper;
import org.zenithblox.util.StringHelper;
import org.zenithblox.util.function.Suppliers;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;

/**
 * Helper to create bean instances from bean model definitions.
 * <p/>
 * Creating beans is complex as Zwangine support many options such as constructor, factory beans, builder beans, scripts,
 * and much more. This helper hides this complexity for creating bean instances.
 */
public final class BeanModelHelper {

    private BeanModelHelper() {
    }

    /**
     * Creates a new bean.
     *
     * @param  def       the bean model
     * @param  context   the zwangine context
     * @return           the created bean instance
     * @throws Exception is thrown if error creating the bean
     */
    public static Object newInstance(BeanFactoryDefinition def, ZwangineContext context) throws Exception {
        Object target;

        String type = def.getType();
        if (!type.startsWith("#")) {
            type = "#class:" + type;
        }

        // script bean
        if (def.getScriptLanguage() != null && def.getScript() != null) {
            // create bean via the script
            final Language lan = context.resolveLanguage(def.getScriptLanguage());
            final ScriptingLanguage slan = lan instanceof ScriptingLanguage ? (ScriptingLanguage) lan : null;
            String fqn = def.getType();
            if (fqn.startsWith("#class:")) {
                fqn = fqn.substring(7);
            }
            final Class<?> clazz = context.getClassResolver().resolveMandatoryClass(fqn);
            if (slan != null) {
                // scripting language should be evaluated with context as binding
                Map<String, Object> bindings = new HashMap<>();
                bindings.put("context", context);
                target = slan.evaluate(def.getScript(), bindings, clazz);
            } else {
                Exchange dummy = ExchangeHelper.getDummy(context);
                String text = ScriptHelper.resolveOptionalExternalScript(context, dummy, def.getScript());
                Expression exp = lan.createExpression(text);
                target = exp.evaluate(dummy, clazz);
            }

            // a bean must be created
            if (target == null) {
                throw new NoSuchBeanException(def.getName(), "Creating bean using script returned null");
            }
        } else if (def.getBuilderClass() != null) {
            // builder class and method
            Class<?> clazz = context.getClassResolver().resolveMandatoryClass(def.getBuilderClass());
            Object builder = context.getInjector().newInstance(clazz);
            String bm = def.getBuilderMethod() != null ? def.getBuilderMethod() : "build";

            // create bean via builder and assign as target output
            target = PropertyBindingSupport.build()
                    .withZwangineContext(context)
                    .withTarget(builder)
                    .withRemoveParameters(true)
                    .withProperties(def.getProperties())
                    .build(Object.class, bm);
        } else {
            // factory bean/method
            if (def.getFactoryBean() != null && def.getFactoryMethod() != null) {
                type = type + "#" + def.getFactoryBean() + ":" + def.getFactoryMethod();
            } else if (def.getFactoryMethod() != null) {
                type = type + "#" + def.getFactoryMethod();
            }
            // property binding support has constructor arguments as part of the type
            StringJoiner ctr = new StringJoiner(", ");
            if (def.getConstructors() != null && !def.getConstructors().isEmpty()) {
                // need to sort constructor args based on index position
                Map<Integer, Object> sorted = new TreeMap<>(def.getConstructors());
                for (Object val : sorted.values()) {
                    String text = val.toString();
                    if (!StringHelper.isQuoted(text)) {
                        text = "\"" + text + "\"";
                    }
                    ctr.add(text);
                }
                type = type + "(" + ctr + ")";
            }

            target = PropertyBindingSupport.resolveBean(context, type);
        }

        // do not set properties when using #type as it uses an existing shared bean
        boolean setProps = !type.startsWith("#type");
        if (setProps) {
            // set optional properties on created bean
            if (def.getProperties() != null && !def.getProperties().isEmpty()) {
                PropertyBindingSupport.setPropertiesOnTarget(context, target, def.getProperties());
            }
        }

        return target;
    }

    /**
     * Creates and binds the bean to the workflow-template repository (local beans for kamelets).
     *
     * @param  def                  the bean factory to bind.
     * @param  workflowTemplateContext the context into which the bean factory should be bound.
     * @throws Exception            if an error occurs while trying to bind the bean factory
     */
    public static void bind(BeanFactoryDefinition<?> def, WorkflowTemplateContext workflowTemplateContext)
            throws Exception {

        final Map<String, Object> props = new HashMap<>();
        if (def.getProperties() != null) {
            props.putAll(def.getProperties());
        }
        if (def.getBeanSupplier() != null) {
            if (props.isEmpty()) {
                // bean class is optional for supplier
                if (def.getBeanClass() != null) {
                    workflowTemplateContext.bind(def.getName(), def.getBeanClass(), def.getBeanSupplier());
                } else {
                    workflowTemplateContext.bind(def.getName(), def.getBeanSupplier());
                }
            }
        } else if (def.getScript() != null && def.getScriptLanguage() != null) {
            final ZwangineContext zwangineContext = workflowTemplateContext.getZwangineContext();
            final Language lan = zwangineContext.resolveLanguage(def.getScriptLanguage());
            final Class<?> clazz;
            if (def.getBeanClass() != null) {
                clazz = def.getBeanClass();
            } else if (def.getType() != null) {
                String fqn = def.getType();
                if (fqn.contains(":")) {
                    fqn = StringHelper.after(fqn, ":");
                }
                clazz = zwangineContext.getClassResolver().resolveMandatoryClass(fqn);
            } else {
                clazz = Object.class;
            }
            final String script = def.getScript();
            final ScriptingLanguage slan = lan instanceof ScriptingLanguage ? (ScriptingLanguage) lan : null;
            if (slan != null) {
                // scripting language should be evaluated with workflow template context as binding
                // and memorize so the script is only evaluated once and the local bean is the same
                // if a workflow template refers to the local bean multiple times
                workflowTemplateContext.bind(def.getName(), clazz, Suppliers.memorize(() -> {
                    Object local;
                    Map<String, Object> bindings = new HashMap<>();
                    // use rtx as the short-hand name, as context would imply its ZwangineContext
                    bindings.put("rtc", workflowTemplateContext);
                    try {
                        local = slan.evaluate(script, bindings, Object.class);
                        if (!props.isEmpty()) {
                            PropertyBindingSupport.setPropertiesOnTarget(zwangineContext, local, props);
                        }
                        if (def.getInitMethod() != null) {
                            org.zenithblox.support.ObjectHelper.invokeMethodSafe(def.getInitMethod(), local);
                        }
                        if (def.getDestroyMethod() != null) {
                            workflowTemplateContext.registerDestroyMethod(def.getName(), def.getDestroyMethod());
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException(
                                "Cannot create bean: " + def.getType(), e);
                    }
                    return local;
                }));
            } else {
                // exchange based languages needs a dummy exchange to be evaluated
                // and memorize so the script is only evaluated once and the local bean is the same
                // if a workflow template refers to the local bean multiple times
                workflowTemplateContext.bind(def.getName(), clazz, Suppliers.memorize(() -> {
                    try {
                        Exchange dummy = ExchangeHelper.getDummy(zwangineContext);
                        String text = ScriptHelper.resolveOptionalExternalScript(zwangineContext, dummy, script);
                        if (text != null) {
                            Expression exp = lan.createExpression(text);
                            Object local = exp.evaluate(dummy, clazz);
                            if (!props.isEmpty()) {
                                PropertyBindingSupport.setPropertiesOnTarget(zwangineContext, local, props);
                            }
                            if (def.getInitMethod() != null) {
                                try {
                                    org.zenithblox.support.ObjectHelper.invokeMethodSafe(def.getInitMethod(), local);
                                } catch (Exception e) {
                                    throw RuntimeZwangineException.wrapRuntimeException(e);
                                }
                            }
                            if (def.getDestroyMethod() != null) {
                                workflowTemplateContext.registerDestroyMethod(def.getName(), def.getDestroyMethod());
                            }
                            return local;
                        } else {
                            return null;
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException(
                                "Cannot create bean: " + def.getType(), e);
                    }
                }));
            }
        } else if (def.getBeanClass() != null || def.getType() != null) {
            String type = def.getType();
            if (type == null) {
                type = def.getBeanClass().getName();
            }
            if (!type.startsWith("#")) {
                type = "#class:" + type;
            }
            // factory bean/method
            if (def.getFactoryBean() != null && def.getFactoryMethod() != null) {
                type = type + "#" + def.getFactoryBean() + ":" + def.getFactoryMethod();
            } else if (def.getFactoryMethod() != null) {
                type = type + "#" + def.getFactoryMethod();
            }
            // property binding support has constructor arguments as part of the type
            StringJoiner ctr = new StringJoiner(", ");
            if (def.getConstructors() != null && !def.getConstructors().isEmpty()) {
                // need to sort constructor args based on index position
                Map<Integer, Object> sorted = new TreeMap<>(def.getConstructors());
                for (Object val : sorted.values()) {
                    String text = val.toString();
                    if (!StringHelper.isQuoted(text)) {
                        text = "\"" + text + "\"";
                    }
                    ctr.add(text);
                }
                type = type + "(" + ctr + ")";
            }
            final String classType = type;

            final ZwangineContext zwangineContext = workflowTemplateContext.getZwangineContext();
            workflowTemplateContext.bind(def.getName(), Object.class, Suppliers.memorize(() -> {
                try {
                    Object local = PropertyBindingSupport.resolveBean(zwangineContext, classType);

                    // do not set properties when using #type as it uses an existing shared bean
                    boolean setProps = !classType.startsWith("#type");
                    if (setProps) {
                        // set optional properties on created bean
                        if (def.getProperties() != null && !def.getProperties().isEmpty()) {
                            PropertyBindingSupport.setPropertiesOnTarget(zwangineContext, local, def.getProperties());
                        }
                    }
                    if (def.getInitMethod() != null) {
                        org.zenithblox.support.ObjectHelper.invokeMethodSafe(def.getInitMethod(), local);
                    }
                    if (def.getDestroyMethod() != null) {
                        workflowTemplateContext.registerDestroyMethod(def.getName(), def.getDestroyMethod());
                    }
                    return local;
                } catch (Exception e) {
                    throw new IllegalStateException(
                            "Cannot create bean: " + def.getType(), e);
                }
            }));
        } else {
            // invalid syntax for the local bean, so lets report an exception
            throw new IllegalArgumentException(
                    "Workflow template local bean: " + def.getName() + " has invalid type syntax: " + def.getType()
                                               + ". To refer to a class then prefix the value with #class such as: #class:fullyQualifiedClassName");
        }
    }
}
