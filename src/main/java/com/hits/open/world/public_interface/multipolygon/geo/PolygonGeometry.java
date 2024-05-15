package com.hits.open.world.public_interface.multipolygon.geo;

import java.util.List;

public record PolygonGeometry(
        String type,
        List<List<List<Double>>> coordinates
) {
}
