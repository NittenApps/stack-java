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
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * AttributeId is an embeddable class used as a composite primary key within an entity. It represents a unique
 * identifier consisting of a parent ID and a code.
 * <p>
 * This class implements {@link Serializable} to support entity serialization and deserialization processes. The class
 * ensures proper handling of equals and hashCode methods, which are critical for correct behavior in collections and
 * when persisted within the database.
 * <p>
 * The {@code parentId} field refers to the parent entity's unique identifier, which is represented by a UUID. The
 * {@code code} field is a string with a maximum length of 50, serving as an additional identifier.
 * <p>
 * Annotations used:
 * - {@code @Embeddable}: Marks the class as embeddable in another entity.
 * - {@code @Getter} and {@code @Setter}: Auto-generates getter and setter methods  for all fields.
 * - {@code @NoArgsConstructor} and {@code @AllArgsConstructor}: Auto-generates  constructors for this class.
 * - {@code @ToString}: Auto-generates a string representation of the class.
 * - {@code @Column}: Specifies column-level metadata such as name, nullability, and length restrictions for each field.
 * <p>
 * This class is designed to be immutable by default, but Lombok annotations are leveraged to provide mutability where
 * required.
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class AttributeId implements Serializable {
    @Serial private static final long serialVersionUID = -8514495177317414261L;

    /**
     * Represents the unique identifier of a parent entity in a composite primary key structure. The field is mapped to
     * the "parent_id" column in the database and cannot be null. It is used to establish a relationship with the parent
     * entity, ensuring unique identification within the scope of the composite key.
     */
    @Column(name = "parent_id", nullable = false)
    private UUID parentId;

    /**
     * Represents the code component of a composite primary key in the database. This field is mapped to the "code"
     * column and serves as an additional identifier alongside other components of the key.
     * <p>
     * The "code" column:
     * - Is mandatory (cannot be null).
     * - Has a maximum length of 50 characters.
     */
    @Column(name = "code", nullable = false, length = 50)
    private String code;

    /**
     * Compares this object with the specified object for equality. Returns true if and only if the specified object is
     * also an {@code AttributeId}, and both instances have equal values for {@code parentId} and {@code code}.
     *
     * @param o the object to be compared for equality with this instance
     * @return true if the specified object is equal to this one; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeId that = (AttributeId)o;
        return Objects.equals(parentId, that.parentId) && Objects.equals(code, that.code);
    }

    /**
     * Computes the hash code for this object based on its properties. The hash code is calculated using the
     * {@code parentId} and {@code code} properties.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(parentId);
        result = 31 * result + Objects.hashCode(code);
        return result;
    }
}
