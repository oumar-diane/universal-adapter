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
package org.zenithblox.impl.event;

import org.zenithblox.Workflow;
import org.zenithblox.spi.ZwangineEvent;

import java.io.Serial;

public class WorkflowRestartingFailureEvent extends AbstractWorkflowEvent implements ZwangineEvent.WorkflowRestartingFailureEvent {
    private static final @Serial long serialVersionUID = 1330257282431407331L;
    private final long attempt;
    private final Throwable cause;
    private final boolean exhausted;

    public WorkflowRestartingFailureEvent(Workflow source, long attempt, Throwable cause, boolean exhausted) {
        super(source);
        this.attempt = attempt;
        this.cause = cause;
        this.exhausted = exhausted;
    }

    public long getAttempt() {
        return attempt;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public boolean isExhausted() {
        return exhausted;
    }

    @Override
    public String toString() {
        return "Workflow " + (attempt == 0 ? "starting " : "restarting ") + (exhausted ? "exhausted: " : "failed: ")
               + getWorkflow().getId() + " (attempt: " + attempt
               + ") due to " + cause.getMessage();
    }
}
