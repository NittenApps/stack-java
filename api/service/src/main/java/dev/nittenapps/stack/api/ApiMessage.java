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
import org.apache.commons.lang3.exception.ExceptionUtils;

/**
 * Represents a standardized message format to be used in API responses or for reporting issues within the application.
 * The class encapsulates information such as severity level, a unique code, a descriptive message, and additional
 * details about the issue.
 * <p>
 * Key Features:
 * - Supports multiple severity levels through the {@link Level} enum.
 * - Allows creation of messages with or without exception information.
 * - Provides constructors for different initialization needs, including support for exceptions and custom error codes.
 * - Can include metadata such as a unique code and stack trace details for enhanced debugging.
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class ApiMessage {
    private Level level;

    private String code;

    private String message;

    private String detail;

    /**
     * Creates an instance of ApiMessage with a default severity level of ERROR.
     *
     * @param exception the exception containing details about the error or issue
     */
    @SuppressWarnings("unused")
    public ApiMessage(Exception exception) {
        this(Level.ERROR, exception);
    }

    /**
     * Constructs an ApiMessage instance with the provided severity level and exception. Defaults the error code to
     * "ERR-000".
     *
     * @param level the severity level of the message
     * @param exception the exception containing details about the error
     */
    public ApiMessage(Level level, Exception exception) {
        this(level, "ERR-000", exception);
    }

    /**
     * Constructs an ApiMessage instance with the specified severity level, unique code, and exception details. The
     * exception details are extracted to populate the message and stack trace.
     *
     * @param level the severity level of the ApiMessage
     * @param code the unique error code associated with the message
     * @param exception the exception containing details about the error or issue
     */
    public ApiMessage(Level level, String code, Exception exception) {
        this(level, code, ExceptionUtils.getRootCauseMessage(exception), ExceptionUtils.getStackTrace(exception));
    }

    public enum Level {
        /**
         * Fatal error, the system cannot function
         */
        FATAL,
        /**
         * Error processing user request
         */
        ERROR,
        /**
         * Alert the user
         */
        WARNING,
        /**
         * Let the user know about the request result
         */
        INFO,
        /**
         * System is ok
         */
        OK,
        /**
         * System is not able to process request
         */
        TOO_BUSY
    }
}
