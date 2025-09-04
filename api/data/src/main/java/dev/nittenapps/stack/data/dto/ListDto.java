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

package dev.nittenapps.stack.data.dto;

import java.io.Serializable;

/**
 * ListDto serves as a generic marker interface for data transfer objects (DTOs) that represent collections or lists  of
 * identifiable entities. It extends the {@link BaseDto} interface, inheriting the capability to handle a unique
 * identifier, and is intended to be subclassed by specific DTO implementations.
 *
 * @param <ID> the type of the identifier for the DTO, which must implement {@link Serializable}
 */
public interface ListDto<ID extends Serializable> extends BaseDto<ID> {
}
