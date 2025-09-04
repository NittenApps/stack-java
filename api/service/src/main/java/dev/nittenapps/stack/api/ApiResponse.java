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

package dev.nittenapps.stack.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents a standardized structure for API responses. The `ApiResponse` class encapsulates metadata about the
 * response, including whether the operation was successful, a timestamp, the response body, and any related messages.
 *
 * @param <T> the type of the response body, which must implement the {@link ApiBody} interface
 */
@Data
@NoArgsConstructor @AllArgsConstructor
public class ApiResponse<T extends ApiBody> {
    private boolean success;

    private Timestamp timestamp;

    private T body;

    private ApiMessage[] messages;

    /**
     * Constructs an ApiResponse instance with a given response body and associated messages. Automatically sets the
     * success status to true and generates a timestamp for the response.
     *
     * @param body the response body of type T implementing the ApiBody interface
     * @param messages an array of ApiMessage objects providing details about the response or errors
     */
    public ApiResponse(T body, ApiMessage[] messages) {
        this(true, new Timestamp(System.currentTimeMillis()), body, messages);
    }
}
