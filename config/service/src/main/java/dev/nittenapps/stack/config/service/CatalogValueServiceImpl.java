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
 * Copyright (c) 2025. NittenApps
 */

package dev.nittenapps.stack.config.service;

import dev.nittenapps.stack.config.domain.CatalogValue;
import dev.nittenapps.stack.config.domain.CatalogValueAttribute;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.mapper.CatalogValueMapper;
import dev.nittenapps.stack.core.domain.Revision;
import dev.nittenapps.stack.data.service.AbstractDataService;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CatalogValueServiceImpl extends AbstractDataService<CatalogValue, UUID, CatalogValueDto, CatalogValueDto>
        implements CatalogValueService {
    private final CatalogValueMapper catalogValueMapper;

    @Override
    public CatalogValueDto findByCatalogCodeAndCode(@NonNull String catalogCode, @NonNull String code) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv LEFT JOIN FETCH cv.attributes cvas LEFT JOIN FETCH cvas.values
                WHERE cv.catalog.code = :catalogCode AND cv.code = :code
                """;
        //noinspection unchecked
        Query<CatalogValue> query = entityManager.createQuery(jpql)
                .setParameter("catalogCode", catalogCode)
                .setParameter("code", code)
                .unwrap(Query.class);
        return catalogValueMapper.toFullDto(query.getSingleResult());
    }

    @Override
    public List<CatalogValueDto> getList(Map<String, Object> filters, int offset, int limit, String sort) {
        String jpql = getBaseListJpql(filters) + buildJpqlRestrictions(filters) + buildJpqlSort(sort);

        // noinspection unchecked
        @SuppressWarnings("SqlSourceToSinkFlow")
        Query<CatalogValue> query = entityManager.createQuery(jpql)
                .unwrap(Query.class);

        if (limit > 0) {
            query.setFirstResult(offset)
                    .setMaxResults(limit);
        }
        bindParams(query, filters);

        List<CatalogValueDto> values = query.getResultList().stream()
                .map(catalogValueMapper::toFullDto)
                .toList();
        for (CatalogValueDto value : values) {
            AuditQuery q1 = AuditReaderFactory.get(entityManager).createQuery()
                    .forRevisionsOfEntity(CatalogValueAttribute.class, false)
                    .setMaxResults(1)
                    .add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext())
                    .add(AuditEntity.property("id.parentId").eq(value.getId()));
            Revision revision1 = null;
            try {
                revision1 = (Revision)q1.getSingleResult();
            } catch (NoResultException ignored) {
            }

            AuditQuery q2 = AuditReaderFactory.get(entityManager).createQuery()
                    .forRevisionsOfEntity(CatalogValue.class, false)
                    .add(AuditEntity.revisionNumber().maximize().computeAggregationInInstanceContext())
                    .add(AuditEntity.id().eq(value.getId()));
            Revision revision2 = null;
            try {
                revision2 = (Revision)q2.getSingleResult();
            } catch (NoResultException ignored) {
            }

            Revision revision = revision1;
            if (revision1 != null && revision2 != null) {
                if (revision2.getRevisionDate().after(revision1.getRevisionDate())) {
                    revision = revision2;
                }
            } else if (revision2 != null) {
                revision = revision2;
            }

            if (revision != null) {
                value.setUpdatedBy(revision.getUpdatedBy());
                value.setUpdatedOn(revision.getRevisionDate().toInstant().atZone(ZoneId.systemDefault()));
            }
        }

        return values;
    }

    @Override
    public CatalogValueDto getObject(@NonNull UUID uuid) {
        return super.getObject(uuid);
    }

    @Override
    @Transactional
    public CatalogValue save(@NonNull CatalogValueDto catalogValueDto) {
        CatalogValue catalogValue = catalogValueMapper.toFullEntity(catalogValueDto);

        if (catalogValue.isNew()) {
            entityManager.persist(catalogValue);
            return catalogValue;
        }
        return entityManager.merge(catalogValue);
    }

    @Override
    protected void bindParams(Query<?> query, @NonNull Map<String, Object> filters) {
        Optional.ofNullable(MapUtils.getString(filters, "catalogId"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> query.setParameter("catalogId", UUID.fromString(code)));
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
        Optional.ofNullable(MapUtils.getString(filters, "catalogId"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> restrictions.append(" WHERE e.catalog.id = :catalogId"));
        Optional.ofNullable(MapUtils.getString(filters, "code"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(name -> {
                    if (restrictions.isEmpty()) {
                        restrictions.append(" WHERE ");
                    } else {
                        restrictions.append(" AND ");
                    }
                    restrictions.append("e.code LIKE :code");
                });
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
                SELECT e
                FROM CatalogValue e LEFT JOIN FETCH e.attributes cvas LEFT JOIN FETCH cvas.values
                """;
    }
}
