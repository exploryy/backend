package com.hits.open.world.core.achievement.repository;

import com.hits.open.world.public_interface.achievement.AchievementDto;

import java.util.List;
import java.util.Optional;

public interface AchievementRepository {
    AchievementEntity createAchievement(AchievementEntity achievementEntity);

    void updateAchievement(AchievementEntity achievementEntity);

    void deleteAchievement(Long achievementId);

    Optional<AchievementEntity> getAchievementById(Long achievementId);

    Optional<AchievementEntity> getAchievementByName(String name);

    List<AchievementDto> getAchievements(String userId);

    void createClientAchievement(ClientAchievementEntity clientAchievementEntity);
}
