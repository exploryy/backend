package com.hits.open.world.public_interface.statistic;

import com.hits.open.world.public_interface.user.ProfileDto;

import java.util.List;

public record TotalStatisticDto(
        List<ProfileDto> bestUsers,
        int userPosition
) {
}
