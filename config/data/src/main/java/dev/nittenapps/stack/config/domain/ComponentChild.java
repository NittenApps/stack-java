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

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.nittenapps.stack.data.domain.AbstractVersioned;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "component_child")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true, doNotUseGetters = true)
public class ComponentChild extends AbstractVersioned<ComponentChildId> {
    @EmbeddedId
    private ComponentChildId id;

    @ManyToOne
    @MapsId("componentId")
    @JsonIgnore @ToString.Exclude
    private Component component;

    @ManyToOne
    @MapsId("childId")
    @JsonIgnore @ToString.Exclude
    private Component child;

    @Column(name = "position", nullable = false)
    private int position;

    @Lob
    @Column(name = "definition")
    private String definition;

    @ElementCollection
    @CollectionTable(name = "assigned_child_role", joinColumns = {@JoinColumn(name = "component_id"),
            @JoinColumn(name = "child_id")})
    @SQLRestriction("type = 'V'")
    protected Set<AssignedRole> viewerRoles;

    @ElementCollection
    @CollectionTable(name = "assigned_child_role", joinColumns = {@JoinColumn(name = "component_id"),
            @JoinColumn(name = "child_id")})
    @SQLRestriction("type = 'E'")
    protected Set<AssignedRole> editorRoles;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ComponentChild that = (ComponentChild)o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
