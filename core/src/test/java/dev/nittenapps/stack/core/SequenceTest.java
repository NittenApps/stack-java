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

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@Slf4j
public class SequenceTest {
    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TestRepository testRepository;

    @org.junit.jupiter.api.Test
    @Transactional
    void whenSavingTest_thenSequenceListenerShouldSetSequenceField() {
        Test test = new Test();
        test = testRepository.save(test);

        entityManager.flush();
        entityManager.clear();

        assertNotNull(test.getId());

        test = testRepository.findById(test.getId()).orElse(null);

        assertNotNull(test);
        assertEquals("24000001", test.getSequence());

        test = new Test();
        test = testRepository.save(test);

        entityManager.flush();
        entityManager.clear();

        assertNotNull(test.getId());

        test = testRepository.findById(test.getId()).orElse(null);

        assertNotNull(test);
        assertEquals("24000002", test.getSequence());
    }

    @Configuration
    @ComponentScan(basePackages = {"dev.nittenapps.stack"})
    @EntityScan(basePackages = {"dev.nittenapps.stack"})
    @EnableJpaRepositories(basePackages = {"dev.nittenapps.stack"})
    static class TestConfiguration {
    }
}
