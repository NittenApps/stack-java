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
import dev.nittenapps.stack.api.ObjectBody;
import dev.nittenapps.stack.config.domain.Field;
import dev.nittenapps.stack.config.dto.FieldDto;
import dev.nittenapps.stack.config.dto.FieldListDto;
import dev.nittenapps.stack.config.service.FieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component("configFields")
@PreAuthorize("hasRole('config')")
@Slf4j
public class Fields extends AbstractActivity<Field, UUID, FieldListDto, FieldDto> {
    public Fields(FieldService fieldService) {
        super(fieldService);
    }

    @Override
    public ApiResponse<ObjectBody<?>> getObject(@NonNull UUID id, User user) {
        return new ApiResponse<>(new ObjectBody<>(dataService.getObject(id)), null);
    }

    @Override
    public ApiResponse<ObjectBody<?>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        FieldDto fieldDto = objectMapper.convertValue(body, FieldDto.class);
        dataService.save(fieldDto);

        return new ApiResponse<>(new ObjectBody<>(null), null);
    }
}
