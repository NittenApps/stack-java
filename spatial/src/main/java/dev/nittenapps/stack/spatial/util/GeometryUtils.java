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

package dev.nittenapps.stack.spatial.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.awt.geom.Point2D;

@Slf4j
@SuppressWarnings("unused")
public class GeometryUtils {
    private static final int DEFAULT_SRID = 4326;

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING), DEFAULT_SRID);

    private GeometryUtils() {
    }

    @NonNull
    public static Geometry wktToGeometry(@NonNull String wkt) throws ParseException {
        return wktToGeometry(wkt, DEFAULT_SRID);
    }

    @NonNull
    public static Geometry wktToGeometry(@NonNull String wkt, int srid) throws ParseException {
        try {
            WKTReader reader = new WKTReader();
            Geometry geometry = reader.read(wkt);
            geometry.setSRID(srid);
            return geometry;
        } catch (ParseException e) {
            log.error("Could not parse text to geometry {}", wkt, e);
            throw e;
        }
    }

    public static Point createGeoPoint(double lat, double lng) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
    }

    public static Geometry createGeoCircle(double lat, double lng, double radius) {
        GeodeticCalculator calculator = new GeodeticCalculator(DefaultEllipsoid.WGS84);
        calculator.setStartingGeographicPoint(lng, lat);
        final int sides = 32 + 16 * ((int)Math.ceil(radius / 40) / 5);

        double baseAzimuth = 360d / sides;
        Coordinate[] coords = new Coordinate[sides + 1];
        for (int i = 0; i < sides; i++) {
            double azimuth = 180 - (i * baseAzimuth);
            calculator.setDirection(azimuth, radius);
            Point2D point = calculator.getDestinationGeographicPoint();
            coords[i] = new Coordinate(point.getX(), point.getY());
        }
        coords[sides] = coords[0];

        LinearRing ring = GEOMETRY_FACTORY.createLinearRing(coords);
        return GEOMETRY_FACTORY.createPolygon(ring);
    }
}
