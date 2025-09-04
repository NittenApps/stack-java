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

import dev.nittenapps.stack.data.annotation.UuidId;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.util.Objects;
import java.util.UUID;

/**
 * Abstract base class for entities with a simple UUID-based identifier and versioning.
 * <p>
 * This class provides an implementation for the unique identifier field using a UUID type and includes basic
 * implementations of `equals` and `hashCode` methods based on the identifier. It extends {@link AbstractVersioned} to
 * support versioning capabilities for optimistic locking.
 * <p>
 * Key Features:
 * - Defines a universally unique identifier (UUID) as the primary field `id`.
 * - Implements the contracts for equality (`equals`) and hash codes (`hashCode`) based solely on the `id` field.
 * - Functions as a base entity class for other domain objects requiring a simple ID definition coupled with versioning.
 * - Configured as a mapped superclass to be inherited by other entity classes.
 * <p>
 * Annotations:
 * - `@MappedSuperclass`: Indicates that this class is a base class to be mapped for JPA inheritance strategies.
 * - Lombok annotations (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@ToString`) for boilerplate
 * code generation.
 * - Hibernate-specific annotation (`@UuidId`) for UUID generation strategy.
 * <p>
 * Inheritors are expected to define additional fields and properties specific to their domain use cases. The `id` field
 * will be used as the identifier across all derived entities.
 */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(doNotUseGetters = true)
public abstract class AbstractSimpleId extends AbstractVersioned<UUID> {
    /**
     * Represents the primary identifier for an entity as a universally unique identifier (UUID).
     * <p>
     * This field is annotated with:
     * - {@code @Id} to specify it as the primary key of the entity.
     * - {@code @UuidId} to enable automatic UUID generation for unique identifiers.
     * - {@code @Column} to configure the corresponding database column with attributes:
     * - {@code name = "id"}: The column in the database will be named "id".
     * - {@code nullable = false}: The column cannot have null values.
     * - {@code updatable = false}: The value of the column cannot be updated after the entity is persisted.
     * <p>
     * The UUID type ensures that each identifier is unique across all instances and provides better
     * scalability and distribution across systems compared to sequential IDs.
     */
    @Id @UuidId
    @Column(name = "id", nullable = false, updatable = false)
    protected UUID id;

    @Override
    public boolean isNew() {
        return id == null;
    }

    /**
     * Compares this object with the specified object to determine equality. This implementation checks if the provided
     * object is of the same class and compares their identifiers.
     *
     * @param o the object to be compared for equality
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractSimpleId that = (AbstractSimpleId)o;
        return Objects.equals(id, that.id);
    }

    /**
     * Computes and returns the hash code value for this object based on the `id` field.
     *
     * @return the hash code value of the object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
