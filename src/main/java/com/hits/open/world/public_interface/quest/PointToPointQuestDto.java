package com.hits.open.world.public_interface.quest;

import com.hits.open.world.public_interface.quest.review.FullQuestReviewDto;
import com.hits.open.world.public_interface.quest.review.QuestReviewDto;
import com.hits.open.world.public_interface.route.RouteDto;

import java.util.List;

public record PointToPointQuestDto(
        CommonQuestDto commonQuestDto,
        RouteDto route,
        FullQuestReviewDto fullReviewDto
) {
}
