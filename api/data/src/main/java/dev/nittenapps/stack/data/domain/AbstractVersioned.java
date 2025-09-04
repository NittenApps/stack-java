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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.*;

import java.io.Serializable;

/**
 * AbstractVersioned represents a base class for entities that include versioning support. This class is designed to
 * provide optimistic locking capabilities using a version field annotated with {@code @Version}. It is designed to be
 * extended by other entities and provides default behavior for common operations.
 * <p>
 * Annotations:
 * - {@code @MappedSuperclass}: Marks this class as a superclass that other entity types can inherit from.
 * - {@code @Getter @Setter}: Automatically generates getter and setter methods for fields.
 * - {@code @NoArgsConstructor @AllArgsConstructor}: Generates constructors for no-arg and all-arg variants.
 * - {@code @ToString}: Generates a string representation of this class, including inherited fields.
 *
 * <p>
 * Fields:
 * - {@code version}: An {@code Integer} field used for optimistic locking. It ensures that changes to an entity are
 * based on the latest state in the persistence context. Annotated with {@code @Version}.
 * <p>
 * Methods:
 * - {@code isNew()}: Determines whether the entity is new (unsaved) by checking if the version field is null. This is
 * marked as {@code @Transient} to exclude it from persistence and is ignored during serialization with
 * {@code @JsonIgnore}.
 *
 * @param <ID> the type of the identifier, which must implement {@code Serializable}
 */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public abstract class AbstractVersioned<ID extends Serializable> implements Versioned<ID> {
    /**
     * Represents the version of the entity for optimistic locking purposes. It is annotated with {@code @Version} to
     * signify its role in managing concurrent modifications to the entity, ensuring that updates occur only if the
     * version matches the expected value.
     * <p>
     * This field is mapped to the database column named "version" and is marked as non-nullable using the
     * {@code @Column} annotation.
     */
    @Version @Column(name = "version", nullable = false)
    protected Integer version;

    /**
     * Determines whether the entity is new (i.e., not yet persisted) by checking if the version field is null.
     *
     * @return true if the entity is new (unsaved), false otherwise.
     */
    @Override
    @Transient
    @JsonIgnore
    public boolean isNew() {
        return version == null;
    }
}
