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
 * Copyright (c) 2026. NittenApps
 */

package dev.nittenapps.stack.data.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * AttributeValueId is an embeddable class used as a composite primary key for attribute value entities. It uniquely
 * identifies an attribute value by combining an attribute identifier and a position within that attribute.
 * <p>
 * This class implements {@link Serializable} to support entity serialization and deserialization processes. The class
 * ensures proper handling of equals and hashCode methods, which are critical for correct behavior in collections and
 * when persisted within the database.
 * <p>
 * The composite key consists of:
 * <ul>
 *   <li>{@code attributeId}: The identifier of the parent attribute entity.</li>
 *   <li>{@code position}: The ordinal position of this value within the attribute's value list.</li>
 * </ul>
 * <p>
 * Annotations used:
 * <ul>
 *   <li>{@code @Embeddable}: Marks the class as embeddable in another entity.</li>
 *   <li>{@code @Getter} and {@code @Setter}: Auto-generates getter and setter methods for all fields.</li>
 *   <li>{@code @NoArgsConstructor} and {@code @AllArgsConstructor}: Auto-generates constructors for this class.</li>
 *   <li>{@code @ToString}: Auto-generates a string representation of the class.</li>
 *   <li>{@code @Column}: Specifies column-level metadata such as name and nullability for the position field.</li>
 * </ul>
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(doNotUseGetters = true)
public class AttributeValueId implements Serializable {
    @Serial private static final long serialVersionUID = 1370672221226556515L;

    /**
     * Represents the identifier of the parent attribute to which this value belongs. This field is part of the
     * composite primary key and establishes the relationship between the attribute value and its parent attribute
     * entity.
     */
    private AttributeId attributeId;

    /**
     * Represents the ordinal position of this value within the attribute's value collection. This field is mandatory
     * and cannot be null. It is mapped to the "position" column and serves as the second component of the composite
     * primary key, allowing multiple values to be associated with a single attribute in a specific order.
     */
    @Column(name = "position", nullable = false)
    private Integer position;

    /**
     * Compares this object with the specified object for equality. Returns true if and only if the specified object is
     * also an {@code AttributeValueId}, and both instances have equal values for {@code attributeId} and
     * {@code position}.
     *
     * @param o the object to be compared for equality with this instance
     * @return true if the specified object is equal to this one; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeValueId that = (AttributeValueId)o;
        return Objects.equals(attributeId, that.attributeId) && Objects.equals(position, that.position);
    }

    /**
     * Computes the hash code for this object based on its properties. The hash code is calculated using the
     * {@code attributeId} and {@code position} properties with a multiplier of 31 to ensure good distribution.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        int result = Objects.hashCode(attributeId);
        result = 31 * result + Objects.hashCode(position);
        return result;
    }
}
