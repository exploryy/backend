package com.hits.open.world.core.statistic.repository;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record StatisticEntity(
        String clientId,
        int experience,
        int distance,
        String webSessionId,
        String previousLatitude,
        String previousLongitude,
        OffsetDateTime lastUpdate
) {
}
