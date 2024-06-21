package com.hits.open.world.public_interface.location;

import com.hits.open.world.public_interface.user.ProfileDto;
import lombok.Builder;

@Builder
public record LocationStatisticDto(
        String previousLatitude,
        String previousLongitude,
        Integer experience,
        Integer distance,
        Integer level,
        Integer totalExperienceInLevel,
        ProfileDto profileDto
) {
}
