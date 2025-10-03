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
import dev.nittenapps.stack.config.domain.FieldGroup;
import dev.nittenapps.stack.config.dto.FieldGroupDto;
import dev.nittenapps.stack.config.dto.FieldGroupListDto;
import dev.nittenapps.stack.config.dto.FieldListDto;
import dev.nittenapps.stack.config.service.FieldGroupService;
import dev.nittenapps.stack.config.service.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

@Component("configFieldGroups")
@PreAuthorize("hasRole('config')")
@Slf4j
public class FieldGroups extends AbstractActivity<FieldGroup, UUID, FieldGroupListDto, FieldGroupDto> {
    private final FieldService fieldService;

    public FieldGroups(FieldService fieldService, FieldGroupService fieldGroupService) {
        super(fieldGroupService);
        this.fieldService = fieldService;
    }

    @SuppressWarnings("unused")
    public ApiResponse<ListBody<FieldListDto>> getFields(@NonNull MultiValueMap<String, String> params,
                                                         User user) {
        return new ApiResponse<>(new ListBody<>(fieldService.getList(Map.of("type", "!LB", "active", true), 0, 0,
                "code")), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> getObject(@NonNull UUID id, User user) {
        return new ApiResponse<>(new ObjectBody<>(dataService.getObject(id)), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        FieldGroupDto fieldGroupDto = objectMapper.convertValue(body, FieldGroupDto.class);
        fieldGroupDto.setType("XX");
        dataService.save(fieldGroupDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
