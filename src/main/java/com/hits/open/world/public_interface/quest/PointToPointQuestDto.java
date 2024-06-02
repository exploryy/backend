package com.hits.open.world.public_interface.quest;

import com.hits.open.world.public_interface.route.RouteDto;

public record PointToPointQuestDto(
        CommonQuestDto commonQuestDto,
        RouteDto route
) {
}
