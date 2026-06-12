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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.core.GenericTypeResolver;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract base class for attribute entities in the system, providing core functionality and common operations for all
 * attribute types.
 * <p>
 * This class serves as the foundation for concrete attribute implementations, such as catalog attributes or custom
 * attributes. It manages attribute metadata, including type classification, unique identification, and associated
 * attribute values.
 * <p>
 * Key Features:
 * <ul>
 *   <li>Generic type-safe handling of attribute values extending {@link AbstractAttributeValue}</li>
 *   <li>Automatic value class resolution using Spring's {@link GenericTypeResolver}</li>
 *   <li>Support for multiple attribute values with automatic positioning</li>
 *   <li>Value synchronization capabilities for efficient updates</li>
 *   <li>Type-based attribute classification using two-character codes</li>
 * </ul>
 * <p>
 * This class is annotated with {@code @MappedSuperclass}, indicating it is not directly persisted but serves as a base
 * for JPA entity hierarchies.
 * <p>
 * Subclasses must implement:
 * <ul>
 *   <li>{@link #getId()} and {@link #setId(AttributeId)} - for unique identification</li>
 *   <li>{@link #setParent(Object)} - to establish parent-child relationships</li>
 *   <li>{@link #getValues()} - to provide access to the collection of attribute values</li>
 * </ul>
 *
 * @param <V> the type of attribute values associated with this attribute, must extend {@link AbstractAttributeValue}
 * @see AbstractAttributeValue
 * @see AttributeId
 * @see AttributeValueId
 * @since 1.0
 */
@MappedSuperclass
@Getter @Setter
@ToString(callSuper = true, doNotUseGetters = true)
@SuppressWarnings("rawtypes")
public abstract class AbstractAttribute<V extends AbstractAttributeValue> {
    /**
     * Represents the class type of the attribute values associated with an instance of {@code AbstractAttribute}.
     * <p>
     * This field is resolved at runtime to the generic type parameter {@code V} of a subclass, ensuring type safety for
     * attribute values. The {@code valueClass} is intended to provide metadata about the specific type of values that
     * an attribute can hold.
     * <p>
     * This field is immutable once initialized and is excluded from both serialization and the {@code toString}
     * representation of the object. It is also marked as transient and annotated to prevent automatic Jackson
     * serialization/deserialization.
     * <p>
     * Not intended for external modification or direct access, this field is specifically designed to support internal
     * functionality of the {@code AbstractAttribute} class and its derived implementations.
     */
    @Transient @Getter(AccessLevel.NONE)
    @JsonIgnore @ToString.Exclude
    protected final Class<V> valueClass;

    /**
     * Constructs a new instance of the AbstractAttribute class.
     * <p>
     * This default constructor initializes the {@code valueClass} field using type resolution to determine the generic
     * type parameter {@code V} of the subclass. It also assigns a new instance of {@link AttributeId} as the identifier
     * for this attribute by invoking the {@code setId} method and sets the attribute type to a default value ("XX").
     * <p>
     * The {@code valueClass} field represents the class type of the attribute values, determined at runtime based on
     * the type provided by the subclass.
     * <p>
     * Note: This constructor assumes that proper generic type inheritance is maintained in subclasses to correctly
     * resolve the generic parameter {@code V}.
     *
     * @throws NullPointerException if the generic type resolution fails and the resulting class type is {@code null}.
     */
    public AbstractAttribute() {
        //noinspection unchecked
        valueClass = (Class<V>)Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(getClass(),
                AbstractAttribute.class))[0];

        setId(new AttributeId());
        setType("XX");
    }

    /**
     * Constructs a new instance of the AbstractAttribute class with the specified identifier and type.
     *
     * @param id the {@code AttributeId} representing the unique identifier for this attribute; must not be {@code null}.
     * @param type the {@code String} representing the type of this attribute; must not be {@code null}.
     * @throws NullPointerException if either {@code id} or {@code type} is {@code null}.
     */
    public AbstractAttribute(AttributeId id, String type) {
        this();

        setId(id);
        this.type = type;
    }

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
     * The type classification code for this attribute, represented as a mandatory two-character string.
     * <p>
     * This field serves as a categorical identifier that distinguishes different kinds of attributes
     * within the system. The type code is crucial for determining attribute behavior, validation rules,
     * and processing logic.
     * <p>
     * Database Constraints:
     * <ul>
     *   <li>Column name: "type"</li>
     *   <li>Not nullable - a type must always be specified</li>
     *   <li>Maximum length: 2 characters</li>
     * </ul>
     * <p>
     * The default value "XX" is assigned in the constructor and should be overridden by concrete
     * implementations or through the parameterized constructor.
     *
     * @see #AbstractAttribute()
     * @see #AbstractAttribute(AttributeId, String)
     */
    @Column(name = "type", nullable = false, length = 2)
    protected String type;

    /**
     * Retrieves the set of attribute values associated with this attribute.
     * <p>
     * The returned collection contains all values linked to this attribute instance.
     * Each value is positioned and ordered according to its {@link AttributeValueId#getPosition()}.
     * <p>
     * Implementations should ensure that the returned set is modifiable to support
     * operations like {@link #addValue(AbstractAttributeValue)} and {@link #synchronizeValues(List)}.
     *
     * @return a {@code Set} containing all attribute values associated with this attribute,
     * never {@code null}
     * @see AttributeValueId#getPosition()
     * @see #addValue(AbstractAttributeValue)
     */
    public abstract Set<V> getValues();

    /**
     * Adds a new value to this attribute's collection of values.
     * <p>
     * This method performs the following operations:
     * <ul>
     *   <li>Creates and assigns a new {@link AttributeValueId} if the value doesn't have one</li>
     *   <li>Associates the value with this attribute instance</li>
     *   <li>Sets the position based on the current size of the values collection</li>
     *   <li>Adds the value to the values collection</li>
     * </ul>
     * <p>
     * The position is automatically determined by the current size of the values set,
     * ensuring proper ordering of values.
     *
     * @param value the attribute value to add; must not be {@code null}
     * @throws NullPointerException if the value parameter is {@code null}
     * @see AttributeValueId
     * @see #getValues()
     */
    public void addValue(@NonNull V value) {
        if (value.getId() == null) {
            value.setId(new AttributeValueId());
            value.getId().setAttributeId(getId());
        }
        //noinspection unchecked
        value.setAttribute(this);
        value.getId().setPosition(getValues().size());
        getValues().add(value);
    }

    /**
     * Synchronizes the current attribute values with the provided list of values.
     * <p>
     * This method efficiently updates the attribute's value collection by:
     * <ul>
     *   <li>Removing values that exceed the incoming list size</li>
     *   <li>Updating existing values at matching positions with new data</li>
     *   <li>Creating new value instances for positions that don't exist yet</li>
     * </ul>
     * <p>
     * The synchronization process preserves value identities where possible and only creates
     * new instances when necessary. All value properties (boolean, code, date, number, string,
     * and text values) are copied from the incoming values to the synchronized values.
     * <p>
     * If the provided list is {@code null}, this method returns immediately without making changes.
     * <p>
     * Implementation Details:
     * <ul>
     *   <li>Uses reflection to instantiate new value instances of type {@code V}</li>
     *   <li>Maintains position-based ordering of values</li>
     *   <li>Establishes bidirectional attribute-value relationships</li>
     * </ul>
     *
     * @param values the list of values to synchronize with; may be {@code null}
     * @throws RuntimeException if a new value instance cannot be created via reflection
     * @see #addValue(AbstractAttributeValue)
     */
    public void synchronizeValues(List<V> values) {
        if (values == null) {
            return;
        }

        int incomingSize = values.size();

        Set<V> currentValues = getValues();
        currentValues.removeIf(value -> value.getId().getPosition() >= incomingSize);

        Map<Integer, V> lookupMap = currentValues.stream()
                .collect(Collectors.toMap(value -> value.getId().getPosition(), value -> value));

        currentValues.clear();

        for (int i = 0; i < values.size(); i++) {
            V incomingValue = values.get(i);
            V value;

            if (lookupMap.containsKey(i)) {
                value = lookupMap.get(i);
            } else {
                try {
                    value = valueClass.getConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                value.getId().setPosition(i);
                //noinspection unchecked
                value.setAttribute(this);
                addValue(value);
            }

            value.setBooleanValue(incomingValue.getBooleanValue());
            value.setCodeValue(incomingValue.getCodeValue());
            value.setDateValue(incomingValue.getDateValue());
            value.setNumberValue(incomingValue.getNumberValue());
            value.setStringValue(incomingValue.getStringValue());
            value.setTextValue(incomingValue.getTextValue());
        }
    }

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

        //noinspection unchecked
        AbstractAttribute<V> abstractAttribute = (AbstractAttribute<V>)o;
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
