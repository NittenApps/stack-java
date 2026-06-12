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
 * Copyright (c) 2024-2026. NittenApps
 */

package dev.nittenapps.stack.flowable;

import dev.nittenapps.stack.activity.api.AbstractActivity;
import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.data.service.DataService;
import dev.nittenapps.stack.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.NativeTaskQuery;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Abstract implementation of an activity built around Flowable's BPM (Business Process Management) engine. This class
 * provides foundational methods for interacting with Flowable's {@code ProcessEngine}, such as querying tasks,
 * completing tasks, creating processes, and signaling events, alongside utility support for security and
 * process-related operations.
 *
 * @param <E>  The type of the primary entity associated with the activity.
 * @param <ID> The type of the identifier of the primary entity.
 * @param <L>  The type of the object used for list output representations.
 * @param <O>  The type of the object used for detail or single output representations.
 */
@Slf4j
@SuppressWarnings("unused")
public class AbstractFlowableActivity<E, ID, L, O> extends AbstractActivity<E, ID, L, O> {
    protected FlowableUtils flowableUtils;

    protected ProcessEngine processEngine;

    protected SecurityUtils securityUtils;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected final void setFlowableUtils(FlowableUtils flowableUtils) {
        this.flowableUtils = flowableUtils;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected final void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    protected final void setSecurityUtils(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    protected AbstractFlowableActivity(DataService<E, ID, L, O> dataService) {
        super(dataService);
    }

    /**
     * Retrieves a list of items based on the provided search parameters and the user's context.
     * This method queries tasks associated with specific activity IDs and filters them according to the user's roles
     * and privileges. The collected task descriptions are then used as input parameters to fetch the desired list.
     *
     * @param params A {@code MultiValueMap} containing search parameters for the list query. This map will be augmented
     *               with additional parameters derived from the queried tasks' descriptions.
     * @param user   The {@code User} object representing the current authenticated user. The user's authorities and
     *               username are used to filter tasks for which the user is authorized.
     * @return An {@code ApiResponse} containing a {@code ListBody} with the queried list of items. If no tasks are
     * found, the returned list will be empty.
     */
    @Override
    public ApiResponse<ListBody<L>> getList(@NonNull MultiValueMap<String, String> params, @NonNull User user) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        /*List<Task> tasks = taskService.createTaskQuery()
                .taskDefinitionKeys(getActivityIds())
                /*.or()
                .taskCandidateGroupIn(user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .filter(authority -> Strings.CS.startsWith(authority, "GROUP_")).toList())
                .taskCandidateUser(user.getUsername())
                .endOr()*/
        //.list();

        String placeholders = IntStream.range(0, getActivityIds().size())
                .mapToObj(i -> "#{activity" + i + "}")
                .collect(Collectors.joining(", "));

        String sql = """
                SELECT
                    t.id_, e.business_key_ AS description_
                FROM
                    act_ru_task t JOIN act_ru_execution e ON t.proc_inst_id_ = e.id_
                WHERE
                    t.task_def_key_ IN ( %s ) AND t.suspension_state_ = 1
                """.formatted(placeholders);
        NativeTaskQuery query = taskService.createNativeTaskQuery()
                .sql(sql);
        int i = 0;
        for (String activityId : getActivityIds()) {
            query.parameter("activity" + i++, activityId);
        }
        List<Task> tasks = query.list();

        if (tasks.isEmpty()) {
            //noinspection unchecked
            return new ApiResponse<>(new ListBody<L>(Collections.EMPTY_LIST, 0, 0), null);
        }

        params.addAll("id", tasks.stream().map(Task::getDescription).collect(Collectors.toSet()).stream()
                .toList());
        log.trace("params: {}", params);

        return super.getList(params, user);
    }

    /**
     * Completes one or more tasks associated with the provided business key. The tasks to be completed are determined
     * based on activity IDs retrieved from the current bean. If no tasks are found for the specified business key, a
     * warning is logged and the method exits.
     *
     * @param businessKey The unique identifier used to locate the process instance and associated tasks.
     *                    Must not be {@code null}.
     * @param variables   A map of variables to be passed to the tasks upon completion.
     *                    These variables are used to update process state or provide input to subsequent tasks.
     */
    protected void completeTask(@NonNull String businessKey, Map<String, Object> variables) {
        completeTask(businessKey, getActivityIds(), variables);
    }

    /**
     * Completes a single task associated with the specified business key and task definition key. This method delegates
     * to the overloaded version of {@code completeTask} that accepts a set of task definition keys.
     *
     * @param businessKey       The unique identifier used to locate the process instance and associated tasks.
     *                          Must not be {@code null}.
     * @param taskDefinitionKey The definition key of the task to be completed. Must not be {@code null}.
     * @param variables         A map of variables to be passed to the task upon completion.
     *                          These variables are used to update process state or provide input to subsequent tasks.
     */
    protected void completeTask(@NonNull String businessKey, String taskDefinitionKey, Map<String, Object> variables) {
        completeTask(businessKey, Set.of(taskDefinitionKey), variables);
    }

    /**
     * Completes one or more tasks associated with the specified business key and task definition keys. The method
     * retrieves tasks based on the provided business key and task definition keys and completes them using the
     * provided variables. If no tasks are found, a warning is logged.
     *
     * @param businessKey        The unique identifier used to locate the process instance and associated tasks. Must
     *                           not be {@code null}.
     * @param taskDefinitionKeys A set of task definition keys used to identify the tasks to be completed. Must not be
     *                           {@code null}.
     * @param variables          A map of variables to be passed to the tasks upon completion. These variables are used
     *                           to update the process state or provide input to later tasks.
     */
    protected void completeTask(@NonNull String businessKey, @NonNull Set<String> taskDefinitionKeys,
                                Map<String, Object> variables) {
        Authentication.setAuthenticatedUserId(securityUtils.getUsername());
        try {
            TaskService taskService = processEngine.getTaskService();
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceBusinessKey(businessKey)
                    .taskDefinitionKeys(taskDefinitionKeys)
                    .list();
            if (CollectionUtils.isEmpty(tasks)) {
                log.warn("No tasks found for business key: {}", businessKey);
                return;
            }
            for (Task task : tasks) {
                log.debug("Completing task: {}", task);
                taskService.complete(task.getId(), variables);
            }
        } finally {
            Authentication.setAuthenticatedUserId(null);
        }
    }

    /**
     * Creates and starts a new Business Process Management (BPM) task (process instance) using the specified process
     * definition key, business key, and variables. The method automatically sets the authenticated user context based
     * on the current security context.
     *
     * @param processDefinitionKey the unique key identifying the process definition. Must not be null.
     * @param businessKey          the business key that serves as a correlation identifier for the process instance.
     *                             Must not be null.
     * @param variables            a map containing variables to set in the process instance. Can be null or empty.
     * @return a {@code ProcessInstance} representing the newly created process instance.
     */
    protected ProcessInstance createBPMTask(@NonNull String processDefinitionKey, @NonNull String businessKey,
                                            Map<String, Object> variables) {
        ProcessInstance processInstance = flowableUtils.createBPMTask(processDefinitionKey, businessKey, variables,
                securityUtils.getUsername());
        log.debug(">>> Created Process Instance: {}", processInstance);
        return processInstance;
    }

    /**
     * Retrieves a set of activity IDs associated with the current bean.
     *
     * @return a {@code Set<String>} containing one or more activity IDs.
     * This implementation returns a singleton set derived from the current bean name.
     */
    protected Set<String> getActivityIds() {
        return Set.of(beanName);
    }

    /**
     * Signals a specific event to a runtime process identified by its business key. It searches for tasks associated
     * with the provided business key and triggers the event for the first task found. If no tasks are associated with
     * the given key, an exception is thrown.
     *
     * @param id         The unique identifier (business key) of the process for which the event is signaled.
     * @param signalName The name of the signal/event to be sent to the process.
     * @throws IllegalArgumentException If no tasks are associated with the given business key.
     */
    protected void signalEvent(@NonNull UUID id, @NonNull String signalName) {
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Execution execution = runtimeService.createExecutionQuery()
                .processInstanceBusinessKey(id.toString())
                .signalEventSubscriptionName(signalName)
                .singleResult();
        if (execution == null) {
            log.warn("No execution found for signal: {}", signalName);
            return;
        }

        log.debug("Signaling event {} for execution {}", signalName, execution.getId());
        runtimeService.signalEventReceived(signalName, execution.getId());
    }
}
