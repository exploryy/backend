package com.hits.open.world.core.quest.repository.entity.quest.point_to_point;

import com.example.open_the_world.public_.tables.records.PointToPointQuestRecord;
import org.jooq.RecordMapper;

public class PointToPointQuestEntityMapper implements RecordMapper<PointToPointQuestRecord, PointToPointQuestEntity> {
    @Override
    public PointToPointQuestEntity map(PointToPointQuestRecord pointToPointQuestRecord) {
        return new PointToPointQuestEntity(
                pointToPointQuestRecord.getQuestId(),
                pointToPointQuestRecord.getRouteId()
        );
    }
}
