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
import dev.nittenapps.stack.data.domain.AttributeValue;
import dev.nittenapps.stack.data.domain.WithAttributes;
import dev.nittenapps.stack.data.dto.AttributeValueDto;
import dev.nittenapps.stack.data.dto.WithAttributesDto;
import io.micrometer.common.util.StringUtils;
import org.apache.commons.collections4.MapUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.MappingTarget;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * AbstractAttributesMapper is an abstract generic class that provides mappings between domain entities with attributes,
 * attribute DTOs, and their corresponding attribute values. It serves as a base class for handling attribute mapping in
 * a consistent and reusable manner.
 * <p>
 * This class includes functionality for:
 * - Mapping attributes from domain objects to DTOs and vice versa.
 * - Linking attributes to their parent objects after the mapping process.
 * - Resolving attributes by their unique codes using an abstract method.
 * - Preparing the parent ID before mapping via lifecycle hooks.
 * <p>
 * The class is designed to be extended by concrete implementations that specify the type of entities, attributes, and
 * their DTO representations.
 * <p>
 * Generic Parameters:
 * - T: A type parameter that extends {@link WithAttributes} to represent domain entities that manage collections of
 * attributes.
 * - A: A type parameter that extends {@link AbstractAttribute} to represent the attributes associated with the
 * entities.
 * - D: A type parameter that extends {@link WithAttributesDto} to represent the DTOs for the entities.
 * <p>
 * Key Features:
 * - `mapAttributesDto`: Converts a map of attributes from a domain entity to a map of DTO-compatible structures.
 * - `mapAttributes`: Converts a map of DTO-compatible structures to a map of attributes for a domain entity.
 * - Lifecycle hooks (`@BeforeMapping`, `@AfterMapping`) for preparing and finalizing the attribute conversion process.
 * - Abstract methods for resolving attributes by key, ensuring flexibility for concrete implementations.
 */
public abstract class AbstractAttributesMapper<T extends WithAttributes<A>, A extends AbstractAttribute,
        D extends WithAttributesDto> {
    protected UUID parentId;

    /**
     * Converts a map of attributes to a map of attribute DTOs, where the values are transformed into a structure
     * suitable for external data representation (AttributeValueDto). Each value in the input map is mapped to a list of
     * {@code AttributeValueDto}, ensuring the appropriate type conversion.
     *
     * @param attributes a map containing attributes with their corresponding values. Each value
     *                   is expected to provide a list of attribute values.
     * @return a map with the same keys as the input, but the values are lists of AttributeValueDto representing the
     * converted attribute values. Returns null if the input map is empty or null.
     */
    protected Map<String, List<AttributeValueDto>> mapAttributesDto(Map<String, A> attributes) {
        if (MapUtils.isEmpty(attributes)) {
            return null;
        }

        return attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> new ArrayList<>(new LinkedHashSet<>(entry.getValue().getValues().stream()
                                .map(value -> new AttributeValueDto(value.getPosition(), value.getCodeValue(),
                                        value.getStringValue(), value.getNumberValue(), value.getDateValue(),
                                        value.getBooleanValue(), value.getTextValue(),
                                        StringUtils.isBlank(value.getCodeValue()) ? null
                                                : new AttributeValueDto.CatalogValue(value.getCodeValue(),
                                                        value.getStringValue())))
                                .toList()))));
    }

    /**
     * Maps a given input map of attribute keys and their corresponding list of {@code AttributeValueDto} instances to a
     * new map. The output map keys remain the same, but the values are transformed into attribute objects of type
     * {@code A}, populated with their respective transformed values.
     * <p>
     * Each {@code AttributeValueDto} is converted into an {@code AttributeValue} and added to the respective
     * attribute's value set. The transformation includes resolving catalog references where applicable and assigning an
     * ordinal index to each value.
     *
     * @param attributes a map where keys are attribute identifiers (as {@code String}), and the values are lists of
     *                   attribute value DTOs ({@code AttributeValueDto}) to be transformed and mapped.
     * @return a map where keys are attribute identifiers (as {@code String}), and values are transformed attributes of
     * type {@code A} with their respective value sets populated. Returns {@code null} if the input map is
     * {@code null} or empty.
     */
    protected Map<String, A> mapAttributes(Map<String, List<AttributeValueDto>> attributes) {
        if (MapUtils.isEmpty(attributes)) {
            return null;
        }

        AtomicInteger position = new AtomicInteger(0);
        return attributes.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            A attribute = resolveAttribute(entry.getKey());
                            position.set(0);
                            attribute.getValues().clear();
                            attribute.getValues().addAll(entry.getValue()
                                    .stream().map(value -> new AttributeValue(position.getAndIncrement(),
                                            value.getCatalogValue() == null ? value.getCodeValue()
                                                    : value.getCatalogValue().code,
                                            value.getCatalogValue() == null ? value.getStringValue()
                                                    : value.getCatalogValue().name,
                                            value.getNumberValue(), value.getDateValue(), value.getBooleanValue(),
                                            value.getTextValue()))
                                    .toList());
                            return attribute;
                        }));
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
     * Establishes bidirectional linking between a target object and its attributes. This method ensures that each
     * attribute within the target's attribute map is linked back to the parent object and sets its parent identifier.
     *
     * @param target the target object containing a map of attributes to be linked. Must not be null. If the attribute
     *               map is null, the method exits without action.
     */
    @AfterMapping
    protected void linkAttributes(@MappingTarget T target) {
        if (target == null || target.getAttributes() == null) {
            return;
        }

        target.getAttributes().values().forEach(attribute -> {
            assert attribute.getId() != null;
            attribute.getId().setParentId(target.getId());
            attribute.setParent(target);
        });
    }

    /**
     * Resolves and retrieves an attribute instance of type {@code A} based on its unique code. This method is intended
     * to be used for mapping or processing attributes by their identifying codes in implementations of the abstract
     * class.
     *
     * @param code the unique identifier for the attribute to be resolved. Must not be null.
     * @return the resolved attribute instance of type {@code A}. Never null.
     */
    @NonNull
    protected abstract A resolveAttribute(@NonNull String code);
}
