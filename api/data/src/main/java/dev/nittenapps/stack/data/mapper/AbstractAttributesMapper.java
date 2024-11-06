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

package dev.nittenapps.stack.data.mapper;

import dev.nittenapps.stack.data.domain.AbstractAttribute;
import dev.nittenapps.stack.data.domain.AttributeValue;
import dev.nittenapps.stack.data.domain.WithAttributes;
import dev.nittenapps.stack.data.dto.AttributeValueDto;
import dev.nittenapps.stack.data.dto.WithAttributesDto;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class AbstractAttributesMapper<T extends WithAttributes<A>, A extends AbstractAttribute,
        D extends WithAttributesDto> {
    protected UUID parentId;

    protected Map<String, List<AttributeValueDto>> mapAttributesDto(Map<String, A> attributes) {
        if (MapUtils.isEmpty(attributes)) {
            return null;
        }
        return attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().getValues().stream()
                                .map(value -> new AttributeValueDto(value.getCodeValue(), value.getStringValue(),
                                        value.getNumberValue(), value.getDateValue(), value.getBooleanValue(),
                                        value.getTextValue(),
                                        StringUtils.isBlank(value.getCodeValue()) ? null
                                                : new AttributeValueDto.CatalogValue(value.getCodeValue(),
                                                        value.getStringValue())))
                                .toList()));
    }

    protected Map<String, A> mapAttributes(Map<String, List<AttributeValueDto>> attributes) {
        if (MapUtils.isEmpty(attributes)) {
            return null;
        }
        return attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            A attribute = resolveAttribute(entry.getKey());
                            attribute.getValues().clear();
                            attribute.getValues().addAll(entry.getValue()
                                    .stream().map(value -> new AttributeValue(
                                            value.getCatalogValue() == null ? value.getCodeValue()
                                                    : value.getCatalogValue().code,
                                            value.getCatalogValue() == null ? value.getStringValue()
                                                    : value.getCatalogValue().name,
                                            value.getNumberValue(), value.getDateValue(), value.getBooleanValue(),
                                            value.getTextValue()))
                                    .toList());
                            return attribute;
                        }));
    }

    @BeforeMapping
    protected void beforeMapping(D dto) {
        if (dto == null) {
            return;
        }
        this.parentId = dto.getId();
    }

    @AfterMapping
    protected void linkAttributes(@NonNull @MappingTarget T target) {
        if (target.getAttributes() == null) {
            return;
        }
        target.getAttributes().values().forEach(attribute -> {
            assert attribute.getId() != null;
            attribute.getId().setParentId(target.getId());
            attribute.setParent(target);
        });
    }

    @NonNull
    protected abstract A resolveAttribute(@NonNull String code);
}
