package com.hits.open.world.core.quest.repository.entity.quest.mapper;

import com.example.open_the_world.public_.tables.records.QuestReviewRecord;
import com.hits.open.world.core.quest.repository.entity.review.QuestReviewEntity;
import org.jooq.RecordMapper;

public class QuestReviewEntityMapper implements RecordMapper<QuestReviewRecord, QuestReviewEntity> {
    @Override
    public QuestReviewEntity map(QuestReviewRecord questReviewRecord) {
        return new QuestReviewEntity(
                questReviewRecord.getQuestReviewId(),
                questReviewRecord.getScore(),
                questReviewRecord.getMessage(),
                questReviewRecord.getClientId(),
                questReviewRecord.getQuestId()
        );
    }
}
