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

package dev.nittenapps.stack.config.service;

import dev.nittenapps.stack.config.domain.Component;
import dev.nittenapps.stack.config.dto.ComponentDto;
import dev.nittenapps.stack.config.dto.ComponentListDto;
import dev.nittenapps.stack.data.service.AbstractDataService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.query.Query;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public abstract class AbstractComponentService<E extends Component, L extends ComponentListDto, O extends ComponentDto>
        extends AbstractDataService<E, UUID, L, O> {
    @Override
    protected void bindParams(Query<?> query, @NonNull Map<String, Object> filters) {
        Optional.ofNullable(MapUtils.getString(filters, "code"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> query.setParameter("code", code));
        Optional.ofNullable(MapUtils.getString(filters, "name"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(name -> query.setParameter("name", name.toLowerCase()));
        Optional.ofNullable(MapUtils.getString(filters, "type"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(type -> query.setParameter("type", StringUtils.removeStart(type, "!")));
        Optional.ofNullable(MapUtils.getBoolean(filters, "active"))
                .ifPresent(active -> query.setParameter("active", active));
    }

    @Override
    protected String buildJpqlRestrictions(@NonNull Map<String, Object> filters) {
        StringBuilder restrictions = new StringBuilder();
        Optional.ofNullable(MapUtils.getString(filters, "code"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(code -> restrictions.append(" WHERE e.code LIKE :code"));
        Optional.ofNullable(MapUtils.getString(filters, "name"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(name -> {
                    if (restrictions.isEmpty()) {
                        restrictions.append(" WHERE ");
                    } else {
                        restrictions.append(" AND ");
                    }
                    restrictions.append("LOWER(e.name) LIKE :name");
                });
        Optional.ofNullable(MapUtils.getString(filters, "type"))
                .filter(StringUtils::isNotBlank)
                .ifPresent(type -> {
                    if (restrictions.isEmpty()) {
                        restrictions.append(" WHERE ");
                    } else {
                        restrictions.append(" AND ");
                    }
                    restrictions.append("e.type").append(type.startsWith("!") ? " <> " : " = ").append(":type");
                });
        Optional.ofNullable(MapUtils.getBoolean(filters, "active"))
                .ifPresent(active -> {
                    if (restrictions.isEmpty()) {
                        restrictions.append(" WHERE ");
                    } else {
                        restrictions.append(" AND ");
                    }
                    restrictions.append("e.active = :active");
                });
        return restrictions.toString();
    }
}
