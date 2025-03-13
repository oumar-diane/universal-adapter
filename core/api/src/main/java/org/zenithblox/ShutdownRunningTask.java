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
 * Represents the kind of options for what to do with the current task when shutting down.
 * <p/>
 * By default the current task is allowed to complete. However some consumers such as {@link BatchConsumer} have pending
 * tasks which you can configure the consumer to complete as well.
 * <ul>
 * <li>CompleteCurrentTaskOnly - Is the <b>default</b> behavior where a workflow consumer will be shutdown as fast as
 * possible. Allowing it to complete its current task, but not to pickup pending tasks (if any).</li>
 * <li>CompleteAllTasks - Allows a workflow consumer to continue to complete all pending tasks (if any).</li>
 *
 * </ul>
 * <b>Notice:</b> Most consumers only have a single task, but {@link org.zenithblox.BatchConsumer} can have many tasks
 * and thus this option mostly applies to this kind of consumer.
 */
public enum ShutdownRunningTask {

    CompleteCurrentTaskOnly,
    CompleteAllTasks

}
