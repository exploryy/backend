package com.hits.open.world.public_interface.quest;

public record CreateDistanceQuestDto(
        CreateQuestDto questDto,
        Double distance
) {
}
