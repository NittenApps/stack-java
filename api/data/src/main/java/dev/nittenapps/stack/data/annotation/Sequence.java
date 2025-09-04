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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to define sequence generation for numeric or alphanumeric fields in an entity.
 * <p>
 * This annotation is used to indicate that the annotated field should have its value generated based on a sequence
 * mechanism. It provides options for configuring the sequence generation such as the sequence code, prefix, suffix,
 * size, increment, and whether to override existing values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sequence {
    /**
     * Specifies the sequence code.
     */
    String code();

    /**
     * Specifies a prefix for the generated sequence value.
     */
    String prefix() default "";

    /**
     * Specifies a suffix to be appended to the generated sequence value.
     */
    String suffix() default "";

    /**
     * Specifies the size of the generated sequence value, left padding with zeroes.
     */
    int size() default 6;

    /**
     * Specifies the increment step for the sequence generation. This value determines the amount by which the sequence
     * is increased each time a new value is generated.
     */
    int increment() default 1;

    /**
     * Specifies whether existing values should be overridden when generating a new value for the annotated field.
     */
    boolean overrideExisting() default false;
}
