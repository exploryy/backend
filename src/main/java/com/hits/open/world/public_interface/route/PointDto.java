package com.hits.open.world.public_interface.route;

public record PointDto(
        String latitude,
        String longitude,
        String previousLatitude,
        String previousLongitude
) {
}
