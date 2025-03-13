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
package org.zenithblox.model.loadbalancer;

import org.zenithblox.Expression;
import org.zenithblox.model.ExpressionNodeHelper;
import org.zenithblox.model.ExpressionSubElementDefinition;
import org.zenithblox.model.LoadBalancerDefinition;
import org.zenithblox.model.language.ExpressionDefinition;
import org.zenithblox.spi.Metadata;

/**
 * Sticky load balancing using an expression to calculate a correlation key to perform the sticky load balancing.
 */
@Metadata(label = "eip,routing")
public class StickyLoadBalancerDefinition extends LoadBalancerDefinition {

    private ExpressionSubElementDefinition correlationExpression;

    public StickyLoadBalancerDefinition() {
    }

    protected StickyLoadBalancerDefinition(StickyLoadBalancerDefinition source) {
        super(source);
        this.correlationExpression
                = source.correlationExpression != null ? source.correlationExpression.copyDefinition() : null;
    }

    @Override
    public StickyLoadBalancerDefinition copyDefinition() {
        return new StickyLoadBalancerDefinition(this);
    }

    public ExpressionSubElementDefinition getCorrelationExpression() {
        return correlationExpression;
    }

    /**
     * The correlation expression to use to calculate the correlation key
     */
    public void setCorrelationExpression(ExpressionSubElementDefinition correlationExpression) {
        this.correlationExpression = correlationExpression;
    }

    public void setCorrelationExpression(Expression expression) {
        ExpressionDefinition def = ExpressionNodeHelper.toExpressionDefinition(expression);
        this.correlationExpression = new ExpressionSubElementDefinition();
        this.correlationExpression.setExpressionType(def);
    }

    @Override
    public String toString() {
        return "StickyLoadBalancer[" + correlationExpression + "]";
    }
}
