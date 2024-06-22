package com.hits.open.world.core.quest.repository;

import com.hits.open.world.core.quest.repository.entity.pass_quest.PassQuestEntity;
import com.hits.open.world.core.quest.repository.entity.pass_quest.PassQuestEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import com.hits.open.world.core.quest.repository.entity.quest.distance.DistanceQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.distance.DistanceQuestEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.mapper.QuestEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.mapper.QuestPhotoEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.mapper.QuestReviewEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.mapper.ReviewPhotoEntityMapper;
import com.hits.open.world.core.quest.repository.entity.quest.point_to_point.PointToPointQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.point_to_point.PointToPointQuestEntityMapper;
import com.hits.open.world.core.quest.repository.entity.review.QuestReviewEntity;
import com.hits.open.world.core.quest.repository.entity.review.ReviewPhotoEntity;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.open_the_world.public_.Tables.DISTANCE_QUEST;
import static com.example.open_the_world.public_.Tables.PASS_QUEST;
import static com.example.open_the_world.public_.Tables.POINT_TO_POINT_QUEST;
import static com.example.open_the_world.public_.Tables.QUEST_PHOTO;
import static com.example.open_the_world.public_.Tables.QUEST_REVIEW;
import static com.example.open_the_world.public_.Tables.REVIEW_PHOTO;
import static com.example.open_the_world.public_.tables.Quest.QUEST;

@Repository
@RequiredArgsConstructor
public class QuestRepositoryImpl implements QuestRepository {
    private static final QuestEntityMapper questEntityMapper = new QuestEntityMapper();
    private static final QuestPhotoEntityMapper questPhotoEntityMapper = new QuestPhotoEntityMapper();
    private static final QuestReviewEntityMapper questReviewEntityMapper = new QuestReviewEntityMapper();
    private static final ReviewPhotoEntityMapper reviewPhotoEntityMapper = new ReviewPhotoEntityMapper();
    private static final PassQuestEntityMapper passQuestEntityMapper = new PassQuestEntityMapper();
    private static final PointToPointQuestEntityMapper pointToPointQuestEntityMapper = new PointToPointQuestEntityMapper();
    private static final DistanceQuestEntityMapper distanceQuestEntityMapper = new DistanceQuestEntityMapper();

    private final DSLContext create;

    @Override
    public QuestEntity createQuest(QuestEntity entity) {
        return create.insertInto(QUEST)
                .set(QUEST.NAME, entity.name())
                .set(QUEST.DESCRIPTION, entity.description())
                .set(QUEST.DIFFICULTY_TYPE, entity.difficultyType().name())
                .set(QUEST.QUEST_TYPE, entity.questType().name())
                .set(QUEST.TRANSPORT_TYPE, entity.transportType().name())
                .returning(QUEST.QUEST_ID, QUEST.NAME, QUEST.DESCRIPTION, QUEST.DIFFICULTY_TYPE, QUEST.QUEST_TYPE, QUEST.TRANSPORT_TYPE)
                .fetchOne(questEntityMapper);
    }

    @Override
    public void updateQuest(QuestEntity entity) {
        create.update(QUEST)
                .set(QUEST.NAME, entity.name())
                .set(QUEST.DESCRIPTION, entity.description())
                .set(QUEST.DIFFICULTY_TYPE, entity.difficultyType().name())
                .set(QUEST.QUEST_TYPE, entity.questType().name())
                .set(QUEST.TRANSPORT_TYPE, entity.transportType().name())
                .where(QUEST.QUEST_ID.eq(entity.questId()))
                .execute();
    }

    @Override
    public void deleteQuest(Long questId) {
        create.deleteFrom(QUEST)
                .where(QUEST.QUEST_ID.eq(questId))
                .execute();
    }

    @Override
    public Optional<QuestEntity> getQuestById(Long questId) {
        return create.selectFrom(QUEST)
                .where(QUEST.QUEST_ID.eq(questId))
                .fetchOptional(questEntityMapper);
    }

    @Override
    public Optional<QuestEntity> getQuestByName(String name) {
        return create.selectFrom(QUEST)
                .where(QUEST.NAME.eq(name))
                .fetchOptional(questEntityMapper);
    }

    @Override
    public QuestPhotoEntity createQuestPhoto(QuestPhotoEntity entity) {
        return create.insertInto(QUEST_PHOTO)
                .set(QUEST_PHOTO.QUEST_ID, entity.questId())
                .returning(QUEST_PHOTO.PHOTO_ID, QUEST_PHOTO.QUEST_ID)
                .fetchOne(questPhotoEntityMapper);
    }

    @Override
    public void deleteQuestPhoto(Long questPhotoId) {
        create.deleteFrom(QUEST_PHOTO)
                .where(QUEST_PHOTO.PHOTO_ID.eq(questPhotoId))
                .execute();
    }

