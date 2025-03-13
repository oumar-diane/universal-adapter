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
import java.util.HashMap;
import java.util.Map;

public final class MetaDataBuilder {
    private final ZwangineContext zwangineContext;
    private Object payload;
    private Map<String, Object> attributes;

    private MetaDataBuilder(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    public MetaDataBuilder withPayload(Object payload) {
        this.payload = payload;
        return this;
    }

    public MetaDataBuilder withAttribute(String name, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }

        this.attributes.put(name, value);
        return this;
    }

    public MetaDataExtension.MetaData build() {
        return new DefaultMetaData(
                zwangineContext,
                attributes == null ? Collections.emptyMap() : attributes,
                payload);
    }

    // *****************************
    //
    // *****************************

    public static MetaDataBuilder on(ZwangineContext zwangineContext) {
        return new MetaDataBuilder(zwangineContext);
    }
}
