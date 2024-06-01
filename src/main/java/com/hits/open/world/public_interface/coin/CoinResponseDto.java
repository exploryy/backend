package com.hits.open.world.public_interface.coin;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CoinResponseDto(
        @JsonProperty("coin_id")
        Long coinId,

        String latitude,

        String longitude,

        boolean taken,

        @JsonProperty("client_id")
        String clientId,

        int value
) {
}
