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

import lombok.extern.slf4j.Slf4j;
import org.geotools.referencing.GeodeticCalculator;
import org.geotools.referencing.datum.DefaultEllipsoid;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.lang.NonNull;

import java.awt.geom.Point2D;

/**
 * Utility class providing methods for operations with geometries. This class includes methods for parsing Well-Known
 * Text (WKT) strings into geometry objects, creating geographic points, and creating geometric circles.
 */
@Slf4j
@SuppressWarnings("unused")
public class GeometryUtils {
    private static final int DEFAULT_SRID = 4326;

    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING), DEFAULT_SRID);

    private GeometryUtils() {
    }

    /**
     * Converts a Well-Known Text (WKT) string into a Geometry object with a default spatial reference ID (SRID).
     *
     * @param wkt A non-null string in Well-Known Text (WKT) format representing a geometry.
     * @return A Geometry object parsed from the WKT string with the default SRID applied.
     * @throws ParseException If the WKT string cannot be parsed into a valid Geometry object.
     */
    @NonNull
    public static Geometry wktToGeometry(@NonNull String wkt) throws ParseException {
        return wktToGeometry(wkt, DEFAULT_SRID);
    }

    /**
     * Converts a Well-Known Text (WKT) string into a Geometry object with the specified spatial reference ID (SRID).
     *
     * @param wkt  A non-null string in Well-Known Text (WKT) format representing a geometry.
     * @param srid An integer specifying the Spatial Reference System Identifier (SRID) to associate with the geometry.
     * @return A Geometry object parsed from the WKT string and assigned the specified SRID.
     * @throws ParseException If the WKT string cannot be parsed into a valid Geometry object.
     */
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

    /**
     * Creates a Point geometry object representing a geographic location from the given latitude and longitude.
     *
     * @param lat The latitude of the geographic location.
     * @param lng The longitude of the geographic location.
     * @return A Point object representing the geographic location constructed with the specified latitude and longitude.
     */
    public static Point createGeoPoint(double lat, double lng) {
        return GEOMETRY_FACTORY.createPoint(new Coordinate(lng, lat));
    }

    /**
     * Creates a circular polygon geometry representing a geographic area around the given latitude and longitude,
     * with the specified radius in meters. The circle is approximated by a polygon with a dynamic number of sides
     * based on the radius to maintain accuracy and minimize computation cost.
     *
     * @param lat    The center's latitude of the circular area.
     * @param lng    The center's longitude of the circular area.
     * @param radius The radius of the circular area in meters.
     * @return A Geometry object representing the circular area as a polygon.
     */
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
