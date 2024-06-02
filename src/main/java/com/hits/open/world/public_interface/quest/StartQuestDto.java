package com.hits.open.world.public_interface.quest;

public record StartQuestDto(
        Long questId,
        String userId,
        String transportType
) {
}
