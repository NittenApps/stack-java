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
import org.hibernate.query.Query;
import org.hibernate.query.TupleTransformer;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractDataService<E, ID, L, O> implements DataService<E, ID, L, O> {
    protected final Class<E> entityClass;

    protected EntityManager entityManager;

    public AbstractDataService() {
        //noinspection unchecked
        entityClass = (Class<E>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public long count(Map<String, Object> filters) {
        //noinspection JpaQlInspection
        String jpql = getCountJpql(filters);

        //noinspection unchecked,SqlSourceToSinkFlow
        Query<Long> query = entityManager.createQuery(jpql)
                .unwrap(Query.class);
        bindParams(query, filters);

        return query.getSingleResult();
    }

    @Override
    public List<L> getList(Map<String, Object> filters, int offset, int limit, String sort) {
        String jpql = getBaseListJpql(filters) + buildJpqlRestrictions(filters) + buildJpqlSort(sort);
        log.trace("jpql: {}", jpql);

        //noinspection unchecked
        Query<L> query = entityManager.createQuery(jpql)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .unwrap(Query.class);
        bindParams(query, filters);

        return query.setTupleTransformer(getListTupleTransformer())
                .getResultList();
    }

    protected String buildJpqlSort(String sort) {
        if (StringUtils.isNotBlank(sort)) {
            return " ORDER BY " + Arrays.stream(sort.split(","))
                    .map(s -> getSortField(StringUtils.substringBefore(s, " "))
                            + " " + StringUtils.defaultIfBlank(StringUtils.substringAfter(s, " "), "asc"))
                    .collect(Collectors.joining(","));
        }
        return "";
    }

    protected abstract void bindParams(Query<?> query, Map<String, Object> filters);

    protected abstract String buildJpqlRestrictions(Map<String, Object> filters);

    protected abstract String getBaseListJpql(Map<String, Object> filters);

    protected String getCountJpql(Map<String, Object> filters) {
        return "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e" + buildJpqlRestrictions(filters);
    }

    protected abstract TupleTransformer<L> getListTupleTransformer();

    protected String getSortField(String sort) {
        return "e." + sort;
    }
}
