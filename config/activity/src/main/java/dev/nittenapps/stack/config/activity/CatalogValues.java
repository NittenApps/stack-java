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

package dev.nittenapps.stack.config.activity;

import dev.nittenapps.stack.activity.api.AbstractActivity;
import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.config.domain.Catalog;
import dev.nittenapps.stack.config.dto.CatalogDto;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.service.CatalogService;
import dev.nittenapps.stack.config.service.CatalogValueService;
import dev.nittenapps.stack.data.util.DataUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Component("configCatalogValues")
@PreAuthorize("hasRole('admin')")
// TODO: read full value only when needed
public class CatalogValues extends AbstractActivity<Catalog, UUID, CatalogListDto, CatalogDto> {
    private final CatalogValueService catalogValueService;

    public CatalogValues(CatalogService catalogService, CatalogValueService catalogValueService) {
        super(catalogService);

        this.catalogValueService = catalogValueService;
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = true)
    public ApiResponse<ListBody<CatalogValueDto>> getValues(@NonNull MultiValueMap<String, String> params,
                                                            @NonNull User user) {
        Pageable pageable = DataUtils.buildPageable(params);
        Map<String, Object> filters = getFilters(params);
        long count = catalogValueService.count(filters);
        if (count == 0) {
            //noinspection unchecked
            return new ApiResponse<>(new ListBody<CatalogValueDto>(Collections.EMPTY_LIST, 0, 0), null);
        }

        if (pageable.isPaged()) {
            return new ApiResponse<>(new ListBody<>(catalogValueService.getList(filters,
                    pageable.getPageNumber() * pageable.getPageSize(), pageable.getPageSize(), params.getFirst("sort")),
                    pageable.getPageNumber(), count), null);
        }
        return new ApiResponse<>(new ListBody<>(catalogValueService.getList(filters, 0, 0, params.getFirst("sort"))),
                null);
    }

    @Override
    @Transactional
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        CatalogValueDto catalogValueDto = objectMapper.convertValue(body, CatalogValueDto.class);
        catalogValueService.save(catalogValueDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
