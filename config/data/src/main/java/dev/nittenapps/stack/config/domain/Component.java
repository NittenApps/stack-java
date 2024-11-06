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

import dev.nittenapps.stack.data.domain.AbstractSimpleId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;

@Entity
@Table(name = "component")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "component_type", length = 1)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Component extends AbstractSimpleId {
    @NaturalId
    @Column(name = "code", nullable = false, updatable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @Column(name = "type", nullable = false, length = 2)
    private String type;

    @Column(name = "description", length = 2000)
    private String description;

    @Lob
    @Column(name = "definition")
    private String definition;

    @Column(name = "active")
    private boolean active;

    @ElementCollection
    @CollectionTable(name = "assigned_role", joinColumns = @JoinColumn(name = "component_id"))
    @SQLRestriction("type = 'V'")
    protected Set<AssignedRole> viewerRoles;

    @ElementCollection
    @CollectionTable(name = "assigned_role", joinColumns = @JoinColumn(name = "component_id"))
    @SQLRestriction("type = 'E'")
    protected Set<AssignedRole> editorRoles;
}
