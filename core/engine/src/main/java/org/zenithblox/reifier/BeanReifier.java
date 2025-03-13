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

import org.zenithblox.BeanScope;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.BeanDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.spi.BeanProcessorFactory;
import org.zenithblox.spi.IdAware;
import org.zenithblox.spi.NodeIdFactory;
import org.zenithblox.support.PluginHelper;

public class BeanReifier extends ProcessorReifier<BeanDefinition> {

    public BeanReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, BeanDefinition.class.cast(definition));
    }

    @Override
    public Processor createProcessor() throws Exception {
        Object bean = definition.getBean();
        String ref = parseString(definition.getRef());
        String method = parseString(definition.getMethod());
        String beanType = parseString(definition.getBeanType());
        Class<?> beanClass = definition.getBeanClass();

        final BeanProcessorFactory fac = PluginHelper.getBeanProcessorFactory(zwangineContext);
        // use singleton as default scope
        BeanScope scope = BeanScope.Singleton;
        if (definition.getScope() != null) {
            scope = parse(BeanScope.class, definition.getScope());
        }
        Processor answer = fac.createBeanProcessor(zwangineContext, bean, beanType, beanClass, ref, method, scope);
        if (answer instanceof IdAware idAware) {
            String id = zwangineContext.getZwangineContextExtension().getContextPlugin(NodeIdFactory.class).createId(definition);
            idAware.setId(id);
        }
        return answer;
    }

}
