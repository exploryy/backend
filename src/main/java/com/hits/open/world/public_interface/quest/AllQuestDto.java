package com.hits.open.world.public_interface.quest;

import java.util.List;

public record AllQuestDto(
        List<CommonQuestDto> notCompleted,
        List<CommonQuestDto> active,
        List<CompletedQuestDto> completed
) {
}
