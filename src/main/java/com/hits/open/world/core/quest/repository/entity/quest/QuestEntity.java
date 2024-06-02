package com.hits.open.world.core.quest.repository.entity.quest;

public record QuestEntity(
        Long questId,
        String name,
        String description,
        DifficultyType difficultyType,
        QuestType questType,
        TransportType transportType
) {
}
