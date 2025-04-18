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

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ManagementNameStrategy;
import org.zenithblox.util.StringHelper;

import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * Default implementation of {@link ManagementNameStrategy}
 * <p/>
 * This implementation will by default use a name pattern as <tt>#name#</tt> and in case of a clash, then the pattern
 * will fallback to be using the counter as <tt>#name#-#counter#</tt>.
 */
public class DefaultManagementNameStrategy implements ManagementNameStrategy {

    private static final Pattern INVALID_PATTERN = Pattern.compile(".*#\\w+#.*");
    private static final AtomicLong NAME_COUNTER = new AtomicLong();

    private final ZwangineContext zwangineContext;
    private final String defaultPattern;
    private final String nextPattern;
    private String name;
    private String namePattern;

    public DefaultManagementNameStrategy(ZwangineContext zwangineContext) {
        this(zwangineContext, null, "#name#-#counter#");
    }

    public DefaultManagementNameStrategy(ZwangineContext zwangineContext, String defaultPattern, String nextPattern) {
        this.zwangineContext = zwangineContext;
        this.defaultPattern = defaultPattern;
        this.nextPattern = nextPattern;
    }

    @Override
    public String getNamePattern() {
        return namePattern;
    }

    @Override
    public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
    }

    @Override
    public String getName() {
        if (name == null) {
            String pattern = getNamePattern();
            if (pattern == null) {
                // fallback and use the default pattern which is the same name as the ZwangineContext has been given
                pattern = defaultPattern != null
                        ? defaultPattern : zwangineContext.getManagementStrategy().getManagementAgent().getManagementNamePattern();
            }
            name = resolveManagementName(pattern, zwangineContext.getName(), true);
        }
        return name;
    }

    @Override
    public String getNextName() {
        if (isFixedName()) {
            // use the fixed name
            return getName();
        } else {
            // or resolve a new name
            String pattern = getNamePattern();
            if (pattern == null) {
                // use a pattern that has a counter to ensure unique next name
                pattern = nextPattern;
            }
            return resolveManagementName(pattern, zwangineContext.getName(), true);
        }
    }

    @Override
    public boolean isFixedName() {
        // the name will be fixed unless there is a counter token
        String pattern = getNamePattern();
        if (pattern == null) {
            // we are not fixed by default
            return false;
        }
        return !pattern.contains("#counter#");
    }

    /**
     * Creates a new management name with the given pattern
     *
     * @param  pattern                  the pattern
     * @param  name                     the name
     * @return                          the management name
     * @throws IllegalArgumentException if the pattern or name is invalid or empty
     */
    @Override
    public String resolveManagementName(String pattern, String name, boolean invalidCheck) {
        StringHelper.notEmpty(pattern, "pattern");
        StringHelper.notEmpty(name, "name");

        // replace tokens
        String answer = pattern;
        if (pattern.contains("#counter#")) {
            // only increment the counter on-demand
            answer = pattern.replace("#counter#", Long.toString(nextNameCounter()));
        }
        // zwangineId and name is the same tokens
        answer = answer.replace("#zwangineId#", name);
        answer = answer.replace("#name#", name);

        // allow custom name resolution as well. For example with zwangine-core-osgi we have a custom
        // name strategy that supports OSGI specific tokens such as #bundleId# etc.
        answer = customResolveManagementName(pattern, answer);

        // are there any #word# combos left, if so they should be considered invalid tokens
        if (invalidCheck && INVALID_PATTERN.matcher(answer).matches()) {
            throw new IllegalArgumentException("Pattern is invalid: " + pattern);
        }

        return answer;
    }

    /**
     * Strategy to do any custom resolution of the name
     *
     * @param  pattern the pattern
     * @param  answer  the current answer, which may have custom patterns still to be resolved
     * @return         the resolved name
     */
    protected String customResolveManagementName(String pattern, String answer) {
        return answer;
    }

    private static long nextNameCounter() {
        // we want to be 1-based, so increment first
        return NAME_COUNTER.incrementAndGet();
    }

    /**
     * To reset the counter, should only be used for testing purposes.
     *
     * @param value the counter value
     */
    public static void setCounter(int value) {
        NAME_COUNTER.set(value);
    }

}
