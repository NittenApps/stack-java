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
import dev.nittenapps.stack.config.domain.CatalogValueAttributeValue;
import dev.nittenapps.stack.config.dto.CatalogValueDto;
import dev.nittenapps.stack.data.mapper.AbstractAttributesMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.mapstruct.*;
import org.springframework.lang.NonNull;

import java.util.Optional;

/**
 * CatalogValueMapper is an abstract class responsible for mapping between the entity class {@code CatalogValue} and its
 * corresponding data transfer object (DTO) {@code CatalogValueDto}. It uses MapStruct and additional mapping strategies
 * to perform this transformation.
 * <p>
 * This class extends {@code AbstractAttributesMapper} to leverage attribute mapping functionalities
 * between {@code CatalogValue}, its attributes, and their corresponding DTOs.
 * <p>
 * The following mapping strategies and configurations are applied:
 * <ul>
 *   <li>Mapping framework: MapStruct</li>
 *   <li>Component model: Spring-based DI</li>
 *   <li>Unmapped target policy: IGNORE</li>
 *   <li>Null value property mapping strategy: IGNORE</li>
 *   <li>Collection mapping strategy: TARGET_IMMUTABLE</li>
 * </ul>
 * <p>
 * Additional functionality provided by this class includes:
 * <ul>
 *   <li>Resolving or creating a {@code CatalogValue} entity from a DTO.</li>
 *   <li>Mapping attributes between {@code CatalogValue} and its DTO counterpart.</li>
 *   <li>Partial or full loading of {@code CatalogValue} based on provided criteria.</li>
 *   <li>Synchronization of attributes to ensure consistency during transformations.</li>
 * </ul>
 * <p>
 * Logging functionality is enabled via Lombok's {@code @Slf4j}.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE)
@Slf4j
public abstract class CatalogValueMapper
        extends AbstractAttributesMapper<CatalogValue, CatalogValueAttribute, CatalogValueAttributeValue,
        CatalogValueDto> {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Loads a {@link CatalogValue} entity based on the provided {@link CatalogValueDto}. Resolves the corresponding
     * {@link CatalogValue} by either fetching it from the database or constructing a new instance if it does not
     * already exist.
     *
     * @param catalogValueDto the DTO containing the data needed to resolve a {@link CatalogValue}. It must include
     *                        details such as catalog code and code to locate or create the appropriate
     *                        {@link CatalogValue}.
     * @return the resolved {@link CatalogValue} entity, which may be an existing entity from the database or a newly
     * constructed one. Returns {@code null} if {@code catalogValueDto} is null or if sufficient data for resolution
     * is not available.
     */
    @Named("load")
    public CatalogValue load(CatalogValueDto catalogValueDto) {
        return resolve(catalogValueDto);
    }

    /**
     * Maps a {@link CatalogValue} entity to its corresponding {@link CatalogValueDto}.
     *
     * @param catalogValue the {@link CatalogValue} entity to be converted. The entity must contain the necessary
     *                     properties, such as the catalog's code.
     * @return a {@link CatalogValueDto} representation of the given {@link CatalogValue}. The {@code catalogCode} is
     * derived from the catalog's {@code code} property, and the {@code attributes} field is ignored during mapping.
     */
    @Mapping(target = "catalogCode", source = "catalog.code")
    @Mapping(target = "attributes", ignore = true)
    public abstract CatalogValueDto toDto(CatalogValue catalogValue);

    /**
     * Converts a {@link CatalogValue} entity into its corresponding {@link CatalogValueDto} representation with
     * detailed mappings of attributes and catalog code.
     *
     * @param catalogValue the {@link CatalogValue} entity to be transformed. Must include the catalog and its
     *                     attributes to enable proper mapping.
     * @return a fully mapped {@link CatalogValueDto}, which includes the catalog code derived from the catalog entity
     * and a detailed representation of attributes.
     */
    @Named("fullDto")
    @Mapping(target = "catalogCode", source = "catalog.code")
    @Mapping(target = "attributes", expression = "java(toAttributesDto(catalogValue.getAttributes()))")
    public abstract CatalogValueDto toFullDto(CatalogValue catalogValue);

    /**
     * Maps a {@link CatalogValueDto} to a {@link CatalogValue} entity while ignoring specific fields.
     *
     * @param catalogValueDto the {@link CatalogValueDto} to be transformed into a {@link CatalogValue} entity. The DTO
     *                        may contain fields that will be ignored during the transformation, as specified by the
     *                        mapping annotations in this method.
     * @return a {@link CatalogValue} entity constructed from the provided {@link CatalogValueDto}, with certain fields
     * such as {@code id}, {@code catalog}, {@code code}, {@code name}, {@code description}, {@code active}, and
     * {@code attributes} explicitly ignored.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "catalog", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "attributes", ignore = true)
    public abstract CatalogValue toEntity(CatalogValueDto catalogValueDto);

    /**
     * Maps a {@link CatalogValueDto} object to a {@link CatalogValue} entity while ignoring the {@code id} field.
     *
     * @param catalogValueDto the {@link CatalogValueDto} instance to be transformed into a {@link CatalogValue} entity.
     *                        The input DTO contains the data required for creating or updating a {@link CatalogValue}.
     * @return a {@link CatalogValue} entity constructed from the provided {@link CatalogValueDto}, with the {@code id}
     * field explicitly ignored during the mapping process.
     */
    @Named("full")
    @Mapping(target = "id", ignore = true)
    public abstract CatalogValue toFullEntity(CatalogValueDto catalogValueDto);

    /**
     * Updates an existing {@link CatalogValue} entity with values provided in a {@link CatalogValueDto}. This method
     * maps properties from the DTO to the target entity while considering the mapping definitions and transformations
     * specified within the implementation.
     *
     * @param catalogValueDto the source {@link CatalogValueDto} containing the updated data to be applied to the target
     *                        {@link CatalogValue} entity. The DTO must provide the necessary data for updating the
     *                        target entity.
     * @param catalogValue    the target {@link CatalogValue} entity to be updated. The method will update the fields of
     *                        this entity based on the values from the provided DTO.
     */
    @Named("update")
    public abstract void updateCatalogValueFromDto(CatalogValueDto catalogValueDto,
                                                   @MappingTarget CatalogValue catalogValue);

    /**
     * Synchronizes the attributes of a {@link CatalogValueDto} with a corresponding {@link CatalogValue} entity. This
     * method ensures that the attributes in the DTO are properly mapped and linked to the target entity, creating or
     * updating attribute structures if necessary.
     *
     * @param catalogValueDto the {@link CatalogValueDto} containing the source attributes to be synchronized.
     *                        It should provide the necessary data to align with the target {@link CatalogValue}.
     * @param catalogValue    the target {@link CatalogValue} entity to which the attributes will be synchronized.
     *                        This entity is modified in place to reflect the attribute mappings from the DTO.
     */
    @AfterMapping
    protected void syncAttributes(@NonNull CatalogValueDto catalogValueDto,
                                  @NonNull @MappingTarget CatalogValue catalogValue) {
        syncAttributes(catalogValueDto.getAttributes(), catalogValue.getAttributes(), CatalogValueAttribute::new,
                CatalogValueAttributeValue::new, (attribute, value) -> {
                    attribute.setParent(catalogValue);
                    value.setAttribute(attribute);
                });
    }

    /**
     * Resolves a {@link CatalogValue} entity from the provided {@link CatalogValueDto}. This method attempts to find
     * the corresponding {@link CatalogValue} based on the DTO's identifier or unique attributes such as catalog code
     * and code. If the entity cannot be found, it attempts to create a new instance if sufficient information is
     * provided.
     *
     * @param catalogValueDto the data transfer object containing the details needed to resolve or create a
     *                        {@link CatalogValue} instance.
     * @return the resolved {@link CatalogValue} entity, a newly created instance if applicable, or null if neither
     * resolution nor creation is possible.
     */
    @ObjectFactory
    protected CatalogValue resolve(CatalogValueDto catalogValueDto) {
        if (catalogValueDto == null) {
            return null;
        }

        log.debug("Resolving catalog value {}", catalogValueDto);
        CatalogValue cv = Optional.ofNullable(catalogValueDto.getId())
                .map(id -> entityManager.getReference(CatalogValue.class, id))
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
        if (cv != null) {
            parentId = cv.getId();
        }
        return cv;
    }
}
