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

import org.zenithblox.spi.ZwangineContextNameStrategy;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A default name strategy which auto assigns a name using a prefix-counter pattern.
 */
public class DefaultZwangineContextNameStrategy implements ZwangineContextNameStrategy {

    private static final AtomicInteger CONTEXT_COUNTER = new AtomicInteger();
    private final String prefix;
    private String name;

    public DefaultZwangineContextNameStrategy() {
        this("zwangine");
    }

    public DefaultZwangineContextNameStrategy(String prefix) {
        this.prefix = prefix;
        this.name = getNextName();
    }

    @Override
    public String getName() {
        if (name == null) {
            name = getNextName();
        }
        return name;
    }

    @Override
    public String getNextName() {
        return prefix + "-" + getNextCounter();
    }

    @Override
    public boolean isFixedName() {
        return false;
    }

    public static int getNextCounter() {
        // we want to start counting from 1, so increment first
        return CONTEXT_COUNTER.incrementAndGet();
    }

    /**
     * To reset the counter, should only be used for testing purposes.
     *
     * @param value the counter value
     */
    public static void setCounter(int value) {
        CONTEXT_COUNTER.set(value);
    }

}
