package com.hits.open.world.public_interface.quest.review;

public record DeleteQuestReviewDto(
        Long questReviewId,
        Long questId,
        String clientId
) {
}
