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

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents an abstract base class designed to manage and associate attributes of type {@code A}, which themselves
 * hold values of type {@code V}. This class provides mechanisms to add, update, and retrieve attributes efficiently.
 * Subclasses must define how attributes are stored and retrieved in the form of a map.
 *
 * @param <A> the type of attribute entity, which extends {@code AbstractAttribute<V>}
 * @param <V> the type of attribute value, which extends {@code AbstractAttributeValue<A>}
 */
public abstract class WithAttributes<A extends AbstractAttribute<V>, V extends AbstractAttributeValue<A>>
        extends AbstractSimpleId {
    protected final Class<A> attributeClass;

    /**
     * Default constructor for the {@code WithAttributes} class. Initializes the {@code attributeClass} field by
     * resolving the generic type parameter of the class.
     * <p>
     * This constructor uses a generic type resolution mechanism to identify the actual class type of the attribute
     * entities ({@code A}) that the extending class will handle.
     * <p>
     * Throws a {@code NullPointerException} if the resolved type arguments are {@code null}, ensuring that the generic
     * type must be properly defined in subclasses.
     */
    public WithAttributes() {
        //noinspection unchecked
        attributeClass = (Class<A>)Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(getClass(),
                WithAttributes.class))[0];
    }

    /**
     * Adds an attribute to the entity, associating it with a specific code. If the attribute's ID is null, a new
     * {@code AttributeId} is created and assigned. The code is set on the attribute's ID, and the attribute is linked
     * to the entity as its parent. Finally, the attribute is stored in the map using the provided code as the key.
     *
     * @param code      the unique string identifying the attribute
     * @param attribute the attribute to be added; must not be null
     */
    public void addAttribute(String code, @NonNull A attribute) {
        if (attribute.getId() == null) {
            attribute.setId(new AttributeId());
        }
        attribute.getId().setCode(code);
        attribute.setParent(this);
        getAttributes().put(code, attribute);
    }

    /**
     * Retrieves a map containing the attributes associated with their respective unique string identifiers (keys). The
     * attributes are represented by objects of type {@code A}.
     *
     * @return a map where the keys are unique strings and the values are instances of type {@code A}. This map
     * represents the attributes managed by the entity.
     */
    public abstract Map<String, A> getAttributes();

    /**
     * Updates an attribute associated with the given code by synchronizing its values with the provided list of values.
     * If the list of values is empty or null, the attribute associated with the code is removed.
     *
     * @param code   the unique string identifying the attribute to be updated; must not be null
     * @param values the list of values to synchronize with the attribute; if null or empty, the attribute will be
     *               removed
     * @throws RuntimeException if a new attribute instance cannot be created via reflection
     */
    public void updateAttribute(@NonNull String code, List<V> values) {
        if (CollectionUtils.isEmpty(values)) {
            this.getAttributes().remove(code);
            return;
        }

        A attribute = this.getAttributes().get(code);
        if (attribute == null) {
            try {
                attribute = attributeClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            addAttribute(code, attribute);
        }

        attribute.synchronizeValues(values);
    }
}
