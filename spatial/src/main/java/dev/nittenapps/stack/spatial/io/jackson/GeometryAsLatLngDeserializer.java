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
import org.locationtech.jts.geom.*;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * A custom deserializer for converting JSON objects with latitude and longitude fields into JTS {@link Geometry}
 * objects, specifically {@link Point}. This deserializer expects the JSON input to contain "lat" and "lng" fields
 * corresponding to the latitude and longitude, respectively.
 * <p>
 * The deserialization process uses a {@link GeometryFactory}, initialized with a {@link PrecisionModel} and a spatial
 * reference identifier (SRID) set to {@code 4326}, to create the {@link Point}.
 * <p>
 * The JSON format is expected to adhere to the following structure:
 * <code><pre>
 * {
 *   "lng": <i>&lt;longitude as double&gt;</i>,
 *   "lat": <i>&lt;latitude as double&gt;</i>
 * }
 * </pre></code>
 * <p>
 * Throws:
 * <ul>
 *   <li>{@link IOException}: If there is an error reading or parsing the JSON input.</li>
 * </ul>
 * <p>
 * Usage scenarios:
 * <ul>
 *   <li>Suitable for applications requiring conversion of JSON spatial data with longitude and latitude fields into JTS
 *   geometry objects.</li>
 *   <li>Commonly used in geospatial applications where JSON serves as the data exchange format.</li>
 * </ul>
 */
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
