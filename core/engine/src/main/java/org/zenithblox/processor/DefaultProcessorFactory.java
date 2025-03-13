/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.processor;

import org.zenithblox.*;
import org.zenithblox.spi.BootstrapCloseable;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.spi.ProcessorFactory;
import org.zenithblox.spi.annotations.JdkService;
import org.zenithblox.support.PluginHelper;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Default {@link ProcessorFactory} that supports using 3rd party Zwangine components to implement the EIP
 * {@link Processor}.
 * <p/>
 * The component should use the {@link FactoryFinder} SPI to specify a file with the name of the EIP model in the
 * directory of {@link #RESOURCE_PATH}. The file should contain a property with key <tt>class</tt> that refers to the
 * name of the {@link ProcessorFactory} the Zwangine component implement, which gets called for creating the
 * {@link Processor}s for the EIP.
 * <p/>
 */
@JdkService(ProcessorFactory.FACTORY)
public class DefaultProcessorFactory implements ProcessorFactory, BootstrapCloseable {

    public static final String RESOURCE_PATH = "META-INF/services.org.zenithblox/model/";

    private FactoryFinder finder;

    @Override
    public void close() throws IOException {
        if (finder instanceof BootstrapCloseable bootstrapCloseable) {
            bootstrapCloseable.close();
            finder = null;
        }
    }

    @Override
    public Processor createChildProcessor(Workflow workflow, NamedNode definition, boolean mandatory) throws Exception {
        String name = definition.getClass().getSimpleName();
        if (finder == null) {
            finder = PluginHelper.getFactoryFinderResolver(workflow.getZwangineContext())
                    .resolveBootstrapFactoryFinder(workflow.getZwangineContext().getClassResolver(), RESOURCE_PATH);
        }
        try {
            Object object = finder.newInstance(name).orElse(null);
            if (object instanceof ProcessorFactory pc) {
                Processor processor = pc.createChildProcessor(workflow, definition, mandatory);
                LineNumberAware.trySetLineNumberAware(processor, definition);
                return processor;
            }
        } catch (NoFactoryAvailableException e) {
            // ignore there is no custom factory
        }

        return null;
    }

    @Override
    public Processor createProcessor(Workflow workflow, NamedNode definition) throws Exception {
        String name = definition.getClass().getSimpleName();
        if (finder == null) {
            finder = PluginHelper.getFactoryFinderResolver(workflow.getZwangineContext())
                    .resolveBootstrapFactoryFinder(workflow.getZwangineContext().getClassResolver(), RESOURCE_PATH);
        }
        ProcessorFactory pc = finder.newInstance(name, ProcessorFactory.class).orElse(null);
        if (pc != null) {
            Processor processor = pc.createProcessor(workflow, definition);
            LineNumberAware.trySetLineNumberAware(processor, definition);
            return processor;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Processor createProcessor(ZwangineContext zwangineContext, String definitionName, Object[] args)
            throws Exception {
        if ("SendDynamicProcessor".equals(definitionName)) {
            String uri = (String) args[0];
            Expression expression = (Expression) args[1];
            ExchangePattern exchangePattern = (ExchangePattern) args[2];
            SendDynamicProcessor processor = new SendDynamicProcessor(uri, expression);
            processor.setZwangineContext(zwangineContext);
            if (exchangePattern != null) {
                processor.setPattern(exchangePattern);
            }
            return processor;
        } else if ("MulticastProcessor".equals(definitionName)) {
            Collection<Processor> processors = (Collection<Processor>) args[0];
            ExecutorService executor = (ExecutorService) args[1];
            boolean shutdownExecutorService = (boolean) args[2];
            return new MulticastProcessor(
                    zwangineContext, null, processors, null, true, executor, shutdownExecutorService, false, false, 0,
                    null, false, false, 0);
        } else if ("Workflow".equals(definitionName)) {
            List<Processor> processors = (List<Processor>) args[0];
            return Pipeline.newInstance(zwangineContext, processors);
        }

        return null;
    }
}
