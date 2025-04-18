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
import org.zenithblox.support.ExchangeHelper;
import org.zenithblox.support.InputStreamIterator;
import org.zenithblox.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Some core java.io based <a href="http://zwangine.zwangine.org/type-converter.html">Type Converters</a>
 */
@Converter(generateBulkLoader = true)
public final class IOConverter {

    private static final Logger LOG = LoggerFactory.getLogger(IOConverter.class);

    /**
     * Utility classes should not have a public constructor.
     */
    private IOConverter() {
    }

    @Converter(order = 1)
    public static InputStream toInputStream(Stream<?> stream, Exchange exchange) {
        Iterator<?> it = stream.iterator();
        return new InputStreamIterator(exchange.getContext().getTypeConverter(), it);
    }

    @Converter(order = 2)
    public static InputStream toInputStream(URL url) throws IOException {
        return IOHelper.buffered(url.openStream());
    }

    @Converter(order = 3)
    public static InputStream toInputStream(File file) throws IOException {
        return IOHelper.buffered(new FileInputStream(file));
    }

    @Converter(order = 4)
    public static InputStream toInputStream(Path file) throws IOException {
        return IOHelper.buffered(Files.newInputStream(file));
    }

    @Converter(order = 5)
    public static BufferedReader toReader(File file, Exchange exchange) throws IOException {
        return IOHelper.toReader(file, ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 6)
    public static BufferedReader toReader(Path path, Exchange exchange) throws IOException {
        return IOHelper.toReader(path, ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 7)
    public static OutputStream toOutputStream(File file) throws FileNotFoundException {
        return IOHelper.buffered(new FileOutputStream(file));
    }

    @Converter(order = 8)
    public static OutputStream toOutputStream(Path file) throws IOException {
        return IOHelper.buffered(Files.newOutputStream(file));
    }

    @Converter(order = 9)
    public static BufferedWriter toWriter(File file, Exchange exchange) throws IOException {
        return toWriter(file.toPath(), exchange);
    }

    @Converter(order = 10)
    public static BufferedWriter toWriter(Path file, Exchange exchange) throws IOException {
        return Files.newBufferedWriter(file, ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 11)
    public static Reader toReader(InputStream in, Exchange exchange) throws IOException {
        return IOHelper.buffered(new InputStreamReader(in, ExchangeHelper.getCharset(exchange)));
    }

    @Converter(order = 12)
    public static Reader toReader(byte[] data, Exchange exchange) throws IOException {
        return toReader(new ByteArrayInputStream(data), exchange);
    }

    @Converter(order = 13)
    public static Writer toWriter(OutputStream out, Exchange exchange) throws IOException {
        return IOHelper.buffered(new OutputStreamWriter(out, ExchangeHelper.getCharset(exchange)));
    }

    @Converter(order = 14)
    public static Reader toReader(String text) {
        // no buffering required as the complete string input is already passed
        // over as a whole
        return new StringReader(text);
    }

    @Converter(order = 15)
    public static InputStream toInputStream(String text, Exchange exchange) throws IOException {
        return toInputStream(text.getBytes(ExchangeHelper.getCharset(exchange)));
    }

    @Converter(order = 16)
    public static InputStream toInputStream(StringBuffer buffer, Exchange exchange) throws IOException {
        return toInputStream(buffer.toString(), exchange);
    }

    @Converter(order = 17)
    public static InputStream toInputStream(StringBuilder builder, Exchange exchange) throws IOException {
        return toInputStream(builder.toString(), exchange);
    }

    @Converter(order = 18)
    public static InputStream toInputStream(BufferedReader buffer, Exchange exchange) throws IOException {
        return toInputStream(toString(buffer), exchange);
    }

    @Converter(order = 19)
    public static InputStream toInputStream(Reader buffer, Exchange exchange) throws IOException {
        return toInputStream(toString(buffer), exchange);
    }

    @Converter(order = 20)
    public static String toString(byte[] data, Exchange exchange) throws IOException {
        return new String(data, ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 21)
    public static String toString(File file, Exchange exchange) throws IOException {
        return toString(file.toPath(), exchange);
    }

    @Converter(order = 22)
    public static String toString(Path file, Exchange exchange) throws IOException {
        Charset cs = ExchangeHelper.getCharset(exchange, false);
        if (cs != null) {
            return Files.readString(file, cs);
        } else {
            byte[] arr = Files.readAllBytes(file);
            if (arr.length == 0) {
                return "";
            } else {
                return new String(arr);
            }
        }
    }

    @Converter(order = 23)
    public static byte[] toByteArray(File file) throws IOException {
        return toByteArray(file.toPath());
    }

    @Converter(order = 24)
    public static byte[] toByteArray(Path file) throws IOException {
        return Files.readAllBytes(file);
    }

    @Converter(order = 25)
    public static byte[] toByteArray(BufferedReader reader, Exchange exchange) throws IOException {
        String s = toString(reader);
        return toByteArray(s, exchange);
    }

    @Converter(order = 26)
    public static String toString(URL url, Exchange exchange) throws IOException {
        InputStream is = toInputStream(url);
        try {
            return toString(is, exchange);
        } finally {
            IOHelper.close(is, "url", LOG);
        }
    }

    @Converter(order = 27)
    public static String toString(BufferedReader reader) throws IOException {
        return IOHelper.toString(reader);
    }

    @Converter(order = 28)
    public static String toString(Reader reader) throws IOException {
        return IOHelper.toString(reader);
    }

    @Converter(order = 29)
    public static byte[] toByteArray(Reader reader, Exchange exchange) throws IOException {
        return toByteArray(IOHelper.buffered(reader), exchange);
    }

    @Converter(order = 30)
    public static byte[] toByteArray(String value, Exchange exchange) throws IOException {
        return value.getBytes(ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 31)
    public static String toString(InputStream in, Exchange exchange) throws IOException {
        return toString(toReader(in, exchange));
    }

    @Converter(order = 32)
    public static InputStream toInputStream(byte[] data) {
        // no buffering required as the complete byte input is already passed
        // over as a whole
        return new ByteArrayInputStream(data);
    }

    @Converter(order = 33)
    public static ObjectOutput toObjectOutput(OutputStream stream) throws IOException {
        if (stream instanceof ObjectOutput out) {
            return out;
        } else {
            return new ObjectOutputStream(IOHelper.buffered(stream));
        }
    }

    @Converter(order = 34)
    public static ObjectInput toObjectInput(final InputStream stream, final Exchange exchange) throws IOException {
        if (stream instanceof ObjectInput objectInput) {
            return objectInput;
        } else {
            return new ObjectInputStream(IOHelper.buffered(stream)) {
                @Override
                protected Class<?> resolveClass(ObjectStreamClass objectStreamClass)
                        throws IOException, ClassNotFoundException {
                    // need to let Zwangine be able to resolve class using ClassResolver SPI, to let class loading
                    // work in OSGi and other containers
                    Class<?> answer = null;
                    String name = objectStreamClass.getName();
                    if (exchange != null) {
                        LOG.trace("Loading class {} using Zwangine ClassResolver", name);
                        answer = exchange.getContext().getClassResolver().resolveClass(name);
                    }
                    if (answer == null) {
                        LOG.trace("Loading class {} using JDK default implementation", name);
                        answer = super.resolveClass(objectStreamClass);
                    }
                    return answer;
                }
            };
        }
    }

    @Converter(order = 35)
    public static byte[] toBytes(InputStream stream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(IOHelper.buffered(stream), bos);

        // no need to close the ByteArrayOutputStream as it's close()
        // implementation is noop
        return bos.toByteArray();
    }

    @Converter(order = 36)
    public static byte[] toByteArray(ByteArrayOutputStream os) {
        return os.toByteArray();
    }

    @Converter(order = 37)
    public static ByteBuffer covertToByteBuffer(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        IOHelper.copyAndCloseInput(is, os);
        return ByteBuffer.wrap(os.toByteArray());
    }

    @Converter(order = 38)
    public static String toString(ByteArrayOutputStream os, Exchange exchange) throws IOException {
        return os.toString(ExchangeHelper.getCharset(exchange));
    }

    @Converter(order = 39)
    public static InputStream toInputStream(ByteArrayOutputStream os) {
        // no buffering required as the complete byte array input is already
        // passed over as a whole
        return new ByteArrayInputStream(os.toByteArray());
    }

    @Converter(order = 40)
    public static Properties toProperties(File file) throws IOException {
        return toProperties(new FileInputStream(file));
    }

    @Converter(order = 41)
    public static Properties toProperties(InputStream is) throws IOException {
        Properties prop = new Properties();
        try {
            prop.load(is);
        } finally {
            IOHelper.close(is);
        }
        return prop;
    }

    @Converter(order = 42)
    public static Properties toProperties(Reader reader) throws IOException {
        Properties prop = new Properties();
        try {
            prop.load(reader);
        } finally {
            IOHelper.close(reader);
        }
        return prop;
    }

    @Converter(order = 43)
    public static Path toPath(File file) {
        return file.toPath();
    }

    @Converter(order = 44)
    public static File toFile(Path path) {
        return path.toFile();
    }

}
