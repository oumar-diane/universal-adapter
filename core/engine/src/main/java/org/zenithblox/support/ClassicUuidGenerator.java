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

import org.zenithblox.spi.UuidGenerator;
import org.zenithblox.util.InetAddressUtil;
import org.zenithblox.util.ObjectHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The classic (Zwangine 2.x) {@link UuidGenerator} optimized for Zwangine usage.
 */
public class ClassicUuidGenerator implements UuidGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(ClassicUuidGenerator.class);
    private static final Lock LOCK = new ReentrantLock();
    private static final String UNIQUE_STUB;
    private static int instanceCount;
    private static String hostName;
    private String seed;
    // must use AtomicLong to ensure atomic get and update operation that is thread-safe
    private final AtomicLong sequence = new AtomicLong(1);
    private final int length;

    static {
        String stub = "";
        boolean canAccessSystemProps = true;
        try {
            SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPropertiesAccess();
            }
        } catch (SecurityException se) {
            canAccessSystemProps = false;
        }

        if (canAccessSystemProps) {
            try {
                if (hostName == null) {
                    hostName = InetAddressUtil.getLocalHostName();
                }
                stub = "-" + System.currentTimeMillis() + "-";
            } catch (Exception e) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace("Cannot generate unique stub by using DNS", e);
                } else {
                    LOG.warn("Cannot generate unique stub by using DNS due {}. This exception is ignored.", e.getMessage());
                }
            }
        }

        // fallback to use localhost
        if (hostName == null) {
            hostName = "localhost";
        }
        hostName = sanitizeHostName(hostName);

        if (ObjectHelper.isEmpty(stub)) {
            stub = "-1-" + System.currentTimeMillis() + "-";
        }
        UNIQUE_STUB = stub;
    }

    public ClassicUuidGenerator(String prefix) {
        LOCK.lock();
        try {
            this.seed = prefix + UNIQUE_STUB + (instanceCount++) + "-";
            // let the ID be friendly for URL and file systems
            this.seed = generateSanitizedId(this.seed);
            this.length = seed.length() + (Long.toString(Long.MAX_VALUE)).length();
        } finally {
            LOCK.unlock();
        }
    }

    public ClassicUuidGenerator() {
        this("ID-" + hostName);
    }

    /**
     * As we have to find the hostname as a side-affect of generating a unique stub, we allow it's easy retrieval here
     *
     * @return the local host name
     */
    public static String getHostName() {
        return hostName;
    }

    public static String sanitizeHostName(String hostName) {
        boolean changed = false;

        StringBuilder sb = new StringBuilder();
        for (char ch : hostName.toCharArray()) {
            // only include ASCII chars
            if (ch < 127) {
                sb.append(ch);
            } else {
                changed = true;
            }
        }

        if (changed) {
            String newHost = sb.toString();
            LOG.info("Sanitized hostname from: {} to: {}", hostName, newHost);
            return newHost;
        } else {
            return hostName;
        }
    }

    @Override
    public String generateUuid() {
        StringBuilder sb = new StringBuilder(length);
        sb.append(seed);
        sb.append(sequence.getAndIncrement());
        return sb.toString();
    }

    /**
     * Generate a unique ID - that is friendly for a URL or file system
     *
     * @return a unique id
     */
    public String generateSanitizedId() {
        return generateSanitizedId(generateUuid());
    }

    /**
     * Ensures that the id is friendly for a URL or file system
     *
     * @param  id the unique id
     * @return    the id as file friendly id
     */
    public static String generateSanitizedId(String id) {
        id = id.replace(':', '-');
        id = id.replace('_', '-');
        id = id.replace('.', '-');
        id = id.replace('/', '-');
        return id;
    }
}
