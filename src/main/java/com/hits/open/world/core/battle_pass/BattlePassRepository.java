package com.hits.open.world.core.battle_pass;

import java.util.List;
import java.util.Optional;

public interface BattlePassRepository {
    Optional<BattlePassEntity> getBattlePass(Long battlePassId);
    Optional<BattlePassEntity> getCurrentBattlePass();
    Optional<BattlePassUserStatisticEntity> getUserStatisticInBattlePass(String userId, Long battlePassId);
    void addUserToBattlePass(String userId, Long battlePassId);
    List<BattlePassEntity> getAllBattlePasses();
    int sumPreviousLevelsExperience(Long battlePassId, int level);
    void updateLevelAndExperience(String userId, Long battlePassId, int level, int experience);
    int getMaxLevel(Long battlePassId);
}
