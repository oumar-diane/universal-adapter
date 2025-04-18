package org.zenithblox.model;

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ExtendedPropertyConfigurerGetter;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.support.component.PropertyConfigurerSupport;
import org.zenithblox.util.CaseInsensitiveMap;

import java.util.Map;

public class Resilience4jConfigurationDefinitionConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, ExtendedPropertyConfigurerGetter {

    private static final Map<String, Object> ALL_OPTIONS;
    static {
        Map<String, Object> map = new CaseInsensitiveMap();
        map.put("AutomaticTransitionFromOpenToHalfOpenEnabled", java.lang.String.class);
        map.put("BulkheadEnabled", java.lang.String.class);
        map.put("BulkheadMaxConcurrentCalls", java.lang.String.class);
        map.put("BulkheadMaxWaitDuration", java.lang.String.class);
        map.put("CircuitBreaker", java.lang.String.class);
        map.put("Config", java.lang.String.class);
        map.put("FailureRateThreshold", java.lang.String.class);
        map.put("Id", java.lang.String.class);
        map.put("IgnoreExceptions", java.util.List.class);
        map.put("MinimumNumberOfCalls", java.lang.String.class);
        map.put("PermittedNumberOfCallsInHalfOpenState", java.lang.String.class);
        map.put("RecordExceptions", java.util.List.class);
        map.put("SlidingWindowSize", java.lang.String.class);
        map.put("SlidingWindowType", java.lang.String.class);
        map.put("SlowCallDurationThreshold", java.lang.String.class);
        map.put("SlowCallRateThreshold", java.lang.String.class);
        map.put("ThrowExceptionWhenHalfOpenOrOpenState", java.lang.String.class);
        map.put("TimeoutCancelRunningFuture", java.lang.String.class);
        map.put("TimeoutDuration", java.lang.String.class);
        map.put("TimeoutEnabled", java.lang.String.class);
        map.put("TimeoutExecutorService", java.lang.String.class);
        map.put("WaitDurationInOpenState", java.lang.String.class);
        map.put("WritableStackTraceEnabled", java.lang.String.class);
        ALL_OPTIONS = map;
    }

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
       Resilience4jConfigurationDefinition target = (Resilience4jConfigurationDefinition) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "automatictransitionfromopentohalfopenenabled":
            case "automaticTransitionFromOpenToHalfOpenEnabled": target.setAutomaticTransitionFromOpenToHalfOpenEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadenabled":
            case "bulkheadEnabled": target.setBulkheadEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": target.setBulkheadMaxConcurrentCalls(property(zwangineContext, java.lang.String.class, value)); return true;
            case "bulkheadmaxwaitduration":
            case "bulkheadMaxWaitDuration": target.setBulkheadMaxWaitDuration(property(zwangineContext, java.lang.String.class, value)); return true;
            case "circuitbreaker":
            case "circuitBreaker": target.setCircuitBreaker(property(zwangineContext, java.lang.String.class, value)); return true;
            case "config": target.setConfig(property(zwangineContext, java.lang.String.class, value)); return true;
            case "failureratethreshold":
            case "failureRateThreshold": target.setFailureRateThreshold(property(zwangineContext, java.lang.String.class, value)); return true;
            case "id": target.setId(property(zwangineContext, java.lang.String.class, value)); return true;
            case "ignoreexceptions":
            case "ignoreExceptions": target.setIgnoreExceptions(property(zwangineContext, java.util.List.class, value)); return true;
            case "minimumnumberofcalls":
            case "minimumNumberOfCalls": target.setMinimumNumberOfCalls(property(zwangineContext, java.lang.String.class, value)); return true;
            case "permittednumberofcallsinhalfopenstate":
            case "permittedNumberOfCallsInHalfOpenState": target.setPermittedNumberOfCallsInHalfOpenState(property(zwangineContext, java.lang.String.class, value)); return true;
            case "recordexceptions":
            case "recordExceptions": target.setRecordExceptions(property(zwangineContext, java.util.List.class, value)); return true;
            case "slidingwindowsize":
            case "slidingWindowSize": target.setSlidingWindowSize(property(zwangineContext, java.lang.String.class, value)); return true;
            case "slidingwindowtype":
            case "slidingWindowType": target.setSlidingWindowType(property(zwangineContext, java.lang.String.class, value)); return true;
            case "slowcalldurationthreshold":
            case "slowCallDurationThreshold": target.setSlowCallDurationThreshold(property(zwangineContext, java.lang.String.class, value)); return true;
            case "slowcallratethreshold":
            case "slowCallRateThreshold": target.setSlowCallRateThreshold(property(zwangineContext, java.lang.String.class, value)); return true;
            case "throwexceptionwhenhalfopenoropenstate":
            case "throwExceptionWhenHalfOpenOrOpenState": target.setThrowExceptionWhenHalfOpenOrOpenState(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutcancelrunningfuture":
            case "timeoutCancelRunningFuture": target.setTimeoutCancelRunningFuture(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutduration":
            case "timeoutDuration": target.setTimeoutDuration(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutenabled":
            case "timeoutEnabled": target.setTimeoutEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
            case "timeoutexecutorservice":
            case "timeoutExecutorService": target.setTimeoutExecutorService(property(zwangineContext, java.lang.String.class, value)); return true;
            case "waitdurationinopenstate":
            case "waitDurationInOpenState": target.setWaitDurationInOpenState(property(zwangineContext, java.lang.String.class, value)); return true;
            case "writablestacktraceenabled":
            case "writableStackTraceEnabled": target.setWritableStackTraceEnabled(property(zwangineContext, java.lang.String.class, value)); return true;
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
            case "automatictransitionfromopentohalfopenenabled":
            case "automaticTransitionFromOpenToHalfOpenEnabled": return java.lang.String.class;
            case "bulkheadenabled":
            case "bulkheadEnabled": return java.lang.String.class;
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": return java.lang.String.class;
            case "bulkheadmaxwaitduration":
            case "bulkheadMaxWaitDuration": return java.lang.String.class;
            case "circuitbreaker":
            case "circuitBreaker": return java.lang.String.class;
            case "config": return java.lang.String.class;
            case "failureratethreshold":
            case "failureRateThreshold": return java.lang.String.class;
            case "id": return java.lang.String.class;
            case "ignoreexceptions":
            case "ignoreExceptions": return java.util.List.class;
            case "minimumnumberofcalls":
            case "minimumNumberOfCalls": return java.lang.String.class;
            case "permittednumberofcallsinhalfopenstate":
            case "permittedNumberOfCallsInHalfOpenState": return java.lang.String.class;
            case "recordexceptions":
            case "recordExceptions": return java.util.List.class;
            case "slidingwindowsize":
            case "slidingWindowSize": return java.lang.String.class;
            case "slidingwindowtype":
            case "slidingWindowType": return java.lang.String.class;
            case "slowcalldurationthreshold":
            case "slowCallDurationThreshold": return java.lang.String.class;
            case "slowcallratethreshold":
            case "slowCallRateThreshold": return java.lang.String.class;
            case "throwexceptionwhenhalfopenoropenstate":
            case "throwExceptionWhenHalfOpenOrOpenState": return java.lang.String.class;
            case "timeoutcancelrunningfuture":
            case "timeoutCancelRunningFuture": return java.lang.String.class;
            case "timeoutduration":
            case "timeoutDuration": return java.lang.String.class;
            case "timeoutenabled":
            case "timeoutEnabled": return java.lang.String.class;
            case "timeoutexecutorservice":
            case "timeoutExecutorService": return java.lang.String.class;
            case "waitdurationinopenstate":
            case "waitDurationInOpenState": return java.lang.String.class;
            case "writablestacktraceenabled":
            case "writableStackTraceEnabled": return java.lang.String.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
       Resilience4jConfigurationDefinition target = (Resilience4jConfigurationDefinition) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "automatictransitionfromopentohalfopenenabled":
            case "automaticTransitionFromOpenToHalfOpenEnabled": return target.getAutomaticTransitionFromOpenToHalfOpenEnabled();
            case "bulkheadenabled":
            case "bulkheadEnabled": return target.getBulkheadEnabled();
            case "bulkheadmaxconcurrentcalls":
            case "bulkheadMaxConcurrentCalls": return target.getBulkheadMaxConcurrentCalls();
            case "bulkheadmaxwaitduration":
            case "bulkheadMaxWaitDuration": return target.getBulkheadMaxWaitDuration();
            case "circuitbreaker":
            case "circuitBreaker": return target.getCircuitBreaker();
            case "config": return target.getConfig();
            case "failureratethreshold":
            case "failureRateThreshold": return target.getFailureRateThreshold();
            case "id": return target.getId();
            case "ignoreexceptions":
            case "ignoreExceptions": return target.getIgnoreExceptions();
            case "minimumnumberofcalls":
            case "minimumNumberOfCalls": return target.getMinimumNumberOfCalls();
            case "permittednumberofcallsinhalfopenstate":
            case "permittedNumberOfCallsInHalfOpenState": return target.getPermittedNumberOfCallsInHalfOpenState();
            case "recordexceptions":
            case "recordExceptions": return target.getRecordExceptions();
            case "slidingwindowsize":
            case "slidingWindowSize": return target.getSlidingWindowSize();
            case "slidingwindowtype":
            case "slidingWindowType": return target.getSlidingWindowType();
            case "slowcalldurationthreshold":
            case "slowCallDurationThreshold": return target.getSlowCallDurationThreshold();
            case "slowcallratethreshold":
            case "slowCallRateThreshold": return target.getSlowCallRateThreshold();
            case "throwexceptionwhenhalfopenoropenstate":
            case "throwExceptionWhenHalfOpenOrOpenState": return target.getThrowExceptionWhenHalfOpenOrOpenState();
            case "timeoutcancelrunningfuture":
            case "timeoutCancelRunningFuture": return target.getTimeoutCancelRunningFuture();
            case "timeoutduration":
            case "timeoutDuration": return target.getTimeoutDuration();
            case "timeoutenabled":
            case "timeoutEnabled": return target.getTimeoutEnabled();
            case "timeoutexecutorservice":
            case "timeoutExecutorService": return target.getTimeoutExecutorService();
            case "waitdurationinopenstate":
            case "waitDurationInOpenState": return target.getWaitDurationInOpenState();
            case "writablestacktraceenabled":
            case "writableStackTraceEnabled": return target.getWritableStackTraceEnabled();
            default: return null;
        }
    }

    @Override
    public Object getCollectionValueType(Object target, String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "ignoreexceptions":
            case "ignoreExceptions": return java.lang.String.class;
            case "recordexceptions":
            case "recordExceptions": return java.lang.String.class;
            default: return null;
        }
    }
}


