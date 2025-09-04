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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.lang.NonNull;

import java.io.IOException;

/**
 * A custom serializer for converting JTS {@link Geometry} objects into JSON objects with latitude and longitude fields,
 * specifically for {@link Point} geometries.
 * <p>
 * The serialization process produces a JSON structure containing "lng" and "lat" fields where:
 * <ul>
 *   <li>"lng" represents the x-coordinate (longitude) of the {@link Point}.</li>
 *   <li>"lat" represents the y-coordinate (latitude) of the {@link Point}.</li>
 * </ul>
 * <p>
 * Example JSON structure produced:
 * <code><pre>
 * {
 *   "lng": <i>&lt;longitude as double>&gt;</i>,
 *   "lat": <i>&lt;latitude as double>&gt;</i>
 * }
 * </pre></code>
 * <p>
 * This serializer supports only {@link Point} geometries. If a geometry type other than {@link Point} is encountered,
 * an {@link UnsupportedOperationException} is thrown with a message indicating the unsupported geometry type.
 * <p>
 * Throws:
 * <ul>
 *   <li>{@link UnsupportedOperationException}: If the provided {@link Geometry} is not a {@link Point}.</li>
 *   <li>{@link IOException}: If an error occurs during JSON generation.</li>
 * </ul>
 * <p>
 * Usage scenarios:
 * <ul>
 *   <li>Suitable for applications requiring conversion of JTS {@link Point} objects into JSON spatial data with
 *   longitude and latitude fields.</li>
 *   <li>Commonly used in geospatial applications that work with JSON as a data exchange format.</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class GeometryAsLatLngSerializer extends JsonSerializer<Geometry> {
    @Override
    public void serialize(Geometry geometry, @NonNull JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        if (geometry instanceof Point point) {
            jsonGenerator.writeNumberField("lng", point.getX());
            jsonGenerator.writeNumberField("lat", point.getY());
        } else {
            throw new UnsupportedOperationException("unknown: " + geometry);
        }
        jsonGenerator.writeEndObject();
    }
}
