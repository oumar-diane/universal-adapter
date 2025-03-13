package org.zenithblox.impl;

import org.zenithblox.ShutdownRunningTask;
import org.zenithblox.ShutdownWorkflow;
import org.zenithblox.StartupSummaryLevel;
import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.*;
import org.zenithblox.support.component.PropertyConfigurerSupport;
import org.zenithblox.support.jsse.SSLContextParameters;
import org.zenithblox.vault.VaultConfiguration;

public class ZwangineContextConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        ZwangineContext target = (ZwangineContext) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowuseoriginalmessage":
            case "allowUseOriginalMessage": target.setAllowUseOriginalMessage(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "applicationcontextclassloader":
            case "applicationContextClassLoader": target.setApplicationContextClassLoader(property(zwangineContext, java.lang.ClassLoader.class, value)); return true;
            case "autostartup":
            case "autoStartup": target.setAutoStartup(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "autowiredenabled":
            case "autowiredEnabled": target.setAutowiredEnabled(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "backlogtracing":
            case "backlogTracing": target.setBacklogTracing(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "backlogtracingstandby":
            case "backlogTracingStandby": target.setBacklogTracingStandby(property(zwangineContext, boolean.class, value)); return true;
            case "backlogtracingtemplates":
            case "backlogTracingTemplates": target.setBacklogTracingTemplates(property(zwangineContext, boolean.class, value)); return true;
            case "caseinsensitiveheaders":
            case "caseInsensitiveHeaders": target.setCaseInsensitiveHeaders(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "classresolver":
            case "classResolver": target.setClassResolver(property(zwangineContext, ClassResolver.class, value)); return true;
            case "debugstandby":
            case "debugStandby": target.setDebugStandby(property(zwangineContext, boolean.class, value)); return true;
            case "delayer": target.setDelayer(property(zwangineContext, java.lang.Long.class, value)); return true;
            case "devconsole":
            case "dumproutes":
            case "dumpWorkflows": target.setDumpWorkflows(property(zwangineContext, java.lang.String.class, value)); return true;
            case "executorservicemanager":
            case "executorServiceManager": target.setExecutorServiceManager(property(zwangineContext, ExecutorServiceManager.class, value)); return true;
            case "globaloptions":
            case "globalOptions": target.setGlobalOptions(property(zwangineContext, java.util.Map.class, value)); return true;
            case "inflightrepository":
            case "inflightRepository": target.setInflightRepository(property(zwangineContext, InflightRepository.class, value)); return true;
            case "injector": target.setInjector(property(zwangineContext, Injector.class, value)); return true;
            case "loadhealthchecks":
            case "loadHealthChecks": target.setLoadHealthChecks(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "loadtypeconverters":
            case "loadTypeConverters": target.setLoadTypeConverters(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": target.setLogExhaustedMessageBody(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "logmask":
            case "logMask": target.setLogMask(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "mdcloggingkeyspattern":
            case "mDCLoggingKeysPattern": target.setMDCLoggingKeysPattern(property(zwangineContext, java.lang.String.class, value)); return true;
            case "managementname":
            case "managementName": target.setManagementName(property(zwangineContext, java.lang.String.class, value)); return true;
            case "managementnamestrategy":
            case "managementNameStrategy": target.setManagementNameStrategy(property(zwangineContext, ManagementNameStrategy.class, value)); return true;
            case "managementstrategy":
            case "managementStrategy": target.setManagementStrategy(property(zwangineContext, ManagementStrategy.class, value)); return true;
            case "messagehistory":
            case "messageHistory": target.setMessageHistory(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "messagehistoryfactory":
            case "messageHistoryFactory": target.setMessageHistoryFactory(property(zwangineContext, MessageHistoryFactory.class, value)); return true;
            case "modeline": target.setModeline(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "namestrategy":
            case "nameStrategy": target.setNameStrategy(property(zwangineContext, ZwangineContextNameStrategy.class, value)); return true;
            case "propertiescomponent":
            case "propertiesComponent": target.setPropertiesComponent(property(zwangineContext, PropertiesComponent.class, value)); return true;
            case "restconfiguration":
            case "restConfiguration": target.setRestConfiguration(property(zwangineContext, RestConfiguration.class, value)); return true;
            case "restregistry":
            case "restRegistry": target.setRestRegistry(property(zwangineContext, RestRegistry.class, value)); return true;
            case "routecontroller":
            case "routeController": target.setWorkflowController(property(zwangineContext, WorkflowController.class, value)); return true;
            case "runtimeendpointregistry":
            case "runtimeEndpointRegistry": target.setRuntimeEndpointRegistry(property(zwangineContext, RuntimeEndpointRegistry.class, value)); return true;
            case "sslcontextparameters":
            case "sSLContextParameters": target.setSSLContextParameters(property(zwangineContext, SSLContextParameters.class, value)); return true;
            case "shutdownroute":
            case "shutdownWorkflow": target.setShutdownWorkflow(property(zwangineContext, ShutdownWorkflow.class, value)); return true;
            case "shutdownrunningtask":
            case "shutdownRunningTask": target.setShutdownRunningTask(property(zwangineContext, ShutdownRunningTask.class, value)); return true;
            case "shutdownstrategy":
            case "shutdownStrategy": target.setShutdownStrategy(property(zwangineContext, ShutdownStrategy.class, value)); return true;
            case "sourcelocationenabled":
            case "sourceLocationEnabled": target.setSourceLocationEnabled(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "startupsummarylevel":
            case "startupSummaryLevel": target.setStartupSummaryLevel(property(zwangineContext, StartupSummaryLevel.class, value)); return true;
            case "streamcaching":
            case "streamCaching": target.setStreamCaching(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "streamcachingstrategy":
            case "streamCachingStrategy": target.setStreamCachingStrategy(property(zwangineContext, StreamCachingStrategy.class, value)); return true;
            case "tracer": target.setTracer(property(zwangineContext, Tracer.class, value)); return true;
            case "tracing": target.setTracing(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "tracingloggingformat":
            case "tracingLoggingFormat": target.setTracingLoggingFormat(property(zwangineContext, java.lang.String.class, value)); return true;
            case "tracingpattern":
            case "tracingPattern": target.setTracingPattern(property(zwangineContext, java.lang.String.class, value)); return true;
            case "tracingstandby":
            case "tracingStandby": target.setTracingStandby(property(zwangineContext, boolean.class, value)); return true;
            case "tracingtemplates":
            case "tracingTemplates": target.setTracingTemplates(property(zwangineContext, boolean.class, value)); return true;
            case "typeconverterregistry":
            case "typeConverterRegistry": target.setTypeConverterRegistry(property(zwangineContext, TypeConverterRegistry.class, value)); return true;
            case "typeconverterstatisticsenabled":
            case "typeConverterStatisticsEnabled": target.setTypeConverterStatisticsEnabled(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "usebreadcrumb":
            case "useBreadcrumb": target.setUseBreadcrumb(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "usedatatype":
            case "useDataType": target.setUseDataType(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "usemdclogging":
            case "useMDCLogging": target.setUseMDCLogging(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "uuidgenerator":
            case "uuidGenerator": target.setUuidGenerator(property(zwangineContext, UuidGenerator.class, value)); return true;
            case "vaultconfiguration":
            case "vaultConfiguration": target.setVaultConfiguration(property(zwangineContext, VaultConfiguration.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowuseoriginalmessage":
            case "allowUseOriginalMessage": return java.lang.Boolean.class;
            case "applicationcontextclassloader":
            case "applicationContextClassLoader": return java.lang.ClassLoader.class;
            case "autostartup":
            case "autoStartup": return java.lang.Boolean.class;
            case "autowiredenabled":
            case "autowiredEnabled": return java.lang.Boolean.class;
            case "backlogtracing":
            case "backlogTracing": return java.lang.Boolean.class;
            case "backlogtracingstandby":
            case "backlogTracingStandby": return boolean.class;
            case "backlogtracingtemplates":
            case "backlogTracingTemplates": return boolean.class;
            case "caseinsensitiveheaders":
            case "caseInsensitiveHeaders": return java.lang.Boolean.class;
            case "classresolver":
            case "classResolver": return ClassResolver.class;
            case "debugstandby":
            case "debugStandby": return boolean.class;
            case "debugging": return java.lang.Boolean.class;
            case "delayer": return java.lang.Long.class;
            case "devconsole":
            case "devConsole": return java.lang.Boolean.class;
            case "dumproutes":
            case "dumpWorkflows": return java.lang.String.class;
            case "executorservicemanager":
            case "executorServiceManager": return ExecutorServiceManager.class;
            case "globaloptions":
            case "globalOptions": return java.util.Map.class;
            case "inflightrepository":
            case "inflightRepository": return InflightRepository.class;
            case "injector": return Injector.class;
            case "loadhealthchecks":
            case "loadHealthChecks": return java.lang.Boolean.class;
            case "loadtypeconverters":
            case "loadTypeConverters": return java.lang.Boolean.class;
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": return java.lang.Boolean.class;
            case "logmask":
            case "logMask": return java.lang.Boolean.class;
            case "mdcloggingkeyspattern":
            case "mDCLoggingKeysPattern": return java.lang.String.class;
            case "managementname":
            case "managementName": return java.lang.String.class;
            case "managementnamestrategy":
            case "managementNameStrategy": return ManagementNameStrategy.class;
            case "managementstrategy":
            case "managementStrategy": return ManagementStrategy.class;
            case "messagehistory":
            case "messageHistory": return java.lang.Boolean.class;
            case "messagehistoryfactory":
            case "messageHistoryFactory": return MessageHistoryFactory.class;
            case "modeline": return java.lang.Boolean.class;
            case "namestrategy":
            case "nameStrategy": return ZwangineContextNameStrategy.class;
            case "propertiescomponent":
            case "propertiesComponent": return PropertiesComponent.class;
            case "restconfiguration":
            case "restConfiguration": return RestConfiguration.class;
            case "restregistry":
            case "restRegistry": return RestRegistry.class;
            case "routecontroller":
            case "routeController": return WorkflowController.class;
            case "runtimeendpointregistry":
            case "runtimeEndpointRegistry": return RuntimeEndpointRegistry.class;
            case "sslcontextparameters":
            case "sSLContextParameters": return SSLContextParameters.class;
            case "shutdownroute":
            case "shutdownWorkflow": return ShutdownWorkflow.class;
            case "shutdownrunningtask":
            case "shutdownRunningTask": return ShutdownRunningTask.class;
            case "shutdownstrategy":
            case "shutdownStrategy": return ShutdownStrategy.class;
            case "sourcelocationenabled":
            case "sourceLocationEnabled": return java.lang.Boolean.class;
            case "startupsummarylevel":
            case "startupSummaryLevel": return StartupSummaryLevel.class;
            case "streamcaching":
            case "streamCaching": return java.lang.Boolean.class;
            case "streamcachingstrategy":
            case "streamCachingStrategy": return StreamCachingStrategy.class;
            case "tracer": return Tracer.class;
            case "tracing": return java.lang.Boolean.class;
            case "tracingloggingformat":
            case "tracingLoggingFormat": return java.lang.String.class;
            case "tracingpattern":
            case "tracingPattern": return java.lang.String.class;
            case "tracingstandby":
            case "tracingStandby": return boolean.class;
            case "tracingtemplates":
            case "tracingTemplates": return boolean.class;
            case "typeconverterregistry":
            case "typeConverterRegistry": return TypeConverterRegistry.class;
            case "typeconverterstatisticsenabled":
            case "typeConverterStatisticsEnabled": return java.lang.Boolean.class;
            case "usebreadcrumb":
            case "useBreadcrumb": return java.lang.Boolean.class;
            case "usedatatype":
            case "useDataType": return java.lang.Boolean.class;
            case "usemdclogging":
            case "useMDCLogging": return java.lang.Boolean.class;
            case "uuidgenerator":
            case "uuidGenerator": return UuidGenerator.class;
            case "vaultconfiguration":
            case "vaultConfiguration": return VaultConfiguration.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        ZwangineContext target = (ZwangineContext) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowuseoriginalmessage":
            case "allowUseOriginalMessage": return target.isAllowUseOriginalMessage();
            case "applicationcontextclassloader":
            case "applicationContextClassLoader": return target.getApplicationContextClassLoader();
            case "autostartup":
            case "autoStartup": return target.isAutoStartup();
            case "autowiredenabled":
            case "autowiredEnabled": return target.isAutowiredEnabled();
            case "backlogtracing":
            case "backlogTracing": return target.isBacklogTracing();
            case "backlogtracingstandby":
            case "backlogTracingStandby": return target.isBacklogTracingStandby();
            case "backlogtracingtemplates":
            case "backlogTracingTemplates": return target.isBacklogTracingTemplates();
            case "caseinsensitiveheaders":
            case "caseInsensitiveHeaders": return target.isCaseInsensitiveHeaders();
            case "classresolver":
            case "classResolver": return target.getClassResolver();
            case "debugstandby":
            case "debugStandby": return target.isDebugStandby();
            case "delayer": return target.getDelayer();
            case "devconsole":
            case "dumproutes":
            case "dumpWorkflows": return target.getDumpWorkflows();
            case "executorservicemanager":
            case "executorServiceManager": return target.getExecutorServiceManager();
            case "globaloptions":
            case "globalOptions": return target.getGlobalOptions();
            case "inflightrepository":
            case "inflightRepository": return target.getInflightRepository();
            case "injector": return target.getInjector();
            case "loadhealthchecks":
            case "loadHealthChecks": return target.isLoadHealthChecks();
            case "loadtypeconverters":
            case "loadTypeConverters": return target.isLoadTypeConverters();
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": return target.isLogExhaustedMessageBody();
            case "logmask":
            case "logMask": return target.isLogMask();
            case "mdcloggingkeyspattern":
            case "mDCLoggingKeysPattern": return target.getMDCLoggingKeysPattern();
            case "managementname":
            case "managementName": return target.getManagementName();
            case "managementnamestrategy":
            case "managementNameStrategy": return target.getManagementNameStrategy();
            case "managementstrategy":
            case "managementStrategy": return target.getManagementStrategy();
            case "messagehistory":
            case "messageHistory": return target.isMessageHistory();
            case "messagehistoryfactory":
            case "messageHistoryFactory": return target.getMessageHistoryFactory();
            case "modeline": return target.isModeline();
            case "namestrategy":
            case "nameStrategy": return target.getNameStrategy();
            case "propertiescomponent":
            case "propertiesComponent": return target.getPropertiesComponent();
            case "restconfiguration":
            case "restConfiguration": return target.getRestConfiguration();
            case "restregistry":
            case "restRegistry": return target.getRestRegistry();
            case "routecontroller":
            case "routeController": return target.getWorkflowController();
            case "runtimeendpointregistry":
            case "runtimeEndpointRegistry": return target.getRuntimeEndpointRegistry();
            case "sslcontextparameters":
            case "sSLContextParameters": return target.getSSLContextParameters();
            case "shutdownroute":
            case "shutdownWorkflow": return target.getShutdownWorkflow();
            case "shutdownrunningtask":
            case "shutdownRunningTask": return target.getShutdownRunningTask();
            case "shutdownstrategy":
            case "shutdownStrategy": return target.getShutdownStrategy();
            case "sourcelocationenabled":
            case "sourceLocationEnabled": return target.isSourceLocationEnabled();
            case "startupsummarylevel":
            case "startupSummaryLevel": return target.getStartupSummaryLevel();
            case "streamcaching":
            case "streamCaching": return target.isStreamCaching();
            case "streamcachingstrategy":
            case "streamCachingStrategy": return target.getStreamCachingStrategy();
            case "tracer": return target.getTracer();
            case "tracing": return target.isTracing();
            case "tracingloggingformat":
            case "tracingLoggingFormat": return target.getTracingLoggingFormat();
            case "tracingpattern":
            case "tracingPattern": return target.getTracingPattern();
            case "tracingstandby":
            case "tracingStandby": return target.isTracingStandby();
            case "tracingtemplates":
            case "tracingTemplates": return target.isTracingTemplates();
            case "typeconverterregistry":
            case "typeConverterRegistry": return target.getTypeConverterRegistry();
            case "typeconverterstatisticsenabled":
            case "typeConverterStatisticsEnabled": return target.isTypeConverterStatisticsEnabled();
            case "usebreadcrumb":
            case "useBreadcrumb": return target.isUseBreadcrumb();
            case "usedatatype":
            case "useDataType": return target.isUseDataType();
            case "usemdclogging":
            case "useMDCLogging": return target.isUseMDCLogging();
            case "uuidgenerator":
            case "uuidGenerator": return target.getUuidGenerator();
            case "vaultconfiguration":
            case "vaultConfiguration": return target.getVaultConfiguration();
            default: return null;
        }
    }

    @Override
    public Object getCollectionValueType(Object target, String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "globaloptions":
            case "globalOptions": return java.lang.String.class;
            default: return null;
        }
    }
}
