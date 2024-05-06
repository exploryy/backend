package com.hits.open.world.core.quest.repository.entity.quest.mapper;

import com.example.open_the_world.public_.tables.records.QuestPhotoRecord;
import com.hits.open.world.core.quest.repository.entity.quest.QuestPhotoEntity;
import org.jooq.RecordMapper;

public class QuestPhotoEntityMapper implements RecordMapper<QuestPhotoRecord, QuestPhotoEntity> {
    @Override
    public QuestPhotoEntity map(QuestPhotoRecord questPhotoRecord) {
        return new QuestPhotoEntity(
                questPhotoRecord.getPhotoId(),
                questPhotoRecord.getQuestId()
        );
    }
}
