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

import org.zenithblox.AsyncCallback;
import org.zenithblox.AsyncProcessor;
import org.zenithblox.Exchange;
import org.zenithblox.Processor;

/**
 * A Shared (thread safe) internal {@link Processor} that Zwangine routing engine used during routing for cross cutting
 * functionality such as:
 * <ul>
 * <li>Execute {@link UnitOfWork}</li>
 * <li>Keeping track which workflow currently is being workflowd</li>
 * <li>Execute {@link WorkflowPolicy}</li>
 * <li>Gather JMX performance statics</li>
 * <li>Tracing</li>
 * <li>Debugging</li>
 * <li>Message History</li>
 * <li>Stream Caching</li>
 * <li>{@link Transformer}</li>
 * </ul>
 * ... and more.
 * <p/>
 *
 * This is intended for internal use only - do not use this.
 */
public interface SharedInternalProcessor extends Processor {

    @Override
    default void process(Exchange exchange) throws Exception {
        // not in use
    }

    /**
     * Asynchronous API
     */
    boolean process(Exchange exchange, AsyncCallback originalCallback, AsyncProcessor processor, Processor resultProcessor);

    /**
     * Synchronous API
     */
    void process(Exchange exchange, AsyncProcessor processor, Processor resultProcessor);

}
