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
package org.zenithblox.saga;

import org.zenithblox.Endpoint;
import org.zenithblox.Expression;

import java.util.Map;
import java.util.Optional;

/**
 * Defines the configuration of a saga step.
 */
public class ZwangineSagaStep {

    private final Endpoint compensation;

    private final Endpoint completion;

    private final Map<String, Expression> options;

    private final Long timeoutInMilliseconds;

    public ZwangineSagaStep(Endpoint compensation, Endpoint completion,
                         Map<String, Expression> options, Long timeoutInMilliseconds) {
        this.compensation = compensation;
        this.completion = completion;
        this.options = options;
        this.timeoutInMilliseconds = timeoutInMilliseconds;
    }

    public Optional<Endpoint> getCompensation() {
        return Optional.ofNullable(compensation);
    }

    public Optional<Endpoint> getCompletion() {
        return Optional.ofNullable(completion);
    }

    public Map<String, Expression> getOptions() {
        return options;
    }

    public Optional<Long> getTimeoutInMilliseconds() {
        return Optional.ofNullable(timeoutInMilliseconds);
    }

    public boolean isEmpty() {
        return compensation == null && completion == null && options.isEmpty() && timeoutInMilliseconds == null;
    }
}
