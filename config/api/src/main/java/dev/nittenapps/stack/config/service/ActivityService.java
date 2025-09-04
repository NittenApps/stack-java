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

package dev.nittenapps.stack.config.service;

import dev.nittenapps.stack.config.domain.Activity;
import dev.nittenapps.stack.config.dto.ActivityDto;
import dev.nittenapps.stack.config.dto.ActivityListDto;
import dev.nittenapps.stack.data.service.DataService;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ActivityService extends DataService<Activity, UUID, ActivityListDto, ActivityDto> {
    List<Map<String, Object>> getFieldGroups(@NonNull String activityCode);
}
