package com.hits.open.world.public_interface.coin;

public record CoinResponseDto(
        Long coinId,
        String latitude,
        String longitude,
        boolean taken,
        String clientId,
        int value
) {
}
