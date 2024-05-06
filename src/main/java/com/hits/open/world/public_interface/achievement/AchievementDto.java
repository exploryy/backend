package com.hits.open.world.public_interface.achievement;

import java.time.OffsetDateTime;

public record AchievementDto(
        Long achievementId,
        String name,
        String description,
        String imageUri,
        Boolean isCompleted,
        OffsetDateTime completionDate
) {
}
