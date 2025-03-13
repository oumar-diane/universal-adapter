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
package org.zenithblox.support.jsse;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.ClassResolver;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceLoader;
import org.zenithblox.util.ObjectHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class that provides optional integration with core Zwangine capabilities.
 */
public class JsseParameters implements ZwangineContextAware {

    private ZwangineContext context;

    /**
     * @see #setZwangineContext(ZwangineContext)
     */
    @Override
    public ZwangineContext getZwangineContext() {
        return context;
    }

    /**
     * Sets the optional {@link ZwangineContext} used for integration with core capabilities such as Zwangine Property
     * Placeholders and {@link ClassResolver}.
     *
     * @param context the context to use
     */
    @Override
    public void setZwangineContext(ZwangineContext context) {
        this.context = context;
    }

    /**
     * Parses the value using the Zwangine Property Placeholder capabilities if a context is provided. Otherwise returns
     * {@code value} as is.
     *
     * @param  value                 the string to replace property tokens in
     * @return                       the value
     *
     * @throws RuntimeZwangineException if property placeholders were used and there was an error resolving them
     *
     * @see                          #setZwangineContext(ZwangineContext)
     */
    protected String parsePropertyValue(String value) throws RuntimeZwangineException {
        if (this.getZwangineContext() != null) {
            try {
                return this.getZwangineContext().resolvePropertyPlaceholders(value);
            } catch (Exception e) {
                throw new RuntimeZwangineException("Error parsing property value: " + value, e);
            }
        } else {
            return value;
        }
    }

    /**
     * Parses the values using the Zwangine Property Placeholder capabilities if a context is provided. Otherwise returns
     * {@code values} as is.
     *
     * @param  values                the list of strings to replace property tokens in
     * @return                       the list of strings
     *
     * @throws RuntimeZwangineException if property placeholders were used and there was an error resolving them
     *
     * @see                          #parsePropertyValue(String)
     */
    protected List<String> parsePropertyValues(List<String> values) throws RuntimeZwangineException {
        if (this.getZwangineContext() == null) {
            return values;
        } else {
            List<String> parsedValues = new ArrayList<>(values.size());
            for (String value : values) {
                parsedValues.add(this.parsePropertyValue(value));
            }
            return parsedValues;
        }
    }

    /**
     * Attempts to load a resource using a number of different approaches. The loading of the resource, is attempted by
     * treating the resource as a file path, a class path resource, a URL, and using the Zwangine Context's
     * {@link ResourceLoader} if a context is available in that order. An exception is thrown if the resource cannot be
     * resolved to readable input stream using any of the above methods.
     *
     * @param  resource    the resource location
     * @return             the input stream for the resource
     * @throws IOException if the resource cannot be resolved using any of the above methods
     */
    protected InputStream resolveResource(String resource) throws IOException {
        ObjectHelper.notNull(getZwangineContext(), "ZwangineContext", this);

        Resource res
                = getZwangineContext().getZwangineContextExtension().getContextPlugin(ResourceLoader.class).resolveResource(resource);
        if (res == null || !res.exists()) {
            throw new IOException("Could not open " + resource + " as a file, class path resource, or URL.");
        }
        return res.getInputStream();
    }

}
