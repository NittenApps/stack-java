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
 * BaseDto is a foundational interface for defining data transfer objects (DTOs) with an identifiable entity. It
 * provides generic methods to get and set a unique identifier for objects implementing this interface.
 * <p>
 * This interface is designed to be extended by other DTO interfaces or classes to ensure a standard structure for
 * identifiable entities.
 *
 * @param <ID> the type of the identifier, which must be serializable
 */
public interface BaseDto<ID extends Serializable> {
    /**
     * Retrieves the unique identifier of the implementing entity.
     *
     * @return the unique identifier of type ID
     */
    ID getId();

    /**
     * Sets the unique identifier for the entity.
     *
     * @param id the unique identifier of type ID
     */
    void setId(ID id);
}
