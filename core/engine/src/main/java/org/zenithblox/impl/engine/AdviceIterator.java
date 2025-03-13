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

package org.zenithblox.impl.engine;

import org.zenithblox.Exchange;
import org.zenithblox.spi.ZwangineInternalProcessorAdvice;

import java.util.List;

final class AdviceIterator {
    private AdviceIterator() {

    }

    static void runAfterTasks(List<? extends ZwangineInternalProcessorAdvice> advices, Object[] states, Exchange exchange) {
        int stateIndex = states.length - 1;

        for (int i = advices.size() - 1; i >= 0; i--) {
            ZwangineInternalProcessorAdvice task = advices.get(i);
            Object state = null;
            if (task.hasState()) {
                state = states[stateIndex--];
            }
            runAfterTask(task, state, exchange);
        }
    }

    static void runAfterTask(ZwangineInternalProcessorAdvice task, Object state, Exchange exchange) {
        try {
            task.after(exchange, state);
        } catch (Exception e) {
            exchange.setException(e);
            // allow all advices to complete even if there was an exception
        }
    }
}
