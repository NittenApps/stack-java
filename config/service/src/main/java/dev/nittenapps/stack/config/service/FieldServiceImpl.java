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

import dev.nittenapps.stack.config.domain.Field;
import dev.nittenapps.stack.config.dto.FieldDto;
import dev.nittenapps.stack.config.dto.FieldListDto;
import dev.nittenapps.stack.config.mapper.FieldMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldServiceImpl extends AbstractComponentService<Field, FieldListDto, FieldDto> implements FieldService {
    private final FieldMapper fieldMapper;

    @Override
    public FieldDto getObject(@NonNull UUID id) {
        return fieldMapper.toDto(Optional.ofNullable(findById(id)).orElseThrow());
    }

    @Override
    @Transactional
    public Field save(@NonNull FieldDto fieldDto) {
        log.debug("Saving field: {}", fieldDto);
        Field field = fieldMapper.toEntity(fieldDto);

        if (field.isNew()) {
            entityManager.persist(field);
            return field;
        }
        return entityManager.merge(field);
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.FieldListDto(e.id,e.code,e.name,e.type,e.description,
                        e.active)
                FROM Field e
                """;
    }
}
