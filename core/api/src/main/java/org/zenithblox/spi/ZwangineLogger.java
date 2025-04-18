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
package org.zenithblox.spi;

import org.zenithblox.LoggingLevel;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * A logger which logs to a slf4j {@link Logger}.
 * <p/>
 * The name <tt>ZwangineLogger</tt> has been chosen to avoid any name clash with log kits which has a <tt>Logger</tt>
 * class.
 */
public class ZwangineLogger {
    private Logger log;
    private LoggingLevel level;
    private Marker marker;

    public ZwangineLogger() {
        this(LoggerFactory.getLogger(ZwangineLogger.class));
    }

    public ZwangineLogger(Logger log) {
        this(log, LoggingLevel.INFO);
    }

    public ZwangineLogger(Logger log, LoggingLevel level) {
        this(log, level, null);
    }

    public ZwangineLogger(Logger log, LoggingLevel level, String marker) {
        this.log = log;
        setLevel(level);
        setMarker(marker);
    }

    public ZwangineLogger(String logName) {
        this(LoggerFactory.getLogger(logName));
    }

    public ZwangineLogger(String logName, LoggingLevel level) {
        this(logName, level, null);
    }

    public ZwangineLogger(String logName, LoggingLevel level, String marker) {
        this(LoggerFactory.getLogger(logName), level, marker);
    }

    @Override
    public String toString() {
        return "Logger[" + log + "]";
    }

    public void log(String message, LoggingLevel loggingLevel) {
        LoggingLevel oldLogLevel = getLevel();
        setLevel(loggingLevel);
        log(message);
        setLevel(oldLogLevel);
    }

    /**
     * Logs the message <b>with</b> checking the {@link #shouldLog()} method first.
     *
     * @param message the message to log, if {@link #shouldLog()} returned <tt>true</tt>
     */
    public void log(String message) {
        if (shouldLog(log, level)) {
            if (marker != null) {
                log(log, level, marker, message);
            } else {
                log(log, level, message);
            }
        }
    }

    /**
     * Logs the message <b>without</b> checking the {@link #shouldLog()} method first.
     *
     * @param message the message to log
     */
    public void doLog(String message) {
        if (marker != null) {
            log(log, level, marker, message);
        } else {
            log(log, level, message);
        }
    }

    public void log(String message, Throwable exception, LoggingLevel loggingLevel) {
        log(log, loggingLevel, marker, message, exception);
    }

    public void log(String message, Throwable exception) {
        if (shouldLog(log, level)) {
            log(log, level, marker, message, exception);
        }
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    public LoggingLevel getLevel() {
        return level;
    }

    public void setLevel(LoggingLevel level) {
        if (level == null) {
            throw new IllegalArgumentException("Log level may not be null");
        }

        this.level = level;
    }

    public void setLogName(String logName) {
        this.log = LoggerFactory.getLogger(logName);
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public void setMarker(String marker) {
        if (ObjectHelper.isNotEmpty(marker)) {
            this.marker = MarkerFactory.getMarker(marker);
        } else {
            this.marker = null;
        }
    }

    public static void log(Logger log, LoggingLevel level, String message) {
        switch (level) {
            case DEBUG:
                log.debug(message);
                break;
            case ERROR:
                log.error(message);
                break;
            case INFO:
                log.info(message);
                break;
            case TRACE:
                log.trace(message);
                break;
            case WARN:
                log.warn(message);
                break;
            default:
        }
    }

    public static void log(Logger log, LoggingLevel level, Marker marker, String message) {
        switch (level) {
            case DEBUG:
                log.debug(marker, message);
                break;
            case ERROR:
                log.error(marker, message);
                break;
            case INFO:
                log.info(marker, message);
                break;
            case TRACE:
                log.trace(marker, message);
                break;
            case WARN:
                log.warn(marker, message);
                break;
            default:
        }
    }

    public static void log(Logger log, LoggingLevel level, String message, Throwable th) {
        switch (level) {
            case DEBUG:
                log.debug(message, th);
                break;
            case ERROR:
                log.error(message, th);
                break;
            case INFO:
                log.info(message, th);
                break;
            case TRACE:
                log.trace(message, th);
                break;
            case WARN:
                log.warn(message, th);
                break;
            default:
        }
    }

    public static void log(Logger log, LoggingLevel level, Marker marker, String message, Throwable th) {
        if (marker == null) {
            log(log, level, message, th);
            return;
        }

        // marker must be provided
        switch (level) {
            case DEBUG:
                log.debug(marker, message, th);
                break;
            case ERROR:
                log.error(marker, message, th);
                break;
            case INFO:
                log.info(marker, message, th);
                break;
            case TRACE:
                log.trace(marker, message, th);
                break;
            case WARN:
                log.warn(marker, message, th);
                break;
            default:
        }
    }

    public boolean shouldLog() {
        return ZwangineLogger.shouldLog(log, level);
    }

    public static boolean shouldLog(Logger log, LoggingLevel level) {
        switch (level) {
            case DEBUG:
                return log.isDebugEnabled();
            case ERROR:
                return log.isErrorEnabled();
            case INFO:
                return log.isInfoEnabled();
            case TRACE:
                return log.isTraceEnabled();
            case WARN:
                return log.isWarnEnabled();
            default:
        }
        return false;
    }
}
