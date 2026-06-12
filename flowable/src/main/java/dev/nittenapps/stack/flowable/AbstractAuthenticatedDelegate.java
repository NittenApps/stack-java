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
 * Copyright (c) 2026. NittenApps
 */

package dev.nittenapps.stack.flowable;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;

/**
 * Abstract base class for implementing Flowable Java delegates that require user authentication during their execution.
 * This class automatically manages the authenticated user context for the delegate execution lifecycle.
 * <p>
 * The class ensures that:
 * <ul>
 *   <li>The authenticated user context is established at the beginning of the execution.</li>
 *   <li>Delegate-specific operations are executed via the abstract method {@code doExecute}.</li>
 *   <li>The authenticated user context is cleared after execution to avoid leaks.</li>
 * </ul>
 * <p>
 * Subclasses must implement the {@code doExecute} method to provide their specific logic.
 * <p>
 * Thread-Safety: This class is not thread-safe as it depends on thread-local storage for the authentication context.
 * <p>
 * Logging: Logs various debug and error information related to authentication context management.
 */
@Slf4j
public abstract class AbstractAuthenticatedDelegate implements JavaDelegate {
    /**
     * Executes the business logic of the delegate while managing the authenticated user context.
     * This method ensures that the authenticated user is correctly set up before invocation and cleared afterward.
     *
     * @param execution the {@link DelegateExecution} instance representing the current execution context in the
     *                  Flowable engine. It provides access to process variables, execution state, and related workflow
     *                  information.
     */
    @Override
    public void execute(DelegateExecution execution) {
        setAuthenticatedUser(execution);

        doExecute(execution);

        clearAuthenticatedUser();
    }

    /**
     * Clears the currently authenticated user from the security context.
     * <p>
     * This method is typically invoked to clean up the authentication context after the execution of delegate-specific
     * logic, ensuring that no residual authentication data leaks to later executions. It interacts with the
     * {@link SecurityContextHolder} to reset the security context.
     * <p>
     * Thread-Safety:
     * This method relies on thread-local storage provided by {@link SecurityContextHolder}. It should only be called
     * within the scope of a single thread's logical execution flow.
     */
    protected void clearAuthenticatedUser() {
        SecurityContextHolder.clearContext();
    }

    /**
     * Executes the business logic for a given process execution. This method is part of the delegate-specific execution
     * chain and is responsible for implementing the core functionality within the authenticated user context.
     *
     * @param execution the {@link DelegateExecution} instance representing the current execution context in the
     *                  Flowable engine. It provides access to process variables, execution state, and related workflow
     *                  information.
     */
    protected abstract void doExecute(DelegateExecution execution);

    /**
     * Sets the authenticated user in the security context based on the current execution context.
     * <p>
     * This method attempts to determine the authenticated user ID. It first retrieves the ID from the current
     * {@link Authentication} context. If no user ID is found, it retrieves it from the process variables associated
     * with the provided execution context. The method then sets the authenticated user in the
     * {@link SecurityContextHolder}.
     *
     * @param execution the {@link DelegateExecution} instance representing the current execution context in the
     *                  workflow engine. Used to fetch process variables and execution state.
     */
    protected void setAuthenticatedUser(DelegateExecution execution) {
        String userId = Authentication.getAuthenticatedUserId();
        if (StringUtils.isBlank(userId)) {
            userId = execution.getVariable("_user").toString();
        }

        if (StringUtils.isNotBlank(userId)) {
            try {
                UserDetails userDetails = new User(userId, null, Collections.emptyList());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                        Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.debug("Set authenticated user to {}", userId);
            } catch (Exception ex) {
                log.error("Failed to set authenticated user", ex);
            }
        }
    }
}
