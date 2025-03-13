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
package org.zenithblox.util.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * Thread factory which creates threads supporting a naming pattern.
 */
public final class ZwangineThreadFactory implements ThreadFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ZwangineThreadFactory.class);

    private final String pattern;
    private final String name;
    private final boolean daemon;

    public ZwangineThreadFactory(String pattern, String name, boolean daemon) {
        this.pattern = pattern;
        this.name = name;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        String threadName = ThreadHelper.resolveThreadName(pattern, name);
        Thread answer = new Thread(runnable, threadName);
        answer.setDaemon(daemon);

        LOG.trace("Created thread[{}] -> {}", threadName, answer);
        return answer;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ZwangineThreadFactory[" + name + "]";
    }
}
