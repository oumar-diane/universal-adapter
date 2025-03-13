package org.zenithblox.throttling;

import org.zenithblox.LoggingLevel;
import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class ThrottlingExceptionWorkflowPolicyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        ThrottlingExceptionWorkflowPolicy target = (ThrottlingExceptionWorkflowPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "exceptions": target.setExceptions(property(zwangineContext, java.lang.String.class, value)); return true;
            case "failurethreshold":
            case "failureThreshold": target.setFailureThreshold(property(zwangineContext, int.class, value)); return true;
            case "failurewindow":
            case "failureWindow": target.setFailureWindow(property(zwangineContext, long.class, value)); return true;
            case "halfopenafter":
            case "halfOpenAfter": target.setHalfOpenAfter(property(zwangineContext, long.class, value)); return true;
            case "halfopenhandler":
            case "halfOpenHandler": target.setHalfOpenHandler(property(zwangineContext, ThrottlingExceptionHalfOpenHandler.class, value)); return true;
            case "keepopen":
            case "keepOpen": target.setKeepOpen(property(zwangineContext, boolean.class, value)); return true;
            case "statelogginglevel":
            case "stateLoggingLevel": target.setStateLoggingLevel(property(zwangineContext, LoggingLevel.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "exceptions": return java.lang.String.class;
            case "failurethreshold":
            case "failureThreshold": return int.class;
            case "failurewindow":
            case "failureWindow": return long.class;
            case "halfopenafter":
            case "halfOpenAfter": return long.class;
            case "halfopenhandler":
            case "halfOpenHandler": return ThrottlingExceptionHalfOpenHandler.class;
            case "keepopen":
            case "keepOpen": return boolean.class;
            case "statelogginglevel":
            case "stateLoggingLevel": return LoggingLevel.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        ThrottlingExceptionWorkflowPolicy target = (ThrottlingExceptionWorkflowPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "exceptions": return target.getExceptions();
            case "failurethreshold":
            case "failureThreshold": return target.getFailureThreshold();
            case "failurewindow":
            case "failureWindow": return target.getFailureWindow();
            case "halfopenafter":
            case "halfOpenAfter": return target.getHalfOpenAfter();
            case "halfopenhandler":
            case "halfOpenHandler": return target.getHalfOpenHandler();
            case "keepopen":
            case "keepOpen": return target.getKeepOpen();
            case "statelogginglevel":
            case "stateLoggingLevel": return target.getStateLoggingLevel();
            default: return null;
        }
    }
}


