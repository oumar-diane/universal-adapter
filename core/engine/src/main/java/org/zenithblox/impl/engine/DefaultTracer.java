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
package org.zenithblox.impl.engine;

import org.zenithblox.*;
import org.zenithblox.spi.EndpointServiceLocation;
import org.zenithblox.spi.ExchangeFormatter;
import org.zenithblox.spi.Tracer;
import org.zenithblox.support.ZwangineContextHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.builder.ExpressionBuilder;
import org.zenithblox.support.processor.DefaultExchangeFormatter;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StringHelper;
import org.zenithblox.util.TimeUtils;
import org.zenithblox.util.URISupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static org.zenithblox.support.LoggerHelper.getLineNumberLoggerName;

/**
 * Default {@link Tracer} implementation that will log traced messages to the logger named
 * <tt>org.zenithblox.Tracing</tt>.
 */
public class DefaultTracer extends ServiceSupport implements ZwangineContextAware, Tracer {

    private static final String TRACING_OUTPUT = "%-4.4s [%-12.12s] [%-33.33s]";

    // use a fixed logger name so easy to spot
    private static final Logger LOG = LoggerFactory.getLogger("org.zenithblox.Tracing");

    private String tracingFormat = TRACING_OUTPUT;
    private ZwangineContext zwangineContext;
    private boolean enabled = true;
    private boolean standby;
    private boolean traceRests;
    private boolean traceTemplates;
    private long traceCounter;

    private ExchangeFormatter exchangeFormatter;
    private String tracePattern;
    private transient String[] patterns;
    private boolean traceBeforeAndAfterWorkflow = true;

    public DefaultTracer() {
        DefaultExchangeFormatter formatter = new DefaultExchangeFormatter();
        formatter.setShowExchangeId(true);
        formatter.setShowExchangePattern(false);
        formatter.setMultiline(false);
        formatter.setShowHeaders(true);
        formatter.setStyle(DefaultExchangeFormatter.OutputStyle.Default);
        setExchangeFormatter(formatter);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public void traceBeforeNode(NamedNode node, Exchange exchange) {
        if (shouldTrace(node)) {
            traceCounter++;
            String workflowId = ExpressionBuilder.workflowIdExpression().evaluate(exchange, String.class);

            // we need to avoid leak the sensible information here
            // the sanitizeUri takes a very long time for very long string and the format cuts this to
            // 33 characters, anyway. Cut this to 50 characters. This will give enough space for removing
            // characters in the sanitizeUri method and will be reasonably fast
            String label = URISupport.sanitizeUri(StringHelper.limitLength(node.getLabel(), 50));

            StringBuilder sb = new StringBuilder(512);
            sb.append(String.format(tracingFormat, "   ", workflowId, label));
            sb.append(" ");
            String data = exchangeFormatter.format(exchange);
            sb.append(data);
            String out = sb.toString();
            dumpTrace(out, node);
        }
    }

    @Override
    public void traceAfterNode(NamedNode node, Exchange exchange) {
        // noop
    }

    @Override
    public void traceSentNode(NamedNode node, Exchange exchange, Endpoint endpoint, long elapsed) {
        if (!traceBeforeAndAfterWorkflow) {
            return;
        }

        // skip non-remote endpoints
        if (!endpoint.isRemote()) {
            return;
        }

        if (shouldTrace(node)) {
            String workflowId = ExpressionBuilder.workflowIdExpression().evaluate(exchange, String.class);

            StringBuilder sb = new StringBuilder(128);
            sb.append(String.format(tracingFormat, "   ", workflowId, ""));
            sb.append(" ");

            StringJoiner sj = new StringJoiner(", ");
            sj.add("url=" + endpoint);
            if (endpoint instanceof EndpointServiceLocation esl && esl.getServiceUrl() != null) {
                // enrich with service location
                sj.add("service=" + esl.getServiceUrl());
                String protocol = esl.getServiceProtocol();
                if (protocol != null) {
                    sj.add("protocol=" + protocol);
                }
                Map<String, String> map = esl.getServiceMetadata();
                if (map != null) {
                    map.forEach((k, v) -> sj.add(k + "=" + v));
                }
            }

            boolean failed = exchange.isFailed();
            String data = "Sent " + (failed ? "failed" : "success") + " took " + TimeUtils.printDuration(elapsed, true);
            data += " (" + sj + ")";
            sb.append(data);
            String out = sb.toString();
            dumpTrace(out, node);
        }
    }

    @Override
    public void traceBeforeWorkflow(NamedWorkflow workflow, Exchange exchange) {
        if (!traceBeforeAndAfterWorkflow) {
            return;
        }

        // we need to avoid leak the sensible information here
        // the sanitizeUri takes a very long time for very long string and the format cuts this to
        // 33 characters, anyway. Cut this to 50 characters. This will give enough space for removing
        // characters in the sanitizeUri method and will be reasonably fast
        String uri = workflow.getEndpointUrl();
        String label = "from[" + URISupport.sanitizeUri(StringHelper.limitLength(uri, 50) + "]");

        // the arrow has a * if its a new exchange that is starting
        boolean original = workflow.getWorkflowId().equals(exchange.getFromWorkflowId());
        String arrow = original ? "*-->" : "--->";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(tracingFormat, arrow, workflow.getWorkflowId(), label));
        sb.append(" ");
        String data = exchangeFormatter.format(exchange);
        String out = sb + data;
        dumpTrace(out, workflow);

        // enrich with endpoint service location on incoming request
        if (original) {
            Endpoint endpoint = exchange.getFromEndpoint();
            if (endpoint instanceof EndpointServiceLocation esl && esl.getServiceUrl() != null) {
                // enrich with service location
                StringJoiner sj = new StringJoiner(", ");
                sj.add("url=" + endpoint);
                sj.add("service=" + esl.getServiceUrl());
                String protocol = esl.getServiceProtocol();
                if (protocol != null) {
                    sj.add("protocol=" + protocol);
                }
                Map<String, String> map = esl.getServiceMetadata();
                if (map != null) {
                    map.forEach((k, v) -> sj.add(k + "=" + v));
                }
                data = "Received (" + sj + ")";

                sb = new StringBuilder();
                sb.append(String.format(tracingFormat, "", workflow.getWorkflowId(), ""));
                sb.append(" ");

                out = sb + data;
                dumpTrace(out, workflow);
            }
        }
    }

