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

import dev.nittenapps.stack.data.domain.AbstractAttribute;
import dev.nittenapps.stack.data.domain.AbstractAttributeValue;
import dev.nittenapps.stack.data.domain.WithAttributes;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class providing methods for building pageable and sortable queries, as well as accessing specific attribute
 * values in a structured data context. This class is intended for use in applications requiring flexible data querying
 * or retrieval of attributes by code.
 * <p>
 * This is a final utility class and cannot be instantiated.
 */
public class DataUtils {
    /**
     * Builds a {@code Pageable} object based on the provided query parameters for pagination.
     *
     * @param params a {@code MultiValueMap} containing query parameters for pagination, including "page" and
     *               "pageSize". If "page" is missing or invalid, it defaults to 0. If "pageSize" is missing or invalid,
     *               it defaults to 0.
     * @return a {@code Pageable} object representing the specified page and page size. If "pageSize" is not greater
     * than 0, returns an unpaged {@code Pageable}.
     */
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

    /**
     * Builds a {@code Sort} object based on the specified query parameters.
     *
     * @param params a {@code MultiValueMap} containing query parameters, including a "sort" key. The value of "sort"
     *               should be a comma-separated list of field names optionally followed by "desc" to indicate
     *               descending order.
     * @return a {@code Sort} object representing the sorting criteria specified in the "sort" parameter. If the
     * parameter is empty or invalid, returns {@code Sort.unsorted()}.
     */
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

    /**
     * Retrieves the value of a specific attribute code from a given {@code WithAttributes} instance. The method
     * attempts to fetch the attribute by its code, extract its values, and return the first value, if available.
     *
     * @param withAttributes the instance containing a collection of attributes
     * @param code           the code of the attribute whose value is to be retrieved
     * @return an {@code Optional} containing the first value of the attribute with the specified code, or an empty
     * {@code Optional} if no value is found
     */
    public static Optional<AbstractAttributeValue<?>> getValue(@NonNull WithAttributes<?, ?> withAttributes,
                                                               @NonNull String code) {
        return Optional.ofNullable(withAttributes.getAttributes())
                .map(attributes -> attributes.get(code))
                .map(AbstractAttribute::getValues)
                .flatMap(values -> values.stream().findFirst());
    }

    private DataUtils() {
    }
}
