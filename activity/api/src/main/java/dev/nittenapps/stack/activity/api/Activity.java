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

package dev.nittenapps.stack.activity.api;

import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

/**
 * Defines the contract for an activity that provides CRUD-like operations,  including retrieving lists, retrieving
 * specific objects, and saving data. The interface is generic and allows parameterization to adapt to various use
 * cases.
 *
 * @param <E> the entity type of the data managed by the activity
 * @param <ID> the data type for the unique identifier of the entity
 * @param <L> the type for the list representation or elements
 * @param <O> the type for the object representation
 */
@SuppressWarnings("unused")
public interface Activity<E, ID, L, O> {
    /**
     * Retrieves a list of items based on the provided parameters and user details.
     *
     * @param params a {@code MultiValueMap} containing query parameters used to filter and sort the results
     * @param user the user requesting the list, used for context or authorization checks
     * @return an {@code ApiResponse} containing a {@code ListBody} that encapsulates the list of items, pagination
     *         details, and total count
     */
    default ApiResponse<ListBody<L>> getList(@NonNull MultiValueMap<String, String> params, User user) {
        throw new NotImplementedException();
    }

    /**
     * Retrieves a specific object based on the provided identifier and user details.
     *
     * @param id   the unique identifier of the object to retrieve
     * @param user the user requesting the retrieval, used for context or authorization checks
     * @return an {@code ApiResponse} containing an {@code ObjectBody} that encapsulates the retrieved object
     */
    default ApiResponse<ObjectBody<O>> getObject(@NonNull ID id, User user) {
        throw new NotImplementedException();
    }

    /**
     * Saves the provided data, associating it with the given user.
     *
     * @param body a map containing the key-value pairs of the object to save
     * @param user the user performing the save operation, used for context or authorization checks
     * @return an {@code ApiResponse} containing an {@code ObjectBody} that encapsulates the saved object
     */
    default ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        throw new NotImplementedException();
    }
}
