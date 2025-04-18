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

import org.zenithblox.AggregationStrategy;
import org.zenithblox.Exchange;
import org.zenithblox.ExchangePropertyKey;
import org.zenithblox.Expression;
import org.zenithblox.spi.Configurer;
import org.zenithblox.spi.Metadata;
import org.zenithblox.support.builder.ExpressionBuilder;

/**
 * Aggregate result of pick expression into a single combined Exchange holding all the aggregated bodies in a
 * {@link String} as the message body.
 *
 * This aggregation strategy can used in combination with {@link org.zenithblox.processor.Splitter} to batch messages
 */
@Metadata(label = "bean",
          description = "Aggregate result of pick expression into a single combined Exchange holding all the aggregated bodies in a"
                        + " String as the message body. This aggregation strategy can used in combination with Splitter to batch messages",
          annotations = { "interfaceName=org.zenithblox.AggregationStrategy" })
@Configurer(metadataOnly = true)
public class StringAggregationStrategy implements AggregationStrategy {

    @Metadata(description = "Delimiter used for joining strings together.")
    private String delimiter = "";
    private Expression pickExpression = ExpressionBuilder.bodyExpression();

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public Expression getPickExpression() {
        return pickExpression;
    }

    public void setPickExpression(Expression pickExpression) {
        this.pickExpression = pickExpression;
    }

    /**
     * Set delimiter used for joining aggregated String
     *
     * @param delimiter The delimiter to join with. Default empty String
     */
    public StringAggregationStrategy delimiter(String delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    /**
     * Set an expression to extract the element to be aggregated from the incoming {@link Exchange}.
     * <p/>
     * By default, it picks the full IN message body of the incoming exchange.
     *
     * @param  expression The picking expression.
     * @return            This instance.
     */
    public StringAggregationStrategy pick(Expression expression) {
        this.pickExpression = expression;
        return this;
    }

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        StringBuffer result; // Aggregate in StringBuffer instead of StringBuilder, to make it thread safe
        StringBuilder value = new StringBuilder(512);
        if (oldExchange == null) {
            result = getStringBuffer(newExchange); // do not prepend delimiter for first invocation
        } else {
            result = getStringBuffer(oldExchange);
            value.append(delimiter);
        }

        if (newExchange != null) {
            String pick = pickExpression.evaluate(newExchange, String.class);
            if (pick != null) {
                value.append(pick);
                result.append(value);
            }
        }

        return oldExchange != null ? oldExchange : newExchange;
    }

    @Override
    public void onCompletion(Exchange exchange) {
        if (exchange != null) {
            StringBuffer stringBuffer = (StringBuffer) exchange.removeProperty(ExchangePropertyKey.GROUPED_EXCHANGE);
            if (stringBuffer != null) {
                exchange.getIn().setBody(stringBuffer.toString());
            }
        }
    }

    private static StringBuffer getStringBuffer(Exchange exchange) {
        StringBuffer stringBuffer = exchange.getProperty(ExchangePropertyKey.GROUPED_EXCHANGE, StringBuffer.class);
        if (stringBuffer == null) {
            stringBuffer = new StringBuffer();
            exchange.setProperty(ExchangePropertyKey.GROUPED_EXCHANGE, stringBuffer);
        }
        return stringBuffer;
    }

}
