package com.hits.open.world.core.quest.repository;

import com.hits.open.world.core.quest.repository.entity.pass_quest.PassQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import com.hits.open.world.core.quest.repository.entity.quest.distance.DistanceQuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.point_to_point.PointToPointQuestEntity;
import com.hits.open.world.core.quest.repository.entity.review.QuestReviewEntity;
import com.hits.open.world.core.quest.repository.entity.review.ReviewPhotoEntity;

import java.util.List;
import java.util.Optional;

public interface QuestRepository {
    QuestEntity createQuest(QuestEntity entity);

    void updateQuest(QuestEntity entity);

    void deleteQuest(Long questId);

    Optional<QuestEntity> getQuestById(Long questId);

    Optional<QuestEntity> getQuestByName(String name);

    QuestPhotoEntity createQuestPhoto(QuestPhotoEntity entity);

    void deleteQuestPhoto(Long questPhotoId);

    Optional<QuestPhotoEntity> getQuestPhotoById(Long questPhotoId);

    List<QuestPhotoEntity> getQuestPhotosByQuestId(Long questId);

    QuestReviewEntity createQuestReview(QuestReviewEntity entity);

    void updateQuestReview(QuestReviewEntity entity);

    void deleteQuestReview(Long questReviewId);

    List<QuestReviewEntity> getQuestReviewsByQuestId(Long questId);

    Optional<QuestReviewEntity> getQuestReviewById(Long questReviewId);

    boolean isQuestReviewExists(Long questId, String userId);

    ReviewPhotoEntity createReviewPhoto(ReviewPhotoEntity entity);

    void deleteReviewPhoto(Long reviewPhotoId);

    List<ReviewPhotoEntity> getReviewPhotosByReviewId(Long reviewId);

    void createPointToPointQuest(PointToPointQuestEntity entity);

    void createDistanceQuest(DistanceQuestEntity entity);

    List<QuestEntity> getQuestsByName(String name);

    void startQuest(PassQuestEntity entity);

    boolean isQuestStarted(Long questId, String userId);

    boolean isQuestFinished(Long questId, String userId);

    void updatePassQuest(PassQuestEntity entity);

    Optional<PassQuestEntity> getPassQuestById(String userId, Long questId);

    void deletePassQuest(Long passQuestId);

    List<QuestEntity> getActiveQuests(String userId);

    List<QuestEntity> getFinishedQuests(String userId);

    Optional<PointToPointQuestEntity> getPointToPointQuestByQuestId(Long questId);

    Optional<DistanceQuestEntity> getDistanceQuestByQuestId(Long questId);
}
