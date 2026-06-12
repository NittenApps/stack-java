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

package dev.nittenapps.stack.data.mapper;

import dev.nittenapps.stack.data.domain.AbstractAttribute;
import dev.nittenapps.stack.data.domain.AbstractAttributeValue;
import dev.nittenapps.stack.data.domain.WithAttributes;
import dev.nittenapps.stack.data.dto.AttributeValueDto;
import dev.nittenapps.stack.data.dto.WithAttributesDto;
import dev.nittenapps.stack.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeforeMapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * AbstractAttributesMapper is an abstract generic class that provides mappings between domain entities with attributes,
 * attribute DTOs, and their corresponding attribute values. It serves as a base class for handling attribute mapping in
 * a consistent and reusable manner.
 * <p>
 * This class includes functionality for:
 * <ul>
 *   <li>Mapping attributes from domain objects to DTOs and vice versa.</li>
 *   <li>Linking attributes to their parent objects after the mapping process.</li>
 *   <li>Resolving attributes by their unique codes using an abstract method.</li>
 *   <li>Preparing the parent ID before mapping via lifecycle hooks.</li>
 * </ul>
 * <p>
 * The class is designed to be extended by concrete implementations that specify the type of entities, attributes, and
 * their DTO representations.
 * <p>
 * Generic Parameters:
 * <ul>
 *   <li><code>T</code>: A type parameter that extends {@link WithAttributes} to represent domain entities that manage
 *   collections of attributes.</li>
 *   <li><code>A</code>: A type parameter that extends {@link AbstractAttribute} to represent the attributes associated
 *   with the entities.</li>
 *   <li><code>D</code>: A type parameter that extends {@link WithAttributesDto} to represent the DTOs for the
 *   entities.</li>
 * </ul>
 * <p>
 * Key Features:
 * <ul>
 *   <li><code>mapAttributesDto</code>: Converts a map of attributes from a domain entity to a map of DTO-compatible
 *   structures.</li>
 *   <li><code>mapAttributes</code>: Converts a map of DTO-compatible structures to a map of attributes for a domain
 *   entity.</li>
 *   <li>Lifecycle hooks (`@BeforeMapping`, `@AfterMapping`) for preparing and finalizing the attribute conversion
 *   process.</li>
 *   <li>Abstract methods for resolving attributes by key, ensuring flexibility for concrete implementations.</li>
 * </ul>
 */
