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

import java.util.ArrayList;
import java.util.List;

/**
 * Strategy for configurers.
 */
public abstract class ConfigurerStrategy {

    private static final List<Runnable> BOOTSTRAP_CLEARERS = new ArrayList<>();

    public static void addBootstrapConfigurerClearer(Runnable strategy) {
        BOOTSTRAP_CLEARERS.add(strategy);
    }

    /**
     * Clears the bootstrap configurers map. Clearing this map allows Zwangine to reduce memory footprint.
     */
    public static void clearBootstrapConfigurers() {
        for (Runnable run : BOOTSTRAP_CLEARERS) {
            run.run();
        }
        BOOTSTRAP_CLEARERS.clear();
    }

}
