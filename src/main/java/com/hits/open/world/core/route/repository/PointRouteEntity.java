package com.hits.open.world.core.route.repository;

public record PointRouteEntity(
        String longitude,
        String latitude,
        String previousLongitude,
        String previousLatitude
) {
}
