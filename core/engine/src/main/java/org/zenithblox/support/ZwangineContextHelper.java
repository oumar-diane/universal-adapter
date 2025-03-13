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
import org.zenithblox.clock.Clock;
import org.zenithblox.clock.EventClock;
import org.zenithblox.spi.NormalizedEndpointUri;
import org.zenithblox.spi.RestConfiguration;
import org.zenithblox.spi.WorkflowStartupOrder;
import org.zenithblox.util.ObjectHelper;
import org.zenithblox.util.TimeUtils;

import java.time.Duration;
import java.util.*;
import java.util.function.Predicate;

import static org.zenithblox.util.ObjectHelper.isNotEmpty;

/**
 * A number of helper methods
 */
public final class ZwangineContextHelper {

    public static final String MODEL_DOCUMENTATION_PREFIX = "META-INF/org/zentihblox/zwangine/model/";

    /**
     * Utility classes should not have a public constructor.
     */
    private ZwangineContextHelper() {
    }

    /**
     * Returns the mandatory endpoint for the given URI or the {@link org.zenithblox.NoSuchEndpointException} is
     * thrown
     */
    public static Endpoint getMandatoryEndpoint(ZwangineContext zwangineContext, String uri)
            throws NoSuchEndpointException {
        Endpoint endpoint = zwangineContext.getEndpoint(uri);
        if (endpoint == null) {
            throw new NoSuchEndpointException(uri);
        } else {
            return endpoint;
        }
    }

    /**
     * Returns the mandatory endpoint for the given URI or the {@link org.zenithblox.NoSuchEndpointException} is
     * thrown
     */
    public static Endpoint getMandatoryEndpoint(ZwangineContext zwangineContext, NormalizedEndpointUri uri)
            throws NoSuchEndpointException {
        Endpoint endpoint = zwangineContext.getZwangineContextExtension().getEndpoint(uri);
        if (endpoint == null) {
            throw new NoSuchEndpointException(uri.getUri());
        } else {
            return endpoint;
        }
    }

    /**
     * Returns the mandatory endpoint (prototype scope) for the given URI or the
     * {@link org.zenithblox.NoSuchEndpointException} is thrown
     */
    public static Endpoint getMandatoryPrototypeEndpoint(ZwangineContext zwangineContext, String uri)
            throws NoSuchEndpointException {
        Endpoint endpoint = zwangineContext.getZwangineContextExtension().getPrototypeEndpoint(uri);
        if (endpoint == null) {
            throw new NoSuchEndpointException(uri);
        } else {
            return endpoint;
        }
    }

    /**
     * Returns the mandatory endpoint (prototype scope) for the given URI or the
     * {@link org.zenithblox.NoSuchEndpointException} is thrown
     */
    public static Endpoint getMandatoryPrototypeEndpoint(ZwangineContext zwangineContext, NormalizedEndpointUri uri)
            throws NoSuchEndpointException {
        Endpoint endpoint = zwangineContext.getZwangineContextExtension().getPrototypeEndpoint(uri);
        if (endpoint == null) {
            throw new NoSuchEndpointException(uri.getUri());
        } else {
            return endpoint;
        }
    }

    /**
     * Returns the mandatory endpoint for the given URI and type or the {@link org.zenithblox.NoSuchEndpointException}
     * is thrown
     */
    public static <T extends Endpoint> T getMandatoryEndpoint(ZwangineContext zwangineContext, String uri, Class<T> type) {
        Endpoint endpoint = getMandatoryEndpoint(zwangineContext, uri);
        return ObjectHelper.cast(type, endpoint);
    }

    public static Endpoint resolveEndpoint(ZwangineContext zwangineContext, String uri, String ref) {
        Endpoint endpoint = null;
        if (uri != null) {
            endpoint = zwangineContext.getEndpoint(uri);
            if (endpoint == null) {
                throw new NoSuchEndpointException(uri);
            }
        }
        if (ref != null) {
            endpoint = zwangineContext.getRegistry().lookupByNameAndType(ref, Endpoint.class);
            if (endpoint == null) {
                throw new NoSuchEndpointException("ref:" + ref, "check your zwangine registry with id " + ref);
            }
            // Check the endpoint has the right ZwangineContext
            if (!zwangineContext.equals(endpoint.getZwangineContext())) {
                throw new NoSuchEndpointException(
                        "ref:" + ref, "make sure the endpoint has the same zwangine context as the workflow does.");
            }
            try {
                // need add the endpoint into service
                zwangineContext.addService(endpoint);
            } catch (Exception ex) {
                throw new RuntimeZwangineException(ex);
            }
        }
        if (endpoint == null) {
            throw new IllegalArgumentException("Either 'uri' or 'ref' must be specified");
        } else {
            return endpoint;
        }
    }

