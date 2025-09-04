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
import jakarta.persistence.Lob;
import lombok.*;
import org.springframework.lang.NonNull;

import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * The AttributeValue class represents a single value associated with an attribute, capable of holding different types
 * such as integer, string, double, date, boolean, or text data. It is annotated as {@code @Embeddable}, meaning it can
 * be embedded in an entity for persistence purposes.
 * <p>
 * This class implements {@link Comparable} to allow comparison of instances based on their position attribute.
 * <p>
 * Annotations used:
 * - {@code @Embeddable}: Marks the class as embeddable in another entity.
 * - {@code @Getter} and {@code @Setter}: Auto-generates accessor and mutator methods for all fields.
 * - {@code @NoArgsConstructor} and {@code @AllArgsConstructor}: Auto-generates constructors for the class.
 * - {@code @ToString}: Auto-generates a string representation of the object, including its field values.
 * - {@code @Column}: Maps fields to database columns, defining column-level metadata such as name.
 * - {@code @Lob}: Specifies that the textValue field is a Large Object (LOB).
 * - {@code @NotAudited}: Prevents auditing of the position field.
 * <p>
 * Key Methods:
 * - {@code equals(Object o)}: Defines equality based on the {@code position} field.
 * - {@code hashCode()}: Computes hash code value based on the {@code position} field.
 * - {@code compareTo(AttributeValue o)}: Compares instances using their {@code position} values to determine ordering.
 */
@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class AttributeValue implements Comparable<AttributeValue> {
    /**
     * Represents the position of the attribute value within a collection or sequence, intended for ordering or sorting
     * purposes. This field is saved in the "position" column of the database table.
     * <p>
     * The position field is of type {@code Integer} and may be {@code null}, indicating that no specific position has
     * been assigned.
     */
    @Column(name = "position")
    private Integer position;

    /**
     * Represents a specific value associated with a code in the context of an attribute. This field is mapped to the
     * "code_value" database column and is used to store a string representation of the value for the corresponding
     * code.
     */
    @Column(name = "code_value")
    private String codeValue;

    /**
     * Represents a string value associated with an attribute. This field is mapped to the "string_value"
     * column in the database.
     * <p>
     * Annotations used:
     * - {@code @Column}: Indicates the mapping of this field to a specific database column with the name
     * "string_value".
     * <p>
     * This field is primarily used for storing textual data that corresponds to an attribute's value.
     */
    @Column(name = "string_value")
    private String stringValue;

    /**
     * Represents a numerical attribute stored in the database. This variable is mapped to the "number_value" column in
     * the database table. It is used to hold attribute values of type {@code Double}.
     * <p>
     * The field is primarily designed to support storage and manipulation of numerical data associated with the
     * containing entity.
     */
    @Column(name = "number_value")
    private Double numberValue;

    /**
     * Represents a date and time value stored in the "date_value" column of the database. The field uses
     * {@link ZonedDateTime} to store both the date and time, along with the associated time zone information.
     * <p>
     * This field can be used to store timestamps or time-related metadata for an entity.
     */
    @Column(name = "date_value")
    private ZonedDateTime dateValue;

    /**
     * Represents a boolean value associated with an attribute. This field is mapped to the "boolean_value" column
     * in the database. It is used to store and retrieve boolean data linked to a specific attribute of the entity.
     */
    @Column(name = "boolean_value")
    private Boolean booleanValue;

    /**
     * Represents a large text value associated with an attribute. This field is intended to store long string
     * content and is mapped to the "text_value" column in the database.
     * <p>
     * Annotations used:
     * - {@code @Lob}: Indicates that the field should be treated as a large object, allowing for storage of
     * substantial text data in the database.
     * - {@code @Column}: Specifies the mapping of this field to the "text_value" column in the database.
     * This annotation also allows customization of the column metadata such as name and additional constraints.
     */
    @Lob
    @Column(name = "text_value")
    private String textValue;

    /**
     * Compares this instance of {@code AttributeValue} with the specified object for equality.
     * Returns {@code true} if the specified object is not null, is of the same class, and its
     * {@code position} field is equal to the {@code position} field of this instance.
     *
     * @param o the object to compare for equality with this instance
     * @return {@code true} if the specified object is equal to this instance; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeValue that = (AttributeValue)o;
        return Objects.equals(position, that.position);
    }

    /**
     * Computes the hash code for this instance of the {@code AttributeValue} class.
     * The hash code is calculated based on the {@code position} field of the object.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(position);
    }

    /**
     * Compares this {@code AttributeValue} object with the specified {@code AttributeValue} object for order.
     * The comparison is based on the {@code position} field of both objects.
     *
     * @param o the {@code AttributeValue} object to be compared
     * @return a negative integer, zero, or a positive integer as this object's {@code position}
     * is less than, equal to, or greater than the specified object's {@code position}
     */
    @Override
    public int compareTo(@NonNull AttributeValue o) {
        return position.compareTo(o.position);
    }
}
