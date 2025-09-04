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

import org.springframework.data.domain.Persistable;

import java.io.Serializable;

/**
 * The Versioned interface defines a contract for entities that support versioning and optimistic locking. It extends
 * the Persistable interface with an additional version attribute, which manages concurrency control and prevents
 * conflicting updates in the persistence layer.
 * <p>
 * The version attribute is typically used alongside a version control mechanism in a database to ensure that updates or
 * modifications to an entity are only applied if the current version matches the expected version.
 *
 * @param <ID> the type of the identifier for the entity, which must implement {@code Serializable}
 */
public interface Versioned<ID extends Serializable> extends Persistable<ID> {
    /**
     * Retrieves the current version of the entity.
     *
     * @return the current version of the entity, represented as an Integer.
     *         This value is used for version control and concurrency management.
     */
    Integer getVersion();

    /**
     * Sets the version number for the entity. The version is used for concurrency control and ensures that updates or
     * modifications to the entity are performed only if the current version matches the expected value.
     *
     * @param version the new version value to be set for the entity. It is represented as an Integer and typically
     *                managed by a persistence system to prevent conflicting updates.
     */
    void setVersion(Integer version);
}
