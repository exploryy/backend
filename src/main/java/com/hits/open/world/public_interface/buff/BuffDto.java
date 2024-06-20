package com.hits.open.world.public_interface.buff;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record BuffDto(
        Long buffId,
        BigDecimal valueFactor,
        String status,
        int levelNumber
) {
}
