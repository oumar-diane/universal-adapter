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

import java.io.Serial;
import java.util.Map;
import java.util.TreeMap;

/**
 * A map that uses case insensitive keys, but preserves the original key cases.
 * <p/>
 * The map is based on {@link TreeMap} and therefore uses O(n) for lookup and not O(1) as a {@link java.util.HashMap}
 * does.
 * <p/>
 * This map is <b>not</b> designed to be thread safe as concurrent access to it is not supposed to be performed by the
 * Zwangine routing engine.
 */
public class CaseInsensitiveMap extends TreeMap<String, Object> {

    private static final @Serial long serialVersionUID = -8538318195477618308L;

    public CaseInsensitiveMap() {
        super(String.CASE_INSENSITIVE_ORDER);
    }

    public CaseInsensitiveMap(Map<? extends String, ?> map) {
        // must use the insensitive order
        super(String.CASE_INSENSITIVE_ORDER);
        putAll(map);
    }

}
