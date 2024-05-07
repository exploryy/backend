package com.hits.open.world.public_interface.quest;

import com.hits.open.world.public_interface.route.CreateRouteDto;

public record CreatePointToPointQuestDto(
        CreateQuestDto questDto,
        CreateRouteDto routeDto
) {
}
