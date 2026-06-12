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
 * Copyright (c) 2026. NittenApps
 */

package dev.nittenapps.stack.config.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.nittenapps.stack.data.domain.AbstractAttributeValue;
import dev.nittenapps.stack.data.domain.AttributeId;
import dev.nittenapps.stack.data.domain.AttributeValueId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;

import java.io.Serial;

@Entity
@Table(name = "value_attribute_value")
@Audited
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true, doNotUseGetters = true)
public class CatalogValueAttributeValue extends AbstractAttributeValue<CatalogValueAttribute> {
    @Serial private static final long serialVersionUID = -3369418501538481465L;

    @EmbeddedId
    @AttributeOverride(name = "attributeId.parentId", column = @Column(name = "value_id"))
    private AttributeValueId id = new AttributeValueId(new AttributeId(), null);

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attributeId")
    @JoinColumns({
            @JoinColumn(name = "value_id", referencedColumnName = "value_id"),
            @JoinColumn(name = "code", referencedColumnName = "code")
    })
    @JsonIgnore @ToString.Exclude
    private CatalogValueAttribute attribute;
}
