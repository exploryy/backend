package com.hits.open.world.public_interface.quest.review;

import java.util.List;

public record FullQuestReviewDto (
        Double avg,
        List<QuestReviewDto> reviews
) {
}
