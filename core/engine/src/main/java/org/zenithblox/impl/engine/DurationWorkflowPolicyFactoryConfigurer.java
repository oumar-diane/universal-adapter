package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class DurationWorkflowPolicyFactoryConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        DurationWorkflowPolicyFactory target = (DurationWorkflowPolicyFactory) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "action": target.setAction(property(zwangineContext, DurationWorkflowPolicy.Action.class, value)); return true;
            case "fromrouteid":
            case "fromWorkflowId": target.setFromWorkflowId(property(zwangineContext, java.lang.String.class, value)); return true;
            case "maxmessages":
            case "maxMessages": target.setMaxMessages(property(zwangineContext, int.class, value)); return true;
            case "maxseconds":
            case "maxSeconds": target.setMaxSeconds(property(zwangineContext, int.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "action": return DurationWorkflowPolicy.Action.class;
            case "fromrouteid":
            case "fromWorkflowId": return java.lang.String.class;
            case "maxmessages":
            case "maxMessages": return int.class;
            case "maxseconds":
            case "maxSeconds": return int.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        DurationWorkflowPolicyFactory target = (DurationWorkflowPolicyFactory) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "action": return target.getAction();
            case "fromrouteid":
            case "fromWorkflowId": return target.getFromWorkflowId();
            case "maxmessages":
            case "maxMessages": return target.getMaxMessages();
            case "maxseconds":
            case "maxSeconds": return target.getMaxSeconds();
            default: return null;
        }
    }
}

