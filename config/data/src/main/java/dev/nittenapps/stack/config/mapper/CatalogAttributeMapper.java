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

package dev.nittenapps.stack.config.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nittenapps.stack.config.domain.CatalogAttribute;
import dev.nittenapps.stack.config.dto.CatalogAttributeDto;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CatalogAttributeMapper {
    protected ObjectMapper objectMapper;

    @Autowired
    protected final void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public abstract CatalogAttributeDto toDto(CatalogAttribute catalogAttribute);

    public abstract CatalogAttribute toEntity(CatalogAttributeDto catalogAttributeDto);

    protected Map<String, Object> mapDefinition(String definition) {
        if (StringUtils.isBlank(definition)) {
            return null;
        }

        try {
            //noinspection unchecked
            return objectMapper.readValue(definition, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected String mapDefinition(Map<String, Object> definition) {
        if (MapUtils.isEmpty(definition)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(definition);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
