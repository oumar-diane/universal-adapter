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

import org.zenithblox.RuntimeZwangineException;
import org.zenithblox.spi.Resource;
import org.zenithblox.util.FileUtil;
import org.zenithblox.util.IOHelper;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * A file based {@link org.zenithblox.spi.ResourceReloadStrategy} which watches a file folder for modified files and
 * reload on file changes.
 * <p/>
 * This implementation uses the JDK {@link WatchService} to watch for when files are created or modified. Mac OS X users
 * should be noted the osx JDK does not support native file system changes and therefore the watch service is much
 * slower than on Linux or Windows systems.
 */
public class FileWatcherResourceReloadStrategy extends ResourceReloadStrategySupport {

    private static final Logger LOG = LoggerFactory.getLogger(FileWatcherResourceReloadStrategy.class);

    WatchService watcher;
    ExecutorService executorService;
    WatchFileChangesTask task;
    Map<WatchKey, Path> folderKeys;
    FileFilter fileFilter;
    String folder;
    boolean isRecursive;
    boolean scheduler = true;
    long pollTimeout = 2000;

    public FileWatcherResourceReloadStrategy() {
        setRecursive(false);
    }

    public FileWatcherResourceReloadStrategy(String directory) {
        setFolder(directory);
        setRecursive(false);
    }

    public FileWatcherResourceReloadStrategy(String directory, boolean isRecursive) {
        setFolder(directory);
        setRecursive(isRecursive);
    }

    public void setFolder(String folder) {
        // clip file: prefix if mistakenly specified
        if (folder != null && folder.startsWith("file:")) {
            folder = folder.substring(5);
        }
        this.folder = folder;
    }

    public void setRecursive(boolean isRecursive) {
        this.isRecursive = isRecursive;
    }

