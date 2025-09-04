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
import dev.nittenapps.stack.config.domain.Module;
import dev.nittenapps.stack.config.dto.ActivityListDto;
import dev.nittenapps.stack.config.dto.ModuleDto;
import dev.nittenapps.stack.config.dto.ModuleListDto;
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
public abstract class ModuleMapper extends AbstractComponentMapper<Module, ModuleListDto, ModuleDto> {
    protected ActivityMapper activityMapper;

    @Autowired
    protected final void setActivityMapper(ActivityMapper activityMapper) {
        this.activityMapper = activityMapper;
    }

    protected List<ActivityListDto> mapActivities(Set<ComponentChild> activities) {
        if (CollectionUtils.isEmpty(activities)) {
            return null;
        }

        return activities.stream().map(child -> activityMapper.toListDto(child.getChild()))
                .collect(Collectors.toList());
    }

    protected Set<ComponentChild> mapActivitiesDto(List<ActivityListDto> activities) {
        if (CollectionUtils.isEmpty(activities)) {
            return null;
        }

        AtomicInteger index = new AtomicInteger(0);
        return activities.stream().map(activity -> {
                    ComponentChild child = resolveComponentChild(componentId, activity.getId());
                    child.setChild(activityMapper.toEntity((SimpleIdDto)activity));
                    child.setPosition(index.getAndIncrement());
                    log.debug("child: {}, activity: {}", child, activity);
                    return child;
                })
                .collect(Collectors.toSet());
    }

    @AfterMapping
    protected void linkChildren(@NonNull @MappingTarget Module module) {
        if (module.getActivities() == null) {
            return;
        }

        module.getActivities().forEach(activity -> {
            if (activity.getId() == null) {
                activity.setId(new ComponentChildId());
            }
            activity.getId().setComponentId(module.getId());
            activity.getId().setChildId(activity.getChild().getId());
            activity.setComponent(module);
        });
    }
}
