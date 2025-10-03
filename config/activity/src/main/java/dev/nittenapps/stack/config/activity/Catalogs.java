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
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.config.domain.Catalog;
import dev.nittenapps.stack.config.dto.CatalogDto;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.service.CatalogService;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("configCatalogs")
@PreAuthorize("hasRole('config')")
public class Catalogs extends AbstractActivity<Catalog, UUID, CatalogListDto, CatalogDto> {
    public Catalogs(CatalogService catalogService) {
        super(catalogService);
    }

    @Override
    public ApiResponse<ObjectBody<?>> getObject(@NonNull UUID id, User user) {
        return new ApiResponse<>(new ObjectBody<>(dataService.getObject(id)), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        CatalogDto catalogDto = objectMapper.convertValue(body, CatalogDto.class);
        dataService.save(catalogDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
