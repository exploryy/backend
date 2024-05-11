package com.hits.open.world.core.multipolygon;

import com.hits.open.world.core.multipolygon.properties.GeoProperties;
import com.hits.open.world.core.multipolygon.repository.MultipolygonRepository;
import com.hits.open.world.public_interface.multipolygon.AreaDto;
import com.hits.open.world.public_interface.multipolygon.CoordinateDto;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import com.vividsolutions.jts.geom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.hits.open.world.core.multipolygon.factory.GeoDtoFactory.buildMultiPolygonGeoDto;
import static com.hits.open.world.core.multipolygon.factory.GeoDtoFactory.buildPolygonGetDto;


@Service
@RequiredArgsConstructor
public class MultipolygonService {
    private final MultipolygonRepository multipolygonRepository;
    private final GeoProperties geoProperties;

    public GeoDto getAllPolygons(String userId) {
        var geoString = multipolygonRepository.getAllCoordinates(userId);
        return buildMultiPolygonGeoDto(geoString);
    }

    public void delete(String userId) {
        multipolygonRepository.delete(userId);
    }

    public AreaDto calculateArea(String userId) {
        var area = multipolygonRepository.calculateArea(userId);
        return new AreaDto(area);
    }

    public GeoDto save(CoordinateDto coordinateDto, String userId) {
        Point point = new GeometryFactory().createPoint(new Coordinate(coordinateDto.longitude().doubleValue(), coordinateDto.latitude().doubleValue()));
        var polygon = createPointInDirection(point);

        multipolygonRepository.insert(userId, polygon);

        return buildPolygonGetDto(polygon);
    }

    private Polygon createPointInDirection(com.vividsolutions.jts.geom.Point centralPoint) {
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