    /**
     * Converts the given value to the requested type
     */
    public static <T> T convertTo(ZwangineContext context, Class<T> type, Object value) {
        return context.getTypeConverter().convertTo(type, value);
    }

    /**
     * Tried to convert the given value to the requested type
     */
    public static <T> T tryConvertTo(ZwangineContext context, Class<T> type, Object value) {
        return context.getTypeConverter().tryConvertTo(type, value);
    }

    /**
     * Converts the given value to the specified type throwing an {@link IllegalArgumentException} if the value could
     * not be converted to a non null value
     */
    public static <T> T mandatoryConvertTo(ZwangineContext context, Class<T> type, Object value) {
        T answer = convertTo(context, type, value);
        if (answer == null) {
            throw new IllegalArgumentException("Value " + value + " converted to " + type.getName() + " cannot be null");
        }
        return answer;
    }

    /**
     * Creates a new instance of the given type using the {@link org.zenithblox.spi.Injector} on the given
     * {@link ZwangineContext}
     */
    public static <T> T newInstance(ZwangineContext context, Class<T> beanType) {
        return context.getInjector().newInstance(beanType);
    }

    /**
     * Look up the given named bean in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext}
     */
    public static Object lookup(ZwangineContext context, String name) {
        return context.getRegistry().lookupByName(name);
    }

    /**
     * Look up the given named bean of the given type in the {@link org.zenithblox.spi.Registry} on the
     * {@link ZwangineContext}
     */
    public static <T> T lookup(ZwangineContext context, String name, Class<T> beanType) {
        return context.getRegistry().lookupByNameAndType(name, beanType);
    }

    /**
     * Look up the given named bean in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext} and try to
     * convert it to the given type.
     */
    public static <T> T lookupAndConvert(ZwangineContext context, String name, Class<T> beanType) {
        return tryConvertTo(context, beanType, lookup(context, name));
    }

    /**
     * Look up a bean of the give type in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext}
     * returning an instance if only one bean is present,
     */
    public static <T> T findSingleByType(ZwangineContext zwangineContext, Class<T> type) {
        return zwangineContext.getRegistry().findSingleByType(type);
    }

    /**
     * Look up a bean of the give type in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext} or
     * throws {@link org.zenithblox.NoSuchBeanTypeException} if not a single bean was found.
     */
    public static <T> T mandatoryFindSingleByType(ZwangineContext zwangineContext, Class<T> type) {
        return zwangineContext.getRegistry().mandatoryFindSingleByType(type);
    }

    /**
     * Look up the given named bean in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext} or throws
     * {@link NoSuchBeanException} if not found.
     */
    public static Object mandatoryLookup(ZwangineContext context, String name) {
        Object answer = lookup(context, name);
        if (answer == null) {
            throw new NoSuchBeanException(name);
        }
        return answer;
    }

    /**
     * Look up the given named bean of the given type in the {@link org.zenithblox.spi.Registry} on the
     * {@link ZwangineContext} or throws NoSuchBeanException if not found.
     */
    public static <T> T mandatoryLookup(ZwangineContext context, String name, Class<T> beanType) {
        T answer = lookup(context, name, beanType);
        if (answer == null) {
            throw new NoSuchBeanException(name, beanType.getName());
        }
        return answer;
    }

    /**
     * Look up the given named bean in the {@link org.zenithblox.spi.Registry} on the {@link ZwangineContext} and convert
     * it to the given type or throws NoSuchBeanException if not found.
     */
    public static <T> T mandatoryLookupAndConvert(ZwangineContext context, String name, Class<T> beanType) {
        Object value = lookup(context, name);
        if (value == null) {
            throw new NoSuchBeanException(name, beanType.getName());
        }
        return convertTo(context, beanType, value);
    }

    /**
     * Evaluates the @EndpointInject annotation using the given context
     */
    public static Endpoint getEndpointInjection(
            ZwangineContext zwangineContext, String uri, String injectionPointName, boolean mandatory) {
        Endpoint endpoint;
        if (isNotEmpty(uri)) {
            endpoint = zwangineContext.getEndpoint(uri);
        } else {
            if (mandatory) {
                endpoint = mandatoryLookup(zwangineContext, injectionPointName, Endpoint.class);
            } else {
                endpoint = lookup(zwangineContext, injectionPointName, Endpoint.class);
            }
        }
        return endpoint;
    }

