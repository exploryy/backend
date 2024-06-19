package com.hits.open.world.public_interface.quest;

import com.hits.open.world.public_interface.quest.review.FullQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.QuestReviewDto;

import java.util.List;

public record DistanceQuestDto(
        CommonQuestDto commonQuestDto,
        Double distance,
        String longitude,
        String latitude,
        FullQuestReviewDto fullReviewDto
) {
}
