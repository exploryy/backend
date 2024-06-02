package com.hits.open.world.public_interface.statistic;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UpdateStatisticDto(
        String userId,
        String webSessionId,
        BigDecimal latitude,
        BigDecimal longitude,
        boolean isNewTerritory,
        OffsetDateTime lastUpdate
) {
}
