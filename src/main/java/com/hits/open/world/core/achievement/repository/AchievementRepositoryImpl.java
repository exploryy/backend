package com.hits.open.world.core.achievement.repository;

import com.hits.open.world.core.file.FileStorageService;
import com.hits.open.world.public_interface.achievement.AchievementDto;
import com.hits.open.world.public_interface.exception.ExceptionInApplication;
import com.hits.open.world.public_interface.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.tables.Achievement.ACHIEVEMENT;
import static com.example.open_the_world.public_.tables.ClientAchievement.CLIENT_ACHIEVEMENT;

@Service
@RequiredArgsConstructor
public class AchievementRepositoryImpl implements AchievementRepository {
    private static final AchievementEntityMapper achievementEntityMapper = new AchievementEntityMapper();

    private final DSLContext create;
    private final FileStorageService fileStorageService;

    @Override
    public AchievementEntity createAchievement(AchievementEntity achievementEntity) {
        return create.insertInto(ACHIEVEMENT)
                .set(ACHIEVEMENT.NAME, achievementEntity.name())
                .set(ACHIEVEMENT.DESCRIPTION, achievementEntity.description())
                .returning(ACHIEVEMENT.ACHIEVEMENT_ID, ACHIEVEMENT.NAME, ACHIEVEMENT.DESCRIPTION)
                .fetchOne(achievementEntityMapper);
    }

    @Override
    public void updateAchievement(AchievementEntity achievementEntity) {
        create.update(ACHIEVEMENT)
                .set(ACHIEVEMENT.NAME, achievementEntity.name())
                .set(ACHIEVEMENT.DESCRIPTION, achievementEntity.description())
                .where(ACHIEVEMENT.ACHIEVEMENT_ID.eq(achievementEntity.achievementId()))
                .execute();
    }

    @Override
    public void deleteAchievement(Long achievementId) {
        create.deleteFrom(ACHIEVEMENT)
                .where(ACHIEVEMENT.ACHIEVEMENT_ID.eq(achievementId))
                .execute();
    }

    @Override
    public Optional<AchievementEntity> getAchievementById(Long achievementId) {
        return create.selectFrom(ACHIEVEMENT)
                .where(ACHIEVEMENT.ACHIEVEMENT_ID.eq(achievementId))
                .fetchOptional(achievementEntityMapper);
    }

    @Override
    public Optional<AchievementEntity> getAchievementByName(String name) {
        return create.selectFrom(ACHIEVEMENT)
                .where(ACHIEVEMENT.NAME.eq(name))
                .fetchOptional(achievementEntityMapper);
    }

    @Override
    public List<AchievementDto> getAchievements(String userId) {
        return create.select()
                .from(CLIENT_ACHIEVEMENT)
                .join(ACHIEVEMENT)
                .on(CLIENT_ACHIEVEMENT.ACHIEVEMENT_ID.eq(ACHIEVEMENT.ACHIEVEMENT_ID))
                .where(CLIENT_ACHIEVEMENT.CLIENT_ID.eq(userId))
                .fetch(record -> new AchievementDto(
                        record.get(ACHIEVEMENT.ACHIEVEMENT_ID),
                        record.get(ACHIEVEMENT.NAME),
                        record.get(ACHIEVEMENT.DESCRIPTION),
                        getPhotoUri(record.get(ACHIEVEMENT.ACHIEVEMENT_ID)),
                        record.get(CLIENT_ACHIEVEMENT.CLIENT_ID) != null,
                        record.get(CLIENT_ACHIEVEMENT.CLIENT_ID) == null ? null : record.get(CLIENT_ACHIEVEMENT.ACHIEVEMENT_DATE)
                ));
    }

    @Override
    public void createClientAchievement(ClientAchievementEntity clientAchievementEntity) {
        create.insertInto(CLIENT_ACHIEVEMENT)
                .set(CLIENT_ACHIEVEMENT.CLIENT_ID, clientAchievementEntity.userId())
                .set(CLIENT_ACHIEVEMENT.ACHIEVEMENT_ID, clientAchievementEntity.achievementId())
                .set(CLIENT_ACHIEVEMENT.ACHIEVEMENT_DATE, clientAchievementEntity.achievementDate())
                .execute();
    }

    private String getPhotoUri(Long achievementId) {
        var fileName = String.format("achievement-%d", achievementId);
        return fileStorageService.getDownloadLinkByName(fileName).orElseThrow(
                () -> new ExceptionInApplication("Achievement image not found", ExceptionType.NOT_FOUND)
        );
    }
}
