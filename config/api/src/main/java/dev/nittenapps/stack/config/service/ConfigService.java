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

package dev.nittenapps.stack.config.service;

import dev.nittenapps.stack.config.domain.Catalog;
import dev.nittenapps.stack.config.domain.CatalogValue;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConfigService {
    Optional<Catalog> getCatalog(@NonNull String code);

    List<CatalogListDto> getCatalogs();

    Optional<CatalogValue> getCatalogValue(@NonNull UUID id);

    Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code);

    Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code, boolean ignoreActive);

    Optional<CatalogValue> getCatalogValueByName(@NonNull String catalogCode, @NonNull String name,
                                                 String jpqlRestrictions);

    List<CatalogValueDto> getCatalogValues(@NonNull String catalogCode, MultiValueMap<String, String> filters);

    Catalog saveCatalog(@NonNull Catalog catalog);

    CatalogValue saveCatalogValue(@NonNull CatalogValueDto catalogValue);

    CatalogValue saveCatalogValue(@NonNull CatalogValue catalogValue);
}
