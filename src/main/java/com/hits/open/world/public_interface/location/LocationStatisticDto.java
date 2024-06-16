package com.hits.open.world.public_interface.location;

import com.hits.open.world.public_interface.user.ProfileDto;
import lombok.Builder;

import java.util.Optional;

@Builder
public record LocationStatisticDto(
        String previousLatitude,
        String previousLongitude,
        int experience,
        int distance,
        int level,
        int totalExperienceInLevel,
        ProfileDto profileDto
) {
}
