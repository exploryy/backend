package com.hits.open.world.core.multipolygon.factory.polygon.impl;

import com.hits.open.world.core.multipolygon.factory.polygon.PolygonService;
import com.hits.open.world.core.multipolygon.properties.GeoProperties;
import com.hits.open.world.core.multipolygon.enums.FigureType;
import com.vividsolutions.jts.geom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CirclePolygonService implements PolygonService {
    private final GeoProperties geoProperties;

    @Override
    public FigureType getType() {
        return FigureType.CIRCLE;
    }

    @Override
    public Polygon constructPolygon(Point centralPoint) {
        double deltaLat = Math.toDegrees(geoProperties.getMaxDistance() / geoProperties.getEarthRadius());
        double deltaLon = deltaLat / Math.cos(Math.toRadians(centralPoint.getY()));

        int sides = 100;
        Coordinate[] coordinates = new Coordinate[sides + 1];
        for (int i = 0; i < sides; i++) {
            double angle = Math.toRadians(i * 360.0 / sides);
            double x = centralPoint.getX() + deltaLon * Math.cos(angle);
            double y = centralPoint.getY() + deltaLat * Math.sin(angle);
            coordinates[i] = new Coordinate(x, y);
        }
        coordinates[sides] = coordinates[0];

        GeometryFactory geometryFactory = new GeometryFactory();
        LinearRing linearRing = geometryFactory.createLinearRing(coordinates);

        return geometryFactory.createPolygon(linearRing);
    }
}
