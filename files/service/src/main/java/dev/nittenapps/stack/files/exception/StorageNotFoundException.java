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
 * This exception is thrown when a specific storage resource could not be found.
 *
 * It extends {@code StorageException}, providing more specificity for scenarios where a required storage element or
 * resource is missing or inaccessible. This might include missing files, directories, or other components in a storage
 * system.
 */
public class StorageNotFoundException extends StorageException {
    @Serial private static final long serialVersionUID = 1428117185242834673L;

    public StorageNotFoundException(String message) {
        super(message);
    }

    public StorageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
