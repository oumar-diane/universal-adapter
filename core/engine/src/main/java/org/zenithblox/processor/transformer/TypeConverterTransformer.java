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

package org.zenithblox.processor.transformer;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineExecutionException;
import org.zenithblox.InvalidPayloadException;
import org.zenithblox.Message;
import org.zenithblox.spi.DataType;
import org.zenithblox.spi.Transformer;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data type converter receives a name and a target type in order to use traditional exchange body conversion mechanisms
 * in order to transform the message body to a given type.
 */
public class TypeConverterTransformer extends Transformer {

    private static final Logger LOG = LoggerFactory.getLogger(TypeConverterTransformer.class);

    private DataType dataType;
    private Class<?> type;

    public TypeConverterTransformer(DataType type) {
        super(type.getFullName());
        this.dataType = type;
    }

    public TypeConverterTransformer(Class<?> type) {
        super("java:" + type.getName());
        this.type = type;
    }

    @Override
    public void transform(Message message, DataType from, DataType to) {
        if (message == null || message.getBody() == null) {
            return;
        }

        try {
            if (dataType != null) {
                if (DataType.isJavaType(dataType) && dataType.getName() != null) {
                    ZwangineContext context = message.getExchange().getContext();
                    type = context.getClassResolver().resolveMandatoryClass(dataType.getName());
                }
            }

            if (type != null && !type.isAssignableFrom(message.getBody().getClass())) {
                LOG.debug("Converting to '{}'", type.getName());
                message.setBody(message.getMandatoryBody(type));
            }
        } catch (InvalidPayloadException | ClassNotFoundException e) {
            throw new ZwangineExecutionException(
                    String.format("Failed to convert body to '%s' content using type conversion for %s",
                            getName(), ObjectHelper.name(type)),
                    message.getExchange(), e);
        }
    }

    public Class<?> getType() {
        return type;
    }
}
