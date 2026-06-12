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

import dev.nittenapps.stack.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * The {@code StoreUserListener} class is a Flowable event listener that handles specific Flowable events and performs
 * actions accordingly. This listener is mainly used to process and store user-related information when a task is
 * completed within the Flowable BPM engine.
 * <p>
 * It relies on {@code FlowableUtils} for interaction with Flowable's runtime services and {@code SecurityUtils} to
 * retrieve user authentication information. The main functionality of this listener is to set the currently
 * authenticated user's username to the execution's variables when a task completion event is triggered.
 * <p>
 * The class implements the {@code FlowableEventListener} interface and provides custom logic for handling Flowable
 * events, along with configurations for event lifecycle behavior.
 */
@Component
@RequiredArgsConstructor
public class StoreUserListener implements FlowableEventListener {
    private final FlowableUtils flowableUtils;

    private final SecurityUtils securityUtils;

    /**
     * Handles the Flowable event triggered in the BPM engine. Performs specific operations based on the event type,
     * such as setting authenticated user information into execution variables when a task completion event is detected.
     *
     * @param event the Flowable event that has been triggered. It must not be null. The event contains details such as
     *              the event type and related entities.
     */
    @Override
    public void onEvent(@NonNull FlowableEvent event) {
        if (event.getType() == FlowableEngineEventType.TASK_COMPLETED) {
            FlowableEngineEntityEvent entityEvent = (FlowableEngineEntityEvent)event;
            flowableUtils.setVariablesToExecution(entityEvent.getExecutionId(),
                    Map.of("_user", securityUtils.getUsername()));
        }
    }

    /**
     * Indicates whether the listener should fail and propagate an exception if an error occurs during event processing.
     *
     * @return {@code false}, as this listener does not fail on exceptions, allowing the process to continue even if an
     * exception occurs.
     */
    @Override
    public boolean isFailOnException() {
        return false;
    }

    /**
     * Determines whether the listener should respond to events that occur during the transaction lifecycle.
     *
     * @return {@code false}, indicating that this listener does not fire on transaction lifecycle events.
     */
    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return false;
    }

    /**
     * Retrieves the transaction-related information associated with the listener.
     *
     * @return an empty {@code String}, as this implementation does not respond to transaction lifecycle events.
     */
    @Override
    public String getOnTransaction() {
        return "";
    }
}
