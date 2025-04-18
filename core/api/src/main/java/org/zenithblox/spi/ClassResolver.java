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

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * A class resolver for loading classes in a loosly coupled manner to cater for different platforms such as standalone,
 * Spring Boot, Quarkus, JBang etc.
 */
public interface ClassResolver {

    /**
     * Adds a custom class loader to use.
     *
     * @param classLoader a custom class loader
     */
    void addClassLoader(ClassLoader classLoader);

    /**
     * Gets the custom class loaders.
     */
    Set<ClassLoader> getClassLoaders();

    /**
     * Gets a custom class loader by its name
     *
     * @param  name the name of the custom classloader
     * @return      the class loader or <tt>null</tt> if not found
     */
    ClassLoader getClassLoader(String name);

    /**
     * Resolves the given class by its name
     *
     * @param  name full qualified name of class
     * @return      the class if resolved, <tt>null</tt> if not found.
     */
    Class<?> resolveClass(String name);

    /**
     * Resolves the given class by its name
     *
     * @param  name full qualified name of class
     * @param  type the expected type of the class
     * @return      the class if resolved, <tt>null</tt> if not found.
     */
    <T> Class<T> resolveClass(String name, Class<T> type);

    /**
     * Resolves the given class by its name
     *
     * @param  name   full qualified name of class
     * @param  loader use the provided class loader
     * @return        the class if resolved, <tt>null</tt> if not found.
     */
    Class<?> resolveClass(String name, ClassLoader loader);

    /**
     * Resolves the given class by its name
     *
     * @param  name   full qualified name of class
     * @param  type   the expected type of the class
     * @param  loader use the provided class loader
     * @return        the class if resolved, <tt>null</tt> if not found.
     */
    <T> Class<T> resolveClass(String name, Class<T> type, ClassLoader loader);

    /**
     * Resolves the given class by its name
     *
     * @param  name                   full qualified name of class
     * @return                        the class if resolved, <tt>null</tt> if not found.
     * @throws ClassNotFoundException is thrown if class not found
     */
    Class<?> resolveMandatoryClass(String name) throws ClassNotFoundException;

    /**
     * Resolves the given class by its name
     *
     * @param  name                   full qualified name of class
     * @param  type                   the expected type of the class
     * @return                        the class if resolved, <tt>null</tt> if not found.
     * @throws ClassNotFoundException is thrown if class not found
     */
    <T> Class<T> resolveMandatoryClass(String name, Class<T> type) throws ClassNotFoundException;

    /**
     * Resolves the given class by its name
     *
     * @param  name                   full qualified name of class
     * @param  loader                 use the provided class loader
     * @return                        the class if resolved, <tt>null</tt> if not found.
     * @throws ClassNotFoundException is thrown if class not found
     */
    Class<?> resolveMandatoryClass(String name, ClassLoader loader) throws ClassNotFoundException;

    /**
     * Resolves the given class by its name
     *
     * @param  name                   full qualified name of class
     * @param  type                   the expected type of the class
     * @param  loader                 use the provided class loader
     * @return                        the class if resolved, <tt>null</tt> if not found.
     * @throws ClassNotFoundException is thrown if class not found
     */
    <T> Class<T> resolveMandatoryClass(String name, Class<T> type, ClassLoader loader) throws ClassNotFoundException;

    /**
     * Loads the given resource as a stream
     *
     * @param  uri the uri of the resource
     * @return     as a stream
     */
    InputStream loadResourceAsStream(String uri);

    /**
     * Loads the given resource as a URL
     *
     * @param  uri the uri of the resource
     * @return     as a URL
     */
    URL loadResourceAsURL(String uri);

    /**
     * Loads the given resources as a URL from the current bundle/classloader
     *
     * @param  uri the uri of the resource
     * @return     the URLs found on the classpath
     */
    Enumeration<URL> loadResourcesAsURL(String uri);

    /**
     * Loads the given resources as a URL from all bundles/classloaders
     *
     * @param  uri the uri of the resource
     * @return     the URLs found on the classpath
     */
    Enumeration<URL> loadAllResourcesAsURL(String uri);
}
