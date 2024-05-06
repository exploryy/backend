package com.hits.open.world.core.achievement.repository;

import java.time.OffsetDateTime;

public record ClientAchievementEntity(
        String userId,
        Long achievementId,
        OffsetDateTime achievementDate
) {
}
