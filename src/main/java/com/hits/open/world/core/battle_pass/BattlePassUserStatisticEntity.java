package com.hits.open.world.core.battle_pass;

public record BattlePassUserStatisticEntity(
        Long battlePassId,
        String userId,
        int level,
        int currentExperience
) {
}