    @Override
    public Optional<QuestPhotoEntity> getQuestPhotoById(Long questPhotoId) {
        return create.selectFrom(QUEST_PHOTO)
                .where(QUEST_PHOTO.PHOTO_ID.eq(questPhotoId))
                .fetchOptional(questPhotoEntityMapper);
    }

    @Override
    public List<QuestPhotoEntity> getQuestPhotosByQuestId(Long questId) {
        return create.selectFrom(QUEST_PHOTO)
                .where(QUEST_PHOTO.QUEST_ID.eq(questId))
                .fetch(questPhotoEntityMapper);
    }

    @Override
    public QuestReviewEntity createQuestReview(QuestReviewEntity entity) {
        return create.insertInto(QUEST_REVIEW)
                .set(QUEST_REVIEW.QUEST_ID, entity.questId())
                .set(QUEST_REVIEW.CLIENT_ID, entity.clientId())
                .set(QUEST_REVIEW.SCORE, entity.score())
                .set(QUEST_REVIEW.MESSAGE, entity.message().orElse(null))
                .returning(QUEST_REVIEW.QUEST_REVIEW_ID, QUEST_REVIEW.QUEST_ID, QUEST_REVIEW.CLIENT_ID, QUEST_REVIEW.SCORE, QUEST_REVIEW.MESSAGE)
                .fetchOne(questReviewEntityMapper);
    }

    @Override
    public void updateQuestReview(QuestReviewEntity entity) {
        create.update(QUEST_REVIEW)
                .set(QUEST_REVIEW.SCORE, entity.score())
                .set(QUEST_REVIEW.MESSAGE, entity.message().orElse(null))
                .where(QUEST_REVIEW.QUEST_REVIEW_ID.eq(entity.questReviewId()))
                .execute();
    }

    @Override
    public void deleteQuestReview(Long questReviewId) {
        create.deleteFrom(QUEST_REVIEW)
                .where(QUEST_REVIEW.QUEST_REVIEW_ID.eq(questReviewId))
                .execute();
    }

    @Override
    public List<QuestReviewEntity> getQuestReviewsByQuestId(Long questId) {
        return create.selectFrom(QUEST_REVIEW)
                .where(QUEST_REVIEW.QUEST_ID.eq(questId))
                .fetch(questReviewEntityMapper);
    }

    @Override
    public Optional<QuestReviewEntity> getQuestReviewById(Long questReviewId) {
        return create.selectFrom(QUEST_REVIEW)
                .where(QUEST_REVIEW.QUEST_REVIEW_ID.eq(questReviewId))
                .fetchOptional(questReviewEntityMapper);
    }

    @Override
    public boolean isQuestReviewExists(Long questId, String userId) {
        return create.fetchExists(
                create.selectOne()
                        .from(QUEST_REVIEW)
                        .where(QUEST_REVIEW.QUEST_ID.eq(questId))
                        .and(QUEST_REVIEW.CLIENT_ID.eq(userId))
        );
    }

    @Override
    public ReviewPhotoEntity createReviewPhoto(ReviewPhotoEntity entity) {
        return create.insertInto(REVIEW_PHOTO)
                .set(REVIEW_PHOTO.REVIEW_ID, entity.reviewId())
                .returning(REVIEW_PHOTO.PHOTO_ID, REVIEW_PHOTO.REVIEW_ID)
                .fetchOne(reviewPhotoEntityMapper);
    }

    @Override
    public void deleteReviewPhoto(Long reviewPhotoId) {
        create.deleteFrom(REVIEW_PHOTO)
                .where(REVIEW_PHOTO.PHOTO_ID.eq(reviewPhotoId))
                .execute();
    }

    @Override
    public List<ReviewPhotoEntity> getReviewPhotosByReviewId(Long reviewId) {
        return create.selectFrom(REVIEW_PHOTO)
                .where(REVIEW_PHOTO.REVIEW_ID.eq(reviewId))
                .fetch(reviewPhotoEntityMapper);
    }

    @Override
    public void createPointToPointQuest(PointToPointQuestEntity entity) {
        create.insertInto(POINT_TO_POINT_QUEST)
                .set(POINT_TO_POINT_QUEST.QUEST_ID, entity.questId())
                .set(POINT_TO_POINT_QUEST.ROUTE_ID, entity.routeId())
                .execute();
    }

    @Override
    public void createDistanceQuest(DistanceQuestEntity entity) {
        create.insertInto(DISTANCE_QUEST)
                .set(DISTANCE_QUEST.QUEST_ID, entity.questId())
                .set(DISTANCE_QUEST.ROUTE_DISTANCE, entity.routeDistance())
                .set(DISTANCE_QUEST.LONGITUDE, entity.longitude())
                .set(DISTANCE_QUEST.LATITUDE, entity.latitude())
                .execute();
    }

    @Override
    public List<QuestEntity> getQuestsByName(String name) {
        Condition condition = DSL.trueCondition();
        if (!name.isEmpty()) {
            condition = condition.and(QUEST.NAME.contains(name));
        }

        return create.selectFrom(QUEST)
                .where(condition)
                .fetch(questEntityMapper);
    }

