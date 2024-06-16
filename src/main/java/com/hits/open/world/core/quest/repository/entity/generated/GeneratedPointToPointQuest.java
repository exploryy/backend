package com.hits.open.world.core.quest.repository.entity.generated;

import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;

import java.util.List;

public record GeneratedPointToPointQuest(
        String name,
        String description,
        DifficultyType difficultyType,
        TransportType transportType,
        List<GeneratedPoint> points
) {
}
