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

package dev.nittenapps.stack.data.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(doNotUseGetters = true)
public class AttributeValue {
    @Column(name = "code_value")
    private String codeValue;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "number_value")
    private Double numberValue;

    @Column(name = "date_value")
    private ZonedDateTime dateValue;

    @Column(name = "boolean_value")
    private Boolean booleanValue;

    @Lob
    @Column(name = "text_value")
    private String textValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeValue that = (AttributeValue)o;
        return Objects.equals(codeValue, that.codeValue) && Objects.equals(stringValue,
                that.stringValue) && Objects.equals(numberValue, that.numberValue) && Objects.equals(
                dateValue, that.dateValue) && Objects.equals(booleanValue, that.booleanValue)
                && Objects.equals(textValue, that.textValue);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(codeValue);
        result = 31 * result + Objects.hashCode(stringValue);
        result = 31 * result + Objects.hashCode(numberValue);
        result = 31 * result + Objects.hashCode(dateValue);
        result = 31 * result + Objects.hashCode(booleanValue);
        result = 31 * result + Objects.hashCode(textValue);
        return result;
    }
}
