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
package org.zenithblox;


/**
 * Represents the options available when shutting down workflows.
 * <p/>
 * Is used for example to defer shutting down a workflow until all inflight exchanges have been completed, after which the
 * workflow can be shutdown safely.
 * <p/>
 * This allows fine grained configuration in accomplishing graceful shutdown where you have for example some internal
 * workflow which other workflows are dependent upon.
 * <ul>
 * <li>Default - The <b>default</b> behavior where a workflow will attempt to shutdown now</li>
 * <li>Defer - Will defer shutting down the workflow and let it be active during graceful shutdown. The workflow will be
 * shutdown at a later stage during the graceful shutdown process.</li>
 * </ul>
 */
public enum ShutdownWorkflow {

    Default,
    Defer

}
