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

import org.zenithblox.spi.HeadersMapFactory;
import org.zenithblox.util.CaseInsensitiveMap;

import java.util.Map;

/**
 * Default {@link HeadersMapFactory} which uses the {@link org.zenithblox.util.CaseInsensitiveMap CaseInsensitiveMap}.
 * This implementation uses a {@link org.zenithblox.util.CaseInsensitiveMap} storing the headers. This allows us to be
 * able to lookup headers using case insensitive keys, making it easier for end users as they do not have to be worried
 * about using exact keys. See more details at {@link org.zenithblox.util.CaseInsensitiveMap}.
 */
public class DefaultHeadersMapFactory implements HeadersMapFactory {

    @Override
    public Map<String, Object> newMap() {
        return new CaseInsensitiveMap();
    }

    @Override
    public Map<String, Object> newMap(Map<String, Object> map) {
        return new CaseInsensitiveMap(map);
    }

    @Override
    public boolean isInstanceOf(Map<String, Object> map) {
        return map instanceof CaseInsensitiveMap;
    }

    @Override
    public boolean isCaseInsensitive() {
        return true;
    }
}
