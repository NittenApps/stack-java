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
import dev.nittenapps.stack.data.domain.WithAttributes;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "catalog_value")
@Audited
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true)
public class CatalogValue extends WithAttributes<CatalogValueAttribute, CatalogValueAttributeValue> {
    @NaturalId
    @ManyToOne(optional = false)
    @JsonIgnore @ToString.Exclude
    private Catalog catalog;

    @NaturalId
    @Column(name = "code", nullable = false, updatable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "active")
    private boolean active;

    @OneToMany(mappedBy = "catalogValue", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @MapKey(name = "id.code")
    @JsonIgnore @ToString.Exclude
    private final Map<String, CatalogValueAttribute> attributes = new HashMap<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        CatalogValue that = (CatalogValue)o;
        return Objects.equals(catalog, that.catalog) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(catalog);
        result = 31 * result + Objects.hashCode(code);
        return result;
    }
}
