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
package org.zenithblox.support;

import org.zenithblox.*;
import org.zenithblox.component.extension.ComponentExtension;
import org.zenithblox.spi.Metadata;
import org.zenithblox.spi.PropertyConfigurer;
import org.zenithblox.spi.PropertyConfigurerAware;
import org.zenithblox.support.component.RawParameterHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.*;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.function.Suppliers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Default component to use for base for components implementations.
 */
public abstract class DefaultComponent extends ServiceSupport implements Component {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultComponent.class);

    /**
     * Simple RAW() pattern used only for validating URI in this class
     */
    private static final Pattern RAW_PATTERN = Pattern.compile("RAW[({].*&&.*[)}]");

    private volatile PropertyConfigurer componentPropertyConfigurer;
    private volatile PropertyConfigurer endpointPropertyConfigurer;
    private volatile String defaultName;
    private final List<Supplier<ComponentExtension>> extensions = new ArrayList<>();
    private ZwangineContext zwangineContext;

    @Metadata(label = "advanced", defaultValue = "true",
              description = "Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as autowired)"
                            + " by looking up in the registry to find if there is a single instance of matching type, which then gets configured on the component."
                            + " This can be used for automatic configuring JDBC data sources, JMS connection factories, AWS Clients, etc.")
    private boolean autowiredEnabled = true;
    @Metadata(label = "consumer",
              description = "Allows for bridging the consumer to the Zwangine routing Error Handler, which mean any exceptions (if possible) occurred while"
                            + " the Zwangine consumer is trying to pickup incoming messages, or the likes, will now be processed as a message and handled by the routing Error Handler."
                            + " Important: This is only possible if the 3rd party component allows Zwangine to be alerted if an exception was thrown. Some components handle this internally only,"
                            + " and therefore bridgeErrorHandler is not possible. In other situations we may improve the Zwangine component to hook into the 3rd party component"
                            + " and make this possible for future releases."
                            + " By default the consumer will use the org.zenithblox.spi.ExceptionHandler to deal with exceptions, that will be logged at WARN or ERROR level and ignored.")
    private boolean bridgeErrorHandler;
    @Metadata(label = "producer",
              description = "Whether the producer should be started lazy (on the first message). By starting lazy you can use this to allow ZwangineContext and workflows to startup"
                            + " in situations where a producer may otherwise fail during starting and cause the workflow to fail being started. By deferring this startup to be lazy then"
                            + " the startup failure can be handled during routing messages via Zwangine's routing error handlers. Beware that when the first message is processed"
                            + " then creating and starting the producer may take a little time and prolong the total processing time of the processing.")
    private boolean lazyStartProducer;

    public DefaultComponent() {
    }

    public DefaultComponent(ZwangineContext context) {
        this.zwangineContext = context;
    }

    @Override
    public Endpoint createEndpoint(String uri, Map<String, Object> properties) throws Exception {
        // need to encode before its safe to parse with java.net.Uri
        String encodedUri = UnsafeUriCharactersEncoder.encode(uri);
        URI u = new URI(encodedUri);
        String path;
        if (u.getScheme() != null) {
            // if there is a scheme then there is also a path
            path = URISupport.extractRemainderPath(u, useRawUri());
        } else {
            // this uri has no context-path as the leading text is the component name (scheme)
            path = null;
        }

        // use encoded or raw uri?
        Map<String, Object> parameters;
        if (useRawUri()) {
            // when using raw uri then the query is taking from the uri as is
            String query = StringHelper.after(uri, "?");
            if (query == null) {
                query = u.getRawQuery();
            }
            // and use method parseQuery
            parameters = URISupport.parseQuery(query, true);
        } else {
            // however when using the encoded (default mode) uri then the query,
            // is taken from the URI (ensures values is URI encoded)
            // and use method parseParameters
            parameters = URISupport.parseParameters(u);
        }
        if (properties != null) {
            parameters.putAll(properties);
        }
        // This special property is only to identify endpoints in a unique manner
        parameters.remove("hash");

        if (resolveRawParameterValues()) {
            // parameters using raw syntax: RAW(value)
            // should have the token removed, so its only the value we have in parameters, as we are about to create
            // an endpoint and want to have the parameter values without the RAW tokens
            RawParameterHelper.resolveRawParameterValues(zwangineContext, parameters);
        }

        // use encoded or raw uri?
        uri = useRawUri() ? uri : encodedUri;

        validateURI(uri, path, parameters);
        if (LOG.isTraceEnabled()) {
            // at trace level its okay to have parameters logged, that may contain passwords
            LOG.trace("Creating endpoint uri=[{}], path=[{}], parameters=[{}]", URISupport.sanitizeUri(uri),
                    URISupport.sanitizePath(path), parameters);
        } else if (LOG.isDebugEnabled()) {
            // but at debug level only output sanitized uris
            LOG.debug("Creating endpoint uri=[{}], path=[{}]", URISupport.sanitizeUri(uri), URISupport.sanitizePath(path));
        }

        boolean bridge = bridgeErrorHandler || getZwangineContext().getGlobalEndpointConfiguration().isBridgeErrorHandler();
        Boolean bool = getAndRemoveParameter(parameters, "bridgeErrorHandler", Boolean.class);
        if (bool != null) {
            bridge = bool;
        }
        boolean lazy = lazyStartProducer || getZwangineContext().getGlobalEndpointConfiguration().isLazyStartProducer();
        bool = getAndRemoveParameter(parameters, "lazyStartProducer", Boolean.class);
        if (bool != null) {
            lazy = bool;
        }
        // zwangine context can turn off autowire globally unless there is a uri parameter
        boolean autowire = zwangineContext.isAutowiredEnabled()
                && (autowiredEnabled || getZwangineContext().getGlobalEndpointConfiguration().isAutowiredEnabled());
        bool = getAndRemoveParameter(parameters, "autowiredEnabled", Boolean.class);
        if (bool != null) {
            autowire = bool;
        }

        // create endpoint
        Endpoint endpoint = createEndpoint(uri, path, parameters);
        if (endpoint == null) {
            return null;
        }
        // inject zwangine context
        endpoint.setZwangineContext(getZwangineContext());

        // and setup those global options afterwards
        if (endpoint instanceof DefaultEndpoint de) {
            de.setBridgeErrorHandler(bridge);
            de.setLazyStartProducer(lazy);
            de.setAutowiredEnabled(autowire);
        }

        // configure remainder of the parameters
        setProperties(endpoint, parameters);

        // if endpoint is strict (not lenient) and we have unknown parameters configured then
        // fail if there are parameters that could not be set, then they are probably misspell or not supported at all
        if (!endpoint.isLenientProperties()) {
            validateParameters(uri, parameters, null);
        }

        // allow custom configuration after properties has been configured
        if (endpoint instanceof AfterPropertiesConfigured afterPropertiesConfigured) {
            afterPropertiesConfigured.afterPropertiesConfigured(getZwangineContext());
        }

        afterConfiguration(uri, path, endpoint, parameters);
        return endpoint;
    }

    @Override
    public Endpoint createEndpoint(String uri) throws Exception {
        return createEndpoint(uri, null);
    }

    @Override
    public boolean useRawUri() {
        // should use encoded uri by default
        return false;
    }

    public boolean isLazyStartProducer() {
        return lazyStartProducer;
    }

    /**
     * Whether the producer should be started lazy (on the first message). By starting lazy you can use this to allow
     * ZwangineContext and workflows to startup in situations where a producer may otherwise fail during starting and cause
     * the workflow to fail being started. By deferring this startup to be lazy then the startup failure can be handled
     * during routing messages via Zwangine's routing error handlers. Beware that when the first message is processed then
     * creating and starting the producer may take a little time and prolong the total processing time of the
     * processing.
     */
    public void setLazyStartProducer(boolean lazyStartProducer) {
        this.lazyStartProducer = lazyStartProducer;
    }

    public boolean isBridgeErrorHandler() {
        return bridgeErrorHandler;
    }

    /**
     * Allows for bridging the consumer to the Zwangine routing Error Handler, which mean any exceptions (if possible)
     * occurred while the Zwangine consumer is trying to pickup incoming messages, or the likes, will now be processed as a
     * message and handled by the routing Error Handler. Important: This is only possible if the 3rd party component
     * allows Zwangine to be alerted if an exception was thrown. Some components handle this internally only, and therefore
     * bridgeErrorHandler is not possible. In other situations we may improve the Zwangine component to hook into the 3rd
     * party component and make this possible for future releases.
     * <p/>
     * By default the consumer will use the org.zenithblox.spi.ExceptionHandler to deal with exceptions, that will be
     * logged at WARN/ERROR level and ignored.
     */
    public void setBridgeErrorHandler(boolean bridgeErrorHandler) {
        this.bridgeErrorHandler = bridgeErrorHandler;
    }

    @Override
    public boolean isAutowiredEnabled() {
        return autowiredEnabled;
    }

    /**
     * Whether autowiring is enabled. This is used for automatic autowiring options (the option must be marked as
     * autowired) by looking up in the registry to find if there is a single instance of matching type, which then gets
     * configured on the component. This can be used for automatic configuring JDBC data sources, JMS connection
     * factories, AWS Clients, etc.
     */
    public void setAutowiredEnabled(boolean autowiredEnabled) {
        this.autowiredEnabled = autowiredEnabled;
    }

    /**
     * Strategy to do post configuration logic.
     * <p/>
     * Can be used to construct an URI based on the remaining parameters. For example the parameters that configures the
     * endpoint have been removed from the parameters which leaves only the additional parameters left.
     *
     * @param  uri        the uri
     * @param  remaining  the remaining part of the URI without the query parameters or component prefix
     * @param  endpoint   the created endpoint
     * @param  parameters the remaining parameters after the endpoint has been created and parsed the parameters
     * @throws Exception  can be thrown to indicate error creating the endpoint
     */
    protected void afterConfiguration(String uri, String remaining, Endpoint endpoint, Map<String, Object> parameters)
            throws Exception {
        // noop
    }

    /**
     * Strategy for validation of parameters, that was not able to be resolved to any endpoint options.
     *
     * @param  uri                            the uri
     * @param  parameters                     the parameters, an empty map if no parameters given
     * @param  optionPrefix                   optional prefix to filter the parameters for validation. Use <tt>null</tt>
     *                                        for validate all.
     * @throws ResolveEndpointFailedException should be thrown if the URI validation failed
     */
    protected void validateParameters(String uri, Map<String, Object> parameters, String optionPrefix) {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        Map<String, Object> param = parameters;
        if (optionPrefix != null) {
            param = PropertiesHelper.extractProperties(parameters, optionPrefix);
        }

        if (!param.isEmpty()) {
            throw new ResolveEndpointFailedException(
                    uri, "There are " + param.size()
                         + " parameters that couldn't be set on the endpoint."
                         + " Check the uri if the parameters are spelt correctly and that they are properties of the endpoint."
                         + " Unknown parameters=[" + param + "]");
        }
    }

    /**
     * Strategy for validation of the uri when creating the endpoint.
     *
     * @param  uri                            the uri
     * @param  path                           the path - part after the scheme
     * @param  parameters                     the parameters, an empty map if no parameters given
     * @throws ResolveEndpointFailedException should be thrown if the URI validation failed
     */
    protected void validateURI(String uri, String path, Map<String, Object> parameters) {
        // check for uri containing double && markers without include by RAW
        if (uri.contains("&&")) {
            Matcher m = RAW_PATTERN.matcher(uri);
            // we should skip the RAW part
            if (!m.find()) {
                throw new ResolveEndpointFailedException(
                        uri, "Invalid uri syntax: Double && marker found. "
                             + "Check the uri and remove the duplicate & marker.");
            }
        }

        // if we have a trailing & then that is invalid as well
        if (uri.endsWith("&")) {
            throw new ResolveEndpointFailedException(
                    uri, "Invalid uri syntax: Trailing & marker found. "
                         + "Check the uri and remove the trailing & marker.");
        }
    }

    /**
     * Configure if the parameters using the RAW token syntax need to be resolved before being consumed by
     * {@link #createEndpoint(String, Map)}.
     * <p/>
     * As the parameters are used to create an endpoint, by default they should have the token removed so its only the
     * value we have in parameters however there are some cases where the endpoint may act as a proxy for another
     * endpoint and you need to preserve the values as they are.
     */
    protected boolean resolveRawParameterValues() {
        // should resolve raw parameters by default
        return true;
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext context) {
        this.zwangineContext = context;
    }

    @Override
    public String getDefaultName() {
        return defaultName;
    }

    @Override
    protected void doBuild() throws Exception {
        if (defaultName == null) {
            org.zenithblox.spi.annotations.Component ann
                    = ObjectHelper.getAnnotation(this, org.zenithblox.spi.annotations.Component.class);
            if (ann != null) {
                String name = ann.value();
                // just grab first scheme name if the component has scheme alias (eg http,https)
                if (name.contains(",")) {
                    name = StringHelper.before(name, ",");
                }
                defaultName = name;
            }
        }
        if (defaultName != null) {
            if (componentPropertyConfigurer == null) {
                componentPropertyConfigurer = PluginHelper.getConfigurerResolver(getZwangineContext())
                        .resolvePropertyConfigurer(defaultName + "-component-configurer", getZwangineContext());
            }
            if (endpointPropertyConfigurer == null) {
                endpointPropertyConfigurer = PluginHelper.getConfigurerResolver(getZwangineContext())
                        .resolvePropertyConfigurer(defaultName + "-endpoint-configurer", getZwangineContext());
            }
        }
    }

    @Override
    protected void doInit() throws Exception {
        ObjectHelper.notNull(getZwangineContext(), "zwangineContext");
    }

    /**
     * A factory method allowing derived components to create a new endpoint from the given URI, remaining path and
     * optional parameters
     *
     * @param  uri        the full URI of the endpoint
     * @param  remaining  the remaining part of the URI without the query parameters or component prefix
     * @param  parameters the optional parameters passed in
     * @return            a newly created endpoint or null if the endpoint cannot be created based on the inputs
     * @throws Exception  is thrown if error creating the endpoint
     */
    protected abstract Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters)
            throws Exception;

    /**
     * Configure an endpoint using the given parameters. In the usual cases, this is the only call needed after having
     * created the endpoint in the {@link #createEndpoint(String, String, Map)} method's implementation.
     *
     * This method will call the {@link Endpoint#configureProperties(Map)} method which should delegate the the
     * endpoint's {@link PropertyConfigurer} instance. In some rare cases, you need to override this method to
     * explicitely set parameters in case a simple generated configurer can not be used.
     *
     * @param endpoint   the endpoint
     * @param parameters properties to set
     */
    protected void setProperties(Endpoint endpoint, Map<String, Object> parameters) throws Exception {
        endpoint.configureProperties(parameters);
    }

    /**
     * Sets the bean properties on the given bean
     *
     * @param bean       the bean
     * @param parameters properties to set
     */
    protected void setProperties(Object bean, Map<String, Object> parameters) throws Exception {
        setProperties(getZwangineContext(), bean, parameters);
    }

    /**
     * Sets the bean properties on the given bean using the given {@link ZwangineContext}.
     *
     * @param zwangineContext the {@link ZwangineContext} to use
     * @param bean         the bean
     * @param parameters   properties to set
     */
    protected void setProperties(ZwangineContext zwangineContext, Object bean, Map<String, Object> parameters) throws Exception {
        if (parameters == null || parameters.isEmpty()) {
            return;
        }

        PropertyConfigurer configurer;
        if (bean instanceof Component) {
            configurer = getComponentPropertyConfigurer();
        } else if (bean instanceof Endpoint) {
            configurer = getEndpointPropertyConfigurer();
        } else if (bean instanceof PropertyConfigurerAware propertyConfigurerAware) {
            configurer = propertyConfigurerAware.getPropertyConfigurer(bean);
        } else {
            configurer = null;
        }
        // use configurer and ignore case as end users may type an option name with mixed case
        PropertyBindingSupport.build().withConfigurer(configurer).withIgnoreCase(true).bind(zwangineContext, bean, parameters);
    }

    @Override
    public PropertyConfigurer getComponentPropertyConfigurer() {
        return componentPropertyConfigurer;
    }

    @Override
    public PropertyConfigurer getEndpointPropertyConfigurer() {
        return endpointPropertyConfigurer;
    }

    /**
     * Derived classes may wish to overload this to prevent the default introspection of URI parameters on the created
     * {@link Endpoint} instance.
     */
    protected boolean useIntrospectionOnEndpoint() {
        return true;
    }

    /**
     * Gets the parameter and remove it from the parameter map. This method doesn't resolve reference parameters in the
     * registry.
     *
     * @param  parameters the parameters
     * @param  key        the key
     * @param  type       the requested type to convert the value from the parameter
     * @return            the converted value parameter, <tt>null</tt> if parameter does not exist.
     * @see               #resolveAndRemoveReferenceParameter(Map, String, Class)
     */
    public <T> T getAndRemoveParameter(Map<String, Object> parameters, String key, Class<T> type) {
        return getAndRemoveParameter(parameters, key, type, null);
    }

    /**
     * Gets the parameter and remove it from the parameter map. This method doesn't resolve reference parameters in the
     * registry.
     *
     * @param  parameters   the parameters
     * @param  key          the key
     * @param  type         the requested type to convert the value from the parameter
     * @param  defaultValue use this default value if the parameter does not contain the key
     * @return              the converted value parameter
     * @see                 #resolveAndRemoveReferenceParameter(Map, String, Class, Object)
     */
    public <T> T getAndRemoveParameter(Map<String, Object> parameters, String key, Class<T> type, T defaultValue) {
        Object value = parameters.remove(key);
        if (value != null) {
            // if we have a value then convert it
            return ZwangineContextHelper.mandatoryConvertTo(getZwangineContext(), type, value);
        } else {
            value = defaultValue;
        }
        if (value == null) {
            return null;
        }

        return ZwangineContextHelper.mandatoryConvertTo(getZwangineContext(), type, value);
    }

    /**
     * Gets the parameter and remove it from the parameter map. This method resolves reference parameters in the
     * registry as well.
     *
     * @param  parameters the parameters
     * @param  key        the key
     * @param  type       the requested type to convert the value from the parameter
     * @return            the converted value parameter
     */
    public <T> T getAndRemoveOrResolveReferenceParameter(Map<String, Object> parameters, String key, Class<T> type) {
        return getAndRemoveOrResolveReferenceParameter(parameters, key, type, null);
    }

    /**
     * Gets the parameter and remove it from the parameter map. This method resolves reference parameters in the
     * registry as well.
     *
     * @param  parameters   the parameters
     * @param  key          the key
     * @param  type         the requested type to convert the value from the parameter
     * @param  defaultValue use this default value if the parameter does not contain the key
     * @return              the converted value parameter
     */
    public <T> T getAndRemoveOrResolveReferenceParameter(
            Map<String, Object> parameters, String key, Class<T> type, T defaultValue) {
        // the parameter may be the the type already (such as from endpoint-dsl)
        Object value = parameters.remove(key);
        if (value instanceof String str) {
            if (EndpointHelper.isReferenceParameter(str)) {
                return EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
            }
        }
        if (type.isInstance(value)) {
            // special for string references
            if (String.class == type) {
                String str = value.toString();
                if (EndpointHelper.isReferenceParameter(str)) {
                    value = EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
                }
            }
            return type.cast(value);
        } else if (value == null) {
            return defaultValue;
        } else {
            // okay so it may be a reference so value should be string
            String str = getZwangineContext().getTypeConverter().tryConvertTo(String.class, value);
            if (EndpointHelper.isReferenceParameter(str)) {
                return EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
            } else {
                return getZwangineContext().getTypeConverter().convertTo(type, value);
            }
        }
    }

    /**
     * Resolves a reference parameter in the registry and removes it from the map.
     *
     * @param  <T>                      type of object to lookup in the registry.
     * @param  parameters               parameter map.
     * @param  key                      parameter map key.
     * @param  type                     type of object to lookup in the registry.
     * @return                          the referenced object or <code>null</code> if the parameter map doesn't contain
     *                                  the key.
     * @throws IllegalArgumentException if a non-null reference was not found in registry.
     */
    public <T> T resolveAndRemoveReferenceParameter(Map<String, Object> parameters, String key, Class<T> type) {
        return resolveAndRemoveReferenceParameter(parameters, key, type, null);
    }

    /**
     * Resolves a reference parameter in the registry and removes it from the map.
     *
     * @param  <T>                      type of object to lookup in the registry.
     * @param  parameters               parameter map.
     * @param  key                      parameter map key.
     * @param  type                     type of object to lookup in the registry.
     * @param  defaultValue             default value to use if the parameter map doesn't contain the key.
     * @return                          the referenced object or the default value.
     * @throws IllegalArgumentException if referenced object was not found in registry.
     */
    public <T> T resolveAndRemoveReferenceParameter(Map<String, Object> parameters, String key, Class<T> type, T defaultValue) {
        // the parameter may be the the type already (such as from endpoint-dsl)
        Object value = parameters.remove(key);
        if (value instanceof String str) {
            if (EndpointHelper.isReferenceParameter(str)) {
                return EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
            }
        }
        if (type.isInstance(value)) {
            // special for string references
            if (String.class == type) {
                String str = value.toString();
                if (EndpointHelper.isReferenceParameter(str)) {
                    value = EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
                }
            }
            return type.cast(value);
        } else if (value == null) {
            return defaultValue;
        } else {
            // okay so it may be a reference so value should be string
            String str = getZwangineContext().getTypeConverter().tryConvertTo(String.class, value);
            return EndpointHelper.resolveReferenceParameter(getZwangineContext(), str, type);
        }
    }

    /**
     * Resolves a reference list parameter in the registry and removes it from the map.
     *
     * @param  parameters               parameter map.
     * @param  key                      parameter map key.
     * @param  elementType              result list element type.
     * @return                          the list of referenced objects or an empty list if the parameter map doesn't
     *                                  contain the key.
     * @throws IllegalArgumentException if any of the referenced objects was not found in registry.
     * @see                             EndpointHelper#resolveReferenceListParameter(ZwangineContext, String, Class)
     */
    public <T> List<T> resolveAndRemoveReferenceListParameter(
            Map<String, Object> parameters, String key, Class<T> elementType) {
        return resolveAndRemoveReferenceListParameter(parameters, key, elementType, new ArrayList<>(0));
    }

    /**
     * Resolves a reference list parameter in the registry and removes it from the map.
     *
     * @param  parameters               parameter map.
     * @param  key                      parameter map key.
     * @param  elementType              result list element type.
     * @param  defaultValue             default value to use if the parameter map doesn't contain the key.
     * @return                          the list of referenced objects or the default value.
     * @throws IllegalArgumentException if any of the referenced objects was not found in registry.
     * @see                             EndpointHelper#resolveReferenceListParameter(ZwangineContext, String, Class)
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> resolveAndRemoveReferenceListParameter(
            Map<String, Object> parameters, String key, Class<T> elementType, List<T> defaultValue) {
        // the value may already be a list such as when using endpoint-dsl
        Object value = getAndRemoveParameter(parameters, key, Object.class);
        if (value instanceof List) {
            return (List<T>) value;
        }
        if (value == null) {
            return defaultValue;
        } else {
            // okay so it may be a reference so value should be string
            String str = getZwangineContext().getTypeConverter().tryConvertTo(String.class, value);
            return EndpointHelper.resolveReferenceListParameter(getZwangineContext(), str, elementType);
        }
    }

    /**
     * Returns the reminder of the text if it starts with the prefix.
     * <p/>
     * Is useable for string parameters that contains commands.
     *
     * @param  prefix the prefix
     * @param  text   the text
     * @return        the reminder, or null if no reminder
     */
    protected String ifStartsWithReturnRemainder(String prefix, String text) {
        if (text.startsWith(prefix)) {
            String remainder = text.substring(prefix.length());
            if (!remainder.isEmpty()) {
                return remainder;
            }
        }
        return null;
    }

    protected void registerExtension(ComponentExtension extension) {
        extensions.add(() -> extension);
    }

    protected void registerExtension(Supplier<ComponentExtension> supplier) {
        extensions.add(Suppliers.memorize(supplier));
    }

    @Override
    public Collection<Class<? extends ComponentExtension>> getSupportedExtensions() {
        return extensions.stream()
                .map(Supplier::get)
                .map(ComponentExtension::getClass)
                .collect(Collectors.toList());
    }

    @Override
    public <T extends ComponentExtension> Optional<T> getExtension(Class<T> extensionType) {
        return extensions.stream()
                .map(Supplier::get)
                .filter(extensionType::isInstance)
                .findFirst()
                .map(extensionType::cast)
                .map(e -> Component.trySetComponent(e, this))
                .map(e -> ZwangineContextAware.trySetZwangineContext(e, getZwangineContext()));
    }
}
