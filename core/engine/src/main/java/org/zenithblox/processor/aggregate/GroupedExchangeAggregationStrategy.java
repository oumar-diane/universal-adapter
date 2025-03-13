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
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.DefaultExchange;

import java.util.List;

/**
 * Aggregate all exchanges into a single combined Exchange holding all the aggregated exchanges in a {@link List} of
 * {@link Exchange} as the message body.
 * <p/>
 * <b>Important:</b> This strategy is not to be used with the
 * <a href="http://zwangine.zwangine.org/content-enricher.html">Content Enricher</a> EIP which is enrich or pollEnrich.
 */
@Metadata(label = "bean",
          description = "Aggregate all Exchanges into a single combined Exchange holding all the aggregated exchanges in a List"
                        + " of Exchange as the message body. This aggregation strategy can be used in combination with"
                        + " Splitter to batch messages.",
          annotations = { "interfaceName=org.zenithblox.AggregationStrategy" })
@Configurer(metadataOnly = true)
public class GroupedExchangeAggregationStrategy extends AbstractListAggregationStrategy<Exchange> {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            // for the first time we must create a new empty exchange as the holder, as the outgoing exchange
            // must not be one of the grouped exchanges, as that causes a endless circular reference
            oldExchange = new DefaultExchange(newExchange);
        }
        return super.aggregate(oldExchange, newExchange);
    }

    @Override
    public Exchange getValue(Exchange exchange) {
        return exchange;
    }

}
