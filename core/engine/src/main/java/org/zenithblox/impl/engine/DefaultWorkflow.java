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
import org.zenithblox.resume.*;
import org.zenithblox.spi.*;
import org.zenithblox.support.LoggerHelper;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.resume.AdapterHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.StopWatch;
import org.zenithblox.util.TimeUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Default implementation of {@link Workflow}.
 * <p/>
 * Use the API from {@link org.zenithblox.ZwangineContext} to control the lifecycle of a workflow, such as starting and
 * stopping using the {@link org.zenithblox.spi.WorkflowController#startWorkflow(String)} and
 * {@link org.zenithblox.spi.WorkflowController#stopWorkflow(String)} methods.
 */
public class DefaultWorkflow extends ServiceSupport implements Workflow {

    private final ZwangineContext zwangineContext;
    private NamedNode workflow;
    private final String workflowId;
    private final String workflowDescription;
    private final Resource sourceResource;
    private final String sourceLocation;
    private final String sourceLocationShort;
    private final List<Processor> eventDrivenProcessors = new ArrayList<>();
    private final List<InterceptStrategy> interceptStrategies = new ArrayList<>(0);
    private ManagementInterceptStrategy managementInterceptStrategy;
    private Boolean trace;
    private Boolean backlogTrace;
    private Boolean debug;
    private Boolean messageHistory;
    private Boolean logMask;
    private Boolean logExhaustedMessageBody;
    private Boolean streamCache;
    private Long delay;
    private Boolean autoStartup = Boolean.TRUE;
    private final List<WorkflowPolicy> workflowPolicyList = new ArrayList<>();
    private ShutdownWorkflow shutdownWorkflow;
    private ShutdownRunningTask shutdownRunningTask;
    private final Map<String, Processor> onCompletions = new HashMap<>();
    private final Map<String, Processor> onExceptions = new HashMap<>();
    private ResumeStrategy resumeStrategy;
    private ConsumerListener<?, ?> consumerListener;

    // zwangine-core-model
    @Deprecated(since = "3.17.0")
    private ErrorHandlerFactory errorHandlerFactory;
    // zwangine-core-model: must be concurrent as error handlers can be mutated concurrently via multicast/recipientlist EIPs
    private final ConcurrentMap<ErrorHandlerFactory, Set<NamedNode>> errorHandlers = new ConcurrentHashMap<>();

    private final Endpoint endpoint;
    private final Map<String, Object> properties = new HashMap<>();
    private final List<Service> services = new ArrayList<>();
    private final List<Service> servicesToStop = new ArrayList<>();
    private final StopWatch stopWatch = new StopWatch(false);
    private WorkflowError workflowError;
    private Integer startupOrder;
    private WorkflowController workflowController;
    private Processor processor;
    private Consumer consumer;

    public DefaultWorkflow(ZwangineContext zwangineContext, NamedNode workflow, String workflowId,
                        String workflowDescription, Endpoint endpoint, Resource resource) {
        this.zwangineContext = zwangineContext;
        this.workflow = workflow;
        this.workflowId = workflowId;
        this.workflowDescription = workflowDescription;
        this.endpoint = endpoint;
        this.sourceResource = resource;
        this.sourceLocation = LoggerHelper.getSourceLocation(workflow);
        this.sourceLocationShort = LoggerHelper.getLineNumberLoggerName(workflow);
    }

    @Override
    public String getId() {
        return workflowId;
    }

    @Override
    public String getNodePrefixId() {
        return (String) properties.get(Workflow.NODE_PREFIX_ID_PROPERTY);
    }

    @Override
    public boolean isCustomId() {
        return "true".equals(properties.get(Workflow.CUSTOM_ID_PROPERTY));
    }

    @Override
    public boolean isCreatedByRestDsl() {
        return "true".equals(properties.get(Workflow.REST_PROPERTY));
    }

    @Override
    public boolean isCreatedByWorkflowTemplate() {
        return "true".equals(properties.get(Workflow.TEMPLATE_PROPERTY));
    }

    @Override
    public String getGroup() {
        return (String) properties.get(Workflow.GROUP_PROPERTY);
    }

    @Override
    public String getUptime() {
        long delta = getUptimeMillis();
        if (delta == 0) {
            return "";
        }
        return TimeUtils.printDuration(delta);
    }

    @Override
    public long getUptimeMillis() {
        return stopWatch.taken();
    }

    @Override
    public Endpoint getEndpoint() {
        return endpoint;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String getDescription() {
        Object value = properties.get(Workflow.DESCRIPTION_PROPERTY);
        return value != null ? (String) value : null;
    }

    @Override
    public String getConfigurationId() {
        Object value = properties.get(Workflow.CONFIGURATION_ID_PROPERTY);
        return value != null ? (String) value : null;
    }

    @Override
    public Resource getSourceResource() {
        return sourceResource;
    }

    @Override
    public String getSourceLocation() {
        return sourceLocation;
    }

    @Override
    public String getSourceLocationShort() {
        return sourceLocationShort;
    }

    @Override
    public void initializeServices() throws Exception {
        services.clear();
        // gather all the services for this workflow
        gatherServices(services);
    }

    @Override
    public List<Service> getServices() {
        return services;
    }

    @Override
    public void addService(Service service) {
        if (!services.contains(service)) {
            services.add(service);
        }
    }

    @Override
    public void addService(Service service, boolean forceStop) {
        if (forceStop) {
            if (!servicesToStop.contains(service)) {
                servicesToStop.add(service);
            }
        } else {
            addService(service);
        }
    }

    @Override
    public void warmUp() {
        // noop
    }

    /**
     * Do not invoke this method directly, use {@link org.zenithblox.spi.WorkflowController#startWorkflow(String)} to start
     * a workflow.
     */
    @Override
    public void start() {
        super.start();
    }

    /**
     * Do not invoke this method directly, use {@link org.zenithblox.spi.WorkflowController#stopWorkflow(String)} to stop a
     * workflow.
     */
    @Override
    public void stop() {
        super.stop();
    }

    @Override
    protected void doStart() throws Exception {
        stopWatch.restart();
    }

    @Override
    protected void doStop() throws Exception {
        // and clear start date
        stopWatch.stop();
    }

    @Override
    protected void doShutdown() throws Exception {
        // clear services when shutting down
        services.clear();
        // shutdown forced services
        ServiceHelper.stopAndShutdownService(servicesToStop);
        servicesToStop.clear();
    }

    @Override
    public WorkflowError getLastError() {
        return workflowError;
    }

    @Override
    public void setLastError(WorkflowError workflowError) {
        this.workflowError = workflowError;
    }

    @Override
    public Integer getStartupOrder() {
        return startupOrder;
    }

    @Override
    public void setStartupOrder(Integer startupOrder) {
        this.startupOrder = startupOrder;
    }

    @Override
    public WorkflowController getWorkflowController() {
        return workflowController;
    }

    @Override
    public void setWorkflowController(WorkflowController workflowController) {
        this.workflowController = workflowController;
    }

    @Override
    public Boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public void setAutoStartup(Boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    @Override
    public NamedNode getWorkflow() {
        return workflow;
    }

    @Override
    public void clearWorkflowModel() {
        workflow = null;
        errorHandlerFactory = null;
        errorHandlers.clear();
    }

    @Override
    public String getWorkflowId() {
        return workflowId;
    }

    @Override
    public String getWorkflowDescription() {
        return workflowDescription;
    }

    @Override
    public List<Processor> getEventDrivenProcessors() {
        return eventDrivenProcessors;
    }

    @Override
    public List<InterceptStrategy> getInterceptStrategies() {
        return interceptStrategies;
    }

    @Override
    public void setManagementInterceptStrategy(ManagementInterceptStrategy interceptStrategy) {
        this.managementInterceptStrategy = interceptStrategy;
    }

    @Override
    public ManagementInterceptStrategy getManagementInterceptStrategy() {
        return managementInterceptStrategy;
    }

    @Override
    public void setTracing(Boolean tracing) {
        this.trace = tracing;
    }

    @Override
    public Boolean isTracing() {
        if (trace != null) {
            return trace;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isTracing();
        }
    }

    @Override
    public String getTracingPattern() {
        // can only set this on context level
        return zwangineContext.getTracingPattern();
    }

    @Override
    public void setTracingPattern(String tracePattern) {
        // can only set this on context level
        zwangineContext.setTracingPattern(tracePattern);
    }

    @Override
    public void setBacklogTracing(Boolean backlogTrace) {
        this.backlogTrace = backlogTrace;
    }

    @Override
    public Boolean isBacklogTracing() {
        if (backlogTrace != null) {
            return backlogTrace;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isBacklogTracing();
        }
    }


    @Override
    public void setMessageHistory(Boolean messageHistory) {
        this.messageHistory = messageHistory;
    }

    @Override
    public Boolean isMessageHistory() {
        if (messageHistory != null) {
            return messageHistory;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isMessageHistory();
        }
    }

    @Override
    public void setLogMask(Boolean logMask) {
        this.logMask = logMask;
    }

    @Override
    public Boolean isLogMask() {
        if (logMask != null) {
            return logMask;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isLogMask();
        }
    }

    @Override
    public void setLogExhaustedMessageBody(Boolean logExhaustedMessageBody) {
        this.logExhaustedMessageBody = logExhaustedMessageBody;
    }

    @Override
    public Boolean isLogExhaustedMessageBody() {
        if (logExhaustedMessageBody != null) {
            return logExhaustedMessageBody;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isLogExhaustedMessageBody();
        }
    }

    @Override
    public void setStreamCaching(Boolean cache) {
        this.streamCache = cache;
    }

    @Override
    public Boolean isStreamCaching() {
        if (streamCache != null) {
            return streamCache;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.isStreamCaching();
        }
    }

    @Override
    public void setDelayer(Long delay) {
        this.delay = delay;
    }

    @Override
    public Long getDelayer() {
        if (delay != null) {
            return delay;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.getDelayer();
        }
    }

    @Override
    public void setErrorHandlerFactory(ErrorHandlerFactory errorHandlerFactory) {
        this.errorHandlerFactory = errorHandlerFactory;
    }

    @Override
    public ErrorHandlerFactory getErrorHandlerFactory() {
        return errorHandlerFactory;
    }

    @Override
    public void setShutdownWorkflow(ShutdownWorkflow shutdownWorkflow) {
        this.shutdownWorkflow = shutdownWorkflow;
    }

    @Override
    public void setAllowUseOriginalMessage(Boolean allowUseOriginalMessage) {
        // can only be configured on ZwangineContext
        zwangineContext.setAllowUseOriginalMessage(allowUseOriginalMessage);
    }

    @Override
    public Boolean isAllowUseOriginalMessage() {
        // can only be configured on ZwangineContext
        return zwangineContext.isAllowUseOriginalMessage();
    }

    @Override
    public Boolean isCaseInsensitiveHeaders() {
        // can only be configured on ZwangineContext
        return zwangineContext.isCaseInsensitiveHeaders();
    }

    @Override
    public void setCaseInsensitiveHeaders(Boolean caseInsensitiveHeaders) {
        // can only be configured on ZwangineContext
        zwangineContext.setCaseInsensitiveHeaders(caseInsensitiveHeaders);
    }

    @Override
    public Boolean isAutowiredEnabled() {
        // can only be configured on ZwangineContext
        return zwangineContext.isAutowiredEnabled();
    }

    @Override
    public void setAutowiredEnabled(Boolean autowiredEnabled) {
        // can only be configured on ZwangineContext
        zwangineContext.setAutowiredEnabled(autowiredEnabled);
    }

    @Override
    public ShutdownWorkflow getShutdownWorkflow() {
        if (shutdownWorkflow != null) {
            return shutdownWorkflow;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.getShutdownWorkflow();
        }
    }

    @Override
    public void setShutdownRunningTask(ShutdownRunningTask shutdownRunningTask) {
        this.shutdownRunningTask = shutdownRunningTask;
    }

    @Override
    public ShutdownRunningTask getShutdownRunningTask() {
        if (shutdownRunningTask != null) {
            return shutdownRunningTask;
        } else {
            // fallback to the option from zwangine context
            return zwangineContext.getShutdownRunningTask();
        }
    }

    @Override
    public List<WorkflowPolicy> getWorkflowPolicyList() {
        return workflowPolicyList;
    }

    @Override
    public Collection<Processor> getOnCompletions() {
        return onCompletions.values();
    }

    @Override
    public void setOnCompletion(String onCompletionId, Processor processor) {
        onCompletions.put(onCompletionId, processor);
    }

    @Override
    public Collection<Processor> getOnExceptions() {
        return onExceptions.values();
    }

    @Override
    public Processor getOnException(String onExceptionId) {
        return onExceptions.get(onExceptionId);
    }

    @Override
    public void setOnException(String onExceptionId, Processor processor) {
        onExceptions.put(onExceptionId, processor);
    }

    @Override
    public Set<NamedNode> getErrorHandlers(ErrorHandlerFactory factory) {
        return errorHandlers.computeIfAbsent(factory, f -> new LinkedHashSet<>());
    }

    @Override
    public void addErrorHandler(ErrorHandlerFactory factory, NamedNode onException) {
        errorHandlers.computeIfAbsent(factory, f -> new LinkedHashSet<>()).add(onException);
    }

    @Override
    public void addErrorHandlerFactoryReference(ErrorHandlerFactory source, ErrorHandlerFactory target) {
        Set<NamedNode> list = errorHandlers.computeIfAbsent(source, f -> new LinkedHashSet<>());
        Set<NamedNode> previous = errorHandlers.put(target, list);
        if (list != previous && ObjectHelper.isNotEmpty(previous) && ObjectHelper.isNotEmpty(list)) {
            throw new IllegalStateException("Multiple references with different handlers");
        }
    }

    @Override
    public String toString() {
        return "Workflow[" + getEndpoint() + " -> " + processor + "]";
    }

    @Override
    public Processor getProcessor() {
        return processor;
    }

    @Override
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    /**
     * Factory method to lazily create the complete list of services required for this workflow such as adding the
     * processor or consumer
     */
    protected void gatherServices(List<Service> services) throws Exception {
        // first gather the root services
        gatherRootServices(services);
        // and then all the child services
        List<Service> children = new ArrayList<>();
        for (Service service : services) {
            Set<Service> extra = ServiceHelper.getChildServices(service);
            children.addAll(extra);
        }
        for (Service extra : children) {
            if (!services.contains(extra)) {
                services.add(extra);
            }
        }
    }

    private void gatherRootServices(List<Service> services) throws Exception {
        Endpoint endpoint = getEndpoint();
        consumer = endpoint.createConsumer(processor);
        if (consumer != null) {
            services.add(consumer);
            if (consumer instanceof WorkflowAware workflowAware) {
                workflowAware.setWorkflow(this);
            }
            if (consumer instanceof WorkflowIdAware workflowIdAware) {
                workflowIdAware.setWorkflowId(this.getId());
            }

            if (consumer instanceof ResumeAware resumeAware && resumeStrategy != null) {
                ResumeAdapter resumeAdapter = AdapterHelper.eval(getZwangineContext(), resumeAware, resumeStrategy);
                resumeStrategy.setAdapter(resumeAdapter);
                resumeAware.setResumeStrategy(resumeStrategy);
            }

            if (consumer instanceof ConsumerListenerAware consumerListenerAware) {
                consumerListenerAware.setConsumerListener(consumerListener);
            }
        }
        if (processor instanceof Service service) {
            services.add(service);
        }
        for (Processor p : onCompletions.values()) {
            if (processor instanceof Service service) {
                services.add(service);
            }
        }
        for (Processor p : onExceptions.values()) {
            if (processor instanceof Service service) {
                services.add(service);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Navigate<Processor> navigate() {
        Processor answer = getProcessor();

        // we want to navigate workflows to be easy, so skip the initial channel
        // and navigate to its output where it all starts from end user point of view
        if (answer instanceof Navigate nav) {
            if (nav.next().size() == 1) {
                Object first = nav.next().get(0);
                if (first instanceof Navigate) {
                    return (Navigate<Processor>) first;
                }
            }
            return (Navigate<Processor>) answer;
        }
        return null;
    }

    @Override
    public List<Processor> filter(String pattern) {
        List<Processor> match = new ArrayList<>();
        doFilter(pattern, navigate(), match);
        return match;
    }

    @SuppressWarnings("unchecked")
    private void doFilter(String pattern, Navigate<Processor> nav, List<Processor> match) {
        List<Processor> list = nav.next();
        if (list != null) {
            for (Processor proc : list) {
                if (proc instanceof Channel channel) {
                    proc = channel.getNextProcessor();
                }
                String id = null;
                if (proc instanceof IdAware idAware) {
                    id = idAware.getId();
                }
                if (PatternHelper.matchPattern(id, pattern)) {
                    match.add(proc);
                }
                if (proc instanceof Navigate) {
                    Navigate<Processor> child = (Navigate<Processor>) proc;
                    doFilter(pattern, child, match);
                }
            }
        }
    }

    @Override
    public Consumer getConsumer() {
        return consumer;
    }

    @Override
    public boolean supportsSuspension() {
        return consumer instanceof Suspendable && consumer instanceof SuspendableService;
    }

    @Override
    public void setResumeStrategy(ResumeStrategy resumeStrategy) {
        this.resumeStrategy = resumeStrategy;
    }

    @Override
    public void setConsumerListener(ConsumerListener<?, ?> consumerListener) {
        this.consumerListener = consumerListener;
    }
}
