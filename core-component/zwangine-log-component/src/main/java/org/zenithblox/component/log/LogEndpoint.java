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

import org.zenithblox.*;
import org.zenithblox.spi.*;
import org.zenithblox.support.ProcessorEndpoint;
import org.zenithblox.support.processor.ZwangineLogProcessor;
import org.zenithblox.support.processor.DefaultExchangeFormatter;
import org.zenithblox.support.processor.DefaultMaskingFormatter;
import org.zenithblox.support.processor.ThroughputLogger;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.zenithblox.spi.ExchangeFormatter;

import static org.zenithblox.support.LoggerHelper.getLineNumberLoggerName;

/**
 * Prints data form the routed message (such as body and headers) to the logger.
 */
@UriEndpoint(firstVersion = "1.1.0", scheme = "log", title = "Log Data",
             remote = false, syntax = "log:loggerName", producerOnly = true, category = { Category.CORE, Category.MONITORING })
public class LogEndpoint extends ProcessorEndpoint implements LineNumberAware {

    private volatile Processor logger;
    private Logger providedLogger;
    private ExchangeFormatter localFormatter;
    private int lineNumber;
    private String location;

    @UriPath(description = "Name of the logging category to use")
    @Metadata(required = true)
    private String loggerName;
    @UriParam(defaultValue = "INFO", enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private String level;
    @UriParam
    private String marker;
    @UriParam
    private Integer groupSize;
    @UriParam
    private Long groupInterval;
    @UriParam(defaultValue = "true")
    private Boolean groupActiveOnly;
    @UriParam
    private Long groupDelay;
    @UriParam
    private Boolean logMask;
    @UriParam(label = "advanced")
    private ExchangeFormatter exchangeFormatter;
    @UriParam(label = "formatting", description = "Show route ID.")
    private boolean showWorkflowId;
    @UriParam(label = "formatting", description = "Show route Group.")
    private boolean showWorkflowGroup;
    @UriParam(label = "formatting", description = "Show the unique exchange ID.")
    private boolean showExchangeId;
    @UriParam(label = "formatting",
              description = "Shows the Message Exchange Pattern (or MEP for short).")
    private boolean showExchangePattern;
    @UriParam(label = "formatting",
              description = "Show the exchange properties (only custom). Use showAllProperties to show both internal and custom properties.")
    private boolean showProperties;
    @UriParam(label = "formatting", description = "Show all of the exchange properties (both internal and custom).")
    private boolean showAllProperties;
    @UriParam(label = "formatting", description = "Show the variables.")
    private boolean showVariables;
    @UriParam(label = "formatting", description = "Show the message headers.")
    private boolean showHeaders;
    @UriParam(label = "formatting", defaultValue = "true",
              description = "Whether to skip line separators when logging the message body."
                            + " This allows to log the message body in one line, setting this option to false will preserve any line separators from the body, which then will log the body as is.")
    private boolean skipBodyLineSeparator = true;
    @UriParam(label = "formatting", defaultValue = "true", description = "Show the message body.")
    private boolean showBody = true;
    @UriParam(label = "formatting", defaultValue = "true", description = "Show the body Java type.")
    private boolean showBodyType = true;
    @UriParam(label = "formatting",
              description = "If the exchange has an exception, show the exception message (no stacktrace)")
    private boolean showException;
    @UriParam(label = "formatting",
              description = "If the exchange has a caught exception, show the exception message (no stack trace)."
                            + " A caught exception is stored as a property on the exchange (using the key org.zenithblox.Exchange#EXCEPTION_CAUGHT) and for instance a doCatch can catch exceptions.")
    private boolean showCaughtException;
    @UriParam(label = "formatting",
              description = "Show the stack trace, if an exchange has an exception. Only effective if one of showAll, showException or showCaughtException are enabled.")
    private boolean showStackTrace;
    @UriParam(label = "formatting",
              description = "Quick option for turning all options on. (multiline, maxChars has to be manually set if to be used)")
    private boolean showAll;
    @UriParam(label = "formatting", description = "If enabled then each information is outputted on a newline.")
    private boolean multiline;
    @UriParam(label = "formatting",
              description = "If enabled Zwangine will on Future objects wait for it to complete to obtain the payload to be logged.")
    private boolean showFuture;
    @UriParam(label = "formatting", defaultValue = "true",
              description = "Whether Zwangine should show cached stream bodies or not (org.zenithblox.StreamCache).")
    private boolean showCachedStreams = true;
    @UriParam(label = "formatting",
              description = "Whether Zwangine should show stream bodies or not (eg such as java.io.InputStream). Beware if you enable this option then "
                            + "you may not be able later to access the message body as the stream have already been read by this logger. To remedy this you will have to use Stream Caching.")
    private boolean showStreams;
    @UriParam(label = "formatting", description = "If enabled Zwangine will output files")
    private boolean showFiles;
    @UriParam(label = "formatting", defaultValue = "10000", description = "Limits the number of characters logged per line.")
    private int maxChars = 10000;
    @UriParam(label = "formatting", enums = "Default,Tab,Fixed", defaultValue = "Default",
              description = "Sets the outputs style to use.")
    private DefaultExchangeFormatter.OutputStyle style = DefaultExchangeFormatter.OutputStyle.Default;
    @UriParam(defaultValue = "false", description = "If enabled only the body will be printed out")
    private boolean plain;
    @UriParam(description = "If enabled then the source location of where the log endpoint is used in Zwangine routes, would be used as logger name, instead"
                            + " of the given name. However, if the source location is disabled or not possible to resolve then the existing logger name will be used.")
    private boolean sourceLocationLoggerName;

    public LogEndpoint() {
    }

    public LogEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    public LogEndpoint(String endpointUri, Component component, Processor logger) {
        super(endpointUri, component);
        setLogger(logger);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        // ensure component is injected
        if (getComponent() == null) {
            setComponent(getZwangineContext().getComponent("log"));
        }

        this.localFormatter = exchangeFormatter;
        if (this.localFormatter == null) {

            // are any options configured if not we can optimize to use shared default
            boolean changed = showExchangePattern || !skipBodyLineSeparator || !showBody || !showBodyType || maxChars != 10000
                    || style != DefaultExchangeFormatter.OutputStyle.Default || plain;
            changed |= showWorkflowId || showWorkflowGroup;
            changed |= showExchangeId || showProperties || showAllProperties || showVariables || showHeaders || showException
                    || showCaughtException
                    || showStackTrace;
            changed |= showAll || multiline || showFuture || !showCachedStreams || showStreams || showFiles;

            if (changed) {
                DefaultExchangeFormatter def = new DefaultExchangeFormatter();
                def.setPlain(plain);
                def.setShowWorkflowId(showWorkflowId);
                def.setShowWorkflowGroup(showWorkflowGroup);
                def.setShowAll(showAll);
                def.setShowBody(showBody);
                def.setShowBodyType(showBodyType);
                def.setShowCaughtException(showCaughtException);
                def.setShowException(showException);
                def.setShowExchangeId(showExchangeId);
                def.setShowExchangePattern(showExchangePattern);
                def.setShowFiles(showFiles);
                def.setShowFuture(showFuture);
                def.setShowHeaders(showHeaders);
                def.setShowVariables(showVariables);
                def.setShowProperties(showProperties);
                def.setShowAllProperties(showAllProperties);
                def.setShowStackTrace(showStackTrace);
                def.setShowCachedStreams(showCachedStreams);
                def.setShowStreams(showStreams);
                def.setMaxChars(maxChars);
                def.setMultiline(multiline);
                def.setSkipBodyLineSeparator(skipBodyLineSeparator);
                def.setStyle(style);
                this.localFormatter = def;
            } else {
                this.localFormatter = getComponent().getDefaultExchangeFormatter();
            }
        }
    }

    @Override
    protected void doStart() throws Exception {
        if (logger == null) {
            logger = createLogger();
        }
        ServiceHelper.startService(logger);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(logger);
    }

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    public void setLogger(Processor logger) {
        this.logger = logger;
        // the logger is the processor
        setProcessor(this.logger);
    }

    public Processor getLogger() {
        return logger;
    }

    @Override
    public Producer createProducer() throws Exception {
        // ensure logger is created and started first
        if (logger == null) {
            logger = createLogger();
        }
        ServiceHelper.startService(logger);
        return new LogProducer(this, logger);
    }

    @Override
    protected String createEndpointUri() {
        return "log:" + logger.toString();
    }

    @Override
    public LogComponent getComponent() {
        return (LogComponent) super.getComponent();
    }

    /**
     * Creates the logger {@link Processor} to be used.
     */
    protected Processor createLogger() {
        Processor answer;
        // setup a new logger here
        ZwangineLogger camelLogger;
        LoggingLevel loggingLevel = LoggingLevel.INFO;
        if (level != null && !level.equals("INFO")) {
            loggingLevel = LoggingLevel.valueOf(level);
        }
        if (providedLogger == null) {
            String name = loggerName;
            if (sourceLocationLoggerName) {
                name = getLineNumberLoggerName(this);
                if (name == null) {
                    name = loggerName;
                }
            }
            camelLogger = new ZwangineLogger(name, loggingLevel, getMarker());
        } else {
            camelLogger = new ZwangineLogger(providedLogger, loggingLevel, getMarker());
        }
        if (getGroupSize() != null) {
            answer = new ThroughputLogger(camelLogger, getGroupSize());
        } else if (getGroupInterval() != null) {
            Boolean groupActiveOnly = getGroupActiveOnly() != null ? getGroupActiveOnly() : Boolean.TRUE;
            Long groupDelay = getGroupDelay();
            answer = new ThroughputLogger(camelLogger, this.getZwangineContext(), getGroupInterval(), groupDelay, groupActiveOnly);
        } else {
            answer = new ZwangineLogProcessor(
                    camelLogger, localFormatter, getMaskingFormatter(),
                    getZwangineContext().getZwangineContextExtension().getLogListeners());
        }
        // the logger is the processor
        setProcessor(answer);
        return answer;
    }

    private MaskingFormatter getMaskingFormatter() {
        if (logMask != null ? logMask : getZwangineContext().isLogMask()) {
            MaskingFormatter formatter = getZwangineContext().getRegistry()
                    .lookupByNameAndType(MaskingFormatter.CUSTOM_LOG_MASK_REF, MaskingFormatter.class);
            if (formatter == null) {
                formatter = new DefaultMaskingFormatter();
            }
            return formatter;
        }
        return null;
    }

    /**
     * Logging level to use.
     * <p/>
     * The default value is INFO.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Logging level to use.
     * <p/>
     * The default value is INFO.
     */
    public void setLevel(String level) {
        this.level = level;
    }

    /**
     * An optional Marker name to use.
     */
    public String getMarker() {
        return marker;
    }

    /**
     * An optional Marker name to use.
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * An integer that specifies a group size for throughput logging.
     */
    public Integer getGroupSize() {
        return groupSize;
    }

    /**
     * An integer that specifies a group size for throughput logging.
     */
    public void setGroupSize(Integer groupSize) {
        this.groupSize = groupSize;
    }

    /**
     * If specified will group message stats by this time interval (in millis)
     */
    public Long getGroupInterval() {
        return groupInterval;
    }

    /**
     * If specified will group message stats by this time interval (in millis)
     */
    public void setGroupInterval(Long groupInterval) {
        this.groupInterval = groupInterval;
    }

    /**
     * If true, will hide stats when no new messages have been received for a time interval, if false, show stats
     * regardless of message traffic.
     */
    public Boolean getGroupActiveOnly() {
        return groupActiveOnly;
    }

    /**
     * If true, will hide stats when no new messages have been received for a time interval, if false, show stats
     * regardless of message traffic.
     */
    public void setGroupActiveOnly(Boolean groupActiveOnly) {
        this.groupActiveOnly = groupActiveOnly;
    }

    /**
     * Set the initial delay for stats (in millis)
     */
    public Long getGroupDelay() {
        return groupDelay;
    }

    /**
     * Set the initial delay for stats (in millis)
     */
    public void setGroupDelay(Long groupDelay) {
        this.groupDelay = groupDelay;
    }

    public Logger getProvidedLogger() {
        return providedLogger;
    }

    public void setProvidedLogger(Logger providedLogger) {
        this.providedLogger = providedLogger;
    }

    /**
     * The logger name to use
     */
    public String getLoggerName() {
        return loggerName;
    }

    /**
     * The logger name to use
     */
    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public Boolean getLogMask() {
        return logMask;
    }

    /**
     * If true, mask sensitive information like password or passphrase in the log.
     */
    public void setLogMask(Boolean logMask) {
        this.logMask = logMask;
    }

    public ExchangeFormatter getExchangeFormatter() {
        return exchangeFormatter;
    }

    /**
     * To use a custom exchange formatter
     */
    public void setExchangeFormatter(ExchangeFormatter exchangeFormatter) {
        this.exchangeFormatter = exchangeFormatter;
    }

    public boolean isShowWorkflowId() {
        return showWorkflowId;
    }

    public void setShowWorkflowId(boolean showWorkflowId) {
        this.showWorkflowId = showWorkflowId;
    }

    public boolean isShowWorkflowGroup() {
        return showWorkflowGroup;
    }

    public void setShowWorkflowGroup(boolean showWorkflowGroup) {
        this.showWorkflowGroup = showWorkflowGroup;
    }

    public boolean isShowExchangeId() {
        return showExchangeId;
    }

    public void setShowExchangeId(boolean showExchangeId) {
        this.showExchangeId = showExchangeId;
    }

    public boolean isShowExchangePattern() {
        return showExchangePattern;
    }

    public void setShowExchangePattern(boolean showExchangePattern) {
        this.showExchangePattern = showExchangePattern;
    }

    public boolean isShowProperties() {
        return showProperties;
    }

    public void setShowProperties(boolean showProperties) {
        this.showProperties = showProperties;
    }

    public boolean isShowAllProperties() {
        return showAllProperties;
    }

    public void setShowAllProperties(boolean showAllProperties) {
        this.showAllProperties = showAllProperties;
    }

    public boolean isShowVariables() {
        return showVariables;
    }

    public void setShowVariables(boolean showVariables) {
        this.showVariables = showVariables;
    }

    public boolean isShowHeaders() {
        return showHeaders;
    }

    public void setShowHeaders(boolean showHeaders) {
        this.showHeaders = showHeaders;
    }

    public boolean isSkipBodyLineSeparator() {
        return skipBodyLineSeparator;
    }

    public void setSkipBodyLineSeparator(boolean skipBodyLineSeparator) {
        this.skipBodyLineSeparator = skipBodyLineSeparator;
    }

    public boolean isShowBody() {
        return showBody;
    }

    public void setShowBody(boolean showBody) {
        this.showBody = showBody;
    }

    public boolean isShowBodyType() {
        return showBodyType;
    }

    public void setShowBodyType(boolean showBodyType) {
        this.showBodyType = showBodyType;
    }

    public boolean isShowException() {
        return showException;
    }

    public void setShowException(boolean showException) {
        this.showException = showException;
    }

    public boolean isShowCaughtException() {
        return showCaughtException;
    }

    public void setShowCaughtException(boolean showCaughtException) {
        this.showCaughtException = showCaughtException;
    }

    public boolean isShowStackTrace() {
        return showStackTrace;
    }

    public void setShowStackTrace(boolean showStackTrace) {
        this.showStackTrace = showStackTrace;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isShowFuture() {
        return showFuture;
    }

    public void setShowFuture(boolean showFuture) {
        this.showFuture = showFuture;
    }

    public boolean isShowCachedStreams() {
        return showCachedStreams;
    }

    public void setShowCachedStreams(boolean showCachedStreams) {
        this.showCachedStreams = showCachedStreams;
    }

    public boolean isShowStreams() {
        return showStreams;
    }

    public void setShowStreams(boolean showStreams) {
        this.showStreams = showStreams;
    }

    public boolean isShowFiles() {
        return showFiles;
    }

    public void setShowFiles(boolean showFiles) {
        this.showFiles = showFiles;
    }

    public int getMaxChars() {
        return maxChars;
    }

    public void setMaxChars(int maxChars) {
        this.maxChars = maxChars;
    }

    public DefaultExchangeFormatter.OutputStyle getStyle() {
        return style;
    }

    public void setStyle(DefaultExchangeFormatter.OutputStyle style) {
        this.style = style;
    }

    public boolean isPlain() {
        return plain;
    }

    public void setPlain(boolean plain) {
        this.plain = plain;
    }

    public boolean isSourceLocationLoggerName() {
        return sourceLocationLoggerName;
    }

    public void setSourceLocationLoggerName(boolean sourceLocationLoggerName) {
        this.sourceLocationLoggerName = sourceLocationLoggerName;
    }
}
