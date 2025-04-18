/* Generated by camel build tools - do NOT edit this file! */
package org.zenithblox.component.log;

import org.zenithblox.spi.EndpointUriFactory;

import javax.annotation.processing.Generated;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Generated by camel build tools - do NOT edit this file!
 */
@Generated("org.zenithblox.maven.packaging.GenerateEndpointUriFactoryMojo")
public class LogEndpointUriFactory extends org.zenithblox.support.component.EndpointUriFactorySupport implements EndpointUriFactory {

    private static final String BASE = ":loggerName";

    private static final Set<String> PROPERTY_NAMES;
    private static final Set<String> SECRET_PROPERTY_NAMES;
    private static final Set<String> MULTI_VALUE_PREFIXES;
    static {
        Set<String> props = new HashSet<>(34);
        props.add("exchangeFormatter");
        props.add("groupActiveOnly");
        props.add("groupDelay");
        props.add("groupInterval");
        props.add("groupSize");
        props.add("lazyStartProducer");
        props.add("level");
        props.add("logMask");
        props.add("loggerName");
        props.add("marker");
        props.add("maxChars");
        props.add("multiline");
        props.add("plain");
        props.add("showAll");
        props.add("showAllProperties");
        props.add("showBody");
        props.add("showBodyType");
        props.add("showCachedStreams");
        props.add("showCaughtException");
        props.add("showException");
        props.add("showExchangeId");
        props.add("showExchangePattern");
        props.add("showFiles");
        props.add("showFuture");
        props.add("showHeaders");
        props.add("showProperties");
        props.add("showRouteGroup");
        props.add("showRouteId");
        props.add("showStackTrace");
        props.add("showStreams");
        props.add("showVariables");
        props.add("skipBodyLineSeparator");
        props.add("sourceLocationLoggerName");
        props.add("style");
        PROPERTY_NAMES = Collections.unmodifiableSet(props);
        SECRET_PROPERTY_NAMES = Collections.emptySet();
        MULTI_VALUE_PREFIXES = Collections.emptySet();
    }

    @Override
    public boolean isEnabled(String scheme) {
        return "log".equals(scheme);
    }

    @Override
    public String buildUri(String scheme, Map<String, Object> properties, boolean encode) throws URISyntaxException {
        String syntax = scheme + BASE;
        String uri = syntax;

        Map<String, Object> copy = new HashMap<>(properties);

        uri = buildPathParameter(syntax, uri, "loggerName", null, true, copy);
        uri = buildQueryParameters(uri, copy, encode);
        return uri;
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_NAMES;
    }

    @Override
    public Set<String> secretPropertyNames() {
        return SECRET_PROPERTY_NAMES;
    }

    @Override
    public Set<String> multiValuePrefixes() {
        return MULTI_VALUE_PREFIXES;
    }

    @Override
    public boolean isLenientProperties() {
        return false;
    }
}

