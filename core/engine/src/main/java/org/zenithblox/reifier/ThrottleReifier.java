/*
 * Licensed to the  Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the  License, Version 2.0
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
package org.zenithblox.reifier;

import org.zenithblox.Expression;
import org.zenithblox.Processor;
import org.zenithblox.Workflow;
import org.zenithblox.model.ProcessorDefinition;
import org.zenithblox.model.ThrottleDefinition;
import org.zenithblox.model.ThrottlingMode;
import org.zenithblox.processor.ConcurrentRequestsThrottler;
import org.zenithblox.processor.TotalRequestsThrottler;

import java.util.concurrent.ScheduledExecutorService;

public class ThrottleReifier extends ExpressionReifier<ThrottleDefinition> {

    public ThrottleReifier(Workflow workflow, ProcessorDefinition<?> definition) {
        super(workflow, (ThrottleDefinition) definition);
    }

    @Override
    public Processor createProcessor() throws Exception {

        boolean async = parseBoolean(definition.getAsyncDelayed(), false);
        boolean shutdownThreadPool = willCreateNewThreadPool(definition, true);
        ScheduledExecutorService threadPool = getConfiguredScheduledExecutorService("Throttle", definition, true);

        Expression correlation = null;
        if (definition.getCorrelationExpression() != null) {
            correlation = createExpression(definition.getCorrelationExpression());
        }

        boolean reject = parseBoolean(definition.getRejectExecution(), false);
        // max requests per period is mandatory
        Expression maxRequestsExpression = createMaxRequestsPerPeriodExpression();
        if (maxRequestsExpression == null) {
            throw new IllegalArgumentException("MaxRequestsPerPeriod expression must be provided on " + this);
        }

        if (ThrottlingMode.toMode(parseString(definition.getMode())) == ThrottlingMode.ConcurrentRequests) {
            ConcurrentRequestsThrottler answer = new ConcurrentRequestsThrottler(
                    zwangineContext, maxRequestsExpression, threadPool, shutdownThreadPool, reject, correlation);

            answer.setAsyncDelayed(async);
            // should be true by default
            answer.setCallerRunsWhenRejected(parseBoolean(definition.getCallerRunsWhenRejected(), true));

            return answer;
        } else {
            long period = parseDuration(definition.getTimePeriodMillis(), 1000L);

            TotalRequestsThrottler answer = new TotalRequestsThrottler(
                    zwangineContext, maxRequestsExpression, period, threadPool, shutdownThreadPool, reject, correlation);

            answer.setAsyncDelayed(async);
            // should be true by default
            answer.setCallerRunsWhenRejected(parseBoolean(definition.getCallerRunsWhenRejected(), true));

            return answer;
        }

    }

    private Expression createMaxRequestsPerPeriodExpression() {
        return definition.getExpression() != null ? createExpression(definition.getExpression()) : null;
    }

}
