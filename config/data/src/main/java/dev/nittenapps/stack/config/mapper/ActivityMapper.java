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

import dev.nittenapps.stack.config.domain.Activity;
import dev.nittenapps.stack.config.domain.ComponentChild;
import dev.nittenapps.stack.config.domain.ComponentChildId;
import dev.nittenapps.stack.config.dto.ActivityDto;
import dev.nittenapps.stack.config.dto.ActivityListDto;
import dev.nittenapps.stack.config.dto.FieldGroupListDto;
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
public abstract class ActivityMapper extends AbstractComponentMapper<Activity, ActivityListDto, ActivityDto> {
    protected FieldGroupMapper fieldGroupMapper;

    @Autowired
    protected final void setFieldGroupMapper(FieldGroupMapper fieldGroupMapper) {
        this.fieldGroupMapper = fieldGroupMapper;
    }

    protected List<FieldGroupListDto> mapFieldGroups(Set<ComponentChild> fieldGroups) {
        if (CollectionUtils.isEmpty(fieldGroups)) {
            return null;
        }

        return fieldGroups.stream().map(child -> fieldGroupMapper.toListDto(child.getChild()))
                .collect(Collectors.toList());
    }

    protected Set<ComponentChild> mapFieldGroupsDto(List<FieldGroupListDto> fieldGroups) {
        if (CollectionUtils.isEmpty(fieldGroups)) {
            return null;
        }

        AtomicInteger index = new AtomicInteger(0);
        return fieldGroups.stream().map(fieldGroup -> {
                    ComponentChild child = resolveComponentChild(componentId, fieldGroup.getId());
                    child.setChild(fieldGroupMapper.toEntity((SimpleIdDto)fieldGroup));
                    child.setPosition(index.getAndIncrement());
                    log.debug("child: {}, fieldGroup: {}", child, fieldGroup);
                    return child;
                })
                .collect(Collectors.toSet());
    }

    @AfterMapping
    protected void linkChildren(@NonNull @MappingTarget Activity activity) {
        if (activity.getFieldGroups() == null) {
            return;
        }

        activity.getFieldGroups().forEach(fieldGroup -> {
            if (fieldGroup.getId() == null) {
                fieldGroup.setId(new ComponentChildId());
            }
            fieldGroup.getId().setComponentId(activity.getId());
            fieldGroup.getId().setChildId(fieldGroup.getChild().getId());
            fieldGroup.setComponent(activity);
        });
    }
}
