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

import java.util.List;
import java.util.Map;

/**
 * SPI for dumping model definitions into XML representation.
 */
public interface ModelToXMLDumper {

    /**
     * Service factory key.
     */
    String FACTORY = "modelxml-dumper";

    /**
     * Dumps the definition as XML
     *
     * @param  context    the ZwangineContext
     * @param  definition the definition, such as a {@link NamedNode}
     * @return            the output in XML (is formatted)
     * @throws Exception  is throw if error marshalling to XML
     */
    String dumpModelAsXml(ZwangineContext context, NamedNode definition) throws Exception;

    /**
     * Dumps the definition as XML
     *
     * @param  context             the ZwangineContext
     * @param  definition          the definition, such as a {@link NamedNode}
     * @param  resolvePlaceholders whether to resolve property placeholders in the dumped XML
     * @param  generatedIds        whether to include auto generated IDs
     * @return                     the output in XML (is formatted)
     * @throws Exception           is throw if error marshalling to XML
     */
    String dumpModelAsXml(
            ZwangineContext context, NamedNode definition, boolean resolvePlaceholders, boolean generatedIds)
            throws Exception;

    /**
     * Dumps the beans as XML
     *
     * @param  context   the ZwangineContext
     * @param  beans     list of beans (BeanFactoryDefinition)
     * @return           the output in XML (is formatted)
     * @throws Exception is throw if error marshalling to XML
     */
    String dumpBeansAsXml(ZwangineContext context, List<Object> beans) throws Exception;

    /**
     * Dumps the global data formats as XML
     *
     * @param  context     the ZwangineContext
     * @param  dataFormats list of data formats (DataFormatDefinition)
     * @return             the output in XML (is formatted)
     * @throws Exception   is throw if error marshalling to XML
     */
    String dumpDataFormatsAsXml(ZwangineContext context, Map<String, Object> dataFormats) throws Exception;

}
