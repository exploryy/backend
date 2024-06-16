package com.hits.open.world.core.quest.repository.entity.generated;

import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;

public record GeneratedDistanceQuest(
        String name,
        String description,
        DifficultyType difficultyType,
        TransportType transportType,
        Double routeDistance,
        String longitude,
        String latitude
) {
}
