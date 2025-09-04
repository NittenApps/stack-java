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

import dev.nittenapps.stack.config.domain.Module;
import dev.nittenapps.stack.config.dto.ModuleDto;
import dev.nittenapps.stack.config.dto.ModuleListDto;
import dev.nittenapps.stack.data.service.DataService;

import java.util.UUID;

public interface ModuleService extends DataService<Module, UUID, ModuleListDto, ModuleDto> {
}
