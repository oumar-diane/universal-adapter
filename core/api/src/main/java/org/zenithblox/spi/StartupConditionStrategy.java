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

import org.zenithblox.StaticService;

import java.util.List;

/**
 * Strategy for performing checks on startup that can validate whether Zwangine can be started, or wait for some conditions
 * to be satisfied, before Zwangine can continue to startup.
 *
 * @see StartupCondition
 */
public interface StartupConditionStrategy extends StaticService {

    /**
     * To enable or disable startup checks
     */
    boolean isEnabled();

    /**
     * To enable or disable startup checks
     */
    void setEnabled(boolean enabled);

    /**
     * Interval in millis between checking startup conditions
     */
    void setInterval(int interval);

    /**
     * Interval in millis between checking startup conditions
     */
    int getInterval();

    /**
     * Total timeout in millis when performing startup checks.
     */
    void setTimeout(int timeout);

    /**
     * Total timeout in millis when performing startup checks.
     */
    int getTimeout();

    /**
     * What action, to do on timeout.
     *
     * fail = do not startup, and throw an exception causing zwangine to fail stop = do not startup, and stop zwangine ignore
     * = log a WARN and continue to startup
     */
    String getOnTimeout();

    /**
     * What action, to do on timeout.
     *
     * fail = do not startup, and throw an exception causing zwangine to fail stop = do not startup, and stop zwangine ignore
     * = log a WARN and continue to startup
     */
    void setOnTimeout(String onTimeout);

    /**
     * Adds a custom {@link StartupCondition} check to be performed.
     */
    void addStartupCondition(StartupCondition startupCondition);

    /**
     * A list of custom class names (FQN) for {@link StartupCondition} classes. Multiple classes can be separated by
     * comma.
     */
    void addStartupConditions(String classNames);

    /**
     * Lists all the {@link StartupCondition}.
     */
    List<StartupCondition> getStartupConditions();

    /**
     * Checks all {@link StartupCondition} whether they are satisfied. If everything is okay, then this method returns,
     * otherwise if a condition is failing then an exception is thrown.
     */
    void checkStartupConditions() throws Exception;

}
