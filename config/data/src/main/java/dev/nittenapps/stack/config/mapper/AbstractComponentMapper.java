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

package dev.nittenapps.stack.config.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nittenapps.stack.config.domain.Component;
import dev.nittenapps.stack.config.domain.ComponentChild;
import dev.nittenapps.stack.config.domain.ComponentChildId;
import dev.nittenapps.stack.config.dto.ComponentDto;
import dev.nittenapps.stack.config.dto.ComponentListDto;
import dev.nittenapps.stack.data.dto.SimpleIdDto;
import dev.nittenapps.stack.data.mapper.DataMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.BeforeMapping;
import org.mapstruct.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class AbstractComponentMapper<E extends Component, L extends ComponentListDto, O extends ComponentDto>
        implements DataMapper<E, UUID, L, O> {
    protected UUID componentId;

    protected final Class<E> entityClass;

    protected EntityManager entityManager;

    protected ObjectMapper objectMapper;

    @BeforeMapping
    protected void beforeMapping(O dto) {
        if (dto == null) {
            return;
        }

        this.componentId = dto.getId();
    }

    @Autowired
    protected final void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    protected final void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public AbstractComponentMapper() {
        log.debug("classes: {}", (Object)GenericTypeResolver.resolveTypeArguments(getClass(),
                AbstractComponentMapper.class));
        //noinspection unchecked
        entityClass = (Class<E>)Objects.requireNonNull(GenericTypeResolver.resolveTypeArguments(getClass(),
                AbstractComponentMapper.class))[0];
    }

    public final E toEntity(@NonNull SimpleIdDto dto) {
        return entityManager.find(entityClass, dto.getId());
    }

    public abstract L toListDto(Component component);

    protected Map<String, Object> mapDefinition(String definition) {
        if (StringUtils.isBlank(definition)) {
            return null;
        }

        try {
            //noinspection unchecked
            return objectMapper.readValue(definition, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected String mapDefinition(Map<String, Object> definition) {
        if (MapUtils.isEmpty(definition)) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(definition);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @ObjectFactory
    protected E resolve(SimpleIdDto dto) {
        if (dto == null) {
            return null;
        }

        try {
            return Optional.ofNullable(dto.getId())
                    .map(id -> entityManager.find(entityClass, id))
                    .orElse(entityClass.getDeclaredConstructor().newInstance());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected ComponentChild resolveComponentChild(UUID componentId, UUID childId) {
        if (componentId == null || childId == null) {
            return new ComponentChild();
        }

        return Optional.ofNullable(entityManager.find(ComponentChild.class, new ComponentChildId(componentId, childId)))
                .orElse(new ComponentChild());
    }
}
