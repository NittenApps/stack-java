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

package dev.nittenapps.stack.core;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.hibernate.StatelessSession;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TestService {
    private final EntityManagerFactory entityManagerFactory;

    public Test save(@NonNull Test test) {
        StatelessSession session = entityManagerFactory.unwrap(SessionFactoryImpl.class).openStatelessSession();
        session.beginTransaction();
        if (test.isNew()) {
            session.insert(test);
        } else {
            session.update(test);
        }
        session.getTransaction().commit();
        session.close();
        return test;
    }
}
