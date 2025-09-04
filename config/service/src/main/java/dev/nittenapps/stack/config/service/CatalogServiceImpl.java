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

import dev.nittenapps.stack.config.domain.Catalog;
import dev.nittenapps.stack.config.dto.CatalogDto;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.mapper.CatalogMapper;
import dev.nittenapps.stack.data.service.AbstractDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogServiceImpl extends AbstractDataService<Catalog, UUID, CatalogListDto, CatalogDto>
        implements CatalogService {
    private final CatalogMapper catalogMapper;

    @Override
    public CatalogDto getObject(@NonNull UUID id) {
        String jpql = """
                SELECT c
                FROM Catalog c LEFT JOIN FETCH c.attributes
                WHERE c.id = :id
                """;
        //noinspection unchecked
        Query<Catalog> query = entityManager.createQuery(jpql)
                .setParameter("id", id)
                .unwrap(Query.class);
        return catalogMapper.toDto(query.getSingleResult());
    }

    @Override
    @Transactional
    public Catalog save(@NonNull CatalogDto catalogDto) {
        log.debug("Saving catalog: {}", catalogDto);
        Catalog catalog = catalogMapper.toEntity(catalogDto);
        if (catalog.getAttributes() != null) {
            for (int i = 0; i < catalog.getAttributes().size(); i++) {
                catalog.getAttributes().get(i).setPosition(i);
            }
        }

        if (catalog.isNew()) {
            entityManager.persist(catalog);
            return catalog;
        }
        return entityManager.merge(catalog);
    }

    @Override
    protected void bindParams(Query<?> query, @NonNull Map<String, Object> filters) {
        Optional.ofNullable(MapUtils.getString(filters, "code"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> query.setParameter("code", code));
        Optional.ofNullable(MapUtils.getString(filters, "name"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(name -> query.setParameter("name", name.toLowerCase()));
    }

    @Override
    protected String buildJpqlRestrictions(@NonNull Map<String, Object> filters) {
        StringBuilder restrictions = new StringBuilder();
        Optional.ofNullable(MapUtils.getString(filters, "code"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> restrictions.append(" WHERE e.code LIKE :code"));
        Optional.ofNullable(MapUtils.getString(filters, "name"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(name -> {
                    if (restrictions.isEmpty()) {
                        restrictions.append(" WHERE ");
                    } else {
                        restrictions.append(" AND ");
                    }
                    restrictions.append("LOWER(e.name) LIKE :name");
                });
        return restrictions.toString();
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.CatalogListDto(e.id,e.code,e.name,e.description,e.active)
                FROM Catalog e
                """;
    }
}
