package com.hits.open.world.public_interface.quest;

public record DistanceQuestDto(
        CommonQuestDto commonQuestDto,
        Double distance,
        String longitude,
        String latitude
) {
}
