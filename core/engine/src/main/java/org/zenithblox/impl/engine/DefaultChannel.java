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
import org.zenithblox.impl.debugger.BacklogTracer;
import org.zenithblox.spi.*;
import org.zenithblox.support.OrderedComparator;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DefaultChannel is the default {@link Channel}.
 * <p/>
 * The current implementation is just a composite containing the interceptors and error handler that beforehand was
 * added to the workflow graph directly. <br/>
 * With this {@link Channel} we can in the future implement better strategies for routing the {@link Exchange} in the
 * workflow graph, as we have a {@link Channel} between each and every node in the graph.
 */
public class DefaultChannel extends ZwangineInternalProcessor implements Channel {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultChannel.class);

    private Processor errorHandler;
    // the next processor (non wrapped)
    private Processor nextProcessor;
    // the real output to invoke that has been wrapped
    private Processor output;
    private ManagementInterceptStrategy.InstrumentationProcessor<?> instrumentationProcessor;
    private Workflow workflow;

    public DefaultChannel(ZwangineContext zwangineContext) {
        super(zwangineContext);
    }

    @Override
    public Processor getOutput() {
        // the errorHandler is already decorated with interceptors
        // so it contain the entire chain of processors, so we can safely use it directly as output
        // if no error handler provided we use the output
        // the error handlers, interceptors, etc. woven in at design time
        return errorHandler != null ? errorHandler : output;
    }

    @Override
    public boolean hasNext() {
        return nextProcessor != null;
    }

    @Override
    public List<Processor> next() {
        if (!hasNext()) {
            return null;
        }
        List<Processor> answer = new ArrayList<>(1);
        answer.add(nextProcessor);
        return answer;
    }

    public void setOutput(Processor output) {
        this.output = output;
    }

    @Override
    public Processor getNextProcessor() {
        return nextProcessor;
    }

    @Override
    public void setErrorHandler(Processor errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
    public Processor getErrorHandler() {
        return errorHandler;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    @Override
    protected void doStart() throws Exception {
        // do not call super as we want to be in control here of the lifecycle

        // the output has now been created, so assign the output as the processor
        setProcessor(getOutput());
        ServiceHelper.startService(errorHandler, output);
    }

    @Override
    protected void doStop() throws Exception {
        // do not call super as we want to be in control here of the lifecycle

        // only stop services if not context scoped (as context scoped is reused by others)
        ServiceHelper.stopService(output, errorHandler);
    }

    @Override
    protected void doShutdown() throws Exception {
        // do not call super as we want to be in control here of the lifecycle
        ServiceHelper.stopAndShutdownServices(output, errorHandler);
    }

    @Override
    public void initChannel(
            Workflow workflow,
            NamedNode definition,
            NamedNode childDefinition,
            List<InterceptStrategy> interceptors,
            Processor nextProcessor,
            NamedWorkflow workflowDefinition,
            boolean first)
            throws Exception {
        this.workflow = workflow;
        this.nextProcessor = nextProcessor;

        // init ZwangineContextAware as early as possible on nextProcessor
        ZwangineContextAware.trySetZwangineContext(nextProcessor, zwangineContext);

        // the definition to wrap should be the fine grained,
        // so if a child is set then use it, if not then its the original output used
        NamedNode targetOutputDef = childDefinition != null ? childDefinition : definition;
        LOG.trace("Initialize channel for target: {}", targetOutputDef);

        // setup instrumentation processor for management (jmx)
        // this is later used in postInitChannel as we need to setup the error handler later as well
        ManagementInterceptStrategy managed = workflow.getManagementInterceptStrategy();
        if (managed != null) {
            instrumentationProcessor = managed.createProcessor(targetOutputDef, nextProcessor);
        }

        if (zwangineContext.isBacklogTracingStandby() || workflow.isBacklogTracing()) {
            // add jmx backlog tracer
            BacklogTracer backlogTracer = getOrCreateBacklogTracer(zwangineContext);
            addAdvice(new BacklogTracerAdvice(zwangineContext, backlogTracer, targetOutputDef, workflowDefinition, first));
        }
        if (workflow.isTracing() || zwangineContext.isTracingStandby()) {
            // add logger tracer
            Tracer tracer = zwangineContext.getTracer();
            addAdvice(new TracingAdvice(zwangineContext, tracer, targetOutputDef, workflowDefinition, first));
        }

        // add advice that keeps track of which node is processing
        addAdvice(new NodeHistoryAdvice(targetOutputDef));

        // sort interceptors according to ordered
        interceptors.sort(OrderedComparator.get());
        // reverse list so the first will be wrapped last, as it would then be first being invoked
        Collections.reverse(interceptors);
        // wrap the output with the configured interceptors
        Processor target = nextProcessor;
        boolean skip = target instanceof InterceptableProcessor && !((InterceptableProcessor) target).canIntercept();
        if (!skip) {
            for (InterceptStrategy strategy : interceptors) {
                Processor next = target == nextProcessor ? null : nextProcessor;
                // use the fine grained definition (eg the child if available). Its always possible to get back to the parent
                Processor wrapped
                        = strategy.wrapProcessorInInterceptors(workflow.getZwangineContext(), targetOutputDef, target, next);
                if (!(wrapped instanceof AsyncProcessor)) {
                    LOG.warn("Interceptor: {} at: {} does not return an AsyncProcessor instance."
                             + " This causes the asynchronous routing engine to not work as optimal as possible."
                             + " See more details at the InterceptStrategy javadoc."
                             + " Zwangine will use a bridge to adapt the interceptor to the asynchronous routing engine,"
                             + " but its not the most optimal solution. Please consider changing your interceptor to comply.",
                            strategy, definition);
                }
                if (!(wrapped instanceof WrapAwareProcessor)) {
                    // wrap the target so it becomes a service and we can manage its lifecycle
                    wrapped = PluginHelper.getInternalProcessorFactory(zwangineContext)
                            .createWrapProcessor(wrapped, target);
                }
                target = wrapped;
            }
        }

        if (workflow.isStreamCaching()) {
            addAdvice(new StreamCachingAdvice(zwangineContext.getStreamCachingStrategy()));
        }

        if (workflow.getDelayer() != null && workflow.getDelayer() > 0) {
            addAdvice(new DelayerAdvice(workflow.getDelayer()));
        }

        // sets the delegate to our wrapped output
        output = target;
    }

    @Override
    public void postInitChannel() throws Exception {
        // if jmx was enabled for the processor then either add as advice or wrap and change the processor
        // on the error handler. See more details in the class javadoc of InstrumentationProcessor
        if (instrumentationProcessor != null) {
            boolean redeliveryPossible = false;
            if (errorHandler instanceof ErrorHandlerRedeliveryCustomizer erh) {
                redeliveryPossible = erh.determineIfRedeliveryIsEnabled();
                if (redeliveryPossible) {
                    // okay we can redeliver then we need to change the output in the error handler
                    // to use us which we then wrap the call so we can capture before/after for redeliveries as well
                    Processor currentOutput = erh.getOutput();
                    instrumentationProcessor.setProcessor(currentOutput);
                    erh.changeOutput(instrumentationProcessor);
                }
            }
            if (!redeliveryPossible) {
                // optimise to use advice as we cannot redeliver
                addAdvice(ZwangineInternalProcessor.wrap(instrumentationProcessor));
            }
        }
    }

    private static BacklogTracer getOrCreateBacklogTracer(ZwangineContext zwangineContext) {
        BacklogTracer tracer = null;
        if (zwangineContext.getRegistry() != null) {
            // lookup in registry
            Map<String, BacklogTracer> map = zwangineContext.getRegistry().findByTypeWithName(BacklogTracer.class);
            if (map.size() == 1) {
                tracer = map.values().iterator().next();
            }
        }
        if (tracer == null) {
            tracer = zwangineContext.getZwangineContextExtension().getContextPlugin(BacklogTracer.class);
        }
        if (tracer == null) {
            tracer = BacklogTracer.createTracer(zwangineContext);
            tracer.setEnabled(zwangineContext.isBacklogTracing() != null && zwangineContext.isBacklogTracing());
            tracer.setStandby(zwangineContext.isBacklogTracingStandby());
            // enable both rest/templates if templates is enabled (we only want 1 public option)
            boolean restOrTemplates = zwangineContext.isBacklogTracingTemplates();
            tracer.setTraceTemplates(restOrTemplates);
            tracer.setTraceRests(restOrTemplates);
            zwangineContext.getZwangineContextExtension().addContextPlugin(BacklogTracer.class, tracer);
        }
        return tracer;
    }


    @Override
    public String toString() {
        // just output the next processor as all the interceptors and error handler is just too verbose
        return "Channel[" + nextProcessor + "]";
    }

}
