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

package dev.nittenapps.stack.core.service;

import dev.nittenapps.stack.core.domain.Sequence;
import dev.nittenapps.stack.data.service.SequenceService;
import jakarta.persistence.EntityManagerFactory;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.StatelessSession;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class SequenceServiceImpl implements SequenceService {
    private static final LockOptions LOCK_OPTIONS = new LockOptions(LockMode.READ, 1000);

    private EntityManagerFactory entityManagerFactory;

    @Autowired
    protected void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public int getCurrentValue(String code, String prefix, String suffix) {
        Sequence sequence = getSequence(entityManagerFactory.unwrap(SessionFactoryImpl.class).openStatelessSession(),
                code, prefix, suffix);
        return sequence.getCurrentValue();
    }

    @Override
    public int getNextValue(String code, String prefix, String suffix, int increment) {
        StatelessSession session = entityManagerFactory.unwrap(SessionFactoryImpl.class).openStatelessSession();
        session.beginTransaction();
        Sequence sequence = getSequence(session, code, prefix, suffix);
        sequence.setCurrentValue(sequence.getCurrentValue() + increment);
        if (sequence.isNew()) {
            session.insert(sequence);
        } else {
            session.update(sequence);
        }
        session.getTransaction().commit();
        session.close();

        return sequence.getCurrentValue();
    }

    @Override
    public void setValue(String code, String prefix, String suffix, int value) {
        StatelessSession session = entityManagerFactory.unwrap(SessionFactoryImpl.class).openStatelessSession();
        session.beginTransaction();
        Sequence sequence = getSequence(session, code, prefix, suffix);
        sequence.setCurrentValue(value);
        if (sequence.isNew()) {
            session.insert(sequence);
        } else {
            session.update(sequence);
        }
        session.getTransaction().commit();
        session.close();
    }

    @NonNull
    private Sequence getSequence(StatelessSession session, String code, String prefix, String suffix) {
        String jpql = "FROM Sequence s WHERE s.code = :code";
        if (StringUtils.isBlank(prefix)) {
            jpql += " AND s.prefix IS NULL";
        } else {
            jpql += " AND s.prefix = :prefix";
        }
        if (StringUtils.isBlank(suffix)) {
            jpql += " AND s.suffix IS NULL";
        } else {
            jpql += " AND s.suffix = :suffix";
        }

        //noinspection unchecked
        Query<Sequence> query = session.createQuery(jpql, Sequence.class)
                .setLockOptions(LOCK_OPTIONS)
                .unwrap(Query.class);
        query.setParameter("code", code);
        if (StringUtils.isNotBlank(prefix)) {
            query.setParameter("prefix", prefix);
        }
        if (StringUtils.isNotBlank(suffix)) {
            query.setParameter("suffix", suffix);
        }

        Sequence sequence = query.uniqueResult();
        if (sequence == null) {
            sequence = new Sequence();
            sequence.setCode(code);
            sequence.setPrefix(StringUtils.defaultIfBlank(prefix, null));
            sequence.setSuffix(StringUtils.defaultIfBlank(suffix, null));
        }

        return sequence;
    }
}
