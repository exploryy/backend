package com.hits.open.world.core.achievement.repository;

import com.example.open_the_world.public_.tables.records.AchievementRecord;
import org.jooq.RecordMapper;

public class AchievementEntityMapper implements RecordMapper<AchievementRecord, AchievementEntity> {
    @Override
    public AchievementEntity map(AchievementRecord achievementRecord) {
        return new AchievementEntity(
                achievementRecord.getAchievementId(),
                achievementRecord.getName(),
                achievementRecord.getDescription()
        );
    }
}
