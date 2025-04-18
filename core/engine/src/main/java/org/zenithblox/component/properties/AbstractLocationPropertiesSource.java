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

import org.zenithblox.spi.LoadablePropertiesSource;
import org.zenithblox.support.ResourceHelper;
import org.zenithblox.support.service.ServiceSupport;
import org.zenithblox.util.OrderedProperties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;

/**
 * Base class for {@link LoadablePropertiesSource} which can load properties from a source such as classpath or file
 * system.
 */
public abstract class AbstractLocationPropertiesSource extends ServiceSupport
        implements LoadablePropertiesSource, LocationPropertiesSource {

    private final Properties properties = new OrderedProperties();
    private final PropertiesComponent propertiesComponent;
    private final PropertiesLocation location;

    protected AbstractLocationPropertiesSource(PropertiesComponent propertiesComponent, PropertiesLocation location) {
        this.propertiesComponent = propertiesComponent;
        this.location = location;
    }

    public abstract Properties loadPropertiesFromLocation(PropertiesComponent propertiesComponent, PropertiesLocation location);

    @Override
    public PropertiesLocation getLocation() {
        return location;
    }

    @Override
    public Properties loadProperties() {
        return properties;
    }

    @Override
    public Properties loadProperties(Predicate<String> filter) {
        Properties answer = new OrderedProperties();

        for (String name : properties.stringPropertyNames()) {
            if (filter.test(name)) {
                answer.put(name, properties.get(name));
            }
        }

        return answer;
    }

    @Override
    public void reloadProperties(String location) {
        String resolver = ResourceHelper.getScheme(location);
        if (resolver != null) {
            location = location.substring(resolver.length());
        }
        PropertiesLocation loc = new PropertiesLocation(resolver, location);
        Properties prop = loadPropertiesFromLocation(propertiesComponent, loc);
        if (prop != null) {
            prop = prepareLoadedProperties(prop);
            // need to clear in case some properties was removed
            properties.clear();
            properties.putAll(prop);
        }
    }

    @Override
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Sets a property
     *
     * @param key   the key
     * @param value the value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    @Override
    protected void doInit() throws Exception {
        super.doInit();

        Properties prop = loadPropertiesFromLocation(propertiesComponent, location);
        if (prop != null) {
            prop = prepareLoadedProperties(prop);
            properties.putAll(prop);
        }
    }

    @Override
    protected void doShutdown() throws Exception {
        properties.clear();
    }

    /**
     * Strategy to prepare loaded properties before being used by Zwangine.
     * <p/>
     * This implementation will ensure values are trimmed, as loading properties from a file with values having trailing
     * spaces is not automatically trimmed by the Properties API from the JDK.
     *
     * @param  properties the properties
     * @return            the prepared properties
     */
    protected static Properties prepareLoadedProperties(Properties properties) {
        Properties answer = new OrderedProperties();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String s) {
                // trim any trailing spaces which can be a problem when loading from
                // a properties file, note that java.util.Properties do already this
                // for any potential leading spaces so there's nothing to do there
                value = trimTrailingWhitespaces(s);
            }
            answer.put(key, value);
        }
        return answer;
    }

    private static String trimTrailingWhitespaces(String s) {
        int endIndex = s.length();
        for (int index = s.length() - 1; index >= 0; index--) {
            if (s.charAt(index) == ' ') {
                endIndex = index;
            } else {
                break;
            }
        }
        return s.substring(0, endIndex);
    }

}
