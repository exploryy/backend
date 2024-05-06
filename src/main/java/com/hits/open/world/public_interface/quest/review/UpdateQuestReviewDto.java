package com.hits.open.world.public_interface.quest.review;

import java.util.Optional;

public record UpdateQuestReviewDto(
        Long questId,
        Long questReviewId,
        String clientId,
        Optional<Integer> score,
        Optional<String> message
) {
}
