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

import dev.nittenapps.stack.config.domain.FieldGroup;
import dev.nittenapps.stack.config.dto.FieldGroupDto;
import dev.nittenapps.stack.config.dto.FieldGroupListDto;
import dev.nittenapps.stack.config.mapper.FieldGroupMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldGroupServiceImpl extends AbstractComponentService<FieldGroup, FieldGroupListDto, FieldGroupDto>
        implements FieldGroupService {
    private final FieldGroupMapper fieldGroupMapper;

    @Override
    public FieldGroupDto getObject(@NonNull UUID id) {
        String jpql = """
                SELECT fg
                FROM FieldGroup fg LEFT JOIN FETCH fg.fields f LEFT JOIN FETCH f.child
                WHERE fg.id = :id
                """;
        //noinspection unchecked
        Query<FieldGroup> query = entityManager.createQuery(jpql).setParameter("id", id).unwrap(Query.class);
        return fieldGroupMapper.toDto(query.getSingleResult());
    }

    @Override
    @Transactional
    public FieldGroup save(@NonNull FieldGroupDto fieldGroupDto) {
        log.debug("Saving fieldGroup: {}", fieldGroupDto);
        FieldGroup fieldGroup = fieldGroupMapper.toEntity(fieldGroupDto);

        if (fieldGroup.isNew()) {
            entityManager.persist(fieldGroup);
            return fieldGroup;
        }
        return entityManager.merge(fieldGroup);
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.FieldGroupListDto(e.id,e.code,e.name,e.type,e.description,
                        e.active)
                FROM FieldGroup e
                """;
    }
}
