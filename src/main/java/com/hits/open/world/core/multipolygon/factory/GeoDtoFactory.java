package com.hits.open.world.core.multipolygon.factory;

import com.google.gson.Gson;
import com.hits.open.world.public_interface.multipolygon.geo.Feature;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import com.hits.open.world.public_interface.multipolygon.geo.Geometry;
import com.hits.open.world.public_interface.multipolygon.geo.Properties;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public final class GeoDtoFactory {
    private static final Gson gson = new Gson();

    private GeoDtoFactory() {
        throw new UnsupportedOperationException("This class is a factory");
    }

    public static GeoDto buildPolygonGetDto(Polygon polygon) {
        Coordinate[] coordinates = polygon.getCoordinates();

        List<List<Double>> coordinatesList = Arrays.stream(coordinates)
                .map(coordinate -> List.of(coordinate.x, coordinate.y))
                .toList();

        Geometry geometry = Geometry.builder()
                .type("MultiPolygon")
                .coordinates(List.of((Collections.singletonList(coordinatesList))))
                .build();

        Feature feature = Feature.builder()
                .type("Feature")
                .geometry(geometry)
                .properties(new Properties())
                .build();

        return GeoDto.builder()
                .type("FeatureCollection")
                .features(List.of(feature))
                .build();
    }

    public static GeoDto buildMultiPolygonGeoDto(String geometry) {
        Geometry geometryDto = gson.fromJson(geometry, Geometry.class);

        Feature feature = Feature.builder()
                .type("Feature")
                .geometry(geometryDto)
                .build();

        return GeoDto.builder()
                .type("FeatureCollection")
                .features(List.of(feature))
                .build();
    }
}

