package com.hits.open.world.core.location.repository;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record UserLocationEntity(
        String clientId,
        String latitude,
        String longitude,
        OffsetDateTime lastVisitation
) {
}
