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
package org.zenithblox.spi;

import org.zenithblox.ZwangineContext;
import org.zenithblox.NamedNode;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;

/**
 * A factory to create {@link Processor} based on the {@link org.zenithblox.model.ProcessorDefinition definition}.
 * <p/>
 * This allows you to implement a custom factory in which you can control the creation of the processors. It also allows
 * you to manipulate the {@link org.zenithblox.model.ProcessorDefinition definition}s for example to configure or
 * change options. Its also possible to add new steps in the workflow by adding outputs to
 * {@link org.zenithblox.model.ProcessorDefinition definition}s.
 * <p/>
 * <b>Important:</b> A custom ProcessorFactory should extend the default implementation
 * <tt>org.zenithblox.processor.DefaultProcessorFactory</tt> and in the overridden methods, super should be called to
 * let the default implementation create the processor when custom processors is not created.
 */
public interface ProcessorFactory {

    /**
     * Service factory key.
     */
    String FACTORY = "processor-factory";

    /**
     * Creates the child processor.
     * <p/>
     * The child processor is an output from the given definition, for example the sub workflow in a splitter EIP.
     *
     * @param  workflow      the workflow context
     * @param  definition the definition which represents the processor
     * @param  mandatory  whether or not the child is mandatory
     * @return            the created processor, or <tt>null</tt> to let the default implementation in Zwangine create the
     *                    processor.
     * @throws Exception  can be thrown if error creating the processor
     */
    Processor createChildProcessor(Workflow workflow, NamedNode definition, boolean mandatory) throws Exception;

    /**
     * Creates the processor.
     *
     * @param  workflow      the workflow context
     * @param  definition the definition which represents the processor
     * @return            the created processor, or <tt>null</tt> to let the default implementation in Zwangine create the
     *                    processor.
     * @throws Exception  can be thrown if error creating the processor
     */
    Processor createProcessor(Workflow workflow, NamedNode definition) throws Exception;

    /**
     * Creates a processor by the name of the definition. This should only be used in some special situations where the
     * processor is used internally by Zwangine itself and some component such as zwangine-cloud, zwangine-seda.
     *
     * @param  zwangineContext   the zwangine context
     * @param  definitionName the name of the definition that represents the processor
     * @param  args           arguments for creating the processor (optimized to use fixed order of parameters)
     * @return                the created processor, or <tt>null</tt> if this situation is not yet implemented.
     * @throws Exception      can be thrown if error creating the processor
     */
    Processor createProcessor(ZwangineContext zwangineContext, String definitionName, Object[] args) throws Exception;

}
