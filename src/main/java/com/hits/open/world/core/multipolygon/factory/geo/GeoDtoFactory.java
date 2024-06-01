package com.hits.open.world.core.multipolygon.factory.geo;

import com.google.gson.Gson;
import com.hits.open.world.public_interface.multipolygon.geo.Feature;
import com.hits.open.world.public_interface.multipolygon.geo.GeoDto;
import com.hits.open.world.public_interface.multipolygon.geo.MultipolygonGeometry;
import com.hits.open.world.public_interface.multipolygon.geo.PolygonGeometry;
import com.hits.open.world.public_interface.multipolygon.geo.Properties;

import java.util.List;


public final class GeoDtoFactory {
    private static final Gson gson = new Gson();

    private GeoDtoFactory() {
        throw new UnsupportedOperationException("This class is a factory");
    }

    public static GeoDto buildMultiPolygonGeoDto(String geometry) {
        MultipolygonGeometry geometryDto = gson.fromJson(geometry, MultipolygonGeometry.class);

        Feature feature = Feature.builder()
                .type("Feature")
                .properties(new Properties())
                .geometry(geometryDto)
                .build();

        return GeoDto.builder()
                .type("FeatureCollection")
                .features(List.of(feature))
                .build();
    }

    public static GeoDto buildPolygonGeoDto(String geometry) {
        PolygonGeometry geometryDto = gson.fromJson(geometry, PolygonGeometry.class);

        Feature feature = Feature.builder()
                .type("Feature")
                .properties(new Properties())
                .geometry(geometryDto)
                .build();

        return GeoDto.builder()
                .type("FeatureCollection")
                .features(List.of(feature))
                .build();
    }
}

