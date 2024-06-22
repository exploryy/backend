package com.hits.open.world.public_interface.client.map;

import com.hits.open.world.core.quest.repository.entity.generated.GeneratedPoint;

import java.util.List;

public record WayFromPointToPointDto(
        List<GeneratedPoint> points,
        double distance
) {
}
