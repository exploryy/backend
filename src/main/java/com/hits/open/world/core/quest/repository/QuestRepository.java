package com.hits.open.world.core.quest.repository;

import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;

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
}
