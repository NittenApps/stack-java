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

import dev.nittenapps.stack.config.domain.ComponentChild;
import dev.nittenapps.stack.config.domain.ComponentChildId;
import dev.nittenapps.stack.config.domain.FieldGroup;
import dev.nittenapps.stack.config.dto.FieldGroupDto;
import dev.nittenapps.stack.config.dto.FieldGroupListDto;
import dev.nittenapps.stack.config.dto.FieldListDto;
import dev.nittenapps.stack.data.dto.SimpleIdDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class FieldGroupMapper extends AbstractComponentMapper<FieldGroup, FieldGroupListDto, FieldGroupDto> {
    protected FieldMapper fieldMapper;

    @Autowired
    protected final void setFieldMapper(FieldMapper fieldMapper) {
        this.fieldMapper = fieldMapper;
    }

    protected List<FieldListDto> mapFields(Set<ComponentChild> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        return fields.stream().map(child -> fieldMapper.toListDto(child.getChild()))
                .collect(Collectors.toList());
    }

    protected Set<ComponentChild> mapFieldsDto(List<FieldListDto> fields) {
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        AtomicInteger index = new AtomicInteger(0);
        return fields.stream().map(field -> {
                    ComponentChild child = resolveComponentChild(componentId, field.getId());
                    child.setChild(fieldMapper.toEntity((SimpleIdDto)field));
                    child.setPosition(index.getAndIncrement());
                    log.debug("child: {}, field: {}", child, field);
                    return child;
                })
                .collect(Collectors.toSet());
    }

    @AfterMapping
    protected void linkChildren(@NonNull @MappingTarget FieldGroup fieldGroup) {
        if (fieldGroup.getFields() == null) {
            return;
        }

        fieldGroup.getFields().forEach(field -> {
            if (field.getId() == null) {
                field.setId(new ComponentChildId());
            }
            field.getId().setComponentId(fieldGroup.getId());
            field.getId().setChildId(field.getChild().getId());
            field.setComponent(fieldGroup);
        });
    }
}
