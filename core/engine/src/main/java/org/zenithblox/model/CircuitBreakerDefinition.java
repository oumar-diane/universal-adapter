/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.model;

import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * Workflow messages in a fault tolerance way using Circuit Breaker
 */
@Metadata(label = "eip,routing,error")
public class CircuitBreakerDefinition extends OutputDefinition<CircuitBreakerDefinition> {

    private String configuration;
    @Metadata(label = "advanced", javaType = "java.lang.Boolean", defaultValue = "false")
    private Boolean inheritErrorHandler;
    private Resilience4jConfigurationDefinition resilience4jConfiguration;
    private FaultToleranceConfigurationDefinition faultToleranceConfiguration;
    private OnFallbackDefinition onFallback;

    public CircuitBreakerDefinition() {
    }

    protected CircuitBreakerDefinition(CircuitBreakerDefinition source) {
        super(source);
        this.configuration = source.configuration;
        this.inheritErrorHandler = source.inheritErrorHandler;
        this.resilience4jConfiguration
                = source.resilience4jConfiguration != null ? source.resilience4jConfiguration.copyDefinition() : null;
        this.faultToleranceConfiguration
                = source.faultToleranceConfiguration != null ? source.faultToleranceConfiguration.copyDefinition() : null;
        this.onFallback = source.onFallback != null ? source.onFallback.copyDefinition() : null;
    }

    @Override
    public CircuitBreakerDefinition copyDefinition() {
        return new CircuitBreakerDefinition(this);
    }

    @Override
    public String toString() {
        return "CircuitBreaker[" + getOutputs() + "]";
    }

    @Override
    public String getShortName() {
        return "circuitBreaker";
    }

    @Override
    public String getLabel() {
        return "circuitBreaker";
    }

    @Override
    public List<ProcessorDefinition<?>> getOutputs() {
        return outputs;
    }

    @Override
    public void setOutputs(List<ProcessorDefinition<?>> outputs) {
        super.setOutputs(outputs);
    }

    @Override
    public void addOutput(ProcessorDefinition<?> output) {
        if (onFallback != null) {
            onFallback.addOutput(output);
        } else {
            super.addOutput(output);
        }
    }

    public Resilience4jConfigurationDefinition getResilience4jConfiguration() {
        return resilience4jConfiguration;
    }

    public void setResilience4jConfiguration(Resilience4jConfigurationDefinition resilience4jConfiguration) {
        this.resilience4jConfiguration = resilience4jConfiguration;
    }

    public FaultToleranceConfigurationDefinition getFaultToleranceConfiguration() {
        return faultToleranceConfiguration;
    }

    public void setFaultToleranceConfiguration(FaultToleranceConfigurationDefinition faultToleranceConfiguration) {
        this.faultToleranceConfiguration = faultToleranceConfiguration;
    }

    public String getConfiguration() {
        return configuration;
    }

    /**
     * Refers to a circuit breaker configuration (such as resillience4j, or microprofile-fault-tolerance) to use for
     * configuring the circuit breaker EIP.
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    @Override
    public Boolean getInheritErrorHandler() {
        return inheritErrorHandler;
    }

    @Override
    public void setInheritErrorHandler(Boolean inheritErrorHandler) {
        this.inheritErrorHandler = inheritErrorHandler;
    }

    public OnFallbackDefinition getOnFallback() {
        return onFallback;
    }

    public void setOnFallback(OnFallbackDefinition onFallback) {
        this.onFallback = onFallback;
    }

    // Fluent API
    // -------------------------------------------------------------------------
    /**
     * Configures the circuit breaker to use Resilience4j.
     * <p/>
     * Use <tt>end</tt> when configuration is complete, to return back to the Circuit Breaker EIP.
     */
    public Resilience4jConfigurationDefinition resilience4jConfiguration() {
        resilience4jConfiguration
                = resilience4jConfiguration == null ? new Resilience4jConfigurationDefinition(this) : resilience4jConfiguration;
        return resilience4jConfiguration;
    }

    /**
     * Configures the circuit breaker to use Resilience4j with the given configuration.
     */
    public CircuitBreakerDefinition resilience4jConfiguration(Resilience4jConfigurationDefinition configuration) {
        resilience4jConfiguration = configuration;
        return this;
    }

    /**
     * Configures the circuit breaker to use MicroProfile Fault Tolerance.
     * <p/>
     * Use <tt>end</tt> when configuration is complete, to return back to the Circuit Breaker EIP.
     */
    public FaultToleranceConfigurationDefinition faultToleranceConfiguration() {
        faultToleranceConfiguration = faultToleranceConfiguration == null
                ? new FaultToleranceConfigurationDefinition(this) : faultToleranceConfiguration;
        return faultToleranceConfiguration;
    }

    /**
     * Configures the circuit breaker to use MicroProfile Fault Tolerance with the given configuration.
     */
    public CircuitBreakerDefinition faultToleranceConfiguration(FaultToleranceConfigurationDefinition configuration) {
        faultToleranceConfiguration = configuration;
        return this;
    }

    /**
     * Refers to a configuration to use for configuring the circuit breaker.
     */
    public CircuitBreakerDefinition configuration(String ref) {
        configuration = ref;
        return this;
    }

    /**
     * To turn on or off Zwangine error handling during circuit breaker.
     *
     * If this is enabled then Zwangine error handler will first trigger if there is an error in the circuit breaker, which
     * allows to let Zwangine handle redeliveries. If all attempts is failed, then after the circuit breaker is finished,
     * then Zwangine error handler can handle the error as well such as the dead letter channel.
     *
     * By default, Zwangine error handler is turned off.
     */
    public CircuitBreakerDefinition inheritErrorHandler(boolean inheritErrorHandler) {
        this.inheritErrorHandler = inheritErrorHandler;
        return this;
    }

    /**
     * The fallback workflow path to execute that does <b>not</b> go over the network.
     * <p>
     * This should be a static or cached result that can immediately be returned upon failure. If the fallback requires
     * network connection then use {@link #onFallbackViaNetwork()}.
     */
    public CircuitBreakerDefinition onFallback() {
        onFallback = new OnFallbackDefinition();
        onFallback.setParent(this);
        return this;
    }

    /**
     * The fallback workflow path to execute that will go over the network.
     * <p/>
     * If the fallback will go over the network it is another possible point of failure.
     */
    public CircuitBreakerDefinition onFallbackViaNetwork() {
        onFallback = new OnFallbackDefinition();
        onFallback.setFallbackViaNetwork(Boolean.toString(true));
        onFallback.setParent(this);
        return this;
    }

}