    /**
     * Gets the maximum cache pool size.
     * <p/>
     * Will use the property set on ZwangineContext with the key {@link Exchange#MAXIMUM_CACHE_POOL_SIZE}. If no property
     * has been set, then it will fallback to return a size of 1000.
     *
     * @param  zwangineContext             the zwangine context
     * @return                          the maximum cache size
     * @throws IllegalArgumentException is thrown if the property is illegal
     */
    public static int getMaximumCachePoolSize(ZwangineContext zwangineContext) throws IllegalArgumentException {
        return getPositiveIntegerProperty(zwangineContext, Exchange.MAXIMUM_CACHE_POOL_SIZE);
    }

    /**
     * Gets the maximum endpoint cache size.
     * <p/>
     * Will use the property set on ZwangineContext with the key {@link Exchange#MAXIMUM_ENDPOINT_CACHE_SIZE}. If no
     * property has been set, then it will fallback to return a size of 1000.
     *
     * @param  zwangineContext             the zwangine context
     * @return                          the maximum cache size
     * @throws IllegalArgumentException is thrown if the property is illegal
     */
    public static int getMaximumEndpointCacheSize(ZwangineContext zwangineContext) throws IllegalArgumentException {
        return getPositiveIntegerProperty(zwangineContext, Exchange.MAXIMUM_ENDPOINT_CACHE_SIZE);
    }

    /**
     * Gets the maximum simple cache size.
     * <p/>
     * Will use the property set on ZwangineContext with the key {@link Exchange#MAXIMUM_SIMPLE_CACHE_SIZE}. If no property
     * has been set, then it will fallback to return a size of 1000. Use value of 0 or negative to disable the cache.
     *
     * @param  zwangineContext             the zwangine context
     * @return                          the maximum cache size
     * @throws IllegalArgumentException is thrown if the property is illegal
     */
    public static int getMaximumSimpleCacheSize(ZwangineContext zwangineContext) throws IllegalArgumentException {
        return getPositiveIntegerProperty(zwangineContext, Exchange.MAXIMUM_SIMPLE_CACHE_SIZE);
    }

    /**
     * Gets the maximum transformer cache size.
     * <p/>
     * Will use the property set on ZwangineContext with the key {@link Exchange#MAXIMUM_TRANSFORMER_CACHE_SIZE}. If no
     * property has been set, then it will fallback to return a size of 1000.
     *
     * @param  zwangineContext             the zwangine context
     * @return                          the maximum cache size
     * @throws IllegalArgumentException is thrown if the property is illegal
     */
    public static int getMaximumTransformerCacheSize(ZwangineContext zwangineContext) throws IllegalArgumentException {
        return getPositiveIntegerProperty(zwangineContext, Exchange.MAXIMUM_TRANSFORMER_CACHE_SIZE);
    }

    private static int getPositiveIntegerProperty(ZwangineContext zwangineContext, String property) {
        if (zwangineContext != null) {
            String s = zwangineContext.getGlobalOption(property);
            if (s != null) {
                // we cannot use Zwangine type converters as they may not be ready this early
                try {
                    int size = Integer.parseInt(s);
                    if (size <= 0) {
                        throw new IllegalArgumentException(
                                "Property " + property + " must be a positive number, was: "
                                                           + s);
                    }
                    return size;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException(
                            "Property " + property + " must be a positive number, was: " + s, e);
                }
            }
        }

        // 1000 is the default fallback
        return 1000;
    }

    /**
     * Gets the maximum validator cache size.
     * <p/>
     * Will use the property set on ZwangineContext with the key {@link Exchange#MAXIMUM_VALIDATOR_CACHE_SIZE}. If no
     * property has been set, then it will fallback to return a size of 1000.
     *
     * @param  zwangineContext             the zwangine context
     * @return                          the maximum cache size
     * @throws IllegalArgumentException is thrown if the property is illegal
     */
    public static int getMaximumValidatorCacheSize(ZwangineContext zwangineContext) throws IllegalArgumentException {
        return getPositiveIntegerProperty(zwangineContext, Exchange.MAXIMUM_VALIDATOR_CACHE_SIZE);
    }

