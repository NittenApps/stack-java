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
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "catalog")
@Audited
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@ToString(callSuper = true)
public class Catalog extends AbstractSimpleId {
    @NaturalId
    @Column(name = "code", nullable = false, updatable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 500)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "sort_by", length = 50)
    private String sortBy;

    @Column(name = "active")
    private boolean active;

    @ElementCollection
    @CollectionTable(name = "attribute", joinColumns = @JoinColumn(name = "catalog_id"))
    @OrderBy("position")
    @NotAudited
    private List<CatalogAttribute> attributes = new ArrayList<>();

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

        Catalog catalog = (Catalog)o;
        return Objects.equals(code, catalog.code);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(code);
        return result;
    }
}
