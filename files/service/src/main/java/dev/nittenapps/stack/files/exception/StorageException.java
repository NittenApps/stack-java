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
 * Copyright (c) 2025. NittenApps
 */

package dev.nittenapps.stack.files.exception;

import java.io.Serial;

/**
 * This exception is thrown to indicate issues related to storage operations.
 * <p>
 * It acts as a base class for more specific storage-related exceptions. Developers can use or extend this exception to
 * represent various errors encountered while performing storage-related tasks such as accessing or modifying files or
 * data in storage systems.
 * <p>
 * The constructor allows specifying a descriptive message and optionally a cause (another throwable), enabling a more
 * detailed diagnostic of the root problem.
 */
public class StorageException extends RuntimeException {
    @Serial private static final long serialVersionUID = -141570273710856752L;

    /**
     * Constructs a new {@code StorageException} with the specified detail message.
     *
     * @param message the detail message explaining the exception. It provides context for the error encountered during
     *                a storage operation.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code StorageException} with the specified detail message and cause.
     *
     * @param message the detail message explaining the exception. It provides context for the error encountered during
     *                a storage operation.
     * @param cause   the underlying cause of the exception. This can be another throwable that triggered this
     *                exception, allowing for a more detailed analysis of the root issue.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
