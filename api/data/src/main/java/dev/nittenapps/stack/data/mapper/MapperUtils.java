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

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Query;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility service for mapper operations that provides access to field metadata.
 * <p>
 * This service maintains a cache of logbook field codes retrieved from the database and makes them available for use in
 * mapping operations across the application. The field cache is initialized once during bean construction and remains
 * available for the lifetime of the application context.
 * </p>
 */
@Service
@Lazy
@RequiredArgsConstructor
public class MapperUtils {
    private final EntityManager entityManager;

    /**
     * Thread-safe set containing field codes of all logbook type fields.
     * <p>
     * This set is populated during initialization and contains the codes of all fields where the field type is 'LB'
     * (logbook). The set is backed by a ConcurrentHashMap to ensure thread-safe access in multi-threaded environments.
     * </p>
     */
    @Getter
    private final Set<String> logBookFields = ConcurrentHashMap.newKeySet();

    /**
     * Initializes the mapper utilities by populating the logbook fields cache.
     * <p>
     * This method is invoked automatically after dependency injection is complete. It queries the database for all
     * logbook type fields and stores their codes in the {@link #logBookFields} set for efficient lookup during mapping
     * operations.
     * </p>
     */
    @PostConstruct
    void init() {
        logBookFields.addAll(findLogBookFields());
    }

    /**
     * Queries the database to retrieve all field codes where the field type is 'LB' (logbook).
     * <p>
     * This method executes a JPQL query against the Field entity to find all fields with type 'LB' and returns their
     * codes as a set. The results are wrapped in a HashSet for efficient storage and retrieval.
     * </p>
     *
     * @return a non-null set containing the codes of all logbook type fields
     */
    @NonNull
    private Set<String> findLogBookFields() {
        String jpql = """
                SELECT f.code
                FROM Field f
                WHERE f.type = 'LB'
                """;
        //noinspection unchecked
        return new HashSet<String>(entityManager.createQuery(jpql)
                .unwrap(Query.class)
                .getResultList());
    }
}
