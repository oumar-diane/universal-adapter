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
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.ExchangeFormatter;
import org.zenithblox.support.processor.DefaultExchangeFormatter;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LanguageHelper {
    private LanguageHelper() {

    }

    /**
     * Extracts the exception from an Exchange
     *
     * @param  exchange the exchange to extract the exception
     * @return          the exception or null if not present
     */
    public static Exception exception(Exchange exchange) {
        Exception exception = exchange.getException();
        if (exception == null) {
            exception = exchange.getProperty(ExchangePropertyKey.EXCEPTION_CAUGHT, Exception.class);
        }
        return exception;
    }

    /**
     * Extracts the exception message from an exchange
     *
     * @param  exchange the exchange to extract the exception
     * @return          the message or null if not found
     */
    public static String exceptionMessage(Exchange exchange) {
        Exception exception = exception(exchange);
        if (exception != null) {
            return exception.getMessage();
        } else {
            return null;
        }
    }

    /**
     * Gets the exception stack trace from an exchange
     *
     * @param  exchange exchange the exchange to extract the stack trace
     * @return          the stack trace or null if no exception is present on the exchange
     */
    public static String exceptionStacktrace(Exchange exchange) {
        Exception exception = exception(exchange);
        return ExceptionHelper.stackTraceToString(exception);
    }

    /**
     * Tests if the exchange ends with a given value
     *
     * @param  exchange   the exchange to test
     * @param  leftValue  the value being tested
     * @param  rightValue the suffix to test
     * @return            true if it ends with or false otherwise
     */
    public static boolean endsWith(Exchange exchange, Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            // they are equal
            return true;
        } else if (leftValue == null || rightValue == null) {
            // only one of them is null so they are not equal
            return false;
        }
        String leftStr = exchange.getContext().getTypeConverter().convertTo(String.class, leftValue);
        String rightStr = exchange.getContext().getTypeConverter().convertTo(String.class, rightValue);
        if (leftStr != null && rightStr != null) {
            return leftStr.endsWith(rightStr);
        } else {
            return false;
        }
    }

    /**
     * Tests if the exchange starts with a given value
     *
     * @param  exchange   the exchange to test
     * @param  leftValue  the value being tested
     * @param  rightValue the prefix to test
     * @return            true if it starts with or false otherwise
     */
    public static boolean startsWith(Exchange exchange, Object leftValue, Object rightValue) {
        if (leftValue == null && rightValue == null) {
            // they are equal
            return true;
        } else if (leftValue == null || rightValue == null) {
            // only one of them is null so they are not equal
            return false;
        }
        String leftStr = exchange.getContext().getTypeConverter().convertTo(String.class, leftValue);
        String rightStr = exchange.getContext().getTypeConverter().convertTo(String.class, rightValue);
        if (leftStr != null && rightStr != null) {
            return leftStr.startsWith(rightStr);
        } else {
            return false;
        }
    }

    /**
     * Gets the exchange formatter or creates one if unset
     *
     * @param  zwangineContext      the Zwangine context
     * @param  exchangeFormatter the exchange formatter
     * @return                   the exchange formatter or the newly created one if previously unset
     */
    public static ExchangeFormatter getOrCreateExchangeFormatter(
            ZwangineContext zwangineContext, ExchangeFormatter exchangeFormatter) {
        if (exchangeFormatter == null) {
            exchangeFormatter = zwangineContext.getRegistry().findSingleByType(ExchangeFormatter.class);
            if (exchangeFormatter == null) {
                // setup exchange formatter to be used for message history dump
                DefaultExchangeFormatter def = new DefaultExchangeFormatter();
                def.setShowExchangeId(true);
                def.setMultiline(true);
                def.setShowHeaders(true);
                def.setStyle(DefaultExchangeFormatter.OutputStyle.Fixed);
                try {
                    Integer maxChars = ZwangineContextHelper.parseInteger(zwangineContext,
                            zwangineContext.getGlobalOption(Exchange.LOG_DEBUG_BODY_MAX_CHARS));
                    if (maxChars != null) {
                        def.setMaxChars(maxChars);
                    }
                } catch (Exception e) {
                    throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
                }
                exchangeFormatter = def;
            }
        }
        return exchangeFormatter;
    }

    /**
     * Gets an environment variable from the system (parsing and adjusting the name according to the OS)
     *
     * @param  name the environment variable name
     * @return      the environment variable value
     */
    public static String sysenv(String name) {
        return IOHelper.lookupEnvironmentVariable(name);
    }

    public static String escapeQuotes(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char prev = i > 0 ? text.charAt(i - 1) : 0;
            char ch = text.charAt(i);

            if (ch == '"' && (i == 0 || prev != '\\')) {
                sb.append('\\');
                sb.append('"');
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static Date dateFromFileLastModified(Exchange exchange, String command) {
        Date date;
        Long num = exchange.getIn().getHeader(Exchange.FILE_LAST_MODIFIED, Long.class);
        if (num != null && num > 0) {
            date = new Date(num);
        } else {
            date = exchange.getIn().getHeader(Exchange.FILE_LAST_MODIFIED, Date.class);
            if (date == null) {
                throw new IllegalArgumentException(
                        "Cannot find " + Exchange.FILE_LAST_MODIFIED + " header at command: " + command);
            }
        }
        return date;
    }

    private static Date toDate(Exchange exchange, BiFunction<Exchange, Object, Date> orElseFunction, Object obj) {
        if (obj instanceof Date date) {
            return date;
        } else if (obj instanceof Long date) {
            return new Date(date);
        } else {
            if (orElseFunction != null) {
                return orElseFunction.apply(exchange, obj);
            }
        }
        return null;
    }

    public static Date dateFromExchangeProperty(
            Exchange exchange, String command, BiFunction<Exchange, Object, Date> orElseFunction) {
        final String key = command.substring(command.lastIndexOf('.') + 1);
        final Object obj = exchange.getProperty(key);

        return toDate(exchange, orElseFunction, obj);
    }

    public static Date dateFromHeader(Exchange exchange, String command, BiFunction<Exchange, Object, Date> orElseFunction) {
        final String key = command.substring(command.lastIndexOf('.') + 1);
        final Object obj = exchange.getMessage().getHeader(key);
        return toDate(exchange, orElseFunction, obj);
    }

    public static Date dateFromVariable(Exchange exchange, String command, BiFunction<Exchange, Object, Date> orElseFunction) {
        final String key = command.substring(command.lastIndexOf('.') + 1);
        final Object obj = exchange.getVariable(key);
        return toDate(exchange, orElseFunction, obj);
    }

    /**
     * Extracts the creation date from an exchange
     *
     * @param  exchange the exchange to extract the create date
     * @return          A Date instance
     */
    public static Date dateFromExchangeCreated(Exchange exchange) {
        return exchange.getClock().asDate();
    }

    /**
     * For the given offsets to a given Date instance and, optionally, convert it to a pattern. NOTE: this is for
     * internal use of Zwangine
     *
     * @param  date     the date to apply the offset
     * @param  offsets  the numeric offset as a milliseconds from epoch
     * @param  pattern  the (optional) date pattern to convert the given date to
     * @param  timezone the timezone for the pattern
     * @return          A new Date instance with the offsets applied to it *or* a String-based if a pattern is provided
     */
    public static Object applyDateOffsets(final Date date, List<Long> offsets, String pattern, String timezone) {
        // Apply offsets
        long dateAsLong = date.getTime();
        for (long offset : offsets) {
            dateAsLong += offset;
        }

        if (pattern != null && !pattern.isEmpty()) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            if (timezone != null && !timezone.isEmpty()) {
                df.setTimeZone(TimeZone.getTimeZone(timezone));
            }
            return df.format(new Date(dateAsLong));
        } else {
            return new Date(dateAsLong);
        }
    }

    public static List<Long> captureOffsets(String commandWithOffsets, Pattern offsetPattern) {
        // Capture optional time offsets
        final List<Long> offsets = new ArrayList<>();
        Matcher offsetMatcher = offsetPattern.matcher(commandWithOffsets);
        while (offsetMatcher.find()) {
            String time = offsetMatcher.group(2).trim();
            long value = TimeUtils.toMilliSeconds(time);
            offsets.add(offsetMatcher.group(1).equals("+") ? value : -value);
        }
        return offsets;
    }
}
