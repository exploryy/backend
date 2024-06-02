package com.hits.open.world.public_interface.quest;

import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;

import java.util.List;

public record CommonQuestDto(
        Long questId,
        String name,
        String description,
        DifficultyType difficultyType,
        QuestType questType,
        TransportType transportType,
        String longitude,
        String latitude,
        List<String> images
) {
}
