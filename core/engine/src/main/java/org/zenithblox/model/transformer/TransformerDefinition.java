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
package org.zenithblox.model.transformer;

import org.zenithblox.model.CopyableDefinition;
import org.zenithblox.model.InputTypeDefinition;
import org.zenithblox.model.OutputTypeDefinition;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Metadata;

/**
 * Represents a {@link org.zenithblox.spi.Transformer} which declarative transforms message content according to the
 * input type declared by {@link InputTypeDefinition} and/or output type declared by {@link OutputTypeDefinition}.
 *
 * If you specify from='java:com.example.ABC' and to='xml:XYZ', the transformer will be picked up when current message
 * type is 'java:com.example.ABC' and expected message type is 'xml:XYZ'. If you specify from='java' to='xml', then it
 * will be picked up for all of Java to xml transformation.
 *
 * Also, it's possible to specify a transformer name that identifies the transformer. Usually the name is a combination
 * of a scheme and a name that represents the supported data type name. The declared {@link InputTypeDefinition} and/or
 * {@link OutputTypeDefinition} can then reference the transformer by its name.
 *
 * In case the transformer name should represent a data type scheme such as name='xml' that specific transformer will
 * also be picked up for all of Java to xml and xml to Java transformation as a fallback when no matching transformer is
 * found.
 */
@Metadata(label = "transformation")
public abstract class TransformerDefinition implements CopyableDefinition<TransformerDefinition> {

    private String scheme;
    private String name;
    private String fromType;
    private String toType;

    public TransformerDefinition() {
    }

    protected TransformerDefinition(TransformerDefinition source) {
        this.scheme = source.scheme;
        this.name = source.name;
        this.fromType = source.fromType;
        this.toType = source.toType;
    }

    public String getScheme() {
        return scheme;
    }

    /**
     * Set a scheme name supported by the transformer. If you specify 'csv', the transformer will be picked up for all
     * of 'csv' from/to Java transformation. Note that the scheme matching is performed only when no exactly matched
     * transformer exists.
     *
     * @param scheme the supported data type scheme
     */
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getName() {
        return name;
    }

    /**
     * Set the transformer name under which the transformer gets referenced when specifying the input/output data type
     * on workflows. If you specify a transformer name that matches a data type scheme like 'csv' the transformer will be
     * picked up for all of 'csv:*' from/to Java transformation. Note that the scheme matching is performed only when no
     * exactly matched transformer exists.
     *
     * @param name transformer name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getFromType() {
        return fromType;
    }

    /**
     * Set the 'from' data type name. If you specify 'xml:XYZ', the transformer will be picked up if source type is
     * 'xml:XYZ'. If you specify just 'xml', the transformer matches with all of 'xml' source type like 'xml:ABC' or
     * 'xml:DEF'.
     *
     * @param from 'from' data type name
     */
    public void setFromType(String from) {
        this.fromType = from;
    }

    /**
     * Set the 'from' data type using Java class.
     *
     * @param clazz 'from' Java class
     */
    public void setFromType(Class<?> clazz) {
        this.fromType = new DataType(clazz).toString();
    }

    public String getToType() {
        return toType;
    }

    /**
     * Set the 'to' data type name. If you specify 'json:XYZ', the transformer will be picked up if destination type is
     * 'json:XYZ'. If you specify just 'json', the transformer matches with all of 'json' destination type like
     * 'json:ABC' or 'json:DEF'.
     *
     * @param to 'to' data type name
     */
    public void setToType(String to) {
        this.toType = to;
    }

    /**
     * Set the 'to' data type using Java class.
     *
     * @param clazz 'to' Java class
     */
    public void setToType(Class<?> clazz) {
        this.toType = new DataType(clazz).toString();
    }

}
