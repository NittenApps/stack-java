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

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceInterceptor /*implements Interceptor*/ {
    /*private static final Pattern TOKEN_PATTERN = Pattern.compile("\\$\\{(?<placeholder>[A-Za-z0-9-_|]+)}");

    private final Map<String, CacheEntry> cache = new HashMap<>();

    private SequenceService sequenceService;

    protected void setSequenceService(@NonNull SequenceService sequenceService) {
        this.sequenceService = sequenceService;
    }

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types)
            throws CallbackException {
        log.debug("onSave({}, {}, {}, {}, {})", entity, id, state, propertyNames, types);
        return updateSequenceValue(entity, state, propertyNames);
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

    private String parseValue(String value) {
        log.debug("parseValue({})", value);
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
        log.debug("output: {}", output);
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

    private boolean updateSequenceValue(Object entity, Object[] state, String[] propertyNames) {
        boolean modified = false;
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
                modified = true;
            }
        } catch (Exception ex) {
            log.error("Failed to set sequence property", ex);
        }
        return modified;
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
    }*/
}
