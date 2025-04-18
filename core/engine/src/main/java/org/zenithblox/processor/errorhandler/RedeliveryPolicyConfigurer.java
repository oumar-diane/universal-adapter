package org.zenithblox.processor.errorhandler;

import org.zenithblox.LoggingLevel;
import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.GeneratedPropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerGetter;
import org.zenithblox.support.component.PropertyConfigurerSupport;

public class RedeliveryPolicyConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {

    @Override
    public boolean configure(ZwangineContext zwangineContext, Object obj, String name, Object value, boolean ignoreCase) {
        RedeliveryPolicy target = (RedeliveryPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowredeliverywhilestopping":
            case "allowRedeliveryWhileStopping": target.setAllowRedeliveryWhileStopping(property(zwangineContext, boolean.class, value)); return true;
            case "asyncdelayedredelivery":
            case "asyncDelayedRedelivery": target.setAsyncDelayedRedelivery(property(zwangineContext, boolean.class, value)); return true;
            case "backoffmultiplier":
            case "backOffMultiplier": target.setBackOffMultiplier(property(zwangineContext, double.class, value)); return true;
            case "collisionavoidancefactor":
            case "collisionAvoidanceFactor": target.setCollisionAvoidanceFactor(property(zwangineContext, double.class, value)); return true;
            case "collisionavoidancepercent":
            case "collisionAvoidancePercent": target.setCollisionAvoidancePercent(property(zwangineContext, double.class, value)); return true;
            case "delaypattern":
            case "delayPattern": target.setDelayPattern(property(zwangineContext, java.lang.String.class, value)); return true;
            case "exchangeformatterref":
            case "exchangeFormatterRef": target.setExchangeFormatterRef(property(zwangineContext, java.lang.String.class, value)); return true;
            case "logcontinued":
            case "logContinued": target.setLogContinued(property(zwangineContext, boolean.class, value)); return true;
            case "logexhausted":
            case "logExhausted": target.setLogExhausted(property(zwangineContext, boolean.class, value)); return true;
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": target.setLogExhaustedMessageBody(property(zwangineContext, java.lang.Boolean.class, value)); return true;
            case "logexhaustedmessagehistory":
            case "logExhaustedMessageHistory": target.setLogExhaustedMessageHistory(property(zwangineContext, boolean.class, value)); return true;
            case "loghandled":
            case "logHandled": target.setLogHandled(property(zwangineContext, boolean.class, value)); return true;
            case "lognewexception":
            case "logNewException": target.setLogNewException(property(zwangineContext, boolean.class, value)); return true;
            case "logretryattempted":
            case "logRetryAttempted": target.setLogRetryAttempted(property(zwangineContext, boolean.class, value)); return true;
            case "logretrystacktrace":
            case "logRetryStackTrace": target.setLogRetryStackTrace(property(zwangineContext, boolean.class, value)); return true;
            case "logstacktrace":
            case "logStackTrace": target.setLogStackTrace(property(zwangineContext, boolean.class, value)); return true;
            case "maximumredeliveries":
            case "maximumRedeliveries": target.setMaximumRedeliveries(property(zwangineContext, int.class, value)); return true;
            case "maximumredeliverydelay":
            case "maximumRedeliveryDelay": target.setMaximumRedeliveryDelay(property(zwangineContext, long.class, value)); return true;
            case "redeliverydelay":
            case "redeliveryDelay": target.setRedeliveryDelay(property(zwangineContext, long.class, value)); return true;
            case "retriesexhaustedloglevel":
            case "retriesExhaustedLogLevel": target.setRetriesExhaustedLogLevel(property(zwangineContext, LoggingLevel.class, value)); return true;
            case "retryattemptedloginterval":
            case "retryAttemptedLogInterval": target.setRetryAttemptedLogInterval(property(zwangineContext, int.class, value)); return true;
            case "retryattemptedloglevel":
            case "retryAttemptedLogLevel": target.setRetryAttemptedLogLevel(property(zwangineContext, LoggingLevel.class, value)); return true;
            case "usecollisionavoidance":
            case "useCollisionAvoidance": target.setUseCollisionAvoidance(property(zwangineContext, boolean.class, value)); return true;
            case "useexponentialbackoff":
            case "useExponentialBackOff": target.setUseExponentialBackOff(property(zwangineContext, boolean.class, value)); return true;
            default: return false;
        }
    }

