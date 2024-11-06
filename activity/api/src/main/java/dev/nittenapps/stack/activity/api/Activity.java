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

package dev.nittenapps.stack.activity.api;

import dev.nittenapps.stack.api.ApiResponse;
import dev.nittenapps.stack.api.ListBody;
import dev.nittenapps.stack.api.ObjectBody;
import lombok.NonNull;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface Activity<E, ID, L, O> {
    default ApiResponse<ListBody<L>> getList(@NonNull MultiValueMap<String, String> params, User user) {
        throw new NotImplementedException();
    }

    default ApiResponse<ObjectBody<O>> getObject(@NonNull UUID id, User user) {
        throw new NotImplementedException();
    }

    default ApiResponse<ObjectBody<O>> save(@NonNull Map<String, Object> body, @NonNull User user) {
        throw new NotImplementedException();
    }
}
