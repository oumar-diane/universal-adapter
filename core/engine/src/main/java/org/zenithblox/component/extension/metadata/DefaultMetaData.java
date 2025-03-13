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
package org.zenithblox.component.extension.metadata;

import org.zenithblox.ZwangineContext;
import org.zenithblox.component.extension.MetaDataExtension;

import java.util.Collections;
import java.util.Map;

public class DefaultMetaData implements MetaDataExtension.MetaData {
    private final Map<String, Object> attributes;
    private final Object payload;
    private final ZwangineContext zwangineContext;

    public DefaultMetaData(ZwangineContext zwangineContext, Map<String, Object> attributes, Object payload) {
        this.zwangineContext = zwangineContext;
        this.attributes = attributes;
        this.payload = payload;
    }

    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    @Override
    public <T> T getAttribute(String name, Class<T> type) {
        Object value = attributes.get(name);
        if (zwangineContext != null) {
            return zwangineContext.getTypeConverter().convertTo(type, value);
        }

        throw new IllegalStateException("Unable to perform conversion as ZwangineContext is not set");
    }

    @Override
    public Object getPayload() {
        return payload;
    }

    @Override
    public <T> T getPayload(Class<T> type) {
        if (zwangineContext != null) {
            return zwangineContext.getTypeConverter().convertTo(type, payload);
        }

        throw new IllegalStateException("Unable to perform conversion as ZwangineContext is not set");
    }
}
