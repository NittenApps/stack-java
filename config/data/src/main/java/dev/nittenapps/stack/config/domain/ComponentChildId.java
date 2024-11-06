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

package dev.nittenapps.stack.config.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ComponentChildId implements Serializable {
    @Serial private static final long serialVersionUID = -7845454784827292268L;

    @Column(name = "component_id", nullable = false)
    private UUID componentId;

    @Column(name = "child_id", nullable = false)
    private UUID childId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentChildId that = (ComponentChildId)o;
        return Objects.equals(componentId, that.componentId) && Objects.equals(childId, that.childId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(componentId);
        result = 31 * result + Objects.hashCode(childId);
        return result;
    }
}
