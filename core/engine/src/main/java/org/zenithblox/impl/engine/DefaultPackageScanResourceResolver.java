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
package org.zenithblox.impl.engine;

import org.zenithblox.ZwangineContextAware;
import org.zenithblox.NonManagedService;
import org.zenithblox.spi.PackageScanResourceResolver;
import org.zenithblox.spi.Resource;
import org.zenithblox.spi.ResourceLoader;
import org.zenithblox.support.PluginHelper;
import org.zenithblox.support.ResourceHelper;
import org.zenithblox.util.AntPathMatcher;
import org.zenithblox.util.IOHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Default implement of {@link org.zenithblox.spi.PackageScanResourceResolver}
 */
public class DefaultPackageScanResourceResolver extends BasePackageScanResolver
        implements PackageScanResourceResolver, NonManagedService, ZwangineContextAware {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultPackageScanResourceResolver.class);

    private static final AntPathMatcher PATH_MATCHER = AntPathMatcher.INSTANCE;

    @Override
    public Collection<Resource> findResources(String location) throws Exception {
        Set<Resource> answer = new HashSet<>();
        doFindResources(location, answer);

        return answer;
    }

    protected void doFindResources(String location, Set<Resource> resources)
            throws Exception {
        // if its a pattern then we need to scan its root path and find
        // all matching resources using the sub pattern
        if (PATH_MATCHER.isPattern(location)) {
            String root = PATH_MATCHER.determineRootDir(location);
            String subPattern = location.substring(root.length());

            String scheme = ResourceHelper.getScheme(location);
            if ("file:".equals(scheme)) {
                // file based scanning
                root = root.substring(scheme.length());
                findInFileSystem(new File(root), resources, subPattern);
            } else {
                if ("classpath:".equals(scheme)) {
                    root = root.substring(scheme.length());
                }
                // assume classpath based scan from root path and find all resources
                findInClasspath(root, resources, subPattern);
            }
        } else {
            final ResourceLoader loader = PluginHelper.getResourceLoader(getZwangineContext());

            // its a single resource so load it directly
            resources.add(loader.resolveResource(location));
        }
    }

    protected void findInFileSystem(
            File dir,
            Set<Resource> resources,
            String subPattern)
            throws Exception {

        final ResourceLoader loader = PluginHelper.getResourceLoader(getZwangineContext());

        for (Path path : ResourceHelper.findInFileSystem(dir.toPath(), subPattern)) {
            resources.add(loader.resolveResource("file:" + path.toString()));
        }
    }

    protected void findInClasspath(
            String packageName,
            Set<Resource> resources,
            String subPattern) {

        // special for root package
        if (".".equals(packageName)) {
            packageName = "";
        } else {
            packageName = packageName.replace('.', '/');
        }

        // If the URL is a jar, the URLClassloader.getResources() seems to require a trailing slash.
        // The trailing slash is harmless for other URLs
        if (!packageName.isEmpty() && !packageName.endsWith("/")) {
            packageName = packageName + "/";
        }

        Set<ClassLoader> set = getClassLoaders();

        for (ClassLoader classLoader : set) {
            doFind(packageName, classLoader, resources, subPattern);
        }
    }

    protected void doFind(
            String packageName,
            ClassLoader classLoader,
            Set<Resource> resources,
            String subPattern) {

        Enumeration<URL> urls = getUrls(packageName, classLoader);
        if (urls == null) {
            return;
        }

        while (urls.hasMoreElements()) {
            URL url = null;
            try {
                url = urls.nextElement();
                LOG.trace("URL from classloader: {}", url);

                url = customResourceLocator(url);

                String urlPath = parseUrlPath(url);
                if (urlPath == null) {
                    continue;
                }

                LOG.trace("Scanning for resources in: {} matching pattern: {}", urlPath, subPattern);

                File file = new File(urlPath);
                if (file.isDirectory()) {
                    LOG.trace("Loading from directory using file: {}", file);
                    loadImplementationsInDirectory(subPattern, packageName, file, resources);
                } else {
                    InputStream stream;
                    if (urlPath.startsWith("http:") || urlPath.startsWith("https:")
                            || urlPath.startsWith("sonicfs:")
                            || isAcceptableScheme(urlPath)) {
                        // load resources using http/https, sonicfs and other acceptable scheme
                        // sonic ESB requires to be loaded using a regular URLConnection
                        LOG.trace("Loading from jar using url: {}", urlPath);
                        URL urlStream = new URL(urlPath);
                        URLConnection con = urlStream.openConnection();
                        // disable cache mainly to avoid jar file locking on Windows
                        con.setUseCaches(false);
                        stream = con.getInputStream();
                    } else {
                        LOG.trace("Loading from jar using file: {}", file);
                        stream = new FileInputStream(file);
                    }

                    loadImplementationsInJar(packageName, subPattern, stream, urlPath, resources);
                }
            } catch (IOException e) {
                // use debug logging to avoid being to noisy in logs
                LOG.debug("Cannot read entries in url: {}", url, e);
            }
        }
    }

    /**
     * Finds matching classes within a jar files that contains a folder structure matching the package structure. If the
     * File is not a JarFile or does not exist a warning will be logged, but no error will be raised.
     *
     * @param packageName the root package name
     * @param subPattern  optional pattern to use for matching resource names
     * @param stream      the inputstream of the jar file to be examined for classes
     * @param urlPath     the url of the jar file to be examined for classes
     * @param resources   the list to add loaded resources
     */
    protected void loadImplementationsInJar(
            String packageName,
            String subPattern,
            InputStream stream,
            String urlPath,
            Set<Resource> resources) {

        List<String> entries = doLoadImplementationsInJar(packageName, stream, urlPath);
        for (String name : entries) {
            String shortName = name.substring(packageName.length());
            boolean match = PATH_MATCHER.match(subPattern, shortName);
            LOG.debug("Found resource: {} matching pattern: {} -> {}", shortName, subPattern, match);
            if (match) {
                final ResourceLoader loader = PluginHelper.getResourceLoader(getZwangineContext());

                resources.add(loader.resolveResource(name));
            }
        }
    }

    protected List<String> doLoadImplementationsInJar(
            String packageName,
            InputStream stream,
            String urlPath) {

        List<String> entries = new ArrayList<>();

        JarInputStream jarStream = null;
        try {
            jarStream = new JarInputStream(stream);

            JarEntry entry;
            while ((entry = jarStream.getNextJarEntry()) != null) {
                final String name = entry.getName().trim();
                if (!entry.isDirectory() && !name.endsWith(".class")) {
                    // name is FQN so it must start with package name
                    if (name.startsWith(packageName)) {
                        entries.add(name);
                    }
                }

            }
        } catch (IOException ioe) {
            LOG.warn("Cannot search jar file '{} due to an IOException: {}", urlPath, ioe.getMessage(), ioe);
        } finally {
            IOHelper.close(jarStream, urlPath, LOG);
        }

        return entries;
    }

    /**
     * Finds matches in a physical directory on a filesystem. Examines all files within a directory - if the File object
     * is not a directory, and ends with <i>.class</i> the file is loaded and tested to see if it is acceptable
     * according to the Test. Operates recursively to find classes within a folder structure matching the package
     * structure.
     *
     * @param parent   the package name up to this directory in the package hierarchy. E.g. if /classes is in the
     *                 classpath and we wish to examine files in /classes/org/zentihblox then the values of <i>parent</i>
     *                 would be <i>org/zentihblox</i>
     * @param location a File object representing a directory
     */
    private void loadImplementationsInDirectory(
            String subPattern,
            String parent,
            File location,
            Set<Resource> resources) {
        File[] files = location.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        StringBuilder builder;
        for (File file : files) {
            builder = new StringBuilder(100);
            String name = file.getName();
            name = name.trim();
            builder.append(parent).append("/").append(name);
            String packageOrClass = parent == null ? name : builder.toString();

            if (file.isDirectory()) {
                loadImplementationsInDirectory(subPattern, packageOrClass, file, resources);
            } else if (file.isFile() && file.exists() && !name.endsWith(".class")) {
                boolean match = PATH_MATCHER.match(subPattern, name);
                LOG.debug("Found resource: {} matching pattern: {} -> {}", name, subPattern, match);
                if (match) {
                    final ResourceLoader loader = PluginHelper.getResourceLoader(getZwangineContext());

                    resources.add(loader.resolveResource("file:" + file.getPath()));
                }
            }
        }
    }

}
