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
import lombok.Getter;

import java.io.Serial;

/**
 * Custom exception used for handling API-related errors.
 * <p>
 * Extends {@link RuntimeException}, allowing it to be thrown during the program's runtime without a declared `throws`
 * clause. Implements the {@link ApiBody} marker interface to signal that it can be included in API response structures.
 * <p>
 * This class encapsulates an HTTP status code, an {@link ApiMessage} object containing error details, and provides
 * mechanisms to build and display a standardized error message.
 * <p>
 * Key features:
 * - Stores an integer status code to align with HTTP response standards.
 * - Encapsulates API-related error information using an {@code ApiMessage} instance.
 * - Offers a default constructor for common server errors, with a status code of 500 and a default {@code ApiMessage}.
 * <p>
 * This exception is particularly useful when designing RESTful APIs  for centralizing error handling and maintaining a
 * consistent error response format.
 */
@Getter
@AllArgsConstructor
public class ApiException extends RuntimeException implements ApiBody {
    @Serial private static final long serialVersionUID = 822513517633307307L;

    private final int code;

    private final ApiMessage apiMessage;

    /**
     * Constructs an instance of {@code ApiException} with a default HTTP status code of 500 and an
     * {@link ApiMessage} containing information derived from the provided exception.
     *
     * @param ex The exception to be encapsulated within this {@code ApiException}. It's used to initialize an
     *           {@link ApiMessage} with the level set to {@code ERROR} and details extracted from the supplied
     *           exception.
     */
    public ApiException(Exception ex) {
        this(500, new ApiMessage(ApiMessage.Level.ERROR, ex));
    }

    /**
     * Returns the formatted error message derived from the associated {@link ApiMessage}. The message includes the code
     * and descriptive text of the API error.
     *
     * @return A string in the format {@code "[<code>] <message>"}, where {@code <code>} is the error code from
     *         {@code apiMessage.getCode()} and {@code <message>} is the detailed error message from
     *         {@code apiMessage.getMessage()}.
     */
    @Override
    public String getMessage() {
        return "[" + apiMessage.getCode() + "] " + apiMessage.getMessage();
    }
}
