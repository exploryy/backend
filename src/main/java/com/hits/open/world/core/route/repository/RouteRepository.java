package com.hits.open.world.core.route.repository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    void savePoints(List<PointRouteEntity> pointRouteEntity);
    RouteEntity saveRoute(RouteEntity routeEntity);
    Optional<RouteEntity> getRoute(Long routeId);
    List<PointRouteEntity> getPointsInRoute(Long routeId);
}
