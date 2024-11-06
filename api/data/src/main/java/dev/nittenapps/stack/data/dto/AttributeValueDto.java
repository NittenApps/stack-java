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

package dev.nittenapps.stack.data.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * DTO for {@link dev.nittenapps.stack.data.domain.AttributeValue}
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class AttributeValueDto implements Serializable {
    @Serial private static final long serialVersionUID = -3668217203922914188L;

    private String codeValue;
    private String stringValue;
    private Double numberValue;
    private ZonedDateTime dateValue;
    private Boolean booleanValue;
    private String textValue;
    private CatalogValue catalogValue;

    @NoArgsConstructor @AllArgsConstructor
    public static class CatalogValue {
        public String code;
        public String name;
    }
}