@Slf4j
public abstract class AbstractAttributesMapper<T extends WithAttributes<A, V>, A extends AbstractAttribute<V>,
        V extends AbstractAttributeValue<A>, D extends WithAttributesDto> {
    protected UUID parentId;

    private MapperUtils mapperUtils;

    private SecurityUtils securityUtils;

    @Autowired
    protected final void setMapperUtils(MapperUtils mapperUtils) {
        this.mapperUtils = mapperUtils;
    }

    @Autowired
    protected final void setSecurityUtils(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    /**
     * Prepares the mapping by performing the necessary initialization using the provided DTO. Specifically, it assigns
     * the unique identifier from the DTO to the `parentId` field of this mapper.
     *
     * @param dto the data transfer object from which the mapping initialization data is derived. If null, the method
     *            exits without performing any action.
     */
    @BeforeMapping
    protected void beforeMapping(D dto) {
        if (dto == null) {
            return;
        }

        this.parentId = dto.getId();
    }

    /**
     * Synchronizes the attributes by aligning values from the source DTO with the existing attributes. It ensures that
     * new attributes are added, existing ones are updated, and obsolete ones are removed.
     *
     * @param attributesDto         the map of attribute keys to a list of {@code AttributeValueDto} representing the
     *                              source data. If null, the method exits without making changes.
     * @param existingAttributes    the map of existing attributes where the key is the attribute identifier and the
     *                              value is the corresponding attribute object. This map is updated in place.
     * @param attributeFactory      a supplier for creating new attribute instances when needed.
     * @param attributeValueFactory a supplier for creating new attribute value instances when needed.
     * @param attributeValueBinder  a function that binds an attribute with an attribute value.
     */
    protected void syncAttributes(Map<String, List<AttributeValueDto>> attributesDto, Map<String, A> existingAttributes,
                                  Supplier<A> attributeFactory, Supplier<V> attributeValueFactory,
                                  BiConsumer<A, V> attributeValueBinder) {
        if (attributesDto == null) {
            return;
        }

        Set<String> logbookFields = mapperUtils.getLogBookFields();
        ZonedDateTime now = ZonedDateTime.now();
        String username = securityUtils.getUsername();
        String userFullName = securityUtils.getFullName();

        existingAttributes.keySet().removeIf(key -> !attributesDto.containsKey(key));

        for (Map.Entry<String, List<AttributeValueDto>> entry : attributesDto.entrySet()) {
            String key = entry.getKey();
            List<AttributeValueDto> valueDtos = entry.getValue();
            if (CollectionUtils.isEmpty(valueDtos)) {
                if (existingAttributes.containsKey(key)) {
                    valueDtos = new ArrayList<>();
                } else {
                    continue;
                }
            }

            A attribute = existingAttributes.get(key);
            if (attribute == null) {
                attribute = attributeFactory.get();
                attribute.getId().setCode(key);
                attribute.setType("XX");
                existingAttributes.put(key, attribute);
            }

            Set<V> currentValues = attribute.getValues();
            if (currentValues == null) {
                currentValues = new HashSet<>();
            }
            int incomingSize = valueDtos.size();
            currentValues.removeIf(value -> value.getId().getPosition() >= incomingSize);

            Map<Integer, V> lookupMap = currentValues.stream()
                    .collect(Collectors.toMap(value -> value.getId().getPosition(), value -> value));

            currentValues.clear();

            for (int i = 0; i < valueDtos.size(); i++) {
                AttributeValueDto dto = valueDtos.get(i);
                V value;

                if (lookupMap.containsKey(i)) {
                    value = lookupMap.get(i);
                } else {
                    value = attributeValueFactory.get();
                    value.getId().setPosition(i);
                }

                if (logbookFields.contains(key) && dto.getDateValue() == null) {
                    dto.setCodeValue(username);
                    dto.setStringValue(userFullName);
                    dto.setDateValue(now);
                }

                value.setBooleanValue(dto.getBooleanValue());
                value.setCodeValue(dto.getCodeValue());
                value.setDateValue(dto.getDateValue());
                value.setNumberValue(dto.getNumberValue());
                value.setStringValue(dto.getStringValue());
                value.setTextValue(dto.getTextValue());
                if (dto.getCatalogValue() != null) {
                    value.setCodeValue(dto.getCatalogValue().code);
                    value.setStringValue(dto.getCatalogValue().name);
                }
                attributeValueBinder.accept(attribute, value);
                attribute.addValue(value);
            }
        }
    }

    /**
     * Converts a map of attributes to a map of attribute data transfer objects (DTOs). This method processes the
     * provided attributes, extracts their values, sorts them by position, and maps them to their corresponding DTO
     * representations.
     *
     * @param attributes a map where the key is the attribute's identifier and the value is the attribute object
     *                   containing its values. If the map is null, an empty map is returned.
     * @return a map where the key is the attribute's identifier and the value is a list of {@code AttributeValueDto}
     * objects representing the attribute's values. If the input map is null, an empty map is returned.
     */
    protected Map<String, List<AttributeValueDto>> toAttributesDto(Map<String, A> attributes) {
        if (attributes == null) {
            return Collections.emptyMap();
        }

        Map<String, List<AttributeValueDto>> dtoMap = new HashMap<>();
        for (Map.Entry<String, A> entry : attributes.entrySet()) {
            String key = entry.getKey();
            A attribute = entry.getValue();

            dtoMap.put(key, attribute.getValues().stream()
                    .sorted(Comparator.comparingInt(v -> v.getId().getPosition()))
                    .map(value -> {
                        AttributeValueDto dto = new AttributeValueDto();
                        dto.setBooleanValue(value.getBooleanValue());
                        dto.setCodeValue(value.getCodeValue());
                        dto.setDateValue(value.getDateValue());
                        dto.setNumberValue(value.getNumberValue());
                        dto.setStringValue(value.getStringValue());
                        dto.setTextValue(value.getTextValue());
                        if (StringUtils.isNotBlank(value.getCodeValue())) {
                            dto.setCatalogValue(
                                    new AttributeValueDto.CatalogValue(value.getCodeValue(), value.getStringValue()));
                        }
                        return dto;
                    })
                    .collect(Collectors.toList()));
        }

        return dtoMap;
    }
}
