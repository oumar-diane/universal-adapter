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

import java.sql.Connection;
import java.sql.SQLException;

public final class PgEventHelper {

    private PgEventHelper() {
    }

    public static PGConnection toPGConnection(Connection connection) throws SQLException {
        if (connection == null) {
            throw new IllegalArgumentException("Connection must not be null");
        }

        PGConnection conn;
        if (connection instanceof PGConnection) {
            conn = (PGConnection) connection;
        } else if (connection.isWrapperFor(PGConnection.class)) {
            conn = connection.unwrap(PGConnection.class);
        } else {
            throw new IllegalStateException("Cannot obtain PGConnection");
        }
        return conn;
    }
}
