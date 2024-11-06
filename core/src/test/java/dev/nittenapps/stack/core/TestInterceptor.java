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

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
public class TestInterceptor implements Interceptor, HibernatePropertiesCustomizer {
    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
        log.debug("onSave({})", entity);
        return false;
    }

    @Override
    public void customize(@NonNull Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.session_factory.interceptor", this);
        log.debug("customize({})", hibernateProperties);
    }
}
