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
import dev.nittenapps.stack.data.domain.AttributeValue;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLOrder;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "value_attribute")
@Audited
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true)
public class CatalogValueAttribute extends AbstractAttribute {
    @EmbeddedId
    @AttributeOverride(name = "parentId", column = @Column(name = "value_id"))
    private AttributeId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("parentId")
    @JoinColumn(name = "value_id", referencedColumnName = "id")
    @JsonIgnore @ToString.Exclude
    private CatalogValue catalogValue;

    @ElementCollection
    @CollectionTable(name = "value_attribute_value",
                     joinColumns = {
                             @JoinColumn(name = "value_id", referencedColumnName = "value_id"),
                             @JoinColumn(name = "code", referencedColumnName = "code")
                     })
    @SQLOrder("position")
    private List<AttributeValue> values = new ArrayList<>();

    public CatalogValueAttribute(AttributeId id, CatalogValue catalogValue, String type) {
        super(type);
        this.id = id;
        this.catalogValue = catalogValue;
    }

    @Override
    public void setParent(Object parent) {
        setCatalogValue((CatalogValue)parent);
    }
}
