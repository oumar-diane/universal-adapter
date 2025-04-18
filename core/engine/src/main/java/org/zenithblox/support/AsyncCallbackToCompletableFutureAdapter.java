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

import org.zenithblox.AsyncCallback;

import java.util.concurrent.CompletableFuture;

/**
 * AsyncCallback that provides a {@link CompletableFuture} completed when async action is done
 */
public class AsyncCallbackToCompletableFutureAdapter<T> implements AsyncCallback {
    private final CompletableFuture<T> future;
    private volatile T result;

    public AsyncCallbackToCompletableFutureAdapter() {
        this(null);
    }

    public AsyncCallbackToCompletableFutureAdapter(T result) {
        this(null, result);
    }

    public AsyncCallbackToCompletableFutureAdapter(CompletableFuture<T> future, T result) {
        this.future = future != null ? future : new CompletableFuture<>();
        this.result = result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    @Override
    public void done(boolean doneSync) {
        future.complete(result);
    }
}
