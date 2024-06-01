package com.hits.open.world.public_interface.battle_pass;

import com.hits.open.world.public_interface.cosmetic_item.CosmeticItemDto;

import java.time.LocalDateTime;
import java.util.List;

public record BattlePassDto(
        Long battlePassId,
        String name,
        String description,
        LocalDateTime startDate,
        LocalDateTime endDate,
        int currentLevel,
        List<BattlePassLevelDto> levels
) {
    public record BattlePassLevelDto(
            Integer level,
            Integer experienceNeeded,
            List<BattlePassRewardDto> rewards
    ) {
        public record BattlePassRewardDto(
                CosmeticItemDto item
        ) {
        }
    }
}
