/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
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
package org.zenithblox.component.pgevent;

import com.impossibl.postgres.api.jdbc.PGConnection;
import com.impossibl.postgres.api.jdbc.PGNotificationListener;
import org.zenithblox.Exchange;
import org.zenithblox.Message;
import org.zenithblox.Processor;
import org.zenithblox.support.DefaultConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;

/**
 * The PgEvent consumer.
 */
public class PgEventConsumer extends DefaultConsumer implements PGNotificationListener {

    private static final Logger LOG = LoggerFactory.getLogger(PgEventConsumer.class);

    private final PgEventEndpoint endpoint;
    private PGConnection dbConnection;

    public PgEventConsumer(PgEventEndpoint endpoint, Processor processor) {
        super(endpoint, processor);
        this.endpoint = endpoint;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        dbConnection = endpoint.initJdbc();
        String sql = String.format("LISTEN %s", endpoint.getChannel());
        try (PreparedStatement statement = dbConnection.prepareStatement(sql)) {
            statement.execute();
        }
        dbConnection.addNotificationListener(endpoint.getChannel(), endpoint.getChannel(), this);
    }

    @Override
    public void notification(int processId, String channel, String payload) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Notification processId: {}, channel: {}, payload: {}", processId, channel, payload);
        }

        Exchange exchange = createExchange(false);
        Message msg = exchange.getIn();
        msg.setHeader(PgEventConstants.HEADER_CHANNEL, channel);
        msg.setBody(payload);

        try {
            getProcessor().process(exchange);
        } catch (Exception e) {
            exchange.setException(e);
        }
        if (exchange.getException() != null) {
            String cause = "Unable to process incoming notification from PostgreSQL: processId='" + processId + "', channel='"
                           + channel + "', payload='" + payload + "'";
            getExceptionHandler().handleException(cause, exchange.getException());
        }
        releaseExchange(exchange, false);
    }

    @Override
    protected void doStop() throws Exception {
        if (dbConnection != null) {
            dbConnection.removeNotificationListener(endpoint.getChannel());
            String sql = String.format("UNLISTEN %s", endpoint.getChannel());
            try (PreparedStatement statement = dbConnection.prepareStatement(sql)) {
                statement.execute();
            }
            dbConnection.close();
        }
    }
}
