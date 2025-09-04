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

package dev.nittenapps.stack.data.domain;

import java.util.Map;

/**
 * Represents an abstract base class for entities that manage a collection of attributes.
 * This class is generic and requires a type parameter that must extend {@code AbstractAttribute}.
 * <p>
 * It provides functionality for:
 * - Storing and retrieving attributes via a map where the key is a string identifier, and the value is the attribute instance.
 * <p>
 * Subclasses should implement the abstract method to retrieve the map of attributes.
 * <p>
 * Key Features:
 * - Inherits from {@code AbstractSimpleId}, gaining UUID-based identification and versioning support.
 * - Designed to be extended by domain-specific entities that require attribute handling.
 * <p>
 * Generic Constraints:
 * - {@code T} must extend {@code AbstractAttribute}, ensuring that the attributes managed by this class comply with the attribute
 *   framework defined by {@code AbstractAttribute}.
 *
 * @param <T> The type of attribute that this entity supports, extending {@code AbstractAttribute}.
 */
public abstract class WithAttributes<T extends AbstractAttribute> extends AbstractSimpleId {
    public abstract Map<String, T> getAttributes();
}
