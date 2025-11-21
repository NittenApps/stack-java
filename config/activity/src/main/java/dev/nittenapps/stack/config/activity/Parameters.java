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
 * Copyright (c) 2025. NittenApps
 */

package dev.nittenapps.stack.config.activity;

import dev.nittenapps.stack.activity.api.AbstractActivity;
import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.config.domain.CatalogAttribute;
import dev.nittenapps.stack.config.dto.CatalogAttributeDto;
import dev.nittenapps.stack.config.dto.CatalogDto;
import dev.nittenapps.stack.config.service.CatalogService;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("parameters")
@PreAuthorize("hasRole('config')")
@RequiredArgsConstructor
public class Parameters extends AbstractActivity<CatalogAttribute, String, CatalogAttributeDto, CatalogAttributeDto> {
    private final CatalogService catalogService;

    @Override
    public ApiResponse<ListBody<CatalogAttributeDto>> getList(@NonNull MultiValueMap<String, String> params,
                                                              User user) {
        try {
            CatalogDto catalogDto = catalogService.findByCode("__PARAMETERS__");
            List<CatalogAttributeDto> attributes = catalogDto.getAttributes();
            String code = params.getFirst("code");
            if (StringUtils.isNotBlank(code)) {
                attributes.removeIf(attribute -> !Strings.CS.contains(attribute.getCode(), code));
            }
            String name = params.getFirst("name");
            if (StringUtils.isNotBlank(name)) {
                attributes.removeIf(attribute -> !Strings.CI.contains(attribute.getName(), name));
            }
            return new ApiResponse<>(new ListBody<>(attributes), null);
        } catch (NoResultException e) {
            return new ApiResponse<>(new ListBody<>(), null);
        }
    }

    @Override
    public ApiResponse<ObjectBody<?>> getObject(@NonNull String id, User user) {
        CatalogDto catalogDto = catalogService.findByCode("__PARAMETERS__");
        List<CatalogAttributeDto> attributes = catalogDto.getAttributes();
        return new ApiResponse<>(new ObjectBody<>(attributes.stream()
                .filter(attribute -> id.equals(attribute.getCode()))
                .findFirst().orElseThrow()), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        CatalogAttributeDto attributeDto = objectMapper.convertValue(body, CatalogAttributeDto.class);

        CatalogDto catalogDto;
        try {
            catalogDto = catalogService.findByCode("__PARAMETERS__");
            catalogDto.getAttributes().removeIf(attribute -> attribute.getCode().equals(attributeDto.getCode()));
        } catch (NoResultException e) {
            catalogDto = new CatalogDto(null, "__PARAMETERS__", "Parameters", null, null, true, null,
                    new ArrayList<>());
        }
        catalogDto.getAttributes().add(attributeDto);
        catalogService.save(catalogDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
