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
package org.zenithblox.spi;

import org.zenithblox.Ordered;

import java.util.Properties;
import java.util.function.Predicate;

/**
 * A source for properties that can be loaded all at once during initialization, such as loading .properties files.
 * <p/>
 * A source can implement {@link Ordered} to control the ordering of which sources are used by the Zwangine properties
 * component. The source with the highest precedence (the lowest number) will be used first.
 */
public interface LoadablePropertiesSource extends PropertiesSource {

    /**
     * Loads the properties from the source
     *
     * @return the loaded properties
     */
    Properties loadProperties();

    /**
     * Loads the properties from the source filtering them out according to a predicate.
     *
     * @param  filter the predicate used to filter out properties based on the key.
     * @return        the properties loaded.
     */
    Properties loadProperties(Predicate<String> filter);

    /**
     * Re-loads the properties from the file location
     *
     * @param location the location of the properties
     */
    void reloadProperties(String location);
}
