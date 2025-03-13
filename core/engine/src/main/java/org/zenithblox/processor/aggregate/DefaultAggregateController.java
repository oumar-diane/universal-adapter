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
package org.zenithblox.processor.aggregate;

/**
 * A default {@link org.zenithblox.processor.aggregate.AggregateController} that offers Java and JMX API.
 */
public class DefaultAggregateController implements AggregateController {

    private AggregateProcessor processor;

    @Override
    public void onStart(AggregateProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void onStop(AggregateProcessor processor) {
        this.processor = null;
    }

    @Override
    public int forceCompletionOfGroup(String key) {
        if (processor != null) {
            return processor.forceCompletionOfGroup(key);
        } else {
            return 0;
        }
    }

    @Override
    public int forceCompletionOfAllGroups() {
        if (processor != null) {
            return processor.forceCompletionOfAllGroups();
        } else {
            return 0;
        }
    }

    @Override
    public int forceDiscardingOfGroup(String key) {
        if (processor != null) {
            return processor.forceDiscardingOfGroup(key);
        } else {
            return 0;
        }
    }

    @Override
    public int forceDiscardingOfAllGroups() {
        if (processor != null) {
            return processor.forceDiscardingOfAllGroups();
        } else {
            return 0;
        }
    }
}
