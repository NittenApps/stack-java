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

/**
 * Represents a generic API response body wrapper for a single object.
 * <p>
 * This class provides a container for wrapping an object within an API response. It implements the {@link ApiBody}
 * interface, ensuring compatibility with standardized API response structures. The generic type parameter <T> allows
 * flexibility in defining the type of object encapsulated by this class.
 *
 * @param <T> the type of the object that this body contains
 */
@Data @AllArgsConstructor @NoArgsConstructor
public class ObjectBody<T> implements ApiBody {
    private T object;
}