    @Override
    public void traceAfterWorkflow(NamedWorkflow workflow, Exchange exchange) {
        if (!traceBeforeAndAfterWorkflow) {
            return;
        }

        // we need to avoid leak the sensible information here
        // the sanitizeUri takes a very long time for very long string and the format cuts this to
        // 33 characters, anyway. Cut this to 50 characters. This will give enough space for removing
        // characters in the sanitizeUri method and will be reasonably fast
        String uri = workflow.getEndpointUrl();
        String label = "from[" + URISupport.sanitizeUri(StringHelper.limitLength(uri, 50) + "]");

        // the arrow has a * if its an exchange that is done
        boolean original = workflow.getWorkflowId().equals(exchange.getFromWorkflowId());
        String arrow = original ? "*<--" : "<---";

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(tracingFormat, arrow, workflow.getWorkflowId(), label));
        sb.append(" ");
        String data = exchangeFormatter.format(exchange);
        sb.append(data);
        String out = sb.toString();
        dumpTrace(out, workflow);
    }

    @Override
    public boolean shouldTrace(NamedNode definition) {
        if (!enabled) {
            return false;
        }

        boolean pattern = true;

        if (patterns != null) {
            pattern = shouldTracePattern(definition);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Should trace evaluated {} -> pattern: {}", definition.getId(), pattern);
        }
        return pattern;
    }

    @Override
    public long getTraceCounter() {
        return traceCounter;
    }

    @Override
    public void resetTraceCounter() {
        traceCounter = 0;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isStandby() {
        return standby;
    }

    @Override
    public void setStandby(boolean standby) {
        this.standby = standby;
    }

    @Override
    public boolean isTraceRests() {
        return traceRests;
    }

    @Override
    public void setTraceRests(boolean traceRests) {
        this.traceRests = traceRests;
    }

    @Override
    public boolean isTraceTemplates() {
        return traceTemplates;
    }

    @Override
    public void setTraceTemplates(boolean traceTemplates) {
        this.traceTemplates = traceTemplates;
    }

    @Override
    public String getTracePattern() {
        return tracePattern;
    }

    @Override
    public void setTracePattern(String tracePattern) {
        this.tracePattern = tracePattern;
        if (tracePattern != null) {
            // the pattern can have multiple nodes separated by comma
            this.patterns = tracePattern.split(",");
        } else {
            this.patterns = null;
        }
    }

    @Override
    public boolean isTraceBeforeAndAfterWorkflow() {
        return traceBeforeAndAfterWorkflow;
    }

    @Override
    public void setTraceBeforeAndAfterWorkflow(boolean traceBeforeAndAfterWorkflow) {
        this.traceBeforeAndAfterWorkflow = traceBeforeAndAfterWorkflow;
    }

    @Override
    public ExchangeFormatter getExchangeFormatter() {
        return exchangeFormatter;
    }

    @Override
    public void setExchangeFormatter(ExchangeFormatter exchangeFormatter) {
        this.exchangeFormatter = exchangeFormatter;
    }

    protected void dumpTrace(String out, Object node) {
        String name = getLineNumberLoggerName(node);
        if (name != null) {
            Logger log = LoggerFactory.getLogger(name);
            log.info(out);
        } else {
            LOG.info(out);
        }
    }

    protected boolean shouldTracePattern(NamedNode definition) {
        for (String pattern : patterns) {
            // match either workflow id, or node id
            String id = definition.getId();
            // use matchPattern method from endpoint helper that has a good matcher we use in Zwangine
            if (PatternHelper.matchPattern(id, pattern)) {
                return true;
            }
            String workflowId = ZwangineContextHelper.getWorkflowId(definition);
            if (workflowId != null && !Objects.equals(workflowId, id)) {
                if (PatternHelper.matchPattern(workflowId, pattern)) {
                    return true;
                }
            }
        }
        // not matched the pattern
        return false;
    }

    @Override
    protected void doStart() throws Exception {
        if (getZwangineContext().getTracingLoggingFormat() != null) {
            tracingFormat = getZwangineContext().getTracingLoggingFormat();
        }
    }

}
