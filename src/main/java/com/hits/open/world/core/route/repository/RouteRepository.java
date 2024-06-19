package com.hits.open.world.core.route.repository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    RouteEntity saveRoute(RouteEntity routeEntity);

    Optional<RouteEntity> getRoute(Long routeId);

    List<Long> savePoints(List<PointEntity> points);

    Optional<PointEntity> getPoint(Long pointId);
}
