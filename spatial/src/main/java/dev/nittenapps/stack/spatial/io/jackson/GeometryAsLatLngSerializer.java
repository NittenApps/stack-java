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
import lombok.NonNull;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

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
