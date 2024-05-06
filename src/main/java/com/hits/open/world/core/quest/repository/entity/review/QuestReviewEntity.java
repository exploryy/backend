package com.hits.open.world.core.quest.repository.entity.review;

public record QuestReviewEntity(
        Long questReviewId,
        Integer score,
        String message,
        String clientId,
        Long questId
) {
}
