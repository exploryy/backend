package com.hits.open.world.public_interface.multipolygon.geo;

import lombok.Builder;

import java.util.List;

@Builder
public record Geometry (
        String type,
        List<List<List<List<Double>>>> coordinates
) {
}
