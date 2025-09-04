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

package dev.nittenapps.stack.data.dto;

import java.io.Serializable;

/**
 * ObjectDto is a marker interface that extends {@link BaseDto} with a unique identifier of a generic type. It is
 * designed to represent data transfer objects (DTOs) that encapsulate identifiable entities within a system.
 * <p>
 * This interface allows flexibility through the use of generics, enabling the identifier to be of any type that
 * implements {@link Serializable}. Subclasses or implementing classes typically define additional properties and
 * behaviors for concrete implementations.
 *
 * @param <ID> the type of the unique identifier, which must be serializable
 */
public interface ObjectDto<ID extends Serializable> extends BaseDto<ID> {
}
