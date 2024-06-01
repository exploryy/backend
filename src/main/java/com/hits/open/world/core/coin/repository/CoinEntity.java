package com.hits.open.world.core.coin.repository;

import lombok.Builder;

@Builder
public record CoinEntity(
        Long coinId,
        String latitude,
        String longitude,
        int value,
        boolean taken,
        String clientId
) {
}
