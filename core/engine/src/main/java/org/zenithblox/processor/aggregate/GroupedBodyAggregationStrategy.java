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

import org.zenithblox.Exchange;
import org.zenithblox.Message;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;

import java.util.List;

/**
 * Aggregate body of input {@link Message} into a single combined Exchange holding all the aggregated bodies in a
 * {@link List} of type {@link Object} as the message body.
 *
 * This aggregation strategy can used in combination with {@link org.zenithblox.processor.Splitter} to batch messages
 */
@Metadata(label = "bean",
          description = "Aggregate body of input Message into a single combined Exchange holding all the aggregated bodies in a List"
                        + " of type Object as the message body. This aggregation strategy can be used in combination with"
                        + " Splitter to batch messages.",
          annotations = { "interfaceName=org.zenithblox.AggregationStrategy" })
@Configurer(metadataOnly = true)
public class GroupedBodyAggregationStrategy extends AbstractListAggregationStrategy<Object> {

    @Override
    public Object getValue(Exchange exchange) {
        return exchange.getIn().getBody();
    }
}
