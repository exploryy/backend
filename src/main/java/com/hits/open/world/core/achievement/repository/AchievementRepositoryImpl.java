package com.hits.open.world.core.achievement.repository;

import com.hits.open.world.public_interface.achievement.AchievementDto;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementRepositoryImpl implements AchievementRepository {
    private final DSLContext create;

    @Override
    public AchievementEntity createAchievement(AchievementEntity achievementEntity) {
        return null;
    }

    @Override
    public void updateAchievement(AchievementEntity achievementEntity) {

    }

    @Override
    public void deleteAchievement(Long achievementId) {

    }

    @Override
    public Optional<AchievementEntity> getAchievementById(Long achievementId) {
        return Optional.empty();
    }

    @Override
    public Optional<AchievementEntity> getAchievementByName(String name) {
        return Optional.empty();
    }

    @Override
    public List<AchievementDto> getAchievements(String userId) {
        return null;
    }

    @Override
    public void createClientAchievement(ClientAchievementEntity clientAchievementEntity) {

    }
}
