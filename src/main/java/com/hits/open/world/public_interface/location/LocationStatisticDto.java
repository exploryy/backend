package com.hits.open.world.public_interface.location;

import java.util.Optional;

public record LocationStatisticDto(
        String name,
        String email,
        String userId,
        String previousLatitude,
        String previousLongitude,
        int experience,
        int distance,
        int level,
        Optional<String> photoUrl
) {
}
