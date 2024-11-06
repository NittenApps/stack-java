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

import dev.nittenapps.stack.config.domain.CatalogValue;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.config.mapper.CatalogValueMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigServiceImpl implements ConfigService {
    private final CatalogValueMapper catalogValueMapper;

    private final EntityManager entityManager;

    @Override
    public Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code) {
        return getCatalogValue(catalogCode, code, false);
    }

    @Override
    public Optional<CatalogValue> getCatalogValue(@NonNull String catalogCode, @NonNull String code,
                                                  boolean ignoreActive) {
        String jpql = """
                SELECT cv
                FROM CatalogValue cv
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
    @Transactional
    public CatalogValue saveCatalogValue(@NonNull CatalogValueDto catalogValueDto) {
        log.debug("Saving catalog value: {}", catalogValueDto);
        CatalogValue catalogValue = catalogValueMapper.toEntity(catalogValueDto);
        if (catalogValue.isNew()) {
            entityManager.persist(catalogValue);
            log.debug("Created new catalog value {}", catalogValue);
            return catalogValue;
        }
        return entityManager.merge(catalogValue);
    }
}
