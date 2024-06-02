package com.hits.open.world.public_interface.statistic;

public record ExperienceDto(
        int currentExperience,
        int distance,
        boolean isNewTerritory
) {
}
