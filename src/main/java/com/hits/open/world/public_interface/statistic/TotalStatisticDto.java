package com.hits.open.world.public_interface.statistic;

import com.hits.open.world.public_interface.location.LocationStatisticDto;

import java.util.List;

public record TotalStatisticDto(
        List<LocationStatisticDto> bestUsers,
        int userPosition
) {
}
