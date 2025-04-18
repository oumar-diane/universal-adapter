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

import org.zenithblox.*;

/**
 * SPI for tracing messages.
 */
public interface Tracer extends StaticService {

    /**
     * Whether or not to trace the given processor definition.
     *
     * @param  definition the processor definition
     * @return            <tt>true</tt> to trace, <tt>false</tt> to skip tracing
     */
    boolean shouldTrace(NamedNode definition);

    /**
     * Trace before the workflow (eg input to workflow)
     *
     * @param workflow    the workflow EIP
     * @param exchange the exchange
     */
    void traceBeforeWorkflow(NamedWorkflow workflow, Exchange exchange);

    /**
     * Trace before the given node
     *
     * @param node     the node EIP
     * @param exchange the exchange
     */
    void traceBeforeNode(NamedNode node, Exchange exchange);

    /**
     * Trace after the given node
     *
     * @param node     the node EIP
     * @param exchange the exchange
     */
    void traceAfterNode(NamedNode node, Exchange exchange);

    /**
     * Trace when an Exchange was sent to a given endpoint
     *
     * @param node     the node EIP
     * @param exchange the exchange
     * @param endpoint the endpoint the exchange was sent to
     * @param elapsed  time in millis for sending the exchange
     */
    void traceSentNode(NamedNode node, Exchange exchange, Endpoint endpoint, long elapsed);

    /**
     * Trace after the workflow (eg output from workflow)
     *
     * @param workflow    the workflow EIP
     * @param exchange the exchange
     */
    void traceAfterWorkflow(NamedWorkflow workflow, Exchange exchange);

    /**
     * Number of traced messages
     */
    long getTraceCounter();

    /**
     * Reset trace counter
     */
    void resetTraceCounter();

    /**
     * Whether the tracer is enabled
     */
    boolean isEnabled();

    /**
     * Whether the tracer is enabled
     */
    void setEnabled(boolean enabled);

    /**
     * Whether the tracer is standby.
     * <p>
     * If a tracer is in standby then the tracer is activated during startup and are ready to be enabled manually via
     * JMX or calling the enabled method.
     */
    boolean isStandby();

    /**
     * Whether the tracer is standby.
     * <p>
     * If a tracer is in standby then the tracer is activated during startup and are ready to be enabled manually via
     * JMX or calling the enabled method.
     */
    void setStandby(boolean standby);

    /**
     * Whether to trace workflows that is created from Rest DSL.
     */
    boolean isTraceRests();

    /**
     * Whether to trace workflows that is created from workflow templates or kamelets.
     */
    void setTraceRests(boolean traceRests);

    /**
     * Whether tracing should trace inner details from workflow templates (or kamelets). Turning this off can reduce the
     * verbosity of tracing when using many workflow templates, and allow to focus on tracing your own Zwangine workflows only.
     */
    boolean isTraceTemplates();

    /**
     * Whether tracing should trace inner details from workflow templates (or kamelets). Turning this off can reduce the
     * verbosity of tracing when using many workflow templates, and allow to focus on tracing your own Zwangine workflows only.
     */
    void setTraceTemplates(boolean traceTemplates);

    /**
     * Tracing pattern to match which node EIPs to trace. For example to match all To EIP nodes, use to*. The pattern
     * matches by node and workflow id's Multiple patterns can be separated by comma.
     */
    String getTracePattern();

    /**
     * Tracing pattern to match which node EIPs to trace. For example to match all To EIP nodes, use to*. The pattern
     * matches by node and workflow id's Multiple patterns can be separated by comma.
     */
    void setTracePattern(String tracePattern);

    /**
     * Whether to include tracing of before/after workflows to trace the input and responses of workflows.
     */
    boolean isTraceBeforeAndAfterWorkflow();

    /**
     * Whether to include tracing of before/after workflows to trace the input and responses of workflows.
     */
    void setTraceBeforeAndAfterWorkflow(boolean traceBeforeAndAfterWorkflow);

    /**
     * To use a custom exchange formatter for formatting the output of the {@link Exchange} in the trace logs.
     */
    ExchangeFormatter getExchangeFormatter();

    /**
     * To use a custom exchange formatter for formatting the output of the {@link Exchange} in the trace logs.
     */
    void setExchangeFormatter(ExchangeFormatter exchangeFormatter);
}
