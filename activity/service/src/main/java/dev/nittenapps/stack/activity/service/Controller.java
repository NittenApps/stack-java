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

package dev.nittenapps.stack.activity.service;

import dev.nittenapps.stack.activity.api.Activity;
import dev.nittenapps.stack.api.*;
import dev.nittenapps.stack.config.service.ActivityService;
import dev.nittenapps.stack.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@RestController("activityController")
@RequestMapping(value = "/activity/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Activity", description = "Application activities operations")
@Slf4j
public class Controller {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final ActivityService activityService;

    private final BeanFactory beanFactory;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    private final SecurityUtils securityUtils;

    @GetMapping("/{activity}/fieldGroups")
    @Operation(summary = "Returns the list of field groups in the activity")
    public ResponseEntity<ApiResponse<? extends ListBody<?>>> fieldGroups(@PathVariable String activity) {
        return ResponseEntity.ok(new ApiResponse<>(new ListBody<>(activityService.getFieldGroups(activity)), null));
    }

    @GetMapping("/{activity}/{method:[a-zA-Z]+}")
    @Operation(summary = "Executes a method in the activity")
    public ResponseEntity<ApiResponse<? extends ApiBody>> get(@PathVariable String activity,
                                                              @PathVariable String method,
                                                              @RequestParam MultiValueMap<String, String> params) {
        User user = securityUtils.getUserDetails();
        log.debug("Get activity: {}, method: {}, params: {}, user: {}", activity, method, params, user);
        Activity<Object, Object, Object, Object> _activity = getActivity(activity);
        try {
            Method _method = getMethod(_activity.getClass(), method, MultiValueMap.class, User.class);
            return ResponseEntity.ok((ApiResponse<? extends ApiBody>)_method.invoke(_activity, params, user));
        } catch (NoSuchMethodException e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            throw new ApiException(500, new ApiMessage(ApiMessage.Level.ERROR, e));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            log.error(cause.getLocalizedMessage(), cause);
            if (cause instanceof NoSuchElementException ex) {
                throw ex;
            }
            if (cause instanceof ApiException ex) {
                throw ex;
            }
            throw new ApiException(500, new ApiMessage(ApiMessage.Level.ERROR, e));
        }
    }

    @GetMapping(value = "/{activity}")
    @Operation(summary = "Returns the list of tasks in the activity")
    @Parameters({
            @Parameter(name = "activity", required = true,
                       description = "The activity code, must correspond to a bean name that implements the Activity "
                               + "interface"),
            @Parameter(name = "page",
                       description = "The page number, defaults to 0"),
            @Parameter(name = "size",
                       description = "The page size, defaults to 20"),
            @Parameter(name = "sort",
                       description = "Comma separated list of attributes to sort the result"),
            @Parameter(name = "filters",
                       description = "Any other query param will be used to filter the items",
                       schema = @Schema(example = "attribute1=value1&attribute2=%value2%"))
    })
    public ResponseEntity<ApiResponse<? extends ListBody<?>>> list(
            @PathVariable String activity,
            @Parameter(hidden = true) @RequestParam MultiValueMap<String, String> params) {
        log.debug("List activity: {}, params: {}", activity, params);
        Activity<Object, Object, Object, Object> _activity = getActivity(activity);
        return ResponseEntity.ok(_activity.getList(params, securityUtils.getUserDetails()));
    }

    @GetMapping(value = "/{activity}/{id:[a-f\\d]{8}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{12}|[A-Z0-9_-]+}")
    @Operation(summary = "Returns the specified task in the activity, by id")
    public ResponseEntity<ApiResponse<? extends ObjectBody<?>>> objectById(@PathVariable String activity,
                                                                           @PathVariable String id) {
        User user = securityUtils.getUserDetails();
        Activity<Object, Object, Object, Object> _activity = getActivity(activity);
        if (id.matches("[a-f\\d]{8}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{4}-[a-f\\d]{12}")) {
            return ResponseEntity.ok(_activity.getObject(UUID.fromString(id), user));
        }
        return ResponseEntity.ok(_activity.getObject(id, user));
    }

    @PostMapping("/{activity}/{method}")
    public ResponseEntity<ApiResponse<? extends ApiBody>> post(@PathVariable String activity,
                                                               @PathVariable String method,
                                                               @RequestParam MultiValueMap<String, String> params,
                                                               @RequestBody Map<String, Object> body) {
        Activity<Object, Object, Object, Object> _activity = getActivity(activity);
        try {
            Method _method = getMethod(_activity.getClass(), method, MultiValueMap.class, Map.class, User.class);
            return ResponseEntity.ok((ApiResponse<? extends ApiBody>)_method.invoke(_activity, params, body,
                    securityUtils.getUserDetails()));
        } catch (NoSuchMethodException e) {
            throw new NoSuchElementException(e.getLocalizedMessage());
        } catch (IllegalAccessException e) {
            throw new ApiException(500, new ApiMessage(ApiMessage.Level.ERROR, e));
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            log.error(cause.getLocalizedMessage(), cause);
            if (cause instanceof NoSuchElementException ex) {
                throw ex;
            }
            if (cause instanceof ApiException ex) {
                throw ex;
            }
            throw new ApiException(500, new ApiMessage(ApiMessage.Level.ERROR, e));
        }
    }

    @PostMapping("/{activity}")
    public ResponseEntity<ApiResponse<? extends ObjectBody<?>>> save(@PathVariable String activity,
                                                                     @RequestBody Map<String, Object> body) {
        Activity<Object, Object, Object, Object> _activity = getActivity(activity);
        return ResponseEntity.ok(_activity.save(body, securityUtils.getUserDetails()));
    }

    @NonNull
    private Activity<Object, Object, Object, Object> getActivity(@NonNull String activity)
            throws NoSuchElementException {
        try {
            //noinspection unchecked
            return beanFactory.getBean(activity, Activity.class);
        } catch (BeansException e) {
            throw new NoSuchElementException(activity);
        }
    }

    @NonNull
    private Method getMethod(@NonNull Class<?> clazz, @NonNull String method, @Nullable Class<?>... parameterTypes)
            throws NoSuchMethodException {
        log.debug("Class: {}, getting method: {}, parameterTypes: {}", clazz, method, parameterTypes);
        try {
            return clazz.getDeclaredMethod(method, parameterTypes);
        } catch (NoSuchMethodException e) {
            if (clazz.getSuperclass() != null) {
                return getMethod(clazz.getSuperclass(), method, parameterTypes);
            }
            throw e;
        }
    }
}
