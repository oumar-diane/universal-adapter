/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;
import org.slf4j.Logger;

/**
 * Used for printing custom messages to the logger.
 */
@Metadata(label = "eip,routing", title = "Logger")
public class LogDefinition extends NoOutputDefinition<LogDefinition> {

    private Logger loggerBean;

    private String message;
    @Metadata(javaType = "org.zenithblox.LoggingLevel", defaultValue = "INFO", enums = "TRACE,DEBUG,INFO,WARN,ERROR,OFF")
    private String loggingLevel;
    private String logName;
    @Metadata(label = "advanced")
    private String marker;
    @Metadata(label = "advanced", javaType = "org.slf4j.Logger")
    private String logger;
    @Metadata(label = "advanced")
    private String logLanguage;

    public LogDefinition() {
    }

    protected LogDefinition(LogDefinition source) {
        super(source);
        this.loggerBean = source.loggerBean;
        this.message = source.message;
        this.loggingLevel = source.loggingLevel;
        this.logName = source.logName;
        this.marker = source.marker;
        this.logger = source.logger;
    }

    public LogDefinition(String message) {
        this();
        this.message = message;
    }

    @Override
    public LogDefinition copyDefinition() {
        return new LogDefinition(this);
    }

    @Override
    public String toString() {
        return "Log[" + message + "]";
    }

    @Override
    public String getShortName() {
        return "log";
    }

    @Override
    public String getLabel() {
        return "log";
    }

    public Logger getLoggerBean() {
        return loggerBean;
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    /**
     * Sets the logging level.
     * <p/>
     * The default value is INFO
     */
    public void setLoggingLevel(String loggingLevel) {
        this.loggingLevel = loggingLevel;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Sets the log message (uses simple language)
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public String getLogName() {
        return logName;
    }

    /**
     * Sets the name of the logger.
     *
     * The name is default the workflowId or the source:line if source location is enabled. You can also specify the name
     * using tokens:
     *
     * <br/>
     * ${class} - the logger class name (org.zenithblox.processor.LogProcessor) <br/>
     * ${contextId} - the zwangine context id <br/>
     * ${workflowId} - the workflow id <br/>
     * ${groupId} - the workflow group id <br/>
     * ${nodeId} - the node id <br/>
     * ${nodePrefixId} - the node prefix id <br/>
     * ${source} - the source:line (source location must be enabled) <br/>
     * ${source.name} - the source filename (source location must be enabled) <br/>
     * ${source.line} - the source line number (source location must be enabled)
     *
     * For example to use the workflow and node id you can specify the name as: ${workflowId}/${nodeId}
     */
    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getMarker() {
        return marker;
    }

    /**
     * To use slf4j marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * To refer to a custom logger instance to lookup from the registry.
     */
    public void setLogger(String logger) {
        this.logger = logger;
    }

    /**
     * To use a custom logger instance
     */
    public void setLogger(Logger logger) {
        this.loggerBean = logger;
    }

    public String getLogger() {
        return logger;
    }

    public String getLogLanguage() {
        return logLanguage;
    }

    /**
     * To configure the language to use. By default, the simple language is used. However, Zwangine also supports other
     * languages such as groovy.
     */
    public void setLogLanguage(String logLanguage) {
        this.logLanguage = logLanguage;
    }
}
