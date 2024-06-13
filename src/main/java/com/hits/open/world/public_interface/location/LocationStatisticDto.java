package com.hits.open.world.public_interface.location;

import lombok.Builder;

import java.util.Optional;

@Builder
public record LocationStatisticDto(
        String username,
        String email,
        String userId,
        String previousLatitude,
        String previousLongitude,
        int experience,
        int distance,
        int level,
        int totalExperienceInLevel,
        Optional<String> photoUrl
) {
}
