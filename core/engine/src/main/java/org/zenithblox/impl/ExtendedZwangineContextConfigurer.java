package org.zenithblox.impl;

import org.zenithblox.ErrorHandlerFactory;
import org.zenithblox.ExtendedZwangineContext;
import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.*;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class ExtendedZwangineContextConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        ExtendedZwangineContext target = (ExtendedZwangineContext) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "basepackagescan":
            case "basePackageScan": target.setBasePackageScan(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bootstrapfactoryfinder":
            case "bootstrapFactoryFinder": target.setBootstrapFactoryFinder(property(zwangineContext, FactoryFinder.class, value)); return true;
            case "defaultfactoryfinder":
            case "defaultFactoryFinder": target.setDefaultFactoryFinder(property(zwangineContext, FactoryFinder.class, value)); return true;
            case "description": target.setDescription(property(zwangineContext, java.lang.String.class, value)); return true;
            case "endpointserviceregistry":
            case "endpointServiceRegistry": target.setEndpointServiceRegistry(property(zwangineContext, EndpointServiceRegistry.class, value)); return true;
            case "errorhandlerfactory":
            case "errorHandlerFactory": target.setErrorHandlerFactory(property(zwangineContext, ErrorHandlerFactory.class, value)); return true;
            case "eventnotificationapplicable":
            case "eventNotificationApplicable": target.setEventNotificationApplicable(property(zwangineContext, boolean.class, value)); return true;
            case "exchangefactory":
            case "exchangeFactory": target.setExchangeFactory(property(zwangineContext, ExchangeFactory.class, value)); return true;
            case "exchangefactorymanager":
            case "exchangeFactoryManager": target.setExchangeFactoryManager(property(zwangineContext, ExchangeFactoryManager.class, value)); return true;
            case "headersmapfactory":
            case "headersMapFactory": target.setHeadersMapFactory(property(zwangineContext, HeadersMapFactory.class, value)); return true;
            case "managementmbeanassembler":
            case "managementMBeanAssembler": target.setManagementMBeanAssembler(property(zwangineContext, ManagementMBeanAssembler.class, value)); return true;
            case "name": target.setName(property(zwangineContext, java.lang.String.class, value)); return true;
            case "processorexchangefactory":
            case "processorExchangeFactory": target.setProcessorExchangeFactory(property(zwangineContext, ProcessorExchangeFactory.class, value)); return true;
            case "profile": target.setProfile(property(zwangineContext, java.lang.String.class, value)); return true;
            case "reactiveexecutor":
            case "reactiveExecutor": target.setReactiveExecutor(property(zwangineContext, ReactiveExecutor.class, value)); return true;
            case "registry": target.setRegistry(property(zwangineContext, Registry.class, value)); return true;
            case "startupsteprecorder":
            case "startupStepRecorder": target.setStartupStepRecorder(property(zwangineContext, StartupStepRecorder.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "basepackagescan":
            case "basePackageScan": return java.lang.String.class;
            case "bootstrapfactoryfinder":
            case "bootstrapFactoryFinder": return FactoryFinder.class;
            case "defaultfactoryfinder":
            case "defaultFactoryFinder": return FactoryFinder.class;
            case "description": return java.lang.String.class;
            case "endpointserviceregistry":
            case "endpointServiceRegistry": return EndpointServiceRegistry.class;
            case "errorhandlerfactory":
            case "errorHandlerFactory": return ErrorHandlerFactory.class;
            case "eventnotificationapplicable":
            case "eventNotificationApplicable": return boolean.class;
            case "exchangefactory":
            case "exchangeFactory": return ExchangeFactory.class;
            case "exchangefactorymanager":
            case "exchangeFactoryManager": return ExchangeFactoryManager.class;
            case "headersmapfactory":
            case "headersMapFactory": return HeadersMapFactory.class;
            case "managementmbeanassembler":
            case "managementMBeanAssembler": return ManagementMBeanAssembler.class;
            case "name": return java.lang.String.class;
            case "processorexchangefactory":
            case "processorExchangeFactory": return ProcessorExchangeFactory.class;
            case "profile": return java.lang.String.class;
            case "reactiveexecutor":
            case "reactiveExecutor": return ReactiveExecutor.class;
            case "registry": return Registry.class;
            case "startupsteprecorder":
            case "startupStepRecorder": return StartupStepRecorder.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        ExtendedZwangineContext target = (ExtendedZwangineContext) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "basepackagescan":
            case "basePackageScan": return target.getBasePackageScan();
            case "bootstrapfactoryfinder":
            case "bootstrapFactoryFinder": return target.getBootstrapFactoryFinder();
            case "defaultfactoryfinder":
            case "defaultFactoryFinder": return target.getDefaultFactoryFinder();
            case "description": return target.getDescription();
            case "endpointserviceregistry":
            case "endpointServiceRegistry": return target.getEndpointServiceRegistry();
            case "errorhandlerfactory":
            case "errorHandlerFactory": return target.getErrorHandlerFactory();
            case "eventnotificationapplicable":
            case "eventNotificationApplicable": return target.isEventNotificationApplicable();
            case "exchangefactory":
            case "exchangeFactory": return target.getExchangeFactory();
            case "exchangefactorymanager":
            case "exchangeFactoryManager": return target.getExchangeFactoryManager();
            case "headersmapfactory":
            case "headersMapFactory": return target.getHeadersMapFactory();
            case "managementmbeanassembler":
            case "managementMBeanAssembler": return target.getManagementMBeanAssembler();
            case "name": return target.getName();
            case "processorexchangefactory":
            case "processorExchangeFactory": return target.getProcessorExchangeFactory();
            case "profile": return target.getProfile();
            case "reactiveexecutor":
            case "reactiveExecutor": return target.getReactiveExecutor();
            case "registry": return target.getRegistry();
            case "startupsteprecorder":
            case "startupStepRecorder": return target.getStartupStepRecorder();
            default: return null;
        }
    }
}
