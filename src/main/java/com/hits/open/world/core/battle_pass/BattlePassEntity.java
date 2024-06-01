package com.hits.open.world.core.battle_pass;

import java.time.LocalDateTime;
import java.util.List;

public record BattlePassEntity(
        Long battlePassId,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        List<BattlePassLevel> levels
) {
    public record BattlePassLevel(
            Long battlePassId,
            Integer level,
            Integer experienceNeeded,
            List<BattlePassReward> rewards
    ) {
        public record BattlePassReward(
                Long battlePassId,
                Integer level,
                Long itemId
        ) {
        }
    }
}
