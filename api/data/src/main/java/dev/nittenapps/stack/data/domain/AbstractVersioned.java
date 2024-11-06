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

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.*;

import java.io.Serializable;

@MappedSuperclass
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public abstract class AbstractVersioned<ID extends Serializable> implements Versioned<ID> {
    @Version @Column(name = "version", nullable = false)
    protected Integer version;

    @Override
    @Transient
    @JsonIgnore
    public boolean isNew() {
        return version == null;
    }
}
