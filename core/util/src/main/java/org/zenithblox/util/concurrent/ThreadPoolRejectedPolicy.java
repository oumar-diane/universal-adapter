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

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Represent the kinds of options for rejection handlers for thread pools.
 * <p/>
 * These options are used for fine-grained thread pool settings, where you want to control which handler to use when a
 * thread pool cannot execute a new task.
 * <p/>
 * Zwangine will by default use <tt>CallerRuns</tt>.
 */
public enum ThreadPoolRejectedPolicy {

    Abort,
    CallerRuns;

    public RejectedExecutionHandler asRejectedExecutionHandler() {
        if (this == Abort) {
            return new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                    if (r instanceof Rejectable rejectable) {
                        rejectable.reject();
                    } else {
                        throw new RejectedExecutionException("Task " + r.toString() + " rejected from " + executor.toString());
                    }
                }

                @Override
                public String toString() {
                    return "Abort";
                }
            };
        } else if (this == CallerRuns) {
            return new ThreadPoolExecutor.CallerRunsPolicy() {
                @Override
                public String toString() {
                    return "CallerRuns";
                }
            };
        }
        throw new IllegalArgumentException("Unknown ThreadPoolRejectedPolicy: " + this);
    }

}
