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

package dev.nittenapps.stack.data.domain;

import com.fasterxml.uuid.Generators;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

import java.io.Serial;
import java.util.EnumSet;
import java.util.UUID;

/**
 * UuidIdGenerator is an implementation of the {@link BeforeExecutionGenerator} interface for generating
 * UUID-based identifiers. The generator provides a time-based UUID using an underlying epoch-based
 * UUID generator from {@code Generators}.
 * <p>
 * This class is primarily used to generate unique identifiers for entities during an INSERT operation in
 * persistence contexts. It ensures that each identifier is globally unique and aligns with the semantics
 * of the time-based UUID generation standard.
 * <p>
 * Key Characteristics:
 * - Generates time-based UUIDs through a time-based epoch generator.
 * - Limits the event types where the generator can be applied to INSERT operations only.
 * <p>
 * Methods:
 * - {@code generate}: Creates a new time-based UUID identifier based on the event context.
 * - {@code getEventTypes}: Specifies the event types applicable for this generator (INSERT_ONLY).
 * <p>
 * This implementation assumes compatibility with Hibernate-specific session contracts and event types.
 */
public class UuidIdGenerator implements BeforeExecutionGenerator {
    @Serial private static final long serialVersionUID = 7679695096875938427L;

    /**
     * Generates a new time-based UUID identifier for the entity.
     *
     * @param sharedSessionContractImplementor the Hibernate session contract implementor used for accessing the session
     *                                         context
     * @param owner the entity or object for which the identifier is being generated
     * @param currentValue the current identifier value, if one exists, otherwise null
     * @param eventType the type of event triggering the identifier generation, typically during an INSERT operation
     * @return a newly generated UUID as the identifier
     */
    @Override
    public UUID generate(SharedSessionContractImplementor sharedSessionContractImplementor, Object owner,
                         Object currentValue, EventType eventType) {
        return Generators.timeBasedEpochGenerator().generate();
    }

    /**
     * Specifies the event types applicable for this generator.
     *
     * @return an EnumSet containing the applicable event types, which in this case is limited to INSERT_ONLY.
     */
    @Override
    public EnumSet<EventType> getEventTypes() {
        return EventTypeSets.INSERT_ONLY;
    }
}
