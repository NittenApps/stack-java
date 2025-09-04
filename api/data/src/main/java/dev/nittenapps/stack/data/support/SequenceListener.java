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

package dev.nittenapps.stack.data.support;

import dev.nittenapps.stack.data.annotation.Sequence;
import dev.nittenapps.stack.data.service.SequenceService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * The SequenceListener class is a Hibernate event listener that implements the PreInsertEventListener interface. It is
 * designed to automatically generate and set sequence values for entity fields annotated with a custom Sequence
 * annotation. The generated sequence values may include a prefix, suffix, and customizable formatting based on
 * configuration.
 * <p>
 * This listener is registered with Hibernate's event system during application initialization. It intercepts pre-insert
 * entity events and evaluates or updates sequence-related fields in entity objects before they are persisted in the
 * database.
 * <p>
 * As part of its functionality, it supports caching and synchronization mechanisms to ensure efficient and thread-safe
 * generation of sequence values.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SequenceListener implements PreInsertEventListener {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(?<placeholder>[A-Za-z0-9-_|]+)}");

    private final Map<String, CacheEntry> cache = new HashMap<>();

    private final EntityManagerFactory entityManagerFactory;

    private final SequenceService sequenceService;

    /**
     * Registers this instance as a listener for Hibernate PRE_INSERT events. This method is annotated with
     * {@code @PostConstruct} to ensure it is executed during the initialization phase of the bean lifecycle.
     * <p>
     * The method retrieves the Hibernate EventListenerRegistry from the EntityManagerFactory's underlying
     * SessionFactory implementation. It then registers this instance to listen for PRE_INSERT events using the
     * EventListenerRegistry.
     */
    @PostConstruct
    public void selfRegister() {
        final EventListenerRegistry registry = entityManagerFactory.unwrap(SessionFactoryImpl.class)
                .getServiceRegistry()
                .getService(EventListenerRegistry.class);
        assert registry != null;
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListeners(this);
        log.debug("SequenceListener registered");
    }

    /**
     * Handles actions to be performed before an entity is inserted into the persistence context. This method updates
     * the sequence value of the given entity before the insertion operation.
     *
     * @param event the event triggered before the insertion, containing the entity and its state
     * @return false to indicate that no further processing should be done by Hibernate for this event
     */
    @Override
    public boolean onPreInsert(@NonNull PreInsertEvent event) {
        log.trace("onPreInsert({})", event);
        updateSequenceValue(event.getEntity(), event.getState(), event.getPersister().getPropertyNames());
        return false;
    }

    /**
     * Evaluates the given token and returns a formatted string based on its content. If the token represents a date
     * expression, it formats the current date-time according to the specified pattern. If the token is blank or does
     * not match the expected pattern, an empty string is returned.
     *
     * @param token the input string containing the evaluation expression
     * @return the resulting string after evaluation based on the token, or an empty string if the evaluation fails
     */
    @NonNull
    private String evaluate(String token) {
        if (StringUtils.isBlank(token)) {
            return "";
        }
        String[] expressions = token.split("\\|");
        if ("date".equalsIgnoreCase(expressions[0]) && expressions.length > 1) {
            LocalDateTime dateTime = LocalDateTime.now();
            return dateTime.format(DateTimeFormatter.ofPattern(expressions[1]));
        }
        return "";
    }

    /**
     * Generates a sequence number based on the provided parameters and maintains a cache for efficiency. The sequence
     * number is built by combining the prefix, a zero-padded value, and the suffix.
     *
     * @param code the unique identifier for the sequence
     * @param prefix the string prefix to prepend to the sequence number; can be blank
     * @param suffix the string suffix to append to the sequence number; can be blank
     * @param increment the increment value to calculate the next sequence number
     * @param size the total size of the padded sequence number, excluding the prefix and suffix
     * @return the generated sequence number combining prefix, zero-padded sequence value, and suffix
     */
    @NonNull
    private String getSequenceNumber(String code, String prefix, String suffix, int increment, int size) {
        synchronized (cache) {
            prefix = StringUtils.defaultIfBlank(parseValue(prefix), "");
            suffix = StringUtils.defaultIfBlank(parseValue(suffix), "");
            String cacheKey = code + ":" + prefix + ":" + suffix;
            CacheEntry current = cache.get(cacheKey);
            if (current == null || current.isEmpty()) {
                long currentValue = sequenceService.getNextValue(code, prefix, suffix, increment);
                log.debug("current value: {}", currentValue);
                current = new CacheEntry(currentValue, currentValue + increment);
                cache.put(cacheKey, current);
            }
            return prefix + StringUtils.leftPad(String.valueOf(current.next()), size, '0') + suffix;
        }
    }

    /**
     * Parses the given string value, evaluates placeholders, and replaces them with the corresponding values.
     *
     * @param value the input string potentially containing placeholders to be evaluated
     * @return the processed string with placeholders replaced by their evaluated values
     */
    private String parseValue(String value) {
        log.trace("parseValue({})", value);
        if (StringUtils.isBlank(value)) {
            return value;
        }
        int lastIndex = 0;
        StringBuilder output = new StringBuilder();
        Matcher matcher = TOKEN_PATTERN.matcher(value);
        while (matcher.find()) {
            String token = matcher.group("placeholder");
            output.append(value, lastIndex, matcher.start()).append(evaluate(token.trim()));
            lastIndex = matcher.end();
        }
        if (lastIndex < value.length()) {
            output.append(value, lastIndex, value.length());
        }
        log.trace("output: {}", output);
        return output.toString();
    }

    /**
     * Updates the state of a specified property within the provided property states array.
     *
     * @param propertyStates An array of property states to be updated.
     * @param propertyNames An array of property names corresponding to the property states.
     * @param propertyName The name of the property to update.
     * @param propertyState The new state to be assigned to the specified property.
     */
    private void setPropertyState(Object[] propertyStates, @NonNull String[] propertyNames,
                                  @NonNull String propertyName, Object propertyState) {
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyName.equals(propertyNames[i])) {
                propertyStates[i] = propertyState;
                return;
            }
        }
    }

    /**
     * Updates the sequence value for fields annotated with {@code Sequence} in the provided entity. The annotated
     * fields must be of type {@code String}. If the {@code overrideExisting} attribute of the {@code Sequence}
     * annotation is true, or if the existing value is blank, the method generates a new sequence value and assigns it
     * to the field. The updated value is also reflected in the {@code state} array based on its corresponding property
     * name.
     *
     * @param entity        The entity object containing the fields to be updated.
     * @param state         The array representing the current state of the entity's properties.
     * @param propertyNames The array of property names, used to map the updated sequence value within the state array.
     */
    private void updateSequenceValue(Object entity, Object[] state, String[] propertyNames) {
        try {
            List<Field> fields = Stream.of(entity.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Sequence.class)
                            && String.class.isAssignableFrom(field.getType()))
                    .toList();
            for (Field field : fields) {
                field.setAccessible(true);
                Sequence sequence = field.getAnnotation(Sequence.class);
                if (sequence == null) {
                    continue;
                }
                String value = (String)field.get(entity);
                if (sequence.overrideExisting() || StringUtils.isBlank(value)) {
                    value = getSequenceNumber(sequence.code(), sequence.prefix(), sequence.suffix(),
                            sequence.increment(), sequence.size());
                    field.set(entity, value);
                }
                setPropertyState(state, propertyNames, field.getName(), value);
            }
        } catch (Exception ex) {
            log.error("Failed to set sequence property", ex);
        }
    }

    /**
     * Represents a single entry in a cache with a current value and a limit. Provides functionality to retrieve the
     * next value and check if the cache entry is depleted.
     */
    private static class CacheEntry {
        private long current;
        private final long limit;

        public CacheEntry(final long current, final long limit) {
            this.current = current;
            this.limit = limit;
        }

        public long next() {
            return current++;
        }

        public boolean isEmpty() {
            return current >= limit;
        }
    }
}
