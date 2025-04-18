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
 * Represents the history of a Zwangine {@link Message} how it was routed by the Zwangine routing engine.
 */
public interface MessageHistory {

    /**
     * Gets the workflow id at the point of this history.
     */
    String getWorkflowId();

    /**
     * Gets the node at the point of this history.
     */
    NamedNode getNode();

    /**
     * Gets the point in time the message history was created
     */
    long getTime();

    /**
     * Gets the elapsed time in millis processing the node took (this is 0 until the node processing is done)
     */
    long getElapsed();

    /**
     * The elapsed time since created.
     */
    default long getElapsedSinceCreated() {
        return System.nanoTime() - getTime();
    }

    /**
     * Used for signalling that processing of the node is done.
     */
    void nodeProcessingDone();

    /**
     * A read-only copy of the message at the point of this history (if this has been enabled).
     */
    Message getMessage();

}
