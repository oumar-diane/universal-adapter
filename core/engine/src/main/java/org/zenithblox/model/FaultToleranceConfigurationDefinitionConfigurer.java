package org.zenithblox.model;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ExtendedPropertyConfigurerGetter;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.support.component.PropertyConfigurerSupport;
import org.zenithblox.util.CaseInsensitiveMap;

import java.util.Map;

public class FaultToleranceConfigurationDefinitionConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, ExtendedPropertyConfigurerGetter {

    private static final Map<String, Object> ALL_OPTIONS;
    static {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("BulkheadEnabled", java.lang.String.class);
        map.put("BulkheadExecutorService", java.lang.String.class);
        map.put("BulkheadMaxConcurrentCalls", java.lang.String.class);
        map.put("BulkheadWaitingTaskQueue", java.lang.String.class);
        map.put("CircuitBreaker", java.lang.String.class);
        map.put("Delay", java.lang.String.class);
        map.put("FailureRatio", java.lang.String.class);
        map.put("Id", java.lang.String.class);
        map.put("RequestVolumeThreshold", java.lang.String.class);
        map.put("SuccessThreshold", java.lang.String.class);
        map.put("TimeoutDuration", java.lang.String.class);
        map.put("TimeoutEnabled", java.lang.String.class);
        map.put("TimeoutPoolSize", java.lang.String.class);
        map.put("TimeoutScheduledExecutorService", java.lang.String.class);
        ALL_OPTIONS = map;
    }

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        FaultToleranceConfigurationDefinition target = (FaultToleranceConfigurationDefinition) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "bulkheadenabled":
            case "bulkheadEnabled": target.setBulkheadEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadexecutorservice":
            case "bulkheadExecutorService": target.setBulkheadExecutorService(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": target.setBulkheadMaxConcurrentCalls(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadwaitingtaskqueue":
            case "bulkheadWaitingTaskQueue": target.setBulkheadWaitingTaskQueue(property(zwangineContext, java.lang.String.class, value)); return true;
            case "circuitbreaker":
            case "circuitBreaker": target.setCircuitBreaker(property(zwangineContext, java.lang.String.class, value)); return true;
            case "delay": target.setDelay(property(zwangineContext, java.lang.String.class, value)); return true;
            case "failureratio":
            case "failureRatio": target.setFailureRatio(property(zwangineContext, java.lang.String.class, value)); return true;
            case "id": target.setId(property(zwangineContext, java.lang.String.class, value)); return true;
            case "requestvolumethreshold":
            case "requestVolumeThreshold": target.setRequestVolumeThreshold(property(zwangineContext, java.lang.String.class, value)); return true;
            case "successthreshold":
            case "successThreshold": target.setSuccessThreshold(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutduration":
            case "timeoutDuration": target.setTimeoutDuration(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutenabled":
            case "timeoutEnabled": target.setTimeoutEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutpoolsize":
            case "timeoutPoolSize": target.setTimeoutPoolSize(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutscheduledexecutorservice":
            case "timeoutScheduledExecutorService": target.setTimeoutScheduledExecutorService(property(zwangineContext, java.lang.String.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Map<String, Object> getAllOptions(Object target) {
        return ALL_OPTIONS;
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "bulkheadenabled":
            case "bulkheadEnabled": return java.lang.String.class;
            case "bulkheadexecutorservice":
            case "bulkheadExecutorService": return java.lang.String.class;
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": return java.lang.String.class;
            case "bulkheadwaitingtaskqueue":
            case "bulkheadWaitingTaskQueue": return java.lang.String.class;
            case "circuitbreaker":
            case "circuitBreaker": return java.lang.String.class;
            case "delay": return java.lang.String.class;
            case "failureratio":
            case "failureRatio": return java.lang.String.class;
            case "id": return java.lang.String.class;
            case "requestvolumethreshold":
            case "requestVolumeThreshold": return java.lang.String.class;
            case "successthreshold":
            case "successThreshold": return java.lang.String.class;
            case "timeoutduration":
            case "timeoutDuration": return java.lang.String.class;
            case "timeoutenabled":
            case "timeoutEnabled": return java.lang.String.class;
            case "timeoutpoolsize":
            case "timeoutPoolSize": return java.lang.String.class;
            case "timeoutscheduledexecutorservice":
            case "timeoutScheduledExecutorService": return java.lang.String.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        FaultToleranceConfigurationDefinition target = (FaultToleranceConfigurationDefinition) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "bulkheadenabled":
            case "bulkheadEnabled": return target.getBulkheadEnabled();
            case "bulkheadexecutorservice":
            case "bulkheadExecutorService": return target.getBulkheadExecutorService();
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": return target.getBulkheadMaxConcurrentCalls();
            case "bulkheadwaitingtaskqueue":
            case "bulkheadWaitingTaskQueue": return target.getBulkheadWaitingTaskQueue();
            case "circuitbreaker":
            case "circuitBreaker": return target.getCircuitBreaker();
            case "delay": return target.getDelay();
            case "failureratio":
            case "failureRatio": return target.getFailureRatio();
            case "id": return target.getId();
            case "requestvolumethreshold":
            case "requestVolumeThreshold": return target.getRequestVolumeThreshold();
            case "successthreshold":
            case "successThreshold": return target.getSuccessThreshold();
            case "timeoutduration":
            case "timeoutDuration": return target.getTimeoutDuration();
            case "timeoutenabled":
            case "timeoutEnabled": return target.getTimeoutEnabled();
            case "timeoutpoolsize":
            case "timeoutPoolSize": return target.getTimeoutPoolSize();
            case "timeoutscheduledexecutorservice":
            case "timeoutScheduledExecutorService": return target.getTimeoutScheduledExecutorService();
            default: return null;
        }
    }
}


