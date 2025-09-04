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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nittenapps.stack.config.domain.Activity;
import dev.nittenapps.stack.config.domain.FieldGroup;
import dev.nittenapps.stack.config.dto.ActivityDto;
import dev.nittenapps.stack.config.dto.ActivityListDto;
import dev.nittenapps.stack.config.mapper.ActivityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl extends AbstractComponentService<Activity, ActivityListDto, ActivityDto>
        implements ActivityService {
    private final ActivityMapper activityMapper;

    private final ObjectMapper objectMapper;

    @Override
    public List<Map<String, Object>> getFieldGroups(@NonNull String activityCode) {
        String jpql = """
                SELECT fg.id
                FROM Activity a JOIN a.fieldGroups fgs JOIN fgs.child fg
                WHERE a.code=:activityCode AND a.active AND fg.active
                ORDER BY fgs.position
                """;
        //noinspection unchecked
        Query<UUID> idsQuery = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("activityCode", activityCode);

        List<UUID> ids = idsQuery.getResultList();
        jpql = """
                SELECT fg
                FROM FieldGroup fg JOIN FETCH fg.fields fs JOIN FETCH fs.child f LEFT JOIN FETCH fg.editorRoles
                    LEFT JOIN FETCH fg.viewerRoles LEFT JOIN FETCH f.editorRoles LEFT JOIN FETCH f.viewerRoles
                    LEFT JOIN FETCH fs.editorRoles LEFT JOIN FETCH fs.viewerRoles
                WHERE fg.id IN (:ids) AND f.active
                """;
        //noinspection unchecked
        Query<FieldGroup> query = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("ids", ids);

        List<Map<String, Object>> result = new ArrayList<>(query.getResultList().stream()
                .map(fg -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", fg.getId());
                    map.put("name", fg.getName());
                    if (fg.getDefinition() != null) {
                        try {
                            map.put("definition",
                                    objectMapper.readValue(fg.getDefinition(), Map.class));
                        } catch (JsonProcessingException e) {
                            log.warn("Error reading definition", e);
                        }
                    }
                    map.put("fields", fg.getFields().stream().map(field -> {
                        Map<String, Object> fieldMap = new HashMap<>();
                        fieldMap.put("code", field.getChild().getCode());
                        fieldMap.put("name", field.getChild().getName());
                        fieldMap.put("type", field.getChild().getType());
                        if (field.getChild().getDefinition() != null) {
                            try {
                                fieldMap.put("definition",
                                        objectMapper.readValue(field.getChild().getDefinition(), Map.class));
                            } catch (JsonProcessingException e) {
                                log.warn("Error reading definition", e);
                            }
                        }
                        fieldMap.put("editorRoles", field.getEditorRoles());
                        fieldMap.put("viewerRoles", field.getViewerRoles());
                        return fieldMap;
                    }).collect(Collectors.toList()));
                    return map;
                })
                .toList());
        result.sort(Comparator.comparingInt(value -> ids.indexOf((UUID)value.get("id"))));
        return result;
    }

    @Override
    public ActivityDto getObject(@NonNull UUID id) {
        String jpql = """
                SELECT a
                FROM Activity a LEFT JOIN FETCH a.fieldGroups fg LEFT JOIN FETCH fg.child
                WHERE a.id = :id
                """;
        //noinspection unchecked
        Query<Activity> query = entityManager.createQuery(jpql).setParameter("id", id).unwrap(Query.class);
        return activityMapper.toDto(query.getSingleResult());
    }

    @Override
    @Transactional
    public Activity save(@NonNull ActivityDto activityDto) {
        log.debug("Saving activity: {}", activityDto);
        Activity activity = activityMapper.toEntity(activityDto);

        if (activity.isNew()) {
            entityManager.persist(activity);
            return activity;
        }
        return entityManager.merge(activity);
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.ActivityListDto(e.id,e.code,e.name,e.type,e.description,
                        e.active)
                FROM Activity e
                """;
    }
}
