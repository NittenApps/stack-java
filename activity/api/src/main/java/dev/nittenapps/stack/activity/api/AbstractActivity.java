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

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.data.service.DataService;
import dev.nittenapps.stack.data.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;

import java.util.*;

/**
 * Abstract base class for implementing activity components. Provides common functionality such as handling bean name
 * setting, ObjectMapper injection, and generic DataService interaction for entities with specific types.
 *
 * @param <E>  the entity type handled by this activity
 * @param <ID> the ID type of the entity
 * @param <L>  the list DTO type used for listing entities
 * @param <O>  the object DTO type used for individual entities
 */
@Slf4j
public abstract class AbstractActivity<E, ID, L, O> implements Activity<E, ID, L, O>, BeanNameAware {
    protected String beanName;

    protected final DataService<E, ID, L, O> dataService;

    protected final Class<ID> idClass;

    protected ObjectMapper objectMapper;

    @Override
    public void setBeanName(@NonNull String beanName) {
        this.beanName = beanName;
    }

    @Autowired
    protected final void setObjectMapper(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AbstractActivity() {
        this(null);
    }

    public AbstractActivity(DataService<E, ID, L, O> dataService) {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(getClass(), AbstractActivity.class);
        log.debug("classes: {}", (Object)classes);

        //noinspection unchecked
        idClass = (Class<ID>)Objects.requireNonNull(classes)[1];

        this.dataService = dataService;
    }

    /**
     * Retrieves a paginated list of items based on the provided parameters and user details.
     *
     * @param params a {@code MultiValueMap} containing query parameters for filtering, sorting, and pagination of the
     *               results
     * @param user   the user requesting the data, used for context or authorization purposes
     * @return an {@code ApiResponse} containing a {@code ListBody} that wraps the list of items, page details, and
     * total count
     * @throws IllegalStateException if the data service is not set
     */
    @Override
    public ApiResponse<ListBody<L>> getList(@NonNull MultiValueMap<String, String> params, User user) {
        if (dataService == null) {
            throw new IllegalStateException("DataService not set");
        }

        Pageable pageable = DataUtils.buildPageable(params);
        Map<String, Object> filters = getFilters(params);
        long count = dataService.count(filters);
        if (count == 0) {
            //noinspection unchecked
            return new ApiResponse<>(new ListBody<L>(Collections.EMPTY_LIST, 0, 0), null);
        }

        if (pageable.isPaged()) {
            return new ApiResponse<>(new ListBody<>(dataService.getList(filters,
                    pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize(), params.getFirst("sort")),
                    pageable.getPageNumber(), count), null);
        }
        return new ApiResponse<>(new ListBody<>(dataService.getList(filters, 0, 0, params.getFirst("sort"))), null);
    }

    /**
     * Retrieves a single object identified by the given id
     *
     * @param id   the unique identifier of the object to retrieve
     * @param user the user requesting the retrieval, used for context or authorization checks
     * @return an {@code ApiResponse} containing a {@code ListBody} that wraps the object
     */
    @Override
    public ApiResponse<ObjectBody<O>> getObject(@NonNull ID id, User user) {
        return new ApiResponse<>(new ObjectBody<>(dataService.getObject(id)), null);
    }

    /**
     * Extracts filters from the provided {@code MultiValueMap} containing query parameters. Filters out specific keys
     * like "sort" and "pageSize" and ignores blank or null values.
     *
     * @param params a {@code MultiValueMap} containing the query parameters for filtering
     * @return a {@code Map} with filtered key-value pairs, where the values are non-blank and valid
     */
    @NonNull
    protected Map<String, Object> getFilters(MultiValueMap<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            //noinspection unchecked
            return Collections.EMPTY_MAP;
        }

        Map<String, Object> filters = new HashMap<>();
        params.entrySet().stream()
                .filter(entry -> !StringUtils.equalsAny(entry.getKey(), "sort", "page", "pageSize"))
                .forEach(entry -> {
                    List<String> values = entry.getValue().stream().filter(StringUtils::isNotBlank).toList();
                    if (values.size() > 1) {
                        filters.put(entry.getKey(), values);
                    } else if (!values.isEmpty()) {
                        filters.put(entry.getKey(), values.getFirst());
                    }
                });

        return filters;
    }
}
