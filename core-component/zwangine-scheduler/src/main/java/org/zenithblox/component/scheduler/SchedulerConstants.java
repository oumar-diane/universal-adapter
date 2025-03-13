/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.zentihblox.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zenithblox.component.scheduler;

import org.zenithblox.Exchange;
import org.zenithblox.spi.Metadata;

public final class SchedulerConstants {

    @Metadata(description = "The timestamp of the message", javaType = "long")
    public static final String MESSAGE_TIMESTAMP = Exchange.MESSAGE_TIMESTAMP;

    private SchedulerConstants() {

    }
}
