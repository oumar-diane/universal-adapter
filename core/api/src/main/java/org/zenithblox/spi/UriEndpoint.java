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
package org.zenithblox.spi;


import org.zenithblox.Category;

import java.lang.annotation.*;

/**
 * Represents an annotated Zwangine <a href="http://zwangine.zentihblox.org/endpoint.html">Endpoint</a> which can have its
 * properties (and the properties on its consumer) injected from the Zwangine URI path and its query parameters
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ ElementType.TYPE })
public @interface UriEndpoint {

    /**
     * The first version this endpoint was added to  Zwangine.
     */
    String firstVersion() default "";

    /**
     * Represents the URI scheme name of this endpoint.
     *
     * Multiple scheme names can be defined as a comma separated value. For example to associate <var>http</var> and
     * <var>https</var> to the same endpoint implementation.
     *
     * The order of the scheme names here should be the same order as in {@link #extendsScheme()} so their are paired.
     *
     * The schema name must be lowercase, it may contain dashes as well. For example: robot-framework.
     */
    String scheme();

    /**
     * Used when an endpoint is extending another endpoint
     *
     * Multiple scheme names can be defined as a comma separated value. For example to associate <var>ftp</var> and
     * <var>ftps</var> to the same endpoint implementation. The order of the scheme names here should be the same order
     * as in {@link #scheme()} so their are paired.
     */
    String extendsScheme() default "";

    /**
     * Represent the URI syntax the endpoint must use.
     *
     * The syntax follows the patterns such as:
     * <ul>
     * <li>scheme:host:port</li>
     * <li>scheme:host:port/path</li>
     * <li>scheme:path</li>
     * <li>scheme:path/path2</li>
     * </ul>
     * Where each path maps to the name of the endpoint {@link org.zenithblox.spi.UriPath} option. The query
     * parameters is implied and should not be included in the syntax.
     *
     * Some examples:
     * <ul>
     * <li>file:directoryName</li>
     * <li>ftp:host:port/directoryName</li>
     * <li>jms:destinationType:destinationName</li>
     * </ul>
     */
    String syntax();

    /**
     * If the endpoint supports specifying username and/or password in the UserInfo part of the URI, then the
     * alternative syntax can represent this such as:
     * <ul>
     * <li>ftp:userName:password@host:port/directoryName</li>
     * <li>ssh:username:password@host:port</li>
     * </ul>
     */
    String alternativeSyntax() default "";

    /**
     * The configuration parameter name prefix used on parameter names to separate the endpoint properties from the
     * consumer properties
     */
    String consumerPrefix() default "";

    /**
     * A human-readable title of this entity, such as the component name of the this endpoint.
     *
     * For example: JMS, MQTT, Netty HTTP, SAP NetWeaver
     */
    String title();

    /**
     * To associate this endpoint with category(ies).
     * <p>
     * This category is intended for grouping the endpoints, such as <code>Category.CORE</code>,
     * <code>Category.FILE</code>, <code>Category.DATABASE</code>, etc, but supplied with as array of {@link Category}
     * enums.
     * </p>
     * For example: @UriEndpoint(category = {Category.CORE, Category.DATABASE})
     */
    Category[] category() default {};

    /**
     * Whether this endpoint can only be used as a producer.
     *
     * By default, its assumed the endpoint can be used as both consumer and producer.
     */
    boolean producerOnly() default false;

    /**
     * Whether this endpoint can only be used as a consumer.
     *
     * By default, its assumed the endpoint can be used as both consumer and producer.
     */
    boolean consumerOnly() default false;

    /**
     * Should all properties be known or does the endpoint allow unknown options?
     *
     * <code>lenient = false</code> means that the endpoint should validate that all given options is known and
     * configured properly. <code>lenient = true</code> means that the endpoint allows additional unknown options to be
     * passed to it but does not throw a ResolveEndpointFailedException when creating the endpoint.
     *
     * This options is used by a few components for instance the HTTP based that can have dynamic URI options appended
     * that is targeted for an external system.
     *
     * Most endpoints is configured to be <b>not</b> lenient.
     */
    boolean lenientProperties() default false;

    /**
     * Generates source code for fast configuring of the endpoint properties which uses direct method invocation of
     * getter/setters. Setting this to false will fallback to use reflection based introspection as Zwangine does in Zwangine
     * 2.x.
     */
    boolean generateConfigurer() default true;

    /**
     * The name of the properties that is used in the endpoint URI to select which API name (method) to use.
     *
     * This is only applicable for API based components where configurations are separated by API names (grouping).
     */
    String apiSyntax() default "";

    /**
     * The class that contains all the name of headers that are supported by the consumer and/or producer. The name of
     * the headers are defined as {@code String} constants in the headers class.
     *
     * The class to provide can be any class but by convention, we would expect a class whose name is of type
     * <i>xxxConstants</i> where <i>xxx</i> is the name of the corresponding component like for example
     * <i>FtpConstants</i> for the component <i>zwangine-ftp</i>.
     *
     * The metadata of a given header are retrieved directly from the annotation {@code @Metadata} added to the
     * {@code String} constant representing its name and defined in the headers class.
     */
    Class<?> headersClass() default void.class;

    /**
     * The name of the field to get or the name of the method without parameters to invoke to get the name of the
     * headers defined in an enum.
     *
     * Only took into account if and only if the class defined as {@code headersClass} is an enum.
     *
     * For example, assuming that {@code SomeEnum} has been configured as the {@code headersClass} of a given component,
     * since the name of the header is actually the value of the field {@code headerName}, the element
     * {@code headersNameProvider} should be set to {@code "headerName"} to get the expected header names.
     *
     * <pre>
     * <code>
     *
     * public enum SomeEnum {
     *    {@literal @}Metadata
     *     FOO("fooKey");
     *
     *     public final String headerName;
     *
     *     SomeEnum(final String str) {
     *         this.headerName = str;
     *     }
     * }
     * </code>
     * </pre>
     */
    String headersNameProvider() default "";

    /**
     * Whether the component does remote communication such as connecting to an external system over the network. Set
     * this to false for internal components such as log, message transformations and other kinds.
     */
    boolean remote() default true;
}
