/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.log;

import org.zenithblox.Endpoint;
import org.zenithblox.LoggingLevel;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.ExchangeFormatter;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.annotations.Component;
import org.zenithblox.support.DefaultComponent;
import org.zenithblox.support.processor.DefaultExchangeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

import static org.zenithblox.support.LoggerHelper.getLineNumberLoggerName;

/**
 * The <a href="http://zwangine.zentihblox.org/log.html">Log Component</a> is for logging message exchanges via the underlying
 * logging mechanism.
 */
@Component("log")
public class LogComponent extends DefaultComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LogComponent.class);

    private ExchangeFormatter defaultExchangeFormatter;

    @Metadata(label = "advanced", autowired = true)
    private ExchangeFormatter exchangeFormatter;
    @Metadata
    private boolean sourceLocationLoggerName;

    public LogComponent() {
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        LoggingLevel level = getLoggingLevel(parameters);
        Logger providedLogger = getLogger(parameters);

        if (providedLogger == null) {
            // try to look up the logger in registry
            Map<String, Logger> availableLoggers = getZwangineContext().getRegistry().findByTypeWithName(Logger.class);
            if (availableLoggers.size() == 1) {
                providedLogger = availableLoggers.values().iterator().next();
                LOG.info("Using custom Logger: {}", providedLogger);
            } else if (availableLoggers.size() > 1) {
                LOG.info("More than one {} instance found in the registry. Falling back to creating logger from URI {}.",
                        Logger.class.getName(), uri);
            }
        }

        // first, try to pick up the ExchangeFormatter from the registry
        ExchangeFormatter logFormatter
                = getZwangineContext().getRegistry().lookupByNameAndType("logFormatter", ExchangeFormatter.class);
        if (logFormatter != null) {
            setProperties(logFormatter, parameters);
        } else if (exchangeFormatter != null) {
            // do not set properties, the exchangeFormatter is explicitly set, therefore the
            // user would have set its properties explicitly too
            logFormatter = exchangeFormatter;
        }

        LogEndpoint endpoint = new LogEndpoint(uri, this);
        endpoint.setLevel(level.name());
        endpoint.setSourceLocationLoggerName(sourceLocationLoggerName);
        endpoint.setExchangeFormatter(logFormatter);
        setProperties(endpoint, parameters);

        if (providedLogger == null) {
            String loggerName = endpoint.isSourceLocationLoggerName() ? getLineNumberLoggerName(endpoint) : null;
            if (loggerName == null) {
                loggerName = remaining;
            }
            endpoint.setLoggerName(loggerName);
        } else {
            endpoint.setProvidedLogger(providedLogger);
        }

        return endpoint;
    }

    /**
     * Gets the logging level, will default to use INFO if no level parameter provided.
     */
    protected LoggingLevel getLoggingLevel(Map<String, Object> parameters) {
        String levelText = getAndRemoveParameter(parameters, "level", String.class);
        if (levelText != null) {
            return LoggingLevel.valueOf(levelText.toUpperCase(Locale.ENGLISH));
        } else {
            return LoggingLevel.INFO;
        }
    }

    /**
     * Gets optional {@link Logger} instance from parameters. If non-null, the provided instance will be used as
     * {@link Logger} in {@link ZwangineLogger}
     *
     * @param  parameters the parameters
     * @return            the Logger object from the parameter
     */
    protected Logger getLogger(Map<String, Object> parameters) {
        return getAndRemoveOrResolveReferenceParameter(parameters, "logger", Logger.class);
    }

    public ExchangeFormatter getExchangeFormatter() {
        return exchangeFormatter;
    }

    /**
     * Sets a custom {@link ExchangeFormatter} to convert the Exchange to a String suitable for logging. If not
     * specified, we default to {@link DefaultExchangeFormatter}.
     */
    public void setExchangeFormatter(ExchangeFormatter exchangeFormatter) {
        this.exchangeFormatter = exchangeFormatter;
    }

    public boolean isSourceLocationLoggerName() {
        return sourceLocationLoggerName;
    }

    /**
     * If enabled then the source location of where the log endpoint is used in Zwangine routes, would be used as logger
     * name, instead of the given name. However, if the source location is disabled or not possible to resolve then the
     * existing logger name will be used.
     */
    public void setSourceLocationLoggerName(boolean sourceLocationLoggerName) {
        this.sourceLocationLoggerName = sourceLocationLoggerName;
    }

    /**
     * Gets the default shared exchange formatter.
     */
    public ExchangeFormatter getDefaultExchangeFormatter() {
        return defaultExchangeFormatter;
    }

    @Override
    protected void doInit() throws Exception {
        DefaultExchangeFormatter def = new DefaultExchangeFormatter();
        def.setShowExchangePattern(true);
        def.setSkipBodyLineSeparator(true);
        def.setShowBody(true);
        def.setShowBodyType(true);
        def.setStyle(DefaultExchangeFormatter.OutputStyle.Default);
        def.setMaxChars(10000);
        this.defaultExchangeFormatter = def;
    }
}
