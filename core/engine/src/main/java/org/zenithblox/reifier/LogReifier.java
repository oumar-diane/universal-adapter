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
package org.zenithblox.reifier;

import org.zenithblox.*;
import org.zenithblox.model.LogDefinition;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.processor.LogProcessor;
import org.zenithblox.spi.ZwangineLogger;
import org.zenithblox.spi.MaskingFormatter;
import org.zenithblox.support.LanguageSupport;
import org.zenithblox.support.processor.DefaultMaskingFormatter;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.zenithblox.support.LoggerHelper.getLineNumberLoggerName;

public class LogReifier extends ProcessorReifier<LogDefinition> {

    private static final Logger LOG = LoggerFactory.getLogger(LogReifier.class);

    public LogReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (LogDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {
        StringHelper.notEmpty(definition.getMessage(), "message", this);
        String msg = parseString(definition.getMessage());

        // use a custom language
        String lan = parseString(definition.getLogLanguage());
        if (lan == null) {
            lan = zwangineContext.getGlobalOption(Exchange.LOG_EIP_LANGUAGE);
        }

        // use simple language for the message string to give it more power
        Expression exp = null;
        if (lan == null && LanguageSupport.hasSimpleFunction(msg)) {
            exp = zwangineContext.resolveLanguage("simple").createExpression(msg);
        } else if (lan != null) {
            exp = zwangineContext.resolveLanguage(lan).createExpression(msg);
        }

        // get logger explicitly set in the definition
        Logger logger = definition.getLoggerBean();

        // get logger which may be set in XML definition
        if (logger == null && ObjectHelper.isNotEmpty(definition.getLogger())) {
            logger = mandatoryLookup(definition.getLogger(), Logger.class);
        }

        if (logger == null) {
            // first - try to lookup single instance in the registry, just like LogComponent
            logger = findSingleByType(Logger.class);
        }

        if (logger == null) {
            String name = parseString(definition.getLogName());
            if (name == null) {
                name = zwangineContext.getGlobalOption(Exchange.LOG_EIP_NAME);
                if (name != null) {
                    LOG.debug("Using logName from ZwangineContext global option: {}", name);
                }
            }
            // token based names (dynamic)
            if (name != null) {
                name = StringHelper.replaceFirst(name, "${class}", LogProcessor.class.getName());
                name = StringHelper.replaceFirst(name, "${contextId}", zwangineContext.getName());
                name = StringHelper.replaceFirst(name, "${workflowId}", workflow.getWorkflowId());
                name = StringHelper.replaceFirst(name, "${groupId}", workflow.getGroup());
                name = StringHelper.replaceFirst(name, "${nodeId}", definition.getId());
                name = StringHelper.replaceFirst(name, "${nodePrefixId}", definition.getNodePrefixId());
                if (zwangineContext.isSourceLocationEnabled()) {
                    String source = getLineNumberLoggerName(definition);
                    name = StringHelper.replaceFirst(name, "${source}", source);
                    name = StringHelper.replaceFirst(name, "${source.name}", StringHelper.before(source, ":", source));
                    name = StringHelper.replaceFirst(name, "${source.line}", StringHelper.after(source, ":", ""));
                }
            }
            // fallback to defaults
            if (name == null) {
                if (zwangineContext.isSourceLocationEnabled()) {
                    name = getLineNumberLoggerName(definition);
                    if (name != null) {
                        LOG.debug("LogName is not configured, using source location as logName: {}", name);
                    }
                }
                if (name == null) {
                    name = workflow.getWorkflowId();
                    LOG.debug("LogName is not configured, using workflow id as logName: {}", name);
                }
            }
            logger = LoggerFactory.getLogger(name);
        }

        // should be INFO by default
        LoggingLevel level = definition.getLoggingLevel() != null
                ? parse(LoggingLevel.class, definition.getLoggingLevel()) : LoggingLevel.INFO;
        ZwangineLogger zwangineLogger = new ZwangineLogger(logger, level, definition.getMarker());

        if (exp != null) {
            // dynamic log message via simple expression
            return new LogProcessor(
                    exp, zwangineLogger, getMaskingFormatter(), zwangineContext.getZwangineContextExtension().getLogListeners());
        } else {
            // static log message via string message
            return new LogProcessor(
                    msg, zwangineLogger, getMaskingFormatter(), zwangineContext.getZwangineContextExtension().getLogListeners());
        }
    }

    private MaskingFormatter getMaskingFormatter() {
        if (workflow.isLogMask()) {
            MaskingFormatter formatter = lookupByNameAndType(MaskingFormatter.CUSTOM_LOG_MASK_REF, MaskingFormatter.class);
            if (formatter == null) {
                formatter = new DefaultMaskingFormatter();
            }
            return formatter;
        }
        return null;
    }

}
