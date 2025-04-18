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

import org.zenithblox.ZwangineContext;
import org.zenithblox.spi.ZwangineEvent;

import java.io.Serial;

public class ZwangineContextResumeFailureEvent extends AbstractContextEvent implements ZwangineEvent.ZwangineContextResumeFailureEvent {
    private static final @Serial long serialVersionUID = -4271899927507894566L;

    private final Throwable cause;

    public ZwangineContextResumeFailureEvent(ZwangineContext context, Throwable cause) {
        super(context);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "Failed to resume ZwangineContext: " + getContext().getName() + " due to " + cause.getMessage();
    }
}
