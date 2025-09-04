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
import dev.nittenapps.stack.config.domain.Module;
import dev.nittenapps.stack.config.dto.ActivityListDto;
import dev.nittenapps.stack.config.dto.ModuleDto;
import dev.nittenapps.stack.config.dto.ModuleListDto;
import dev.nittenapps.stack.config.service.ActivityService;
import dev.nittenapps.stack.config.service.ModuleService;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

@Component("configModules")
@PreAuthorize("hasRole('config')")
public class Modules extends AbstractActivity<Module, UUID, ModuleListDto, ModuleDto> {
    private final ActivityService activityService;

    public Modules(ModuleService moduleService, ActivityService activityService) {
        super(moduleService);
        this.activityService = activityService;
    }

    @SuppressWarnings("unused")
    public ApiResponse<ListBody<ActivityListDto>> getActivities(@NonNull MultiValueMap<String, String> params,
                                                                User user) {
        return new ApiResponse<>(new ListBody<>(activityService.getList(Map.of("active", true), 0, 0, "code")), null);
    }

    @Override
    public ApiResponse<ObjectBody<ModuleDto>> getObject(@NonNull UUID id, User user) {
        return new ApiResponse<>(new ObjectBody<>(dataService.getObject(id)), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        ModuleDto moduleDto = objectMapper.convertValue(body, ModuleDto.class);
        moduleDto.setType("XX");
        dataService.save(moduleDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
