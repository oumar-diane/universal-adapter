package org.zenithblox.impl;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class DefaultDumpWorkflowsStrategyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        DefaultDumpWorkflowsStrategy target = (DefaultDumpWorkflowsStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "zwanginecontext":
            case "zwangineContext": target.setZwangineContext(property(zwangineContext, ZwangineContext.class, value)); return true;
            case "generatedids":
            case "generatedIds": target.setGeneratedIds(property(zwangineContext, boolean.class, value)); return true;
            case "include": target.setInclude(property(zwangineContext, java.lang.String.class, value)); return true;
            case "log": target.setLog(property(zwangineContext, boolean.class, value)); return true;
            case "output": target.setOutput(property(zwangineContext, java.lang.String.class, value)); return true;
            case "resolveplaceholders":
            case "resolvePlaceholders": target.setResolvePlaceholders(property(zwangineContext, boolean.class, value)); return true;
            case "uriasparameters":
            case "uriAsParameters": target.setUriAsParameters(property(zwangineContext, boolean.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "zwanginecontext":
            case "zwangineContext": return ZwangineContext.class;
            case "generatedids":
            case "generatedIds": return boolean.class;
            case "include": return java.lang.String.class;
            case "log": return boolean.class;
            case "output": return java.lang.String.class;
            case "resolveplaceholders":
            case "resolvePlaceholders": return boolean.class;
            case "uriasparameters":
            case "uriAsParameters": return boolean.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        DefaultDumpWorkflowsStrategy target = (DefaultDumpWorkflowsStrategy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "zwanginecontext":
            case "zwangineContext": return target.getZwangineContext();
            case "generatedids":
            case "generatedIds": return target.isGeneratedIds();
            case "include": return target.getInclude();
            case "log": return target.isLog();
            case "output": return target.getOutput();
            case "resolveplaceholders":
            case "resolvePlaceholders": return target.isResolvePlaceholders();
            case "uriasparameters":
            case "uriAsParameters": return target.isUriAsParameters();
            default: return null;
        }
    }
}
