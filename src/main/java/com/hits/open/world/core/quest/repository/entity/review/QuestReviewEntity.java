package com.hits.open.world.core.quest.repository.entity.review;

import java.time.OffsetDateTime;
import java.util.Optional;

public record QuestReviewEntity(
        Long questReviewId,
        Integer score,
        Optional<String> message,
        String clientId,
        Long questId,
        OffsetDateTime createdAt
) {
}
