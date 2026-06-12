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

package dev.nittenapps.stack.data.mapper;

import dev.nittenapps.stack.data.dto.ListDto;
import dev.nittenapps.stack.data.dto.ObjectDto;
import org.mapstruct.MappingTarget;

import java.io.Serializable;

/**
 * DataMapper is a generic interface designed to facilitate the mapping between entity objects and their corresponding
 * Data Transfer Objects (DTOs), as well as the mapping between entity objects and list-based DTOs. This interface can
 * be implemented to define the specific mapping logic for various types of entities and their DTOs.
 *
 * @param <E>  the type of the entity object
 * @param <ID> the type of the unique identifier used for the entity and DTOs, which must implement {@link Serializable}
 * @param <L>  the type of the list-based DTO that extends {@link ListDto}
 * @param <O>  the type of the object-based DTO that extends {@link ObjectDto}
 */
public interface DataMapper<E, ID extends Serializable, L extends ListDto<ID>, O extends ObjectDto<ID>> {
    /**
     * Converts an entity object of type {@code E} to its corresponding DTO representation of type {@code O}.
     *
     * @param entity the entity object to be converted to a Data Transfer Object (DTO). Must not be null.
     * @return the DTO representation of the provided entity, of type {@code O}.
     */
    O toDto(E entity);

    /**
     * Converts a Data Transfer Object (DTO) of type {@code O} into its corresponding entity object of type {@code E}.
     *
     * @param dto the DTO to be converted into an entity. Must not be null.
     * @return the entity representation of the provided DTO, of type {@code E}.
     */
    E toEntity(O dto);

    /**
     * Converts a list-based Data Transfer Object (DTO) of type {@code L} into its corresponding entity object of type
     * {@code E}.
     *
     * @param dto the list-based DTO to be converted into an entity. Must not be null.
     * @return the entity representation of the provided list-based DTO, of type {@code E}.
     */
    E toEntity(L dto);

    /**
     * Converts an entity object of type {@code E} to its corresponding list-based Data Transfer Object (DTO)
     * representation of type {@code L}.
     *
     * @param entity the entity object to be converted to a list-based Data Transfer Object (DTO). Must not be null.
     * @return the list-based DTO representation of the provided entity, of type {@code L}.
     */
    L toListDto(E entity);

    /**
     * Updates the values of an existing entity using the data provided in the given Data Transfer Object (DTO).
     *
     * @param dto    the Data Transfer Object (DTO) that contains updated values. Must not be null.
     * @param entity the entity object to be updated with the values from the DTO. Must not be null.
     */
    void updateFromDto(O dto, @MappingTarget E entity);
}
