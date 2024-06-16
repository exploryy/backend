package com.hits.open.world.public_interface.coin;

public record BalanceResponseDto(
        int balance,
        int experience,
        int level,
        int totalExperienceInLevel
) {
}
