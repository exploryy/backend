package com.hits.open.world.public_interface.quest;

import java.util.Optional;

public record UpdateQuestDto(
        Long questId,
        Optional<String> name,
        Optional<String> description,
        Optional<String> difficultyType,
        Optional<String> questType,
        Optional<String> transportType
) {
}
