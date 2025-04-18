/*
 * Licensed to the Zenithblox Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Zenithblox License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.timer;

import org.zenithblox.*;
import org.zenithblox.spi.*;
import org.zenithblox.support.DefaultEndpoint;

import java.util.Date;
import java.util.Timer;

/**
 * Generate messages in specified intervals using <code>java.util.Timer</code>.
 *
 * This component is similar to the scheduler component, but has much less functionality.
 */
@UriEndpoint(firstVersion = "1.0.0", scheme = "timer", title = "Timer", syntax = "timer:timerName", consumerOnly = true,
             remote = false, category = { Category.CORE, Category.SCHEDULING }, headersClass = TimerConstants.class)
public class TimerEndpoint extends DefaultEndpoint implements MultipleConsumersSupport {
    @UriPath
    @Metadata(required = true)
    private String timerName;
    @UriParam(defaultValue = "1000", javaType = "java.time.Duration")
    private long period = 1000;
    @UriParam(defaultValue = "1000", javaType = "java.time.Duration")
    private long delay = 1000;
    @UriParam
    private long repeatCount;
    @UriParam
    private boolean fixedRate;
    @UriParam
    private boolean includeMetadata;
    @UriParam(defaultValue = "TRACE", label = "consumer,scheduler")
    private LoggingLevel runLoggingLevel = LoggingLevel.TRACE;
    @UriParam(label = "advanced", defaultValue = "true")
    private boolean daemon = true;
    @UriParam(label = "advanced")
    private Date time;
    @UriParam(label = "advanced")
    private String pattern;
    @UriParam(label = "advanced")
    private Timer timer;
    @UriParam(label = "advanced")
    private boolean synchronous;

    public TimerEndpoint() {
    }

    public TimerEndpoint(String uri, Component component, String timerName) {
        super(uri, component);
        this.timerName = timerName;
    }

    protected TimerEndpoint(String endpointUri, Component component) {
        super(endpointUri, component);
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    @Override
    public TimerComponent getComponent() {
        return (TimerComponent) super.getComponent();
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new RuntimeZwangineException("Cannot produce to a TimerEndpoint: " + getEndpointUri());
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        Consumer answer = new TimerConsumer(this, processor);
        configureConsumer(answer);
        return answer;
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();
        if (timerName == null) {
            timerName = getEndpointUri();
        }
        // do nothing in regard to setTimer, the timer will be set when the first consumer requests it
    }

    @Override
    protected void doStop() throws Exception {
        setTimer(null);
        super.doStop();
    }

    @Override
    public boolean isMultipleConsumersSupported() {
        return true;
    }

    public String getTimerName() {
        return timerName;
    }

    /**
     * The name of the timer
     */
    public void setTimerName(String timerName) {
        this.timerName = timerName;
    }

    public boolean isDaemon() {
        return daemon;
    }

    /**
     * Specifies whether the thread associated with the timer endpoint runs as a daemon.
     * <p/>
     * The default value is true.
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

    public long getDelay() {
        return delay;
    }

    /**
     * The number of milliseconds to wait before the first event is generated. Should not be used in conjunction with
     * the time option.
     * <p/>
     * The default value is 1000.
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean isFixedRate() {
        return fixedRate;
    }

    /**
     * Events take place at approximately regular intervals, separated by the specified period.
     */
    public void setFixedRate(boolean fixedRate) {
        this.fixedRate = fixedRate;
    }

    public LoggingLevel getRunLoggingLevel() {
        return runLoggingLevel;
    }

    /**
     * The consumer logs a start/complete log line when it polls. This option allows you to configure the logging level
     * for that.
     */
    public void setRunLoggingLevel(LoggingLevel runLoggingLevel) {
        this.runLoggingLevel = runLoggingLevel;
    }

    public long getPeriod() {
        return period;
    }

    /**
     * Generate periodic events every period. Must be zero or positive value.
     * <p/>
     * The default value is 1000.
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    public long getRepeatCount() {
        return repeatCount;
    }

    /**
     * Specifies a maximum limit for the number of fires. Therefore, if you set it to 1, the timer will only fire once.
     * If you set it to 5, it will only fire five times. A value of zero or negative means fire forever.
     */
    public void setRepeatCount(long repeatCount) {
        this.repeatCount = repeatCount;
    }

    public Date getTime() {
        return time;
    }

    /**
     * A java.util.Date the first event should be generated. If using the URI, the pattern expected is: yyyy-MM-dd
     * HH:mm:ss or yyyy-MM-dd'T'HH:mm:ss.
     */
    public void setTime(Date time) {
        this.time = time;
    }

    public String getPattern() {
        return pattern;
    }

    /**
     * Allows you to specify a custom Date pattern to use for setting the time option using URI syntax.
     */
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Timer getTimer() {
        return timer;
    }

    /**
     * To use a custom {@link Timer}
     */
    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public Timer getTimer(TimerConsumer consumer) {
        if (timer != null) {
            // use custom timer
            return timer;
        }
        return getComponent().getTimer(consumer);
    }

    public boolean isIncludeMetadata() {
        return includeMetadata;
    }

    /**
     * Whether to include metadata in the exchange such as fired time, timer name, timer count etc.
     */
    public void setIncludeMetadata(boolean includeMetadata) {
        this.includeMetadata = includeMetadata;
    }

    public boolean isSynchronous() {
        return synchronous;
    }

    /**
     * Sets whether synchronous processing should be strictly used
     */
    public void setSynchronous(boolean synchronous) {
        this.synchronous = synchronous;
    }

    public void removeTimer(TimerConsumer consumer) {
        if (timer == null) {
            // only remove timer if we are not using a custom timer
            getComponent().removeTimer(consumer);
        }
    }

}