    /**
     * Parses the given text and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the parsed text, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument
     */
    public static String parseText(ZwangineContext zwangineContext, String text) {
        // ensure we support property placeholders
        return zwangineContext.resolvePropertyPlaceholders(text);
    }

    /**
     * Parses the given text and converts it to an Integer and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the integer vale, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Integer parseInteger(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Integer.class, text);
    }

    /**
     * Parses the given text and converts it to an Integer and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the int value, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Integer parseInt(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Integer.class, text);
    }

    /**
     * Parses the given text and converts it to an Long and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the long value, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Long parseLong(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Long.class, text);
    }

    /**
     * Parses the given text and converts it to a Duration and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the Duration value, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Duration parseDuration(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Duration.class, text);
    }

    /**
     * Parses the given text and converts it to a Float and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the float value, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Float parseFloat(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Float.class, text);
    }

    /**
     * Parses the given text and converts it to a Double and handling property placeholders as well
     *
     * @param  zwangineContext          the zwangine context
     * @param  text                  the text
     * @return                       the double vale, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalStateException is thrown if illegal argument or type conversion not possible
     */
    public static Double parseDouble(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Double.class, text);
    }

    /**
     * Parses the given text and converts it to an Boolean and handling property placeholders as well
     *
     * @param  zwangineContext             the zwangine context
     * @param  text                     the text
     * @return                          the boolean vale, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalArgumentException is thrown if illegal argument or type conversion not possible
     */
    public static Boolean parseBoolean(ZwangineContext zwangineContext, String text) {
        return parse(zwangineContext, Boolean.class, text);
    }

    /**
     * Parses the given text and converts it to the specified class and handling property placeholders as well
     *
     * @param  zwangineContext             the zwangine context
     * @param  clazz                    the class to convert the value to
     * @param  text                     the text
     * @return                          the boolean vale, or <tt>null</tt> if the text was <tt>null</tt>
     * @throws IllegalArgumentException is thrown if illegal argument or type conversion not possible
     */
    public static <T> T parse(ZwangineContext zwangineContext, Class<T> clazz, String text) {
        // ensure we support property placeholders
        String s = zwangineContext.resolvePropertyPlaceholders(text);
        if (s != null) {
            try {
                return zwangineContext.getTypeConverter().mandatoryConvertTo(clazz, s);
            } catch (Exception e) {
                if (s.equals(text)) {
                    throw new IllegalArgumentException("Error parsing [" + s + "] as a " + clazz.getName() + ".", e);
                } else {
                    throw new IllegalArgumentException(
                            "Error parsing [" + s + "] from property " + text + " as a " + clazz.getName() + ".", e);
                }
            }
        }
        return null;
    }

    /**
     * Gets the workflow startup order for the given workflow id
     *
     * @param  zwangineContext the zwangine context
     * @param  workflowId      the id of the workflow
     * @return              the startup order, or <tt>0</tt> if not possible to determine
     */
    public static int getWorkflowStartupOrder(ZwangineContext zwangineContext, String workflowId) {
        for (WorkflowStartupOrder order : zwangineContext.getZwangineContextExtension().getWorkflowStartupOrder()) {
            if (order.getWorkflow().getId().equals(workflowId)) {
                return order.getStartupOrder();
            }
        }
        return 0;
    }

    /**
     * A helper method to access a zwangine context properties with a prefix
     *
     * @param  prefix       the prefix
     * @param  zwangineContext the zwangine context
     * @return              the properties which holds the zwangine context properties with the prefix, and the key omit
     *                      the prefix part
     */
    public static Properties getZwanginePropertiesWithPrefix(String prefix, ZwangineContext zwangineContext) {
        Properties answer = new Properties();
        Map<String, String> zwangineProperties = zwangineContext.getGlobalOptions();
        if (zwangineProperties != null) {
            for (Map.Entry<String, String> entry : zwangineProperties.entrySet()) {
                String key = entry.getKey();
                if (key != null && key.startsWith(prefix)) {
                    answer.put(key.substring(prefix.length()), entry.getValue());
                }
            }
        }
        return answer;
    }

    /**
     * Gets the workflow id the given node belongs to.
     *
     * @param  node the node
     * @return      the workflow id, or <tt>null</tt> if not possible to find
     */
    public static String getWorkflowId(NamedNode node) {
        NamedNode parent = node;
        while (parent != null && parent.getParent() != null) {
            parent = parent.getParent();
        }
        return parent != null ? parent.getId() : null;
    }

    /**
     * Gets the workflow the given node belongs to.
     *
     * @param  node the node
     * @return      the workflow, or <tt>null</tt> if not possible to find
     */
    public static NamedWorkflow getWorkflow(NamedNode node) {
        NamedNode parent = node;
        while (parent != null && parent.getParent() != null) {
            parent = parent.getParent();
        }
        if (parent instanceof NamedWorkflow namedWorkflow) {
            return namedWorkflow;
        }
        return null;
    }

    /**
     * Gets the {@link RestConfiguration} from the {@link ZwangineContext} and check if the component which consumes the
     * configuration is compatible with the one for which the rest configuration is set-up.
     *
     * @param  zwangineContext             the zwangine context
     * @param  component                the component that will consume the {@link RestConfiguration}
     * @return                          the {@link RestConfiguration}
     * @throws IllegalArgumentException is the component is not compatible with the {@link RestConfiguration} set-up
     */
    public static RestConfiguration getRestConfiguration(ZwangineContext zwangineContext, String component) {
        RestConfiguration configuration = zwangineContext.getRestConfiguration();

        validateRestConfigurationComponent(component, configuration.getComponent());

        return configuration;
    }

    /**
     * Gets the {@link RestConfiguration} from the {@link ZwangineContext} and check if the component which consumes the
     * configuration is compatible with the one for which the rest configuration is set-up.
     *
     * @param  zwangineContext             the zwangine context
     * @param  component                the component that will consume the {@link RestConfiguration}
     * @param  producerComponent        the producer component that will consume the {@link RestConfiguration}
     * @return                          the {@link RestConfiguration}
     * @throws IllegalArgumentException is the component is not compatible with the {@link RestConfiguration} set-up
     */
    public static RestConfiguration getRestConfiguration(
            ZwangineContext zwangineContext, String component, String producerComponent) {
        RestConfiguration configuration = zwangineContext.getRestConfiguration();

        validateRestConfigurationComponent(component, configuration.getComponent());
        validateRestConfigurationComponent(producerComponent, configuration.getProducerComponent());

        return configuration;
    }

    /**
     * Gets the components from the given {@code ZwangineContext} that match with the given predicate.
     *
     * @param  zwangineContext the zwangine context
     * @param  predicate    the predicate to evaluate to know whether a given component should be returned or not.
     * @return              the existing components that match the predicate.
     */
    public static List<Component> getComponents(ZwangineContext zwangineContext, Predicate<Component> predicate) {
        return zwangineContext.getComponentNames().stream()
                .map(zwangineContext::getComponent)
                .filter(predicate)
                .toList();
    }

    /**
     * Gets the endpoints from the given {@code ZwangineContext} that match with the given predicate
     *
     * @param  zwangineContext the zwangine context
     * @param  predicate    the predicate to evaluate to know whether a given endpoint should be returned or not.
     * @return              the existing endpoints that match the predicate.
     */
    public static List<Endpoint> getEndpoints(ZwangineContext zwangineContext, Predicate<Endpoint> predicate) {
        return zwangineContext.getEndpoints().stream()
                .filter(predicate)
                .toList();
    }

    private static void validateRestConfigurationComponent(String component, String configurationComponent) {
        if (ObjectHelper.isEmpty(component) || ObjectHelper.isEmpty(configurationComponent)) {
            return;
        }

        if (!Objects.equals(component, configurationComponent)) {
            throw new IllegalArgumentException(
                    "No RestConfiguration for component: " + component + " found, RestConfiguration targets: "
                                               + configurationComponent);
        }
    }

    /**
     * Gets the uptime in a human-readable format
     *
     * @return the uptime in days/hours/minutes
     */
    public static String getUptime(ZwangineContext context) {
        long delta = context.getUptime().toMillis();
        if (delta == 0) {
            return "0ms";
        }

        return TimeUtils.printDuration(delta);
    }

    /**
     * Gets the uptime in milliseconds
     *
     * @return the uptime in milliseconds
     */
    public static long getUptimeMillis(ZwangineContext context) {
        return context.getUptime().toMillis();
    }

    /**
     * Gets the date and time Zwangine was started up.
     */
    public static Date getStartDate(ZwangineContext context) {
        EventClock<ContextEvents> contextClock = context.getClock();

        final Clock clock = contextClock.get(ContextEvents.START);
        if (clock == null) {
            return null;
        }

        return clock.asDate();
    }

}
