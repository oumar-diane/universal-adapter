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
package org.zenithblox.converter;

import org.zenithblox.Converter;
import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.zenithblox.util.BufferCaster.cast;

/**
 * Some core java.nio based <a href="http://zwangine.zwangine.org/type-converter.html">Type Converters</a>
 */
@Converter(generateBulkLoader = true)
public final class NIOConverter {

    private static final Logger LOG = LoggerFactory.getLogger(NIOConverter.class);

    /**
     * Utility classes should not have a public constructor.
     */
    private NIOConverter() {
    }

    @Converter(order = 1)
    public static byte[] toByteArray(ByteBuffer buffer) {
        byte[] bArray = new byte[buffer.limit()];
        buffer.get(bArray);
        return bArray;
    }

    @Converter(order = 2)
    public static String toString(ByteBuffer buffer, Exchange exchange) throws IOException {
        return IOConverter.toString(toByteArray(buffer), exchange);
    }

    @Converter(order = 3)
    public static ByteBuffer toByteBuffer(byte[] data) {
        return ByteBuffer.wrap(data);
    }

    @Converter(order = 4)
    public static ByteBuffer toByteBuffer(ByteArrayOutputStream baos) {
        return ByteBuffer.wrap(baos.toByteArray());
    }

    @Converter(order = 5)
    public static ByteBuffer toByteBuffer(File file) throws IOException {
        return toByteBuffer(file.toPath());
    }

    @Converter(order = 6)
    public static ByteBuffer toByteBuffer(Path file) throws IOException {
        long length = Files.size(file);
        if (length > Integer.MAX_VALUE) {
            // very big file we cannot load into memory
            throw new IOException(
                    "Cannot convert file: " + file + " to ByteBuffer. The file length is too large: "
                                  + length);
        }
        return ByteBuffer.wrap(Files.readAllBytes(file));
    }

    @Converter(order = 7)
    public static ByteBuffer toByteBuffer(String value, Exchange exchange) {
        byte[] bytes = null;
        if (exchange != null) {
            String charsetName = exchange.getProperty(ExchangePropertyKey.CHARSET_NAME, String.class);
            if (charsetName != null) {
                try {
                    bytes = value.getBytes(charsetName);
                } catch (UnsupportedEncodingException e) {
                    LOG.warn("Cannot convert the byte to String with the charset {}", charsetName, e);
                }
            }
        }
        if (bytes == null) {
            bytes = value.getBytes();
        }
        return ByteBuffer.wrap(bytes);
    }

    @Converter(order = 8)
    public static ByteBuffer toByteBuffer(Short value) {
        ByteBuffer buf = ByteBuffer.allocate(2);
        buf.putShort(value);
        cast(buf).flip();
        return buf;
    }

    @Converter(order = 9)
    public static ByteBuffer toByteBuffer(Integer value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putInt(value);
        cast(buf).flip();
        return buf;
    }

    @Converter(order = 10)
    public static ByteBuffer toByteBuffer(Long value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putLong(value);
        cast(buf).flip();
        return buf;
    }

    @Converter(order = 11)
    public static ByteBuffer toByteBuffer(Float value) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.putFloat(value);
        cast(buf).flip();
        return buf;
    }

    @Converter(order = 12)
    public static ByteBuffer toByteBuffer(Double value) {
        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.putDouble(value);
        cast(buf).flip();
        return buf;
    }

    @Converter(order = 13)
    public static InputStream toInputStream(ByteBuffer bufferbuffer) {
        return IOConverter.toInputStream(toByteArray(bufferbuffer));
    }

}
