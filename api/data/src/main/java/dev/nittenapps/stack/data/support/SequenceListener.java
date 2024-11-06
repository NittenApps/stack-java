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

@Component
@RequiredArgsConstructor
@Slf4j
public class SequenceListener implements PreInsertEventListener {
    private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(?<placeholder>[A-Za-z0-9-_|]+)}");

    private final Map<String, CacheEntry> cache = new HashMap<>();

    private final EntityManagerFactory entityManagerFactory;

    private final SequenceService sequenceService;

    @PostConstruct
    public void selfRegister() {
        final EventListenerRegistry registry = entityManagerFactory.unwrap(SessionFactoryImpl.class)
                .getServiceRegistry()
                .getService(EventListenerRegistry.class);
        assert registry != null;
        registry.getEventListenerGroup(EventType.PRE_INSERT).appendListeners(this);
        log.debug("SequenceListener registered");
    }

    @Override
    public boolean onPreInsert(@NonNull PreInsertEvent event) {
        log.trace("onPreInsert({})", event);
        updateSequenceValue(event.getEntity(), event.getState(), event.getPersister().getPropertyNames());
        return false;
    }

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

    /*@NonNull
    private String getSequenceNumber(String code, String prefix, String suffix, int increment, int size) {
        synchronized (cache) {
            prefix = StringUtils.defaultIfBlank(parseValue(prefix), "");
            suffix = StringUtils.defaultIfBlank(parseValue(suffix), "");
            String cacheKey = code + ":" + prefix + ":" + suffix;
            CacheEntry current = cache.get(cacheKey);
            if (current == null || current.isEmpty()) {
                StatelessSession session = entityManagerFactory.unwrap(SessionFactoryImpl.class).openStatelessSession();
                session.beginTransaction();
                String jpql = "FROM Sequence s WHERE s.code=:code";
                if (StringUtils.isBlank(prefix)) {
                    jpql += " AND s.prefix IS NULL";
                } else {
                    jpql += " AND s.prefix=:prefix";
                }
                if (StringUtils.isBlank(suffix)) {
                    jpql += " AND s.suffix IS NULL";
                } else {
                    jpql += " AND s.suffix=:suffix";
                }
                Query<Sequence> query = session.createQuery(jpql, Sequence.class);
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

                current = new CacheEntry(sequence.getNextValue(), sequence.getNextValue() + increment);
                cache.put(cacheKey, current);
                sequence.setNextValue(sequence.getNextValue() + increment);

                if (sequence.getId() == null) {
                    session.insert(sequence);
                } else {
                    session.update(sequence);
                }
                session.getTransaction().commit();
                session.close();
            }

            return prefix + StringUtils.leftPad(String.valueOf(current.next()), size, '0') + suffix;
        }
    }*/

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

    private void setPropertyState(Object[] propertyStates, @NonNull String[] propertyNames,
                                  @NonNull String propertyName, Object propertyState) {
        for (int i = 0; i < propertyNames.length; i++) {
            if (propertyName.equals(propertyNames[i])) {
                propertyStates[i] = propertyState;
                return;
            }
        }
    }

    private void updateSequenceValue(Object entity, Object[] state, String[] propertyNames) {
        try {
            List<Field> fields = Stream.of(entity.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(Sequence.class)
                            && String.class.isAssignableFrom(field.getType()))
                    .toList();
            for (Field field : fields) {
                field.setAccessible(true);
                Sequence sequence = field.getAnnotation(Sequence.class);
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
