/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zwangine.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox;

import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.StringHelper;

import java.util.Map;

/**
 * An <a href="http://zwangine.zwangine.org/endpoint.html">endpoint</a> implements the
 * <a href="http://zwangine.zwangine.org/message-endpoint.html">Message Endpoint</a> pattern and represents an endpoint that
 * can send and receive message exchanges
 *
 * @see Exchange
 * @see Message
 */
public interface Endpoint extends IsSingleton, Service, ComponentAware {

    /**
     * Returns the string representation of the endpoint URI
     *
     * @return the endpoint URI
     */
    String getEndpointUri();

    /**
     * Returns the default exchange pattern to use when creating an exchange.
     */
    ExchangePattern getExchangePattern();

    /**
     * Returns the string representation of the base endpoint URI (without query parameters).
     */
    default String getEndpointBaseUri() {
        String value = getEndpointUri();
        return StringHelper.before(value, "?", value);
    }

    /**
     * Returns a string key of this endpoint.
     * <p/>
     * This key is used by {@link org.zenithblox.spi.LifecycleStrategy} when registering endpoint. This allows to
     * register different instances of endpoints with the same key.
     * <p/>
     * For JMX mbeans this allows us to use the same JMX Mbean for all endpoints that are logical the same but have
     * different parameters. For instance the http endpoint.
     *
     * @return the endpoint key
     */
    String getEndpointKey();

    /**
     * Create a new exchange for communicating with this endpoint.
     * <p/>
     * <b>Important:</b> Consumers should use {@link Consumer#createExchange(boolean)} to create an exchange for which
     * the consumer received a message.
     *
     * @return a new exchange
     */
    Exchange createExchange();

    /**
     * Create a new exchange for communicating with this endpoint with the specified {@link ExchangePattern} such as
     * whether its going to be an {@link ExchangePattern#InOnly} or {@link ExchangePattern#InOut} exchange
     * <p/>
     * <b>Important:</b> Consumers should use {@link Consumer#createExchange(boolean)} to create an exchange for which
     * the consumer received a message.
     *
     * @param  pattern the message exchange pattern for the exchange
     * @return         a new exchange
     */
    Exchange createExchange(ExchangePattern pattern);

    /**
     * Configures a newly created {@link Exchange}.
     *
     * @param exchange the new exchange
     */
    void configureExchange(Exchange exchange);

    /**
     * Returns the context which created the endpoint
     *
     * @return the context which created the endpoint
     */
    ZwangineContext getZwangineContext();

    /**
     * Creates a new producer which is used send messages into the endpoint.
     *
     * Important: Do not do any initialization in the constructor of the {@link Producer}. Instead use
     * {@link ServiceSupport#doInit()} or {@link ServiceSupport#doStart()}.
     *
     * @return           a newly created producer
     * @throws Exception can be thrown
     */
    Producer createProducer() throws Exception;

    /**
     * Whether this endpoint creates singleton producers
     */
    default boolean isSingletonProducer() {
        return isSingleton();
    }

    /**
     * Creates a new producer which is used send messages into the endpoint
     *
     * Important: Do not do any initialization in the constructor of the {@link Producer}. Instead use
     * {@link ServiceSupport#doInit()} or {@link ServiceSupport#doStart()}.
     *
     * @return           a newly created producer
     * @throws Exception can be thrown
     */
    AsyncProducer createAsyncProducer() throws Exception;

    /**
     * Creates a new <a href="http://zwangine.zwangine.org/event-driven-consumer.html">Event Driven Consumer</a> which
     * consumes messages from the endpoint using the given processor
     *
     * Important: Do not do any initialization in the constructor of the {@link Consumer}. Instead use
     * {@link ServiceSupport#doInit()} or {@link ServiceSupport#doStart()}.
     *
     * @param  processor the given processor
     * @return           a newly created consumer
     * @throws Exception can be thrown
     */
    Consumer createConsumer(Processor processor) throws Exception;

    /**
     * Creates a new <a href="http://zwangine.zwangine.org/polling-consumer.html">Polling Consumer</a> so that the caller can
     * poll message exchanges from the consumer using {@link PollingConsumer#receive()},
     * {@link PollingConsumer#receiveNoWait()} or {@link PollingConsumer#receive(long)} whenever it is ready to do so
     * rather than using the <a href="http://zwangine.zwangine.org/event-driven-consumer.html">Event Based Consumer</a>
     * returned by {@link #createConsumer(Processor)}
     *
     * Important: Do not do any initialization in the constructor of the {@link PollingConsumer}. Instead use
     * {@link ServiceSupport#doInit()} or {@link ServiceSupport#doStart()}.
     *
     * @return           a newly created pull consumer
     * @throws Exception if the pull consumer could not be created
     */
    PollingConsumer createPollingConsumer() throws Exception;

    /**
     * Configure properties on this endpoint.
     *
     * @param options the options (properties)
     */
    void configureProperties(Map<String, Object> options);

    /**
     * Sets the zwangine context.
     *
     * @param context the zwangine context
     */
    void setZwangineContext(ZwangineContext context);

    /**
     * Should all properties be known or does the endpoint allow unknown options?
     * <p/>
     * <tt>lenient = false</tt> means that the endpoint should validate that all given options is known and configured
     * properly. <tt>lenient = true</tt> means that the endpoint allows additional unknown options to be passed to it
     * but does not throw a ResolveEndpointFailedException when creating the endpoint.
     * <p/>
     * This options is used by a few components for instance the HTTP based that can have dynamic URI options appended
     * that is targeted for an external system.
     * <p/>
     * Most endpoints is configured to be <b>not</b> lenient.
     *
     * @return whether properties is lenient or not
     */
    boolean isLenientProperties();

    /**
     * Whether this endpoint can connect to remote system, such as cloud providers, messaging brokers, databases. A
     * local endpoint operates locally only, such as an internal message transformer, logger, or such as direct/seda
     * components.
     */
    default boolean isRemote() {
        return true;
    }
}
