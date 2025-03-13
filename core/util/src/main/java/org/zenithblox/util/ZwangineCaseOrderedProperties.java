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
package org.zenithblox.util;

import java.util.Properties;

/**
 * This class is a zwangineCase ordered {@link Properties} where the key/values are stored in the order they are added or
 * loaded.
 * <p/>
 * The keys are stored in the original case, for example a key of <code>zwangine.main.stream-caching-enabled</code> is
 * stored as <code>zwangine.main.stream-caching-enabled</code>.
 * <p/>
 * However the lookup of a value by key with the <tt>get</tt> methods, will support zwangineCase or dash style.
 * <p/>
 * Note: This implementation is only intended as implementation detail for Zwangine tooling such as zwangine-jbang, and has
 * only been designed to provide the needed functionality. The complex logic for loading properties has been kept from
 * the JDK {@link Properties} class.
 */
public final class ZwangineCaseOrderedProperties extends BaseOrderedProperties {

    @Override
    public Object get(Object key) {
        lock.lock();
        try {
            return getProperty(key.toString());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getProperty(String key) {
        String answer = super.getProperty(key);
        if (answer == null) {
            answer = super.getProperty(StringHelper.dashToZwangineCase(key));
        }
        if (answer == null) {
            answer = super.getProperty(StringHelper.zwangineCaseToDash(key));
        }
        return answer;
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String answer = getProperty(key);
        if (answer == null) {
            answer = defaultValue;
        }
        return answer;
    }

}
