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
package org.zenithblox.spi;

import org.zenithblox.Exchange;
import org.zenithblox.NamedNode;
import org.zenithblox.Processor;
import org.zenithblox.spi.ZwangineEvent.ExchangeEvent;

/**
 * A condition to define when a given {@link Exchange} matches when is being workflowd.
 * <p/>
 * Is used by the {@link org.zenithblox.spi.Debugger} to apply {@link Condition}s to
 * {@link org.zenithblox.spi.Breakpoint}s to define rules when the breakpoints should match.
 */
public interface Condition {

    /**
     * Does the condition match
     *
     * @param  exchange   the exchange
     * @param  processor  the {@link Processor}
     * @param  definition the present location in the workflow where the {@link Exchange} is located at
     * @param  before     before or after processing
     * @return            <tt>true</tt> to match, <tt>false</tt> otherwise
     */
    boolean matchProcess(Exchange exchange, Processor processor, NamedNode definition, boolean before);

    /**
     * Does the condition match
     *
     * @param  exchange the exchange
     * @param  event    the event (instance of {@link ExchangeEvent}
     * @return          <tt>true</tt> to match, <tt>false</tt> otherwise
     * @see             ExchangeEvent
     */
    boolean matchEvent(Exchange exchange, ExchangeEvent event);

}
