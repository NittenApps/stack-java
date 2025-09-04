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
import dev.nittenapps.stack.config.mapper.ModuleMapper;
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
public class ModuleServiceImpl extends AbstractComponentService<Module, ModuleListDto, ModuleDto>
        implements ModuleService {
    private final ModuleMapper moduleMapper;

    @Override
    public ModuleDto getObject(@NonNull UUID id) {
        String jpql = """
                SELECT m
                FROM Module m LEFT JOIN FETCH m.activities a LEFT JOIN FETCH a.child
                WHERE m.id = :id
                """;
        //noinspection unchecked
        Query<Module> query = entityManager.createQuery(jpql).setParameter("id", id).unwrap(Query.class);
        return moduleMapper.toDto(query.getSingleResult());
    }

    @Override
    @Transactional
    public Module save(@NonNull ModuleDto moduleDto) {
        log.debug("Saving module: {}", moduleDto);
        Module module = moduleMapper.toEntity(moduleDto);

        if (module.isNew()) {
            entityManager.persist(module);
            return module;
        }
        return entityManager.merge(module);
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.ModuleListDto(e.id,e.code,e.name,e.type,e.description,
                        e.active)
                FROM Module e
                """;
    }
}
