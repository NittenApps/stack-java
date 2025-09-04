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

package dev.nittenapps.stack.data.service;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;

/**
 * The DataService interface serves as a base contract for CRUD operations on a data layer, providing generic methods
 * that can be implemented for specific entity and DTO types.
 *
 * @param <E>  Represents the entity type.
 * @param <ID> Represents the type of the identifier for the entity.
 * @param <L>  Represents the type of the list DTO used for query results.
 * @param <O>  Represents the type of the object DTO used for CRUD operations.
 */
@SuppressWarnings("unused")
public interface DataService<E, ID, L, O> {
    /**
     * Counts the number of entities that match the given filters.
     *
     * @param filters A map of filter criteria used to constrain the query. Keys represent attribute names, and values
     *                represent the corresponding values to filter by.
     * @return The total number of entities that satisfy the given filter conditions.
     */
    default long count(Map<String, Object> filters) {
        throw new NotImplementedException();
    }

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to be fetched. Must not be null.
     * @return The entity associated with the provided identifier, or null if no entity is found.
     */
    E findById(@NonNull ID id);

    /**
     * Retrieves a list of items that match the given filters, with support for pagination and sorting.
     *
     * @param filters A map of filter criteria used to constrain the query. Keys represent attribute names, and values
     *                represent the corresponding values to filter by.
     * @param offset  The starting position of the results. Used for pagination.
     * @param limit   The maximum number of results to return. A value of 0 indicates no limit.
     * @param sort    A string representing sorting criteria. Multiple sort fields can be provided, separated by commas.
     *                Each field can optionally specify "asc" or "desc" for ascending or descending order, respectively.
     * @return A list of items of type L that match the specified criteria.
     */
    default List<L> getList(Map<String, Object> filters, int offset, int limit, String sort) {
        throw new NotImplementedException();
    }

    /**
     * Retrieves an object DTO associated with the given identifier.
     *
     * @param id The unique identifier of the object to be retrieved. Must not be null.
     * @return The object DTO of type O corresponding to the provided identifier. If no object is found, the
     *         implementation may throw an exception or return null.
     */
    default O getObject(@NonNull ID id) {
        throw new NotImplementedException();
    }

    /**
     * Saves the given object to the data store. The object is typically a Data Transfer Object (DTO) which will be
     * mapped to an entity for persistence. This method can be used to create or update entities in the data store.
     *
     * @param object The object of type O to be saved. Must not be null.
     * @return The saved entity of type E.
     */
    default E save(@NonNull O object) {
        throw new NotImplementedException();
    }
}
