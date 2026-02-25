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

package dev.nittenapps.stack.data.service;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import org.hibernate.query.Query;
import org.hibernate.query.ResultListTransformer;
import org.hibernate.query.TupleTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.lang.NonNull;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AbstractDataService serves as a base class for implementing data services which interact with an underlying data
 * source using JPA. This abstract class provides default implementations for common CRUD operations and enforces the
 * implementation of specific behaviors such as query parameter binding and dynamic JPQL construction.
 *
 * @param <E>  Represents the entity class associated with the data service.
 * @param <ID> Represents the type of the identifier for the entity.
 * @param <L>  Represents the type of the list DTO for query results.
 * @param <O>  Represents the type of the object DTO for CRUD operations.
 */
@Slf4j
public abstract class AbstractDataService<E, ID, L, O> implements DataService<E, ID, L, O> {
    protected final Class<E> entityClass;

    protected EntityManager entityManager;

    public AbstractDataService() {
        //noinspection unchecked
        entityClass = (Class<E>)Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(getClass(),
                AbstractDataService.class))[0];
    }

    @Autowired
    protected final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Counts the number of entities that match the given filter criteria.
     *
     * @param filters A map containing filter criteria where keys represent attribute names and values represent the
     *                corresponding values to filter by.
     * @return The total number of entities that satisfy the given filter conditions.
     */
    @Override
    public long count(Map<String, Object> filters) {
        //noinspection JpaQlInspection
        String jpql = getCountJpql(filters) + buildJpqlRestrictions(filters);

        //noinspection unchecked,SqlSourceToSinkFlow
        Query<Long> query = entityManager.createQuery(jpql)
                .unwrap(Query.class);
        bindParams(query, filters);

        return query.getSingleResult();
    }

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity to be fetched. Must not be null.
     * @return The entity associated with the provided identifier, or null if no such entity is found.
     */
    @Override
    public E findById(@NonNull ID id) {
        return entityManager.find(entityClass, id);
    }

    /**
     * Retrieves a list of items based on the specified filters, pagination parameters, and sorting criteria.
     *
     * @param filters A map containing filter criteria for the query. Keys represent attribute names, and values
     *                represent the corresponding filter values to apply.
     * @param offset  The starting position of the results for pagination. Determines the number of initial entries to
     *                skip.
     * @param limit   The maximum number of results to return. A value of 0 means there is no limit.
     * @param sort    Sorting criteria for the query results. This is a string of comma-separated fields, optionally
     *                specifying "asc" or "desc" for each field to define the sort order.
     * @return A list of items of type L that match the specified filters, pagination, and sorting criteria.
     */
    @Override
    public List<L> getList(Map<String, Object> filters, int offset, int limit, String sort) {
        String jpql = getBaseListJpql(filters) + buildJpqlRestrictions(filters) + buildJpqlSort(sort);
        log.trace("jpql: {}", jpql);

        // noinspection unchecked
        @SuppressWarnings("SqlSourceToSinkFlow")
        Query<L> query = entityManager.createQuery(jpql)
                .unwrap(Query.class);

        if (limit > 0) {
            query.setFirstResult(offset)
                    .setMaxResults(limit);
        }
        bindParams(query, filters);

        Optional.ofNullable(getListTupleTransformer())
                .ifPresent(query::setTupleTransformer);
        Optional.ofNullable(getResultListTransformer())
                .ifPresent(query::setResultListTransformer);
        return query.getResultList();
    }

    /**
     * Builds a JPQL "ORDER BY" clause based on the provided sorting criteria.
     *
     * @param sort A comma-separated string representing sorting criteria. Each criterion can specify a field name
     *             optionally followed by "asc" or "desc" to indicate sorting order. If no order is given, "asc" is used
     *             by default.
     * @return A JPQL "ORDER BY" clause as a string. If the input is blank or null, an empty string is returned.
     */
    protected String buildJpqlSort(String sort) {
        if (StringUtils.isNotBlank(sort)) {
            return " ORDER BY " + Arrays.stream(sort.split(","))
                    .map(s -> getSortField(StringUtils.substringBefore(s, " "))
                            + " " + StringUtils.defaultIfBlank(StringUtils.substringAfter(s, " "), "asc"))
                    .collect(Collectors.joining(","));
        }
        return "";
    }

    /**
     * Binds parameters to the specified query based on the provided filter criteria.
     *
     * @param query   The query to which the parameters will be bound. Must not be null.
     * @param filters A map containing filter criteria where keys represent the parameter names and values represent the
     *                parameter values to apply to the query.
     */
    protected void bindParams(Query<?> query, @NonNull Map<String, Object> filters) {
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = StringUtils.removeStart(entry.getKey(), '!');
            Object value = entry.getValue();

            if (value instanceof String) {
                if (!Strings.CS.equalsAny((String)value, "__NULL__", "__NOT_NULL__")) {
                    query.setParameter(key, getFilterValue(key, value.toString()));
                }
            } else if (value instanceof List<?>) {
                query.setParameter(key, ((List<?>)value).stream().map(v -> getFilterValue(key, v.toString()))
                        .toList());
            }
        }
    }

    /**
     * Constructs a JPQL restriction clause based on the provided filters. This method is intended to be used to
     * dynamically generate JPQL query conditions based on a set of key-value pairs where keys represent the attribute
     * names and values represent the desired filter criteria.
     *
     * @param filters A map of filter criteria where the keys correspond to the attribute names and the values represent
     *                the constraints or values to filter against.
     * @return A JPQL restriction clause in the form of a string that can be used to constrain the results of a query.
     */
    protected String buildJpqlRestrictions(@NonNull Map<String, Object> filters) {
        StringBuilder restrictions = new StringBuilder();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (restrictions.isEmpty()) {
                restrictions.append(" WHERE ");
            } else {
                restrictions.append(" AND ");
            }
            restrictions.append(getFilter(key, value));
        }
        return restrictions.toString();
    }

    /**
     * Constructs the base JPQL query string for fetching data based on the provided filters.
     *
     * @param filters A map containing filter criteria. The keys represent attribute names, and the values represent the
     *                corresponding filter values.
     * @return A string representing the base JPQL query. This query can be extended or modified to include additional
     * clauses like sorting and pagination as needed.
     */
    protected abstract String getBaseListJpql(Map<String, Object> filters);

    /**
     * Constructs a JPQL query string to count the number of entities of the given type.
     *
     * @param filters A map of filter criteria used to constrain the query. The filters are currently not applied in the
     *                query.
     * @return A string representing the JPQL query to count entities.
     */
    protected String getCountJpql(Map<String, Object> filters) {
        return "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e";
    }

    protected String getFilter(@NonNull String field, @NonNull Object value) {
        StringBuilder filter = new StringBuilder();
        filter.append(getFilterField(field)).append(" ");
        if (value instanceof String || value instanceof Number) {
            if ("__NULL__".equals(value)) {
                filter.append("IS NULL");
            } else if ("__NOT_NULL__".equals(value)) {
                filter.append("IS NOT NULL");
            } else {
                filter.append(getFilterOperator(field)).append(" :").append(StringUtils.removeStart(field, '!'));
            }
        } else if (value instanceof List<?>) {
            filter.append(" IN (:").append(field).append(") ");
        }
        return filter.toString();
    }

    /**
     * Provides a TupleTransformer that can be used to transform query results into a list of DTOs or other objects of
     * type L.
     *
     * @return A TupleTransformer of type L, or null if no transformer is configured.
     */
    protected TupleTransformer<L> getListTupleTransformer() {
        return null;
    }

    /**
     * Provides a transformer for converting the result list into a desired format.
     *
     * @return A ResultListTransformer instance for handling the transformation of result lists, or null if no
     * transformer is provided.
     */
    protected ResultListTransformer<L> getResultListTransformer() {
        return null;
    }

    /**
     * Constructs a fully qualified field name for filtering purposes based on the provided filter field.
     *
     * @param field The name of the field to be used for filtering. Must not be null.
     * @return The fully qualified field name prefixed with "e." for use in filter expressions.
     */
    protected String getFilterField(@NonNull String field) {
        return "e." + field;
    }

    /**
     * Retrieves the filter operator to be used in a filter expression for the given field.
     *
     * @param field The name of the field for which the operator is being determined. Must not be null.
     * @return A string representing the filter operator (e.g., "=").
     */
    protected String getFilterOperator(@NonNull String field) {
        return "=";
    }

    /**
     * Determines and returns the filter value to be used in filter expressions for a given field.
     *
     * @param field The name of the field to be filtered. Must not be null.
     * @param value The value to be used for filtering. Must not be null.
     * @return An object representing the filter value.
     */
    protected Object getFilterValue(@NonNull String field, @NonNull String value) {
        if ("id".equals(field)) {
            return UUID.fromString(value);
        }
        return value;
    }

    /**
     * Constructs a fully qualified field name for sorting purposes based on the provided sort field.
     *
     * @param sort The name of the field to be used for sorting. Must not be null.
     * @return The fully qualified field name prefixed with "e." for use in sort expressions.
     */
    protected String getSortField(@NonNull String sort) {
        return "e." + sort;
    }
}