    @Override
    public Class<?> getOptionType(String name, boolean ignoreCase) {
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowredeliverywhilestopping":
            case "allowRedeliveryWhileStopping": return boolean.class;
            case "asyncdelayedredelivery":
            case "asyncDelayedRedelivery": return boolean.class;
            case "backoffmultiplier":
            case "backOffMultiplier": return double.class;
            case "collisionavoidancefactor":
            case "collisionAvoidanceFactor": return double.class;
            case "collisionavoidancepercent":
            case "collisionAvoidancePercent": return double.class;
            case "delaypattern":
            case "delayPattern": return java.lang.String.class;
            case "exchangeformatterref":
            case "exchangeFormatterRef": return java.lang.String.class;
            case "logcontinued":
            case "logContinued": return boolean.class;
            case "logexhausted":
            case "logExhausted": return boolean.class;
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": return java.lang.Boolean.class;
            case "logexhaustedmessagehistory":
            case "logExhaustedMessageHistory": return boolean.class;
            case "loghandled":
            case "logHandled": return boolean.class;
            case "lognewexception":
            case "logNewException": return boolean.class;
            case "logretryattempted":
            case "logRetryAttempted": return boolean.class;
            case "logretrystacktrace":
            case "logRetryStackTrace": return boolean.class;
            case "logstacktrace":
            case "logStackTrace": return boolean.class;
            case "maximumredeliveries":
            case "maximumRedeliveries": return int.class;
            case "maximumredeliverydelay":
            case "maximumRedeliveryDelay": return long.class;
            case "redeliverydelay":
            case "redeliveryDelay": return long.class;
            case "retriesexhaustedloglevel":
            case "retriesExhaustedLogLevel": return LoggingLevel.class;
            case "retryattemptedloginterval":
            case "retryAttemptedLogInterval": return int.class;
            case "retryattemptedloglevel":
            case "retryAttemptedLogLevel": return LoggingLevel.class;
            case "usecollisionavoidance":
            case "useCollisionAvoidance": return boolean.class;
            case "useexponentialbackoff":
            case "useExponentialBackOff": return boolean.class;
            default: return null;
        }
    }

    @Override
    public Object getOptionValue(Object obj, String name, boolean ignoreCase) {
        RedeliveryPolicy target = (RedeliveryPolicy) obj;
        switch (ignoreCase ? name.toLowerCase() : name) {
            case "allowredeliverywhilestopping":
            case "allowRedeliveryWhileStopping": return target.isAllowRedeliveryWhileStopping();
            case "asyncdelayedredelivery":
            case "asyncDelayedRedelivery": return target.isAsyncDelayedRedelivery();
            case "backoffmultiplier":
            case "backOffMultiplier": return target.getBackOffMultiplier();
            case "collisionavoidancefactor":
            case "collisionAvoidanceFactor": return target.getCollisionAvoidanceFactor();
            case "collisionavoidancepercent":
            case "collisionAvoidancePercent": return target.getCollisionAvoidancePercent();
            case "delaypattern":
            case "delayPattern": return target.getDelayPattern();
            case "exchangeformatterref":
            case "exchangeFormatterRef": return target.getExchangeFormatterRef();
            case "logcontinued":
            case "logContinued": return target.isLogContinued();
            case "logexhausted":
            case "logExhausted": return target.isLogExhausted();
            case "logexhaustedmessagebody":
            case "logExhaustedMessageBody": return target.isLogExhaustedMessageBody();
            case "logexhaustedmessagehistory":
            case "logExhaustedMessageHistory": return target.isLogExhaustedMessageHistory();
            case "loghandled":
            case "logHandled": return target.isLogHandled();
            case "lognewexception":
            case "logNewException": return target.isLogNewException();
            case "logretryattempted":
            case "logRetryAttempted": return target.isLogRetryAttempted();
            case "logretrystacktrace":
            case "logRetryStackTrace": return target.isLogRetryStackTrace();
            case "logstacktrace":
            case "logStackTrace": return target.isLogStackTrace();
            case "maximumredeliveries":
            case "maximumRedeliveries": return target.getMaximumRedeliveries();
            case "maximumredeliverydelay":
            case "maximumRedeliveryDelay": return target.getMaximumRedeliveryDelay();
            case "redeliverydelay":
            case "redeliveryDelay": return target.getRedeliveryDelay();
            case "retriesexhaustedloglevel":
            case "retriesExhaustedLogLevel": return target.getRetriesExhaustedLogLevel();
            case "retryattemptedloginterval":
            case "retryAttemptedLogInterval": return target.getRetryAttemptedLogInterval();
            case "retryattemptedloglevel":
            case "retryAttemptedLogLevel": return target.getRetryAttemptedLogLevel();
            case "usecollisionavoidance":
            case "useCollisionAvoidance": return target.isUseCollisionAvoidance();
            case "useexponentialbackoff":
            case "useExponentialBackOff": return target.isUseExponentialBackOff();
            default: return null;
        }
    }
}
