package com.hits.open.world.public_interface.coin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoinResponseDto(
        Long coinId,
        String latitude,
        String longitude,
        boolean taken,
        String clientId,
        int value
) {
}
