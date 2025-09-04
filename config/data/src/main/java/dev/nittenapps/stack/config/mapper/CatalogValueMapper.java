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

package dev.nittenapps.stack.config.mapper;

import dev.nittenapps.stack.config.domain.Catalog;
import dev.nittenapps.stack.config.domain.CatalogValue;
import dev.nittenapps.stack.config.domain.CatalogValueAttribute;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.data.domain.AttributeId;
import dev.nittenapps.stack.data.mapper.AbstractAttributesMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
@Slf4j
public abstract class CatalogValueMapper
        extends AbstractAttributesMapper<CatalogValue, CatalogValueAttribute, CatalogValueDto> {
    private EntityManager entityManager;

    @Autowired
    protected final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Mapping(target = "catalogCode", source = "catalog.code")
    @Mapping(target = "attributes", ignore = true)
    public abstract CatalogValueDto toDto(CatalogValue catalogValue);

    @Named("fullDto")
    @Mapping(target = "catalogCode", source = "catalog.code")
    public abstract CatalogValueDto toFullDto(CatalogValue catalogValue);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "catalog", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    public abstract CatalogValue toEntity(CatalogValueDto catalogValueDto);

    @Mapping(target = "id", ignore = true)
    @Named("full")
    public abstract CatalogValue toFullEntity(CatalogValueDto catalogValueDto);

    @ObjectFactory
    protected CatalogValue resolve(CatalogValueDto catalogValueDto) {
        if (catalogValueDto == null) {
            return null;
        }

        log.debug("Resolving catalog value {}", catalogValueDto);
        return Optional.ofNullable(catalogValueDto.getId())
                .map(id -> entityManager.find(CatalogValue.class, id))
                .orElseGet(() -> {
                    //noinspection unchecked
                    Query<CatalogValue> query = entityManager.createQuery("SELECT v "
                                    + "FROM CatalogValue v "
                                    + "WHERE v.catalog.code = :catalogCode AND v.code = :code")
                            .setParameter("catalogCode", catalogValueDto.getCatalogCode())
                            .setParameter("code", catalogValueDto.getCode())
                            .unwrap(Query.class);
                    try {
                        return query.getSingleResult();
                    } catch (NoResultException ex) {
                        if (StringUtils.isNotBlank(catalogValueDto.getName())) {
                            //noinspection unchecked
                            Query<Catalog> catalogQuery = entityManager.createQuery("SELECT c "
                                            + "FROM Catalog c "
                                            + "WHERE c.code = :code")
                                    .setParameter("code", catalogValueDto.getCatalogCode())
                                    .unwrap(Query.class);
                            try {
                                Catalog catalog = catalogQuery.getSingleResult();
                                CatalogValue catalogValue = new CatalogValue();
                                catalogValue.setCatalog(catalog);
                                catalogValue.setCode(catalogValueDto.getCode());
                                catalogValue.setName(catalogValueDto.getName());
                                catalogValue.setActive(true);
                                return catalogValue;
                            } catch (NoResultException ex1) {
                                return null;
                            }
                        }
                        return new CatalogValue();
                    }
                });
    }

    @Override
    @NonNull
    protected CatalogValueAttribute resolveAttribute(@NonNull String code) {
        CatalogValueAttribute attribute = null;
        if (parentId != null) {
            attribute = entityManager.find(CatalogValueAttribute.class, new AttributeId(parentId, code));
        }
        return Objects.requireNonNullElseGet(attribute,
                () -> new CatalogValueAttribute(new AttributeId(null, code), null, "XX"));
    }
}
