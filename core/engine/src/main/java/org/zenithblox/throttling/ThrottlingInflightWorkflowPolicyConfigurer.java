package org.zenithblox.throttling;

import org.zenithblox.LoggingLevel;
import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class ThrottlingInflightWorkflowPolicyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        ThrottlingInflightWorkflowPolicy target = (ThrottlingInflightWorkflowPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "logginglevel":
            case "loggingLevel": target.setLoggingLevel(property(zwangineContext, LoggingLevel.class, value)); return true;
            case "maxinflightexchanges":
            case "maxInflightExchanges": target.setMaxInflightExchanges(property(zwangineContext, int.class, value)); return true;
            case "resumepercentofmax":
            case "resumePercentOfMax": target.setResumePercentOfMax(property(zwangineContext, int.class, value)); return true;
            case "scope": target.setScope(property(zwangineContext, ThrottlingInflightWorkflowPolicy.ThrottlingScope.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "logginglevel":
            case "loggingLevel": return LoggingLevel.class;
            case "maxinflightexchanges":
            case "maxInflightExchanges": return int.class;
            case "resumepercentofmax":
            case "resumePercentOfMax": return int.class;
            case "scope": return ThrottlingInflightWorkflowPolicy.ThrottlingScope.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        ThrottlingInflightWorkflowPolicy target = (ThrottlingInflightWorkflowPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "logginglevel":
            case "loggingLevel": return target.getLoggingLevel();
            case "maxinflightexchanges":
            case "maxInflightExchanges": return target.getMaxInflightExchanges();
            case "resumepercentofmax":
            case "resumePercentOfMax": return target.getResumePercentOfMax();
            case "scope": return target.getScope();
            default: return null;
        }
    }
}

