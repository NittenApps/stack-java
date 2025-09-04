/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Copyright (c) 2024. NittenApps
 */

package dev.nittenapps.stack.spatial.oracle;

import lombok.SneakyThrows;
import oracle.jdbc.driver.OracleConnection;
import org.geolatte.geom.codec.db.oracle.DefaultConnectionFinder;
import org.springframework.lang.NonNull;

import java.io.Serial;
import java.sql.Connection;

/**
 * ConnectionFinder is a specialized implementation of the DefaultConnectionFinder class designed to handle connection
 * unwrapping for specific database connection types.
 * <p>
 * This class overrides the {@code find} method to provide custom logic for unwrapping connections, such as converting a
 * generic {@link Connection} instance into an Oracle-specific {@link OracleConnection}.
 */
@SuppressWarnings("unused")
public class ConnectionFinder extends DefaultConnectionFinder {
    @Serial private static final long serialVersionUID = -7040813961287083250L;

    @SneakyThrows
    @Override
    public Connection find(@NonNull Connection connection) {
        return connection.unwrap(OracleConnection.class);
    }
}
