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

import java.util.UUID;

/**
 * SimpleIdDto is a marker interface for Data Transfer Objects (DTOs) that extend {@link BaseDto} with a unique
 * identifier of type {@link UUID}. This interface is designed to provide a consistent definition of entities
 * identifiable by a UUID within the application.
 * <p>
 * Implementations of this interface can add additional properties and behaviors while maintaining the required
 * structure of having a UUID identifier. It serves as a base for DTOs used in a variety of contexts, such as
 * components, lists, and attribute-based entities.
 */
public interface SimpleIdDto extends BaseDto<UUID> {
}
