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

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

/**
 * A {@link org.zenithblox.StreamCache} for String {@link java.io.Reader}s.
 * <p/>
 * <b>Important:</b> All the classes from the Zwangine release that implements {@link StreamCache} is NOT intended for end
 * users to create as instances, but they are part of Zwangines
 * <a href="https://zwangine.zwangine.org/manual/stream-caching.html">stream-caching</a> functionality.
 */
public class ReaderCache extends StringReader implements StreamCache {

    private final String data;

    public ReaderCache(String data) {
        super(data);
        this.data = data;
    }

    @Override
    public void close() {
        // Do not release the string for caching
    }

    @Override
    public void reset() {
        try {
            super.reset();
        } catch (IOException e) {
            // ignore
        }
    }

    @Override
    public void writeTo(OutputStream os) throws IOException {
        os.write(data.getBytes());
    }

    @Override
    public StreamCache copy(Exchange exchange) throws IOException {
        return new ReaderCache(data);
    }

    @Override
    public boolean inMemory() {
        return true;
    }

    @Override
    public long length() {
        return data.length();
    }

    @Override
    public long position() {
        return -1;
    }

    String getData() {
        return data;
    }

}
