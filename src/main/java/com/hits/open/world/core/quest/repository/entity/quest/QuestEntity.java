package com.hits.open.world.core.quest.repository.entity.quest;

import com.hits.open.world.public_interface.quest.CommonQuestDto;

public record QuestEntity(
        Long questId,
        String name,
        String description,
        DifficultyType difficultyType,
        QuestType questType,
        TransportType transportType
) {
    public static CommonQuestDto toDto(QuestEntity entity) {
        return new CommonQuestDto(
                entity.questId(),
                entity.name(),
                entity.description(),
                entity.difficultyType(),
                entity.questType(),
                entity.transportType()
        );
    }
}
