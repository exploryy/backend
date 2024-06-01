package com.hits.open.world.core.multipolygon.factory.polygon.impl;

import com.hits.open.world.core.multipolygon.enums.FigureType;
import com.hits.open.world.core.multipolygon.factory.polygon.PolygonService;
import com.hits.open.world.core.multipolygon.properties.GeoProperties;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SquarePolygonService implements PolygonService {
    private final GeoProperties geoProperties;

    @Override
    public FigureType getType() {
        return FigureType.SQUARE;
    }

    @Override
    public Polygon constructPolygon(Point centralPoint) {
        double deltaLat = Math.toDegrees(Math.sqrt(2) * geoProperties.getMaxDistance() / geoProperties.getEarthRadius());
        double deltaLon = deltaLat / Math.cos(Math.toRadians(centralPoint.getY()));

        Coordinate[] coordinates = new Coordinate[5];
        coordinates[0] = new Coordinate(centralPoint.getX() - deltaLon, centralPoint.getY() - deltaLat);
        coordinates[1] = new Coordinate(centralPoint.getX() - deltaLon, centralPoint.getY() + deltaLat);
        coordinates[2] = new Coordinate(centralPoint.getX() + deltaLon, centralPoint.getY() + deltaLat);
        coordinates[3] = new Coordinate(centralPoint.getX() + deltaLon, centralPoint.getY() - deltaLat);
        coordinates[4] = coordinates[0];

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);

        return geometryFactory.createPolygon(linearRing);
    }

}
