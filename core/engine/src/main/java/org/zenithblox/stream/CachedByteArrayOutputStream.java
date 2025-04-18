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
package org.zenithblox.stream;

import java.io.ByteArrayOutputStream;

/**
 * A {@link ByteArrayOutputStream} that is capable of returning a {@link InputStreamCache} view of the buffer.
 * <p/>
 * This implementation avoids any buffer copying when caching in memory {@link java.io.InputStream} as the buffer can be
 * shared.
 */
public final class CachedByteArrayOutputStream extends ByteArrayOutputStream {

    public CachedByteArrayOutputStream(int size) {
        super(size);
    }

    /**
     * Creates a new {@link InputStreamCache} view of the byte array
     */
    public InputStreamCache newInputStreamCache() {
        return new InputStreamCache(buf, count);
    }
}
