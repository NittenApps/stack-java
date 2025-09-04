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

package dev.nittenapps.stack.config.dto;

import dev.nittenapps.stack.data.dto.ListDto;
import dev.nittenapps.stack.data.dto.SimpleIdDto;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
public abstract class ComponentListDto implements SimpleIdDto, ListDto<UUID> {
    protected UUID id;
    @EqualsAndHashCode.Include protected String code;
    protected String name;
    protected String type;
    protected String description;
    protected Boolean active;
}