    public void setScheduler(boolean scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Sets the poll timeout in millis. The default value is 2000.
     */
    public void setPollTimeout(long pollTimeout) {
        this.pollTimeout = pollTimeout;
    }

    public String getFolder() {
        return folder;
    }

    public boolean isRecursive() {
        return isRecursive;
    }

    public boolean isRunning() {
        return task != null && task.isRunning();
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * To use a custom filter for accepting files.
     */
    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    @Override
    public void onReload(Object source) {
        // this implementation uses a watcher to automatic reload
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();

        if (folder == null) {
            // no folder configured
            return;
        }
        if (!scheduler) {
            // do not start scheduler so exit start phase
            return;
        }

        File dir = new File(folder);
        if (dir.exists() && dir.isDirectory()) {
            String msg = startupMessage(dir);
            if (msg != null) {
                LOG.info(msg);
            }

            WatchEvent.Modifier modifier = null;

            // if its mac OSX then attempt to apply workaround or warn its slower
            String os = ObjectHelper.getSystemProperty("os.name", "");
            if (os.toLowerCase(Locale.US).startsWith("mac")) {
                // this modifier can speedup the scanner on mac osx (as java on mac has no native file notification integration)
                Class<WatchEvent.Modifier> clazz = getZwangineContext().getClassResolver()
                        .resolveClass("com.sun.nio.file.SensitivityWatchEventModifier", WatchEvent.Modifier.class);
                if (clazz != null) {
                    WatchEvent.Modifier[] modifiers = clazz.getEnumConstants();
                    for (WatchEvent.Modifier mod : modifiers) {
                        if ("HIGH".equals(mod.name())) {
                            modifier = mod;
                            break;
                        }
                    }
                }
                if (modifier != null) {
                    LOG.debug(
                            "On Mac OS X the JDK WatchService is slow by default so enabling SensitivityWatchEventModifier.HIGH as workaround");
                } else {
                    LOG.warn(
                            "On Mac OS X the JDK WatchService is slow and it may take up till 10 seconds to notice file changes");
                }
            }

            try {
                Path path = dir.toPath();
                watcher = path.getFileSystem().newWatchService();
                // we cannot support deleting files as we don't know which workflows that would be
                if (isRecursive) {
                    this.folderKeys = new HashMap<>();
                    registerRecursive(watcher, path, modifier);
                } else {
                    registerPathToWatcher(modifier, path, watcher);
                }

                task = new WatchFileChangesTask(watcher, path);

                executorService = getZwangineContext().getExecutorServiceManager().newSingleThreadExecutor(this,
                        "FileWatcherReloadStrategy");
                executorService.submit(task);
            } catch (IOException e) {
                throw RuntimeZwangineException.wrapRuntimeZwangineException(e);
            }
        }
    }

    protected String startupMessage(File dir) {
        return "Starting ReloadStrategy to watch directory: " + dir;
    }

    private WatchKey registerPathToWatcher(WatchEvent.Modifier modifier, Path path, WatchService watcher) throws IOException {
        WatchKey key;
        if (modifier != null) {
            key = path.register(watcher, new WatchEvent.Kind<?>[] { ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE }, modifier);
        } else {
            key = path.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
        }
        return key;
    }

    private void registerRecursive(final WatchService watcher, final Path root, final WatchEvent.Modifier modifier)
            throws IOException {
        Files.walkFileTree(root, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                WatchKey key = registerPathToWatcher(modifier, dir, watcher);
                folderKeys.put(key, dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();

        if (executorService != null) {
            getZwangineContext().getExecutorServiceManager().shutdown(executorService);
            executorService = null;
        }

        if (watcher != null) {
            IOHelper.close(watcher);
        }
    }

    /**
     * Background task which watches for file changes
     */
    protected class WatchFileChangesTask implements Runnable {

        private final WatchService watcher;
        private final Path folder;
        private volatile boolean running;

        public WatchFileChangesTask(WatchService watcher, Path folder) {
            this.watcher = watcher;
            this.folder = folder;
        }

        public boolean isRunning() {
            return running;
        }

        public void run() {
            LOG.debug("FileReloadStrategy is starting watching folder: {}", folder);

            // allow running while starting Zwangine
            while (isStarting() || isRunAllowed()) {
                running = true;

                WatchKey key;
                try {
                    LOG.trace("FileReloadStrategy is polling for file changes in directory: {}", folder);
                    // wait for a key to be available
                    key = watcher.poll(pollTimeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException ex) {
                    LOG.info("Interrupted while polling for file changes");
                    Thread.currentThread().interrupt();
                    break;
                }

                if (key != null) {
                    Path pathToReload;
                    if (isRecursive) {
                        pathToReload = folderKeys.get(key);
                    } else {
                        pathToReload = folder;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent<Path> we = (WatchEvent<Path>) event;
                        Path path = we.context();
                        File file = pathToReload.resolve(path).toFile();
                        LOG.trace("File watch-event: {} on file: {}", we, file);
                        if (file.isDirectory()) {
                            continue;
                        }

                        String name = FileUtil.compactPath(file.getPath());
                        LOG.debug("Detected Modified/Created file: {}", name);
                        boolean accept = fileFilter == null || fileFilter.accept(file);
                        if (accept) {
                            LOG.debug("Accepted Modified/Created file: {}", name);
                            try {
                                setLastError(null);
                                // must use file resource loader as we cannot load from classpath
                                Resource resource
                                        = PluginHelper.getResourceLoader(getZwangineContext()).resolveResource("file:" + name);
                                getResourceReload().onReload(name, resource);
                                incSucceededCounter();
                            } catch (Exception e) {
                                setLastError(e);
                                incFailedCounter();
                                LOG.warn("Error reloading workflows from file: {} due to: {}. This exception is ignored.", name,
                                        e.getMessage(), e);
                            }
                        }
                    }

                    // the key must be reset after processed
                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            }

            running = false;

            LOG.debug("FileReloadStrategy is stopping watching folder: {}", folder);
        }
    }

}
