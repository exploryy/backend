package com.hits.open.world.public_interface.route;

import java.util.List;

public record CreateRouteDto(
        List<PointDto> points
) {
}
