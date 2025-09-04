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

package dev.nittenapps.stack.data.service;

/**
 * SequenceService is an interface that provides methods for managing sequence values with a combination of code,
 * prefix, and suffix. It allows fetching the current value, generating the next sequence value with an optional
 * increment, and setting a specific sequence value.
 */
public interface SequenceService {
    /**
     * Retrieves the current sequence value based on the given code, prefix, and suffix.
     *
     * @param code The unique identifier representing the sequence. Must not be null or empty.
     * @param prefix The prefix to be used as part of the sequence value. Can be null or empty.
     * @param suffix The suffix to be appended to the sequence value. Can be null or empty.
     * @return The current integer value of the sequence associated with the given code, prefix, and suffix.
     */
    int getCurrentValue(String code, String prefix, String suffix);

    /**
     * Generates and retrieves the next sequence value based on the provided code, prefix, suffix, and increment value.
     *
     * @param code The unique identifier representing the sequence. Must not be null or empty.
     * @param prefix The prefix to be used as part of the sequence value. Can be null or empty.
     * @param suffix The suffix to be appended to the sequence value. Can be null or empty.
     * @param increment The value to increment the current sequence by. Must be a positive integer.
     * @return The next integer value of the sequence after applying the specified increment.
     */
    int getNextValue(String code, String prefix, String suffix, int increment);

    /**
     * Sets the sequence value for the given combination of code, prefix, and suffix.
     *
     * @param code The unique identifier representing the sequence. Must not be null or empty.
     * @param prefix The prefix to be used as part of the sequence value. Can be null or empty.
     * @param suffix The suffix to be appended to the sequence value. Can be null or empty.
     * @param value The integer value to set for the sequence. Must be a non-negative integer.
     */
    void setValue(String code, String prefix, String suffix, int value);
}
