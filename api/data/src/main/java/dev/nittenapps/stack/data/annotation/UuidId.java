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

package dev.nittenapps.stack.data.annotation;

import dev.nittenapps.stack.data.domain.UuidIdGenerator;
import org.hibernate.annotations.IdGeneratorType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to indicate that a field or method should have its identifier value automatically generated using a
 * UUID-based generator.
 * <p>
 * This annotation binds the annotated field or method with the {@link UuidIdGenerator} implementation, which generates
 * unique identifiers of type {@link java.util.UUID}.
 * <p>
 * The {@code UuidIdGenerator} generates time-based UUIDs utilizing a {@code timeBasedEpochGenerator}, ensuring
 * uniqueness across different entities in the database context where this annotation is applied.
 * <p>
 * This annotation is typically used for marking primary keys or other fields that require a globally unique identifier.
 * <p>
 * Usage:
 * - Can be applied to fields or methods in an entity class.
 * - The generated IDs will be assigned automatically during INSERT operations.
 * <p>
 * Retention Policy:
 * - Retention is set to {@link RetentionPolicy#RUNTIME}, meaning the annotation's metadata will be available at runtime
 *   through reflection.
 * <p>
 * Target:
 * - Can be applied on fields ({@link ElementType#FIELD}) or methods ({@link ElementType#METHOD}).
 * <p>
 * Associated Generator:
 * - Generator Class: {@code UuidIdGenerator}
 * - Generation Type: {@code BeforeExecutionGenerator}, focusing on pre-insert event handling.
 */
@IdGeneratorType(UuidIdGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface UuidId {
}
