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
package org.zenithblox.vault;

import org.zenithblox.spi.Metadata;

/**
 * Configuration for access to Kubernetes Confimaps
 */
public class KubernetesConfigMapVaultConfiguration extends VaultConfiguration {

    @Metadata
    private boolean refreshEnabled;
    @Metadata
    private String configmaps;

    public boolean isRefreshEnabled() {
        return refreshEnabled;
    }

    /**
     * Whether to automatically reload Zwangine upon configmaps being updated in Kubernetes Cluster.
     */
    public void setRefreshEnabled(boolean refreshEnabled) {
        this.refreshEnabled = refreshEnabled;
    }

    public String getConfigmaps() {
        return configmaps;
    }

    /**
     * Specify the configmap names (or pattern) to check for updates. Multiple configmaps can be separated by comma.
     */
    public void setConfigmaps(String configmaps) {
        this.configmaps = configmaps;
    }
}
