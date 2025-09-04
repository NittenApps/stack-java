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

package dev.nittenapps.stack.util;

import org.springframework.security.core.userdetails.User;

import java.util.Map;

/**
 * Interface that defines utility methods for working with security-related information in the current authentication
 * context.
 */
public interface SecurityUtils {
    /**
     * Retrieves a map of user claims associated with the currently authenticated user.
     *
     * @return a map containing the claims of the authenticated user, where the keys are claim names and  the values are
     *         the corresponding claim values
     */
    Map<String, Object> getUserClaims();

    /**
     * Retrieves the details of the currently authenticated user.
     *
     * @return a User object representing the details of the authenticated user
     */
    User getUserDetails();

    /**
     * Retrieves the username of the currently authenticated user.
     *
     * @return the username of the authenticated user
     */
    String getUsername();
}
