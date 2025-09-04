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

package dev.nittenapps.stack.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Represents a paginated list structure to be used as a response body in API responses.
 * <p>
 * The class encapsulates the data items in the list, the current page number, and the total number of items available.
 * It provides constructors to initialize the instance with a list of items, automatically setting the page to 1 and
 * calculating the total number of items if only the list is provided.
 *
 * @param <T> the type of items contained within the list
 */
@Data
@NoArgsConstructor @AllArgsConstructor
public class ListBody<T> implements ApiBody {
    private List<T> items;

    private int page;

    private long total;

    /**
     * Constructs a new ListBody instance with the provided list of items. Initializes the current page to 1 and
     * calculates the total number of items in the provided list. If the list is null, the total is set to 0.
     *
     * @param items the list of items to be contained within this ListBody instance
     */
    public ListBody(List<T> items) {
        this.items = items;
        this.page = 1;
        this.total = items == null ? 0 : items.size();
    }
}
