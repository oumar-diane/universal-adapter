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
package org.zenithblox.support;

import org.zenithblox.ZwangineContext;
import org.zenithblox.Exchange;
import org.zenithblox.NoTypeConversionAvailableException;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.Scanner;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Group based {@link Iterator} which groups the given {@link Iterator} a number of times and then return a combined
 * response as a String.
 * <p/>
 * This implementation uses an internal byte array buffer, to combine the response. The token is inserted between the
 * individual parts.
 * <p/>
 * For example if you group by new line, then a new line token is inserted between the lines.
 */
public final class GroupTokenIterator implements Iterator<Object>, Closeable {

    private final ZwangineContext zwangineContext;
    private final Exchange exchange;
    private final Iterator<?> it;
    private final String token;
    private final int group;
    private final boolean skipFirst;
    private final AtomicBoolean hasSkipFirst;
    private boolean closed;
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    /**
     * Creates a new token based group iterator
     *
     * @param  exchange                 the exchange used to create this group iterator
     * @param  it                       the iterator to group
     * @param  token                    then token used to separate between the parts, use <tt>null</tt> to not add the
     *                                  token
     * @param  group                    number of parts to group together
     * @throws IllegalArgumentException is thrown if group is not a positive number
     */
    public GroupTokenIterator(Exchange exchange, Iterator<?> it, String token, int group, boolean skipFirst) {
        this.exchange = exchange;
        this.zwangineContext = exchange.getContext();
        this.it = it;
        // if the iterator is a scanner then it may have a dynamic delimiter
        // so we need to use the actual evaluated delimiter as token
        if (LanguageSupport.hasSimpleFunction(token) && it instanceof Scanner scanner) {
            this.token = scanner.getDelim();
        } else {
            this.token = token;
        }
        this.group = group;
        if (group <= 0) {
            throw new IllegalArgumentException("Group must be a positive number, was: " + group);
        }
        this.skipFirst = skipFirst;
        if (skipFirst) {
            this.hasSkipFirst = new AtomicBoolean();
        } else {
            this.hasSkipFirst = null;
        }
    }

    @Override
    public void close() throws IOException {
        try {
            IOHelper.closeIterator(it);
        } finally {
            // close the buffer as well
            bos.close();
            // we are now closed
            closed = true;
        }
    }

    @Override
    public boolean hasNext() {
        if (closed) {
            return false;
        }

        boolean answer = it.hasNext();
        if (!answer) {
            // auto close
            try {
                close();
            } catch (IOException e) {
                // ignore
            }
        }
        return answer;
    }

    @Override
    public Object next() {
        try {
            return doNext();
        } catch (Exception e) {
            throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
        }
    }

    private Object doNext() throws IOException, NoTypeConversionAvailableException {
        int count = 0;
        Object data;
        while (count < group && it.hasNext()) {
            data = it.next();

            if (skipFirst && hasSkipFirst.compareAndSet(false, true)) {
                if (it.hasNext()) {
                    data = it.next();
                } else {
                    // Content with header only which is marked to skip
                    data = "";
                }
            }

            // include token in between
            if (data != null && count > 0 && token != null) {
                bos.write(token.getBytes());
            }
            if (data instanceof InputStream is) {
                IOHelper.copy(is, bos);
            } else if (data instanceof byte[] bytes) {
                bos.write(bytes);
            } else if (data != null) {
                // convert to input stream
                InputStream is = zwangineContext.getTypeConverter().mandatoryConvertTo(InputStream.class, exchange, data);
                IOHelper.copy(is, bos);
            }

            count++;
        }

        // prepare and return answer as String using exchange's charset
        String answer = bos.toString(ExchangeHelper.getCharset(exchange));
        bos.reset();
        return answer;
    }

    @Override
    public void remove() {
        it.remove();
    }
}
