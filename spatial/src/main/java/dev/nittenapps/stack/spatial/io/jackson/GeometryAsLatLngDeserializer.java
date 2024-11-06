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

package dev.nittenapps.stack.spatial.io.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.NonNull;
import org.locationtech.jts.geom.*;

import java.io.IOException;

@SuppressWarnings("unused")
public class GeometryAsLatLngDeserializer extends JsonDeserializer<Geometry> {
    private static final int DEFAULT_SRID = 4326;

    private static final GeometryFactory DEFAULT_GEOMETRY_FACTORY = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING), DEFAULT_SRID);

    @Override
    public Geometry deserialize(@NonNull JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode node = jsonParser.readValueAs(JsonNode.class);
        Coordinate coordinate = new CoordinateXY(node.get("lng").asDouble(), node.get("lat").asDouble());
        return DEFAULT_GEOMETRY_FACTORY.createPoint(coordinate);
    }
}
