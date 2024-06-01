package com.hits.open.world.core.statistic.repository;

import lombok.Builder;

@Builder
public record StatisticEntity(
        String clientId,
        int experience,
        int distance,
        String webSessionId,
        String previousLatitude,
        String previousLongitude
) {
}
