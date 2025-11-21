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
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.service.CatalogService;
import dev.nittenapps.stack.config.service.CatalogValueService;
import dev.nittenapps.stack.data.dto.AttributeValueDto;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Component("parametersValue")
@PreAuthorize("hasRole('admin')")
@RequiredArgsConstructor
@Slf4j
public class ParametersValue
        extends AbstractActivity<CatalogAttribute, String, Map<String, Object>, CatalogAttributeDto> {
    private final CatalogService catalogService;

    private final CatalogValueService catalogValueService;

    @Override
    public ApiResponse<ListBody<Map<String, Object>>> getList(@NonNull MultiValueMap<String, String> params,
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

            //noinspection unchecked
            List<Map<String, Object>> result = attributes.stream()
                    .map(attribute -> (Map<String, Object>)objectMapper.convertValue(attribute, Map.class))
                    .toList();
            try {
                CatalogValueDto catalogValueDto = catalogValueService.findByCatalogCodeAndCode("__PARAMETERS__",
                        "__DEFAULT__");
                for (Map<String, Object> attr : result) {
                    Optional.ofNullable(catalogValueDto.getAttributes())
                            .map(attrs -> attrs.get((String)attr.get("code")))
                            .flatMap(attribute -> attribute.stream().findFirst())
                            .ifPresent(value -> {
                                MapUtils.safeAddToMap(attr, "codeValue", value.getCodeValue());
                                MapUtils.safeAddToMap(attr, "stringValue", value.getStringValue());
                                MapUtils.safeAddToMap(attr, "numberValue", value.getNumberValue());
                                MapUtils.safeAddToMap(attr, "dateValue", value.getDateValue());
                                MapUtils.safeAddToMap(attr, "booleanValue", value.getBooleanValue());
                                MapUtils.safeAddToMap(attr, "textValue", value.getTextValue());
                            });
                }
            } catch (NoResultException ignored) {
            }

            return new ApiResponse<>(new ListBody<>(result), null);
        } catch (NoResultException e) {
            return new ApiResponse<>(new ListBody<>(), null);
        }
    }

    @Override
    public ApiResponse<ObjectBody<?>> getObject(@NonNull String id, User user) {
        CatalogDto catalogDto = catalogService.findByCode("__PARAMETERS__");
        List<CatalogAttributeDto> attributes = catalogDto.getAttributes();

        //noinspection unchecked
        Map<String, Object> result = attributes.stream()
                .filter(attribute -> id.equals(attribute.getCode()))
                .map(attribute -> (Map<String, Object>)objectMapper.convertValue(attribute, Map.class))
                .findFirst()
                .orElseThrow();
        try {
            CatalogValueDto catalogValueDto = catalogValueService.findByCatalogCodeAndCode("__PARAMETERS__",
                    "__DEFAULT__");
            log.trace("CatalogValueDto: {}", catalogValueDto);
            Optional.ofNullable(catalogValueDto.getAttributes())
                    .map(attrs -> attrs.get(id))
                    .flatMap(attribute -> attribute.stream().findFirst())
                    .ifPresent(value -> {
                        MapUtils.safeAddToMap(result, "codeValue", value.getCodeValue());
                        MapUtils.safeAddToMap(result, "stringValue", value.getStringValue());
                        MapUtils.safeAddToMap(result, "numberValue", value.getNumberValue());
                        MapUtils.safeAddToMap(result, "dateValue", value.getDateValue());
                        MapUtils.safeAddToMap(result, "booleanValue", value.getBooleanValue());
                        MapUtils.safeAddToMap(result, "textValue", value.getTextValue());
                    });
        } catch (NoResultException ignored) {
            log.trace("No default profile found");
        }

        return new ApiResponse<>(new ObjectBody<>(result), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        CatalogValueDto catalogValueDto;
        try {
            catalogValueDto = catalogValueService.findByCatalogCodeAndCode("__PARAMETERS__", "__DEFAULT__");
        } catch (NoResultException e) {
            catalogValueDto = new CatalogValueDto(null, "__PARAMETERS__", "__DEFAULT__", "Default Profile", null, null,
                    null, null, new HashMap<>());
        }
        AttributeValueDto value = objectMapper.convertValue(body, AttributeValueDto.class);
        value.setPosition(0);
        if (catalogValueDto.getAttributes() == null) {
            catalogValueDto.setAttributes(new HashMap<>());
        }
        catalogValueDto.getAttributes().put((String)body.get("code"),
                new ArrayList<>(List.of(value)));
        catalogValueService.save(catalogValueDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
