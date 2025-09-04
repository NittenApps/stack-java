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

package dev.nittenapps.stack.data.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * AbstractAttribute serves as a base class for defining attributes in a system, with a specific type and associated
 * values. This class is abstract and must be extended by concrete implementations that define its behavior and define
 * the specific attribute's parent and identifier.
 * <p>
 * This class includes support for:
 * - An abstract identifier (`AttributeId`) that uniquely identifies an attribute entity.
 * - A type field representing the attribute type.
 * - A sorted set of `AttributeValue` objects, representing possible values for the attribute.
 * - Abstract operations for managing the parent object of the attribute.
 * - Equality and hashcode implementations based on the attribute's unique identifier.
 * <p>
 * Annotations:
 * - `@MappedSuperclass`: Denotes a base class that other entity types can extend.
 * - Lombok annotations `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, and `@ToString` to automate
 * boilerplate code for fields and methods.
 * - Database mapping annotations like `@Column` for defining attributes specific to entity persistence.
 * <p>
 * Subclasses should implement methods for:
 * - Accessing and modifying the `id` field.
 * - Setting a parent entity appropriately.
 * - Accessing and modifying the associated `values`.
 */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true, doNotUseGetters = true)
public abstract class AbstractAttribute extends AbstractVersioned<AttributeId> {
    /**
     * Retrieves the unique identifier of the attribute.
     *
     * @return the {@code AttributeId} representing the unique identifier of the attribute.
     */
    public abstract AttributeId getId();

    /**
     * Sets the unique identifier for the attribute.
     *
     * @param id the {@code AttributeId} representing the unique identifier to be assigned to the attribute
     */
    public abstract void setId(AttributeId id);

    /**
     * Sets the parent object of the attribute. This method is abstract and must be implemented by concrete subclasses
     * to define how the parent object is managed for the attribute.
     *
     * @param parent the parent object to be associated with this attribute
     */
    public abstract void setParent(Object parent);

    /**
     * Represents the type of the attribute, encoded as a two-character string. This field is mandatory and cannot be
     * null, as indicated by the `nullable = false` constraint. The `length = 2` constraint enforces that the string
     * must not exceed two characters.
     * <p>
     * It is used to distinguish or categorize attributes within the system and  plays a critical role in determining
     * the attribute's classification and behavior.
     */
    @Column(name = "type", nullable = false, length = 2)
    protected String type;

    /**
     * Retrieves the set of attribute values associated with this attribute. The returned collection is sorted based on
     * the natural ordering of {@code AttributeValue}.
     *
     * @return a {@code List} containing the attribute values linked to this attribute
     */
    public abstract List<AttributeValue> getValues();

    /**
     * Compares this instance with the specified object to check for equality.
     *
     * @param o the object to be compared for equality with this instance
     * @return true if the specified object is equal to this instance, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractAttribute abstractAttribute = (AbstractAttribute)o;
        return Objects.equals(getId(), abstractAttribute.getId());
    }

    /**
     * Generates a hash code for the object based on its unique identifier.
     *
     * @return an integer hash code value computed from the unique identifier of the object.
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
