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

package dev.nittenapps.stack.data.mapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.mapstruct.Named;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility class that provides mapping functionality to resolve and return entity references based on their unique
 * identifiers. This class can handle both direct UUID mappings and JPA-managed entity references.
 * <p>
 * This component is intended to be used in scenarios where an entity reference needs to be retrieved efficiently
 * without triggering a full database fetch.
 */
@Component
public class ReferenceMapper {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Retrieves a reference to an entity of the specified type using its unique identifier.
     * If the provided identifier is null, this method returns null.
     * For entities of type {@link UUID}, the identifier itself is returned cast to the specified type.
     * For other entity types, the reference is resolved using the JPA {@link EntityManager#getReference(Class, Object)}
     * method.
     *
     * @param id          the unique identifier of the entity to retrieve; can be null
     * @param entityClass the class type of the entity to resolve
     * @return a reference to the entity of the specified type, or null if the identifier is null
     */
    @Named("idToEntity")
    public <T> T getReference(UUID id, @TargetType Class<T> entityClass) {
        if (id == null) {
            return null;
        }
        if (UUID.class.isAssignableFrom(entityClass)) {
            return entityClass.cast(id);
        }
        return entityManager.getReference(entityClass, id);
    }
}
