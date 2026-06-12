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
import dev.nittenapps.stack.config.domain.CatalogValue;
import dev.nittenapps.stack.config.domain.CatalogValueAttributeValue;
import dev.nittenapps.stack.config.dto.CatalogListDto;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.mapper.CatalogMapper;
import dev.nittenapps.stack.config.mapper.CatalogValueMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.Strings;
import org.hibernate.query.Query;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    private final CatalogMapper catalogMapper;

    private final CatalogValueMapper catalogValueMapper;

    private final EntityManager entityManager;

    @Override
    public Optional<Catalog> getCatalog(@NonNull String code) {
        String jpql = """
                SELECT c FROM Catalog c WHERE c.code = :code
                """;
        //noinspection unchecked
        Query<Catalog> query = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("code", code);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CatalogListDto> getCatalogs() {
        String jpql = """
                SELECT c
                FROM Catalog c
                WHERE c.active
                ORDER BY c.code
                """;
        //noinspection unchecked
        Query<Catalog> query = entityManager.createQuery(jpql).unwrap(Query.class);
        return query.getResultList().stream()
                .map(catalogMapper::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CatalogValue> getCatalogValue(@NonNull UUID id) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv INNER JOIN FETCH cv.catalog LEFT JOIN FETCH cv.attributes cvas
                        LEFT JOIN FETCH cvas.values cvavs
                WHERE cv.id = :id
                """;
        //noinspection unchecked
        Query<CatalogValue> query = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code) {
        return getCatalogValue(catalogCode, code, false);
    }

    @Override
    public Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code,
                                                  boolean ignoreActive) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv INNER JOIN FETCH cv.catalog LEFT JOIN FETCH cv.attributes cvas
                        LEFT JOIN FETCH cvas.values cvavs
                WHERE cv.catalog.code=:catalogCode AND cv.code=:code
                """;
        if (!ignoreActive) {
            jpql += " AND cv.catalog.active AND cv.active";
        }
        //noinspection unchecked
        Query<CatalogValue> query = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("catalogCode", catalogCode)
                .setParameter("code", code);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<CatalogValue> getCatalogValueByName(@NonNull String catalogCode, @NonNull String name,
                                                        String jpqlRestrictions) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv INNER JOIN FETCH cv.catalog LEFT JOIN FETCH cv.attributes cvas
                        LEFT JOIN FETCH cvas.values cvavs
                WHERE cv.catalog.code=:catalogCode AND cv.name=:name AND cv.catalog.active AND cv.active
                """;
        if (StringUtils.isNotBlank(jpqlRestrictions)) {
            jpql += jpqlRestrictions;
        }
        //noinspection unchecked,SqlSourceToSinkFlow
        Query<CatalogValue> query = entityManager.createQuery(jpql).unwrap(Query.class)
                .setParameter("catalogCode", catalogCode)
                .setParameter("name", name);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NonUniqueResultException | NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CatalogValueDto> getCatalogValues(@NonNull String catalogCode, MultiValueMap<String, String> filters) {
        StringBuilder jpql = new StringBuilder("""
                SELECT cv
                FROM CatalogValue cv JOIN FETCH cv.catalog LEFT JOIN FETCH cv.attributes cvas
                    LEFT JOIN FETCH cvas.values
                WHERE cv.id IN (
                    SELECT cv1.id
                    FROM CatalogValue cv1 INNER JOIN cv1.catalog c
                """);
        if (MapUtils.isNotEmpty(filters)) {
            int i = 0;
            for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                if (Strings.CS.equalsAny(entry.getKey(), "code", "term")) {
                    continue;
                }
                jpql.append(" INNER JOIN cv1.attributes cvas").append(i);
                jpql.append(" ON KEY(cvas").append(i).append(")=:").append(entry.getKey());
                jpql.append(" INNER JOIN cvas").append(i).append(".values cvavs").append(i);
                if (entry.getValue().size() > 1) {
                    jpql.append(" ON cvavs").append(i).append(".codeValue IN (:").append(entry.getKey())
                            .append("Values)");
                } else {
                    jpql.append(" ON cvavs").append(i).append(".codeValue=:").append(entry.getKey()).append("Value");
                }
                i++;
            }
        }
        jpql.append(" WHERE c.code=:catalogCode AND cv1.active)");
        if (filters.containsKey("term")) {
            jpql.append((" AND LOWER(cv.code) LIKE :term OR LOWER(cv.name) LIKE :term"));
        }
        if (filters.containsKey("code")) {
            jpql.append((" AND cv.code LIKE :code"));
        }
        jpql.append(" ORDER BY cv.code");
        log.trace(jpql.toString());
        //noinspection unchecked
        Query<CatalogValue> query = entityManager.createQuery(jpql.toString())
                //.setHint("org.hibernate.cacheable", Boolean.TRUE)
                .unwrap(Query.class)
                .setParameter("catalogCode", catalogCode);

        if (MapUtils.isNotEmpty(filters)) {
            for (Map.Entry<String, List<String>> entry : filters.entrySet()) {
                if ("term".equals(entry.getKey())) {
                    query.setParameter("term", entry.getValue().getFirst());
                    continue;
                }
                if ("code".equals(entry.getKey())) {
                    query.setParameter("code", entry.getValue().getFirst());
                    continue;
                }
                query.setParameter(entry.getKey(), entry.getKey());
                if (entry.getValue().size() > 1) {
                    query.setParameter(entry.getKey() + "Values", entry.getValue());
                } else {
                    query.setParameter(entry.getKey() + "Value", entry.getValue().getFirst());
                }
            }
        }

        return query.getResultList().stream()
                .map(catalogValueMapper::toFullDto)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "parameters", key = "#profile + ':' + #code")
    public CatalogValueAttributeValue getParameterValue(@NonNull String profile, @NonNull String code) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv LEFT JOIN FETCH cv.attributes cvas LEFT JOIN FETCH cvas.values
                WHERE cv.catalog.code='__PARAMETERS__' AND cv.code=:profile AND KEY(cvas)=:code
                """;
        //noinspection unchecked
        Query<CatalogValue> query = entityManager.createQuery(jpql)
                .unwrap(Query.class)
                .setParameter("code", code)
                .setParameter("profile", profile);
        return query.getResultList().getFirst().getAttributes().get(code)
                .getValues().stream().findFirst().orElse(null);
    }

    @Override
    @Transactional
    public Catalog saveCatalog(@NonNull Catalog catalog) {
        log.debug("Saving catalog: {}", catalog);
        if (catalog.isNew()) {
            entityManager.persist(catalog);
            log.debug("Created new catalog: {}", catalog);
            return catalog;
        }
        return entityManager.merge(catalog);
    }

    @Override
    @Transactional
    public CatalogValue saveCatalogValue(@NonNull CatalogValueDto catalogValueDto) {
        log.debug("Saving catalog value: {}", catalogValueDto);
        return saveCatalogValue(catalogValueMapper.toFullEntity(catalogValueDto));
    }

    @Override
    @Transactional
    public CatalogValue saveCatalogValue(@NonNull CatalogValue catalogValue) {
        log.debug("Saving catalog value with attributes: {}", catalogValue.getAttributes());
        if (catalogValue.isNew()) {
            entityManager.persist(catalogValue);
            log.debug("Created new catalog value {}", catalogValue);
            return catalogValue;
        }
        return entityManager.merge(catalogValue);
    }
}
