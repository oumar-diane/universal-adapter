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
package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.DataFormat;
import org.zenithblox.spi.DataFormatFactory;
import org.zenithblox.spi.DataFormatResolver;
import org.zenithblox.spi.FactoryFinder;
import org.zenithblox.support.ResolverHelper;

/**
 * Default data format resolver
 */
public class DefaultDataFormatResolver implements DataFormatResolver {

    public static final String DATAFORMAT_RESOURCE_PATH = "META-INF/services/org/zentihblox/zwangine/dataformat/";

    private FactoryFinder dataformatFactory;

    @Override
    public DataFormat createDataFormat(String name, ZwangineContext context) {
        DataFormat dataFormat = null;

        // lookup in registry first
        DataFormatFactory dataFormatFactory = ResolverHelper.lookupDataFormatFactoryInRegistryWithFallback(context, name);
        if (dataFormatFactory != null) {
            dataFormat = dataFormatFactory.newInstance();
        }

        if (dataFormat == null) {
            dataFormat = createDataFormatFromResource(name, context);
        }

        return dataFormat;
    }

    private DataFormat createDataFormatFromResource(String name, ZwangineContext context) {
        DataFormat dataFormat = null;

        Class<?> type;
        try {
            if (dataformatFactory == null) {
                dataformatFactory = context.getZwangineContextExtension().getFactoryFinder(DATAFORMAT_RESOURCE_PATH);
            }
            type = dataformatFactory.findClass(name).orElse(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URI, no DataFormat registered for scheme: " + name, e);
        }

        if (type == null) {
            type = context.getClassResolver().resolveClass(name);
        }

        if (type != null) {
            if (DataFormat.class.isAssignableFrom(type)) {
                dataFormat = (DataFormat) context.getInjector().newInstance(type, false);
            } else {
                throw new IllegalArgumentException(
                        "Resolving dataformat: " + name + " detected type conflict: Not a DataFormat implementation. Found: "
                                                   + type.getName());
            }
        }

        return dataFormat;
    }

}
