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

package dev.nittenapps.stack.core.domain;

import dev.nittenapps.stack.data.domain.AbstractSimpleId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "sequence")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString
public class Sequence extends AbstractSimpleId {
    @NaturalId
    @Column(name = "code", nullable = false, updatable = false, length = 50)
    private String code;

    @NaturalId
    @Column(name = "prefix", updatable = false, length = 50)
    private String prefix;

    @NaturalId
    @Column(name = "suffix", updatable = false, length = 50)
    private String suffix;

    @Column(name = "current_value", nullable = false)
    private int currentValue = 0;
}
