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
import dev.nittenapps.stack.config.domain.CatalogAttribute;
import dev.nittenapps.stack.config.dto.CatalogDto;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.mapper.CatalogMapper;
import dev.nittenapps.stack.data.service.AbstractDataService;
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
public class CatalogServiceImpl extends AbstractDataService<Catalog, UUID, CatalogListDto, CatalogDto>
        implements CatalogService {
    private final CatalogMapper catalogMapper;

    @Override
    public CatalogDto findByCode(@NonNull String code) {
        String jpql = """
                SELECT c
                FROM Catalog c LEFT JOIN FETCH c.attributes
                WHERE c.code = :code
                """;
        //noinspection unchecked
        Query<Catalog> query = entityManager.createQuery(jpql)
                .setParameter("code", code)
                .unwrap(Query.class);
        return catalogMapper.toDto(query.getSingleResult());
    }

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
            int i = 0;
            for (CatalogAttribute attribute : catalog.getAttributes()) {
                attribute.setPosition(i++);
            }
        }

        if (catalog.isNew()) {
            entityManager.persist(catalog);
            return catalog;
        }
        return entityManager.merge(catalog);
    }

    @Override
    protected String buildJpqlRestrictions(@NonNull Map<String, Object> filters) {
        StringBuilder restrictions = new StringBuilder(super.buildJpqlRestrictions(filters));
        if (restrictions.isEmpty()) {
            restrictions.append(" WHERE ");
        } else {
            restrictions.append(" AND ");
        }
        restrictions.append("e.code <> '__PARAMETERS__'");
        return restrictions.toString();
    }

    @Override
    protected String getBaseListJpql(Map<String, Object> filters) {
        return """
                SELECT new dev.nittenapps.stack.config.dto.CatalogListDto(e.id,e.code,e.name,e.description,e.active)
                FROM Catalog e
                """;
    }

    @Override
    protected String getFilterField(@NonNull String field) {
        if ("name".equals(field)) {
            return "LOWER(e.name)";
        }
        return super.getFilterField(field);
    }

    @Override
    protected String getFilterOperator(@NonNull String field) {
        return switch (field) {
            case "code", "name" -> "LIKE";
            default -> super.getFilterOperator(field);
        };
    }
}
