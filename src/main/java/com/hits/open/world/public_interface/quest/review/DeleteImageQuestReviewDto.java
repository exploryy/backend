package com.hits.open.world.public_interface.quest.review;

public record DeleteImageQuestReviewDto(
        Long questId,
        Long reviewId,
        String userId,
        Long imageId
) {
}
