package com.hits.open.world.core.route.repository;

import java.util.List;

public record RouteEntity(
        Long routeId,
        Double distance,
        List<PointEntity> points
) {
}
