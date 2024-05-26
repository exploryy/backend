package com.hits.open.world.public_interface.multipolygon.geo;

import lombok.Builder;

import java.util.List;

@Builder
public record GeoDto(
        String type,
        List<Feature> features
) {
}
