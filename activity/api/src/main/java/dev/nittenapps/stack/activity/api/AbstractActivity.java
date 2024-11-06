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
import dev.nittenapps.stack.data.service.DataService;
import dev.nittenapps.stack.data.util.DataUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractActivity<E, ID, L, O> implements Activity<E, ID, L, O>, BeanNameAware {
    protected String beanName;

    protected final DataService<E, ID, L, O> dataService;

    protected final Class<ID> idClass;

    protected ObjectMapper objectMapper;

    @Override
    public final void setBeanName(@NonNull String beanName) {
        this.beanName = beanName;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected final void setObjectMapper(@NonNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AbstractActivity() {
        this(null);
    }

    public AbstractActivity(DataService<E, ID, L, O> dataService) {
        Type _class = getClass();
        while (_class != null) {
            if (_class instanceof ParameterizedType) {
                break;
            }
            if (_class instanceof Class) {
                _class = ((Class<?>)_class).getGenericSuperclass();
            } else {
                _class = null;
            }
        }
        if (_class == null) {
            //noinspection unchecked
            idClass = (Class<ID>)UUID.class;
        } else {
            Type[] types = ((ParameterizedType)_class).getActualTypeArguments();
            if (types.length == 4) {
                //noinspection unchecked
                idClass = (Class<ID>)types[1];
            } else {
                //noinspection unchecked
                idClass = (Class<ID>)UUID.class;
            }
        }

        this.dataService = dataService;
    }

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
            return new ApiResponse<>(new ListBody<L>(Collections.EMPTY_LIST, pageable.getPageNumber(), 0), null);
        }

        return new ApiResponse<>(new ListBody<>(dataService.getList(filters,
                pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize(), params.getFirst("sort")),
                pageable.getPageNumber(), count), null);
    }

    @NonNull
    protected Map<String, Object> getFilters(MultiValueMap<String, String> params) {
        if (MapUtils.isEmpty(params)) {
            //noinspection unchecked
            return Collections.EMPTY_MAP;
        }

        return params.entrySet().stream()
                .filter(entry -> !StringUtils.equalsAny(entry.getKey(), "sort", "pageSize"))
                .filter(entry -> StringUtils.isNotBlank(params.getFirst(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> Objects.nonNull(params.getFirst(entry.getKey()))));
    }
}
