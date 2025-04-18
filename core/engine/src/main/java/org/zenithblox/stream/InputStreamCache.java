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

import org.zenithblox.Exchange;
import org.zenithblox.StreamCache;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A {@link StreamCache} for caching using an in-memory byte array.
 * <p/>
 * <b>Important:</b> All the classes from the Zwangine release that implements {@link StreamCache} is NOT intended for end
 * users to create as instances, but they are part of Zwangines
 * <a href="https://zwangine.zwangine.org/manual/stream-caching.html">stream-caching</a> functionality.
 */
public final class InputStreamCache extends ByteArrayInputStream implements StreamCache {

    public InputStreamCache(byte[] data) {
        super(data);
    }

    public InputStreamCache(byte[] data, int count) {
        super(data);
        super.count = count;
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(buf, pos, count - pos);
    }

    @Override
    public StreamCache copy(Exchange exchange) {
        return new InputStreamCache(buf, count);
    }

    @Override
    public boolean inMemory() {
        return true;
    }

    @Override
    public long length() {
        return count;
    }

    public long position() {
        return available() - count;
    }
}
