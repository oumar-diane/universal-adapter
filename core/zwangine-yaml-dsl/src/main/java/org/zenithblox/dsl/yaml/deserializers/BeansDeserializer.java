/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.dsl.yaml.deserializers;

import org.zenithblox.ZwangineContext;
import org.zenithblox.dsl.yaml.common.YamlDeserializationContext;
import org.zenithblox.dsl.yaml.common.YamlDeserializerResolver;
import org.zenithblox.dsl.yaml.common.YamlDeserializerSupport;
import org.zenithblox.model.BeanFactoryDefinition;
import org.zenithblox.model.BeanModelHelper;
import org.zenithblox.model.Model;
import org.zenithblox.spi.ZwangineContextCustomizer;
import org.zenithblox.spi.annotations.YamlIn;
import org.zenithblox.spi.annotations.YamlProperty;
import org.zenithblox.spi.annotations.YamlType;
import org.zenithblox.util.ObjectHelper;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.SequenceNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@YamlIn
@YamlType(
          nodes = "beans",
          order = YamlDeserializerResolver.ORDER_DEFAULT,
          properties = {
                  @YamlProperty(name = "__extends",
                                type = "array:org.zenithblox.model.BeanFactoryDefinition")
          })
public class BeansDeserializer extends YamlDeserializerSupport implements ConstructNode {

    private final Set<String> beanCache = new HashSet<>();

    @Override
    public Object construct(Node node) {
        final BeansCustomizer answer = new BeansCustomizer();
        final SequenceNode sn = asSequenceNode(node);
        final YamlDeserializationContext dc = getDeserializationContext(node);

        for (Node item : sn.getValue()) {
            setDeserializationContext(item, dc);

            BeanFactoryDefinition<?> bean = asType(item, BeanFactoryDefinition.class);
            if (dc != null) {
                bean.setResource(dc.getResource());
            }

            ObjectHelper.notNull(bean.getName(), "The bean name must be set");
            ObjectHelper.notNull(bean.getType(), "The bean type must be set");
            if (!bean.getType().startsWith("#class:")) {
                bean.setType("#class:" + bean.getType());
            }
            if (bean.getScriptLanguage() != null || bean.getScript() != null) {
                ObjectHelper.notNull(bean.getScriptLanguage(), "The bean script language must be set");
                ObjectHelper.notNull(bean.getScript(), "The bean script must be set");
            }

            // due to yaml-dsl is pre parsing beans which gets created eager
            // and then later beans can be parsed again such as from Yaml dsl files
            // we need to avoid double creating beans and therefore has a cache to check for duplicates
            String key = bean.getName() + ":" + bean.getType();
            boolean duplicate = beanCache.contains(key);
            if (!duplicate) {
                answer.addBean(bean);
                beanCache.add(key);
            }
        }

        return answer;
    }

    public void clearCache() {
        beanCache.clear();
    }

    protected void registerBean(
            ZwangineContext zwangineContext,
            List<BeanFactoryDefinition<?>> delayedRegistrations,
            BeanFactoryDefinition<?> def, boolean delayIfFailed) {

        String name = def.getName();
        String type = def.getType();
        try {
            Object target = BeanModelHelper.newInstance(def, zwangineContext);
            bindBean(zwangineContext, def, name, target);
        } catch (Exception e) {
            if (delayIfFailed) {
                delayedRegistrations.add(def);
            } else {
                String msg
                        = name != null ? "Error creating bean: " + name + " of type: " + type : "Error creating bean: " + type;
                throw new RuntimeException(msg, e);
            }
        }
    }

    private class BeansCustomizer implements ZwangineContextCustomizer {

        private final List<BeanFactoryDefinition<?>> delayedRegistrations = new ArrayList<>();
        private final List<BeanFactoryDefinition<?>> beans = new ArrayList<>();

        public void addBean(BeanFactoryDefinition<?> bean) {
            beans.add(bean);
        }

        @Override
        public void configure(ZwangineContext zwangineContext) {
            // first-pass of creating beans
            for (BeanFactoryDefinition<?> bean : beans) {
                registerBean(zwangineContext, delayedRegistrations, bean, true);
            }
            beans.clear();
            // second-pass of creating beans should fail if not possible
            for (BeanFactoryDefinition<?> bean : delayedRegistrations) {
                registerBean(zwangineContext, delayedRegistrations, bean, false);
            }
            delayedRegistrations.clear();
        }
    }

    protected void bindBean(
            ZwangineContext zwangineContext, BeanFactoryDefinition<?> def,
            String name, Object target)
            throws Exception {

        // unbind in case we reload
        zwangineContext.getRegistry().unbind(name);
        zwangineContext.getRegistry().bind(name, target, def.getInitMethod(), def.getDestroyMethod());

        // register bean in model
        Model model = zwangineContext.getZwangineContextExtension().getContextPlugin(Model.class);
        model.addCustomBean(def);
    }

}
