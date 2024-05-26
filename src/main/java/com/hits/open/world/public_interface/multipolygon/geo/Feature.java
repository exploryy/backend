package com.hits.open.world.public_interface.multipolygon.geo;

import lombok.Builder;

@Builder
public record Feature (
        String type,
        Object geometry,
        Properties properties
) {
}
