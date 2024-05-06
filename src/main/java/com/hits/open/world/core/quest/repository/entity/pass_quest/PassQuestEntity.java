package com.hits.open.world.core.quest.repository.entity.pass_quest;

import com.hits.open.world.core.quest.repository.entity.quest.TransportType;

import java.time.OffsetDateTime;

public record PassQuestEntity(
        Long passQuestId,
        Long questId,
        Long routeId,
        String userId,
        TransportType transportType,
        OffsetDateTime startTime,
        OffsetDateTime endTime
) {
}
