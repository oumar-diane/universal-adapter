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
import org.zenithblox.StaticService;

import java.util.Collection;

/**
 * A repository which tracks in flight {@link Exchange}s.
 */
public interface InflightRepository extends StaticService {

    /**
     * Information about the inflight exchange.
     */
    interface InflightExchange {

        /**
         * The exchange being inflight
         */
        Exchange getExchange();

        /**
         * The duration in millis the exchange has been inflight
         */
        long getDuration();

        /**
         * The elapsed time in millis processing the exchange at the current node
         */
        long getElapsed();

        /**
         * The id of the node from the workflow where the exchange currently is being processed
         * <p/>
         * Is <tt>null</tt> if message history is disabled.
         */
        String getNodeId();

        /**
         * The id of the workflow where the exchange originates (started)
         */
        String getFromWorkflowId();

        /**
         * Whether the endpoint is remote where the exchange originates (started)
         */
        boolean isFromRemoteEndpoint();

        /**
         * The id of the workflow where the exchange currently is being processed
         * <p/>
         * Is <tt>null</tt> if message history is disabled.
         */
        String getAtWorkflowId();

    }

    /**
     * Adds the exchange to the inflight registry to the total counter
     *
     * @param exchange the exchange
     */
    void add(Exchange exchange);

    /**
     * Removes the exchange from the inflight registry to the total counter
     *
     * @param exchange the exchange
     */
    void remove(Exchange exchange);

    /**
     * Adds the exchange to the inflight registry associated to the given workflow
     *
     * @param exchange the exchange
     * @param workflowId  the id of the workflow
     */
    void add(Exchange exchange, String workflowId);

    /**
     * Removes the exchange from the inflight registry removing association to the given workflow
     *
     * @param exchange the exchange
     * @param workflowId  the id of the workflow
     */
    void remove(Exchange exchange, String workflowId);

    /**
     * Current size of inflight exchanges.
     * <p/>
     * Will return 0 if there are no inflight exchanges.
     *
     * @return number of exchanges currently in flight.
     */
    int size();

    /**
     * Adds the workflow from the in flight registry.
     * <p/>
     * Is used for initializing up resources
     *
     * @param workflowId the id of the workflow
     */
    void addWorkflow(String workflowId);

    /**
     * Removes the workflow from the in flight registry.
     * <p/>
     * Is used for cleaning up resources to avoid leaking.
     *
     * @param workflowId the id of the workflow
     */
    void removeWorkflow(String workflowId);

    /**
     * Current size of inflight exchanges which are from the given workflow.
     * <p/>
     * Will return 0 if there are no inflight exchanges.
     *
     * @param  workflowId the id of the workflow
     * @return         number of exchanges currently in flight.
     */
    int size(String workflowId);

    /**
     * Whether the inflight repository should allow browsing each inflight exchange.
     *
     * This is by default disabled as there is a very slight performance overhead when enabled.
     */
    boolean isInflightBrowseEnabled();

    /**
     * Whether the inflight repository should allow browsing each inflight exchange.
     *
     * This is by default disabled as there is a very slight performance overhead when enabled.
     *
     * @param inflightBrowseEnabled whether browsing is enabled
     */
    void setInflightBrowseEnabled(boolean inflightBrowseEnabled);

    /**
     * A <i>read-only</i> browser of the {@link InflightExchange}s that are currently inflight.
     */
    Collection<InflightExchange> browse();

    /**
     * A <i>read-only</i> browser of the {@link InflightExchange}s that are currently inflight that started from the
     * given workflow.
     *
     * @param fromWorkflowId the workflow id, or <tt>null</tt> for all workflows.
     */
    Collection<InflightExchange> browse(String fromWorkflowId);

    /**
     * A <i>read-only</i> browser of the {@link InflightExchange}s that are currently inflight.
     *
     * @param limit                 maximum number of entries to return
     * @param sortByLongestDuration to sort by the longest duration. Set to <tt>true</tt> to include the exchanges that
     *                              has been inflight the longest time, set to <tt>false</tt> to sort by exchange id
     */
    Collection<InflightExchange> browse(int limit, boolean sortByLongestDuration);

    /**
     * A <i>read-only</i> browser of the {@link InflightExchange}s that are currently inflight that started from the
     * given workflow.
     *
     * @param fromWorkflowId           the workflow id, or <tt>null</tt> for all workflows.
     * @param limit                 maximum number of entries to return
     * @param sortByLongestDuration to sort by the longest duration. Set to <tt>true</tt> to include the exchanges that
     *                              has been inflight the longest time, set to <tt>false</tt> to sort by exchange id
     */
    Collection<InflightExchange> browse(String fromWorkflowId, int limit, boolean sortByLongestDuration);

    /**
     * Gets the oldest {@link InflightExchange} that are currently inflight that started from the given workflow.
     *
     * @param  fromWorkflowId the workflow id, or <tt>null</tt> for all workflows.
     * @return             the oldest, or <tt>null</tt> if none inflight
     */
    InflightExchange oldest(String fromWorkflowId);

}
