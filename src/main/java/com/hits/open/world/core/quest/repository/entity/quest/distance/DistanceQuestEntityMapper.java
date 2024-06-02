package com.hits.open.world.core.quest.repository.entity.quest.distance;

import com.example.open_the_world.public_.tables.records.DistanceQuestRecord;
import org.jooq.RecordMapper;

public class DistanceQuestEntityMapper implements RecordMapper<DistanceQuestRecord, DistanceQuestEntity> {
    @Override
    public DistanceQuestEntity map(DistanceQuestRecord distanceQuestRecord) {
        return new DistanceQuestEntity(
                distanceQuestRecord.getQuestId(),
                distanceQuestRecord.getRouteDistance()
        );
    }
}
