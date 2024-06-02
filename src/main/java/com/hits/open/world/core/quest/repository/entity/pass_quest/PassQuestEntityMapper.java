package com.hits.open.world.core.quest.repository.entity.pass_quest;

import com.example.open_the_world.public_.tables.records.PassQuestRecord;
import com.hits.open.world.core.quest.repository.entity.quest.TransportType;
import org.jooq.RecordMapper;

public class PassQuestEntityMapper implements RecordMapper<PassQuestRecord, PassQuestEntity> {
    @Override
    public PassQuestEntity map(PassQuestRecord passQuestRecord) {
        return new PassQuestEntity(
                passQuestRecord.getPassQuestId(),
                passQuestRecord.getQuestId(),
                passQuestRecord.getRouteId(),
                passQuestRecord.getClientId(),
                TransportType.valueOf(passQuestRecord.getTransportType()),
                passQuestRecord.getStartTime(),
                passQuestRecord.getEndTime()
        );
    }
}
