package com.hits.open.world.public_interface.route;

import java.util.List;

public record RouteDto(
        List<PointDto> points,
        Double distance,
        Long routeId
) {
}
