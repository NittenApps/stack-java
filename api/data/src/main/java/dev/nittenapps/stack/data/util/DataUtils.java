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

package dev.nittenapps.stack.data.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DataUtils {
    @NonNull
    public static Pageable buildPageable(@NonNull MultiValueMap<String, String> params) {
        int page = Integer.parseInt(StringUtils.defaultIfBlank(params.getFirst("page"), "0"));
        int pageSize = Integer.parseInt(StringUtils.defaultIfBlank(params.getFirst("pageSize"), "0"));

        Pageable pageable = Pageable.unpaged();
        if (pageSize > 0) {
            pageable = PageRequest.of(page, pageSize, buildSort(params));
        }

        return pageable;
    }

    @NonNull
    public static Sort buildSort(@NonNull MultiValueMap<String, String> params) {
        String sortString = params.getFirst("sort");

        Sort sort = Sort.unsorted();
        if (StringUtils.isNotBlank(sortString)) {
            List<Sort.Order> orders = Arrays.stream(sortString.split(",")).map(s -> {
                if (StringUtils.substringAfter(s, " ").equalsIgnoreCase("desc")) {
                    return Sort.Order.desc(StringUtils.substringBefore(s, " "));
                }
                return Sort.Order.asc(StringUtils.substringBefore(s, " "));
            }).collect(Collectors.toList());
            sort = Sort.by(orders);
        }

        return sort;
    }

    private DataUtils() {
    }
}
