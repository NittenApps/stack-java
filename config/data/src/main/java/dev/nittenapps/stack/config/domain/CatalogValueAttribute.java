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
import dev.nittenapps.stack.data.domain.AbstractAttribute;
import dev.nittenapps.stack.data.domain.AttributeId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "value_attribute")
@Audited
@Getter @Setter
@NoArgsConstructor
@ToString(callSuper = true, doNotUseGetters = true)
public class CatalogValueAttribute extends AbstractAttribute<CatalogValueAttributeValue> {
    @EmbeddedId
    @AttributeOverride(name = "parentId", column = @Column(name = "value_id"))
    private AttributeId id = new AttributeId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "value_id", referencedColumnName = "id")
    @JsonIgnore @ToString.Exclude
    private CatalogValue catalogValue;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore @ToString.Exclude
    private Set<CatalogValueAttributeValue> values = new HashSet<>();

    public CatalogValueAttribute(AttributeId id, String type) {
        super(id, type);

        this.id = id;
    }

    @Override
    public void setParent(Object parent) {
        this.catalogValue = (CatalogValue)parent;
    }
}
