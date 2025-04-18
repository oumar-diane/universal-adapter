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
package org.zenithblox.processor.loadbalancer;

import org.zenithblox.AsyncProcessor;
import org.zenithblox.Exchange;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WeightedRandomLoadBalancer extends WeightedLoadBalancer {

    public WeightedRandomLoadBalancer(List<Integer> distributionRatioList) {
        super(distributionRatioList);
    }

    @Override
    protected AsyncProcessor chooseProcessor(AsyncProcessor[] processors, Exchange exchange) {
        lock.lock();
        try {
            int randomWeight = ThreadLocalRandom.current().nextInt(runtimeRatioSum);
            int choiceWeight = 0;
            int index = 0;
            while (true) {
                DistributionRatio ratio = getRatios().get(index);
                choiceWeight += ratio.getRuntimeWeight();
                if (randomWeight < choiceWeight) {
                    ratio.decrement();
                    decrementSum();
                    lastIndex = index;
                    return processors[index];
                }
                index++;
            }
        } finally {
            lock.unlock();
        }
    }

}
