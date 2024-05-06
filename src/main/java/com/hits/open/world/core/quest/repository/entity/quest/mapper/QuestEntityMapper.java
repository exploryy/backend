package com.hits.open.world.core.quest.repository.entity.quest.mapper;

import com.example.open_the_world.public_.tables.records.QuestRecord;
import com.hits.open.world.core.quest.repository.entity.quest.DifficultyType;
import com.hits.open.world.core.quest.repository.entity.quest.QuestEntity;
import com.hits.open.world.core.quest.repository.entity.quest.QuestType;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import org.jooq.RecordMapper;

public class QuestEntityMapper implements RecordMapper<QuestRecord, QuestEntity> {
    @Override
    public QuestEntity map(QuestRecord questRecord) {
        return new QuestEntity(
                questRecord.getQuestId(),
                questRecord.getName(),
                questRecord.getDescription(),
                DifficultyType.fromString(questRecord.getDifficultyType()),
                QuestType.fromString(questRecord.getQuestType()),
                TransportType.valueOf(questRecord.getTransportType())
        );
    }
}
