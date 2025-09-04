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

package dev.nittenapps.stack.data.dto;

import java.util.List;
import java.util.Map;

/**
 * WithAttributesDto is an interface that extends the {@link SimpleIdDto} interface, representing a Data Transfer Object
 * (DTO) with a unique identifier and a collection of attributes. It is typically used for entities that need to
 * dynamically manage a set of attribute key-value pairs.
 * <p>
 * The attributes are represented as a map, where the keys are strings and the values are lists of
 * {@link AttributeValueDto} objects. This design allows for multiple values to be associated with a single attribute
 * key.
 * <p>
 * It provides methods to retrieve and set the attribute map, enabling flexibility in handling attribute-based
 * entities.
 */
public interface WithAttributesDto extends SimpleIdDto {
    /**
     * Retrieves the attributes associated with the implementing entity. The attributes are represented as a map where
     * the keys are strings representing attribute names and the values are lists of {@link AttributeValueDto} objects,
     * allowing for multiple values per attribute.
     *
     * @return a map containing the attributes of the entity, with keys as attribute names (strings) and values as lists
     * of {@link AttributeValueDto} instances
     */
    Map<String, List<AttributeValueDto>> getAttributes();

    /**
     * Sets the attributes for the implementing entity. The attributes are represented as a map where keys are strings
     * representing attribute names, and values are lists of {@link AttributeValueDto} objects. This design allows for
     * associating multiple values with a single attribute key.
     *
     * @param attributes a list containing the attributes to be set, with keys as attribute names (strings) and values
     *                   as lists of {@link AttributeValueDto} instances
     */
    void setAttributes(Map<String, List<AttributeValueDto>> attributes);
}
