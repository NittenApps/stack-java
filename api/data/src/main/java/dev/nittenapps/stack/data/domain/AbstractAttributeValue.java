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
import jakarta.persistence.Lob;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * Abstract base class representing attribute values with support for multiple data types. This mapped superclass
 * provides common fields and behavior for storing attribute values as code references, strings, numbers, dates,
 * booleans, or large text content.
 * <p>
 * Subclasses must implement abstract methods to provide attribute-specific identifier and relationship management.
 * <p>
 * Annotations used:
 * <ul>
 *   <li>{@code @MappedSuperclass}: Marks this class as a JPA mapped superclass whose mappings are inherited by
 *  subclasses.</li>
 *   <li>{@code @Getter} and {@code @Setter}: Auto-generates accessor and mutator methods for all fields.</li>
 *   <li>{@code @NoArgsConstructor} and {@code @AllArgsConstructor}: Auto-generates default and all-argument
 *   constructors.</li>
 *   <li>{@code @ToString}: Auto-generates a string representation including field values.</li>
 *   <li>{@code @Column}: Maps fields to database columns with specified column names.</li>
 *   <li>{@code @Lob}: Designates the textValue field as a Large Object for storing substantial text data.</li>
 * </ul>
 * <p>
 * Key Methods:
 * <ul>
 * - {@code equals(Object o)}: Defines equality based on the composite identifier returned by {@code getId()}.
 * - {@code hashCode()}: Computes hash code based on the composite identifier returned by {@code getId()}.
 * </ul>
 *
 * @param <A> the type of attribute this value is associated with, extending {@link AbstractAttribute}
 */
@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true, doNotUseGetters = true)
public abstract class AbstractAttributeValue<A extends AbstractAttribute<?>> implements Serializable {
    @Serial private static final long serialVersionUID = -1773693014258690262L;

    /**
     * Retrieves the unique composite identifier of the attribute value entity.
     *
     * @return an {@code AttributeValueId} object representing the composite primary key for this entity, which includes
     * both the parent attribute's identifier and the value's ordinal position.
     */
    public abstract AttributeValueId getId();

    /**
     * Sets the composite identifier for this attribute value entity.
     *
     * @param id an {@code AttributeValueId} object that represents the composite primary key of this entity. The
     *           composite key consists of the identifier of the parent attribute and the ordinal position of this value
     *           within the attribute's value collection.
     */
    public abstract void setId(AttributeValueId id);

    /**
     * Associates the given attribute with this entity. This method allows the assignment of an attribute
     * to the current instance, where the attribute represents contextual or descriptive information.
     *
     * @param attribute the attribute to be set for this entity, represented by an instance of type {@code A}. It
     *                  encapsulates details or properties relevant to the entity's domain logic.
     */
    public abstract void setAttribute(A attribute);

    /**
     * Stores a code reference value for this attribute. Mapped to the {@code code_value} database column.
     */
    @Column(name = "code_value")
    private String codeValue;

    /**
     * Stores a string value for this attribute. Mapped to the {@code string_value} database column.
     */
    @Column(name = "string_value")
    private String stringValue;

    /**
     * Stores a numerical value for this attribute using {@link BigDecimal} for precision. Mapped to the
     * {@code number_value} database column.
     */
    @Column(name = "number_value")
    private BigDecimal numberValue;

    /**
     * Stores a date and time value with time zone information for this attribute. Mapped to the {@code date_value}
     * database column.
     */
    @Column(name = "date_value")
    private ZonedDateTime dateValue;

    /**
     * Stores a boolean value for this attribute. Mapped to the {@code boolean_value} database column.
     */
    @Column(name = "boolean_value")
    private Boolean booleanValue;

    /**
     * Stores a large text value for this attribute as a LOB (Large Object). Suitable for storing large text
     * content. Mapped to the {@code text_value} database column.
     */
    @Lob
    @Column(name = "text_value")
    private String textValue;

    /**
     * Compares this instance with the specified object for equality. Two attribute values are considered equal if they
     * have the same composite identifier.
     *
     * @param o the object to compare for equality with this instance
     * @return {@code true} if the specified object is equal to this instance; {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        //noinspection unchecked
        AbstractAttributeValue<A> that = (AbstractAttributeValue<A>)o;
        return Objects.equals(getId(), that.getId());
    }

    /**
     * Computes the hash code for this instance. The hash code is calculated based on the composite identifier returned
     * by {@code getId()}.
     *
     * @return the hash code value for this object
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
