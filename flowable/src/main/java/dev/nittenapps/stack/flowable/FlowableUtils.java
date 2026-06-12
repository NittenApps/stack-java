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

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utility class for interacting with the Flowable Business Process Management (BPM) engine. Provides methods for
 * managing process instances and setting execution variables. This class is designed to simplify common operations
 * within the Flowable engine.
 * <p>
 * This class is annotated with {@code @Component} to mark it as a Spring-managed bean and uses constructor injection to
 * initialize the Flowable {@link ProcessEngine}.
 */
@Component
@RequiredArgsConstructor
public class FlowableUtils {
    private final ProcessEngine processEngine;

    /**
     * Creates and starts a new Business Process Management (BPM) task (process instance) using the provided process
     * definition key, business key, variables, and username. This method sets the authenticated user context when
     * starting the process instance and ensures the user context is cleared afterward.
     *
     * @param processDefinitionKey the unique key identifying the process definition. Must not be null.
     * @param businessKey          the business key that serves as a correlation identifier for the process instance.
     *                             Must not be null.
     * @param variables            a map containing variables to set in the process instance. Can be null or empty.
     * @param username             the username of the authenticated user to set for context at the start of the
     *                             process. Can be null or empty.
     * @return the created {@code ProcessInstance} representing the new process instance.
     */
    public ProcessInstance createBPMTask(@NonNull String processDefinitionKey, @NonNull String businessKey,
                                         Map<String, Object> variables, String username) {
        try {
            Authentication.setAuthenticatedUserId(username);
            RuntimeService runtimeService = processEngine.getRuntimeService();
            return runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
        } finally {
            if (StringUtils.isNotBlank(username)) {
                Authentication.setAuthenticatedUserId(null);
            }
        }
    }

    /**
     * Retrieves a {@code ProcessInstance} by its unique identifier.
     *
     * @param processInstanceId the unique identifier of the process instance to retrieve. Must not be null.
     * @return the {@code ProcessInstance} corresponding to the given identifier, or {@code null} if no matching process
     * instance is found.
     */
    public ProcessInstance getProcessInstanceById(@NonNull String processInstanceId) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        return runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
    }

    /**
     * Sets the specified variables to the given execution within the Flowable runtime environment.
     *
     * @param executionId the unique identifier of the execution to which the variables will be set.  Must not be null.
     * @param variables   a map of variables to set for the specified execution. Can be null or empty.
     */
    public void setVariablesToExecution(@NonNull String executionId, Map<String, Object> variables) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        runtimeService.setVariables(executionId, variables);
    }
}