    @Override
    public void startQuest(PassQuestEntity entity) {
        create.insertInto(PASS_QUEST)
                .set(PASS_QUEST.START_TIME, entity.startTime())
                .set(PASS_QUEST.QUEST_ID, entity.questId())
                .set(PASS_QUEST.CLIENT_ID, entity.userId())
                .set(PASS_QUEST.TRANSPORT_TYPE, entity.transportType().name())
                .execute();
    }

    @Override
    public boolean isQuestStarted(Long questId, String userId) {
        return create.fetchExists(
                create.selectOne()
                        .from(PASS_QUEST)
                        .where(PASS_QUEST.QUEST_ID.eq(questId))
                        .and(PASS_QUEST.CLIENT_ID.eq(userId))
                        .and(PASS_QUEST.END_TIME.isNull())
        );
    }

    @Override
    public boolean isQuestFinished(Long questId, String userId) {
        return create.fetchExists(
                create.selectOne()
                        .from(PASS_QUEST)
                        .where(PASS_QUEST.QUEST_ID.eq(questId))
                        .and(PASS_QUEST.CLIENT_ID.eq(userId))
                        .and(PASS_QUEST.END_TIME.isNotNull())
        );
    }

    @Override
    public void updatePassQuest(PassQuestEntity entity) {
        create.update(PASS_QUEST)
                .set(PASS_QUEST.END_TIME, entity.endTime())
                .set(PASS_QUEST.START_TIME, entity.startTime())
                .set(PASS_QUEST.TRANSPORT_TYPE, entity.transportType().name())
                .set(PASS_QUEST.ROUTE_ID, entity.routeId())
                .set(PASS_QUEST.QUEST_ID, entity.questId())
                .set(PASS_QUEST.CLIENT_ID, entity.userId())
                .where(PASS_QUEST.PASS_QUEST_ID.eq(entity.passQuestId()))
                .execute();
    }

    @Override
    public Optional<PassQuestEntity> getPassQuestById(String userId, Long questId) {
        return create.selectFrom(PASS_QUEST)
                .where(PASS_QUEST.CLIENT_ID.eq(userId))
                .and(PASS_QUEST.QUEST_ID.eq(questId))
                .fetchOptional(passQuestEntityMapper);
    }

    @Override
    public void deletePassQuest(Long passQuestId) {
        create.deleteFrom(PASS_QUEST)
                .where(PASS_QUEST.PASS_QUEST_ID.eq(passQuestId))
                .execute();
    }

    @Override
    public List<QuestEntity> getActiveQuests(String userId) {
        return create.select(QUEST.QUEST_ID, QUEST.NAME, QUEST.DESCRIPTION, QUEST.DIFFICULTY_TYPE, QUEST.QUEST_TYPE, QUEST.TRANSPORT_TYPE)
                .from(PASS_QUEST)
                .join(QUEST).on(PASS_QUEST.QUEST_ID.eq(QUEST.QUEST_ID).and(PASS_QUEST.CLIENT_ID.eq(userId)).and(PASS_QUEST.END_TIME.isNull()))
                .fetch(questEntityMapper());
    }

    @Override
    public List<QuestEntity> getFinishedQuests(String userId) {
        return create.select(QUEST.QUEST_ID, QUEST.NAME, QUEST.DESCRIPTION, QUEST.DIFFICULTY_TYPE, QUEST.QUEST_TYPE, QUEST.TRANSPORT_TYPE)
                .from(PASS_QUEST)
                .join(QUEST).on(PASS_QUEST.QUEST_ID.eq(QUEST.QUEST_ID).and(PASS_QUEST.CLIENT_ID.eq(userId)).and(PASS_QUEST.END_TIME.isNotNull()))
                .fetch(questEntityMapper());
    }

    @Override
    public Optional<PointToPointQuestEntity> getPointToPointQuestByQuestId(Long questId) {
        return create.selectFrom(POINT_TO_POINT_QUEST)
                .where(POINT_TO_POINT_QUEST.QUEST_ID.eq(questId))
                .fetchOptional(pointToPointQuestEntityMapper);
    }

    @Override
    public Optional<DistanceQuestEntity> getDistanceQuestByQuestId(Long questId) {
        return create.selectFrom(DISTANCE_QUEST)
                .where(DISTANCE_QUEST.QUEST_ID.eq(questId))
                .fetchOptional(distanceQuestEntityMapper);
    }

    private RecordMapper<org.jooq.Record, QuestEntity> questEntityMapper() {
        return record -> new QuestEntity(
                record.get(QUEST.QUEST_ID),
                record.get(QUEST.NAME),
                record.get(QUEST.DESCRIPTION),
                DifficultyType.fromString(record.get(QUEST.DIFFICULTY_TYPE)),
                QuestType.fromString(record.get(QUEST.QUEST_TYPE)),
                TransportType.fromString(record.get(QUEST.TRANSPORT_TYPE))
        );
    }
}
