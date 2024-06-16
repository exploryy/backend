package com.hits.open.world.public_interface.statistic;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record UpdateStatisticDto(
        String userId,
        String webSessionId,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isNewTerritory,
        OffsetDateTime lastUpdate
) {
}
