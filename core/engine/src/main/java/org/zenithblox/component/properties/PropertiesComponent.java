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
package org.zenithblox.component.properties;

import org.zenithblox.ZwangineContext;
import org.zenithblox.ZwangineContextAware;
import org.zenithblox.PropertiesLookupListener;
import org.zenithblox.StaticService;
import org.zenithblox.spi.*;
import org.zenithblox.spi.annotations.JdkService;
import org.zenithblox.support.OrderedComparator;
import org.zenithblox.support.PatternHelper;
import org.zenithblox.support.service.ServiceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * The properties component allows you to use property placeholders in Zwangine.
 */
@JdkService(org.zenithblox.spi.PropertiesComponent.FACTORY)
@Configurer(extended = true)
public class PropertiesComponent extends ServiceSupport
        implements org.zenithblox.spi.PropertiesComponent, StaticService, ZwangineContextAware {

    /**
     * Never check system properties.
     */
    public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

    /**
     * Check system properties if not resolvable in the specified properties.
     */
    public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

    /**
     * Check system properties variables) first, before trying the specified properties. This allows system properties
     * to override any other property source (environment variable and then system properties takes precedence).
     * <p/>
     * This is the default.
     */
    public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;

    /**
     * Never check OS environment variables.
     */
    public static final int ENVIRONMENT_VARIABLES_MODE_NEVER = 0;

    /**
     * Check OS environment variables if not resolvable in the specified properties.
     * <p/>
     * This is the default.
     */
    public static final int ENVIRONMENT_VARIABLES_MODE_FALLBACK = 1;

    /**
     * Check OS environment variables first, before trying the specified properties. This allows environment variables
     * to override any other property source (environment variable and then system properties takes precedence).
     */
    public static final int ENVIRONMENT_VARIABLES_MODE_OVERRIDE = 2;

    /**
     * Key for stores special override properties that containers such as OSGi can store in the OSGi service registry
     */
    public static final String OVERRIDE_PROPERTIES = PropertiesComponent.class.getName() + ".OverrideProperties";

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesComponent.class);

    private static final String NEGATE_PREFIX = PREFIX_TOKEN + "!";

    private ZwangineContext zwangineContext;
    private PropertiesFunctionResolver propertiesFunctionResolver = new DefaultPropertiesFunctionResolver();
    private PropertiesParser propertiesParser = new DefaultPropertiesParser(this);
    private final PropertiesLookup propertiesLookup = new DefaultPropertiesLookup(this);
    private final List<PropertiesLookupListener> propertiesLookupListeners = new ArrayList<>();
    private final PropertiesSourceFactory propertiesSourceFactory = new DefaultPropertiesSourceFactory(this);
    private final DefaultPropertiesLookupListener defaultPropertiesLookupListener = new DefaultPropertiesLookupListener();
    private final List<PropertiesSource> sources = new ArrayList<>();
    private List<PropertiesLocation> locations = new ArrayList<>();
    private String location;
    private boolean ignoreMissingLocation;
    private boolean ignoreMissingProperty;
    private boolean nestedPlaceholder = true;
    private String encoding;
    private boolean defaultFallbackEnabled = true;
    private Properties initialProperties;
    private Properties overrideProperties;
    private final Stack<Properties> localProperties = new Stack<>();;
    private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_OVERRIDE;
    private int environmentVariableMode = ENVIRONMENT_VARIABLES_MODE_OVERRIDE;
    private boolean autoDiscoverPropertiesSources = true;

    public PropertiesComponent() {
        addPropertiesLookupListener(defaultPropertiesLookupListener);
        // include out of the box functions
        addPropertiesFunction(new EnvPropertiesFunction());
        addPropertiesFunction(new SysPropertiesFunction());
        addPropertiesFunction(new ServicePropertiesFunction());
        addPropertiesFunction(new ServiceHostPropertiesFunction());
        addPropertiesFunction(new ServicePortPropertiesFunction());
    }

    /**
     * A list of locations to load properties. You can use comma to separate multiple locations.
     */
    public PropertiesComponent(String location) {
        this();
        setLocation(location);
    }

    /**
     * A list of locations to load properties.
     */
    public PropertiesComponent(String... locations) {
        this();
        setLocations(locations);
    }

    @Override
    public ZwangineContext getZwangineContext() {
        return zwangineContext;
    }

    @Override
    public void setZwangineContext(ZwangineContext zwangineContext) {
        this.zwangineContext = zwangineContext;
    }

    @Override
    public String parseUri(String uri) {
        return parseUri(uri, false);
    }

    @Override
    public String parseUri(String uri, boolean keepUnresolvedOptional) {
        return parseUri(uri, propertiesLookup, keepUnresolvedOptional);
    }

    @Override
    public Optional<String> resolveProperty(String key) {
        try {
            boolean keep = isIgnoreMissingProperty();
            String value = parseUri(key, propertiesLookup, keep);
            if (value == null) {
                return Optional.empty();
            }
            return Optional.of(value);
        } catch (IllegalArgumentException e) {
            // property not found
            return Optional.empty();
        }
    }

    @Override
    public Optional<PropertiesResolvedValue> getResolvedValue(String key) {
        return Optional.ofNullable(defaultPropertiesLookupListener.getProperty(key));
    }

    public void updateResolvedValue(String key, String newValue, String newSource) {
        defaultPropertiesLookupListener.updateValue(key, newValue, newSource);
    }

    @Override
    public Properties loadProperties() {
        // this method may be replaced by loadProperties(k -> true) but the underlying sources
        // may have some optimization for bulk load so let's keep it

        OrderedLocationProperties prop = new OrderedLocationProperties();

        // use initial properties
        if (initialProperties != null) {
            prop.putAll("initial", initialProperties);
        }

        if (!sources.isEmpty()) {
            // sources are ordered according to {@link org.zenithblox.support.OrderComparator} so
            // it is needed to iterate them in reverse order otherwise lower priority sources may
            // override properties from higher priority ones
            for (int i = sources.size(); i-- > 0;) {
                PropertiesSource ps = sources.get(i);
                if (ps instanceof LoadablePropertiesSource lps) {
                    Properties p = lps.loadProperties();
                    if (p instanceof OrderedLocationProperties propSource) {
                        prop.putAll(propSource);
                    } else if (ps instanceof LocationPropertiesSource propSource) {
                        String loc = propSource.getLocation().getPath();
                        prop.putAll(loc, p);
                    } else {
                        prop.putAll(lps.getName(), p);
                    }
                }
            }
        }

        // use override properties
        if (overrideProperties != null) {
            // make a copy to avoid affecting the original properties
            OrderedLocationProperties override = new OrderedLocationProperties();
            override.putAll(prop);
            override.putAll("override", overrideProperties);
            prop = override;
        }

        return prop;
    }

    @Override
    public Properties loadProperties(Predicate<String> filter) {
        return loadProperties(filter, k -> k);
    }

    @Override
    public Properties extractProperties(String optionPrefix, boolean nested) {
        Properties answer = new Properties();
        var map = loadPropertiesAsMap(k -> {
            boolean accept = k.startsWith(optionPrefix);
            if (accept && !nested) {
                int pos = k.lastIndexOf('.');
                accept = pos == -1 || pos <= optionPrefix.length();
            }
            return accept;
        });
        answer.putAll(PropertiesHelper.extractProperties(map, optionPrefix));
        return answer;
    }

    @Override
    public Properties loadProperties(Predicate<String> filter, Function<String, String> keyMapper) {
        OrderedLocationProperties prop = new OrderedLocationProperties();

        // use initial properties
        if (initialProperties != null) {
            for (String name : initialProperties.stringPropertyNames()) {
                if (filter.test(name)) {
                    Object value = initialProperties.get(name);
                    name = keyMapper.apply(name);
                    prop.put("initial", name, value);
                }
            }
        }

        if (!sources.isEmpty()) {
            // sources are ordered according to {@link org.zenithblox.support.OrderComparator} so
            // it is needed to iterate them in reverse order otherwise lower priority sources may
            // override properties from higher priority ones
            for (int i = sources.size(); i-- > 0;) {
                PropertiesSource ps = sources.get(i);
                if (ps instanceof LoadablePropertiesSource lps) {
                    Properties p = lps.loadProperties(filter);
                    if (p instanceof OrderedLocationProperties olp) {
                        for (String name : olp.stringPropertyNames()) {
                            String loc = olp.getLocation(name);
                            Object value = olp.getProperty(name);
                            name = keyMapper.apply(name);
                            prop.put(loc, name, value);
                        }
                    } else {
                        String loc = lps.getName();
                        if (ps instanceof LocationPropertiesSource olp) {
                            loc = olp.getLocation().getPath();
                        }
                        for (String name : p.stringPropertyNames()) {
                            Object value = p.getProperty(name);
                            name = keyMapper.apply(name);
                            prop.put(loc, name, value);
                        }
                    }
                }
            }
        }

        // use override properties
        if (overrideProperties != null) {
            for (String name : overrideProperties.stringPropertyNames()) {
                if (filter.test(name)) {
                    Object value = overrideProperties.get(name);
                    name = keyMapper.apply(name);
                    prop.put("override", name, value);
                }
            }
        }

        return prop;
    }

    protected String parseUri(final String uri, PropertiesLookup properties, boolean keepUnresolvedOptional) {
        LOG.trace("Parsing uri {}", uri);

        String key = uri;
        // enclose tokens if missing
        if (!key.contains(PREFIX_TOKEN) && !key.startsWith(PREFIX_TOKEN)) {
            key = PREFIX_TOKEN + key;
        }
        if (!key.contains(SUFFIX_TOKEN) && !key.endsWith(SUFFIX_TOKEN)) {
            key = key + SUFFIX_TOKEN;
        }

        // if key starts with a ! then negate a boolean response
        boolean negate = key.startsWith(NEGATE_PREFIX);
        if (negate) {
            key = PREFIX_TOKEN + key.substring(NEGATE_PREFIX.length());
        }

        String answer
                = propertiesParser.parseUri(key, properties, defaultFallbackEnabled, keepUnresolvedOptional, nestedPlaceholder);
        if (negate) {
            if ("true".equalsIgnoreCase(answer)) {
                answer = "false";
            } else if ("false".equalsIgnoreCase(answer)) {
                answer = "true";
            }
        }
        if (answer != null) {
            // Remove the escape characters if any
            answer = unescape(answer);
        }
        LOG.trace("Parsed uri {} -> {}", uri, answer);
        return answer;
    }

    @Override
    public List<String> getLocations() {
        if (locations.isEmpty()) {
            return Collections.emptyList();
        } else {
            return locations.stream().map(PropertiesLocation::toString).toList();
        }
    }

    /**
     * A list of locations to load properties. This option will override any default locations and only use the
     * locations from this option.
     */
    public void setLocations(List<PropertiesLocation> locations) {
        // reset locations
        locations = parseLocations(locations);
        this.locations = Collections.unmodifiableList(locations);

        // we need to re-create the property sources which may have already been created from locations
        this.sources.removeIf(s -> s instanceof LocationPropertiesSource);
        // ensure the locations are in the same order as here, and therefore we provide the order number
        int order = 100;
        for (PropertiesLocation loc : locations) {
            addPropertiesLocationsAsPropertiesSource(loc, order++);
        }
    }

    /**
     * A list of locations to load properties. This option will override any default locations and only use the
     * locations from this option.
     */
    public void setLocations(String[] locationStrings) {
        List<PropertiesLocation> propertiesLocations = new ArrayList<>();
        if (locationStrings != null) {
            for (String locationString : locationStrings) {
                propertiesLocations.add(new PropertiesLocation(locationString));
            }
        }

        setLocations(propertiesLocations);
    }

    @Override
    public PropertiesSourceFactory getPropertiesSourceFactory() {
        return propertiesSourceFactory;
    }

    public void addLocation(PropertiesLocation location) {
        this.locations.add(location);
    }

    @Override
    public void addLocation(String location) {
        if (location != null) {
            List<PropertiesLocation> newLocations = new ArrayList<>();
            for (String loc : location.split(",")) {
                newLocations.add(new PropertiesLocation(loc));
            }
            List<PropertiesLocation> current = locations;
            if (!current.isEmpty()) {
                newLocations.addAll(0, current);
            }
            setLocations(newLocations);
        }
    }

    /**
     * A list of locations to load properties. You can use comma to separate multiple locations. This option will
     * override any default locations and only use the locations from this option.
     */
    @Override
    public void setLocation(String location) {
        this.location = location;
        if (location != null) {
            setLocations(location.split(","));
        }
    }

    public String getLocation() {
        return location;
    }

    public String getEncoding() {
        return encoding;
    }

    /**
     * Encoding to use when loading properties file from the file system or classpath.
     * <p/>
     * If no encoding has been set, then the properties files is loaded using ISO-8859-1 encoding (latin-1) as
     * documented by {@link Properties#load(java.io.InputStream)}
     */
    @Override
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public PropertiesParser getPropertiesParser() {
        return propertiesParser;
    }

    /**
     * To use a custom PropertiesParser
     */
    public void setPropertiesParser(PropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    public PropertiesFunctionResolver getPropertiesFunctionResolver() {
        return propertiesFunctionResolver;
    }

    /**
     * To use a custom PropertiesFunctionResolver
     */
    public void setPropertiesFunctionResolver(PropertiesFunctionResolver propertiesFunctionResolver) {
        this.propertiesFunctionResolver = propertiesFunctionResolver;
    }

    public boolean isDefaultFallbackEnabled() {
        return defaultFallbackEnabled;
    }

    /**
     * If false, the component does not attempt to find a default for the key by looking after the colon separator.
     */
    public void setDefaultFallbackEnabled(boolean defaultFallbackEnabled) {
        this.defaultFallbackEnabled = defaultFallbackEnabled;
    }

    public boolean isIgnoreMissingLocation() {
        return ignoreMissingLocation;
    }

    @Override
    public void setIgnoreMissingLocation(boolean ignoreMissingLocation) {
        this.ignoreMissingLocation = ignoreMissingLocation;
    }

    public boolean isIgnoreMissingProperty() {
        return ignoreMissingProperty;
    }

    public void setIgnoreMissingProperty(boolean ignoreMissingProperty) {
        this.ignoreMissingProperty = ignoreMissingProperty;
    }

    public boolean isNestedPlaceholder() {
        return nestedPlaceholder;
    }

    @Override
    public void setNestedPlaceholder(boolean nestedPlaceholder) {
        this.nestedPlaceholder = nestedPlaceholder;
    }

    /**
     * @return a list of properties which will be used before any locations are resolved (can't be null).
     */
    public Properties getInitialProperties() {
        if (initialProperties == null) {
            initialProperties = new OrderedProperties();
        }

        return initialProperties;
    }

    @Override
    public void setInitialProperties(Properties initialProperties) {
        this.initialProperties = initialProperties;
    }

    @Override
    public void addInitialProperty(String key, String value) {
        if (this.initialProperties == null) {
            this.initialProperties = new OrderedProperties();
        }
        this.initialProperties.setProperty(key, value);
    }

    /**
     * @return a list of properties that take precedence and will use first, if a property exists (can't be null).
     */
    public Properties getOverrideProperties() {
        if (overrideProperties == null) {
            overrideProperties = new OrderedProperties();
        }

        return overrideProperties;
    }

    @Override
    public void setOverrideProperties(Properties overrideProperties) {
        this.overrideProperties = overrideProperties;
    }

    @Override
    public void addOverrideProperty(String key, String value) {
        if (this.overrideProperties == null) {
            this.overrideProperties = new OrderedProperties();
        }
        this.overrideProperties.setProperty(key, value);

    }

    @Override
    public void setLocalProperties(Properties localProperties) {
        if (localProperties != null) {
            this.localProperties.push(localProperties);
        } else if (!this.localProperties.isEmpty()) {
            this.localProperties.pop();
        }
    }

    /**
     * Gets a list of properties that are local for the current thread only (ie thread local), or <tt>null</tt> if not
     * currently in use.
     */
    public Properties getLocalProperties() {
        if (localProperties.isEmpty()) {
            return null;
        }
        return localProperties.peek();
    }

    @Override
    public PropertiesFunction getPropertiesFunction(String name) {
        if (name == null) {
            return null;
        }
        return propertiesFunctionResolver.resolvePropertiesFunction(name);
    }

    @Override
    public void addPropertiesFunction(PropertiesFunction function) {
        propertiesFunctionResolver.addPropertiesFunction(function);
    }

    @Override
    public boolean hasPropertiesFunction(String name) {
        return propertiesFunctionResolver.hasFunction(name);
    }

    public int getSystemPropertiesMode() {
        return systemPropertiesMode;
    }

    /**
     * Sets the JVM system property mode (0 = never, 1 = fallback, 2 = override).
     *
     * The default mode (override) is to use system properties if present, and override any existing properties.
     *
     * OS environment variable mode is checked before JVM system property mode.
     *
     * @see #SYSTEM_PROPERTIES_MODE_NEVER
     * @see #SYSTEM_PROPERTIES_MODE_FALLBACK
     * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
     */
    public void setSystemPropertiesMode(int systemPropertiesMode) {
        this.systemPropertiesMode = systemPropertiesMode;
    }

    public int getEnvironmentVariableMode() {
        return environmentVariableMode;
    }

    /**
     * Sets the OS environment variables mode (0 = never, 1 = fallback, 2 = override).
     *
     * The default mode (override) is to use OS environment variables if present, and override any existing properties.
     *
     * OS environment variable mode is checked before JVM system property mode.
     *
     * @see #ENVIRONMENT_VARIABLES_MODE_NEVER
     * @see #ENVIRONMENT_VARIABLES_MODE_FALLBACK
     * @see #ENVIRONMENT_VARIABLES_MODE_OVERRIDE
     */
    public void setEnvironmentVariableMode(int environmentVariableMode) {
        this.environmentVariableMode = environmentVariableMode;
    }

    public boolean isAutoDiscoverPropertiesSources() {
        return autoDiscoverPropertiesSources;
    }

    /**
     * Whether to automatically discovery instances of {@link PropertiesSource} from registry and service factory.
     */
    public void setAutoDiscoverPropertiesSources(boolean autoDiscoverPropertiesSources) {
        this.autoDiscoverPropertiesSources = autoDiscoverPropertiesSources;
    }

    @Override
    public void addPropertiesSource(PropertiesSource propertiesSource) {
        ZwangineContextAware.trySetZwangineContext(propertiesSource, getZwangineContext());
        lock.lock();
        try {
            sources.add(propertiesSource);
            // resort after we add a new source
            sources.sort(OrderedComparator.get());
            if (!isNew()) {
                // if we have already initialized or started then also init the source
                ServiceHelper.initService(propertiesSource);
            }
            if (isStarted()) {
                ServiceHelper.startService(propertiesSource);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public PropertiesSource getPropertiesSource(String name) {
        for (PropertiesSource source : sources) {
            if (name.equals(source.getName())) {
                return source;
            }
        }
        return null;
    }

    @Override
    public List<PropertiesSource> getPropertiesSources() {
        return sources;
    }

    public void addPropertiesLookupListener(PropertiesLookupListener propertiesLookupListener) {
        propertiesLookupListeners.add(propertiesLookupListener);
    }

    /**
     * Remove {@link PropertiesLookupListener}
     */
    public void removePropertiesLookupListener(PropertiesLookupListener propertiesLookupListener) {
        propertiesLookupListeners.remove(propertiesLookupListener);
    }

    /**
     * Gets the {@link PropertiesLookupListener}
     */
    public List<PropertiesLookupListener> getPropertiesLookupListeners() {
        return propertiesLookupListeners;
    }

    @Override
    public boolean reloadProperties(String pattern) {
        if (ObjectHelper.isEmpty(pattern)) {
            pattern = "*";
        }
        LOG.debug("Reloading properties (pattern: {})", pattern);
        boolean answer = false;

        // find sources with this location to reload
        for (PropertiesSource source : sources) {
            if (source instanceof LocationPropertiesSource loc && source instanceof LoadablePropertiesSource loadable) {
                String schemeAndPath = loc.getLocation().getResolver() + ":" + loc.getLocation().getPath();
                String path = loc.getLocation().getPath();
                if (PatternHelper.matchPattern(schemeAndPath, pattern) || PatternHelper.matchPattern(path, pattern)) {
                    loadable.reloadProperties(schemeAndPath);
                    LOG.trace("Reloaded properties: {}", schemeAndPath);
                    answer = true;
                }
            }
        }
        return answer;
    }

    @Override
    public void keepOnlyChangeProperties(Properties properties) {
        Properties loaded = loadProperties();
        for (String key : loaded.stringPropertyNames()) {
            Object v1 = loaded.getProperty(key);
            Object v2 = properties.getProperty(key);
            if (Objects.equals(v1, v2)) {
                properties.remove(key);
            }
        }
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        ObjectHelper.notNull(zwangineContext, "ZwangineContext", this);
        ZwangineContextAware.trySetZwangineContext(propertiesFunctionResolver, zwangineContext);

        if (systemPropertiesMode != SYSTEM_PROPERTIES_MODE_NEVER
                && systemPropertiesMode != SYSTEM_PROPERTIES_MODE_FALLBACK
                && systemPropertiesMode != SYSTEM_PROPERTIES_MODE_OVERRIDE) {
            throw new IllegalArgumentException("Option systemPropertiesMode has invalid value: " + systemPropertiesMode);
        }
        if (environmentVariableMode != ENVIRONMENT_VARIABLES_MODE_NEVER
                && environmentVariableMode != ENVIRONMENT_VARIABLES_MODE_FALLBACK
                && environmentVariableMode != ENVIRONMENT_VARIABLES_MODE_OVERRIDE) {
            throw new IllegalArgumentException("Option environmentVariableMode has invalid value: " + environmentVariableMode);
        }

        // inject the component to the parser
        if (propertiesParser instanceof DefaultPropertiesParser defaultPropertiesParser) {
            defaultPropertiesParser.setPropertiesComponent(this);
        }

        if (isAutoDiscoverPropertiesSources()) {
            // discover any 3rd party properties sources
            try {
                for (PropertiesSource source : getZwangineContext().getRegistry().findByType(PropertiesSource.class)) {
                    addPropertiesSource(source);
                    LOG.debug("PropertiesComponent added custom PropertiesSource (registry): {}", source);
                }

                FactoryFinder factoryFinder = getZwangineContext().getZwangineContextExtension()
                        .getBootstrapFactoryFinder();
                Class<?> type = factoryFinder.findClass("properties-source-factory").orElse(null);
                if (type != null) {
                    Object obj = getZwangineContext().getInjector().newInstance(type, false);
                    if (obj instanceof PropertiesSource ps) {
                        addPropertiesSource(ps);
                        LOG.debug("PropertiesComponent added custom PropertiesSource (factory): {}", ps);
                    } else if (obj != null) {
                        LOG.warn(
                                "PropertiesComponent cannot add custom PropertiesSource as the type is not a {} but: {}",
                                PropertiesSource.class.getName(), type.getName());
                    }
                }
            } catch (Exception e) {
                LOG.debug("Error discovering and using custom PropertiesSource due to {}. This exception is ignored",
                        e.getMessage(), e);
            }
        }

        sources.sort(OrderedComparator.get());
        ServiceHelper.initService(sources, propertiesFunctionResolver, defaultPropertiesLookupListener);
    }

    @Override
    protected void doBuild() throws Exception {
        ServiceHelper.buildService(sources, propertiesFunctionResolver, defaultPropertiesLookupListener);
    }

    @Override
    protected void doStart() throws Exception {
        ServiceHelper.startService(sources, propertiesFunctionResolver, defaultPropertiesLookupListener);
    }

    @Override
    protected void doStop() throws Exception {
        ServiceHelper.stopService(sources, propertiesFunctionResolver, defaultPropertiesLookupListener);
    }

    @Override
    protected void doShutdown() throws Exception {
        ServiceHelper.stopAndShutdownServices(sources, propertiesFunctionResolver, defaultPropertiesLookupListener);
    }

    private void addPropertiesLocationsAsPropertiesSource(PropertiesLocation location, int order) {
        if ("ref".equals(location.getResolver())) {
            addPropertiesSource(new RefPropertiesSource(this, location, order));
        } else if ("file".equals(location.getResolver())) {
            addPropertiesSource(new FilePropertiesSource(this, location, order));
        } else if ("classpath".equals(location.getResolver())) {
            addPropertiesSource(new ClasspathPropertiesSource(this, location, order));
        }
    }

    private List<PropertiesLocation> parseLocations(List<PropertiesLocation> locations) {
        List<PropertiesLocation> answer = new ArrayList<>();

        for (PropertiesLocation propertiesLocation : locations) {
            LOG.trace("Parsing location: {}", propertiesLocation);

            try {
                String path = FilePathResolver.resolvePath(propertiesLocation.getPath());
                LOG.debug("Parsed location: {}", path);
                if (ObjectHelper.isNotEmpty(path)) {
                    answer.add(new PropertiesLocation(
                            propertiesLocation.getResolver(),
                            path,
                            propertiesLocation.isOptional()));
                }
            } catch (IllegalArgumentException e) {
                if (!ignoreMissingLocation && !propertiesLocation.isOptional()) {
                    throw e;
                } else {
                    LOG.debug("Ignored missing location: {}", propertiesLocation);
                }
            }
        }

        // must return a not-null answer
        return answer;
    }

    /**
     * Replaces all the double curly braces that have been escaped by double curly braces.
     *
     * @param  input the content to unescape
     * @return       the provided content with all the escaped double curly braces restored.
     */
    private static String unescape(String input) {
        int index = input.indexOf('\\');
        if (index == -1) {
            return input;
        }
        int length = input.length();
        StringBuilder result = new StringBuilder(length);
        int start = 0;
        do {
            result.append(input, start, index);
            start = index + 1;
            if (index + 2 < length) {
                char next = input.charAt(index + 1);
                char afterNext = input.charAt(index + 2);
                if (next == '{' && afterNext == '{' || next == '}' && afterNext == '}') {
                    // Escaped double curly braces detected, so let's keep the escape character
                    continue;
                }
                result.append('\\');
            } else {
                break;
            }
        } while ((index = input.indexOf('\\', start)) != -1);
        if (start < length) {
            result.append(input, start, length);
        }
        return result.toString();
    }
}
